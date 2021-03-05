package io.github.polysmee.interfaces;

import java.util.Set;

/**
 * A generic appointment
 */
public interface Appointment {

    /**
     * Retrieves the starting time of the appointment in EPOCH representation
     * @return the appointment's start time
     */
    long getStartTime();

    /**
     * Retrieves the appointment's duration in seconds to stay consistent with the EPOCH
     * representation
     * @return the appointment's duration
     */
    long getDuration();

    /**
     * Retrieves the appointment's course
     * @return the appointment's course
     */
    String getCourse();

    /**
     * Retrieves the appointment's title
     * @return the appointment's title
     */
    String getTitle();

    /**
     * Retrieves all users taking part in this appointment
     * @return the appointment's participants in an unmodifiable set
     */
    Set<User> getParticipants();

    /**
     * Adds the given user to the set of participant
     * @param newParticipant the user to be added
     */
    void addParticipant(User newParticipant);

    /**
     * Removes the given user to the set of participant
     * @param participant the user to be removed
     */
    void removeParticipant(User participant);
}
