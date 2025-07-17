package com.shibam.clinic.online_doctor_clinic.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.shibam.clinic.online_doctor_clinic.Models.Patient;
import com.shibam.clinic.online_doctor_clinic.Models.User;

@Repository
public interface PatientRepo extends JpaRepository<Patient, Long> {
    public Patient findByUser(User user);

    @Query("SELECT p FROM Patient p WHERE p.user.email = :email")
    public Patient findByUserEmail(@Param("email") String email);
}
