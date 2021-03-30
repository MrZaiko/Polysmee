package io.github.polysmee.interfaces;

import java.util.Set;

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

    default void getNameAndThen(StringValueListener valueListener) {}

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

    static void getAllUsersIdsAndThenOnce(StringSetValueListener valueListener){
        DatabaseFactory.getAdaptedInstance().getReference("users").addListenerForSingleValueEvent(valueListener);
    }
}
