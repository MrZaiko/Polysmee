package io.github.polysmee.interfaces;

import java.util.List;

/**
 * A generic user
 */
public interface User {

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
     * Retrieves the list of the upcoming appointments for this user
     * @return user's list of appointment in an unmodifiable list
     */
    List<Appointment> getAppointments();

    /**
     * Adds the given appointment to the list of appointments
     * @param newAppointment the appointment to be added
     */
    void addAppointment(Appointment newAppointment);

    /**
     * Removes the given appointment to the list of appointments
     * @param appointment the appointment to be removed
     */
    void removeAppointment(Appointment appointment);
}
