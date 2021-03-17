package io.github.polysmee.calendar;

import io.github.polysmee.interfaces.User;

public class CalendarAppointmentInfo {

    private String course;
    private String title;
    private long startTime;
    private long duration;
    private final String id;
    private final User owner;
    private int index;
    public CalendarAppointmentInfo(String course, String title, long startTime, long duration, String id, User owner, int index){
        this.course = new String(course);
        this.title = new String(title);
        this.startTime = startTime;
        this.duration = duration;
        this.id = new String(id);
        this.owner = owner;
        this.index = index;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getDuration() {
        return duration;
    }

    public long getStartTime() {
        return startTime;
    }

    public String getCourse() {
        return course;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public User getOwner() {
        return owner;
    }
}
