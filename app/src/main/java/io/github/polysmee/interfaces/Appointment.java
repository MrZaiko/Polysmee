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
     * Retrieves the appointment's owner
     *
     * @return the appointment's owner
     */
    User getOwner();

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
