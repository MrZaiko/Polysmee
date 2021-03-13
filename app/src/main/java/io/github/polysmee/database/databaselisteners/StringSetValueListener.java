package io.github.polysmee.database.databaselisteners;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.Set;

import io.github.polysmee.interfaces.Appointment;

public interface StringSetValueListener extends ValueEventListener {

    void onDone(Set<String> o);

    @Override
    default void onDataChange(@NonNull DataSnapshot snapshot) {
        onDone(stringToAppointments((String) snapshot.getValue()));
    }

    @Override
    default void onCancelled(@NonNull DatabaseError error) {

    }

    default Set<String> stringToAppointments(String str) {
        throw new IllegalStateException("not implemented");
    }
}
