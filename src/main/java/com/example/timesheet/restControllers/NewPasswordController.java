package com.example.timesheet.restControllers;

import com.example.timesheet.DatabaseController;
import com.google.gson.Gson;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NewPasswordController {

    DatabaseController databaseController = DatabaseController.getInstance();

    @RequestMapping(value = "/api/new_password/post", method = RequestMethod.POST)
    void addNewUser(@RequestParam(value = "password") String passwordNotEncrypted, @AuthenticationPrincipal User user) {
        databaseController.updatePassword(passwordNotEncrypted, user.getUsername());
    }
}
