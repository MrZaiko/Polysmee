package io.github.polysmee.roomActivityTests;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.testing.FragmentScenario;
import androidx.preference.PreferenceManager;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.intent.Intents;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.FirebaseApp;

import org.junit.Assert;
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
import io.github.polysmee.znotification.AppointmentReminderNotification;
import io.github.polysmee.profile.ProfileActivity;
import io.github.polysmee.room.fragments.RoomActivityParticipantsFragment;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed;
import static com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertNotDisplayed;
import static com.schibsted.spain.barista.interaction.BaristaClickInteractions.clickOn;
import static com.schibsted.spain.barista.interaction.BaristaSleepInteractions.sleep;
import static com.schibsted.spain.barista.interaction.BaristaSpinnerInteractions.clickSpinnerItem;
import static java.util.concurrent.TimeUnit.SECONDS;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasExtra;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(JUnit4.class)
public class RoomActivityParticipantsFragmentTest {
    private static final String username1 = "Mathis L'utilisateur";
    private static String id2 = "poiqsdhfgreidfgknbcbv";
    private static final String username2 = "Sami L'imposteur";
    private static final String appointmentTitle = "It's a title";
    private static String appointmentId = "nbcwxuhcjgvwxcuftyqf";
    private static final String appointmentCourse = "Totally not SWENG";
    private static final long appointmentStart = 265655445;

    private static String profilePictureId = "bigYOSHI";

    @BeforeClass
    public static void setUp() throws Exception {
        AppointmentReminderNotification.setIsNotificationSetterEnable(false);
        UploadServiceFactory.setTest(true);
        DatabaseFactory.setTest();
        AuthenticationFactory.setTest();
        FirebaseApp.clearInstancesForTest();
        FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext());
        Tasks.await(AuthenticationFactory.getAdaptedInstance().createUserWithEmailAndPassword("RoomActivityParticipantsFragmentTest@gmail.com", "fakePassword"));
        UploadServiceFactory.getAdaptedInstance().uploadImage(BigYoshi.getBytes(), profilePictureId, s ->{}, s->{});
        DatabaseFactory.getAdaptedInstance().getReference("users").child(MainUser.getMainUser().getId()).child("name").setValue(username1);
        DatabaseFactory.getAdaptedInstance().getReference("users").child(MainUser.getMainUser().getId()).child("picture").setValue(profilePictureId);
        DatabaseFactory.getAdaptedInstance().getReference("users").child(id2).child("name").setValue(username2);
        DatabaseFactory.getAdaptedInstance().getReference("users").child(id2).child("picture").setValue(profilePictureId);
        DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId).child("title").setValue(appointmentTitle);
        DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId).child("course").setValue(appointmentCourse);
        DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId).child("owner").setValue(MainUser.getMainUser().getId());
        DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId).child("start").setValue(appointmentStart);
        DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId).child("participants").child(MainUser.getMainUser().getId()).setValue(true);
    }


    @Test
    public void participantsAreCorrectlyDisplayed() {
        Bundle bundle = new Bundle();
        bundle.putString(RoomActivityParticipantsFragment.PARTICIPANTS_KEY, appointmentId);
        FragmentScenario.launchInContainer(RoomActivityParticipantsFragment.class, bundle);
        sleep(1, SECONDS);
        assertDisplayed("You");
        clickOn(R.id.roomActivityParticipantElementCallButton);
        clickOn(R.id.roomActivityParticipantElementMuteButton);
        clickOn(R.id.roomActivityParticipantElementMuteButton);
        clickOn(R.id.roomActivityParticipantElementCallButton);
        assertDisplayed(R.id.roomActivityParticipantElementOwnerVoiceMenu);
    }

    @Test
    public void joinChannelWorks() {
        List usersInCall = new ArrayList<String>();
        DatabaseAppointment appointment = new DatabaseAppointment(appointmentId);
        appointment.addInCallListener(new BooleanChildListener() {
            @Override
            public void childAdded(String key, boolean value) {
                usersInCall.add(key);
            }
        });
        Bundle bundle = new Bundle();
        bundle.putString(RoomActivityParticipantsFragment.PARTICIPANTS_KEY, appointmentId);
        FragmentScenario.launchInContainer(RoomActivityParticipantsFragment.class, bundle);
        sleep(1, SECONDS);
        clickOn(R.id.roomActivityParticipantElementCallButton);


        sleep(1, SECONDS);
        assert (!usersInCall.isEmpty());
        assertEquals(MainUser.getMainUser().getId(), usersInCall.get(0));
        clickOn(R.id.roomActivityParticipantElementCallButton);
    }

    @Test
    public void muteWorks() {
        List usersMuted = new ArrayList<String>();
        DatabaseAppointment appointment = new DatabaseAppointment(appointmentId);
        appointment.addInCallListener(new BooleanChildListener() {
            @Override
            public void childChanged(String key, boolean value) {
                if(value) {
                    usersMuted.add(key);
                }
            }
        });
        Bundle bundle = new Bundle();
        bundle.putString(RoomActivityParticipantsFragment.PARTICIPANTS_KEY, appointmentId);
        FragmentScenario.launchInContainer(RoomActivityParticipantsFragment.class, bundle);
        sleep(1, SECONDS);
        clickOn(R.id.roomActivityParticipantElementCallButton);
        sleep(1, SECONDS);
        clickOn(R.id.roomActivityParticipantElementMuteButton);
        sleep(1, SECONDS);
        assert(!usersMuted.isEmpty());
        assertEquals(MainUser.getMainUser().getId(),usersMuted.get(0));
        clickOn(R.id.roomActivityParticipantElementCallButton);
    }
    @Test
    public void unMuteWorks() {
        List usersUnmuted = new ArrayList<String>();
        DatabaseAppointment appointment = new DatabaseAppointment(appointmentId);
        appointment.addInCallListener(new BooleanChildListener() {
            @Override
            public void childChanged(String key, boolean value) {
                if(!value) {
                    usersUnmuted.add(key);
                }
            }
        });
        Bundle bundle = new Bundle();
        bundle.putString(RoomActivityParticipantsFragment.PARTICIPANTS_KEY, appointmentId);
        FragmentScenario.launchInContainer(RoomActivityParticipantsFragment.class, bundle);
        sleep(1, SECONDS);
        clickOn(R.id.roomActivityParticipantElementCallButton);
        sleep(1, SECONDS);
        clickOn(R.id.roomActivityParticipantElementMuteButton);
        sleep(1, SECONDS);
        clickOn(R.id.roomActivityParticipantElementMuteButton);
        sleep(1,SECONDS);
        assert(!usersUnmuted.isEmpty());
        assertEquals(MainUser.getMainUser().getId(),usersUnmuted.get(0));
        clickOn(R.id.roomActivityParticipantElementCallButton);
    }

    @Test
    public void testVoiceTuner() {
        Bundle bundle = new Bundle();
        bundle.putString(RoomActivityParticipantsFragment.PARTICIPANTS_KEY, appointmentId);
        FragmentScenario.launchInContainer(RoomActivityParticipantsFragment.class, bundle);
        sleep(1, SECONDS);
        clickOn(R.id.roomActivityParticipantElementOwnerVoiceMenu);
        clickSpinnerItem(R.id.voiceTunerSpinner, 2);
        pressBack();
        int currentVoicePosition = PreferenceManager.getDefaultSharedPreferences(ApplicationProvider.getApplicationContext()).getInt(
                ApplicationProvider.getApplicationContext().getResources().getString(R.string.preference_key_voice_tuner_current_voice_tune) ,0);
        Assert.assertEquals(currentVoicePosition, 2);
    }


}
