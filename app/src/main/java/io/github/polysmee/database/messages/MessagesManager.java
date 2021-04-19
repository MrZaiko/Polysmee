package io.github.polysmee.database.messages;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import io.github.polysmee.database.DatabaseFactory;

public class MessagesManager {
    private DatabaseReference databaseReference;

    public MessagesManager(String appointmentId) {
        databaseReference = DatabaseFactory.getAdaptedInstance().getReference("appointments/" + appointmentId + "/messages");
    }

    public void createListener(MessageListener onChildAdded, MessageListener onChildChanged,
                               MessageListener onChildRemoved, MessageListener onChildMoved) {
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Message message = snapshot.getValue(Message.class);
                String key = snapshot.getKey();

                onChildAdded.update(message, key);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Message message = snapshot.getValue(Message.class);
                String key = snapshot.getKey();

                onChildChanged.update(message, key);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                Message message = snapshot.getValue(Message.class);
                String key = snapshot.getKey();

                onChildRemoved.update(message, key);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Message message = snapshot.getValue(Message.class);
                String key = snapshot.getKey();

                onChildMoved.update(message, key);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    /**
     *
     * @param content
     * @param userId
     * @return the key of the message in the database
     *
     * Adds a new message to the database at the path given as reference, with sender and content given as argument and current time as message time
     */
    public String sendMessage(@NonNull String content, @NonNull String userId, boolean isAPicture) {
        String key = databaseReference.push().getKey();
        databaseReference.child(key).setValue(new Message(userId, content, System.currentTimeMillis(), isAPicture));
        return key;
    }


    public void editMessage(String messageKey, String newContent) {
        databaseReference.child(messageKey).child("content").setValue(newContent);
    }

    /*
     * Deletes the message whose key is messageKey from the database
     */
    public void deleteMessage(String messageKey) {
        databaseReference.child(messageKey).removeValue();
    }


}
