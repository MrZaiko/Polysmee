package io.github.polysmee.roomActivityTests;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;

import io.github.polysmee.database.Message;

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

        Message message = new Message(userId, content, 0l, false);

        assertEquals(content, message.getContent());
        assertEquals(userId, message.getSender());
        assertEquals(messageTime, message.getMessageTime());
    }

    @Test
    public void equalsMethodReturnsFalseWithOtherTypesOfObject() {
        Message message = new Message("userID", "content", 0, false);
        assertFalse(message.equals("test"));
        assertFalse(message.equals(new Integer(10)));
        assertFalse(message.equals(new ArrayList<>()));
    }

    @Test
    public void equalsMethodReturnsFalseWithNullArg() {
        Message message = new Message("userID", "content", 0, false);
        assertFalse(message.equals(null));
    }

    @Test
    public void equalsMethodReturnsFalseWhenOneAttributDiffers() {
        Message message = new Message("userID", "content", 0, false);
        Message message1 = new Message("userID1", "content", 0, false);
        Message message2 = new Message("userID", "content1", 0, false);
        Message message3 = new Message("userID", "content", 1, false);

        assertFalse(message.equals(message1));
        assertFalse(message.equals(message2));
        assertFalse(message.equals(message3));
    }


    @Test
    public void equalsReturnsTrueWhenSameValues() {
        Message message1 = new Message("userID", "content", 0, false);
        Message message2 = new Message("userID", "content", 0, false);
        assertTrue(message1.equals(message2));
    }

}
