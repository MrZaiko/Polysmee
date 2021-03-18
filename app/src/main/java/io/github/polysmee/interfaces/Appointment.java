package io.github.polysmee.interfaces;

import java.util.HashSet;
import java.util.Set;

import io.github.polysmee.database.databaselisteners.LongValueListener;
import io.github.polysmee.database.databaselisteners.StringSetValueListener;
import io.github.polysmee.database.databaselisteners.StringValueListener;

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

    default void getStartTimeAndThen(LongValueListener l) {}

    /**
     * Retrieves the appointment's duration in milliseconds to stay consistent with the EPOCH
     * representation
     *
     * @return the appointment's duration
     */
    @Deprecated
    long getDuration();

    default void getDurationAndThen(LongValueListener l) {}


    default String getId(){return null;}

    /**
     * Retrieves the appointment's course
     *
     * @return the appointment's course
     */
    @Deprecated
    String getCourse();

    default void getCourseAndThen(StringValueListener s) {}

    /**
     * Retrieves the appointment's title
     *
     * @return the appointment's title
     */
    @Deprecated
    String getTitle();

    default void getTitleAndThen(StringValueListener s) {}

    /**
     * Retrieves all users taking part in this appointment
     *
     * @return the appointment's participants in an unmodifiable set
     */
    @Deprecated
    Set<User> getParticipants();

    default void getParticipantsIdAndThen(StringSetValueListener s) {}


    /**
     * Retrieves the appointment's owner
     *
     * @return the appointment's owner
     */
    @Deprecated
    User getOwner();

    default void getOwnerIdAndThen(StringValueListener s) {}

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

    /**
     * Retrieves all users banned from this appointment
     *
     * @return the appointment's banned users in an unmodifiable set
     */
    default Set<User> getBans() {
        return new HashSet<>();
    }


    /**
     * Says whether or not this is a private appointment
     *
     * @return true if the appointment is private
     */
    default boolean isPrivate() {
        return false;
    }

    /**
     * Adds the given user to the set of banned users
     * Cannot ban the owner
     *
     * @param banned the user to be banned
     * @return true if the user was successfully banned
     */
    default boolean addBan(User banned) {
        return false;
    }

    /**
     * Removes the given user from the set of banned users
     *
     * @param unbanned the user to be unbanned
     * @return true if the user was successfully unbanned
     */
    default boolean removeBan(User unbanned) {
        return false;
    }
}
