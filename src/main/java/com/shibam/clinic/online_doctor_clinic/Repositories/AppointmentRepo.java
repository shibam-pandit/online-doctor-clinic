package com.shibam.clinic.online_doctor_clinic.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.shibam.clinic.online_doctor_clinic.Models.Appointment;

@Repository
public interface AppointmentRepo extends JpaRepository<Appointment, Long> {
    
}
