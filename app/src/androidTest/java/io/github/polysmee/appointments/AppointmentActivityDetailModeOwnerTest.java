package io.github.polysmee.appointments;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.FirebaseApp;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Calendar;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import io.github.polysmee.R;
import io.github.polysmee.database.DatabaseFactory;
import io.github.polysmee.login.AuthenticationFactory;
import io.github.polysmee.login.MainUserSingleton;
import io.github.polysmee.znotification.AppointmentReminderNotificationSetupListener;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static com.schibsted.spain.barista.assertion.BaristaCheckedAssertions.assertChecked;
import static com.schibsted.spain.barista.assertion.BaristaClickableAssertions.assertClickable;
import static com.schibsted.spain.barista.assertion.BaristaClickableAssertions.assertNotClickable;
import static com.schibsted.spain.barista.assertion.BaristaEnabledAssertions.assertDisabled;
import static com.schibsted.spain.barista.assertion.BaristaEnabledAssertions.assertEnabled;
import static com.schibsted.spain.barista.assertion.BaristaHintAssertions.assertHint;
import static com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed;
import static com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertNotDisplayed;
import static com.schibsted.spain.barista.interaction.BaristaClickInteractions.clickOn;
import static com.schibsted.spain.barista.interaction.BaristaEditTextInteractions.writeTo;
import static com.schibsted.spain.barista.interaction.BaristaPickerInteractions.setDateOnPicker;
import static com.schibsted.spain.barista.interaction.BaristaPickerInteractions.setTimeOnPicker;
import static com.schibsted.spain.barista.interaction.BaristaScrollInteractions.scrollTo;
import static com.schibsted.spain.barista.interaction.BaristaSleepInteractions.sleep;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(JUnit4.class)
public class AppointmentActivityDetailModeOwnerTest {
    private static final String username1 = "Mathis L'utilisateur";
    private static final String id2 = "ppppppsdfkjhsfdlkisdfhl";
    private static final String username2 = "Sami L'imposteur";
    private static final String id3 = "eeeeeeefdisjpdfjsp";
    private static final String username3 = "Léo La fouine";

    private static final String appointmentId = "tttttdlsfjfsdpm";
    private static final String course = "SDP";
    private static final String title = "Aglouglou sur mon cheval";
    private static final long duration = 3600000;

    private static Calendar startTime, endTime;
    private final String dateFormat = "dd/MM/yyyy - HH:mm";

    private static final String newCourse = "Sweng";
    private static final String newTitle = "La valse sous l'eau";
    private static final long newDuration = 7200000;

    @BeforeClass
    public static void setUp() throws Exception {
        startTime = Calendar.getInstance();
        startTime.set(2022,4,22,18,3,0);
        endTime = (Calendar) startTime.clone();
        endTime.add(Calendar.MILLISECOND, (int) duration);
        AppointmentReminderNotificationSetupListener.setIsNotificationSetterEnable(false);
        DatabaseFactory.setTest();
        AuthenticationFactory.setTest();
        FirebaseApp.clearInstancesForTest();
        FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext());
        Tasks.await(AuthenticationFactory.getAdaptedInstance().createUserWithEmailAndPassword("AppointmentActivityDetailModeOwnerTest@gmail.com", "fakePassword"));
        DatabaseFactory.getAdaptedInstance().getReference("users").child(MainUserSingleton.getInstance().getId()).child("name").setValue(username1);
        DatabaseFactory.getAdaptedInstance().getReference("users").child(id2).child("name").setValue(username2);
        DatabaseFactory.getAdaptedInstance().getReference("users").child(id3).child("name").setValue(username3);

        DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId).child("title").setValue(title);
        DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId).child("course").setValue(course);
        DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId).child("start").setValue(startTime.getTimeInMillis());
        DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId).child("duration").setValue(duration);
        DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId).child("participants").child(MainUserSingleton.getInstance().getId()).setValue(true);
        DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId).child("participants").child(id2).setValue(true);
        DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId).child("banned").child(id3).setValue(true);
        DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId).child("owner").setValue(MainUserSingleton.getInstance().getId());
        DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId).child("private").setValue(true);
    }

    @AfterClass
    public static void clean() {
        DatabaseFactory.getAdaptedInstance().getReference().setValue(null);
    }

    /**
    @Test
    public void everyFieldAreCorrectlyDisplayedAndClickable() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), AppointmentActivity.class);
        intent.putExtra(AppointmentActivity.LAUNCH_MODE, AppointmentActivity.DETAIL_MODE);
        intent.putExtra(AppointmentActivity.APPOINTMENT_ID, appointmentId);

        try (ActivityScenario<AppointmentActivity> ignored = ActivityScenario.launch(intent)) {
            sleep(2, SECONDS);
            assertEnabled(R.id.appointmentCreationEditTxtAppointmentTitleSet);
            assertClickable(R.id.appointmentCreationStartTimeLayout);
            assertClickable(R.id.appointmentCreationEndTimeLayout);
            assertEnabled(R.id.appointmentCreationEditTxtAppointmentCourseSet);
            assertClickable(R.id.appointmentCreationPrivateSelector);

            assertHint(R.id.appointmentCreationEditTxtAppointmentTitleSet, title);
            assertHint(R.id.appointmentCreationEditTxtAppointmentCourseSet, course);
            assertDisplayed(DateFormat.format(dateFormat, startTime.getTime()).toString());
            assertDisplayed(DateFormat.format(dateFormat, endTime.getTime()).toString());
            assertChecked(R.id.appointmentCreationPrivateSelector);
            scrollTo(R.id.appointmentCreationTxtWarning);
            clickOn(R.id.appointmentCreationAddTextView);
            scrollTo(R.id.appointmentCreationShowBan);
            sleep(5, SECONDS);
            assertDisplayed(R.id.appointmentSettingsSearchAddLayout);
            assertDisplayed(username1);
            assertDisplayed(username2);
            scrollTo(R.id.appointmentCreationTxtWarning);
            clickOn(R.id.appointmentCreationBanTextView);
            scrollTo(R.id.appointmentCreationTxtWarning);
            assertDisplayed(R.id.appointmentSettingsSearchBanLayout);
            assertDisplayed(username3);

            assertDisplayed(R.id.appointmentCreationBottomBar);
        }
    }**/

    @Test
    public void doneButtonUpdateCorrectlyTheAppointmentInTheDatabase() throws InterruptedException, ExecutionException {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), AppointmentActivity.class);
        intent.putExtra(AppointmentActivity.LAUNCH_MODE, AppointmentActivity.DETAIL_MODE);
        intent.putExtra(AppointmentActivity.APPOINTMENT_ID, appointmentId);

        ActivityScenario<AppointmentActivity> scenario = ActivityScenario.launch(intent);

        sleep(2, SECONDS);

        //COURSE
        scrollTo(R.id.appointmentCreationEditTxtAppointmentCourseSet);
        writeTo(R.id.appointmentCreationEditTxtAppointmentCourseSet, newCourse);
        closeSoftKeyboard();

        //TITLE
        scrollTo(R.id.appointmentCreationEditTxtAppointmentTitleSet);
        writeTo(R.id.appointmentCreationEditTxtAppointmentTitleSet, newTitle);
        closeSoftKeyboard();

        //START TIME
        scrollTo(R.id.appointmentCreationStartTimeLayout);
        clickOn(R.id.appointmentCreationStartTimeLayout);
        setDateOnPicker(2022, 3, 23);
        setTimeOnPicker(17, 2);

        //END TIME
        scrollTo(R.id.appointmentCreationEndTimeLayout);
        clickOn(R.id.appointmentCreationEndTimeLayout);
        setDateOnPicker(2022, 3, 23);
        setTimeOnPicker(19, 2);

        //PRIVATE = false
        scrollTo(R.id.appointmentCreationShowAdd);
        clickOn(R.id.appointmentCreationPrivateSelector);

        clickOn(R.id.appointmentCreationbtnDone);

        Thread.sleep(2000);
        scenario.close();

        HashMap apt = (HashMap) Tasks.await(DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId).get()).getValue();

        assertEquals(newTitle, apt.get("title"));
        assertTrue(((HashMap) apt.get("banned")).containsKey(id3));
        assertTrue(((HashMap) apt.get("participants")).containsKey(id2));
        assertEquals(newCourse, apt.get("course"));
        assertFalse(((boolean) apt.get("private")));
        assertEquals(newDuration, ((long) apt.get("duration")), 1000);
        Calendar expectedDate = Calendar.getInstance();
        expectedDate.set(2022, 2, 23, 17, 2, 0);
        assertEquals(expectedDate.getTimeInMillis(), ((long) apt.get("start")), 1000);

        DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId).child("title").setValue(title);
        DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId).child("course").setValue(course);
        DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId).child("start").setValue(startTime.getTimeInMillis());
        DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId).child("duration").setValue(duration);
        DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId).child("participants").child(MainUserSingleton.getInstance().getId()).setValue(true);
        DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId).child("participants").child(id2).setValue(true);
        DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId).child("banned").child(id3).setValue(true);
        DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId).child("owner").setValue(MainUserSingleton.getInstance().getId());
        DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId).child("private").setValue(true);

        Thread.sleep(2000);
    }
}
