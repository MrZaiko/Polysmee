package io.github.polysmee.calendar;


import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.testing.FragmentScenario;
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
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import io.github.polysmee.R;
import io.github.polysmee.calendar.detailsFragments.CalendarEntryDetailAddBanParticipantsFragment;
import io.github.polysmee.calendar.detailsFragments.CalendarEntryDetailsGeneralFragment;
import io.github.polysmee.database.DatabaseAppointment;
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
import static org.junit.Assert.assertEquals;

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

    private static final String username1 = "Youssef le a";

    private static final String appointmentTitle = "coucouchou";
    private static final String appointmentId = "-lsdqrhzutatisjhmf";
    private static final String appointmentCourse = "SDP";
    private static final long appointmentStart = 265655445;
    @BeforeClass
    public static void setUp() throws Exception {

        DatabaseFactory.setTest();
        AuthenticationFactory.setTest();
        FirebaseApp.clearInstancesForTest();
        FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext());
        Tasks.await(AuthenticationFactory.getAdaptedInstance().createUserWithEmailAndPassword("polysmee241098@gmail.com", "fakePassword"));
        DatabaseFactory.getAdaptedInstance().getReference("users").child(MainUserSingleton.getInstance().getId()).child("name").setValue(username1);

    }

    @Before
    public void setTodayDateInDailyCalendar(){
        Calendar calendar = Calendar.getInstance();
        DailyCalendar.setDayEpochTimeAtMidnight(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DATE));
    }

    @Test
    public void appointmentDetailsAreCorrect(){
        CalendarAppointmentInfo info = new CalendarAppointmentInfo("FakeCourse0", "FakeTitle0",
                DailyCalendar.getDayEpochTimeAtMidnight() ,50,"0",null,0);

        DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId).child("title").setValue(info.getTitle());
        DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId).child("course").setValue(info.getCourse());
        DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId).child("start").setValue(info.getStartTime());
        DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId).child("owner").setValue(MainUserSingleton.getInstance().getId());
        DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId).child("participants").child(MainUserSingleton.getInstance().getId()).setValue(true);

        Bundle bundle = new Bundle();

        bundle.putSerializable(CalendarActivity.UserTypeCode,"Real");
        bundle.putSerializable(CalendarEntryDetailsGeneralFragment.APPOINTMENT_DETAIL_GENERAL_ID,appointmentId);

        FragmentScenario.launchInContainer(CalendarEntryDetailsGeneralFragment.class,bundle);
        sleep(3,SECONDS);
        assertDisplayed(info.getTitle());
        assertDisplayed(info.getCourse());

        DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId).setValue(null);

    }

   @Test
    public void appointmentModificationIsSeenOnCalendar(){
        String newTitle = "NewTitleTest";
        String newCourse = "NewCourseTest";
        CalendarAppointmentInfo info = new CalendarAppointmentInfo("FakeCourse0", "FakeTitle0",
               DailyCalendar.getDayEpochTimeAtMidnight() ,50,"0",null,0);

        DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId+1).child("title").setValue(info.getTitle());
        DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId+1).child("course").setValue(info.getCourse());
        DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId+1).child("start").setValue(info.getStartTime());
        DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId+1).child("owner").setValue(MainUserSingleton.getInstance().getId());
        DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId+1).child("participants").child(MainUserSingleton.getInstance().getId()).setValue(true);
       DatabaseFactory.getAdaptedInstance().getReference("users").child(MainUserSingleton.getInstance().getId()).child("appointments").child(appointmentId + 1).setValue(true);

        Bundle bundle = new Bundle();

        bundle.putSerializable(CalendarActivity.UserTypeCode,"Real");
        Intent intent = new Intent(getApplicationContext(),CalendarActivity.class);
        intent.putExtras(bundle);
        try(ActivityScenario<CalendarActivity> ignored = ActivityScenario.launch(intent)) {

           sleep(3,SECONDS);
           Espresso.onView(withText("Details")).perform(ViewActions.click());
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
           sleep(3, SECONDS);

           try {
               getCourseTest(appointmentId + 1, newCourse);
               getTitleTest(appointmentId + 1, newTitle);
           } catch (InterruptedException ex) {

           }
       }
       DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId+1).setValue(null);

    }

    private void getTitleTest(String id, String newTitle) throws InterruptedException {
        ReentrantLock lock = new ReentrantLock();
        Condition cv = lock.newCondition();
        AtomicBoolean bool = new AtomicBoolean(false);
        AtomicReference<String> gotName = new AtomicReference<>("wrong name");
        lock.lock();
        try {
            new DatabaseAppointment(id).getTitleAndThen(
                    (name) -> {
                        lock.lock();
                        gotName.set(name);
                        bool.set(Boolean.TRUE);
                        cv.signal();
                        lock.unlock();
                    }
            );
            while(!bool.get())
                cv.await();

        } finally {
            lock.unlock();
            assertEquals(newTitle, gotName.get());
        }
    }

    private void getCourseTest(String id, String newCourse) throws InterruptedException {
        ReentrantLock lock = new ReentrantLock();
        Condition cv = lock.newCondition();
        AtomicBoolean bool = new AtomicBoolean(false);
        AtomicReference<String> gotName = new AtomicReference<>("wrong name");
        lock.lock();
        try {
            new DatabaseAppointment(id).getCourseAndThen(
                    (name) -> {
                        lock.lock();
                        gotName.set(name);
                        bool.set(Boolean.TRUE);
                        cv.signal();
                        lock.unlock();
                    }
            );
            while(!bool.get())
                cv.await();

        } finally {
            lock.unlock();
            assertEquals(newCourse, gotName.get());
        }
    }
}