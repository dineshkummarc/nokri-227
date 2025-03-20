package com.scriptsbundle.nokri.candidate.edit.models;

public class TimeModel {
    public TimeModel(String time, String day) {
        this.time = time;
        this.day = day;
    }

    public String time;
    public String day;
    public boolean closed = false;
}
