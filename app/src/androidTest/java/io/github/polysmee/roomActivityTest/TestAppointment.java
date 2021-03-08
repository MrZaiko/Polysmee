package io.github.polysmee.roomActivityTest;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import io.github.polysmee.interfaces.Appointment;
import io.github.polysmee.interfaces.User;

public class TestAppointment implements Appointment, Serializable {
    long startTime;
    long duration;
    String course;
    String title;
    Set<User> participants;

    public TestAppointment(long startTime, long duration, String course,
                           String title, Set<User> participants) {
        this.startTime = startTime;
        this.duration = duration;
        this.course = course;
        this.title = title;
        this.participants = participants;
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
        return new HashSet<>();
    }

    @Override
    public void addParticipant(User newParticipant) {
        participants.add(newParticipant);
    }

    @Override
    public void removeParticipant(User participant) {
        participants.remove(participant);
    }
}
