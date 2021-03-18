package io.github.polysmee.calendarTests;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import io.github.polysmee.calendar.CalendarAppointmentInfo;
import io.github.polysmee.calendar.DailyCalendar;
import io.github.polysmee.database.decoys.FakeDatabaseUser;
import io.github.polysmee.interfaces.User;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

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
    public void equalsMethodWorksCorrectly(){
        User user = new FakeDatabaseUser("5","Youssef");
        CalendarAppointmentInfo calendarAppointmentInfo = new CalendarAppointmentInfo("Course","Title",
                0,0,"0",user,0);
        CalendarAppointmentInfo calendarAppointmentInfo2 = new CalendarAppointmentInfo("Course","Title",
                0,0,"0",user,0);
        assertEquals(true,calendarAppointmentInfo.equals(calendarAppointmentInfo2));
        assertEquals(false,calendarAppointmentInfo.equals(null));
        CalendarAppointmentInfo calendarAppointmentInfo3 = new CalendarAppointmentInfo("Course","Title",
                0,10,"0",user,0);
        assertEquals(false,calendarAppointmentInfo.equals(calendarAppointmentInfo3));
    }


}
