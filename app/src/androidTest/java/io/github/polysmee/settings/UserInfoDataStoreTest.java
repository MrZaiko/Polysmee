package io.github.polysmee.settings;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;


import com.google.android.gms.tasks.Tasks;
import com.google.firebase.FirebaseApp;


import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;


import io.github.polysmee.database.DatabaseFactory;
import io.github.polysmee.database.DatabaseUser;
import io.github.polysmee.login.AuthenticationFactory;
import io.github.polysmee.login.MainUserSingleton;

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
        DatabaseFactory.setTest();
        AuthenticationFactory.setTest();
        FirebaseApp.clearInstancesForTest();
        FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext());
        Tasks.await(AuthenticationFactory.getAdaptedInstance().createUserWithEmailAndPassword(userEmail, userPassword));
        DatabaseFactory.getAdaptedInstance().getReference("users").child(MainUserSingleton.getInstance().getId()).child("name").setValue(userName);
    }

    public static void testNameDatabase(String value){
        new DatabaseUser(MainUserSingleton.getInstance().getId()).getName_Once_AndThen(name -> assertEquals(value, name ));
        sleep(1, SECONDS);
    }
    @Test
    public void putString() {
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