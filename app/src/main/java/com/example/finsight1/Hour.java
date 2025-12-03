package com.example.finsight1;

public class Hour {
    private int hour;
    private int minutes;

    public Hour(int hour, int minutes){
        this.hour = hour;
        this.minutes = minutes;
    }

    public int getHour() { return hour; }
    public void setHour(int hour) { this.hour = hour; }

    public int getMinute() { return minutes; }
    public void setMinute(int minute) { this.minutes = minute; }

    @Override
    public String toString() {
        return String.format("%02d:%02d", hour, minutes);
    }
}
