package io.github.polysmee.interfaces;

import java.io.Serializable;
import java.util.Set;

/**
 * A generic user
 */
public interface User{

    /**
     * Retrieves the user's id
     * @return user's id, not null
     */
    String getId();

    /**
     * Retrieves the user's name
     * @return user's name
     */
    String getName();

    /**
     * Retrieves the user's surname
     * @return user's surname
     */
    String getSurname();

    /**
     * Retrieves the set of the upcoming appointments for this user
     * @return user's set of appointment in an unmodifiable set
     */
    Set<Appointment> getAppointments();

    /**
     * Adds the given appointment to the set of appointments
     * @param newAppointment the appointment to be added
     */
    void addAppointment(Appointment newAppointment);

    /**
     * Removes the given appointment to the set of appointments
     * @param appointment the appointment to be removed
     */
    void removeAppointment(Appointment appointment);
}
