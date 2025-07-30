package com.shibam.clinic.online_doctor_clinic.Services;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shibam.clinic.online_doctor_clinic.Models.Appointment;
import com.shibam.clinic.online_doctor_clinic.Models.Doctor;
import com.shibam.clinic.online_doctor_clinic.Models.DoctorAvailability;
import com.shibam.clinic.online_doctor_clinic.Models.Patient;
import com.shibam.clinic.online_doctor_clinic.Models.User;
import com.shibam.clinic.online_doctor_clinic.Repositories.AppointmentRepo;
import com.shibam.clinic.online_doctor_clinic.Repositories.DoctorRepo;
import com.shibam.clinic.online_doctor_clinic.Repositories.DoctorAvailabilityRepo;
import com.shibam.clinic.online_doctor_clinic.DTOs.PatientSummaryDTO;

@Service
public class DoctorService {

    @Autowired
    private DoctorRepo doctorRepo;

    @Autowired
    private AppointmentRepo appointmentRepo;

    @Autowired
    private DoctorAvailabilityRepo doctorAvailabilityRepo;

    public Doctor findByUser(User user) {
        return doctorRepo.findByUser(user);
    }

    public Doctor getDoctorById(Long doctorId) {
        return doctorRepo.findById(doctorId).orElse(null);
    }

    public Doctor findByUserEmail(String email) {
        return doctorRepo.findByUserEmail(email);
    }

    public boolean isDoctorApproved(String email) {
        Doctor doctor = doctorRepo.findByUserEmail(email);
        return doctor != null && doctor.isApproved();
    }

    public boolean isDoctorApproved(User user) {
        Doctor doctor = doctorRepo.findByUser(user);
        return doctor != null && doctor.isApproved();
    }

    public List<Doctor> getAllDoctors() {
        try {
            List<Doctor> doctors = doctorRepo.findAll();
            return doctors != null ? doctors : new ArrayList<>();
        } catch (Exception e) {
            // Log the exception if needed
            return new ArrayList<>();
        }
    }

    public List<Doctor> getPendingDoctors() {
        return doctorRepo.findByApproved(false);
    }

    public Doctor approveDoctor(Long doctorId) {
        Doctor doctor = doctorRepo.findById(doctorId).orElse(null);
        if (doctor != null) {
            doctor.setApproved(true);
            return doctorRepo.save(doctor);
        }
        return null;
    }

    public Doctor rejectDoctor(Long doctorId) {
        Doctor doctor = doctorRepo.findById(doctorId).orElse(null);
        if (doctor != null) {
            doctor.setApproved(false);
            return doctorRepo.save(doctor);
        }
        return null;
    }

    public List<Appointment> getTodayAppointmentsByDoctorId(Long doctorId) {
        return appointmentRepo.todayAppointmentsByDoctorId(doctorId);
    }

    public int getUniquePatientCountByDoctorId(Long doctorId) {
        return appointmentRepo.getUniquePatientCountByDoctorId(doctorId);
    }

    public double getMonthlyIncomeByDoctorId(Long doctorId) {
        Double income = appointmentRepo.getMonthlyIncomeByDoctorId(doctorId);
        return income != null ? income : 0.0;
    }

    public double getAverageRatingByDoctorId(Long doctorId) {
        Double rating = appointmentRepo.getAverageRatingByDoctorId(doctorId);
        return rating != null ? rating : 0.0;
    }

    public List<Appointment> findAllAppointmentsWithPatientDetails(Long doctorId) {
        return appointmentRepo.getAllAppointmentsByDoctorIdWithPatientDetails(doctorId);
    }

