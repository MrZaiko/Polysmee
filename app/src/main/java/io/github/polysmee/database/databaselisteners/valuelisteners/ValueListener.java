package io.github.polysmee.database.databaselisteners.valuelisteners;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public interface ValueListener<T> extends ValueEventListener {

    void onDone(T o);

    @Override
    default void onDataChange(@NonNull DataSnapshot snapshot) {
        T value = (T) snapshot.getValue();
        if (value != null)
            onDone(value);
    }

    @Override
    default void onCancelled(@NonNull DatabaseError error) {

    }

}