package com.shibam.clinic.online_doctor_clinic.Models;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
public class DoctorAvailability {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Doctor doctor;

    private LocalDate date;

    // private LocalTime startTime;
    // private LocalTime endTime;

    private int sessionMinutes;
    private double price;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private List<LocalTime> availableSlotStarts;  // Store as JSON array of LocalTime

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    // public LocalTime getStartTime() {
    //     return startTime;
    // }

    // public void setStartTime(LocalTime startTime) {
    //     this.startTime = startTime;
    // }

    // public LocalTime getEndTime() {
    //     return endTime;
    // }

    // public void setEndTime(LocalTime endTime) {
    //     this.endTime = endTime;
    // }

    public int getSessionMinutes() {
        return sessionMinutes;
    }

    public void setSessionMinutes(int sessionMinutes) {
        this.sessionMinutes = sessionMinutes;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public List<LocalTime> getAvailableSlotStarts() {
        return availableSlotStarts;
    }

    public void setAvailableSlotStarts(List<LocalTime> availableSlotStarts) {
        this.availableSlotStarts = availableSlotStarts;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    // constructor
    public DoctorAvailability() {
    }

    public DoctorAvailability(Doctor doctor, LocalDate date, int sessionMinutes,
            double price) {
        this.doctor = doctor;
        this.date = date;
        // this.startTime = startTime;
        // this.endTime = endTime;
        this.sessionMinutes = sessionMinutes;
        this.price = price;
    }
}
