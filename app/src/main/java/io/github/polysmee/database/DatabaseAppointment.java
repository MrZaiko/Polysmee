package io.github.polysmee.database;

import com.google.firebase.database.FirebaseDatabase;

import java.util.Set;

import io.github.polysmee.database.databaselisteners.BooleanValueListener;
import io.github.polysmee.database.databaselisteners.LongValueListener;
import io.github.polysmee.database.databaselisteners.StringSetValueListener;
import io.github.polysmee.database.databaselisteners.StringValueListener;
import io.github.polysmee.interfaces.Appointment;
import io.github.polysmee.interfaces.User;

public class DatabaseAppointment implements Appointment {

    private final String id;

    public DatabaseAppointment(String id) {
        this.id = id;
    }

    @Override
    public void getStartTimeAndThen(LongValueListener l) {
        DatabaseFactory
                .getAdaptedInstance()
                .getReference("appointments")
                .child(id)
                .child("start")
                .addValueEventListener(l);
    }

    @Override
    public void getDurationAndThen(LongValueListener l) {
        DatabaseFactory
                .getAdaptedInstance()
                .getReference("appointments")
                .child(id)
                .child("duration")
                .addValueEventListener(l);
    }

    @Override
    public void getCourseAndThen(StringValueListener s) {
        DatabaseFactory
                .getAdaptedInstance()
                .getReference("appointments")
                .child(id)
                .child("course")
                .addValueEventListener(s);
    }

    @Override
    public void getTitleAndThen(StringValueListener s) {
        DatabaseFactory
                .getAdaptedInstance()
                .getReference("appointments")
                .child(id).child("title")
                .addValueEventListener(s);
    }

    @Override
    public void getParticipantsIdAndThen(StringSetValueListener s) {
        DatabaseFactory
                .getAdaptedInstance()
                .getReference("appointments")
                .child(id)
                .child("participants")
                .addValueEventListener(s);
    }

    @Override
    public void getOwnerIdAndThen(StringValueListener s) {
        DatabaseFactory
                .getAdaptedInstance()
                .getReference("appointments")
                .child(id)
                .child("owner")
                .addValueEventListener(s);
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public boolean setStartTime(long startTime) {
        if (startTime < 0)
            return false;

        DatabaseFactory
                .getAdaptedInstance()
                .getReference("appointments")
                .child(id)
                .child("start")
                .setValue(startTime);
        return true;
    }

    @Override
    public boolean setDuration(long duration) {
        if (duration < 0 || duration > 3600 * 4)
            return false;

        DatabaseFactory
                .getAdaptedInstance()
                .getReference("appointments")
                .child(id)
                .child("duration")
                .setValue(duration);
        return true;
    }

    @Override
    public void setCourse(String course) {
        DatabaseFactory
                .getAdaptedInstance()
                .getReference("appointments")
                .child(id)
                .child("course")
                .setValue(course);
    }

    @Override
    public void setTitle(String title) {
        DatabaseFactory
                .getAdaptedInstance()
                .getReference("appointments")
                .child(id)
                .child("title")
                .setValue(title);
    }

    @Override
    public boolean addParticipant(User newParticipant) {
        DatabaseFactory
                .getAdaptedInstance()
                .getReference("appointments")
                .child(id)
                .child("participants")
                .child(newParticipant.getId())
                .setValue(true);
        return true;
    }

    @Override
    public boolean removeParticipant(User participant) {
        DatabaseFactory
                .getAdaptedInstance()
                .getReference("appointments")
                .child(id)
                .child("participants")
                .child(participant.getId())
                .setValue(null);
        return true;
    }

    @Override
    public void getBansAndThen(StringSetValueListener s) {
        DatabaseFactory
                .getAdaptedInstance()
                .getReference("appointments")
                .child(id)
                .child("banned")
                .addValueEventListener(s);
    }

    @Override
    public void getPrivateAndThen(BooleanValueListener b) {
        DatabaseFactory
                .getAdaptedInstance()
                .getReference("appointments")
                .child(id)
                .child("private")
                .addValueEventListener(b);
    }

    @Override
    public void setPrivate(boolean isPrivate) {
        DatabaseFactory
                .getAdaptedInstance()
                .getReference("appointments")
                .child(id)
                .child("private")
                .setValue(isPrivate);
    }

    @Override
    public boolean addBan(User banned) {
        DatabaseFactory
                .getAdaptedInstance()
                .getReference("appointments")
                .child(id)
                .child("banned")
                .child(banned.getId())
                .setValue(true);
        return true;
    }

    @Override
    public boolean removeBan(User unbanned) {
        DatabaseFactory.getAdaptedInstance().getReference("appointments").child(id).child("banned").child(unbanned.getId()).setValue(null);
        return true;
    }
}
