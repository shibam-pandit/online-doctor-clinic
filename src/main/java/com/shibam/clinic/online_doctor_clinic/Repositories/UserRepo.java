package com.shibam.clinic.online_doctor_clinic.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.shibam.clinic.online_doctor_clinic.Models.User;

@Repository
public interface UserRepo extends JpaRepository<User, Long> {
    public boolean existsByEmail(String email);

    public User findByEmail(String email);
}
