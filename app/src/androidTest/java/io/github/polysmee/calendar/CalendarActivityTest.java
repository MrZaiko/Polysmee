package io.github.polysmee.calendar;

import android.content.Intent;
import android.provider.CalendarContract;

import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.intent.Intents;
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

import io.github.polysmee.R;
import io.github.polysmee.calendar.googlecalendarsync.CalendarUtilities;
import io.github.polysmee.database.DatabaseSingleton;
import io.github.polysmee.internet.connection.InternetConnection;
import io.github.polysmee.login.AuthenticationFactory;
import io.github.polysmee.login.MainUser;
import io.github.polysmee.znotification.AppointmentReminderNotification;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasData;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static androidx.test.espresso.intent.matcher.IntentMatchers.toPackage;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withHint;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed;
import static com.schibsted.spain.barista.interaction.BaristaClickInteractions.clickOn;
import static com.schibsted.spain.barista.interaction.BaristaClickInteractions.longClickOn;
import static com.schibsted.spain.barista.interaction.BaristaDialogInteractions.clickDialogPositiveButton;
import static com.schibsted.spain.barista.interaction.BaristaEditTextInteractions.writeTo;
import static com.schibsted.spain.barista.interaction.BaristaPickerInteractions.setDateOnPicker;
import static com.schibsted.spain.barista.interaction.BaristaScrollInteractions.scrollTo;
import static com.schibsted.spain.barista.interaction.BaristaSleepInteractions.sleep;
import static com.schibsted.spain.barista.interaction.BaristaViewPagerInteractions.swipeViewPagerBack;
import static com.schibsted.spain.barista.interaction.BaristaViewPagerInteractions.swipeViewPagerForward;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class CalendarActivityTest {

    private static final String username1 = "Youssef le dindon";
    private static final String userDescription1 = "Bonjour tout le monde !";
    private static final String appointmentTitle = "J'adore le surf";
    private static final String appointmentCourse = "SDP";
    private static final String appointmentId = "-lsdqrhrrdtisjhmf";
    //be care full the email has to be in lower case for the test to pass
    private static final String MAIN_USER_EMAIL = "calendaractivitytest@gmail.com";
    private static final int appointmentYear = 2022;
    private static final int appointmentMonth = 3;
    private static final int appointmentDay = 7;
    private static final SimpleDateFormat dayFormatter = new SimpleDateFormat("d");
    private static final SimpleDateFormat letterDayFormatter = new SimpleDateFormat("EEEE");
    private static Calendar startTime;

    @BeforeClass
    public static void setUp() throws Exception {
        startTime = Calendar.getInstance();
        startTime.set(appointmentYear, appointmentMonth, appointmentDay, 18, 3, 0);
        AppointmentReminderNotification.setIsNotificationSetterEnable(false);
        DatabaseSingleton.setTest();
        AuthenticationFactory.setTest();
        CalendarUtilities.setTest(true, false);
        InternetConnection.setManuallyInternetConnectionForTests(true);
        FirebaseApp.clearInstancesForTest();
        FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext());
        Tasks.await(AuthenticationFactory.getAdaptedInstance().createUserWithEmailAndPassword(MAIN_USER_EMAIL, "fakePassword"));
        DatabaseSingleton.getAdaptedInstance().getReference("users").child(MainUser.getMainUser().getId()).child("name").setValue(username1);
        DatabaseSingleton.getAdaptedInstance().getReference("users").child(MainUser.getMainUser().getId()).child("appointments").child(appointmentId).setValue(true);
        DatabaseSingleton.getAdaptedInstance().getReference("users").child(MainUser.getMainUser().getId()).child("description").setValue(userDescription1);
        DatabaseSingleton.getAdaptedInstance().getReference("courses").child(appointmentCourse).setValue(appointmentCourse);
    }


    @AfterClass
    public static void clean() {
        DatabaseSingleton.getAdaptedInstance().getReference().setValue(null);
    }

    @Before
    public void setTodayDateInDailyCalendar() {
        Calendar calendar = Calendar.getInstance();
        DailyCalendar.setDayEpochTimeAtMidnight(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE), false);
    }

    @Test
    public void allCalendarTest() {
        Intent intent = new Intent(getApplicationContext(), CalendarActivity.class);

        Calendar calendar = Calendar.getInstance();
        ActivityScenario<CalendarActivity> scenario = ActivityScenario.launch(intent);
        String title = "NewTitle";
        long startTime = calendar.getTimeInMillis() + 60 * 1000;
        CalendarAppointmentInfo info = new CalendarAppointmentInfo("SDP", "ClickMeBoi",
                startTime, 3600 * 6 * 1000, appointmentId + 5,0);
        MainUser.getMainUser().createNewUserAppointment(info.getStartTime(),
                info.getDuration(), info.getCourse(), info.getTitle(), false);
        sleep(3, SECONDS);
        clickOn(info.getTitle());
        sleep(1, SECONDS);
        scrollTo(R.id.appointmentCreationEditTxtAppointmentTitleSet);
        writeTo(R.id.appointmentCreationEditTxtAppointmentTitleSet, title);
        closeSoftKeyboard();
        clickOn(R.id.appointmentCreationbtnDone);
        sleep(2, SECONDS);
        assertDisplayed(title);

        longClickOn(title);
        Intents.init();
        clickDialogPositiveButton();
        intended(hasData(CalendarContract.Events.CONTENT_URI));
        intended(hasExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startTime));
        intended(hasExtra(CalendarContract.Events.TITLE, title));
        Intents.release();
        scenario.moveToState(Lifecycle.State.DESTROYED);
        scenario.close();
    }

    @Test
    public void clickingOnAnAppointmentLaunchesItsDetailsWhenItsBeforeItsTime() {
        Intent intent = new Intent(getApplicationContext(), CalendarActivity.class);
        try (ActivityScenario<CalendarActivity> ignored = ActivityScenario.launch(intent)) {
            Calendar calendar = Calendar.getInstance();
            Date date = new Date(DailyCalendar.getDayEpochTimeAtMidnight(false));
            //modifyingTitleIsSeenOnTheCalendar
            String title = "NewTitle";
            long startTime = calendar.getTimeInMillis() + 60 * 1000;
            CalendarAppointmentInfo info = new CalendarAppointmentInfo("SDP", "ClickMeBoi",
                    startTime, 3600 * 6 * 1000, appointmentId + 5,0);
            MainUser.getMainUser().createNewUserAppointment(info.getStartTime(),
                    info.getDuration(), info.getCourse(), info.getTitle(), false);
            sleep(3, SECONDS);
            clickOn(info.getTitle());
            sleep(1, SECONDS);
            scrollTo(R.id.appointmentCreationEditTxtAppointmentTitleSet);
            writeTo(R.id.appointmentCreationEditTxtAppointmentTitleSet, title);
            closeSoftKeyboard();
            clickOn(R.id.appointmentCreationbtnDone);
            sleep(2, SECONDS);
            assertDisplayed(title);

            //clickingOnAnAppointmentLaunchesItsDetailsWhenItsBeforeItsTime
            CalendarAppointmentInfo info1 = new CalendarAppointmentInfo("SDP", "ClickMe",
                    calendar.getTimeInMillis() + 60 * 1000, 3600 * 6 * 1000, appointmentId + 5,0);
            MainUser.getMainUser().createNewUserAppointment(info1.getStartTime(),
                    info1.getDuration(), info1.getCourse(), info1.getTitle(), false);
            sleep(3, SECONDS);
            scrollTo(info1.getTitle());
            clickOn(info1.getTitle());
            assertDisplayed(withHint(info1.getTitle()));
            assertDisplayed(withText(info1.getCourse()));
            pressBack();
            swipeViewPagerForward();
            sleep(2, SECONDS);
            assertDisplayed(withText(info1.getTitle()));
            swipeViewPagerBack();
            sleep(2, SECONDS);
            //writtenDateIsCorrectTest
            assertDisplayed(dayFormatter.format(date));
            assertDisplayed(letterDayFormatter.format(date));
            sleep(3, SECONDS);

            //addingAnAppointmentOnAnotherDayDisplaysItOnlyWhenChoosingThatDay

            MainUser.getMainUser().createNewUserAppointment(CalendarActivityTest.startTime.getTimeInMillis(),
                    3600, appointmentCourse, appointmentTitle, false);
            sleep(5, SECONDS);

            boolean thrown = false;
            try {
                onView(withText(appointmentTitle)).check(matches(isDisplayed()));
            } catch (NoMatchingViewException e) {
                thrown = true;
            }
            assertTrue(thrown);

            clickOn(R.id.activityCalendarMonthMyAppointments);
            setDateOnPicker(appointmentYear, appointmentMonth + 1, appointmentDay);
            sleep(2, SECONDS);
            assertDisplayed(appointmentTitle);
            sleep(2, SECONDS);


            //notificationButtonShouldOpenInvites
            sleep(2, SECONDS);
            try {
                clickOn(R.id.calendarMenuNotifications);
                sleep(1, SECONDS);
                assertDisplayed("Current invitations");
            } catch (Exception e) {
                openActionBarOverflowOrOptionsMenu(getApplicationContext());
                sleep(2, SECONDS);
                clickOn("Notifications");
                sleep(1, SECONDS);
                assertDisplayed("Current invitations");
            }
            pressBack();

            //exportCalendar
            sleep(2, SECONDS);
            Intents.init();
            try {
                clickOn(R.id.calendarMenuExport);
                sleep(1, SECONDS);
            } catch (Exception e) {
                openActionBarOverflowOrOptionsMenu(getApplicationContext());
                sleep(2, SECONDS);
                clickOn("Export");
                sleep(1, SECONDS);
            }
            intended(toPackage("io.github.polysmee"));
            Intents.release();
            pressBack();

            //clickingSettingsButtonLaunchesSettingsActivity
            sleep(2, SECONDS);
            try {
                clickOn(R.id.calendarMenuSettings);
                assertDisplayed("Appointments reminder settings");
            } catch (Exception e) {
                openActionBarOverflowOrOptionsMenu(getApplicationContext());
                sleep(2, SECONDS);
                clickOn("Settings");
                assertDisplayed("Appointments reminder settings");
            }
            pressBack();
            sleep(2, SECONDS);
            //profileButtonShouldOpenProfile
            clickOn(R.id.calendarMenuProfile);
            sleep(2, SECONDS);

            assertDisplayed(R.string.title_profile_user_name);
            assertDisplayed(username1);
            assertDisplayed(R.string.title_profile_user_description);
            assertDisplayed(userDescription1);
            assertDisplayed(R.string.title_profile_user_email);
            assertDisplayed(MAIN_USER_EMAIL);
            assertDisplayed(R.string.title_profile_main_user_friends);
        }
    }

}