package com.example.timesheet.employee;

import com.example.timesheet.DatabaseController;
import org.springframework.data.util.Pair;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.Model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

public class Admin {

    DatabaseController databaseController;

    Admin() {
        databaseController = DatabaseController.getInstance();
    }

    public void generateModel(Model model) {

        String date = generateDate();
        model.addAttribute("date", date);


        List<String> pair = generateWorkRatioAndLeftToWork();
        String workRatio  = pair.get(0);
        String leftToWork = pair.get(1);

        model.addAttribute("hours_worked_ratio", workRatio);
        model.addAttribute("left_to_work_hours", leftToWork);

        String name = generateName();
        model.addAttribute("name", name);

    }

//    =============================================================================

    private String generateDate() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();

        int year  = now.getYear();
        int month = now.getMonthValue();

        return month + " / " + year;
    }

    private List<String> generateWorkRatioAndLeftToWork() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        String username;
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }

        double monthWork  = databaseController.getUserWorkHoursPerPeriod(username);
        double worked     = databaseController.getUserWorkedHours(username);
        double leftToWork = monthWork - worked;

        return Arrays.asList(worked + "h / " + monthWork + "h", "Zbývá " + leftToWork + "h");
    }

    private String generateName() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        String username;
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }

        return databaseController.getName(username);
    }
}
