package io.github.polysmee.database.decoys;

import java.util.HashSet;

import java.util.Objects;

import java.util.Set;

import io.github.polysmee.database.databaselisteners.BooleanValueListener;
import io.github.polysmee.database.databaselisteners.LongValueListener;
import io.github.polysmee.database.databaselisteners.StringSetValueListener;
import io.github.polysmee.database.databaselisteners.StringValueListener;
import io.github.polysmee.database.Appointment;
import io.github.polysmee.database.User;

public class FakeDatabaseAppointment implements Appointment {

    public final String id;
    private final TestAppointmentInfo appointmentInfo;

    public FakeDatabaseAppointment(String id){
        this.id = id;
        this.appointmentInfo = FakeDatabase.appId2App.get(id);
    }

    @Override
    public void getStartTimeAndThen(LongValueListener l) {

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
    public void getCourseAndThen(StringValueListener s) {
        s.onDone(appointmentInfo.course);
    }

    @Override
    public void getTitleAndThen(StringValueListener s) {
        s.onDone(appointmentInfo.name);
    }

    @Override
    public void getParticipantsIdAndThen(StringSetValueListener s) {

        Set<String> hashed = new HashSet<>();
        hashed.add(appointmentInfo.owner.getId());
        s.onDone(hashed);

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
    public void getBansAndThen(StringSetValueListener s) {

    }

    @Override
    public void getPrivateAndThen(BooleanValueListener bool) {

    }

    @Override
    public void setPrivate(boolean isPrivate) {

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
        FakeDatabaseAppointment that = (FakeDatabaseAppointment) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
