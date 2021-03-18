package io.github.polysmee.Messages;

import com.google.firebase.database.DatabaseReference;

import java.io.Serializable;

/**
 * Class representing the message object
 */
public class Message  {

    private final String sender;
    private String content;
    private final long messageTime;

    /**
     *
     * @param sender
     * @param content
     * @param messageTime
     */
    public Message(String sender, String content, long messageTime) {

        if(sender == null || content == null) {
            throw new IllegalArgumentException("null argument");
        }

        this.sender = sender;
        this.content = content;
        this.messageTime = messageTime;
    }


    /**
     *
     * @param newContent
     */
    public void editContent(String newContent) {
        if(newContent == null) {
            throw new IllegalArgumentException("null argument");
        }

        this.content = newContent;
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

    public static String sendMessage(String content, DatabaseReference ref, String userId) {
       String key = ref.push().getKey();
       ref.child(key).setValue(new Message(userId, content, System.currentTimeMillis()));
       return key;
    }

    @Override
    public boolean equals(Object other) {
        if(!(other instanceof Message) || other == null) {
            return false;
        }
        Message otherMessage = (Message) other;
        return otherMessage.content.equals(content) && otherMessage.messageTime == messageTime && otherMessage.sender.equals(sender);

    }


}

