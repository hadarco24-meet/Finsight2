package com.example.finsight1;

public class WeeklyTrack {

    double expenses;
    double income;

    public WeeklyTrack(double expenses, double income) {
        this.expenses = expenses;
        this.income = income;
    }

    public WeeklyTrack() {
    }

    public double getExpenses() {
        return expenses;
    }

    public void setExpenses(double expenses) {
        this.expenses = expenses;
    }

    public double getIncome() {
        return income;
    }

    public void setIncome(double income) {
        this.income = income;
    }
}
