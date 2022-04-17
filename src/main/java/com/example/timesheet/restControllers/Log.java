package com.example.timesheet.restControllers;

public class Log {
    private int id;
    private String employeeName;
    private int employeeId;
    private String projectName;
    private int projectId;
    private String companyName;
    // private int month;
    // private int year;
    private String periodName;
    private int periodId;
    private String service;
    private String details;
    private int hoursWorkedTimes1000;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    // public int getMonth() {
    //     return month;
    // }

    // public void setMonth(int month) {
    //     this.month = month;
    // }

    // public int getYear() {
    //     return year;
    // }

    // public void setYear(int year) {
    //     this.year = year;
    // }

    public String getPeriodName() {
        return periodName;
    }

    public void setPeriodName(String periodName) {
        this.periodName = periodName;
    }

    public int getPeriodId() {
        return periodId;
    }

    public void setPeriodId(int periodId) {
        this.periodId = periodId;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public int getHoursWorkedTimes1000() {
        return hoursWorkedTimes1000;
    }

    public void setHoursWorkedTimes1000(int hoursWorked) {
        this.hoursWorkedTimes1000 = hoursWorked;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    @Override
    public String toString() {
        return "Log{" +
                "id=" + id +
                ", employeeName='" + employeeName + '\'' +
                ", employeeId=" + employeeId +
                ", projectName='" + projectName + '\'' +
                ", projectId=" + projectId +
                ", companyName='" + companyName + '\'' +
                // ", month=" + month +
                // ", year=" + year +
                ", periodId=" + periodId +
                ", service='" + service + '\'' +
                ", details='" + details + '\'' +
                ", hoursWorkedTimes1000=" + hoursWorkedTimes1000 +
                '}';
    }
}
