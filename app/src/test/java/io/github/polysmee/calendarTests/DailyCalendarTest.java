package io.github.polysmee.calendarTests;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
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


    @Before
    public void setTodayDateInDailyCalendar() {
        Calendar calendar = Calendar.getInstance();
        DailyCalendar.setDayEpochTimeAtMidnight(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE), false);
    }

    @Test
    public void todayEpochTimeAtMidnightTest() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        long trueTime = calendar.getTimeInMillis();
        assertEquals(DailyCalendar.getDayEpochTimeAtMidnight(false), trueTime);
    }

    @Test
    public void getAppointmentsForTheDayThrowsExceptionIfNullSetTest() {
        assertThrows(IllegalArgumentException.class, () -> DailyCalendar.getAppointmentsForTheDay(null, false));
    }

    @Test
    public void getAppointmentsForTheDayReturnsTheSortedAppointments() {
        Random random = new Random();
        Set<CalendarAppointmentInfo> setOfAppointments = new HashSet<>();
        for (int i = 0; i < random.nextInt(5); ++i) {
            setOfAppointments.add(new CalendarAppointmentInfo("TestCourse" + i, "TestTitle",
                    DailyCalendar.getDayEpochTimeAtMidnight(false) + random.nextInt(60), 60,
                    "TestId" + i));
        }
        setOfAppointments.add(new CalendarAppointmentInfo("TestCourseTomorrow", "TestTitleTomorrow",
                DailyCalendar.getDayEpochTimeAtMidnight(false) + 3600 * 24 * 1000, 60,
                "TestIdTomorrow"));
        List<CalendarAppointmentInfo> sortedAppointmentsInfo = new ArrayList<>(setOfAppointments);
        Collections.sort(sortedAppointmentsInfo, (appointment, t1) -> Long.compare(appointment.getStartTime(), t1.getStartTime()));
        sortedAppointmentsInfo.remove(sortedAppointmentsInfo.size() - 1);
        assertEquals(sortedAppointmentsInfo, DailyCalendar.getAppointmentsForTheDay(setOfAppointments, false));
    }
}
