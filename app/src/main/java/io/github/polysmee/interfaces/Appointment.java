package io.github.polysmee.interfaces;

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
    long getStartTime();

    /**
     * Retrieves the appointment's duration in milliseconds to stay consistent with the EPOCH
     * representation
     *
     * @return the appointment's duration
     */
    long getDuration();

    /**
     * Retrieves the appointment's course
     *
     * @return the appointment's course
     */
    String getCourse();

    /**
     * Retrieves the appointment's title
     *
     * @return the appointment's title
     */
    String getTitle();

    /**
     * Retrieves all users taking part in this appointment
     *
     * @return the appointment's participants in an unmodifiable set
     */
    Set<User> getParticipants();

    /**
     * Retrieves all users banned from this appointment
     *
     * @return the appointment's banned users in an unmodifiable set
     */
    Set<User> getBans();

    /**
     * Retrieves the appointment's owner
     *
     * @return the appointment's owner
     */
    User getOwner();

    /**
     * Says whether or not this is a private appointment
     *
     * @return true if the appointment is private
     */
    boolean isPrivate();

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
