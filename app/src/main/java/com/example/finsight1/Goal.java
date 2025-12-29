package com.example.finsight1;

import java.util.List;

public class Goal {

    //מוזנים בהתחלה
    private String goalName;
    private double requiredAmount;
    private double currentAmount;
    private int monthsTillDue;
    private int workDaysPerWeek;
    private double monthlyExpenses;


    //מוזנים תוך כדי
    private List<WeeklyTrack> weeklyTrack;

    //מחושבים
    private double expectedWeeklyIncome;
    private double expectedWeeklyExpenses;

    public Goal() {}

    public Goal(String goalName, double requiredAmount, double currentAmount, int monthsTillDue, int workDaysPerWeek, double monthlyExpenses) {
        this.goalName = goalName;
        this.requiredAmount = requiredAmount;
        this.currentAmount = currentAmount;
        this.monthsTillDue = monthsTillDue;
        this.workDaysPerWeek = workDaysPerWeek;
        this.monthlyExpenses = monthlyExpenses;
    }

    public String getGoalName() { return goalName; }
    public void setGoalName(String goalName) { this.goalName = goalName; }

    public double getRequiredAmount() { return requiredAmount; }
    public void setRequiredAmount(double requiredAmount) { this.requiredAmount = requiredAmount; }

    public double getCurrentAmount() { return currentAmount; }
    public void setCurrentAmount(double currentAmount) { this.currentAmount = currentAmount; }

    public int getMonthsTillDue() {return monthsTillDue;}

    public void setMonthsTillDue(int monthsTillDue) {this.monthsTillDue = monthsTillDue;}

    public int getWorkDaysPerWeek() { return workDaysPerWeek; }
    public void setWorkDaysPerWeek(int workDaysPerWeek) { this.workDaysPerWeek = workDaysPerWeek; }

    public double getMonthlyExpenses() { return monthlyExpenses; }
    public void setMonthlyExpenses(double monthlyExpenses) { this.monthlyExpenses = monthlyExpenses; }


    @Override
    public String toString() {
        return "Goal: " +
                "Name='" + goalName +
                ", Required=" + requiredAmount +
                ", Current=" + currentAmount +
                ", MonthsTillDue=" + monthsTillDue +
                ", WorkDaysPerWeek=" + workDaysPerWeek +
                ", MonthlyExpenses=" + monthlyExpenses;
    }

    private void weeklyInfo(){
        expectedWeeklyIncome = this.requiredAmount / this.monthsTillDue / 4.2;  //4.2 שבועות בחודש
        expectedWeeklyExpenses = this.monthlyExpenses / 4.2;    //4.2 שבועות בחודש
    }

}
