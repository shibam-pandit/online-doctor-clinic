package com.shibam.clinic.online_doctor_clinic.Models;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Doctor doctor;

    @ManyToOne(fetch = FetchType.LAZY)
    private Patient patient;

    private LocalDate date;

    private LocalTime slot;

    private int durationMinutes; 

    private double fee;

    public enum AppointmentStatus {
        BOOKED,
        CANCELLED_BY_DOCTOR,
        CANCELLED_BY_PATIENT,
        COMPLETED
    }
    @Enumerated(EnumType.STRING)
    private AppointmentStatus status;

    private String prescription;

    private String meetingLink;

    private boolean paymentDone;

    private double rating;

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

    public Patient getPatient() {
        return patient;
    }
    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public LocalDate getDate() {
        return date;
    }
    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getSlot() {
        return slot;
    }
    public void setSlot(LocalTime slot) {
        this.slot = slot;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }
    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public double getFee() {
        return fee;
    }
    public void setFee(double fee) {
        this.fee = fee;
    }

    public AppointmentStatus getStatus() {
        return status;
    }
    public void setStatus(AppointmentStatus status) {
        this.status = status;
    }

    public String getPrescription() {
        return prescription;
    }
    public void setPrescription(String prescription) {
        this.prescription = prescription;
    }

    public String getMeetingLink() {
        return meetingLink;
    }
    public void setMeetingLink(String meetingLink) {
        this.meetingLink = meetingLink;
    }

    public boolean isPaymentDone() {
        return paymentDone;
    }
    public void setPaymentDone(boolean paymentDone) {
        this.paymentDone = paymentDone;
    }

    public double getRating() {
        return rating;
    }
    public void setRating(double rating) {
        this.rating = rating;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    // constructor
    public Appointment() {
    }

    public Appointment(Doctor doctor, Patient patient, LocalDate date, LocalTime slot, double fee, int durationMinutes) {
        this.doctor = doctor;
        this.patient = patient;
        this.date = date;
        this.slot = slot;
        this.durationMinutes = durationMinutes;
        this.status = AppointmentStatus.BOOKED; // Default status
    }
}
