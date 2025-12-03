package com.example.finsight1;

public class Goal {
    private String goalName;
    private double requiredAmount;
    private double currentAmount;
    private int timeTillDue;
    private int workDaysPerWeek;
    private double monthlyExpenses;

    public Goal() {}

    public Goal(String name, double requiredAmount, double currentAmount,
                int timeTillDue, int workDaysPerWeek, double monthlyExpenses) {
        this.goalName = name;
        this.requiredAmount = requiredAmount;
        this.currentAmount = currentAmount;
        this.timeTillDue = timeTillDue;
        this.workDaysPerWeek = workDaysPerWeek;
        this.monthlyExpenses = monthlyExpenses;
    }

    public String getGoalName() { return goalName; }
    public void setGoalName(String goalName) { this.goalName = goalName; }

    public double getRequiredAmount() { return requiredAmount; }
    public void setRequiredAmount(double requiredAmount) { this.requiredAmount = requiredAmount; }

    public double getCurrentAmount() { return currentAmount; }
    public void setCurrentAmount(double currentAmount) { this.currentAmount = currentAmount; }

    public int getTimeTillDue() { return timeTillDue; }
    public void setTimeTillDue(int timeTillDue) { this.timeTillDue = timeTillDue; }

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
                ", Months=" + timeTillDue +
                ", WorkDaysPerWeek=" + workDaysPerWeek +
                ", MonthlyExpenses=" + monthlyExpenses;
    }
}
