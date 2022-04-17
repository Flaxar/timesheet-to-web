package com.example.timesheet.restControllers;

import com.example.timesheet.DatabaseController;
import com.example.timesheet.Date;
import com.google.gson.Gson;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BankHolidaysController {

    DatabaseController databaseController = DatabaseController.getInstance();

    @RequestMapping(value = "/api/bankHolidays/post", method = RequestMethod.POST)
    void addLog(@RequestParam(value = "json") String json) {
        Gson gson = new Gson();
        Date newBankHoliday = gson.fromJson(json, Date.class);
        databaseController.addBankHoliday(newBankHoliday);
    }
}
