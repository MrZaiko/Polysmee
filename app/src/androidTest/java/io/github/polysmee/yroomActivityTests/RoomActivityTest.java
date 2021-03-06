package io.github.polysmee.yroomActivityTests;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.FirebaseApp;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeUnit;

import io.github.polysmee.R;
import io.github.polysmee.appointments.AppointmentActivity;
import io.github.polysmee.calendar.googlecalendarsync.CalendarUtilities;
import io.github.polysmee.database.DatabaseSingleton;
import io.github.polysmee.login.AuthenticationSingleton;
import io.github.polysmee.login.MainUser;
import io.github.polysmee.room.RoomActivity;
import io.github.polysmee.znotification.AppointmentReminderNotification;

import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertContains;
import static com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed;
import static com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertNotDisplayed;
import static com.schibsted.spain.barista.interaction.BaristaClickInteractions.clickOn;
import static com.schibsted.spain.barista.interaction.BaristaClickInteractions.longClickOn;
import static com.schibsted.spain.barista.interaction.BaristaMenuClickInteractions.clickMenu;
import static com.schibsted.spain.barista.interaction.BaristaScrollInteractions.scrollTo;
import static com.schibsted.spain.barista.interaction.BaristaSleepInteractions.sleep;
import static com.schibsted.spain.barista.interaction.BaristaViewPagerInteractions.swipeViewPagerForward;
import static java.util.concurrent.TimeUnit.SECONDS;


@RunWith(AndroidJUnit4.class)
public class RoomActivityTest {
    private static final String username1 = "Mathis L'utilisateur";
    private static final String calendarId = "roomactivitytest@gmail.com";
    private static final String id2 = "bxcwviusergpoza";
    private static final String username2 = "Sami L'imposteur";

    private static final String appointmentTitle = "It's a title";
    private static final String appointmentId = "cwxbihezroijgdf";
    private static final String appointmentCourse = "Totally not SWENG";
    private static final long appointmentStart = 265655445;

    private static final String firstMessageId = "jkxwcoihjcwxp";
    private static final String firstMessage = "I'm a message";


