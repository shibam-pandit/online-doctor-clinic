package com.shibam.clinic.online_doctor_clinic.Services;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.shibam.clinic.online_doctor_clinic.Models.Doctor;
import com.shibam.clinic.online_doctor_clinic.Models.Patient;
import com.shibam.clinic.online_doctor_clinic.Models.User;
import com.shibam.clinic.online_doctor_clinic.Repositories.DoctorRepo;
import com.shibam.clinic.online_doctor_clinic.Repositories.PatientRepo;
import com.shibam.clinic.online_doctor_clinic.Repositories.UserRepo;

@Service
public class AuthService {
    @Autowired
    private Cloudinary cloudinary;

    @Autowired
    private DoctorRepo doctorRepo;

    @Autowired
    private PatientRepo patientRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void registerDoctor(String name, String email, String phone, String password, String gender, Integer age,
            MultipartFile profilePicture, String qualification, String specialization, Integer experience) {
        try {
            // Validate input parameters
            if (name == null || email == null || phone == null || password == null || gender == null || age == null
                    || profilePicture == null || qualification == null || specialization == null
                    || experience == null) {
                throw new IllegalArgumentException("All fields are required");
            }

            // Check if the user already exists
            if (userRepo.existsByEmail(email)) {
                throw new RuntimeException("User with this email already exists");
            }

            String profilePictureUrl = uploadProfilePicture(profilePicture);

            // Create and save the user entity
            User user = new User();
            user.setName(name);
            user.setEmail(email);
            user.setPhone(phone);
            user.setPassword(passwordEncoder.encode(password));
            user.setGender(gender);
            user.setAge(age);
            user.setRole(User.Role.DOCTOR);
            user.setProfilePictureUrl(profilePictureUrl);
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

    public void registerPatient(String name, String email, String phone, String password, String gender, Integer age,
            MultipartFile profilePicture) {
        System.out.println("Registering patient with name: " + name + ", email: " + email);
        try {
            // Validate input parameters
            if (name == null || email == null || phone == null || password == null || gender == null || age == null) {
                throw new IllegalArgumentException("All fields are required");
            }

            // Check if the user already exists
            if (userRepo.existsByEmail(email)) {
                throw new RuntimeException("User with this email already exists");
            }

            String profilePictureUrl = uploadProfilePicture(profilePicture);

            // Create and save the user entity
            User user = new User();
            user.setName(name);
            user.setEmail(email);
            user.setPhone(phone);
            user.setPassword(passwordEncoder.encode(password));
            user.setRole(User.Role.PATIENT);
            user.setProfilePictureUrl(profilePictureUrl);
            user.setGender(gender);
            user.setAge(age);
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

    private String uploadProfilePicture(MultipartFile file) {
        try {
            if (file == null || file.isEmpty()) {
                throw new IllegalArgumentException("Profile picture is required");
            }

            @SuppressWarnings("unchecked")
            Map<String, Object> uploadResult = (Map<String, Object>) cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
            if (uploadResult == null || !uploadResult.containsKey("secure_url")) {
                throw new RuntimeException("Failed to upload profile picture");
            }
            
            return (String) uploadResult.get("secure_url");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to upload profile picture: " + e.getMessage());
        }
    }

}