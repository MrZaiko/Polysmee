package io.github.polysmee.notification;

import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationManagerCompat;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.Until;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.FirebaseApp;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeUnit;

import io.github.polysmee.R;
import io.github.polysmee.calendar.CalendarActivity;
import io.github.polysmee.calendar.googlecalendarsync.CalendarUtilities;
import io.github.polysmee.database.DatabaseSingleton;
import io.github.polysmee.login.AuthenticationSingleton;
import io.github.polysmee.login.MainUser;

import static com.schibsted.spain.barista.interaction.BaristaSleepInteractions.sleep;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class AppointmentReminderNotificationTest {

    private final static long TIMEOUT = SECONDS.toMillis(10);
    private final static String username = "UsernameAppointmentReminderNotificationTest";
    private final static String appointmentId = "AppointmentIdAppointmentReminderNotificationTest";
    private static final UiDevice uiDevice =
            UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
    private static final Context context = ApplicationProvider.getApplicationContext();
    private static final String EXPECTED_APP_NAME = context.getString(R.string.app_name);
    private final static String NOTIFICATION_TEXT =
            context.getResources().getString(R.string.text_appointment_reminder_notification_notification);
    private final static String NOTIFICATION_TITLE =
            context.getResources().getString(R.string.title_appointment_reminder_notification_notification);

    @Rule
    public ActivityScenarioRule<CalendarActivity> testRule =
            new ActivityScenarioRule<>(CalendarActivity.class);

    @BeforeClass
    public static void setUp() throws Exception {
        DatabaseSingleton.setLocal();
        AuthenticationSingleton.setLocal();
        AppointmentReminderNotification.setIsNotificationSetterEnable(true);
        CalendarUtilities.setTest(true, false);
        FirebaseApp.clearInstancesForTest();
        FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext());
        Tasks.await(AuthenticationSingleton.getAdaptedInstance().createUserWithEmailAndPassword(
                "AppointmentReminderTest@gmail.com", "fakePassword"));
        DatabaseSingleton.getAdaptedInstance().getReference("users").child(MainUser.getMainUser()
                .getId()).child("name").setValue(username);
    }

    @Test
    public void appointmentReminderNotificationSyncedOnline() {
        long timeOfAppointment = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(5);
        DatabaseSingleton.getAdaptedInstance().getReference("appointments").child(appointmentId)
                .child("start").setValue(timeOfAppointment);
        DatabaseSingleton.getAdaptedInstance().getReference("users").child(MainUser.getMainUser()
                .getId()).child("appointments").child(appointmentId).setValue("");
        reminderNotificationPresent();
    }

    @Before
    @After
    public void resetStateNotification() {
        NotificationManagerCompat.from(context).cancelAll();
    }

    @Test
    public void notification_launch_with_good_title_and_text() {
        AppointmentReminderNotificationPublisher publisher =
                new AppointmentReminderNotificationPublisher();
        Intent intent = new Intent(context, AppointmentReminderNotificationPublisher.class);
        publisher.onReceive(context, intent);
        reminderNotificationPresent();
    }

    //assert that a notification reminder is present in the system at return notification layout
    // will be closed
    public static void reminderNotificationPresent() {
        uiDevice.openNotification();
        assertTrue(uiDevice.wait(Until.hasObject(By.textStartsWith(EXPECTED_APP_NAME)), TIMEOUT));
        assertNotNull(uiDevice.findObject(By.text(NOTIFICATION_TEXT)));
        assertNotNull(uiDevice.findObject(By.text(NOTIFICATION_TITLE)));
        closeNotification();
    }

    private static void closeNotification() {
        Intent closeIntent = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        context.sendBroadcast(closeIntent);
    }


}