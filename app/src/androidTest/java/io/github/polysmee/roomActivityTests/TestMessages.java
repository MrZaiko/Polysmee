package io.github.polysmee.roomActivityTests;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.junit.Test;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.*;


import io.github.polysmee.Messages.Message;

public class TestMessages {

    final String NULL_ARG = "null argument";

    /*@Test
    public void constructorThrowsExceptionsWithNullUser() {
        try {
            new Message(null, "content", 0);
        } catch (IllegalArgumentException e) {
            assertEquals(NULL_ARG,e.getMessage());
            return;
        }

        assert(false);

    }

    @Test
    public void constructorThrowsExceptionsWithNullContent() {
        try {
            new Message("userID", null, 0);
        } catch (IllegalArgumentException e) {
            assertEquals(NULL_ARG,e.getMessage());
            return;
        }

        assert(false);

    }

    @Test
    public void editContentThrowsIllegalArgumentExceptionWithNullString() {
        try {
            Message message = new Message("userID", "content", 0);
            message.editContent(null);
        } catch (IllegalArgumentException e) {
            assertEquals(NULL_ARG,e.getMessage());
            return;
        }

        assert(false);
    }*/

    @Test
    public void gettersReturnTheRightValue() {
        String content = "content";
        String userId = "userId";
        long messageTime = 0l;

        Message message = new Message(userId, content, 0l);

        assertEquals(content, message.getContent());
        assertEquals(userId, message.getSender());
        assertEquals(messageTime, message.getMessageTime());
    }

    /*@Test
    public void editContentBehavesWellWithValidArgument() {
        Message message = new Message("userID", "content", 0);
        assertEquals("content", message.getContent());
        String newContent = "new content";
        message.editContent(newContent);
        System.out.println(message.getContent());

        assertEquals(newContent, message.getContent());
    }*/

    @Test
    public void equalsMethodReturnsFalseWithOtherTypesOfObject() {
        Message message = new Message("userID", "content", 0);
        assertEquals(false, message.equals("test"));
        assertEquals(false, message.equals(new Integer(10)));
        assertEquals(false, message.equals(new ArrayList<>()));
    }

    @Test
    public void equalsMethodReturnsFalseWithNullArg() {
        Message message = new Message("userID", "content", 0);
        assertEquals(false, message.equals(null));
    }

    @Test
    public void equalsMethodReturnsFalseWhenOneAttributDiffers() {
        Message message = new Message("userID", "content", 0);
        Message message1 = new Message("userID1", "content", 0);
        Message message2 = new Message("userID", "content1", 0);
        Message message3 = new Message("userID", "content", 1);

        assertEquals(false, message.equals(message1));
        assertEquals(false, message.equals(message2));
        assertEquals(false, message.equals(message3));
    }


    @Test
    public void equalsReturnsTrueWhenSameValues() {
        Message message1 = new Message("userID", "content", 0);
        Message message2 = new Message("userID", "content", 0);
        assertEquals(true, message1.equals(message2));
    }

    /*@Test
    public void sendMessageBehavesProperlyWithValidArgs() throws ExecutionException, InterruptedException {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("messages");
        String content = "content";
        String userId = "testUser";
        String key = Message.sendMessage(content, ref, userId);

        DataSnapshot ds = Tasks.await(ref.child(key).get());
        String user = ds.child("sender").getValue(String.class);
        String text = ds.child("content").getValue(String.class);
        Thread.sleep(3000);
        Tasks.await(ref.child(key).removeValue());
        assertEquals(userId, user);
        assertEquals(content, text);
    }*/
}
