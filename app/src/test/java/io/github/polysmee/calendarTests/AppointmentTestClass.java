package io.github.polysmee.calendarTests;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import io.github.polysmee.interfaces.Appointment;
import io.github.polysmee.interfaces.User;

public class AppointmentTestClass implements Appointment {

    private final long startTime;
    private final long duration;
    private final String course;
    private final String title;
    private Set<User> users = new HashSet<>();

    public AppointmentTestClass(long startTime, long duration, String course, String title){
        this.startTime = startTime;
        this.duration = duration;
        this.course = new String(course);
        this.title = new String(title);
    }
    @Override
    public long getStartTime() {
        return startTime;
    }

    @Override
    public long getDuration() {
        return duration;
    }

    @Override
    public String getCourse() {
        return course;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public Set<User> getParticipants() {
        return users;
    }

    @Override
    public User getOwner() {
        return null;
    }

    @Override
    public boolean addParticipant(User newParticipant) {
        users.add(newParticipant);
        return false;
    }

    @Override
    public boolean removeParticipant(User participant) {
        users.add(participant);
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(startTime);
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || o.getClass() != this.getClass()) return false;
        else{
            AppointmentTestClass that = (AppointmentTestClass)o;
            return that.startTime == this.startTime && that.users.equals(this.users) && that.title.equals(this.title) && that.course.equals(this.course) && that.duration == this.duration;
        }
    }
}