package com.shibam.clinic.online_doctor_clinic.Controllers;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.shibam.clinic.online_doctor_clinic.Models.Patient;
import com.shibam.clinic.online_doctor_clinic.Models.Appointment;
import com.shibam.clinic.online_doctor_clinic.Models.Doctor;
import com.shibam.clinic.online_doctor_clinic.Models.DoctorAvailability;
import com.shibam.clinic.online_doctor_clinic.Models.Appointment.AppointmentStatus;
import com.shibam.clinic.online_doctor_clinic.Repositories.AppointmentRepo;
import com.shibam.clinic.online_doctor_clinic.Services.DoctorService;
import com.shibam.clinic.online_doctor_clinic.Services.PatientService;

import org.springframework.ui.Model;

@Controller
@RequestMapping("/patient")
public class PatientController {
    @Autowired
    private PatientService patientService;

    @Autowired
    private DoctorService doctorService;

    @Autowired
    private AppointmentRepo appointmentRepo;

    @GetMapping({ "", "/" })
    public String patientHome(Principal principal, Model model) {
        String email = principal.getName();
        Patient patient = patientService.findByEmail(email);
        if (patient == null) {
            model.addAttribute("error", "Patient not found");
            return "error";
        }

        List<Appointment> appointments = appointmentRepo.findByPatientId(patient.getUser().getId());

        // Calculate statistics
        List<Appointment> upcomingAppointments = appointments.stream()
                .filter(appointment -> appointment.getDate().isAfter(LocalDate.now()) ||
                        (appointment.getDate().isEqual(LocalDate.now())
                                && appointment.getSlot().isAfter(LocalTime.now())))
                .filter(appointment -> appointment.getStatus() == AppointmentStatus.BOOKED)
                .collect(Collectors.toList());

        List<Appointment> todayAppointments = appointments.stream()
                .filter(appointment -> appointment.getDate().isEqual(LocalDate.now()))
                .filter(appointment -> appointment.getStatus() == AppointmentStatus.BOOKED)
                .collect(Collectors.toList());

        int totalAppointments = appointments.size();
        int completedAppointments = (int) appointments.stream()
                .filter(appointment -> appointment.getStatus() == AppointmentStatus.COMPLETED)
                .count();

        // Get next upcoming appointment
        Appointment nextAppointment = upcomingAppointments.stream()
                .sorted((a1, a2) -> {
                    int dateComparison = a1.getDate().compareTo(a2.getDate());
                    if (dateComparison == 0) {
                        return a1.getSlot().compareTo(a2.getSlot());
                    }
                    return dateComparison;
                })
                .findFirst()
                .orElse(null);

        // Determine greeting time based on current time
        String greetTime;
        LocalTime now = LocalTime.now();
        if (now.isBefore(LocalTime.NOON)) {
            greetTime = "morning";
        } else if (now.isBefore(LocalTime.of(17, 0))) {
            greetTime = "afternoon";
        } else if (now.isBefore(LocalTime.of(20, 0))) {
            greetTime = "evening";
        } else {
            greetTime = "night";
        }

        model.addAttribute("patient", patient);
        model.addAttribute("appointments", appointments);
        model.addAttribute("upcomingAppointments", upcomingAppointments);
        model.addAttribute("todayAppointments", todayAppointments);
        model.addAttribute("totalAppointments", totalAppointments);
        model.addAttribute("completedAppointments", completedAppointments);
        model.addAttribute("nextAppointment", nextAppointment);
        model.addAttribute("upcomingCount", upcomingAppointments.size());
        model.addAttribute("todayCount", todayAppointments.size());
        model.addAttribute("greetTime", greetTime);

        return "patient/home";
    }

    @GetMapping("/book-appointment")
    public String bookAppointment(Principal principal, Model model) {
        String email = principal.getName();
        Patient patient = patientService.findByEmail(email);
        if (patient == null) {
            model.addAttribute("error", "Patient not found");
            return "error";
        }

        List<Doctor> doctors = doctorService.getAllDoctors();

        // Filter only approved doctors and set their average ratings
        List<Doctor> approvedDoctors = doctors.stream()
                .filter(doctor -> doctor != null && doctor.isApproved())
                .peek(doctor -> {
                    // Set the average rating for each doctor
                    double rating = doctorService.getAverageRatingByDoctorId(doctor.getId());
                    doctor.setAverageRating(rating);
                })
                .collect(Collectors.toList());

        // Calculate statistics for the page
        int totalDoctors = approvedDoctors.size();
        long onlineDoctors = approvedDoctors.size(); // For now, consider all approved doctors as "online"

        model.addAttribute("patient", patient);
        model.addAttribute("doctorsList", approvedDoctors); // Single attribute instead of redundant doctors +
                                                            // doctorsList
        model.addAttribute("totalDoctors", totalDoctors);
        model.addAttribute("onlineDoctors", onlineDoctors);

        return "patient/book-appointment";
    }

