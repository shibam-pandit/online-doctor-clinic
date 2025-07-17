package com.shibam.clinic.online_doctor_clinic.Controllers;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.shibam.clinic.online_doctor_clinic.Models.Doctor;
import com.shibam.clinic.online_doctor_clinic.Services.DoctorService;

@Controller
@RequestMapping("/doctor")
public class DoctorController {

    @Autowired
    private DoctorService doctorService;

    @GetMapping({ "", "/" })
    public String doctorHome(Principal principal) {
        String email = principal.getName();
        if (!doctorService.isDoctorApproved(email)) {
            return "redirect:/doctor/wait";
        }
        return "doctor/home";
    }

    @GetMapping("/wait")
    public String doctorWait(Principal principal, Model model) {
        String email = principal.getName();
        Doctor doctor = doctorService.findByUserEmail(email);

        if (doctor != null && doctor.isApproved()) {
            return "redirect:/doctor";
        }

        model.addAttribute("doctor", doctor);
        return "doctor/wait";
    }
}
