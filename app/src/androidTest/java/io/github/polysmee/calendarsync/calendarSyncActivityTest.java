package io.github.polysmee.calendarsync;

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

import java.util.concurrent.ExecutionException;

import io.github.polysmee.R;
import io.github.polysmee.calendar.googlecalendarsync.CalendarUtilities;
import io.github.polysmee.calendar.googlecalendarsync.GoogleCalendarSyncActivity;
import io.github.polysmee.database.DatabaseFactory;
import io.github.polysmee.internet.connection.InternetConnection;
import io.github.polysmee.login.AuthenticationFactory;
import io.github.polysmee.login.LoginActivity;
import io.github.polysmee.login.MainUser;
import io.github.polysmee.notification.AppointmentReminderNotification;

import static com.schibsted.spain.barista.assertion.BaristaClickableAssertions.assertClickable;
import static com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed;
import static com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertNotDisplayed;
import static com.schibsted.spain.barista.interaction.BaristaClickInteractions.clickOn;
import static com.schibsted.spain.barista.interaction.BaristaSleepInteractions.sleep;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertEquals;

@RunWith(JUnit4.class)
public class calendarSyncActivityTest {
    private static final String username1 = "Youssef le dindon";
    private static final String email1 = "calendarsyncactivitytest@gmail.com";

    @BeforeClass
    public static void setUp() throws Exception {
        AppointmentReminderNotification.setIsNotificationSetterEnable(false);
        DatabaseFactory.setTest();
        AuthenticationFactory.setTest();
        CalendarUtilities.setTest(true, false);
        InternetConnection.setManuallyInternetConnectionForTests(true);
        FirebaseApp.clearInstancesForTest();
        FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext());
        Tasks.await(AuthenticationFactory.getAdaptedInstance().createUserWithEmailAndPassword(email1, "fakePassword"));
        DatabaseFactory.getAdaptedInstance().getReference("users").child(MainUser.getMainUser().getId()).child("name").setValue(username1);
        DatabaseFactory.getAdaptedInstance().getReference("users").child(MainUser.getMainUser().getId()).child("calendarId").setValue("");
    }


    @AfterClass
    public static void clean() {
        DatabaseFactory.getAdaptedInstance().getReference().setValue(null);
    }

    @Test
    public void calendarSyncTest() throws ExecutionException, InterruptedException {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), GoogleCalendarSyncActivity.class);
        try (ActivityScenario<LoginActivity> ignored = ActivityScenario.launch(intent)) {
            assertClickable(R.id.calendarSyncActivitySyncButton);
            clickOn(R.id.calendarSyncActivitySyncButton);

            sleep(2, SECONDS);

            assertNotDisplayed(R.id.calendarSyncActivitySyncButton);
            assertDisplayed(R.id.calendarSyncActivityCalendarIdText);
            assertDisplayed(R.id.calendarSyncActivityCopyButton);
            assertClickable(R.id.calendarSyncActivityCopyButton);
            assertDisplayed(R.id.calendarSyncActivityDeleteButton);
            assertClickable(R.id.calendarSyncActivityDeleteButton);

            clickOn(R.id.calendarSyncActivityCopyButton);

            String calendarID1 = (String) Tasks.await(DatabaseFactory.getAdaptedInstance().getReference("users").child(MainUser.getMainUser().getId()).child("calendarId").get()).getValue();
            assertEquals(email1, calendarID1);

            clickOn(R.id.calendarSyncActivityDeleteButton);

            sleep(2, SECONDS);
            assertDisplayed(R.id.calendarSyncActivitySyncButton);
            assertNotDisplayed(R.id.calendarSyncActivityCalendarIdText);
            assertNotDisplayed(R.id.calendarSyncActivityCopyButton);
            assertNotDisplayed(R.id.calendarSyncActivityDeleteButton);

            String calendarID2 = (String) Tasks.await(DatabaseFactory.getAdaptedInstance().getReference("users").child(MainUser.getMainUser().getId()).child("calendarId").get()).getValue();
            assertEquals("", calendarID2);

        }
    }
}
