package io.github.polysmee.calendar;

import android.content.Intent;
import android.os.Bundle;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

import io.github.polysmee.R;
import io.github.polysmee.database.decoys.FakeDatabase;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.containsString;

@RunWith(AndroidJUnit4.class)
public class CalendarActivityTest {

    private static final Intent intent;
    static {
        intent = new Intent(getApplicationContext(), CalendarActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(CalendarActivity.UserTypeCode, "Fake");
        intent.putExtras(bundle);
    }
    @Rule
    public ActivityScenarioRule<CalendarActivity> testRule = new ActivityScenarioRule<>(intent);
    private static final int constraintLayoutIdForTests = 284546;

    @Before
    public void initUser(){
        FakeDatabase.idGenerator = new AtomicLong(0);
    }


    @Test
    public void writtenDateIsCorrectTest(){

        //Intent intent = new Intent(getApplicationContext(),CalendarActivity.class);
      //  intent.putExtra(CalendarActivity.UserTypeCode,"Fake");

        Date date = new Date(DailyCalendar.todayEpochTimeAtMidnight()*1000);
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        try(ActivityScenario<CalendarActivity> ignored = ActivityScenario.launch(intent)){
            Espresso.onView(withId(R.id.todayDateCalendarActivity)).check(ViewAssertions.matches(
                    withText(containsString("Appointments on the " + formatter.format(date) +" : " ))));
        }
    }

    @Test
    public void scrollViewContentIsCoherentAfterAddingAppointments(){
        Random rand = new Random();
        int number_of_appointments = rand.nextInt(9) + 1;

        CalendarAppointmentInfo[] infos = new CalendarAppointmentInfo[number_of_appointments];
        for(int i = 0; i<number_of_appointments; ++i){
            infos[i] = new CalendarAppointmentInfo("FakeCourse" + i, "FakeTitle" + i,
                    DailyCalendar.todayEpochTimeAtMidnight() + i*60,50,""+i,null,i);
        }

        try(ActivityScenario<CalendarActivity> ignored = ActivityScenario.launch(intent)){
            ViewInteraction demoButton = Espresso.onView(withId(R.id.calendarActivityDemoButton));
            for(int i = 0; i < number_of_appointments; ++i)
                demoButton.perform(ViewActions.click()); //add the appointments to layout
            int j = 0;
            for(int i = 0; i<number_of_appointments;++i){
                ViewInteraction calendarEntryDescription = Espresso.onView(withId(constraintLayoutIdForTests + j + 1));
                j+=3;
                calendarEntryDescription.check(ViewAssertions.matches(withText(formatAppointmentDescription(infos[i]))));
            }

        }
    }



    private String formatAppointmentDescription(CalendarAppointmentInfo appointment){
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
