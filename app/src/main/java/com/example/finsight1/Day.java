package com.example.finsight1;

public class Day {
    private Date date;
    private Hour time;

    public Day(Date date, Hour time) {
        this.date = date;
        this.time = time;
    }

    public Day() {}

    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }


    public Hour getTime() { return time; }
    public void setTime(Hour time) { this.time = time; }


    @Override
    public String toString() {
        return date.toString() + " " + time.toString();
    }
}
