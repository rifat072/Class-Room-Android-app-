package com.example.rifat.classroom.Fragments.RoutineFragments;

public class subject {
    String time;
    String title;
    String day;

    public subject(){

    }

    public subject(String time, String title, String day) {
        this.time = time;
        this.title = title;
        this.day = day;
    }

    public String getTime() {
        return time;
    }

    public String getTitle() {
        return title;
    }

    public String getDay() {
        return day;
    }
}
