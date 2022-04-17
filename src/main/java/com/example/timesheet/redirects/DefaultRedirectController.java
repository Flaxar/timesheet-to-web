package com.example.timesheet.redirects;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class DefaultRedirectController {

    /*
     * this method ensures, you get redirected from default
     * endpoint to the login endpoint
     *
     * E.g.
     * www.your-adress.com -> www.your-adress.com/login
     */
    @GetMapping
    ModelAndView redirect() {
        return new ModelAndView("redirect:/login");
    }
}
