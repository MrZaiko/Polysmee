package io.github.polysmee.calendarTests;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import io.github.polysmee.calendar.CalendarAppointmentInfo;
import io.github.polysmee.database.User;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

@RunWith(JUnit4.class)
public class CalendarAppointmentInfoTest {

    @Test
    public void appointmentInfoGettersWorkCorrectly(){
        CalendarAppointmentInfo calendarAppointmentInfo = new CalendarAppointmentInfo("Course","Title",
                0,0,"0");
        assertEquals("Course",calendarAppointmentInfo.getCourse());
        assertEquals("Title",calendarAppointmentInfo.getTitle());
        assertEquals(0,calendarAppointmentInfo.getStartTime());
        assertEquals(0,calendarAppointmentInfo.getDuration());
        assertEquals("0",calendarAppointmentInfo.getId());
        //didn't put for user because equals is not defined for FakeDatabaseUser

    }

}
