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
    private final Set<User> bans;
    private final boolean isPrivate;

    public BasicAppointment(long startTime, long duration, String course, String title, User owner) {
        this(startTime, duration, course, title, owner, false, new HashSet<>(), new HashSet<>());
    }

    public BasicAppointment(long startTime, long duration, String course, String title, User owner, boolean isPrivate) {
        this(startTime, duration, course, title, owner, isPrivate, new HashSet<>(), new HashSet<>());
    }

    public BasicAppointment(long startTime, long duration, String course, String title, User owner, boolean isPrivate, Set<User> bans, Set<User> invites) {
        if(startTime < 0) {
            this.startTime = 0;
        } else {
            this.startTime = startTime;
        }
        //duration cannot be less than 0 or more than 4 hours
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
        this.isPrivate = isPrivate;
        this.bans = bans;
        participants.addAll(invites);
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
    public Set<User> getBans() {
        return Collections.unmodifiableSet(bans);
    }

    @Override
    public User getOwner() {
        return owner;
    }

    @Override
    public boolean isPrivate() {
        return isPrivate;
    }

    public boolean setStartTime(long startTime) {
        if (startTime < 0) {
            return false;
        }
        this.startTime = startTime;
        return true;
    }

    public boolean setDuration(long duration) {
        if(duration > 3600000*4 || duration < 0) {
            return false;
        }
        this.duration = duration;
        return true;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public boolean addParticipant(User newParticipant) {
        if (bans.contains(newParticipant)) {
            return false;
        }
        return participants.add(newParticipant);
    }

    @Override
    public boolean removeParticipant(User participant) {
        if(participant.equals(owner)) {
            return false;
        }
        return participants.remove(participant);
    }

    @Override
    public boolean addBan(User banned) {
        if(banned.equals(owner)) {
            return false;
        }
        return bans.add(banned);
    }

    @Override
    public boolean removeBan(User unbanned) {
        return bans.remove(unbanned);
    }
}
