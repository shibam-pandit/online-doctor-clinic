package com.shibam.clinic.online_doctor_clinic.Controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shibam.clinic.online_doctor_clinic.Models.Doctor;
import com.shibam.clinic.online_doctor_clinic.Services.DoctorService;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private DoctorService doctorService;

    @GetMapping({ "", "/" })
    public String adminHome(Model model) {
        List<Doctor> pendingDoctors = doctorService.getPendingDoctors();
        model.addAttribute("pendingDoctors", pendingDoctors);
        model.addAttribute("pendingCount", pendingDoctors.size());
        return "admin/home";
    }

    @GetMapping("/doctors")
    public String manageDoctors(Model model) {
        List<Doctor> allDoctors = doctorService.getAllDoctors();
        List<Doctor> pendingDoctors = doctorService.getPendingDoctors();

        model.addAttribute("allDoctors", allDoctors);
        model.addAttribute("pendingDoctors", pendingDoctors);
        return "admin/doctors";
    }

    @PostMapping("/approve-doctor/{id}")
    public String approveDoctor(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Doctor doctor = doctorService.approveDoctor(id);
        if (doctor != null) {
            redirectAttributes.addFlashAttribute("success",
                    "Doctor " + doctor.getUser().getName() + " has been approved successfully!");
        } else {
            redirectAttributes.addFlashAttribute("error", "Failed to approve doctor.");
        }
        return "redirect:/admin/doctors";
    }

    @PostMapping("/reject-doctor/{id}")
    public String rejectDoctor(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Doctor doctor = doctorService.rejectDoctor(id);
        if (doctor != null) {
            redirectAttributes.addFlashAttribute("warning",
                    "Doctor " + doctor.getUser().getName() + " has been rejected.");
        } else {
            redirectAttributes.addFlashAttribute("error", "Failed to reject doctor.");
        }
        return "redirect:/admin/doctors";
    }
}
