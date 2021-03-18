package io.github.polysmee.calendar;


import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

import io.github.polysmee.R;
import io.github.polysmee.database.decoys.FakeDatabase;
import io.github.polysmee.database.decoys.FakeDatabaseUser;
import io.github.polysmee.interfaces.User;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertEquals;
import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class CalendarEntryDetailsActivityTest {

    private static final int constraintLayoutIdForTests = 284546;
    private User user ;

    @Before
    public void initUser(){
        user = FakeDatabaseUser.getInstance();
        FakeDatabase.idGenerator = new AtomicLong(0);
    }

    @Test
    public void appointmentDetailsAreCorrect(){
        CalendarAppointmentInfo info = new CalendarAppointmentInfo("FakeCourse0", "FakeTitle0",
                DailyCalendar.todayEpochTimeAtMidnight() ,50,"0",user,0);
        Intent intent = new Intent(getApplicationContext(),CalendarActivity.class);
        try(ActivityScenario<CalendarActivity> ignored = ActivityScenario.launch(intent)){
            int j = 0;

            ViewInteraction demoButton = Espresso.onView(withId(R.id.calendarActivityDemoButton));
            demoButton.perform(ViewActions.click());

            ViewInteraction calendarEntryDetailButton = Espresso.onView(withId(constraintLayoutIdForTests + j + 2));
            calendarEntryDetailButton.perform(ViewActions.click());
            ViewInteraction titleDetails = Espresso.onView(withId(R.id.calendarEntryDetailActivityTitleSet));
            titleDetails.check(ViewAssertions.matches(withText(info.getTitle())));
            ViewInteraction courseDetails = Espresso.onView(withId(R.id.calendarEntryDetailActivityCourseSet));
            courseDetails.check(ViewAssertions.matches(withText(info.getCourse())));
        }
    }

    @Test
    public void appointmentModificationIsSeenOnCalendar(){
        Intent intent = new Intent(getApplicationContext(),CalendarActivity.class);
        String newTitle = "NewTitleTest";
        String newCourse = "NewCourseTest";
        CalendarAppointmentInfo info = new CalendarAppointmentInfo(newCourse, newTitle,
                DailyCalendar.todayEpochTimeAtMidnight() ,50,"0",user,0);
        try(ActivityScenario<CalendarActivity> ignored = ActivityScenario.launch(intent)){
            int j = 0;

            ViewInteraction demoButton = Espresso.onView(withId(R.id.calendarActivityDemoButton));
            demoButton.perform(ViewActions.click());

            ViewInteraction calendarEntryDetailButton = Espresso.onView(withId(constraintLayoutIdForTests + j + 2));
            calendarEntryDetailButton.perform(ViewActions.click());

            ViewInteraction titleDetails = Espresso.onView(withId(R.id.calendarEntryDetailActivityTitleSet));
            titleDetails.perform(ViewActions.clearText());
            titleDetails.perform(ViewActions.typeText(newTitle));
            closeSoftKeyboard();

            ViewInteraction courseDetails = Espresso.onView(withId(R.id.calendarEntryDetailActivityCourseSet));
            courseDetails.perform(ViewActions.clearText());
            courseDetails.perform(ViewActions.typeText(newCourse));
            closeSoftKeyboard();

            ViewInteraction modifyButton = Espresso.onView(withId(R.id.calendarEntryDetailActivityDoneModifyButton));
            modifyButton.perform(ViewActions.click());

            ViewInteraction calendarEntryDescription = Espresso.onView(withId(constraintLayoutIdForTests + j + 1));
            calendarEntryDescription.check(ViewAssertions.matches(withText(formatAppointmentDescription(info))));
            assertEquals(1,1);
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
