package io.github.polysmee.calendar;

import android.content.Intent;

import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;


import org.junit.Before;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.assertion.ViewAssertions;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import io.github.polysmee.R;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static org.hamcrest.Matchers.*;


import io.github.polysmee.interfaces.Appointment;
import io.github.polysmee.interfaces.User;

@RunWith(AndroidJUnit4.class)
public class CalendarActivityTest {

    @Rule
    public ActivityScenarioRule<CalendarActivity> testRule = new ActivityScenarioRule<>(CalendarActivity.class);
    private static final int constraintLayoutId = 284546;
    private User user ;

    @Before
    public void initUser(){
        user = PseudoLoggedUser.getSingletonPseudoUser("idMagique");
    }

    @Test
    public void writtenDateIsCorrectTest(){
        Intent intent = new Intent(getApplicationContext(),CalendarActivity.class);
        Date date = new Date(DailyCalendar.todayEpochTimeAtMidnight()*1000);
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        try(ActivityScenario<CalendarActivity> ignored = ActivityScenario.launch(intent)){
            Espresso.onView(withId(R.id.todayDateCalendarActivity)).check(ViewAssertions.matches(
                    withText(containsString("Appointments on the " + formatter.format(date) +" : " ))));
        }
    }


    @Test
    public void scrollViewContentIsCoherentAtStartup(){
        Random rand = new Random();
        int size = rand.nextInt(9) + 1;
        long[] times = new long[size]; // the 5th won't be today
        Appointment[] appointments = new AppointmentTestClass[size];

        for (int i = 0; i<size-1; ++i){
            times[i] = DailyCalendar.todayEpochTimeAtMidnight() + i * 60;
        }
        times[size-1] = times[0] + 3600 *24;
        for(int i = 0; i < times.length;++i){
            Appointment a = new AppointmentTestClass(times[i],60,"ML"+i,"Title"+i);
            user.addAppointment(a);
            appointments[i] = a;
        }
        Intent intent = new Intent(getApplicationContext(),CalendarActivity.class);

        try(ActivityScenario<CalendarActivity> ignored = ActivityScenario.launch(intent)){
            int j = 0;
            for(int i = 0; i<times.length -1; ++i){
                ViewInteraction calendarEntryDescription = Espresso.onView(withId(constraintLayoutId + j + 1));
                j+=3;
                calendarEntryDescription.check(ViewAssertions.matches(
                        withText(formatAppointmentDescription(appointments[i]))));
            }

        }
    }
    @Test
    public void scrollViewContentAfterRefreshIsCoherentWithUserAdds(){
        Random rand = new Random();
        int size = rand.nextInt(9) + 1;
        long[] times = new long[size]; // the 5th won't be today
        Appointment[] appointments = new AppointmentTestClass[size];

        for (int i = 0; i<size-1; ++i){
            times[i] = DailyCalendar.todayEpochTimeAtMidnight() + i * 60;
        }
        times[size-1] = times[0] + 3600 *24;

        Intent intent = new Intent(getApplicationContext(),CalendarActivity.class);
        try(ActivityScenario<CalendarActivity> ignored = ActivityScenario.launch(intent)){
            for(int i = 0; i < times.length;++i){
                Appointment a = new AppointmentTestClass(times[i],60,"ML"+i,"Title"+i);
                user.addAppointment(a);
                appointments[i] = a;
            }
            ViewInteraction onRefreshButton = Espresso.onView(withId(R.id.refreshButtonCalendarActivity));
            onRefreshButton.perform(ViewActions.click());
            int j = 0;
            for(int i = 0; i<times.length -1; ++i){
                ViewInteraction calendarEntryDescription = Espresso.onView(withId(constraintLayoutId + j + 1));
                j+=3;
                calendarEntryDescription.check(ViewAssertions.matches(
                        withText(formatAppointmentDescription(appointments[i]))));
            }

        }
    }

    private String formatAppointmentDescription(Appointment appointment){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Reunion name : ").append(appointment.getTitle());
        stringBuilder.append("\n");
        stringBuilder.append("Course name  : ").append(appointment.getCourse());
        stringBuilder.append("\n");
        Date date = new Date(appointment.getStartTime() * 1000);
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        stringBuilder.append("Start time : ").append(formatter.format(date));
        return stringBuilder.toString();
    }
}
