package io.github.polysmee.database.databaselisteners.valuelisteners;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public interface StringSetValueListener extends ValueListener<Set<String>> {

    @Override
    default void onDataChange(@NonNull DataSnapshot snapshot) {
        HashMap<String, Object> retrieved = (HashMap<String, Object>) snapshot.getValue();
        if (retrieved != null)
            onDone(retrieved.keySet());
        else
            onDone(new HashSet<>());
    }

}
