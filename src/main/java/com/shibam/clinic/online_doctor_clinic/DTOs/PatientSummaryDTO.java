package com.shibam.clinic.online_doctor_clinic.DTOs;

import java.time.LocalDate;

public class PatientSummaryDTO {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private String gender;
    private Integer age;
    private String profileImage;
    private Integer totalVisits;
    private LocalDate lastVisitDate;
    private String lastPrescriptionSnippet;

    // Constructors
    public PatientSummaryDTO() {
    }

    public PatientSummaryDTO(Long id, String name, String email, String phone, String gender, Integer age,
            String profileImage, Integer totalVisits, LocalDate lastVisitDate, String lastPrescriptionSnippet) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.gender = gender;
        this.age = age;
        this.profileImage = profileImage;
        this.totalVisits = totalVisits;
        this.lastVisitDate = lastVisitDate;
        this.lastPrescriptionSnippet = lastPrescriptionSnippet;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public Integer getTotalVisits() {
        return totalVisits;
    }

    public void setTotalVisits(Integer totalVisits) {
        this.totalVisits = totalVisits;
    }

    public LocalDate getLastVisitDate() {
        return lastVisitDate;
    }

    public void setLastVisitDate(LocalDate lastVisitDate) {
        this.lastVisitDate = lastVisitDate;
    }

    public String getLastPrescriptionSnippet() {
        return lastPrescriptionSnippet;
    }

    public void setLastPrescriptionSnippet(String lastPrescriptionSnippet) {
        this.lastPrescriptionSnippet = lastPrescriptionSnippet;
    }
}
