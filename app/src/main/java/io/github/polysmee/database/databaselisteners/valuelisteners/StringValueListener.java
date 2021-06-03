package io.github.polysmee.database.databaselisteners.valuelisteners;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;

public interface StringValueListener extends ValueListener<String> {

    @Override
    default void onDataChange(@NonNull DataSnapshot snapshot) {
        String data = (String) snapshot.getValue();
        if (data != null)
            onDone(data);
        else
            onDone("");
    }
}
