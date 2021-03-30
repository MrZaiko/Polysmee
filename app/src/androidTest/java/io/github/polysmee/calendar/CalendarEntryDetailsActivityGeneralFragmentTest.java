package io.github.polysmee.calendar;


import android.content.Intent;
import android.os.Bundle;

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

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed;

@RunWith(AndroidJUnit4.class)
public class CalendarEntryDetailsActivityGeneralFragmentTest {

    private static final int constraintLayoutIdForTests = 284546;

    private static final Intent intent;
    static {
        intent = new Intent(getApplicationContext(), CalendarActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(CalendarActivity.UserTypeCode, "Fake");
        intent.putExtras(bundle);
    }
    @Before
    public void initUser(){
        FakeDatabase.idGenerator = new AtomicLong(0);
    }

    @Test
    public void appointmentDetailsAreCorrect(){
        CalendarAppointmentInfo info = new CalendarAppointmentInfo("FakeCourse0", "FakeTitle0",
                DailyCalendar.todayEpochTimeAtMidnight() ,50,"0",null,0);
        try(ActivityScenario<CalendarActivity> ignored = ActivityScenario.launch(intent)){

            ViewInteraction demoButton = Espresso.onView(withId(R.id.calendarActivityCreateAppointmentButton));
            demoButton.perform(ViewActions.click());

            ViewInteraction calendarEntryDetailButton = Espresso.onView(withText("Details"));
            calendarEntryDetailButton.perform(ViewActions.click());
            ViewInteraction titleDetails = Espresso.onView(withId(R.id.calendarEntryDetailActivityTitleSet));
            titleDetails.check(ViewAssertions.matches(withText(info.getTitle())));
            ViewInteraction courseDetails = Espresso.onView(withId(R.id.calendarEntryDetailActivityCourseSet));
            courseDetails.check(ViewAssertions.matches(withText(info.getCourse())));
        }
    }

    @Test
    public void appointmentModificationIsSeenOnCalendar(){
        String newTitle = "NewTitleTest";
        String newCourse = "NewCourseTest";
        CalendarAppointmentInfo info = new CalendarAppointmentInfo(newCourse, newTitle,
                DailyCalendar.todayEpochTimeAtMidnight() ,50,"0",null,0);
        try(ActivityScenario<CalendarActivity> ignored = ActivityScenario.launch(intent)){

            ViewInteraction demoButton = Espresso.onView(withId(R.id.calendarActivityCreateAppointmentButton));
            demoButton.perform(ViewActions.click());

            ViewInteraction calendarEntryDetailButton = Espresso.onView(withText("Details"));
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

            assertDisplayed(formatAppointmentDescription(info));
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
