package com.shibam.clinic.online_doctor_clinic.Repositories;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.shibam.clinic.online_doctor_clinic.Models.Appointment;
import com.shibam.clinic.online_doctor_clinic.Models.Doctor;
import com.shibam.clinic.online_doctor_clinic.Models.Patient;

@Repository
public interface AppointmentRepo extends JpaRepository<Appointment, Long> {
        @Query("SELECT a FROM Appointment a WHERE a.doctor.user.id = :doctorId AND a.date = CURRENT_DATE AND a.status = 'BOOKED'")
        public List<Appointment> todayAppointmentsByDoctorId(Long doctorId);

        @Query("SELECT COUNT(DISTINCT a.patient.id) FROM Appointment a WHERE a.doctor.user.id = :doctorId")
        public Integer getUniquePatientCountByDoctorId(Long doctorId);

        @Query("SELECT SUM(a.fee) FROM Appointment a WHERE a.doctor.user.id = :doctorId AND a.status = 'COMPLETED' AND EXTRACT(MONTH FROM a.date) = EXTRACT(MONTH FROM CURRENT_DATE) AND EXTRACT(YEAR FROM a.date) = EXTRACT(YEAR FROM CURRENT_DATE)")
        public Double getMonthlyIncomeByDoctorId(Long doctorId);

        @Query("SELECT AVG(a.rating) FROM Appointment a WHERE a.doctor.user.id = :doctorId AND a.rating IS NOT NULL")
        public Double getAverageRatingByDoctorId(Long doctorId);

        @Query("SELECT a FROM Appointment a JOIN FETCH a.patient WHERE a.doctor.user.id = :doctorId")
        public List<Appointment> getAppointmentsWithPatientDetails(Long doctorId);

        @Query("SELECT a FROM Appointment a WHERE a.doctor.user.id = :doctorId AND a.date = :date AND a.status = 'BOOKED'")
        public List<Appointment> getAppointmentsByDoctorIdAndDate(Long doctorId, LocalDate date);

        // native query for date arithmetic which varies by database
        @Query(value = "SELECT COUNT(DISTINCT p.id) FROM appointment a JOIN doctor d ON a.doctor_id = d.id JOIN patient p ON a.patient_id = p.id WHERE d.user_id = :doctorId AND a.date >= CURRENT_DATE - INTERVAL '30 days'", nativeQuery = true)
        public Integer newPatients(Long doctorId);

        @Query("SELECT SUM(a.fee) FROM Appointment a WHERE a.doctor.user.id = :doctorId AND a.status = 'COMPLETED'")
        public Double getTotalEarningsByDoctorId(Long doctorId);

        @Query("SELECT COUNT(a) FROM Appointment a WHERE a.doctor.user.id = :doctorId AND a.status = 'COMPLETED'")
        public Integer getCompletedAppointmentsByDoctorId(Long doctorId);

        @Query("SELECT AVG(a.fee) FROM Appointment a WHERE a.doctor.user.id = :doctorId AND a.status = 'COMPLETED'")
        public Double getAverageFeeByDoctorId(Long doctorId);

        // native SQL due to complex date arithmetic that varies by database
        @Query(value = "SELECT SUM(a.fee) FROM appointment a JOIN doctor d ON a.doctor_id = d.id WHERE d.user_id = :doctorId AND a.status = 'COMPLETED' AND EXTRACT(MONTH FROM a.date) = EXTRACT(MONTH FROM (CURRENT_DATE - INTERVAL '1 month')) AND EXTRACT(YEAR FROM a.date) = EXTRACT(YEAR FROM (CURRENT_DATE - INTERVAL '1 month'))", nativeQuery = true)
        public Double getLastMonthEarningsByDoctorId(Long doctorId);

        // Earnings list with pagination - simplified using JPA
        @Query("SELECT a FROM Appointment a " +
                        "JOIN FETCH a.patient p " +
                        "JOIN FETCH p.user u " +
                        "WHERE a.doctor.user.id = :doctorId AND a.status IN ('COMPLETED', 'CANCELLED') " +
                        "ORDER BY a.date DESC")
        public List<Appointment> getEarningsAppointmentsByDoctorId(@Param("doctorId") Long doctorId);

        @Query("SELECT COUNT(a) FROM Appointment a WHERE a.doctor.user.id = :doctorId AND a.status IN ('COMPLETED', 'CANCELLED')")
        public Long getTotalEarningsCount(@Param("doctorId") Long doctorId);

        // Chart data - last N days - Using JPA instead of native SQL for better
        // compatibility
        @Query("SELECT a.date FROM Appointment a " +
                        "WHERE a.doctor.user.id = :doctorId AND a.status = 'COMPLETED' " +
                        "AND a.date >= :startDate " +
                        "GROUP BY a.date " +
                        "ORDER BY a.date")
        public List<LocalDate> getChartDates(@Param("doctorId") Long doctorId, @Param("startDate") LocalDate startDate);

        @Query("SELECT a.date, COALESCE(SUM(a.fee), 0) FROM Appointment a " +
                        "WHERE a.doctor.user.id = :doctorId AND a.status = 'COMPLETED' " +
                        "AND a.date >= :startDate " +
                        "GROUP BY a.date " +
                        "ORDER BY a.date")
        public List<Object[]> getChartDataWithDates(@Param("doctorId") Long doctorId,
                        @Param("startDate") LocalDate startDate);

        // patients things

        @Query("SELECT a FROM Appointment a JOIN FETCH a.doctor d JOIN FETCH d.user WHERE a.patient.user.id = :patientId")
        public List<Appointment> findByPatientId(Long patientId);

        // Booking Appointment

        @Modifying
        @Transactional
        @Query("INSERT INTO Appointment (doctor, patient, date, slot, durationMinutes, fee, status, paymentDone) " +
                        "VALUES (:doctor, :patient, :date, :slot, :durationMinutes, :fee, 'BOOKED', false)")
        public void bookAppointment(@Param("doctor") Doctor doctor, @Param("patient") Patient patient,
                        @Param("date") LocalDate date, @Param("slot") LocalTime slot,
                        @Param("durationMinutes") int durationMinutes, @Param("fee") double fee);


        @Modifying
        @Transactional
        @Query("UPDATE Appointment a SET a.meetingLink = :meetingLink WHERE a.id = :appointmentId")
        public void updateMeetingLink(@Param("appointmentId") Long appointmentId, @Param("meetingLink") String meetingLink);
}
