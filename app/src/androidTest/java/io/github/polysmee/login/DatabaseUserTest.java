package io.github.polysmee.login;

import org.junit.Test;

import java.util.HashSet;

import io.github.polysmee.database.DatabaseUser;
import io.github.polysmee.interfaces.Appointment;

import static org.junit.Assert.*;

public class DatabaseUserTest {
    //todo : adapt when the user is actually implemented
    private final String randomString = "ZmeJaByFsVTqLWFjKvE";

    @Test
    public void getIdWorks() {
        assertEquals(randomString, new DatabaseUser(randomString).getId());
    }

    @Test
    public void getNameWorks() {
        assertEquals("", new DatabaseUser(randomString).getName());
    }

    @Test
    public void getSurnameWorks() {
        assertEquals("", new DatabaseUser(randomString).getSurname());
    }

    @Test
    public void getAppointmentsWorks() {
        assertEquals(new HashSet<Appointment>(), new DatabaseUser(randomString).getAppointments());
    }

    @Test
    public void addAppointmentWorks() {

    }

    @Test
    public void removeAppointmentWorks() {

    }

    @Test
    public void testEquals() {
        assertEquals(new DatabaseUser(randomString), new DatabaseUser(randomString));
    }

    @Test
    public void testHashCode() {
        assertEquals(new DatabaseUser(randomString).hashCode(), new DatabaseUser(randomString).hashCode());
    }
}