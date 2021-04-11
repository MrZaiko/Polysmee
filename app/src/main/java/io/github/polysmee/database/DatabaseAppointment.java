package io.github.polysmee.database;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import io.github.polysmee.database.databaselisteners.BooleanValueListener;
import io.github.polysmee.database.databaselisteners.LongValueListener;
import io.github.polysmee.database.databaselisteners.StringSetValueListener;
import io.github.polysmee.database.databaselisteners.StringValueListener;

public class DatabaseAppointment implements Appointment {

    private final String id;

    public DatabaseAppointment(String id) {
        this.id = id;
    }

    public static void getAllPublicAppointmentsOnce(StringSetValueListener ssv) {

        DatabaseFactory
                .getAdaptedInstance()
                .getReference("appointments")
                .get().addOnSuccessListener(dataSnapshot -> {
                    if(dataSnapshot.getValue() != null) {
                        Set<String> appos = new HashSet<>();
                        HashMap<String, Object> hash = (HashMap<String, Object>) dataSnapshot.getValue();
                        for (Map.Entry<String, Object> entry : hash.entrySet()) {
                            if(!((Boolean) ((HashMap<String, Object>) entry.getValue()).get("private"))){
                                appos.add(entry.getKey());
                            }
                        }
                        ssv.onDone(appos);
                    }

                });
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
    public void removeStartListener(LongValueListener l) {
        DatabaseFactory
                .getAdaptedInstance()
                .getReference("appointments")
                .child(id)
                .child("start")
                .removeEventListener(l);
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
    public void removeDurationListener(LongValueListener l) {
        DatabaseFactory
                .getAdaptedInstance()
                .getReference("appointments")
                .child(id)
                .child("duration")
                .removeEventListener(l);
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
    public void removeCourseListener(StringValueListener l) {
        DatabaseFactory
                .getAdaptedInstance()
                .getReference("appointments")
                .child(id)
                .child("course")
                .removeEventListener(l);
    }

    @Override
    public void getTitleAndThen(StringValueListener s) {
        DatabaseFactory
                .getAdaptedInstance()
                .getReference("appointments")
                .child(id)
                .child("title")
                .addValueEventListener(s);
    }

    @Override
    public void removeTitleListener(StringValueListener l) {
        DatabaseFactory
                .getAdaptedInstance()
                .getReference("appointments")
                .child(id)
                .child("title")
                .removeEventListener(l);
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
    public void removeParticipantsListener(StringSetValueListener s) {
        DatabaseFactory
                .getAdaptedInstance()
                .getReference("appointments")
                .child(id)
                .child("participants")
                .removeEventListener(s);
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
    public void removeOwnerListener(StringValueListener s) {
        DatabaseFactory
                .getAdaptedInstance()
                .getReference("appointments")
                .child(id)
                .child("owner")
                .removeEventListener(s);
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
        if (duration < 0 || duration > 3600000 * 4)
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
    public void removeBansListener(StringSetValueListener s) {
        DatabaseFactory
                .getAdaptedInstance()
                .getReference("appointments")
                .child(id)
                .child("banned")
                .removeEventListener(s);
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
    public void removePrivateListener(BooleanValueListener bool) {
        DatabaseFactory
                .getAdaptedInstance()
                .getReference("appointments")
                .child(id)
                .child("private")
                .removeEventListener(bool);
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
        DatabaseFactory
                .getAdaptedInstance()
                .getReference("appointments")
                .child(id)
                .child("banned")
                .child(unbanned.getId())
                .setValue(null);
        return true;
    }
}
