package com.example.timesheet.restControllers;

import com.example.timesheet.DatabaseController;
import com.google.gson.Gson;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProjectController {

    DatabaseController databaseController = DatabaseController.getInstance();

    @RequestMapping(value = "/api/project/add", method = RequestMethod.POST)
    public void addProject(@RequestParam(value = "json") String json) {
        Gson gson = new Gson();
        Project newProject = gson.fromJson(json, Project.class);
        databaseController.addProject(newProject);
    }
}
