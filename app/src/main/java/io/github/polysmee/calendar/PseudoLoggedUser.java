package io.github.polysmee.calendar;

import java.util.HashSet;
import java.util.Set;

import io.github.polysmee.interfaces.Appointment;
import io.github.polysmee.interfaces.User;


//USELESS CLASS; JUST CREATED TO USE THE SINGLETON, REPLACE WITH DATABASE USER SINGLETON
public class PseudoLoggedUser  implements User {

    private String id;
    private Set<Appointment> appointments;
    private static User user = null;

    public static User getSingletonPseudoUser(String id){
        if(user == null)
            user = new PseudoLoggedUser(id);
        return user;
    }
    private PseudoLoggedUser(String id){
        this.id = id;
        appointments = new HashSet<>();
    }
    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return "";
    }

    @Override
    public String getSurname() {
        return "";
    }

    @Override
    public Set<Appointment> getAppointments() {
        return appointments;
    }

    @Override
    public void addAppointment(Appointment newAppointment) {
        appointments.add(newAppointment);
    }

    @Override
    public void removeAppointment(Appointment appointment) {
        appointments.remove(appointment);
    }
}
