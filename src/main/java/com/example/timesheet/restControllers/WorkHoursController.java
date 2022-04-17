package com.example.timesheet.restControllers;

import com.example.timesheet.DatabaseController;
import com.example.timesheet.WorkDays;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class WorkHoursController {

    DatabaseController databaseController = DatabaseController.getInstance();

    @RequestMapping(value = "/api/work_hours")
    List<Map.Entry<String, Integer>> getWorkHours() {
        return databaseController.getWorkHours();
    }

    @RequestMapping(value = "/api/work_hours/post", method = RequestMethod.POST)
    void addWorkHours(@RequestParam(value = "json") String workHoursString) {
        Gson gson = new Gson();
        List<WorkHours> workHours = gson.fromJson(workHoursString, new TypeToken<List<WorkHours>>(){}.getType());
        databaseController.addWorkHours(workHours);
    }

    @RequestMapping(value = "/api/work_days/get", method = RequestMethod.GET)
    Integer getWorkDaysInPeriod(@RequestParam(required=true) Integer periodId) {
        WorkDays workDays = new WorkDays();
        Period period = databaseController.getPeriodInfo(periodId);
        return workDays.getNumberOfWorkDaysInPeriod(period);
    }
}
