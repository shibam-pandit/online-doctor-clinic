package com.shibam.clinic.online_doctor_clinic.Controllers;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.shibam.clinic.online_doctor_clinic.Models.Appointment;
import com.shibam.clinic.online_doctor_clinic.Models.Doctor;
import com.shibam.clinic.online_doctor_clinic.Models.DoctorAvailability;
import com.shibam.clinic.online_doctor_clinic.Services.DoctorService;
import com.shibam.clinic.online_doctor_clinic.DTOs.PatientSummaryDTO;

@Controller
@RequestMapping("/doctor")
public class DoctorController {

    @Autowired
    private DoctorService doctorService;

    @GetMapping({ "", "/" })
    public String doctorHome(Principal principal, Model model) {
        String email = principal.getName();
        Doctor doctor = doctorService.findByUserEmail(email);

        if (doctor == null) {
            return "redirect:/login?error=User not found";
        }

        if (!doctor.isApproved()) {
            return "redirect:/doctor/wait"; // Redirect to wait page if not approved
        }

        Long doctorId = doctor.getUser().getId();

        List<Appointment> todayAppointments = doctorService.getTodayAppointmentsByDoctorId(doctorId);
        int uniquePatientCount = doctorService.getUniquePatientCountByDoctorId(doctorId);
        double monthlyIncome = doctorService.getMonthlyIncomeByDoctorId(doctorId);
        double averageRating = doctorService.getAverageRatingByDoctorId(doctorId);

        // Add doctor details to the model
        model.addAttribute("doctor", doctor);
        model.addAttribute("todayAppointments", todayAppointments);
        model.addAttribute("uniquePatientCount", uniquePatientCount);
        model.addAttribute("monthlyIncome", monthlyIncome);
        model.addAttribute("averageRating", averageRating);

        return "doctor/home";
    }

    @GetMapping("/wait")
    public String doctorWait(Principal principal, Model model) {
        String email = principal.getName();
        Doctor doctor = doctorService.findByUserEmail(email);

        if (doctor != null && doctor.isApproved()) {
            return "redirect:/doctor";
        }

        model.addAttribute("doctor", doctor);
        return "doctor/wait";
    }

    @GetMapping("/appointments")
    public String appointments(Principal principal, Model model) {
        String email = principal.getName();
        Doctor doctor = doctorService.findByUserEmail(email);

        if (!doctor.isApproved()) {
            return "redirect:/doctor/wait";
        }

        Long doctorId = doctor.getUser().getId();

        List<Appointment> appointments = doctorService.findAllAppointmentsWithPatientDetails(doctorId);

        // Calculate counts for different appointment statuses
        long todayCount = appointments.stream()
                .filter(app -> app.getDate().equals(java.time.LocalDate.now()))
                .count();

        long completedCount = appointments.stream()
                .filter(app -> app.getStatus() == Appointment.AppointmentStatus.COMPLETED)
                .count();

        long cancelledCount = appointments.stream()
                .filter(app -> app.getStatus() == Appointment.AppointmentStatus.CANCELLED_BY_DOCTOR ||
                        app.getStatus() == Appointment.AppointmentStatus.CANCELLED_BY_PATIENT)
                .count();

        long upcomingCount = appointments.stream()
                .filter(app -> app.getDate().isAfter(java.time.LocalDate.now()))
                .count();

        model.addAttribute("doctor", doctor);
        model.addAttribute("appointments", appointments);
        model.addAttribute("todayAppointmentsCount", todayCount);
        model.addAttribute("completedAppointmentsCount", completedCount);
        model.addAttribute("cancelledAppointmentsCount", cancelledCount);
        model.addAttribute("upcomingAppointmentsCount", upcomingCount);

        return "doctor/appointments";
    }

    @GetMapping("/availability")
    public String setAvailability(Principal principal, Model model,
            @RequestParam(value = "success", required = false) String success,
            @RequestParam(value = "error", required = false) String error) {
        String email = principal.getName();
        Doctor doctor = doctorService.findByUserEmail(email);

        if (doctor == null || !doctor.isApproved()) {
            return "redirect:/doctor/wait"; // Redirect to wait page if not approved
        }

        Long doctorId = doctor.getUser().getId(); // Use doctor.getUser().getId() for centralized user ID

        // Get availability data for display - only unbooked slots with date information
        List<DoctorAvailability> availableSlotsList = doctorService.getUnbookedAvailabilitiesByDoctorId(doctorId);
        List<Appointment> bookedSlotsList = doctorService.getBookedAppointmentsByDoctorId(doctorId);

        // Count available and booked slots
        int availableSlots = availableSlotsList.stream()
                .mapToInt(availability -> availability.getAvailableSlotStarts() != null
                        ? availability.getAvailableSlotStarts().size()
                        : 0)
                .sum();

        int bookedSlots = bookedSlotsList.size();

        model.addAttribute("doctor", doctor);
        model.addAttribute("availableSlotsList", availableSlotsList);
        model.addAttribute("bookedSlotsList", bookedSlotsList);
        model.addAttribute("availableSlots", availableSlots);
        model.addAttribute("bookedSlots", bookedSlots);

        // Add success/error messages
        if (success != null) {
            model.addAttribute("success", "Availability slots created successfully!");
        }
        if (error != null) {
            model.addAttribute("error", "Failed to create availability slots. Please check for overlapping times.");
        }

        return "doctor/availability";
    }

