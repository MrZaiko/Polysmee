package io.github.polysmee.appointments;

import java.io.Serializable;
import java.util.Objects;
import java.util.Set;

import io.github.polysmee.interfaces.Appointment;
import io.github.polysmee.interfaces.User;

public class TestUser implements User, Serializable {
    private final String surname;
    String name;

    public TestUser(String name, String surname) {
        this.name = name;
        this.surname = surname;
    }

    public TestUser(String name) {
        this.name = name;
        this.surname = "default-man";
    }

    @Override
    public String getId() {
        return null;
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
        return null;
    }

    @Override
    public void addAppointment(Appointment newAppointment) {

    }

    @Override
    public void removeAppointment(Appointment appointment) {

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TestUser testUser = (TestUser) o;
        return Objects.equals(getName(), testUser.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }
}
