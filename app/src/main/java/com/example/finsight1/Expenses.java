package com.example.finsight1;

public class Expenses {
    private Day day;
    private double amount;

    public Expenses() {}

    public Expenses(Day day, double amount) {
        this.day = day;
        this.amount = amount;
    }

    public Day getDay() { return day; }
    public void setDay(Day day) { this.day = day; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    @Override
    public String toString() {
        return "Expense: " + "day: " + day + ", amount:" + amount;
    }
}
