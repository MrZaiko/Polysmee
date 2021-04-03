package io.github.polysmee.database.databaselisteners;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public interface BooleanValueListener extends ValueEventListener {

    void onDone(boolean o);

    @Override
    default void onDataChange(@NonNull DataSnapshot snapshot) {
        Boolean value = (Boolean) snapshot.getValue();
        if(value != null)
            onDone(value);
    }

    @Override
    default void onCancelled(@NonNull DatabaseError error) {

    }

}
