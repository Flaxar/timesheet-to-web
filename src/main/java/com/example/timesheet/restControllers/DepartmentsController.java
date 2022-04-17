package com.example.timesheet.restControllers;

import com.example.timesheet.DatabaseController;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
public class DepartmentsController {

    DatabaseController databaseController = DatabaseController.getInstance();

    @RequestMapping(value = "/api/departments", method = RequestMethod.GET)
    List<String> getDepartments() {
        return databaseController.getDepartments();
    }

    @RequestMapping(value = "/api/departments/post",  method = RequestMethod.POST)
    void postDepartments(@RequestParam(value = "array") String[] array) {
        databaseController.addDepartments(Arrays.asList(array));
    }
}
