package io.github.polysmee.calendar;

import androidx.annotation.Nullable;

import io.github.polysmee.database.User;


/**
 * This class serves as a way to keep an appointment's information
 * to display them on the calendar without having to use callbacks all the time.
 */
public class CalendarAppointmentInfo {

    private String course;
    private String title;
    private long startTime;
    private long duration;
    private final String id;
    public CalendarAppointmentInfo(String course, String title, long startTime, long duration, String id){
        this.course = course;
        this.title = title;
        this.startTime = startTime;
        this.duration = duration;
        this.id = id;
    }

    /**
     * Sets the appointment's description's course
     * @param course the course we chose to set
     */
    public void setCourse(String course) {
        this.course = course;
    }

    /**
     * Sets the appointment's description's duration
     * @param duration the duration we chose to set
     */
    public void setDuration(long duration) {
        this.duration = duration;
    }

    /**
     * Sets the appointment's description start time
     * @param startTime the startime we chose to set
     */
    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    /**
     * Sets the appointment's description title
     * @param title the title we chose to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Gets the appointment's stored duration
     * @return the appointment's duration
     */
    public long getDuration() {
        return duration;
    }
    /**
     * Gets the appointment's stored start time
     * @return the appointment's start time
     */
    public long getStartTime() {
        return startTime;
    }
    /**
     * Gets the appointment's stored course
     * @return the appointment's course
     */
    public String getCourse() {
        return course;
    }
    /**
     * Gets the appointment's stored id
     * @return the appointment's id
     */
    public String getId() {
        return id;
    }
    /**
     * Gets the appointment's stored title
     * @return the appointment's title
     */
    public String getTitle() {
        return title;
    }


}
