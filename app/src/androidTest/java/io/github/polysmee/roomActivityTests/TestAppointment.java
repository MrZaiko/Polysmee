package io.github.polysmee.roomActivityTests;

import java.io.Serializable;
import java.util.Objects;
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
        return participants;
    }

    @Override
    public Set<User> getBans() {
        return null;
    }

    @Override
    public User getOwner() {
        return null;
    }

    @Override
    public boolean isPrivate() {
        return false;
    }

    @Override
    public boolean addParticipant(User newParticipant) {
        return participants.add(newParticipant);
    }

    @Override
    public boolean removeParticipant(User participant) {
        return participants.remove(participant);
    }

    @Override
    public boolean addBan(User banned) {
        return false;
    }

    @Override
    public boolean removeBan(User unbanned) {
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TestAppointment that = (TestAppointment) o;
        return startTime == that.startTime &&
                duration == that.duration &&
                Objects.equals(course, that.course) &&
                Objects.equals(title, that.title) &&
                Objects.equals(participants, that.participants);
    }

    @Override
    public int hashCode() {
        return Objects.hash(startTime, duration, course, title, participants);
    }
}
