package com.shibam.clinic.online_doctor_clinic.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.shibam.clinic.online_doctor_clinic.Models.Doctor;
import com.shibam.clinic.online_doctor_clinic.Models.Patient;
import com.shibam.clinic.online_doctor_clinic.Models.User;
import com.shibam.clinic.online_doctor_clinic.Repositories.DoctorRepo;
import com.shibam.clinic.online_doctor_clinic.Repositories.PatientRepo;
import com.shibam.clinic.online_doctor_clinic.Repositories.UserRepo;

@Service
public class AuthService {
    @Autowired
    private DoctorRepo doctorRepo;

    @Autowired
    private PatientRepo patientRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void registerDoctor(String name, String email, String phone, String password, String qualification,
            String specialization, Integer experience) {
        try {
            // Validate input parameters
            if (name == null || email == null || phone == null || password == null || qualification == null
                    || specialization == null || experience == null) {
                throw new IllegalArgumentException("All fields are required");
            }

            // Check if the user already exists
            if (userRepo.existsByEmail(email)) {
                throw new RuntimeException("User with this email already exists");
            }

            // Create and save the user entity
            User user = new User();
            user.setName(name);
            user.setEmail(email);
            user.setPhone(phone);
            user.setPassword(passwordEncoder.encode(password));
            user.setRole(User.Role.DOCTOR);
            userRepo.save(user);

            // Create and save the doctor entity
            Doctor doctor = new Doctor();
            doctor.setUser(user);
            doctor.setQualification(qualification);
            doctor.setSpecialization(specialization);
            doctor.setExperience(experience);
            doctorRepo.save(doctor);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error while registering doctor: " + e.getMessage());
        }
    }

    public void registerPatient(String name, String email, String phone, String password) {
        System.out.println("Registering patient with name: " + name + ", email: " + email);
        try {
            // Validate input parameters
            if (name == null || email == null || phone == null || password == null) {
                throw new IllegalArgumentException("All fields are required");
            }

            // Check if the user already exists
            if (userRepo.existsByEmail(email)) {
                throw new RuntimeException("User with this email already exists");
            }

            // Create and save the user entity
            User user = new User();
            user.setName(name);
            user.setEmail(email);
            user.setPhone(phone);
            user.setPassword(passwordEncoder.encode(password));
            user.setRole(User.Role.PATIENT);
            userRepo.save(user);

            // Create and save the patient entity
            Patient patient = new Patient();
            patient.setUser(user);
            patientRepo.save(patient);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error while registering patient: " + e.getMessage());
        }
    }
}
