package com.example.finsight1;

public class Incomes {
    private Day day;
    private double amount;

    public Incomes() {}

    public Incomes(Day day, double amount) {
        this(day, amount, null);
    }

    public Incomes(Day day, double amount, String source) {
        this.day = day;
        this.amount = amount;
    }

    public Day getDay() { return day; }
    public void setDay(Day day) { this.day = day; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    @Override
    public String toString() {
        return "Income: " + "day: " + day + ", amount: " + amount ;
    }
}

