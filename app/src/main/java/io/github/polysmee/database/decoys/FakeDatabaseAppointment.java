package io.github.polysmee.database.decoys;

import androidx.annotation.Nullable;

import java.util.HashSet;
import java.util.Set;

import io.github.polysmee.database.databaselisteners.LongValueListener;
import io.github.polysmee.database.databaselisteners.StringSetValueListener;
import io.github.polysmee.database.databaselisteners.StringValueListener;
import io.github.polysmee.interfaces.Appointment;
import io.github.polysmee.interfaces.User;

public class FakeDatabaseAppointment implements Appointment {

    public final String id;
    private final TestAppointmentInfo appointmentInfo;

    public FakeDatabaseAppointment(String id){
        this.id = id;
        this.appointmentInfo = FakeDatabase.appId2App.get(id);
    }
    @Override
    public long getStartTime() {
        return 0;
    }

    @Override
    public void getStartTimeAndThen(LongValueListener l) {
        l.onDone(appointmentInfo.start);
    }

    @Override
    public long getDuration() {
        return 0;
    }

    @Override
    public void getDurationAndThen(LongValueListener l) {
        l.onDone(appointmentInfo.duration);
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getCourse() {
        return null;
    }

    @Override
    public void getCourseAndThen(StringValueListener s) {
        s.onDone(appointmentInfo.course);
    }

    @Override
    public String getTitle() {
        return null;
    }

    @Override
    public void getTitleAndThen(StringValueListener s) {
        s.onDone(appointmentInfo.name);
    }

    @Override
    public Set<User> getParticipants() {
        return null;
    }

    @Override
    public void getParticipantsIdAndThen(StringSetValueListener s) {
        Set<String> ids = new HashSet<>();
        s.onDone(ids);
    }

    @Override
    public User getOwner() {
        return null;
    }

    @Override
    public void getOwnerIdAndThen(StringValueListener s) {
        s.onDone(appointmentInfo.owner.getId());
    }

    @Override
    public boolean setStartTime(long startTime) {
        appointmentInfo.start = startTime;
        return true;
    }

    @Override
    public boolean setDuration(long duration) {
        appointmentInfo.duration = duration;
        return true;
    }

    @Override
    public void setCourse(String course) {
        appointmentInfo.course = course;
    }

    @Override
    public void setTitle(String title) {
        appointmentInfo.name = title;
    }

    @Override
    public boolean addParticipant(User newParticipant) {
        return false;
    }

    @Override
    public boolean removeParticipant(User participant) {
        return false;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return super.equals(obj);
    }
}
