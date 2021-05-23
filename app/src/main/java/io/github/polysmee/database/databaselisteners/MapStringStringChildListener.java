package io.github.polysmee.database.databaselisteners;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

public interface MapStringStringChildListener extends ChildEventListener {
    //TODO add documentation

    void childAdded(String key, @Nullable String value);

    void childChanged(String key, @Nullable String value);

    void childRemoved(String key,@Nullable String value);
    void childMoved(String key , @Nullable String value );

    @Override
    default void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
        String key = snapshot.getKey();
        String data = (String) snapshot.getValue();
        childAdded(key, data);
    }

    @Override
    default void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
        String key = snapshot.getKey();
        String data = (String) snapshot.getValue();
        childChanged(key, data);
    }

    @Override
    default void onChildRemoved(@NonNull DataSnapshot snapshot) {
        String key = snapshot.getKey();
        String data = (String) snapshot.getValue();
        childRemoved(key, data);
    }

    @Override
    default void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
        String key = snapshot.getKey();
        String data = (String) snapshot.getValue();
        childMoved(key, data);
    }

    @Override
    default void onCancelled(@NonNull DatabaseError error) {}
}
