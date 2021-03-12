package io.github.polysmee.database;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.Set;

import io.github.polysmee.interfaces.Appointment;

public interface AppointmentsValueListener extends ValueEventListener {

    void onDone(Set<Appointment> o);

    @Override
    default void onDataChange(@NonNull DataSnapshot snapshot) {
        onDone(stringToAppointments((String) snapshot.getValue()));
    }

    @Override
    default void onCancelled(@NonNull DatabaseError error) {

    }

    default Set<Appointment> stringToAppointments(String str) {
        throw new IllegalStateException("not implemented");
    }
}
