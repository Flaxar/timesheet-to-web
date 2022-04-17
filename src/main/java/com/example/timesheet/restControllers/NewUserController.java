package com.example.timesheet.restControllers;

import com.example.timesheet.DatabaseController;
import com.example.timesheet.GeneralStorage;
import com.google.gson.Gson;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NewUserController {

    DatabaseController databaseController = DatabaseController.getInstance();

    @RequestMapping(value = "/api/newUser/post", method = RequestMethod.POST)
    void addNewUser(@RequestParam(value = "json") String json) {
        Gson gson = new Gson();
        NewUser newUser = gson.fromJson(json, NewUser.class);
        System.out.println(newUser);
        databaseController.addNewUser(newUser);
    }
}
