package io.github.polysmee.calendarsync;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import io.github.polysmee.calendar.googlecalendarsync.CalendarService;
import io.github.polysmee.calendar.googlecalendarsync.CalendarUtilities;

@RunWith(JUnit4.class)
public class CalendarUtilitiesTest {
    @BeforeClass
    public static void init() {
        CalendarUtilities.setTest(true, true);
    }

    @Test
    public void onErrorAreCorrectlyCalled() {
        CalendarUtilities.deleteAppointmentFromCalendar(null, null, null, Assert::fail, () -> {});
        CalendarUtilities.createCalendar(null, null, Assert::fail, () -> {});
        CalendarUtilities.deleteCalendar(null, null, Assert::fail, () -> {});
        CalendarUtilities.addUserToCalendar(null, null, null, Assert::fail, () -> {});
        CalendarUtilities.updateAppointmentOnCalendar(null, null, null, null, null, null, null, Assert::fail, () -> {});
        CalendarUtilities.addAppointmentToCalendar(null, null, null, null, 0, 0, Assert::fail, () -> {});
    }
}
