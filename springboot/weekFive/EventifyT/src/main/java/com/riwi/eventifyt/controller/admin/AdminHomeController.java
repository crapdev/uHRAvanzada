package com.riwi.eventifyt.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller                 // returns view names, not JSON
@RequestMapping("/admin")
public class AdminHomeController {

    @GetMapping
    public String home() {
        return "admin/home";   // → templates/admin/home.html
    }
}