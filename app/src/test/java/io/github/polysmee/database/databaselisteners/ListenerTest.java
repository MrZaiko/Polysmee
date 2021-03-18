package io.github.polysmee.database.databaselisteners;

import com.google.firebase.database.DataSnapshot;

import org.junit.Test;

import java.util.HashSet;

import static org.junit.Assert.*;

public class ListenerTest {

    StringValueListener sv     = (s) -> assertEquals("1234567890", s);
    LongValueListener   lv     = (l) -> assertEquals(1234567890,   l);
    StringSetValueListener ssv = (s) -> assertEquals(0, s.size());

    @Test
    public void onDone() {
        sv.onDone("1234567890");
        lv.onDone(1234567890);
        ssv.onDone(new HashSet<>());
    }

    @Test
    public void onDataChange() {
        assertThrows(NullPointerException.class, () -> sv.onDataChange(null));
        assertThrows(NullPointerException.class, () -> lv.onDataChange(null));
        assertThrows(NullPointerException.class, () -> ssv.onDataChange(null));
    }

    @Test
    public void onCancelled() {
        sv.onCancelled(null);
        lv.onCancelled(null);
        ssv.onCancelled(null);
    }
}