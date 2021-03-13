package io.github.polysmee.database;

import com.google.firebase.database.FirebaseDatabase;

import java.util.Set;

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
        FirebaseDatabase.getInstance().getReference("appointments").child(id).child("start_time").addValueEventListener(l);
    }

    @Override
    public void getDurationAndThen(LongValueListener l) {
        FirebaseDatabase.getInstance().getReference("appointments").child(id).child("duration").addValueEventListener(l);
    }

    @Override
    public void getCourseAndThen(StringValueListener s) {
        FirebaseDatabase.getInstance().getReference("appointments").child(id).child("course").addValueEventListener(s);
    }

    @Override
    public void getTitleAndThen(StringValueListener s) {
        FirebaseDatabase.getInstance().getReference("appointments").child(id).child("title").addValueEventListener(s);
    }

    @Override
    public void getParticipantsIdAndThen(StringSetValueListener s) {
        FirebaseDatabase.getInstance().getReference("appointments").child(id).child("participants").addValueEventListener(s);
    }

    @Override
    public void getOwnerIdAndThen(StringValueListener s) {
        FirebaseDatabase.getInstance().getReference("appointments").child(id).child("owner").addValueEventListener(s);
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
        if(startTime < 0)
            return false;

        FirebaseDatabase.getInstance().getReference("appointments").child(id).child("start_time").setValue(startTime);
        return true;
    }

    @Override
    public boolean setDuration(long duration) {
        if(duration < 0 || duration > 3600*4)
            return false;

        FirebaseDatabase.getInstance().getReference("appointments").child(id).child("duration").setValue(duration);
        return true;
    }

    @Override
    public void setCourse(String course) {
        FirebaseDatabase.getInstance().getReference("appointments").child(id).child("course").setValue(course);
    }

    @Override
    public void setTitle(String title) {
        FirebaseDatabase.getInstance().getReference("appointments").child(id).child("title").setValue(title);
    }

    @Override
    public boolean addParticipant(User newParticipant) {
        FirebaseDatabase.getInstance().getReference("appointments").child(id).child("participants").child(newParticipant.getId()).setValue(true);
        return true;
    }

    @Override
    public boolean removeParticipant(User participant) {
        FirebaseDatabase.getInstance().getReference("appointments").child(id).child("participants").child(participant.getId()).setValue(null);
        return true;
    }
}
