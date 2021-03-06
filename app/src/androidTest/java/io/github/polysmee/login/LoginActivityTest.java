package io.github.polysmee.login;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.FirebaseApp;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.github.polysmee.calendar.googlecalendarsync.CalendarUtilities;
import io.github.polysmee.database.DatabaseSingleton;
import io.github.polysmee.znotification.AppointmentReminderNotification;

import static androidx.test.espresso.Espresso.pressBack;
import static com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed;
import static com.schibsted.spain.barista.interaction.BaristaClickInteractions.clickOn;

@RunWith(AndroidJUnit4.class)
public class LoginActivityTest {

    @BeforeClass
    public static void setUp() throws Exception {
        AppointmentReminderNotification.setIsNotificationSetterEnable(false);
        DatabaseSingleton.setLocal();
        AuthenticationSingleton.setLocal();
        CalendarUtilities.setTest(true, false);

        FirebaseApp.clearInstancesForTest();
        FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext());

        Tasks.await(AuthenticationSingleton.getAdaptedInstance().createUserWithEmailAndPassword("LoginActivityTest@gmail.com", "fakePassword"));
        DatabaseSingleton.getAdaptedInstance().getReference("users").child(MainUser.getMainUser().getId()).child("name").setValue("blabla");
        Thread.sleep(1000);
    }


    @AfterClass
    public static void clean() {
        DatabaseSingleton.getAdaptedInstance().getReference().setValue(null);
    }

    @Test
    public void goesBackToLoginAfterCancel() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), LoginActivity.class);
        Intents.init();
        try (ActivityScenario<LoginActivity> ignored = ActivityScenario.launch(intent)) {
            clickOn("LOGIN");
            pressBack();
            assertDisplayed("LOGIN");
        }
        Intents.release();
    }

    @Test
    public void worksOnCorrectCode() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), LoginActivity.class);
        Intents.init();
        try (ActivityScenario<LoginActivity> ignored = ActivityScenario.launch(intent)) {
            ignored.onActivity((act) -> act.onActivityResult(123, -1 , null));
        }
        Intents.release();
    }

    @Test
    public void failsOnIncorrectCode() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), LoginActivity.class);
        Intents.init();
        try (ActivityScenario<LoginActivity> ignored = ActivityScenario.launch(intent)) {
            ignored.onActivity((act) -> act.onActivityResult(0, 0 , null));
        }
        Intents.release();
    }
}