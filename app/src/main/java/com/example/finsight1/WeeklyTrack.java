package com.example.finsight1;

public class WeeklyTrack {

    int expenses;
    int income;

    public WeeklyTrack(int expenses, int income) {
        this.expenses = expenses;
        this.income = income;
    }

    public WeeklyTrack() {
    }

    public int getExpenses() {
        return expenses;
    }

    public void setExpenses(int expenses) {
        this.expenses = expenses;
    }

    public int getIncome() {
        return income;
    }

    public void setIncome(int income) {
        this.income = income;
    }
}
