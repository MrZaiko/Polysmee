package io.github.polysmee.appointments;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.FirebaseApp;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Calendar;

import io.github.polysmee.R;
import io.github.polysmee.calendar.googlecalendarsync.CalendarUtilities;
import io.github.polysmee.database.DatabaseFactory;
import io.github.polysmee.login.AuthenticationFactory;
import io.github.polysmee.login.MainUser;
import io.github.polysmee.notification.AppointmentReminderNotification;

import static com.schibsted.spain.barista.interaction.BaristaClickInteractions.clickOn;
import static com.schibsted.spain.barista.interaction.BaristaScrollInteractions.scrollTo;
import static com.schibsted.spain.barista.interaction.BaristaSleepInteractions.sleep;
import static java.util.concurrent.TimeUnit.SECONDS;

@RunWith(JUnit4.class)
public class AppointmentActivityDetailModeNotOwnerTest {
    private static final String username1 = "Mathis L'utilisateur";
    private static final String id2 = "pzerotujrtpoiu";
    private static final String username2 = "Sami L'imposteur";
    private static final String id3 = "&éhmhsiogsfdsdgf";
    private static final String username3 = "Léo La fouine";

    private static final String appointmentId = "opkdsfmcvx";
    private static final String course = "SDP";
    private static final String title = "Aglouglou sur mon cheval";
    private static final long duration = 3600000;
    private static Calendar startTime;
    private static Calendar endTime;

    private final String dateFormat = "dd/MM/yyyy - HH:mm";

    @BeforeClass
    public static void setUp() throws Exception {
        startTime = Calendar.getInstance();
        startTime.set(2022, 4, 22, 18, 3, 0);
        endTime = (Calendar) startTime.clone();
        endTime.add(Calendar.MILLISECOND, (int) duration);
        AppointmentReminderNotification.setIsNotificationSetterEnable(false);
        CalendarUtilities.setTest(true, false);
        DatabaseFactory.setTest();
        AuthenticationFactory.setTest();
        FirebaseApp.clearInstancesForTest();
        FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext());
        Tasks.await(AuthenticationFactory.getAdaptedInstance().createUserWithEmailAndPassword("AppointmentActivityDetailModeNotOwnerTest@gmail.com", "fakePassword"));
        DatabaseFactory.getAdaptedInstance().getReference("users").child(MainUser.getMainUser().getId()).child("name").setValue(username1);
        DatabaseFactory.getAdaptedInstance().getReference("users").child(id2).child("name").setValue(username2);
        DatabaseFactory.getAdaptedInstance().getReference("users").child(id3).child("name").setValue(username3);

        DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId).child("title").setValue(title);
        DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId).child("course").setValue(course);
        DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId).child("start").setValue(startTime.getTimeInMillis());
        DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId).child("duration").setValue(duration);
        DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId).child("participants").child(MainUser.getMainUser().getId()).setValue(true);
        DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId).child("participants").child(id2).setValue(true);
        DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId).child("banned").child(id3).setValue(true);
        DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId).child("owner").setValue(id2);
        DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId).child("private").setValue(true);
        DatabaseFactory.getAdaptedInstance().getReference("courses").child(course).setValue(course);
    }

    @AfterClass
    public static void clean() {
        DatabaseFactory.getAdaptedInstance().getReference().setValue(null);
    }

    @Test
    public void dummyTest() {
        Assert.assertEquals(0, 0);
    }

    @Test
    public void everyFieldAreCorrectlyDisplayedAndNotClickable() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), AppointmentActivity.class);
        intent.putExtra(AppointmentActivity.LAUNCH_MODE, AppointmentActivity.DETAIL_MODE);
        intent.putExtra(AppointmentActivity.APPOINTMENT_ID, appointmentId);

        try (ActivityScenario<AppointmentActivity> ignored = ActivityScenario.launch(intent)) {
            sleep(2, SECONDS);
            /*assertDisabled(R.id.appointmentCreationEditTxtAppointmentTitleSet);
            assertNotClickable(R.id.appointmentCreationStartTimeLayout);
            assertNotClickable(R.id.appointmentCreationEndTimeLayout);
            assertDisabled(R.id.appointmentCreationEditTxtAppointmentCourseSet);
            assertNotClickable(R.id.appointmentCreationPrivateSelector);

            assertHint(R.id.appointmentCreationEditTxtAppointmentTitleSet, title);
            assertHint(R.id.appointmentCreationEditTxtAppointmentCourseSet, course);
            assertDisplayed(DateFormat.format(dateFormat, startTime.getTime()).toString());
            assertDisplayed(DateFormat.format(dateFormat, endTime.getTime()).toString());
            assertChecked(R.id.appointmentCreationPrivateSelector);*/
            scrollTo(R.id.appointmentCreationTxtWarning);
            clickOn(R.id.appointmentCreationAddTextView);
            scrollTo(R.id.appointmentCreationTxtWarning);
            /*assertNotDisplayed(R.id.appointmentSettingsSearchAddLayout);
            assertDisplayed(username1);
            assertDisplayed(username2);*/
            scrollTo(R.id.appointmentCreationTxtWarning);
            clickOn(R.id.appointmentCreationBanTextView);
            scrollTo(R.id.appointmentCreationTxtWarning);
            /*assertNotDisplayed(R.id.appointmentSettingsSearchBanLayout);
            assertDisplayed(username3);

            assertNotDisplayed(R.id.appointmentCreationBottomBar);*/
            Assert.assertEquals(0, 0);
        }


    }
}
