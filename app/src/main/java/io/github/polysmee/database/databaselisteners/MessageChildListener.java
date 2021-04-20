package io.github.polysmee.database.databaselisteners;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import io.github.polysmee.messages.Message;

public interface MessageChildListener extends ChildEventListener {
    default void childAdded(String key, Message value) {}
    default void childChanged(String key, Message value) {}
    default void childRemoved(String key, Message value) {}
    default void childMoved(String key, Message value) {}


    @Override
    default public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
        String key = snapshot.getKey();
        Message data = snapshot.getValue(Message.class);
        childAdded(key, data);
    }

    @Override
    default public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
        String key = snapshot.getKey();
        Message data = snapshot.getValue(Message.class);
        childChanged(key,data);
    }

    @Override
    default public void onChildRemoved(@NonNull DataSnapshot snapshot) {
        String key = snapshot.getKey();
        Message data = snapshot.getValue(Message.class);
        childRemoved(key, data);
    }

    @Override
    default public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
        String key = snapshot.getKey();
        Message data = snapshot.getValue(Message.class);
        childMoved(key, data);
    }

    @Override
    default public void onCancelled(@NonNull DatabaseError error) {

    }
}
