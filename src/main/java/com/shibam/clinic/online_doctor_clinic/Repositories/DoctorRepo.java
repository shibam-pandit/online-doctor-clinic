package com.shibam.clinic.online_doctor_clinic.Repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.shibam.clinic.online_doctor_clinic.Models.Doctor;
import com.shibam.clinic.online_doctor_clinic.Models.User;

@Repository
public interface DoctorRepo extends JpaRepository<Doctor, Long> {
    public Doctor findByUser(User user);

    @Query("SELECT d FROM Doctor d WHERE d.user.email = :email")
    public Doctor findByUserEmail(@Param("email") String email);

    @Query("SELECT d FROM Doctor d WHERE d.user.id = :userId")
    public Optional<Doctor> findByUserId(@Param("userId") Long userId);

    public List<Doctor> findByApproved(boolean approved);
}
