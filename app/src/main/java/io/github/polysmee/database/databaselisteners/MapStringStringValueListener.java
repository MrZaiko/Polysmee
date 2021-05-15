package io.github.polysmee.database.databaselisteners;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public interface MapStringStringValueListener extends ValueEventListener {
    void onDone(Map<String, String> o);

    @Override
    default void onDataChange(@NonNull DataSnapshot snapshot) {
        HashMap<String, String> retrieved = (HashMap<String, String>) snapshot.getValue();
        if (retrieved != null)
            onDone(retrieved);
    }

    @Override
    default void onCancelled(@NonNull DatabaseError error) {
    }
}
