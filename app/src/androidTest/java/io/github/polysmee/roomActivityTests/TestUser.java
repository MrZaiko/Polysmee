package io.github.polysmee.roomActivityTests;

import java.io.Serializable;
import java.util.Objects;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TestUser testUser = (TestUser) o;
        return Objects.equals(id, testUser.id) &&
                Objects.equals(name, testUser.name) &&
                Objects.equals(surname, testUser.surname) &&
                Objects.equals(appointments, testUser.appointments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, surname, appointments);
    }
}
