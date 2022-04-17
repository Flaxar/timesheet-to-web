package com.example.timesheet.restControllers;

public class Employee {
    private int id;
    private String name;
    private String department;
    private String workHoursType;
    private int workHoursPer100Days;
    private String role;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getWorkHoursType() {
        return workHoursType;
    }

    public void setWorkHoursType(String workHoursType) {
        this.workHoursType = workHoursType;
    }

    public int getWorkHoursPer100Days() {
        return workHoursPer100Days;
    }

    public void setWorkHoursPer100Days(int workHoursPer100Days) {
        this.workHoursPer100Days = workHoursPer100Days;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "Employee{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", department='" + department + '\'' +
                ", workHoursType='" + workHoursType + '\'' +
                ", workHoursPer100Days=" + workHoursPer100Days +
                '}';
    }
}
