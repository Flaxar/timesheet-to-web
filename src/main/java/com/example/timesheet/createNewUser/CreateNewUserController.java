package com.example.timesheet.createNewUser;

import com.example.timesheet.DatabaseController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping
public class CreateNewUserController {

    @GetMapping("/create")
    String getCreate() {
        return "newUserCreate";
    }
}
