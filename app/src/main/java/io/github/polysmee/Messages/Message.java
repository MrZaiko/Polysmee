package io.github.polysmee.Messages;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.Serializable;

import io.github.polysmee.login.MainUserSingleton;

public class Message implements Serializable {

    private final String sender;
    private String content;
    private final long messageTime;


    public Message(String sender, String content, long messageTime) {

        if(sender == null || content == null) {
            throw new IllegalArgumentException("null argument");
        }

        this.sender = sender;
        this.content = content;
        this.messageTime = messageTime;
    }


    public void editContent(String newContent) {
        this.content = content;
    }

    public String getSender() {
        return sender;
    }

    public String getContent() {
        return content;
    }

    public long getMessageTime() {
        return messageTime;
    }

    public static void sendMessage(String content, String roomId, String userId) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        database.getReference("rooms/currentRoomId/messages").push().setValue(new Message(userId, content, System.currentTimeMillis()));
    }

    @Override
    public boolean equals(Object other) {
        if(!(other instanceof Message) || other == null) {
            return false;
        }
        Message otherMessage = (Message) other;
        return otherMessage.content.equals(content) && otherMessage.messageTime == messageTime && otherMessage.sender == sender;

    }


}

