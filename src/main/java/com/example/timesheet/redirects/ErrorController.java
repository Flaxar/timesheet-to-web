package com.example.timesheet.redirects;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ErrorController {

    @GetMapping("/error")
    ModelAndView redirect() {
        return new ModelAndView("redirect:/login");
    }
}
