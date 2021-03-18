package io.github.polysmee.login;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import io.github.polysmee.interfaces.Appointment;
import io.github.polysmee.interfaces.User;

//todo : implement when database is working
public final class DatabaseUser implements User, Serializable {

    private final String self_id;

    public DatabaseUser(String id) {
        self_id = id;
    }

    @Override
    public String getId() {
        return self_id;
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
        return new HashSet<>();
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
        DatabaseUser that = (DatabaseUser) o;
        return self_id.equals(that.self_id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(self_id);
    }
}