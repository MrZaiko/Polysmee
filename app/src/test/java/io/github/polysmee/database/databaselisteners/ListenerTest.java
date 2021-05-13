package io.github.polysmee.database.databaselisteners;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashSet;

import static org.junit.Assert.assertThrows;

public class ListenerTest {

    BooleanValueListener bv = Assert::assertTrue;
    DownloadValueListener dv = (b) -> assertEquals(1, b.length);
    LoadValueListener lvl = (s) -> assertEquals("1234567890", s);
    StringValueListener sv = (s) -> assertEquals("1234567890", s);
    LongValueListener lv = (l) -> assertEquals(1234567890, l);
    StringSetValueListener ssv = (s) -> assertEquals(0, s.size());

    @Test
    public void onDone() {
        bv.onDone(true);
        dv.onDone(new byte[]{0});
        lvl.onDone("1234567890");
        sv.onDone("1234567890");
        lv.onDone(1234567890);
        ssv.onDone(new HashSet<>());
    }

    @Test
    public void onDataChange() {
        assertThrows(NullPointerException.class, () -> sv.onDataChange(null));
        assertThrows(NullPointerException.class, () -> lv.onDataChange(null));
        assertThrows(NullPointerException.class, () -> ssv.onDataChange(null));
        assertThrows(NullPointerException.class, () -> bv.onDataChange(null));
    }

    @Test
    public void onCancelled() {
        sv.onCancelled(null);
        lv.onCancelled(null);
        ssv.onCancelled(null);
        bv.onCancelled(null);
    }
}