    public void addAvailability(Long doctorId, LocalDate date, LocalTime startTime, LocalTime endTime,
            int sessionMinutes, double price) {
        // Validate input parameters
        if (startTime.isAfter(endTime) || startTime.equals(endTime)) {
            throw new IllegalArgumentException("Start time must be before end time");
        }
        if (sessionMinutes <= 0) {
            throw new IllegalArgumentException("Session duration must be positive");
        }
        if (price < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }

        // Check if there's already an availability record for this doctor and date
        DoctorAvailability existingAvailability = doctorAvailabilityRepo.findByDoctorIdAndDate(doctorId, date);

        // Get existing slots (will be empty list if no existing availability)
        List<LocalTime> existingSlots = new ArrayList<>();
        if (existingAvailability != null && existingAvailability.getAvailableSlotStarts() != null) {
            existingSlots = new ArrayList<>(existingAvailability.getAvailableSlotStarts());
        }
        existingSlots.sort(Comparator.naturalOrder());

        // Generate all possible new slots
        List<LocalTime> newSlots = new ArrayList<>();
        LocalTime currentSlot = startTime;

        while (currentSlot.isBefore(endTime)) {
            LocalTime nextSlot = currentSlot.plusMinutes(sessionMinutes);

            // Check if the complete slot (currentSlot to nextSlot) fits within the time
            // range
            if (nextSlot.isAfter(endTime)) {
                break; // Don't add slots that would extend beyond the end time
            }

            // Check for overlap with existing slots using binary search (O(log n))
            boolean hasOverlap = false;
            if (!existingSlots.isEmpty()) {
                // Use binary search to find the insertion point
                int insertionPoint = java.util.Collections.binarySearch(existingSlots, currentSlot);

                // If insertionPoint is negative, it means the element wasn't found
                // Convert to the index where it would be inserted
                int index = insertionPoint < 0 ? -(insertionPoint + 1) : insertionPoint;

                // Check the slot at the insertion point (if it exists)
                if (index < existingSlots.size()) {
                    LocalTime existingSlot = existingSlots.get(index);
                    LocalTime existingSlotEnd = existingSlot.plusMinutes(sessionMinutes);

                    // Check if there's overlap with this slot
                    if (currentSlot.isBefore(existingSlotEnd) && nextSlot.isAfter(existingSlot)) {
                        hasOverlap = true;
                    }
                }

                // Check the previous slot (if it exists)
                if (!hasOverlap && index > 0) {
                    LocalTime existingSlot = existingSlots.get(index - 1);
                    LocalTime existingSlotEnd = existingSlot.plusMinutes(sessionMinutes);

                    // Check if there's overlap with the previous slot
                    if (currentSlot.isBefore(existingSlotEnd) && nextSlot.isAfter(existingSlot)) {
                        hasOverlap = true;
                    }
                }
            }

            // Only add the slot if there's no overlap
            if (!hasOverlap) {
                newSlots.add(currentSlot);
            }

            currentSlot = nextSlot;
        }

        // If we have new slots to add, create or update the availability record
        if (!newSlots.isEmpty()) {
            if (existingAvailability != null) {
                // Merge new slots with existing ones
                List<LocalTime> allSlots = new ArrayList<>(existingSlots);
                allSlots.addAll(newSlots);
                allSlots.sort(Comparator.naturalOrder());

                existingAvailability.setAvailableSlotStarts(allSlots);
                // Update session minutes and price (assuming they should be consistent)
                existingAvailability.setSessionMinutes(sessionMinutes);
                existingAvailability.setPrice(price);

                doctorAvailabilityRepo.save(existingAvailability);
            } else {
                // Create new availability record
                Doctor doctor = doctorRepo.findByUserId(doctorId)
                        .orElseThrow(() -> new IllegalArgumentException("Doctor not found with User ID: " + doctorId));

                DoctorAvailability availability = new DoctorAvailability();
                availability.setDoctor(doctor);
                availability.setDate(date);
                availability.setSessionMinutes(sessionMinutes);
                availability.setPrice(price);
                availability.setAvailableSlotStarts(newSlots);

                doctorAvailabilityRepo.save(availability);
            }
        } else {
            throw new IllegalArgumentException(
                    "No new slots could be added due to overlapping with existing availability");
        }
    }

