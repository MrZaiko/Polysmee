package io.github.polysmee.database.databaselisteners;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

public interface BooleanChildListener extends ChildEventListener {
    default void childAdded(String key, boolean value) {}
    default void childChanged(String key, boolean value) {}
    default void childRemoved(String key, boolean value) {}
    default void childMoved(String key, boolean value) {}


    @Override
    default public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
        String key = snapshot.getKey();
        boolean data = (boolean) snapshot.getValue();
        childAdded(key, data);
    }

    @Override
    default public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
        String key = snapshot.getKey();
        boolean data = (boolean) snapshot.getValue();
        childChanged(key,data);
    }

    @Override
    default public void onChildRemoved(@NonNull DataSnapshot snapshot) {
        String key = snapshot.getKey();
        boolean data = (boolean) snapshot.getValue();
        childRemoved(key, data);
    }

    @Override
    default public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
        String key = snapshot.getKey();
        boolean data = (boolean) snapshot.getValue();
        childMoved(key, data);
    }

    @Override
    default public void onCancelled(@NonNull DatabaseError error) {

    }
}
