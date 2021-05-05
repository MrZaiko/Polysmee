package io.github.polysmee.calendar;


import android.content.Intent;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.FirebaseApp;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import io.github.polysmee.R;
import io.github.polysmee.calendar.fragments.CalendarActivityPublicAppointmentsFragment;
import io.github.polysmee.database.DatabaseFactory;
import io.github.polysmee.login.AuthenticationFactory;
import io.github.polysmee.login.MainUser;
import io.github.polysmee.znotification.AppointmentReminderNotificationSetupListener;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.swipeLeft;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed;
import static com.schibsted.spain.barista.interaction.BaristaClickInteractions.clickOn;
import static com.schibsted.spain.barista.interaction.BaristaEditTextInteractions.writeTo;
import static com.schibsted.spain.barista.interaction.BaristaPickerInteractions.setDateOnPicker;
import static com.schibsted.spain.barista.interaction.BaristaSleepInteractions.sleep;
import static java.util.concurrent.TimeUnit.SECONDS;

@RunWith(AndroidJUnit4.class)
public class CalendarActivityPublicAppointmentsFragmentTest {

    private static int appointmentYear = 2042;
    private static int appointmentMonth = 10;
    private static int appointmentDay = 27;


    private static final String appointmentId = "ukcfjsqmcutn";
    private static final String username1 = "Youssef le gentil";
    private static String id2 = "uzeyazfedst";
    private static final String username2 = "amogus";

    private static final SimpleDateFormat dayFormatter = new SimpleDateFormat("d");
    private static final SimpleDateFormat letterDayFormatter = new SimpleDateFormat("EEEE");
    private static Calendar startTime;

    @BeforeClass
    public static void setUp() throws Exception {

        startTime = Calendar.getInstance();
        startTime.set(appointmentYear,appointmentMonth,appointmentDay,18,3,0);
        AppointmentReminderNotificationSetupListener.setIsNotificationSetterEnable(false);

        DatabaseFactory.setTest();
        AuthenticationFactory.setTest();
        FirebaseApp.clearInstancesForTest();
        FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext());

