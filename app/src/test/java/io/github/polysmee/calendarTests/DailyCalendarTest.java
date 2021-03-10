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
import java.util.Set;

import io.github.polysmee.calendar.DailyCalendar;
import io.github.polysmee.interfaces.Appointment;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import java.util.Random;

@RunWith(JUnit4.class)
public class DailyCalendarTest {




    @Test
    public void todayEpochTimeAtMidnightTest(){
        Calendar calendar =  Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY,0);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);
        calendar.set(Calendar.MILLISECOND,0);
        long trueTime = calendar.getTimeInMillis()/1000;
        assertEquals(DailyCalendar.todayEpochTimeAtMidnight(),trueTime);
    }

    @Test
    public void getAppointmentsForTheDayThrowsExceptionIfNullSetTest(){
        Set<Appointment> nullSet = null;
        assertThrows(IllegalArgumentException.class,()-> {
            DailyCalendar.getAppointmentsForTheDay(nullSet);});
    }

    @Test
    public void getAppointmentsForTheDayReturnsTheSortedAppointments(){
        Random random = new Random();
        Set<Appointment> setOfAppointments = new HashSet<>();
        for(int i = 0; i < random.nextInt(5); ++i){
            setOfAppointments.add(new AppointmentTestClass(
                    DailyCalendar.todayEpochTimeAtMidnight() + random.nextInt(60),60,"","")
            );
        }
        setOfAppointments.add(new AppointmentTestClass(
                DailyCalendar.todayEpochTimeAtMidnight() + 3600*24,60,"","")
        );
        List<Appointment> sortedAppointments = new ArrayList(setOfAppointments);
        Collections.sort(sortedAppointments, new Comparator<Appointment>() {
            @Override
            public int compare(Appointment appointment, Appointment t1) {
                return Long.compare(appointment.getStartTime(),t1.getStartTime());
            }
        });
        sortedAppointments.remove(sortedAppointments.size()-1);
        assertEquals(sortedAppointments, DailyCalendar.getAppointmentsForTheDay(setOfAppointments));
    }
}
