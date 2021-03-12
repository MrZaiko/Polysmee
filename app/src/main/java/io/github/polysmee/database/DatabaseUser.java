package io.github.polysmee.database;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import io.github.polysmee.interfaces.Appointment;
import io.github.polysmee.interfaces.User;

public final class DatabaseUser implements User {

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
        FirebaseDatabase.getInstance().getReference("users").child(self_id).child("appointments").child(newAppointment.getId()).setValue(true);
    }

    @Override
    public void removeAppointment(Appointment appointment) {
        FirebaseDatabase.getInstance().getReference("users").child(self_id).child("appointments").child(appointment.getId()).setValue(null);
    }

    @Override
    public void getNameAndThen(StringValueListener valueListener) {
        FirebaseDatabase.getInstance().getReference("users").child(self_id).child("name").addValueEventListener(valueListener);
    }

    @Override
    public void getAppointmentsAndThen(AppointmentsValueListener valueListener) {
        FirebaseDatabase.getInstance().getReference("users").child(self_id).child("appointments").addValueEventListener(valueListener);
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
