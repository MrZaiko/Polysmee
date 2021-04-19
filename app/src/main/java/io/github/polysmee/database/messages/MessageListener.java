package io.github.polysmee.database.messages;

public interface MessageListener {
    void update(Message message, String Key);
}
