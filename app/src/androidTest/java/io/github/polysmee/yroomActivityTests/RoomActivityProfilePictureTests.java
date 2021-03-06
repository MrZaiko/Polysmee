package io.github.polysmee.yroomActivityTests;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.intent.Intents;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.FirebaseApp;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import io.github.polysmee.BigYoshi;
import io.github.polysmee.R;
import io.github.polysmee.calendar.googlecalendarsync.CalendarUtilities;
import io.github.polysmee.database.DatabaseSingleton;
import io.github.polysmee.database.UploadServiceFactory;
import io.github.polysmee.login.AuthenticationSingleton;
import io.github.polysmee.login.MainUser;
import io.github.polysmee.profile.ProfileActivity;
import io.github.polysmee.room.RoomActivity;
import io.github.polysmee.znotification.AppointmentReminderNotification;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static com.schibsted.spain.barista.interaction.BaristaClickInteractions.clickOn;
import static com.schibsted.spain.barista.interaction.BaristaSleepInteractions.sleep;
import static com.schibsted.spain.barista.interaction.BaristaViewPagerInteractions.swipeViewPagerForward;
import static java.util.concurrent.TimeUnit.SECONDS;

@RunWith(JUnit4.class)
public class RoomActivityProfilePictureTests {
    private static final String username1 = "Mathis L'utilisateur";
    private static final String id2 = "oierytuhzzaeazjdfbsgvcwx";
    private static final String username2 = "Sami L'imposteur";
    private static final String appointmentTitle = "Yoshi";
    private static final String appointmentId = "nbcwxuhcjgvqsqdwxcufqdsfdqcs";
    private static final String appointmentCourse = "Totally not SWENG";
    private static final long appointmentStart = 265655445;

    private static final String firstMessageId = "jkxwcoihjqdsdqsqsdcwxp";
    private static final String firstMessage = "I'm a message";

    private static final String profilePictureId = "bigYOSHI";

    @BeforeClass
    public static void setUp() throws Exception {
        AppointmentReminderNotification.setIsNotificationSetterEnable(false);
        UploadServiceFactory.setLocal(true);


        DatabaseSingleton.setLocal();
        AuthenticationSingleton.setLocal();
        CalendarUtilities.setTest(true, false);
        FirebaseApp.clearInstancesForTest();
        FirebaseApp.initializeApp(getApplicationContext());
        Tasks.await(AuthenticationSingleton.getAdaptedInstance().createUserWithEmailAndPassword("RoomActivityProfilePictureTests@gmail.com", "fakePassword"));
        UploadServiceFactory.getAdaptedInstance().uploadImage(BigYoshi.getBytes(), profilePictureId, s -> {
        }, s -> {
        }, getApplicationContext());
        DatabaseSingleton.getAdaptedInstance().getReference("users").child(MainUser.getMainUser().getId()).child("name").setValue(username1);
        DatabaseSingleton.getAdaptedInstance().getReference("users").child(MainUser.getMainUser().getId()).child("picture").setValue(profilePictureId);
        DatabaseSingleton.getAdaptedInstance().getReference("users").child(id2).child("name").setValue(username2);
        DatabaseSingleton.getAdaptedInstance().getReference("users").child(id2).child("picture").setValue(profilePictureId);

        DatabaseSingleton.getAdaptedInstance().getReference("appointments").child(appointmentId).child("title").setValue(appointmentTitle);
        DatabaseSingleton.getAdaptedInstance().getReference("appointments").child(appointmentId).child("course").setValue(appointmentCourse);

        DatabaseSingleton.getAdaptedInstance().getReference("appointments").child(appointmentId).child("messages").child(firstMessageId).child("content").setValue(firstMessage);
        DatabaseSingleton.getAdaptedInstance().getReference("appointments").child(appointmentId).child("messages").child(firstMessageId).child("sender").setValue(id2);

        DatabaseSingleton.getAdaptedInstance().getReference("appointments").child(appointmentId).child("owner").setValue(id2);
        DatabaseSingleton.getAdaptedInstance().getReference("appointments").child(appointmentId).child("start").setValue(appointmentStart);
        DatabaseSingleton.getAdaptedInstance().getReference("appointments").child(appointmentId).child("participants").child(MainUser.getMainUser().getId()).setValue(true);
    }

    @Test
    public void participantsProfilePicturesAreClickable() {
        Intent intent = new Intent(getApplicationContext(), RoomActivity.class);
        intent.putExtra(RoomActivity.APPOINTMENT_KEY, appointmentId);

        try (ActivityScenario<RoomActivity> ignored = ActivityScenario.launch(intent)) {
            sleep(1, SECONDS);
            swipeViewPagerForward();
            swipeViewPagerForward();
            sleep(1, SECONDS);
            clickOn(R.id.roomActivityParticipantElementCallButton);
            sleep(2, SECONDS);
            clickOn(R.id.roomActivityParticipantElementVideoButton);
            sleep(2, SECONDS);
            clickOn(R.id.roomActivityParticipantElementCallButton);
            sleep(2, SECONDS);
            Intents.init();
            clickOn(R.id.roomActivityParticipantElementProfilePicture);
            intended(hasExtra(ProfileActivity.PROFILE_ID_USER, MainUser.getMainUser().getId()));
            intended(hasExtra(ProfileActivity.PROFILE_VISIT_CODE, ProfileActivity.PROFILE_OWNER_MODE));
            Intents.release();
        }
    }

    @Test
    public void messagesProfilePicturesAreClickable() {
        Intent intent = new Intent(getApplicationContext(), RoomActivity.class);
        intent.putExtra(RoomActivity.APPOINTMENT_KEY, appointmentId);

        try (ActivityScenario<RoomActivity> ignored = ActivityScenario.launch(intent)) {
            sleep(1, SECONDS);
            Intents.init();
            clickOn(R.id.roomActivityMessageElementProfilePicture);
            intended(hasExtra(ProfileActivity.PROFILE_VISIT_CODE, ProfileActivity.PROFILE_VISITING_MODE));
            intended(hasExtra(ProfileActivity.PROFILE_ID_USER, id2));
            Intents.release();
        }
    }

}