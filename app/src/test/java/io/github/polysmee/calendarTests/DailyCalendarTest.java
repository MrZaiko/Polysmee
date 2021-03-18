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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

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
        assertThrows(IllegalArgumentException.class,()-> {
            DailyCalendar.getAppointmentsForTheDay(null);});
    }

    @Test
    public void getAppointmentsForTheDayReturnsTheSortedAppointments(){
        Random random = new Random();
        Set<CalendarAppointmentInfo> setOfAppointments = new HashSet<>();
        for(int i = 0; i < random.nextInt(5); ++i){
           setOfAppointments.add(new CalendarAppointmentInfo("TestCourse" + i,"TestTitle",
                   DailyCalendar.todayEpochTimeAtMidnight() + random.nextInt(60),60,
                   "TestId" + i, null,i ));
        }
        setOfAppointments.add(new CalendarAppointmentInfo("TestCourseTomorrow" ,"TestTitleTomorrow",
                DailyCalendar.todayEpochTimeAtMidnight() + 3600*24,60,
                "TestIdTomorrow", null,setOfAppointments.size() ));
        List<CalendarAppointmentInfo> sortedAppointmentsInfo = new ArrayList<>(setOfAppointments);
        Collections.sort(sortedAppointmentsInfo, new Comparator<CalendarAppointmentInfo>() {
            @Override
            public int compare(CalendarAppointmentInfo appointment, CalendarAppointmentInfo t1) {
                return Long.compare(appointment.getStartTime(),t1.getStartTime());
            }
        });
        sortedAppointmentsInfo.remove(sortedAppointmentsInfo.size() -1);
        assertEquals(sortedAppointmentsInfo, DailyCalendar.getAppointmentsForTheDay(setOfAppointments));
    }
}
