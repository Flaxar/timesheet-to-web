package com.example.timesheet.restControllers;

public class WorkHours {
    private String type;
    private Integer hours;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getHours() {
        return hours;
    }

    public void setHours(Integer hours) {
        this.hours = hours;
    }

    @Override
    public String toString() {
        return "WorkHours{" +
                "type='" + type + '\'' +
                ", hours=" + hours +
                '}';
    }
}
