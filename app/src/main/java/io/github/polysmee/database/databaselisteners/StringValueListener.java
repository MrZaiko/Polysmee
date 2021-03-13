package io.github.polysmee.database.databaselisteners;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public interface StringValueListener extends ValueEventListener {

    void onDone(String o);

    @Override
    default void onDataChange(@NonNull DataSnapshot snapshot) {
        onDone((String) snapshot.getValue());
    }

    @Override
    default void onCancelled(@NonNull DatabaseError error) {

    }
}
