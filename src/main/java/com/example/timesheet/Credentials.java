package com.example.timesheet;

public class Credentials {
    private final String username;
    private final String password;
    private final String role;

    public Credentials(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }
}
