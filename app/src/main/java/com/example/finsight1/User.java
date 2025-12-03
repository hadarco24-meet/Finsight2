package com.example.finsight1;

import java.util.ArrayList;


public class User {
    private String username;
    private String password;
    private ArrayList<Goal> goals;

    public static User currentUser = null;

    public User(String username, String password){
        this.username = username;
        this.password = password;
        this.goals = new ArrayList<>();
    }

    public User(){
        this.goals = new ArrayList<>();
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public ArrayList<Goal> getGoals() { return goals; }
    public void setGoals(ArrayList<Goal> goals) { this.goals = goals; }

    @Override
    public String toString() {
        return "Username: " +  username;
    }
}
