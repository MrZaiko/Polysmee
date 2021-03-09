package io.github.polysmee.appointments;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import io.github.polysmee.interfaces.Appointment;
import io.github.polysmee.interfaces.User;

public class BasicAppointment implements Appointment, Serializable {
    private long startTime;
    private long duration;
    private String course;
    private String title;
    private final Set<User> participants;
    private final User owner;

    public BasicAppointment(long startTime, long duration, String course, String title, User owner) {
        if(startTime < 0) {
            this.startTime = 0;
        } else {
            this.startTime = startTime;
        }
        if(duration < 0 ) {
            this.duration = 0;
        } else if (duration > 3600000*4) {
            this.duration = 3600000*4;
        } else {
            this.duration = duration;
        }
        this.course = course;
        this.title = title;
        participants = new HashSet<User>();
        participants.add(owner);
        this.owner = owner;
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
        return Collections.unmodifiableSet(participants);
    }

    @Override
    public User getOwner() {
        return owner;
    }

    @Override
    public boolean setStartTime(long startTime) {
        if (startTime < 0) {
            return false;
        }
        this.startTime = startTime;
        return true;
    }

    @Override
    public boolean setDuration(long duration) {
        if(duration > 3600000*4 || duration < 0) {
            return false;
        }
        this.duration = duration;
        return true;
    }

    @Override
    public void setCourse(String course) {
        this.course = course;
    }

    @Override
    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public boolean addParticipant(User newParticipant) {
        return participants.add(newParticipant);
    }

    @Override
    public boolean removeParticipant(User participant) {
        if(participant.equals(owner)) {
            return false;
        }
        return participants.remove(participant);
    }
}
