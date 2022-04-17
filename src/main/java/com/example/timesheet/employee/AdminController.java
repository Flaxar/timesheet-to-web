package com.example.timesheet.employee;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping
public class AdminController {

    @GetMapping("/admin")
    String getAdminHTML(@AuthenticationPrincipal User user) {
//        TODO: load values from database

        if(General.checkIfFirstLogin(user)) {
            return "setPassword";
        }

        return "admin";
    }
}
