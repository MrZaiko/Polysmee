package io.github.polysmee.database;

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
     * @param valueListener the listener to be added for changes to the user name.
     *          It is scheduled once when added, even if there is no change at that moment.
     */
    void getNameAndThen(StringValueListener valueListener);

    /**
     * @param valueListener the listener to be added for changes to the user name.
     *          It is scheduled only once.
     */
    void getName_Once_AndThen(StringValueListener valueListener);

    /**
     * @param valueListener the listener to be removed from listening to the user name
     */
    void removeNameListener(StringValueListener valueListener);

    /**
     * @param valueListener the listener to be added for changes to the appointments the user is part of.
     *          It is scheduled once when added, even if there is no change at that moment.
     */
    void getAppointmentsAndThen(StringSetValueListener valueListener);

    /**
     * @param valueListener the listener to be added for changes to the appointments the user is part of.
     *          It is scheduled only once.
     */
    void getAppointments_Once_AndThen(StringSetValueListener valueListener);

    /**
     * @param valueListener the listener to be removed from listening to the appointment list
     */
    void removeAppointmentsListener(StringSetValueListener valueListener);

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


    /**
     * @param start the start time of the new appointment
     * @param duration the duration of the new appointment
     * @param course the course name of the new appointment
     * @param name the name of the new appointment
     * @param isPrivate a boolean indication the privacy level of the new appointment
     * @return the ID of the freshly created appointment
     */
    String createNewUserAppointment(long start, long duration, String course, String name, boolean isPrivate);

    /**
     * @param valueListener a listener that will be run once that will receive the list of every user on the database
     */
    static void getAllUsersIdsAndThenOnce(StringSetValueListener valueListener){
        DatabaseFactory.getAdaptedInstance().getReference("users").addListenerForSingleValueEvent(valueListener);
    }
}
