package com.example.timesheet.login;

import com.example.timesheet.GeneralStorage;
import com.example.timesheet.employee.General;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    String getEmployee(@RequestParam(name = "new", defaultValue = "no") String isNew, Model model) {

        if(isNew.equals("yes")) {
            model.addAttribute("isHidden", "shown");
            model.addAttribute("usernameText", "Nové přihlašovací jméno: " + GeneralStorage.lastAddedUsername);
        }

        return "login";
    }
}
