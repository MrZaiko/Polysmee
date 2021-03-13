package io.github.polysmee.database.databaselisteners;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public interface LongValueListener extends ValueEventListener {

    void onDone(long o);

    @Override
    default void onDataChange(@NonNull DataSnapshot snapshot) {
        onDone((Long) snapshot.getValue());
    }

    @Override
    default void onCancelled(@NonNull DatabaseError error) {

    }
}
