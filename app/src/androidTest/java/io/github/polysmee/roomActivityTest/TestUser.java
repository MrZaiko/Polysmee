package io.github.polysmee.roomActivityTest;

import java.io.Serializable;
import java.util.Set;

import io.github.polysmee.interfaces.Appointment;
import io.github.polysmee.interfaces.User;

public class TestUser implements User, Serializable {
    String id;
    String name;
    String surname;
    Set<Appointment> appointments;

    public TestUser(String id, String name, String surname, Set<Appointment> appointments) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.appointments = appointments;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getSurname() {
        return surname;
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
