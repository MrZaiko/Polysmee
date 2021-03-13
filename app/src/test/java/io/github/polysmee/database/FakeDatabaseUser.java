package io.github.polysmee.database;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.github.polysmee.database.databaselisteners.StringSetValueListener;
import io.github.polysmee.database.databaselisteners.StringValueListener;
import io.github.polysmee.interfaces.Appointment;
import io.github.polysmee.interfaces.User;

public class FakeDatabaseUser implements User {

    private final String id;
    private final String name;
    private final Set<String> appointments;
    private final List<StringSetValueListener> appListeners;
    private final List<StringValueListener> nameListeners;


    public FakeDatabaseUser(String id, String name, FakeDatabase db) {
        this.id = id;
        this.name = name;
        this.appointments = new HashSet<>();
        this.appListeners = new ArrayList<>();
        this.nameListeners = new ArrayList<>();
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
    public void getNameAndThen(StringValueListener valueListener) {
        valueListener.onDone(name);
    }

    @Override
    public String getSurname() {
        throw new IllegalStateException("surname doesnt work");
    }

    @Override
    public Set<Appointment> getAppointments() {
        return null;
    }

    @Override
    public void getAppointmentsAndThen(StringSetValueListener valueListener) {

    }

    @Override
    public void addAppointment(Appointment newAppointment) {

    }

    @Override
    public void removeAppointment(Appointment appointment) {

    }

    @Override
    public String createNewUserAppointment(long start, long duration, String course, String name) {
        return null;
    }
}
