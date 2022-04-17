package com.example.timesheet.restControllers;

import com.example.timesheet.DatabaseController;
import com.google.gson.Gson;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class EmployeeController {

    DatabaseController databaseController = DatabaseController.getInstance();

    @RequestMapping(value = "/api/employees/get", method = RequestMethod.GET)
    public List<Employee> getAllEmployees() {
        return databaseController.getAllEmployees();
    }

    @RequestMapping(value = "/api/employee/add", method = RequestMethod.POST)
    public String addEmployee(@RequestParam(value = "json") String json) {
        Gson gson = new Gson();
        Employee newEmployee = gson.fromJson(json, Employee.class);
        return databaseController.addEmployee(newEmployee);
    }

    @RequestMapping(value = "/api/employee/update", method = RequestMethod.POST)
    public void updateEmployee(@RequestParam(value = "json") String json) {
        Gson gson = new Gson();
        Employee updatedEmployee = gson.fromJson(json, Employee.class);
        databaseController.updateEmployee(updatedEmployee);
    }

    @RequestMapping(value = "/api/employee/delete", method = RequestMethod.DELETE)
    public void deleteEmployee(@RequestParam(value = "id") String idText) {
        int idToDelete = Integer.parseInt(idText);
        databaseController.deleteEmployee(idToDelete);
    }

}