    // Get all available slots for a doctor (across all dates)
    public List<DoctorAvailability> getAvailableSlotsByDoctorId(Long doctorId) {
        return doctorAvailabilityRepo.findAvailabilityByDoctorIdOrderByDateAsc(doctorId);
    }

    // Get filtered available slots (unbooked only) for a doctor with date information
    public List<DoctorAvailability> getUnbookedAvailabilitiesByDoctorId(Long doctorId) {

        List<DoctorAvailability> allAvailability = doctorAvailabilityRepo.findAvailabilityByDoctorIdOrderByDateAsc(doctorId);

        List<DoctorAvailability> filteredAvailabilities = new ArrayList<>();

        for (DoctorAvailability availability : allAvailability) {
            LocalDate date = availability.getDate();
            List<LocalTime> availableSlots = availability.getAvailableSlotStarts();
            List<LocalTime> bookedSlots = getBookedSlotsByDoctorId(doctorId, date);

            if (availableSlots != null) {
                List<LocalTime> unbookedSlots = availableSlots.stream()
                        .filter(slot -> !bookedSlots.contains(slot))
                        .collect(Collectors.toList());

                // Only include availability if there are unbooked slots
                if (!unbookedSlots.isEmpty()) {
                    // Create a new DoctorAvailability object with filtered slots
                    DoctorAvailability filteredAvailability = new DoctorAvailability();
                    filteredAvailability.setId(availability.getId());
                    filteredAvailability.setDoctor(availability.getDoctor());
                    filteredAvailability.setDate(availability.getDate());
                    filteredAvailability.setSessionMinutes(availability.getSessionMinutes());
                    filteredAvailability.setPrice(availability.getPrice());
                    filteredAvailability.setAvailableSlotStarts(unbookedSlots);

                    filteredAvailabilities.add(filteredAvailability);
                }
            }
        }

        return filteredAvailabilities;
    }

    // Get all unbooked available slots for a doctor (across all dates) - flat list
    public List<LocalTime> getUnbookedAvailableSlotsByDoctorId(Long doctorId) {
        List<DoctorAvailability> allAvailability = doctorAvailabilityRepo.findAvailabilityByDoctorIdOrderByDateAsc(doctorId);
        List<LocalTime> unbookedSlots = new ArrayList<>();

        for (DoctorAvailability availability : allAvailability) {
            LocalDate date = availability.getDate();
            List<LocalTime> availableSlots = availability.getAvailableSlotStarts();
            List<LocalTime> bookedSlots = getBookedSlotsByDoctorId(doctorId, date);

            if (availableSlots != null) {
                List<LocalTime> dateUnbookedSlots = availableSlots.stream()
                        .filter(slot -> !bookedSlots.contains(slot))
                        .collect(Collectors.toList());
                unbookedSlots.addAll(dateUnbookedSlots);
            }
        }

        return unbookedSlots;
    }

    // Get booked appointments for a doctor
    public List<Appointment> getBookedAppointmentsByDoctorId(Long doctorId) {
        List<Appointment> allAppointments = appointmentRepo.findBookedAppointmentsByDoctorId(doctorId);
        return allAppointments;
    }

    // Get available slots by doctor and date (existing method - keeping for
    // compatibility)
    public List<LocalTime> getAvailableSlotsByDoctorId(Long doctorId, LocalDate date) {
        return doctorAvailabilityRepo.getAvailableSlotsByDateAndDoctorId(doctorId, date);
    }

    // Get booked slots by doctor and date (existing method - keeping for
    // compatibility)
    public List<LocalTime> getBookedSlotsByDoctorId(Long doctorId, LocalDate date) {
        List<Appointment> appointments = appointmentRepo.getAppointmentsByDoctorIdAndDate(doctorId, date);
        List<LocalTime> bookedSlots = new ArrayList<>();

        for (Appointment appointment : appointments) {
            if (appointment.getSlot() != null) {
                bookedSlots.add(appointment.getSlot());
            }
        }

        return bookedSlots;
    }

