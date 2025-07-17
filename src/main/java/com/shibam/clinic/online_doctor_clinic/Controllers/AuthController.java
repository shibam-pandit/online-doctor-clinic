package com.shibam.clinic.online_doctor_clinic.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shibam.clinic.online_doctor_clinic.Services.AuthService;

@Controller
public class AuthController {
    @GetMapping("/register")
    public String register() {
        return "auth/register";
    }

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public String handleRegister(@RequestParam("role") String role,
            @RequestParam("name") String name,
            @RequestParam("email") String email,
            @RequestParam("phone") String phone,
            @RequestParam("password") String password,
            @RequestParam(value = "qualification", required = false) String qualification,
            @RequestParam(value = "specialization", required = false) String specialization,
            @RequestParam(value = "experience", required = false) Integer experience,
            RedirectAttributes redirectAttributes) {
            
        System.out.println("Registering user with role: " + role + ", name: " + name + ", email: " + email);

        try {
            if ("doctor".equals(role)) {
                authService.registerDoctor(name, email, phone, password, qualification, specialization, experience);
            } else if ("patient".equals(role)) {
                authService.registerPatient(name, email, phone, password);
            } else {
                throw new IllegalArgumentException("Invalid role specified");
            }

            redirectAttributes.addFlashAttribute("success", "Registration Successful");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Registration Failed: " + e.getMessage());
            return "redirect:/register"; // Redirect back to registration page with error message
        }

        return "redirect:/login"; // Redirect to login after successful registration
    }
}
