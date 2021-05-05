package io.github.polysmee.settings;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.FirebaseApp;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.github.polysmee.R;
import io.github.polysmee.database.DatabaseFactory;
import io.github.polysmee.login.AuthenticationFactory;
import io.github.polysmee.login.MainUser;
import io.github.polysmee.znotification.AppointmentReminderNotificationSetupListener;
import io.github.polysmee.settings.fragments.SettingsUserInfoFragment;

import static com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed;
import static com.schibsted.spain.barista.interaction.BaristaClickInteractions.clickOn;
import static com.schibsted.spain.barista.interaction.BaristaSleepInteractions.sleep;
import static java.util.concurrent.TimeUnit.SECONDS;

@RunWith(AndroidJUnit4.class)
public class SettingsUserInfoFragmentTest {
    private final static String userEmail = "settingsuserinfofragmenttest@gmail.com";
    private final static String userName = "SettingsUserInfoTest";
    private final static String userPassword = "fakePassword";


    @BeforeClass
    public static void setUp() throws Exception {
        AppointmentReminderNotificationSetupListener.setIsNotificationSetterEnable(false);
        DatabaseFactory.setTest();
        AuthenticationFactory.setTest();
        FirebaseApp.clearInstancesForTest();
        FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext());
        Tasks.await(AuthenticationFactory.getAdaptedInstance().createUserWithEmailAndPassword(userEmail, userPassword));
        DatabaseFactory.getAdaptedInstance().getReference("users").child(MainUser.getMainUser().getId()).child("name").setValue(userName);
    }

    @Before
    public void createFragment(){
        FragmentScenario.launchInContainer(SettingsUserInfoFragment.class);
        sleep(1, SECONDS);
    }

    //used in tests (this test or any other) to know if the fragment is been displayed
    public static void checkFragmentIsDisplayed(String userName, String userEmail){
        checkFragmentIsDisplayed();
        sleep(1, SECONDS);
        assertDisplayed(userName);
        assertDisplayed(userEmail);
    }
    //used in tests (this test or any other) to know if the fragment is been displayed
    public static void checkFragmentIsDisplayed(){
        assertDisplayed(R.string.title_settings_main_user_email);
        assertDisplayed(R.string.title_settings_main_user_name);
    }

    @Test
    public void checkFragmentIsWellDisplayed(){
        checkFragmentIsDisplayed(userName, userEmail);
        /**
        clickOn(R.string.title_settings_main_user_name);
        UiDevice uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        uiDevice.pressKeyCode(KEYCODE_A);
        uiDevice.pressKeyCode(KEYCODE_F);
        uiDevice.pressKeyCode(KEYCODE_U);
        clickOn("ok");
        sleep(1, SECONDS);
        checkFragmentIsDisplayed("AFU", userEmail);**/
    }


}