    // Cancel availability by ID
    public void cancelAvailability(Long availabilityId, LocalTime slotStart, Long doctorId) {
        DoctorAvailability availability = doctorAvailabilityRepo.findById(availabilityId)
                .orElseThrow(() -> new IllegalArgumentException("Availability not found with ID: " + availabilityId));

        // Verify that this availability belongs to the doctor
        if (!availability.getDoctor().getUser().getId().equals(doctorId)) {
            throw new IllegalArgumentException(
                    "Unauthorized: This availability does not belong to the specified doctor");
        }

        List<LocalTime> availableSlots = availability.getAvailableSlotStarts();
        if (availableSlots != null && availableSlots.contains(slotStart)) {
            availableSlots.remove(slotStart);
            if (availableSlots.isEmpty()) {
                doctorAvailabilityRepo.delete(availability);
            } else {
                availability.setAvailableSlotStarts(availableSlots);
                doctorAvailabilityRepo.save(availability);
            }
        } else {
            throw new IllegalArgumentException("Slot start time not found in availability");
        }
    }

    public List<Appointment> getAppointmentsAndPatientDetailsByDoctorId(Long doctorId) {
        return appointmentRepo.getAppointmentsWithPatientDetails(doctorId);
    }

    public Integer newPatients(Long doctorId) {
        return appointmentRepo.newPatients(doctorId);
    }

    public List<PatientSummaryDTO> getPatientSummariesByDoctorId(Long doctorId) {
        // Get all appointments for the doctor
        List<Appointment> appointments = appointmentRepo.getAppointmentsWithPatientDetails(doctorId);

        // Group appointments by patient ID
        Map<Long, List<Appointment>> appointmentsByPatient = appointments.stream()
                .collect(Collectors.groupingBy(app -> app.getPatient().getId()));

        List<PatientSummaryDTO> patientsList = new ArrayList<>();

        List<LocalDate> visitDates = new ArrayList<>();

        for (Map.Entry<Long, List<Appointment>> entry : appointmentsByPatient.entrySet()) {
            List<Appointment> patientAppointments = entry.getValue();

            // Get the first appointment to get patient details
            Appointment firstAppointment = patientAppointments.get(0);
            Patient patient = firstAppointment.getPatient();

            // Calculate summary statistics
            int totalVisits = patientAppointments.size();
            LocalDate lastVisitDate = patientAppointments.stream()
                    .map(Appointment::getDate)
                    .max(LocalDate::compareTo)
                    .orElse(null);

            // Get the latest prescription
            String lastPrescription = patientAppointments.stream()
                    .filter(app -> app.getPrescription() != null && !app.getPrescription().trim().isEmpty())
                    .sorted((a1, a2) -> a2.getDate().compareTo(a1.getDate()))
                    .map(Appointment::getPrescription)
                    .findFirst()
                    .orElse("No prescription available");

            // Collect visit dates
            visitDates = patientAppointments.stream()
                    .map(Appointment::getDate)
                    .distinct()
                    .sorted()
                    .collect(Collectors.toList());

            // Create patient summary
            PatientSummaryDTO patientSummary = new PatientSummaryDTO(
                    patient.getId(),
                    patient.getUser().getName(),
                    patient.getUser().getEmail(),
                    patient.getUser().getPhone(),
                    "N/A", // gender - not available in current model
                    null, // age - not available in current model
                    null, // profileImage - not available in current model
                    totalVisits,
                    lastVisitDate,
                    lastPrescription,
                    visitDates);

            patientsList.add(patientSummary);
        }

        // Sort by last visit date (most recent first)
        patientsList.sort((p1, p2) -> {
            if (p1.getLastVisitDate() == null && p2.getLastVisitDate() == null)
                return 0;
            if (p1.getLastVisitDate() == null)
                return 1;
            if (p2.getLastVisitDate() == null)
                return -1;
            return p2.getLastVisitDate().compareTo(p1.getLastVisitDate());
        });

        return patientsList;
    }

