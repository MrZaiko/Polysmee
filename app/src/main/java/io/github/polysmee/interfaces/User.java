package io.github.polysmee.interfaces;

import java.util.Set;

import io.github.polysmee.database.databaselisteners.StringSetValueListener;
import io.github.polysmee.database.databaselisteners.StringValueListener;

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
    @Deprecated
    String getName();

    default void getNameAndThen(StringValueListener valueListener) {}

    /**
     * Retrieves the user's surname
     * @return user's surname
     */
    @Deprecated
    String getSurname();

    /**
     * Retrieves the set of the upcoming appointments for this user
     * @return user's set of appointment in an unmodifiable set
     */
    @Deprecated
    Set<Appointment> getAppointments();

    default void getAppointmentsAndThen(StringSetValueListener valueListener) {}

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

    default String createNewUserAppointment(long start, long duration, String course, String name){return null;}
}
