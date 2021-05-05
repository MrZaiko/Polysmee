package io.github.polysmee.settings;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;


import com.google.android.gms.tasks.Tasks;
import com.google.firebase.FirebaseApp;


import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;


import io.github.polysmee.database.DatabaseFactory;
import io.github.polysmee.login.AuthenticationFactory;
import io.github.polysmee.login.MainUserSingleton;
import io.github.polysmee.znotification.AppointmentReminderNotificationSetupListener;

import static com.schibsted.spain.barista.interaction.BaristaSleepInteractions.sleep;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.*;

public class UserInfoDataStoreTest {
    private final static String userEmail = "UserInfoDataStoreTest@gmail.com";
    private final static String userName = "UserInfoDataStoreTest";
    private final static String userPassword = "fakePassword";

    @Rule
    public ActivityScenarioRule<SettingsActivity> testRule = new ActivityScenarioRule<SettingsActivity>(SettingsActivity.class);

    @BeforeClass
    public static void setUp() throws Exception {
        AppointmentReminderNotificationSetupListener.setIsNotificationSetterEnable(false);
        DatabaseFactory.setTest();
        AuthenticationFactory.setTest();
        FirebaseApp.clearInstancesForTest();
        FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext());
        Tasks.await(AuthenticationFactory.getAdaptedInstance().createUserWithEmailAndPassword(userEmail, userPassword));
        DatabaseFactory.getAdaptedInstance().getReference("users").child(MainUserSingleton.getInstance().getId()).child("name").setValue(userName);
    }
    @AfterClass
    public static void clearUp(){
        DatabaseFactory.getAdaptedInstance().getReference("users").setValue(null);
        DatabaseFactory.getAdaptedInstance().getReference("appointments").setValue(null);
    }
    public static void testNameDatabase(String value) throws Exception{
        sleep(1, SECONDS);
        String name  = (String) Tasks.await(DatabaseFactory.getAdaptedInstance().getReference().child("users")
                .child(MainUserSingleton.getInstance().getId()).child("name").get()).getValue();
        assertEquals(value, name);
    }
    @Test
    public void putString() throws Exception{
        UserInfoDataStore userInfoDataStore = new UserInfoDataStore();
        String stringToPut = "name change test";
        userInfoDataStore.putString(UserInfoDataStore.preferenceKeyMainUserName, stringToPut);
        testNameDatabase(stringToPut);
        userInfoDataStore.putString(UserInfoDataStore.preferenceKeyMainUserName, userName);
        testNameDatabase(userName);
        userInfoDataStore.putString("efse", stringToPut);
        testNameDatabase(userName);
        userInfoDataStore.putString(UserInfoDataStore.preferenceKeyMainUserEmail, stringToPut);
        testNameDatabase(userName);
        userInfoDataStore.putString("fkesjnfejsf", stringToPut);
        testNameDatabase(userName);
    }

    @Test
    public void getString() {
        UserInfoDataStore userInfoDataStore = new UserInfoDataStore();
        assertEquals("",userInfoDataStore.getString(UserInfoDataStore.preferenceKeyMainUserName, "test"));
        assertEquals( "", userInfoDataStore.getString(UserInfoDataStore.preferenceKeyMainUserEmail, "test"));
        assertEquals("", userInfoDataStore.getString("jfnsejfnes", "test"));

    }
}