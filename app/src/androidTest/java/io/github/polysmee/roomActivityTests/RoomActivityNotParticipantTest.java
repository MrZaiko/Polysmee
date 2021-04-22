package io.github.polysmee.roomActivityTests;

import android.app.Activity;
import android.content.Intent;

import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.runner.MonitoringInstrumentation;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.FirebaseApp;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.security.SecureRandom;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import io.github.polysmee.R;
import io.github.polysmee.database.DatabaseFactory;
import io.github.polysmee.login.AuthenticationFactory;
import io.github.polysmee.login.MainUserSingleton;
import io.github.polysmee.room.RoomActivity;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertContains;
import static com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed;
import static com.schibsted.spain.barista.interaction.BaristaClickInteractions.clickOn;
import static com.schibsted.spain.barista.interaction.BaristaSleepInteractions.sleep;
import static com.schibsted.spain.barista.interaction.BaristaViewPagerInteractions.swipeViewPagerForward;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class RoomActivityNotParticipantTest {
    private static final String username1 = "Mathis L'utilisateur";
    private static String id2 = "posdkojerzyugcwxu";
    private static final String username2 = "Sami L'imposteur";

    private static final String appointmentTitle = "It's a title";
    private static String appointmentId = "oiuowfpkksdnf";
    private static final String appointmentCourse = "Totally not SWENG";
    private static final long appointmentStart = 265655445;


    @BeforeClass
    public static void setUp() throws Exception {
        DatabaseFactory.setTest();
        AuthenticationFactory.setTest();
        FirebaseApp.clearInstancesForTest();
        FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext());
        Tasks.await(AuthenticationFactory.getAdaptedInstance().createUserWithEmailAndPassword("RoomActivityNotParticipantTest@gmail.com", "fakePassword"));
        DatabaseFactory.getAdaptedInstance().getReference("users").child(MainUserSingleton.getInstance().getId()).child("name").setValue(username1);
        DatabaseFactory.getAdaptedInstance().getReference("users").child(id2).child("name").setValue(username2);

        DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId).child("title").setValue(appointmentTitle);
        DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId).child("course").setValue(appointmentCourse);
        DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId).child("start").setValue(appointmentStart);
        DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId).child("participants").child(id2).setValue(true);
    }

    @Test
    public void onlyParticipantCanJoinARoom() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), RoomActivity.class);
        intent.putExtra(RoomActivity.APPOINTMENT_KEY, appointmentId);

        try (ActivityScenario<RoomActivity> ignored = ActivityScenario.launch(intent)){
           sleep(2, TimeUnit.SECONDS);
           assertDisplayed(R.id.roomActivityRemovedDialogText);
           assertDisplayed(R.id.roomActivityRemovedDialogQuitButton);
        }
    }

    @Test
    public void quitButtonShouldFinishTheActivity() throws InterruptedException {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), RoomActivity.class);
        intent.putExtra(RoomActivity.APPOINTMENT_KEY, appointmentId);

        ActivityScenario<RoomActivity> scenario = ActivityScenario.launch(intent);
        sleep(1, TimeUnit.SECONDS);
        clickOn(R.id.roomActivityRemovedDialogQuitButton);
        assertEquals(Activity.RESULT_CANCELED, scenario.getResult().getResultCode());
        Thread.sleep(1000);
        scenario.close();
    }
}