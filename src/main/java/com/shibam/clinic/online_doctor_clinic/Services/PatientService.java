package com.shibam.clinic.online_doctor_clinic.Services;

import java.time.LocalDate;
import java.time.LocalTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shibam.clinic.online_doctor_clinic.Models.Doctor;
import com.shibam.clinic.online_doctor_clinic.Models.Patient;
import com.shibam.clinic.online_doctor_clinic.Repositories.AppointmentRepo;
import com.shibam.clinic.online_doctor_clinic.Repositories.PatientRepo;

@Service
public class PatientService {
    @Autowired
    private PatientRepo patientRepo;

    @Autowired
    private AppointmentRepo appointmentRepo;

    @Autowired
    private DoctorService doctorService;

    public Patient findByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return null;
        }
        try {
            Patient patient = patientRepo.findByUserEmail(email);
            return patient;
        } catch (Exception e) {
            return null;
        }
    }

    public Patient findById(Long id) {
        if (id == null) {
            return null;
        }
        try {
            return patientRepo.findById(id).orElse(null);
        } catch (Exception e) {
            return null;
        }
    }

    public void bookAppointment(Long doctorId, Long patientId, LocalDate date, LocalTime slot, int durationMinutes, double fee) {
        try {
            Doctor doctor = doctorService.getDoctorById(doctorId);
            if (doctor == null) {
                throw new RuntimeException("Doctor not found with ID: " + doctorId);
            }
            Patient patient = findById(patientId);
            if (patient == null) {
                throw new RuntimeException("Patient not found with ID: " + patientId);
            }

            appointmentRepo.bookAppointment(doctor, patient, date, slot, durationMinutes, fee);
        } catch (Exception e) {
            throw new RuntimeException("Error booking appointment: " + e.getMessage());
        }
    }
}
