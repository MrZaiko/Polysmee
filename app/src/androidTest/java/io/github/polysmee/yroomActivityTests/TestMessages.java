package io.github.polysmee.yroomActivityTests;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;

import io.github.polysmee.database.Message;
import io.github.polysmee.internet.connection.InternetConnection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(JUnit4.class)
public class TestMessages {

    final String NULL_ARG = "null argument";

    @Test
    public void gettersReturnTheRightValue() {
        String content = "content";
        String userId = "userId";
        long messageTime = 0L;

        Message message = new Message(userId, content, 0l, false, 0);

        assertEquals(content, message.getContent());
        assertEquals(userId, message.getSender());
        assertEquals(messageTime, message.getMessageTime());
        assertEquals(true,InternetConnection.isOn());
    }

    @Test
    public void equalsMethodReturnsFalseWithOtherTypesOfObject() {
        Message message = new Message("userID", "content", 0, false, 0);
        assertFalse(message.equals("test"));
        assertFalse(message.equals(new Integer(10)));
        assertFalse(message.equals(new ArrayList<>()));
    }

    @Test
    public void equalsMethodReturnsFalseWithNullArg() {
        Message message = new Message("userID", "content", 0, false, 0);
        assertFalse(message.equals(null));
    }

    @Test
    public void equalsMethodReturnsFalseWhenOneAttributDiffers() {
        Message message = new Message("userID", "content", 0, false, 0);
        Message message1 = new Message("userID1", "content", 0, false, 0);
        Message message2 = new Message("userID", "content1", 0, false, 0);
        Message message3 = new Message("userID", "content", 1, false, 0);

        assertFalse(message.equals(message1));
        assertFalse(message.equals(message2));
        assertFalse(message.equals(message3));
    }


    @Test
    public void equalsReturnsTrueWhenSameValues() {
        Message message1 = new Message("userID", "content", 0, false, 0);
        Message message2 = new Message("userID", "content", 0, false, 0);
        assertTrue(message1.equals(message2));
    }

}
