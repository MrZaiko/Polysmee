package io.github.polysmee.roomActivityTests;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.testing.FragmentScenario;
import androidx.preference.PreferenceManager;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.intent.Intents;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.FirebaseApp;

import org.junit.Assert;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.List;

import io.github.polysmee.BigYoshi;
import io.github.polysmee.R;
import io.github.polysmee.database.DatabaseAppointment;
import io.github.polysmee.database.DatabaseFactory;
import io.github.polysmee.database.UploadServiceFactory;
import io.github.polysmee.database.databaselisteners.BooleanChildListener;
import io.github.polysmee.login.AuthenticationFactory;
import io.github.polysmee.login.MainUser;
import io.github.polysmee.profile.ProfileActivity;
import io.github.polysmee.room.RoomActivity;
import io.github.polysmee.znotification.AppointmentReminderNotificationSetupListener;
import io.github.polysmee.room.fragments.RoomActivityParticipantsFragment;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed;
import static com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertNotDisplayed;
import static com.schibsted.spain.barista.interaction.BaristaClickInteractions.clickOn;
import static com.schibsted.spain.barista.interaction.BaristaSleepInteractions.sleep;
import static com.schibsted.spain.barista.interaction.BaristaSpinnerInteractions.clickSpinnerItem;
import static com.schibsted.spain.barista.interaction.BaristaViewPagerInteractions.swipeViewPagerForward;
import static com.schibsted.spain.barista.internal.viewaction.SwipeActions.swipeLeft;
import static java.util.concurrent.TimeUnit.SECONDS;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasExtra;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(JUnit4.class)
public class RoomActivityProfilePictureTests {
    private static final String username1 = "Mathis L'utilisateur";
    private static String id2 = "oierytuhzzaeazjdfbsgvcwx";
    private static final String username2 = "Sami L'imposteur";
    private static final String appointmentTitle = "Yoshi";
    private static String appointmentId = "nbcwxuhcjgvqsqdwxcufqdsfdqcs";
    private static final String appointmentCourse = "Totally not SWENG";
    private static final long appointmentStart = 265655445;

    private static String firstMessageId = "jkxwcoihjqdsdqsqsdcwxp";
    private static final String firstMessage = "I'm a message";

    private static String profilePictureId = "bigYOSHI";

    @BeforeClass
    public static void setUp() throws Exception {
        AppointmentReminderNotificationSetupListener.setIsNotificationSetterEnable(false);
        UploadServiceFactory.setTest(true);


        DatabaseFactory.setTest();
        AuthenticationFactory.setTest();
        FirebaseApp.clearInstancesForTest();
        FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext());
        Tasks.await(AuthenticationFactory.getAdaptedInstance().createUserWithEmailAndPassword("RoomActivityProfilePictureTests@gmail.com", "fakePassword"));
        UploadServiceFactory.getAdaptedInstance().uploadImage(BigYoshi.getBytes(), profilePictureId, s ->{}, s->{});
        DatabaseFactory.getAdaptedInstance().getReference("users").child(MainUser.getMainUser().getId()).child("name").setValue(username1);
        DatabaseFactory.getAdaptedInstance().getReference("users").child(MainUser.getMainUser().getId()).child("picture").setValue(profilePictureId);
        DatabaseFactory.getAdaptedInstance().getReference("users").child(id2).child("name").setValue(username2);
        DatabaseFactory.getAdaptedInstance().getReference("users").child(id2).child("picture").setValue(profilePictureId);

        DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId).child("title").setValue(appointmentTitle);
        DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId).child("course").setValue(appointmentCourse);

        DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId).child("messages").child(firstMessageId).child("content").setValue(firstMessage);
        DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId).child("messages").child(firstMessageId).child("sender").setValue(id2);

        DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId).child("owner").setValue(id2);
        DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId).child("start").setValue(appointmentStart);
        DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId).child("participants").child(MainUser.getMainUser().getId()).setValue(true);
    }

    @Test
    public void participantsProfilePicturesAreClickable() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), RoomActivity.class);
        intent.putExtra(RoomActivity.APPOINTMENT_KEY, appointmentId);

        try (ActivityScenario<RoomActivity> ignored = ActivityScenario.launch(intent)){
            sleep(1, SECONDS);
            swipeViewPagerForward();
            swipeViewPagerForward();
            sleep(1, SECONDS);
            Intents.init();
            clickOn(R.id.roomActivityParticipantElementProfilePicture);
            intended(hasExtra(ProfileActivity.PROFILE_ID_USER, MainUser.getMainUser().getId()));
            intended(hasExtra(ProfileActivity.PROFILE_VISIT_CODE, ProfileActivity.PROFILE_OWNER_MODE));
            Intents.release();
        }
    }

    @Test
    public void messagesProfilePicturesAreClickable() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), RoomActivity.class);
        intent.putExtra(RoomActivity.APPOINTMENT_KEY, appointmentId);

        try (ActivityScenario<RoomActivity> ignored = ActivityScenario.launch(intent)){
            sleep(1, SECONDS);
            Intents.init();
            clickOn(R.id.roomActivityMessageElementProfilePicture);
            intended(hasExtra(ProfileActivity.PROFILE_VISIT_CODE, ProfileActivity.PROFILE_VISITING_MODE));
            intended(hasExtra(ProfileActivity.PROFILE_ID_USER, id2));
            Intents.release();
        }
    }

}