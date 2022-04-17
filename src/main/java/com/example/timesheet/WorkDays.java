package com.example.timesheet;

// import java.util.Date;
//import java.time.*;
import com.example.timesheet.restControllers.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

public class WorkDays {

    DatabaseController databaseController;

    public WorkDays() {
        databaseController = DatabaseController.getInstance();
    };

    public Boolean isWorkDay(LocalDate date) {
        if (this.isWeekend(date) || this.isBankHoliday(date)) {
            return false;
        } else {
            return true;
        }
    }

    public Boolean isWeekend(LocalDate date) {
        DayOfWeek dayOfWeek = DayOfWeek.from(date);
        // int val = dayOfWeek.getValue(); // .name()
        return (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY);
    }

    public Boolean isBankHoliday(LocalDate date) {
        return databaseController.isBankHoliday(date);
    }

    public Integer getNumberOfWorkDays(LocalDate startDate, LocalDate endDate) {
        Integer ret = 0;
        for (LocalDate date = startDate; date.isBefore(endDate); date = date.plusDays(1)) {
            if (isWorkDay(date)) {
                ret++;
            }
        }
        return ret;
    }

    public Integer getNumberOfWorkDaysInPeriod(Period period) {
        if (period == null) {
            period = databaseController.getPeriodInfo(null);
        };
//        System.out.println("returning: " + period);
        return getNumberOfWorkDays(period.getStart(), period.getEnd().plusDays(1));
    }

    public Integer workDaysToWorkHours(Integer workDaysCount) {
        return workDaysCount * 8;
    }
}
