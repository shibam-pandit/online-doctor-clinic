package com.shibam.clinic.online_doctor_clinic.Repositories;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.shibam.clinic.online_doctor_clinic.Models.DoctorAvailability;

@Repository
public interface DoctorAvailabilityRepo extends JpaRepository<DoctorAvailability, Long> {

    @Query("SELECT da.availableSlotStarts FROM DoctorAvailability da WHERE da.doctor.user.id = :doctorId AND da.date = :date")
    public List<LocalTime> getAvailableSlotsByDateAndDoctorId(@Param("doctorId") Long doctorId,
            @Param("date") LocalDate date);

    @Query("SELECT da FROM DoctorAvailability da WHERE da.doctor.user.id = :doctorId AND da.date = :date")
    public DoctorAvailability findByDoctorIdAndDate(@Param("doctorId") Long doctorId, @Param("date") LocalDate date);

    @Query("SELECT da FROM DoctorAvailability da WHERE da.doctor.user.id = :doctorId AND da.date >= CURRENT_DATE ORDER BY da.date ASC")
    public List<DoctorAvailability> findAvailabilityByDoctorIdOrderByDateAsc(@Param("doctorId") Long doctorId);
}
