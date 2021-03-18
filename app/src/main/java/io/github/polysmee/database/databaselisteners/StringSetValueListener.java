package io.github.polysmee.database.databaselisteners;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import io.github.polysmee.interfaces.Appointment;

public interface StringSetValueListener extends ValueEventListener {

    void onDone(Set<String> o);

    @Override
    default void onDataChange(@NonNull DataSnapshot snapshot) {
        HashMap<String, Object> retrieved = (HashMap<String, Object>) snapshot.getValue();
        if(retrieved != null)
            onDone(retrieved.keySet());
        else
            onDone(new HashSet<>());
    }

    @Override
    default void onCancelled(@NonNull DatabaseError error) {

    }

}
