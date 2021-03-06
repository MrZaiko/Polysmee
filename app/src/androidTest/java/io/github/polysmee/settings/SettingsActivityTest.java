package io.github.polysmee.settings;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.FirebaseApp;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.github.polysmee.R;
import io.github.polysmee.calendar.googlecalendarsync.CalendarUtilities;
import io.github.polysmee.database.DatabaseSingleton;
import io.github.polysmee.login.AuthenticationSingleton;
import io.github.polysmee.znotification.AppointmentReminderNotification;

import static com.schibsted.spain.barista.interaction.BaristaClickInteractions.clickOn;

@RunWith(AndroidJUnit4.class)
public class SettingsActivityTest {


    private final static String userEmail = "SettingsActivityTest@gmail.com";
    private final static String userName = "SettingsActivityTest";
    private final static String userPassword = "fakePassword";


    @Rule
    public ActivityScenarioRule<SettingsActivity> testRule = new ActivityScenarioRule<SettingsActivity>(SettingsActivity.class);

    @BeforeClass
    public static void setUp() throws Exception {
        AppointmentReminderNotification.setIsNotificationSetterEnable(false);
        DatabaseSingleton.setLocal();
        CalendarUtilities.setTest(true, false);
        AuthenticationSingleton.setLocal();
        FirebaseApp.clearInstancesForTest();
        FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext());
        Tasks.await(AuthenticationSingleton.getAdaptedInstance().createUserWithEmailAndPassword(userEmail, userPassword));
    }

    @Test
    public void appointmentsReminderSettingFragmentsIsLaunchWhenClickOnMain() {
        SettingsMainFragmentTest.checkFragmentIsDisplayed();
        clickOn(R.string.title_settings_appointments_reminder);
        SettingsAppointmentsReminderFragmentTest.checkFragmentIsDisplayed();
    }

}