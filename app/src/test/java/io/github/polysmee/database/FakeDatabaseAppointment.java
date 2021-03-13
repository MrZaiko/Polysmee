package io.github.polysmee.database;

import androidx.annotation.Nullable;

import java.util.Set;

import io.github.polysmee.database.databaselisteners.LongValueListener;
import io.github.polysmee.database.databaselisteners.StringSetValueListener;
import io.github.polysmee.database.databaselisteners.StringValueListener;
import io.github.polysmee.interfaces.Appointment;
import io.github.polysmee.interfaces.User;

public class FakeDatabaseAppointment implements Appointment {

    public final String id;
    public final String name;
    public final String course;
    public final long start;
    public final long duration;

    public FakeDatabaseAppointment(String id, String name, String course, long start, long duration){
        this.id = id;
        this.name = name;
        this.course = course;
        this.start = start;
        this.duration = duration;
    }
    @Override
    public long getStartTime() {
        return 0;
    }

    @Override
    public void getStartTimeAndThen(LongValueListener l) {

    }

    @Override
    public long getDuration() {
        return 0;
    }

    @Override
    public void getDurationAndThen(LongValueListener l) {

    }

    @Override
    public String getId() {
        return null;
    }

    @Override
    public String getCourse() {
        return null;
    }

    @Override
    public void getCourseAndThen(StringValueListener s) {

    }

    @Override
    public String getTitle() {
        return null;
    }

    @Override
    public void getTitleAndThen(StringValueListener s) {

    }

    @Override
    public Set<User> getParticipants() {
        return null;
    }

    @Override
    public void getParticipantsIdAndThen(StringSetValueListener s) {

    }

    @Override
    public User getOwner() {
        return null;
    }

    @Override
    public void getOwnerIdAndThen(StringValueListener s) {

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

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return super.equals(obj);
    }
}
