package com.example.timesheet.employee;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class General {

    public static boolean checkIfFirstLogin(User user) {
        // checks if current password == "default"
        return new BCryptPasswordEncoder().matches("default", user.getPassword());
    }
}
