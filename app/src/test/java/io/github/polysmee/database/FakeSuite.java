package io.github.polysmee.database;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.HashSet;

import io.github.polysmee.database.decoys.FakeDatabase;
import io.github.polysmee.database.decoys.FakeDatabaseAppointment;
import io.github.polysmee.database.decoys.FakeDatabaseUser;
import io.github.polysmee.interfaces.Appointment;
import io.github.polysmee.interfaces.User;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

@RunWith(JUnit4.class)
public class FakeSuite {

    private final String id = "boloss";
    private final String name = "MathisTestFake";

    @AfterClass
    public static void tearDown() {
        FakeDatabase.appId2App.clear();
    }

    @Test
    public void e2eFakes() {
        //user test
        User usr = new FakeDatabaseUser(id, name);
        assertEquals(id, usr.getId());
        usr.getNameAndThen((newname) -> assertEquals(name, newname));

        usr.getAppointmentsAndThen((apps) -> assertEquals(new HashSet<String>(), apps));

        String roomId = usr.createNewUserAppointment(0, 3600, "fakeCourse", "fakeRoom", false);
        usr.removeAppointment(new FakeDatabaseAppointment(roomId));

        //appointment test
        Appointment appo = new FakeDatabaseAppointment(roomId);
        appo.getStartTimeAndThen((st) -> assertEquals(0, st));

        appo.getDurationAndThen((dur) -> assertEquals(3600, dur));
        assertEquals(roomId, appo.getId());

        appo.getTitleAndThen((titl) -> assertEquals("fakeRoom", titl));

        appo.getParticipantsIdAndThen((ids) -> assertEquals(1, ids.size()));

        appo.getOwnerIdAndThen((idd) -> assertEquals(id, idd));

        assertTrue(appo.setStartTime(1));
        assertTrue(appo.setDuration(3599));
        appo.setCourse("HEEEELLLLO");
        appo.setTitle("AEZEZAE");

        assertFalse(appo.addParticipant(null));
        assertFalse(appo.removeParticipant(null));


    }

}
