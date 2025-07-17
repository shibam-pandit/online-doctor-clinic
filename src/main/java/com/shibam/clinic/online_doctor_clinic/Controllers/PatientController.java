package com.shibam.clinic.online_doctor_clinic.Controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/patient")
public class PatientController {
    @GetMapping({"", "/"})
    public String patientHome() {
        return "patient/home";
    }
}
