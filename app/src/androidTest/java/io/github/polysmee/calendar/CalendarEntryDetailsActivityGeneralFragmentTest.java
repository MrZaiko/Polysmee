package io.github.polysmee.calendar;


import android.content.Intent;
import android.os.Bundle;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.FirebaseApp;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

import io.github.polysmee.R;
import io.github.polysmee.database.DatabaseFactory;
import io.github.polysmee.database.decoys.FakeDatabase;
import io.github.polysmee.login.AuthenticationFactory;
import io.github.polysmee.login.MainUserSingleton;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed;
import static com.schibsted.spain.barista.interaction.BaristaSleepInteractions.sleep;
import static java.util.concurrent.TimeUnit.SECONDS;

@RunWith(AndroidJUnit4.class)
public class CalendarEntryDetailsActivityGeneralFragmentTest {

    /*private static final Intent intent;
    static {
        intent = new Intent(getApplicationContext(), CalendarActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(CalendarActivity.UserTypeCode, "Fake");
        intent.putExtras(bundle);
    }
    @Before
    public void initUser(){
        FakeDatabase.idGenerator = new AtomicLong(0);
    }*/

    private static final String username1 = "Youssef le don";

    private static final String appointmentTitle = "coucouchou";
    private static final String appointmentId = "-lsdqrhzutetisjhmf";

    @BeforeClass
    public static void setUp() throws Exception {

        DatabaseFactory.setTest();
        AuthenticationFactory.setTest();
        FirebaseApp.clearInstancesForTest();
        FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext());
        Tasks.await(AuthenticationFactory.getAdaptedInstance().createUserWithEmailAndPassword("polysmee251098@gmail.com", "fakePassword"));
        DatabaseFactory.getAdaptedInstance().getReference("users").child(MainUserSingleton.getInstance().getId()).child("name").setValue(username1);

        /*DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId).child("title").setValue(appointmentTitle);
        DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId).child("course").setValue(appointmentCourse);
        DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId).child("start").setValue(appointmentStart);
        DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId).child("owner").setValue(MainUserSingleton.getInstance().getId());
        DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId).child("participants").child(MainUserSingleton.getInstance().getId()).setValue(true);*/


    }
    @Before
    public void setTodayDateInDailyCalendar(){
        Calendar calendar = Calendar.getInstance();
        DailyCalendar.setDayEpochTimeAtMidnight(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DATE));
    }

    @Test
    public void appointmentDetailsAreCorrect(){
        CalendarAppointmentInfo info = new CalendarAppointmentInfo("FakeCourse0", "FakeTitle0",
                DailyCalendar.getDayEpochTimeAtMidnight() ,50,appointmentId+12,null,0);
        Intent intent = new Intent(getApplicationContext(), CalendarActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(CalendarActivity.UserTypeCode, "Real");
        intent.putExtras(bundle);


        try(ActivityScenario<CalendarActivity> ignored = ActivityScenario.launch(intent)){

            DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId+12).child("title").setValue(info.getTitle());
            DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId+12).child("course").setValue(info.getCourse());
            DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId+12).child("start").setValue(info.getStartTime());
            DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId+12).child("owner").setValue(MainUserSingleton.getInstance().getId());
            DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId+12).child("participants").child(MainUserSingleton.getInstance().getId()).setValue(true);
            DatabaseFactory.getAdaptedInstance().getReference("users").child(MainUserSingleton.getInstance().getId()).child("appointments").child(appointmentId+12).setValue(true);
            sleep(5,SECONDS);
            ViewInteraction calendarEntryDetailButton = Espresso.onView(withText("Details"));
            calendarEntryDetailButton.perform(ViewActions.click());
            sleep(3,SECONDS);
            ViewInteraction titleDetails = Espresso.onView(withId(R.id.calendarEntryDetailActivityTitleSet));
            titleDetails.check(ViewAssertions.matches(withText(info.getTitle())));
            ViewInteraction courseDetails = Espresso.onView(withId(R.id.calendarEntryDetailActivityCourseSet));
            courseDetails.check(ViewAssertions.matches(withText(info.getCourse())));

            DatabaseFactory.getAdaptedInstance().getReference("users").child(MainUserSingleton.getInstance().getId()).child("appointments").child(appointmentId+12).setValue(null);
            DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId+12).setValue(null);
        }
    }

    @Test
    public void appointmentModificationIsSeenOnCalendar(){
        String newTitle = "NewTitleTest";
        String newCourse = "NewCourseTest";

        CalendarAppointmentInfo info = new CalendarAppointmentInfo("FakeCourse0", "FakeTitle0",
                DailyCalendar.getDayEpochTimeAtMidnight() ,50,appointmentId+13,null,0);
        Intent intent = new Intent(getApplicationContext(), CalendarActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(CalendarActivity.UserTypeCode, "Real");
        intent.putExtras(bundle);

        try(ActivityScenario<CalendarActivity> ignored = ActivityScenario.launch(intent)){

            DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId+13).child("title").setValue(info.getTitle());
            DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId+13).child("course").setValue(info.getCourse());
            DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId+13).child("start").setValue(info.getStartTime());
            DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId+13).child("owner").setValue(MainUserSingleton.getInstance().getId());
            DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId+13).child("participants").child(MainUserSingleton.getInstance().getId()).setValue(true);
            DatabaseFactory.getAdaptedInstance().getReference("users").child(MainUserSingleton.getInstance().getId()).child("appointments").child(appointmentId+13).setValue(true);
            sleep(5,SECONDS);

            ViewInteraction calendarEntryDetailButton = Espresso.onView(withText("Details"));
            calendarEntryDetailButton.perform(ViewActions.click());

            sleep(3,SECONDS);
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

            sleep(3,SECONDS);
            assertDisplayed(formatAppointmentDescription(info));

            DatabaseFactory.getAdaptedInstance().getReference("users").child(MainUserSingleton.getInstance().getId()).child("appointments").child(appointmentId+13).setValue(null);
            DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId+13).setValue(null);
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