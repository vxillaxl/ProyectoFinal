package com.smarttraffic.controller;


import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Profile("dashboard")
public class DashboardHomeController {

    @GetMapping("/")
    public String home() {
        return "redirect:/dashboard.html";
    }
}
