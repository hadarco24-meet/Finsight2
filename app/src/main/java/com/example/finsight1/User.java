package com.example.finsight1;

import java.util.ArrayList;


public class User {
    private String username;
    private String password;
    private String currency;
    private ArrayList<Goal> goals;
    private boolean isDarkMode;

    public static User currentUser = null;

    public User(String username, String password){
        this.username = username;
        this.password = password;
        this.goals = new ArrayList<>();
        this.currency = "₪";
    }

    public User() {
        this.goals = new ArrayList<>();
        this.currency = "₪";
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public ArrayList<Goal> getGoals() { return goals; }
    public void setGoals(ArrayList<Goal> goals) { this.goals = goals; }

    public boolean isDarkMode() {
        return isDarkMode;
    }

    public void setDarkMode(boolean darkMode) {
        isDarkMode = darkMode;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    @Override
    public String toString() {
        return "Username: " +  username;
    }
}
