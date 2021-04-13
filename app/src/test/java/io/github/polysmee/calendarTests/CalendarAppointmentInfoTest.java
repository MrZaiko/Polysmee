package io.github.polysmee.calendarTests;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import io.github.polysmee.calendar.CalendarAppointmentInfo;
import io.github.polysmee.database.decoys.FakeDatabaseUser;
import io.github.polysmee.database.User;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

@RunWith(JUnit4.class)
public class CalendarAppointmentInfoTest {

    @Test
    public void appointmentInfoGettersWorkCorrectly(){
        User user = new FakeDatabaseUser("5","Youssef");
        CalendarAppointmentInfo calendarAppointmentInfo = new CalendarAppointmentInfo("Course","Title",
                0,0,"0",user,0);
        assertEquals("Course",calendarAppointmentInfo.getCourse());
        assertEquals("Title",calendarAppointmentInfo.getTitle());
        assertEquals(0,calendarAppointmentInfo.getStartTime());
        assertEquals(0,calendarAppointmentInfo.getDuration());
        assertEquals("0",calendarAppointmentInfo.getId());
        assertEquals(0,calendarAppointmentInfo.getIndex());
        //didn't put for user because equals is not defined for FakeDatabaseUser

    }

    @Test
    public void equalsReturnsTrueWithTwoAppointmentsWithSameAttributes(){
        User user = new FakeDatabaseUser("5","Youssef");
        CalendarAppointmentInfo calendarAppointmentInfo = new CalendarAppointmentInfo("Course","Title",
                0,0,"0",user,0);
        CalendarAppointmentInfo calendarAppointmentInfo2 = new CalendarAppointmentInfo("Course","Title",
                0,0,"0",user,0);
        assertEquals(calendarAppointmentInfo, calendarAppointmentInfo2);
    }

    @Test
    public void equalsReturnsFalseWithTwoAppointmentsOfDifferentAttributes(){
        User user = new FakeDatabaseUser("5","Youssef");
        CalendarAppointmentInfo calendarAppointmentInfo = new CalendarAppointmentInfo("Course","Title",
                0,0,"0",user,0);
        CalendarAppointmentInfo calendarAppointmentInfo2 = new CalendarAppointmentInfo("Course","Title",
                0,100,"0",user,0);
        assertNotEquals(calendarAppointmentInfo, calendarAppointmentInfo2);
    }
    @Test
    public void equalsReturnsTrueForTheSameObject(){
        User user = new FakeDatabaseUser("5","Youssef");
        CalendarAppointmentInfo calendarAppointmentInfo = new CalendarAppointmentInfo("Course","Title",
                0,0,"0",user,0);
        assertEquals(calendarAppointmentInfo, calendarAppointmentInfo);
    }

    @Test
    public void equalsReturnsFalseForDifferentClass(){
        User user = new FakeDatabaseUser("5","Youssef");
        CalendarAppointmentInfo calendarAppointmentInfo = new CalendarAppointmentInfo("Course","Title",
                0,0,"0",user,0);
        assertNotEquals(calendarAppointmentInfo, "");
    }

    @Test
    public void equalsReturnsFalseForNull(){
        User user = new FakeDatabaseUser("5","Youssef");
        CalendarAppointmentInfo calendarAppointmentInfo = new CalendarAppointmentInfo("Course","Title",
                0,0,"0",user,0);
        assertNotEquals(null, calendarAppointmentInfo);
    }

}
