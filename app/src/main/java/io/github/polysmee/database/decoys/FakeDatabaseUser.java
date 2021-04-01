package io.github.polysmee.database.decoys;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import io.github.polysmee.database.databaselisteners.StringSetValueListener;
import io.github.polysmee.database.databaselisteners.StringValueListener;
import io.github.polysmee.interfaces.Appointment;
import io.github.polysmee.interfaces.User;

public class FakeDatabaseUser implements User {

    public String id;
    public String name;
    public Set<Appointment> appointments;
    private static User user = null;

    public static User getInstance(){

        user = new FakeDatabaseUser("TestId","TestName");
        return user;
    }

    public FakeDatabaseUser(String id, String name) {
        this.id = id;
        this.name = name;
        this.appointments = new HashSet<>();
    }

    @Override
    public String getId() {
        return id;
    }


    @Override
    public void getNameAndThen(StringValueListener valueListener) {
        valueListener.onDone(name);
    }


    @Override
    public void getAppointmentsAndThen(StringSetValueListener valueListener) {
        Set<String> res = new HashSet<>();
        for(Appointment elem : appointments)
            res.add(elem.getId());
        valueListener.onDone(res);
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
    public String createNewUserAppointment(long start, long duration, String course, String name) {
        long id = FakeDatabase.idGenerator.incrementAndGet();
        addAppointment(new FakeDatabaseAppointment("" + id));
        FakeDatabase.appId2App.put("" + id, new TestAppointmentInfo(name, course, start, duration, this));
        return "" + id;
    }

    @Override
    public String createNewUserAppointment(long start, long duration, String course, String name, boolean isPrivate) {
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FakeDatabaseUser that = (FakeDatabaseUser) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
