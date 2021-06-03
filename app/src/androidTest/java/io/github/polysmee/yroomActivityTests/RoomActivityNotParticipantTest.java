package io.github.polysmee.yroomActivityTests;

import android.app.Activity;
import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.FirebaseApp;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeUnit;

import io.github.polysmee.R;
import io.github.polysmee.calendar.googlecalendarsync.CalendarUtilities;
import io.github.polysmee.database.DatabaseFactory;
import io.github.polysmee.login.AuthenticationFactory;
import io.github.polysmee.login.MainUser;
import io.github.polysmee.room.RoomActivity;
import io.github.polysmee.notification.AppointmentReminderNotification;

import static com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed;
import static com.schibsted.spain.barista.interaction.BaristaClickInteractions.clickOn;
import static com.schibsted.spain.barista.interaction.BaristaSleepInteractions.sleep;
import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class RoomActivityNotParticipantTest {
    private static final String username1 = "Mathis L'utilisateur";
    private static final String id2 = "posdkojerzyugcwxu";
    private static final String username2 = "Sami L'imposteur";

    private static final String appointmentTitle = "It's a title";
    private static final String appointmentId = "oiuowfpkksdnf";
    private static final String appointmentCourse = "Totally not SWENG";
    private static final long appointmentStart = 265655445;


    @BeforeClass
    public static void setUp() throws Exception {
        AppointmentReminderNotification.setIsNotificationSetterEnable(false);
        DatabaseFactory.setTest();
        AuthenticationFactory.setTest();
        FirebaseApp.clearInstancesForTest();
        CalendarUtilities.setTest(true, false);
        FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext());
        Tasks.await(AuthenticationFactory.getAdaptedInstance().createUserWithEmailAndPassword("RoomActivityNotParticipantTest@gmail.com", "fakePassword"));
        DatabaseFactory.getAdaptedInstance().getReference("users").child(MainUser.getMainUser().getId()).child("name").setValue(username1);
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

        try (ActivityScenario<RoomActivity> ignored = ActivityScenario.launch(intent)) {
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