package com.shibam.clinic.online_doctor_clinic.Repositories;

import java.util.List;

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

    public List<Doctor> findByApproved(boolean approved);
}
