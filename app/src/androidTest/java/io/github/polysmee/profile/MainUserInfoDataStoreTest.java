package io.github.polysmee.profile;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.FirebaseApp;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import io.github.polysmee.calendar.googlecalendarsync.CalendarUtilities;
import io.github.polysmee.database.DatabaseSingleton;
import io.github.polysmee.login.AuthenticationSingleton;
import io.github.polysmee.login.MainUser;
import io.github.polysmee.settings.SettingsActivity;
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
        DatabaseSingleton.setLocal();
        AuthenticationSingleton.setLocal();
        CalendarUtilities.setTest(true, false);
        FirebaseApp.clearInstancesForTest();
        FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext());
        Tasks.await(AuthenticationSingleton.getAdaptedInstance().createUserWithEmailAndPassword(userEmail, userPassword));
    }

    @AfterClass
    public static void clean() {
        DatabaseSingleton.getAdaptedInstance().getReference().setValue(null);
    }



    private static void testNameDatabase(String value) throws Exception {
        // necessary otherwise not enough time to set the value
        sleep(1, SECONDS);
        String name = (String) Tasks.await(DatabaseSingleton.getAdaptedInstance().getReference().child("users")
                .child(MainUser.getMainUser().getId()).child("name").get()).getValue();
        assertEquals(value, name);
    }

    private static void testDescriptionDatabase(String value) throws Exception{
        sleep(1, SECONDS);
        String description = (String) Tasks.await(DatabaseSingleton.getAdaptedInstance().getReference().child("users")
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
        mainUserInfoDataStore.putString("fkesjnfejsf", nameToPut);
        testNameDatabase(userName);
        mainUserInfoDataStore.putString("fkesjnfejsf", null);
        testNameDatabase(userName);
    }

    @Test
    public void getString() {
        MainUserInfoDataStore mainUserInfoDataStore = new MainUserInfoDataStore();
        assertEquals("", mainUserInfoDataStore.getString(MainUserInfoDataStore.PREFERENCE_KEY_MAIN_USER_NAME, "test"));
        assertEquals("", mainUserInfoDataStore.getString(MainUserInfoDataStore.PREFERENCE_KEY_MAIN_USER_DESCRIPTION, "test"));
        assertEquals("", mainUserInfoDataStore.getString("jfnsejfnes", "test"));

    }
}