    @GetMapping("/book-slot/{doctorId}")
    public String bookSlot(Principal principal, Model model, @PathVariable("doctorId") Long doctorId) {
        String email = principal.getName();
        Patient patient = patientService.findByEmail(email);
        if (patient == null) {
            model.addAttribute("error", "Patient not found");
            return "error";
        }

        Doctor doctor = doctorService.getDoctorById(doctorId);
        if (doctor == null || !doctor.isApproved()) {
            model.addAttribute("error", "Doctor not found or not approved");
            return "error";
        }

        double averageRating = doctorService.getAverageRatingByDoctorId(doctorId);
        doctor.setAverageRating(averageRating);

        // Get unbooked available slots for the doctor using the User ID
        Long userDoctorId = doctor.getUser().getId();
        List<DoctorAvailability> availableSlots = doctorService.getUnbookedAvailabilitiesByDoctorId(userDoctorId);

        model.addAttribute("patient", patient);
        model.addAttribute("doctor", doctor);
        model.addAttribute("availableSlots", availableSlots);

        // Return the view for booking a slot
        return "patient/book-slot";
    }

    @PostMapping("/confirm-appointment")
    public String confirmAppointment(Principal principal, Model model, @RequestParam Long doctorId,
            @RequestParam Long patientId, @RequestParam LocalDate date, @RequestParam LocalTime slot,
            @RequestParam int durationMinutes, @RequestParam Double fee) {
        String email = principal.getName();
        Patient patient = patientService.findByEmail(email);
        if (patient == null) {
            model.addAttribute("error", "Patient not found");
            return "error";
        }

        try {
            // Book the appointment
            patientService.bookAppointment(doctorId, patientId, date, slot, durationMinutes, fee);
            model.addAttribute("success", "Appointment booked successfully!");
        } catch (Exception e) {
            model.addAttribute("error", "Error booking appointment: " + e.getMessage());
            return "error";
        }

        return "redirect:/patient/appointments";
    }

    @GetMapping("/appointments")
    public String viewAppointments(Principal principal, Model model) {
        String email = principal.getName();
        Patient patient = patientService.findByEmail(email);
        if (patient == null) {
            model.addAttribute("error", "Patient not found");
            return "error";
        }

        List<Appointment> appointments = appointmentRepo.findByPatientId(patient.getUser().getId());

        // Calculate statistics
        List<Appointment> upcomingAppointments = appointments.stream()
                .filter(appointment -> appointment.getDate().isAfter(LocalDate.now()) ||
                        (appointment.getDate().isEqual(LocalDate.now())
                                && appointment.getSlot().isAfter(LocalTime.now())))
                .filter(appointment -> appointment.getStatus() == AppointmentStatus.BOOKED)
                .collect(Collectors.toList());

        List<Appointment> todayAppointments = appointments.stream()
                .filter(appointment -> appointment.getDate().isEqual(LocalDate.now()))
                .filter(appointment -> appointment.getStatus() == AppointmentStatus.BOOKED)
                .collect(Collectors.toList());

        int totalAppointments = appointments.size();
        int completedAppointments = (int) appointments.stream()
                .filter(appointment -> appointment.getStatus() == AppointmentStatus.COMPLETED)
                .count();

        // Get cancelled appointments count
        int cancelledAppointments = (int) appointments.stream()
                .filter(appointment -> appointment.getStatus() == AppointmentStatus.CANCELLED_BY_PATIENT ||
                        appointment.getStatus() == AppointmentStatus.CANCELLED_BY_DOCTOR)
                .count();

        // Get completed appointments list
        List<Appointment> completedAppointmentsList = appointments.stream()
                .filter(appointment -> appointment.getStatus() == AppointmentStatus.COMPLETED)
                .collect(Collectors.toList());

        // Get cancelled appointments list
        List<Appointment> cancelledAppointmentsList = appointments.stream()
                .filter(appointment -> appointment.getStatus() == AppointmentStatus.CANCELLED_BY_PATIENT ||
                        appointment.getStatus() == AppointmentStatus.CANCELLED_BY_DOCTOR)
                .collect(Collectors.toList());

        // Get next upcoming appointment
        Appointment nextAppointment = upcomingAppointments.stream()
                .sorted((a1, a2) -> {
                    int dateComparison = a1.getDate().compareTo(a2.getDate());
                    if (dateComparison == 0) {
                        return a1.getSlot().compareTo(a2.getSlot());
                    }
                    return dateComparison;
                })
                .findFirst()
                .orElse(null);

        model.addAttribute("patient", patient);
        model.addAttribute("appointments", appointments);
        model.addAttribute("upcomingAppointments", upcomingAppointments);
        model.addAttribute("todayAppointments", todayAppointments);
        model.addAttribute("completedAppointmentsList", completedAppointmentsList);
        model.addAttribute("cancelledAppointmentsList", cancelledAppointmentsList);
        model.addAttribute("totalAppointments", totalAppointments);
        model.addAttribute("completedAppointments", completedAppointments);
        model.addAttribute("cancelledAppointments", cancelledAppointments);
        model.addAttribute("upcomingCount", upcomingAppointments.size());
        model.addAttribute("todayCount", todayAppointments.size());
        model.addAttribute("completedCount", completedAppointments);
        model.addAttribute("cancelledCount", cancelledAppointments);
        model.addAttribute("nextAppointment", nextAppointment);

        return "patient/my-appointments";
    }

