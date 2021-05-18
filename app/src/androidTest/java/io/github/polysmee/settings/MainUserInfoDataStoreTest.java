package io.github.polysmee.settings;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.FirebaseApp;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

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
    private final static String userPassword = "fakePassword";

    @Rule
    public ActivityScenarioRule<SettingsActivity> testRule = new ActivityScenarioRule<SettingsActivity>(SettingsActivity.class);

    @BeforeClass
    public static void setUp() throws Exception {
        AppointmentReminderNotification.setIsNotificationSetterEnable(false);
        DatabaseFactory.setTest();
        AuthenticationFactory.setTest();
        FirebaseApp.clearInstancesForTest();
        FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext());
        Tasks.await(AuthenticationFactory.getAdaptedInstance().createUserWithEmailAndPassword(userEmail, userPassword));
        DatabaseFactory.getAdaptedInstance().getReference("users").child(MainUser.getMainUser().getId()).child("name").setValue(userName);
    }


    public static void testNameDatabase(String value) throws Exception {
        sleep(1, SECONDS);
        String name = (String) Tasks.await(DatabaseFactory.getAdaptedInstance().getReference().child("users")
                .child(MainUser.getMainUser().getId()).child("name").get()).getValue();
        assertEquals(value, name);
    }

    @Test
    public void putString() throws Exception {
        MainUserInfoDataStore mainUserInfoDataStore = new MainUserInfoDataStore();
        String stringToPut = "name change test";
        mainUserInfoDataStore.putString(MainUserInfoDataStore.PREFERENCE_KEY_MAIN_USER_NAME, stringToPut);
        testNameDatabase(stringToPut);
        mainUserInfoDataStore.putString(MainUserInfoDataStore.PREFERENCE_KEY_MAIN_USER_NAME, userName);
        testNameDatabase(userName);
        mainUserInfoDataStore.putString("efse", stringToPut);
        testNameDatabase(userName);
        mainUserInfoDataStore.putString(MainUserInfoDataStore.PREFERENCE_KEY_MAIN_USER_EMAIL, stringToPut);
        testNameDatabase(userName);
        mainUserInfoDataStore.putString("fkesjnfejsf", stringToPut);
        testNameDatabase(userName);
    }

    @Test
    public void getString() {
        MainUserInfoDataStore mainUserInfoDataStore = new MainUserInfoDataStore();
        assertEquals("", mainUserInfoDataStore.getString(MainUserInfoDataStore.PREFERENCE_KEY_MAIN_USER_NAME, "test"));
        assertEquals("", mainUserInfoDataStore.getString(MainUserInfoDataStore.PREFERENCE_KEY_MAIN_USER_EMAIL, "test"));
        assertEquals("", mainUserInfoDataStore.getString("jfnsejfnes", "test"));

    }
}