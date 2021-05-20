package io.github.polysmee.settings;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.FirebaseApp;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import io.github.polysmee.calendar.googlecalendarsync.CalendarUtilities;
import io.github.polysmee.database.DatabaseFactory;
import io.github.polysmee.login.AuthenticationFactory;
import io.github.polysmee.login.MainUser;
import io.github.polysmee.znotification.AppointmentReminderNotification;

import static com.schibsted.spain.barista.interaction.BaristaSleepInteractions.sleep;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertEquals;

public class MainUserInfoDataStoreTest {
    private final static String userEmail = "MainUserInfoDataStoreTest@gmail.com";
    private final static String userName = "MainUserInfoDataStoreTest";
    private final static String userDescription = "MainUserInfoDataStoreTest description";
    private final static String userPassword = "fakePassword";
    @Rule
    public ActivityScenarioRule<SettingsActivity> testRule = new ActivityScenarioRule<SettingsActivity>(SettingsActivity.class);

    @BeforeClass
    public static void setUp() throws Exception {
        AppointmentReminderNotification.setIsNotificationSetterEnable(false);
        DatabaseFactory.setTest();
        AuthenticationFactory.setTest();
        CalendarUtilities.setTest(true, false);
        FirebaseApp.clearInstancesForTest();
        FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext());
        Tasks.await(AuthenticationFactory.getAdaptedInstance().createUserWithEmailAndPassword(userEmail, userPassword));
        DatabaseFactory.getAdaptedInstance().getReference("users").child(MainUser.getMainUser().getId()).child("name").setValue(userName);
    }

    @AfterClass
    public static void clean() {
        DatabaseFactory.getAdaptedInstance().getReference().setValue(null);
    }

    public static void testNameDatabase(String value) throws Exception {
        sleep(1, SECONDS);
        String name = (String) Tasks.await(DatabaseFactory.getAdaptedInstance().getReference().child("users")
                .child(MainUser.getMainUser().getId()).child("name").get()).getValue();
        assertEquals(value, name);
    }


    private static void testDescriptionDatabase(String value) throws Exception{
        sleep(1, SECONDS);
        String description = (String) Tasks.await(DatabaseFactory.getAdaptedInstance().getReference().child("users")
                .child(MainUser.getMainUser().getId()).child("description").get()).getValue();
        assertEquals(value, description);
    }

    @Test
    public void putString() throws Exception {
        MainUserInfoDataStore mainUserInfoDataStore = new MainUserInfoDataStore();
        String nameToPut = "name change test";
        mainUserInfoDataStore.putString(MainUserInfoDataStore.PREFERENCE_KEY_MAIN_USER_NAME, nameToPut);
        testNameDatabase(nameToPut);
        mainUserInfoDataStore.putString(MainUserInfoDataStore.PREFERENCE_KEY_MAIN_USER_NAME, userName);
        testNameDatabase(userName);
        String descriptionToPut = "description change test";
        mainUserInfoDataStore.putString(MainUserInfoDataStore.PREFERENCE_KEY_MAIN_USER_DESCRIPTION, descriptionToPut);
        testDescriptionDatabase(descriptionToPut);
        mainUserInfoDataStore.putString(MainUserInfoDataStore.PREFERENCE_KEY_MAIN_USER_DESCRIPTION,userDescription);
        testDescriptionDatabase(userDescription);
        mainUserInfoDataStore.putString("efse", nameToPut);
        testNameDatabase(userName);
        mainUserInfoDataStore.putString(MainUserInfoDataStore.PREFERENCE_KEY_MAIN_USER_EMAIL, nameToPut);
        testNameDatabase(userName);
        mainUserInfoDataStore.putString("fkesjnfejsf", nameToPut);
        testNameDatabase(userName);;
    }

    @Test
    public void getString() {
        MainUserInfoDataStore mainUserInfoDataStore = new MainUserInfoDataStore();
        assertEquals("", mainUserInfoDataStore.getString(MainUserInfoDataStore.PREFERENCE_KEY_MAIN_USER_NAME, "test"));
        assertEquals("", mainUserInfoDataStore.getString(MainUserInfoDataStore.PREFERENCE_KEY_MAIN_USER_EMAIL, "test"));
        assertEquals("", mainUserInfoDataStore.getString(MainUserInfoDataStore.PREFERENCE_KEY_MAIN_USER_DESCRIPTION, "test"));
        assertEquals("", mainUserInfoDataStore.getString("jfnsejfnes", "test"));

    }
}