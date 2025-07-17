package com.shibam.clinic.online_doctor_clinic.Services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shibam.clinic.online_doctor_clinic.Models.Doctor;
import com.shibam.clinic.online_doctor_clinic.Models.User;
import com.shibam.clinic.online_doctor_clinic.Repositories.DoctorRepo;

@Service
public class DoctorService {

    @Autowired
    private DoctorRepo doctorRepo;

    public Doctor findByUser(User user) {
        return doctorRepo.findByUser(user);
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
        return doctorRepo.findAll();
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
}
