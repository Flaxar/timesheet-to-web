package com.example.timesheet;

public class ExcelRow {
    private String employeeName;
    private String project;
    private String company;
    private String service;
    private String details;
    private Double hoursWorked;

    public void setProject(String abbr, String name) {
        project = abbr + " - " + name;
    }

    public String getProject() {
        return project;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public Double getHoursWorked() {
        return hoursWorked;
    }

    public void setHoursWorked(Double hoursWorked) {
        this.hoursWorked = hoursWorked;
    }
}