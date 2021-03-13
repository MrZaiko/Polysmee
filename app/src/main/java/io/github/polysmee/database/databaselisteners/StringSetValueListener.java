package io.github.polysmee.database.databaselisteners;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashSet;
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
        try {
            JSONObject reader = new JSONObject(str);
            Set<String> ids = new HashSet<>();
            for(int i = 0; i < reader.names().length(); ++i)
                ids.add((String) reader.names().get(i));
            return ids;
        } catch (JSONException e) {
            return new HashSet<>();
        }

    }
}
