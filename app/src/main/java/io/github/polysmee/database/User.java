package io.github.polysmee.database;

import io.github.polysmee.database.Appointment;
import io.github.polysmee.database.DatabaseFactory;
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

    void getNameAndThen(StringValueListener valueListener);

    void removeNameListener(StringValueListener valueListener);

    void getAppointmentsAndThen(StringSetValueListener valueListener);

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

    @Deprecated
    String createNewUserAppointment(long start, long duration, String course, String name);

    String createNewUserAppointment(long start, long duration, String course, String name, boolean isPrivate);

    static void getAllUsersIdsAndThenOnce(StringSetValueListener valueListener){
        DatabaseFactory.getAdaptedInstance().getReference("users").addListenerForSingleValueEvent(valueListener);
    }
}
