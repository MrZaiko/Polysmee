package io.github.polysmee.messages;

import androidx.annotation.NonNull;

import com.google.firebase.database.DatabaseReference;

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
     * Creates a new Message with given time, senderId and content
     */
    public Message(@NonNull String sender, @NonNull String content, long messageTime) {
        this.sender = sender;
        this.content = content;
        this.messageTime = messageTime;
    }

    /**
     * A default constructor is needed for firebase database
     */
    public Message() {
        this.sender = "";
        this.content = "";
        this.messageTime = 0;
    }



    /**
     *
     * @return the uid of the sender
     */
    public String getSender() {
        assert(sender != null);
        return sender;
    }

    /**
     *
     * @return the content of the message
     */
    public String getContent() {
        assert(content != null);
        return content;
    }

    /**
     *
     * @return the send time of the message
     */
    public long getMessageTime() {
        return messageTime;
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

