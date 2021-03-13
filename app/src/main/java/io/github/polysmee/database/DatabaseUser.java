package io.github.polysmee.database;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import io.github.polysmee.database.databaselisteners.StringSetValueListener;
import io.github.polysmee.database.databaselisteners.StringValueListener;
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
        return "YOU USED GETNAME";
    }

    @Override
    public String getSurname() {
        return "YOU USED GETSURNAME";
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
    public void getAppointmentsAndThen(StringSetValueListener valueListener) {
        FirebaseDatabase.getInstance().getReference("users").child(self_id).child("appointments").addValueEventListener(valueListener);
    }

    @Override
    public String createNewUserAppointment() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("appointments").push();
        Map<String, Object> newAppo = new HashMap<>();
        newAppo.put("owner", self_id);
        newAppo.put("id", ref.getKey());
        newAppo.put("members", new HashMap<String, Boolean>().put("owner", true));
        ref.setValue(true);
        addAppointment(new DatabaseAppointment(ref.getKey()));
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
