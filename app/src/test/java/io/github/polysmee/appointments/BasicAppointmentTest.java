package io.github.polysmee.appointments;

import org.junit.Test;

import io.github.polysmee.interfaces.User;
import io.github.polysmee.login.DatabaseUser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class BasicAppointmentTest {
    User testUser = new DatabaseUser("1");
    User testUser2 = new DatabaseUser("2");
    BasicAppointment a = new BasicAppointment(3600000, 1800000, "AICC", "Serie AICC", testUser);

    @Test
    public void constructorTest() {
        BasicAppointment b = new BasicAppointment(-2000, -1800000, "AICC", "Serie AICC", testUser);
        BasicAppointment c = new BasicAppointment(3600000, 100000000, "AICC", "Serie AICC", testUser);
        assertEquals(a.getStartTime(), 3600000);
        assertEquals(a.getDuration(), 1800000);
        assertEquals(b.getStartTime(), 0);
        assertEquals(b.getDuration(), 0);
        assertEquals(c.getDuration(), 3600000*4);
    }

    @Test
    public void getStartTime() {
        assertEquals(a.getStartTime(), 3600000);
    }

    @Test
    public void getDuration() {
        assertEquals(a.getDuration(), 1800000);
    }

    @Test
    public void getCourse() {
        assertEquals(a.getCourse(), "AICC");
    }

    @Test
    public void getTitle() {
        assertEquals(a.getTitle(), "Serie AICC");
    }

    @Test
    public void getParticipants() {
        assertTrue(a.getParticipants().contains(testUser));
        assertEquals(1, a.getParticipants().size());
    }

    @Test
    public void getOwner() {
        assertEquals(a.getOwner(), testUser);
    }

    @Test
    public void setStartTime() {
        BasicAppointment b = new BasicAppointment(-2000, -1800000, "AICC", "Serie AICC", testUser);
        assertTrue(b.setStartTime(50000));
        assertEquals(50000, b.getStartTime());
        assertFalse(b.setStartTime(-10000));
    }

    @Test
    public void setDuration() {
        BasicAppointment b = new BasicAppointment(-2000, -1800000, "AICC", "Serie AICC", testUser);
        assertTrue(b.setDuration(50000));
        assertEquals(50000, b.getDuration());
        assertFalse(b.setDuration(-10000));
        assertFalse(b.setDuration(1000000000));
    }

    @Test
    public void setCourse() {
        BasicAppointment b = new BasicAppointment(-2000, -1800000, "AICC", "Serie AICC", testUser);
        b.setCourse("CSN");
        assertEquals(b.getCourse(), "CSN");
    }

    @Test
    public void setTitle() {
        BasicAppointment b = new BasicAppointment(-2000, -1800000, "AICC", "Serie AICC", testUser);
        b.setTitle("Serie CSN");
        assertEquals(b.getTitle(), "Serie CSN");
    }

    @Test
    public void addParticipant() {
        BasicAppointment b = new BasicAppointment(-2000, -1800000, "AICC", "Serie AICC", testUser);
        assertTrue(b.addParticipant(testUser2));
        assertEquals(2, b.getParticipants().size());
        assertTrue(b.getParticipants().contains(testUser2));
    }

    @Test
    public void removeParticipant() {
        BasicAppointment b = new BasicAppointment(-2000, -1800000, "AICC", "Serie AICC", testUser);
        b.addParticipant(testUser2);
        assertTrue(b.removeParticipant(testUser2));
        assertFalse(b.getParticipants().contains(testUser2));
        assertFalse(b.removeParticipant(testUser));
    }
}