    @PostMapping("/set-availability")
    public String setAvailability(@RequestParam("selectedDate") LocalDate date,
            @RequestParam("startTime") LocalTime startTime,
            @RequestParam("endTime") LocalTime endTime,
            @RequestParam("duration") int sessionMinutes,
            @RequestParam("fees") double price,
            Principal principal) {
        try {
            String email = principal.getName();
            Doctor doctor = doctorService.findByUserEmail(email);

            if (doctor == null || !doctor.isApproved()) {
                return "redirect:/doctor/wait";
            }

            Long doctorId = doctor.getUser().getId(); // Use doctor.getUser().getId() for centralized user ID
            doctorService.addAvailability(doctorId, date, startTime, endTime, sessionMinutes, price);

            return "redirect:/doctor/availability?success=true";
        } catch (Exception e) {
            return "redirect:/doctor/availability?error=true";
        }
    }

    @PostMapping("/cancel-slot/{id}")
    public String cancelSlot(@PathVariable("id") Long availabilityId, @RequestParam("slotStart") LocalTime slotStart,
            Principal principal) {
        try {
            String email = principal.getName();
            Doctor doctor = doctorService.findByUserEmail(email);

            if (doctor == null || !doctor.isApproved()) {
                return "redirect:/doctor/wait";
            }

            doctorService.cancelAvailability(availabilityId, slotStart, doctor.getUser().getId());

            return "redirect:/doctor/availability?success=Availability cancelled successfully";
        } catch (Exception e) {
            return "redirect:/doctor/availability?error=Failed to cancel availability";
        }
    }

    @GetMapping("/patients")
    public String patients(Principal principal, Model model) {
        String email = principal.getName();
        Doctor doctor = doctorService.findByUserEmail(email);

        if (doctor == null || !doctor.isApproved()) {
            return "redirect:/doctor/wait"; // Redirect to wait page if not approved
        }

        Long doctorId = doctor.getUser().getId();

        // Get patient summaries from service
        List<PatientSummaryDTO> patientsList = doctorService.getPatientSummariesByDoctorId(doctorId);
        Integer totalPatients = doctorService.getUniquePatientCountByDoctorId(doctorId);
        Integer newPatientsThisMonth = doctorService.newPatients(doctorId);

        model.addAttribute("doctor", doctor);
        model.addAttribute("patientsList", patientsList);
        model.addAttribute("totalPatients", totalPatients);
        model.addAttribute("newPatientsThisMonth", newPatientsThisMonth);

        return "doctor/patients";
    }

    @GetMapping("/earnings")
    public String earnings(Principal principal, Model model,
            @RequestParam(defaultValue = "0") int page) {
        String email = principal.getName();
        Doctor doctor = doctorService.findByUserEmail(email);

        if (doctor == null || !doctor.isApproved()) {
            return "redirect:/doctor/wait"; // Redirect to wait page if not approved
        }

        Long doctorId = doctor.getUser().getId();
        int pageSize = 10; // Records per page

        // Get earnings data
        double monthlyIncome = doctorService.getMonthlyIncomeByDoctorId(doctorId);
        double totalEarnings = doctorService.getTotalEarningsByDoctorId(doctorId);
        int completedAppointments = doctorService.getCompletedAppointmentsByDoctorId(doctorId);
        double averageFee = doctorService.getAverageFeeByDoctorId(doctorId);

        // Calculate monthly growth
        double lastMonthEarnings = doctorService.getLastMonthEarningsByDoctorId(doctorId);
        double monthlyGrowth = 0.0;
        if (lastMonthEarnings > 0) {
            monthlyGrowth = ((monthlyIncome - lastMonthEarnings) / lastMonthEarnings) * 100;
        }

        // Use Appointments directly - clean and simple approach
        List<Appointment> appointmentsList = doctorService.getEarningsAppointmentsByDoctorId(doctorId);
        // Apply pagination manually if needed
        int startIndex = page * pageSize;
        int endIndex = Math.min(startIndex + pageSize, appointmentsList.size());
        List<Appointment> paginatedAppointments = appointmentsList.subList(startIndex, endIndex);

        Long totalRecords = doctorService.getTotalEarningsCount(doctorId);
        int totalPages = (int) Math.ceil((double) totalRecords / pageSize);

        // Get chart data (last 30 days)
        List<String> chartLabels = doctorService.getChartLabels(doctorId, 30);
        List<Double> chartData = doctorService.getChartData(doctorId, 30);

        // Add attributes to model
        model.addAttribute("doctor", doctor);
        model.addAttribute("monthlyEarnings", monthlyIncome);
        model.addAttribute("totalEarnings", totalEarnings);
        model.addAttribute("completedAppointments", completedAppointments);
        model.addAttribute("averageFee", averageFee);
        model.addAttribute("monthlyGrowth", String.format("%.1f", monthlyGrowth));

        // Earnings table data
        model.addAttribute("appointmentsList", paginatedAppointments);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("totalRecords", totalRecords);

        // Chart data
        model.addAttribute("chartLabels", chartLabels);
        model.addAttribute("chartData", chartData);

        return "doctor/earnings";
    }

    @GetMapping("/settings")
    public String settings(Principal principal, Model model) {
        String email = principal.getName();
        Doctor doctor = doctorService.findByUserEmail(email);

        if (doctor == null || !doctor.isApproved()) {
            return "redirect:/doctor/wait"; // Redirect to wait page if not approved
        }

        model.addAttribute("doctor", doctor);
        return "doctor/settings";
    }
}