        Tasks.await(AuthenticationFactory.getAdaptedInstance().createUserWithEmailAndPassword("CalendarActivityPublicAppointmentsFragmentTest@gmail.com", "fakePassword"));
        DatabaseFactory.getAdaptedInstance().getReference("users").child(MainUser.getMainUser().getId()).child("name").setValue(username1);
        DatabaseFactory.getAdaptedInstance().getReference("users").child(id2).child("name").setValue(username2);
        DatabaseFactory.getAdaptedInstance().getReference("courses").child("SDP").setValue("SDP");
        DatabaseFactory.getAdaptedInstance().getReference("courses").child("ICG").setValue("ICG");
    }

    @AfterClass
    public static void clean() {
        DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId).child("private").setValue(true);
        DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId+1).child("private").setValue(true);
        DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId+2).child("private").setValue(true);
        DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId+3).child("private").setValue(true);
    }


    @Before
    public void setupDailyCalendar(){
        Calendar calendar = Calendar.getInstance();
        DailyCalendar.setDayEpochTimeAtMidnight(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DATE),true);
    }

    @Test
    public void writtenDateIsCorrectPublicAppointments(){
        Date date = new Date(DailyCalendar.getDayEpochTimeAtMidnight(true));
        FragmentScenario.launchInContainer(CalendarActivityPublicAppointmentsFragment.class);
        sleep(3, TimeUnit.SECONDS);
        assertDisplayed(dayFormatter.format(date));
        assertDisplayed(letterDayFormatter.format(date));
    }



    @Test
    public void anotherUsersAppointmentIsVisible(){
        CalendarAppointmentInfo calendarAppointmentInfo = new CalendarAppointmentInfo("SDP","amogus",DailyCalendar.getDayEpochTimeAtMidnight(true),60,appointmentId);
        addAppointmentOtherUser(calendarAppointmentInfo);
        FragmentScenario.launchInContainer(CalendarActivityPublicAppointmentsFragment.class);
        sleep(5,TimeUnit.SECONDS);
        assertDisplayed(calendarAppointmentInfo.getTitle());

        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
        Date startDate = new Date(calendarAppointmentInfo.getStartTime());
        Date endDate = new Date((calendarAppointmentInfo.getStartTime()+calendarAppointmentInfo.getDuration()));
        assertDisplayed(formatter.format(startDate) + " - " + formatter.format(endDate));
        assertDisplayed("Join");
    }

    @Test
    public void choosingAnotherDateInPublicAppointmentsDateChangesDisplayedDate(){
        FragmentScenario.launchInContainer(CalendarActivityPublicAppointmentsFragment.class);
        sleep(3,TimeUnit.SECONDS);

        clickOn(R.id.todayDatePublicAppointmentsCalendarActivity);
        setDateOnPicker(appointmentYear, appointmentMonth, appointmentDay);
        long epochTimeToday = DailyCalendar.getDayEpochTimeAtMidnight(true);
        Date date = new Date(epochTimeToday);
        assertDisplayed(dayFormatter.format(date));
        assertDisplayed(letterDayFormatter.format(date));
    }

    @Test
    public void addingAnAppointmentOnAnotherDayDisplaysItOnlyWhenChoosingThatDay(){

        CalendarAppointmentInfo calendarAppointmentInfo = new CalendarAppointmentInfo("SDP","BonjourBing",startTime.getTimeInMillis(),60,appointmentId+1);
        addAppointmentOtherUser(calendarAppointmentInfo);
        FragmentScenario.launchInContainer(CalendarActivityPublicAppointmentsFragment.class);
        sleep(3,TimeUnit.SECONDS);


        clickOn(R.id.todayDatePublicAppointmentsCalendarActivity);
        setDateOnPicker(appointmentYear, appointmentMonth+1, appointmentDay);
        sleep(3,SECONDS);
        assertDisplayed(calendarAppointmentInfo.getTitle());

    }

    @Test
    public void filterButtonLeavesOnlyAppointmentWithCorrespondingCourse(){
        CalendarAppointmentInfo calendarAppointmentInfo = new CalendarAppointmentInfo("SDP","BonjourBing1",DailyCalendar.getDayEpochTimeAtMidnight(true),60,appointmentId+2);
        CalendarAppointmentInfo calendarAppointmentInfo2 = new CalendarAppointmentInfo("ICG","BonjourBing2",DailyCalendar.getDayEpochTimeAtMidnight(true),60,appointmentId+3);
        addAppointmentOtherUser(calendarAppointmentInfo);
        addAppointmentOtherUser(calendarAppointmentInfo2);

        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), CalendarActivity.class);

        try (ActivityScenario<CalendarActivity> ignored = ActivityScenario.launch(intent)) {
            sleep(1,TimeUnit.SECONDS);

            //clickOn("PUBLIC APPOINTMENTS");
            onView(withId(R.id.calendarActivityPager)).perform(swipeLeft());
            sleep(1, SECONDS);

            writeTo(R.id.calendarActivityPublicAppointmentsEditTxtCourse, "apsdijf");
            closeSoftKeyboard();
            clickOn(R.id.calendarActivityPublicAppointmentsFilterBtn);
            assertDisplayed("OK");
            clickOn("OK");
            writeTo(R.id.calendarActivityPublicAppointmentsEditTxtCourse, "SDP");
            closeSoftKeyboard();
            clickOn(R.id.calendarActivityPublicAppointmentsFilterBtn);
            sleep(1, SECONDS);
            assertDisplayed("BonjourBing1");
            onView(withText("BonjourBing2")).check(doesNotExist());
        }

    }

    private void addAppointmentOtherUser(CalendarAppointmentInfo calendarAppointmentInfo){
        DatabaseFactory.getAdaptedInstance().getReference("appointments").child(calendarAppointmentInfo.getId()).child("id").setValue(calendarAppointmentInfo.getId());
        DatabaseFactory.getAdaptedInstance().getReference("appointments").child(calendarAppointmentInfo.getId()).child("duration").setValue(calendarAppointmentInfo.getDuration());
        DatabaseFactory.getAdaptedInstance().getReference("appointments").child(calendarAppointmentInfo.getId()).child("private").setValue(false);
        DatabaseFactory.getAdaptedInstance().getReference("appointments").child(calendarAppointmentInfo.getId()).child("owner").setValue(id2);
        DatabaseFactory.getAdaptedInstance().getReference("appointments").child(calendarAppointmentInfo.getId()).child("participants").child(id2).setValue(true);
        DatabaseFactory.getAdaptedInstance().getReference("appointments").child(calendarAppointmentInfo.getId()).child("title").setValue(calendarAppointmentInfo.getTitle());
        DatabaseFactory.getAdaptedInstance().getReference("appointments").child(calendarAppointmentInfo.getId()).child("course").setValue(calendarAppointmentInfo.getCourse());
        DatabaseFactory.getAdaptedInstance().getReference("appointments").child(calendarAppointmentInfo.getId()).child("start").setValue(calendarAppointmentInfo.getStartTime());
    }

}
