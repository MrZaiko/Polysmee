package io.github.polysmee.database;

import java.util.Set;

import io.github.polysmee.interfaces.Appointment;
import io.github.polysmee.interfaces.User;

public class DatabaseAppointment implements Appointment {

    private final String id;

    public DatabaseAppointment(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public long getStartTime() {
        return 0;
    }

    @Override
    public long getDuration() {
        return 0;
    }

    @Override
    public String getCourse() {
        return null;
    }

    @Override
    public String getTitle() {
        return null;
    }

    @Override
    public Set<User> getParticipants() {
        return null;
    }

    @Override
    public User getOwner() {
        return null;
    }

    @Override
    public boolean setStartTime(long startTime) {
        return false;
    }

    @Override
    public boolean setDuration(long duration) {
        return false;
    }

    @Override
    public void setCourse(String course) {

    }

    @Override
    public void setTitle(String title) {

    }

    @Override
    public boolean addParticipant(User newParticipant) {
        return false;
    }

    @Override
    public boolean removeParticipant(User participant) {
        return false;
    }
}
