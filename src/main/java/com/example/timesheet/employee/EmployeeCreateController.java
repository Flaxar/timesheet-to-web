package com.example.timesheet.employee;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Controller
@RequestMapping
public class EmployeeCreateController {

    @GetMapping("/employee")
    String getEmployee(@AuthenticationPrincipal User user, Model model) {

        if(General.checkIfFirstLogin(user)) {
            return "setPassword";
        }

        model.addAttribute("title", "Filip Schwarz");
        return "employee";
    }


    @GetMapping("/admin/employees")
    String getEmployee() {
        return "employeeManager";
    }
}