    public Double getTotalEarningsByDoctorId(Long doctorId) {
        Double earnings = appointmentRepo.getTotalEarningsByDoctorId(doctorId);
        return earnings != null ? earnings : 0.0;
    }

    public Integer getCompletedAppointmentsByDoctorId(Long doctorId) {
        Integer completed = appointmentRepo.getCompletedAppointmentsByDoctorId(doctorId);
        return completed != null ? completed : 0;
    }

    public Double getAverageFeeByDoctorId(Long doctorId) {
        Double avgFee = appointmentRepo.getAverageFeeByDoctorId(doctorId);
        return avgFee != null ? avgFee : 0.0;
    }

    public Double getLastMonthEarningsByDoctorId(Long doctorId) {
        Double earnings = appointmentRepo.getLastMonthEarningsByDoctorId(doctorId);
        return earnings != null ? earnings : 0.0;
    }

    // Simplified approach: Return Appointments directly instead of DTOs
    public List<Appointment> getEarningsAppointmentsByDoctorId(Long doctorId) {
        return appointmentRepo.getEarningsAppointmentsByDoctorId(doctorId);
    }

    public Long getTotalEarningsCount(Long doctorId) {
        return appointmentRepo.getTotalEarningsCount(doctorId);
    }

    public List<String> getChartLabels(Long doctorId, int days) {
        try {
            LocalDate startDate = LocalDate.now().minusDays(days);
            List<LocalDate> dates = appointmentRepo.getChartDates(doctorId, startDate);

            List<String> labels = new ArrayList<>();
            for (LocalDate date : dates) {
                // Format the date as "Mon DD"
                labels.add(date.format(java.time.format.DateTimeFormatter.ofPattern("MMM dd")));
            }

            // If no data found, provide default labels for last 7 days
            if (labels.isEmpty()) {
                for (int i = days - 1; i >= 0; i--) {
                    LocalDate date = LocalDate.now().minusDays(i);
                    labels.add(date.format(java.time.format.DateTimeFormatter.ofPattern("MMM dd")));
                }
            }

            return labels;
        } catch (Exception e) {
            // Fallback: return last 7 days labels
            List<String> fallbackLabels = new ArrayList<>();
            for (int i = 6; i >= 0; i--) {
                LocalDate date = LocalDate.now().minusDays(i);
                fallbackLabels.add(date.format(java.time.format.DateTimeFormatter.ofPattern("MMM dd")));
            }
            return fallbackLabels;
        }
    }

    public List<Double> getChartData(Long doctorId, int days) {
        try {
            LocalDate startDate = LocalDate.now().minusDays(days);
            List<Object[]> results = appointmentRepo.getChartDataWithDates(doctorId, startDate);

            List<Double> data = new ArrayList<>();
            for (Object[] row : results) {
                Double amount = row[1] != null ? (Double) row[1] : 0.0;
                data.add(amount);
            }

            // If no data found, provide default zeros
            if (data.isEmpty()) {
                for (int i = 0; i < days; i++) {
                    data.add(0.0);
                }
            }

            return data;
        } catch (Exception e) {
            // Fallback: return zeros for last 7 days
            List<Double> fallbackData = new ArrayList<>();
            for (int i = 0; i < 7; i++) {
                fallbackData.add(0.0);
            }
            return fallbackData;
        }
    }

    public void addMeetingLinkToAppointment(Long appointmentId, String meetingLink) {
        appointmentRepo.updateMeetingLink(appointmentId, meetingLink);
    }

    public void addPrescriptionToAppointment(Long appointmentId, String prescription) {
        appointmentRepo.updatePrescription(appointmentId, prescription);
    }
}
