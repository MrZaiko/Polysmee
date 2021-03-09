package io.github.polysmee.appointments;

import org.junit.Test;

import java.util.Arrays;
import java.util.Set;

import io.github.polysmee.interfaces.Appointment;
import io.github.polysmee.interfaces.User;

import static org.junit.Assert.*;

public class BasicAppointmentTest {
    User testUser = new TestUser("1");
    User testUser2 = new TestUser("2");
    Appointment a = new BasicAppointment(3600000, 1800000, "AICC", "Serie AICC", testUser);

    @Test
    public void constructorTest() {
        Appointment b = new BasicAppointment(-2000, -1800000, "AICC", "Serie AICC", testUser);
        Appointment c = new BasicAppointment(3600000, 100000000, "AICC", "Serie AICC", testUser);
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
        Appointment b = new BasicAppointment(-2000, -1800000, "AICC", "Serie AICC", testUser);
        assertTrue(b.setStartTime(50000));
        assertEquals(50000, b.getStartTime());
        assertFalse(b.setStartTime(-10000));
    }

    @Test
    public void setDuration() {
        Appointment b = new BasicAppointment(-2000, -1800000, "AICC", "Serie AICC", testUser);
        assertTrue(b.setDuration(50000));
        assertEquals(50000, b.getDuration());
        assertFalse(b.setDuration(-10000));
        assertFalse(b.setDuration(1000000000));
    }

    @Test
    public void setCourse() {
        Appointment b = new BasicAppointment(-2000, -1800000, "AICC", "Serie AICC", testUser);
        b.setCourse("CSN");
        assertEquals(b.getCourse(), "CSN");
    }

    @Test
    public void setTitle() {
        Appointment b = new BasicAppointment(-2000, -1800000, "AICC", "Serie AICC", testUser);
        b.setTitle("Serie CSN");
        assertEquals(b.getTitle(), "Serie CSN");
    }

    @Test
    public void addParticipant() {
        Appointment b = new BasicAppointment(-2000, -1800000, "AICC", "Serie AICC", testUser);
        assertTrue(b.addParticipant(testUser2));
        assertEquals(2, b.getParticipants().size());
        assertTrue(b.getParticipants().contains(testUser2));
    }

    @Test
    public void removeParticipant() {
        Appointment b = new BasicAppointment(-2000, -1800000, "AICC", "Serie AICC", testUser);
        b.addParticipant(testUser2);
        assertTrue(b.removeParticipant(testUser2));
        assertFalse(b.getParticipants().contains(testUser2));
        assertFalse(b.removeParticipant(testUser));
    }
}