    @PostMapping("/cancel-appointment/{appointmentId}")
    public String cancelAppointment(@PathVariable Long appointmentId, Principal principal, Model model) {
        try {
            String email = principal.getName();
            Patient patient = patientService.findByEmail(email);
            if (patient == null) {
                model.addAttribute("error", "Failed to cancel appointment: Patient not found");
                return "redirect:/patient/appointments";
            }

            // Find the appointment
            Appointment appointment = appointmentRepo.findById(appointmentId).orElse(null);
            if (appointment == null) {
                model.addAttribute("error", "Failed to cancel appointment: Appointment not found");
                return "redirect:/patient/appointments";
            }

            // Check if the appointment belongs to this patient
            if (!appointment.getPatient().getUser().getId().equals(patient.getUser().getId())) {
                model.addAttribute("error", "Failed to cancel appointment: Unauthorized");
                return "redirect:/patient/appointments";
            }

            // Check if appointment can be cancelled (only BOOKED appointments)
            if (appointment.getStatus() != AppointmentStatus.BOOKED) {
                model.addAttribute("error", "Failed to cancel appointment: Appointment cannot be cancelled");
                return "redirect:/patient/appointments";
            }

            // Update appointment status
            appointment.setStatus(AppointmentStatus.CANCELLED_BY_PATIENT);
            appointmentRepo.save(appointment);

            model.addAttribute("success", "Appointment cancelled successfully");
            return "redirect:/patient/appointments";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to cancel appointment: " + e.getMessage());
            return "redirect:/patient/appointments";
        }
    }

    @GetMapping("/prescriptions")
    public String viewPrescriptions(Principal principal, Model model) {
        String email = principal.getName();
        Patient patient = patientService.findByEmail(email);
        if (patient == null) {
            model.addAttribute("error", "Patient not found");
            return "error";
        }

        List<Appointment> appointments = appointmentRepo.findByPatientId(patient.getUser().getId());

        // Filter appointments that have prescriptions
        List<Appointment> prescriptions = appointments.stream()
                .filter(appointment -> appointment.getPrescription() != null
                        && !appointment.getPrescription().isEmpty())
                .collect(Collectors.toList());

        // recent prescriptions in less than 7 days
        List<Appointment> recentPrescriptions = prescriptions.stream()
                .filter(appointment -> appointment.getDate().isAfter(LocalDate.now().minusDays(7)))
                .collect(Collectors.toList());

        model.addAttribute("patient", patient);
        model.addAttribute("prescriptionsList", prescriptions);
        model.addAttribute("recentPrescriptionsList", recentPrescriptions);
        model.addAttribute("totalPrescriptions", prescriptions.size());
        model.addAttribute("recentPrescriptions", recentPrescriptions.size());

        return "patient/prescriptions";
    }

    @GetMapping("/settings")
    public String settings(Principal principal, Model model) {
        String email = principal.getName();
        Patient patient = patientService.findByEmail(email);
        if (patient == null) {
            model.addAttribute("error", "Patient not found");
            return "error";
        }

        model.addAttribute("patient", patient);

        return "patient/settings";
    }

}
