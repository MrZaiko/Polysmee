package io.github.polysmee.database.databaselisteners;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

public interface StringChildListener extends ChildEventListener {

    default void childAdded(String id) {}
    default void childChanged(String id) {}
    default void childRemoved(String id) {}
    default void childMoved(String id) {}


    @Override
    default public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
        String data = (String) snapshot.getValue();
        if(data != null) {
            childAdded(data);
        }
    }

    @Override
    default public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
        String data = (String) snapshot.getValue();
        if(data != null) {
            childChanged(data);
        }
    }

    @Override
    default public void onChildRemoved(@NonNull DataSnapshot snapshot) {
        String data = (String) snapshot.getValue();
        if(data != null) {
            childRemoved(data);
        }
    }

    @Override
    default public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
        String data = (String) snapshot.getValue();
        if(data != null) {
            childMoved(data);
        }
    }

    @Override
    default public void onCancelled(@NonNull DatabaseError error) {

    }
}
