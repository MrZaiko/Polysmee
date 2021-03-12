package io.github.polysmee.interfaces;

import java.io.Serializable;
import java.util.Set;

/**
 * A generic appointment
 */
public interface Appointment {

    /**
     * Retrieves the starting time of the appointment in EPOCH representation (milliseconds)
     *
     * @return the appointment's start time
     */
    @Deprecated
    long getStartTime();

    /**
     * Retrieves the appointment's duration in milliseconds to stay consistent with the EPOCH
     * representation
     *
     * @return the appointment's duration
     */
    @Deprecated
    long getDuration();

    default String getId(){return null;}

    /**
     * Retrieves the appointment's course
     *
     * @return the appointment's course
     */
    @Deprecated
    String getCourse();

    /**
     * Retrieves the appointment's title
     *
     * @return the appointment's title
     */
    @Deprecated
    String getTitle();

    /**
     * Retrieves all users taking part in this appointment
     *
     * @return the appointment's participants in an unmodifiable set
     */
    @Deprecated
    Set<User> getParticipants();

    /**
     * Retrieves the appointment's owner
     *
     * @return the appointment's owner
     */
    @Deprecated
    User getOwner();

    /**
     * Sets the appointment's start time
     *
     * @param startTime the new start time
     * @return true if the time was set, false if it had an incorrect value (<0);
     */
    boolean setStartTime(long startTime);

    /**
     * Sets the appointment's duration, which cannot be longer than 4 hours
     *
     * @param duration the new duration
     * @return true if the time was set, false if it had an incorrect value (<0 or more than 4 hours);
     */
    boolean setDuration(long duration);

    /**
     * Sets the appointment's course
     *
     * @param course the new course
     */
    void setCourse(String course);

    /**
     * Sets the appointment's title
     *
     * @param title the new title
     */
    void setTitle(String title);

    /**
     * Adds the given user to the set of participant
     *
     * @param newParticipant the user to be added
     * @return true if the participant was successfully added
     */
    boolean addParticipant(User newParticipant);

    /**
     * Removes the given user to the set of participant
     * Cannot remove the owner
     *
     * @param participant the user to be removed
     * @return true if the participant was successfully removed
     */
    boolean removeParticipant(User participant);
}
