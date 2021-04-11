package io.github.polysmee.database;

import io.github.polysmee.database.databaselisteners.BooleanValueListener;
import io.github.polysmee.database.databaselisteners.LongValueListener;
import io.github.polysmee.database.databaselisteners.StringSetValueListener;
import io.github.polysmee.database.databaselisteners.StringValueListener;

/**
 * A generic appointment
 */
public interface Appointment {

    void getStartTimeAndThen(LongValueListener l);

    void removeStartListener(LongValueListener l);

    void getDurationAndThen(LongValueListener l);

    void removeDurationListener(LongValueListener l);

    String getId();

    void getCourseAndThen(StringValueListener s);

    void removeCourseListener(StringValueListener l);

    void getTitleAndThen(StringValueListener s);

    void removeTitleListener(StringValueListener l);

    void getParticipantsIdAndThen(StringSetValueListener s);

    void removeParticipantsListener(StringSetValueListener s);

    void getOwnerIdAndThen(StringValueListener s);

    void removeOwnerListener(StringValueListener s);

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

    void getBansAndThen(StringSetValueListener s);

    void removeBansListener(StringSetValueListener s);

    void getPrivateAndThen(BooleanValueListener bool);

    void removePrivateListener(BooleanValueListener bool);

    void setPrivate(boolean isPrivate);

    /**
     * Adds the given user to the set of banned users
     * Cannot ban the owner
     *
     * @param banned the user to be banned
     * @return true if the user was successfully banned
     */
    boolean addBan(User banned);

    /**
     * Removes the given user from the set of banned users
     *
     * @param unbanned the user to be unbanned
     * @return true if the user was successfully unbanned
     */
    boolean removeBan(User unbanned);
}