    @BeforeClass
    public static void setUp() throws Exception {
        AppointmentReminderNotification.setIsNotificationSetterEnable(false);
        DatabaseSingleton.setLocal();
        AuthenticationSingleton.setLocal();
        FirebaseApp.clearInstancesForTest();
        CalendarUtilities.setTest(true, false);
        FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext());
        Tasks.await(AuthenticationSingleton.getAdaptedInstance().createUserWithEmailAndPassword("RoomActivityTest@gmail.com", "fakePassword"));
        DatabaseSingleton.getAdaptedInstance().getReference("users").child(MainUser.getMainUser().getId()).child("name").setValue(username1);
        DatabaseSingleton.getAdaptedInstance().getReference("users").child(MainUser.getMainUser().getId()).child("calendarId").setValue(calendarId);
        DatabaseSingleton.getAdaptedInstance().getReference("users").child(MainUser.getMainUser().getId()).child("appointments").child(appointmentId).setValue(calendarId);
        DatabaseSingleton.getAdaptedInstance().getReference("users").child(id2).child("name").setValue(username2);
        DatabaseSingleton.getAdaptedInstance().getReference("appointments").child(appointmentId).child("owner").setValue(MainUser.getMainUser().getId());
        DatabaseSingleton.getAdaptedInstance().getReference("appointments").child(appointmentId).child("title").setValue(appointmentTitle);
        DatabaseSingleton.getAdaptedInstance().getReference("appointments").child(appointmentId).child("course").setValue(appointmentCourse);
        DatabaseSingleton.getAdaptedInstance().getReference("appointments").child(appointmentId).child("start").setValue(appointmentStart);
        DatabaseSingleton.getAdaptedInstance().getReference("appointments").child(appointmentId).child("messages").child(firstMessageId).child("content").setValue(firstMessage);
        DatabaseSingleton.getAdaptedInstance().getReference("appointments").child(appointmentId).child("messages").child(firstMessageId).child("sender").setValue(id2);
        DatabaseSingleton.getAdaptedInstance().getReference("appointments").child(appointmentId).child("participants").child(MainUser.getMainUser().getId()).setValue(true);
        DatabaseSingleton.getAdaptedInstance().getReference("appointments").child(appointmentId).child("participants").child(id2).setValue(true);
    }


    @Test
    public void reactionsWorkProperly() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), RoomActivity.class);
        intent.putExtra(RoomActivity.APPOINTMENT_KEY, appointmentId);

        try (ActivityScenario<RoomActivity> ignored = ActivityScenario.launch(intent)) {
            sleep(1, SECONDS);
            scrollTo(firstMessage);
            longClickOn(firstMessage);
            sleep(500);
            clickOn(R.id.roomActivityMessageElementJoyReaction);
            assertDisplayed(R.string.emoji_joy);
            sleep(500);
            longClickOn(firstMessage);
            sleep(500);
            clickOn(R.id.roomActivityMessageElementSadReaction);
            assertDisplayed(R.string.emoji_sad);
            sleep(500);
            longClickOn(firstMessage);
            sleep(500);
            clickOn(R.id.roomActivityMessageElementHeartEyesReaction);
            assertDisplayed(R.string.emoji_heart_eyes);
            sleep(500);
            longClickOn(firstMessage);
            sleep(500);
            clickOn(R.id.roomActivityMessageElementSunglassesReaction);
            assertDisplayed(R.string.emoji_sunglasses);
            sleep(500);
            longClickOn(firstMessage);
            sleep(500);
            clickOn(R.id.roomActivityMessageElementExpressionLessReaction);
            assertDisplayed(R.string.emoji_expression_less);
            sleep(500);
            longClickOn(firstMessage);
            sleep(500);
            clickOn(R.id.roomActivityMessageElementExpressionLessReaction);
            sleep(1000);
            assertNotDisplayed(R.id.roomActivityMessageElementReactionLayout);
        }
    }

    @Test
    public void titleOfTheActivityShouldBeTheAppointmentTitle() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), RoomActivity.class);
        intent.putExtra(RoomActivity.APPOINTMENT_KEY, appointmentId);

        try (ActivityScenario<RoomActivity> ignored = ActivityScenario.launch(intent)) {
            assertContains(appointmentTitle);
        }
    }

    @Test
    public void infoItemMenuShouldFireAnIntentWithTheCurrentAppointment() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), RoomActivity.class);

        intent.putExtra(RoomActivity.APPOINTMENT_KEY, appointmentId);

        try (ActivityScenario<RoomActivity> ignored = ActivityScenario.launch(intent)) {
            Intents.init();
            clickMenu(R.id.roomMenuInfo);
            intended(hasExtra(AppointmentActivity.LAUNCH_MODE, AppointmentActivity.DETAIL_MODE));
            intended(hasExtra(AppointmentActivity.APPOINTMENT_ID, appointmentId));
            Intents.release();
        }
    }

    @Test
    public void participantsAreCorrectlyDisplayed() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), RoomActivity.class);

        intent.putExtra(RoomActivity.APPOINTMENT_KEY, appointmentId);
        try (ActivityScenario<RoomActivity> ignored = ActivityScenario.launch(intent)) {
            sleep(1, TimeUnit.SECONDS);
            swipeViewPagerForward();
            sleep(1, TimeUnit.SECONDS);
            swipeViewPagerForward();
            sleep(2, TimeUnit.SECONDS);
            assertDisplayed("You");
            assertDisplayed(username2);
        }
    }

    @Test
    public void leaveAppointmentDisplaysDialogFragment() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), RoomActivity.class);

        intent.putExtra(RoomActivity.APPOINTMENT_KEY, appointmentId);
        try (ActivityScenario<RoomActivity> ignored = ActivityScenario.launch(intent)) {
            sleep(2, TimeUnit.SECONDS);
            clickMenu(R.id.roomMenuLeave);
            sleep(2, TimeUnit.SECONDS);
            assertDisplayed("Leave");
            clickOn("Leave");
            sleep(2, TimeUnit.SECONDS);
            assertDisplayed(R.id.roomActivityRemovedDialogText);
            assertDisplayed(R.id.roomActivityRemovedDialogQuitButton);
        }
        DatabaseSingleton.getAdaptedInstance().getReference("appointments").child(appointmentId).child("owner").setValue(MainUser.getMainUser().getId());
        DatabaseSingleton.getAdaptedInstance().getReference("appointments").child(appointmentId).child("participants").child(MainUser.getMainUser().getId()).setValue(true);
    }


}