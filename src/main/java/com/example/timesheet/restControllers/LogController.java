package com.example.timesheet.restControllers;

import com.example.timesheet.DatabaseController;
//import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class LogController {
    DatabaseController databaseController = DatabaseController.getInstance();
    Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    @RequestMapping(value = "/api/log/get/{id}", method = RequestMethod.GET)
    List<Log> fetchLogs(@PathVariable("id") Integer periodId, @AuthenticationPrincipal User user) {
        logger.info(user.getUsername() + " requested logs from period = " + periodId);
        return databaseController.getLogs(user.getUsername(), periodId);
    }

    @RequestMapping(value = "/api/log/create", method = RequestMethod.POST)
    Integer addLog(@RequestBody Log newLog) {
        Integer id = databaseController.addNewLog(newLog);
        if (id < 0) {
            // to produce an HTTP error:
            throw new RuntimeException("Test error message");   // FIXME message content?
        };
        return id;
    }

    @RequestMapping(value = "/api/log/update", method = RequestMethod.POST)
    Integer updateLog(@RequestBody Log updatedLog) {
        Integer id = databaseController.updateLog(updatedLog);
        if (id < 0) {
            // to produce an HTTP error:
            throw new RuntimeException("Test error message");   // FIXME message content?
        };
        return id;
    }

    @RequestMapping(value = "/api/log/delete", method = RequestMethod.DELETE)
    void deleteLog(@RequestParam(value = "id") String stringId) {
        Integer idToDelete = Integer.parseInt(stringId);
        databaseController.deleteLog(idToDelete);
    }

    @RequestMapping(value = "/api/projects/get", method = RequestMethod.GET)
    List<Project> fetchProjects() {
        return databaseController.getProjects();
    }

    @RequestMapping(value = "/api/periods/get", method = RequestMethod.GET)
    List<Period> fetchPeriods() {
        return databaseController.getPeriods();
    }

    @RequestMapping(value = "/api/employee_info/get", method = RequestMethod.GET)
    Employee fetchEmployeeInfo(@AuthenticationPrincipal User user) {
        return databaseController.getEmployee(user.getUsername());
    }


    @RequestMapping(value = "/api/period_info/get/default", method = RequestMethod.GET)
    Period fetchPeriodInfo() {
        return databaseController.getPeriodInfo(null);
    }
    @RequestMapping(value = "/api/period_info/get/{id}", method = RequestMethod.GET)
    Period fetchPeriodInfo(@PathVariable("id") Integer periodId) {
        return databaseController.getPeriodInfo(periodId);
    }
}
