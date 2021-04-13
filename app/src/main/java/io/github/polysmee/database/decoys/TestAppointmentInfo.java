package io.github.polysmee.database.decoys;

import io.github.polysmee.database.User;

public class TestAppointmentInfo {

    public  String name;
    public String course;
    public long start;
    public long duration;
    public User owner;

    public TestAppointmentInfo(String name, String course, long start, long duration, User owner) {
        this.name = name;
        this.course = course;
        this.start = start;
        this.duration = duration;
        this.owner = owner;
    }
}
