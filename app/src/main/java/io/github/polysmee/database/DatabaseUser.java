package io.github.polysmee.database;

import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import io.github.polysmee.database.databaselisteners.StringSetValueListener;
import io.github.polysmee.database.databaselisteners.StringValueListener;

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
    public void addAppointment(Appointment appointment) {
        DatabaseFactory.getAdaptedInstance()
                .getReference("users")
                .child(self_id)
                .child("appointments")
                .child(appointment.getId())
                .setValue(true);
    }

    @Override
    public void removeAppointment(Appointment appointment) {
        DatabaseFactory
                .getAdaptedInstance()
                .getReference("users")
                .child(self_id)
                .child("appointments")
                .child(appointment.getId())
                .setValue(null);
    }

    @Override
    public void getNameAndThen(StringValueListener valueListener) {
        DatabaseFactory
                .getAdaptedInstance()
                .getReference("users")
                .child(self_id)
                .child("name")
                .addValueEventListener(valueListener);
    }

    @Override
    public void getName_Once_AndThen(StringValueListener valueListener) {
        DatabaseFactory
                .getAdaptedInstance()
                .getReference("users")
                .child(self_id)
                .child("name")
                .addListenerForSingleValueEvent(valueListener);
    }

    @Override
    public void removeNameListener(StringValueListener valueListener) {
        DatabaseFactory
                .getAdaptedInstance()
                .getReference("users")
                .child(self_id)
                .child("name")
                .removeEventListener(valueListener);
    }

    @Override
    public void getAppointmentsAndThen(StringSetValueListener valueListener) {
        DatabaseFactory
                .getAdaptedInstance()
                .getReference("users")
                .child(self_id)
                .child("appointments")
                .addValueEventListener(valueListener);
    }

    @Override
    public void getAppointments_Once_AndThen(StringSetValueListener valueListener) {
        DatabaseFactory
                .getAdaptedInstance()
                .getReference("users")
                .child(self_id)
                .child("appointments")
                .addListenerForSingleValueEvent(valueListener);
    }

    @Override
    public void removeAppointmentsListener(StringSetValueListener valueListener) {
        DatabaseFactory
                .getAdaptedInstance()
                .getReference("users")
                .child(self_id)
                .child("appointments")
                .removeEventListener(valueListener);
    }

    @Override
    public String createNewUserAppointment(long start, long duration, String course, String name, boolean isPrivate) {
        DatabaseReference ref = DatabaseFactory.getAdaptedInstance().getReference("appointments").push();

        Map<String, Object> newAppo = new HashMap<>();
        newAppo.put("owner", self_id);
        newAppo.put("id", ref.getKey());
        newAppo.put("members", new HashMap<String, Boolean>().put(self_id, true));
        newAppo.put("start", start);
        newAppo.put("duration", duration);
        newAppo.put("course", course);
        newAppo.put("title", name);
        newAppo.put("private", isPrivate);
        ref.setValue(newAppo);

        Appointment appointment = new DatabaseAppointment(ref.getKey());
        this.addAppointment(appointment);
        appointment.addParticipant(new DatabaseUser(self_id));
        return ref.getKey();
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
