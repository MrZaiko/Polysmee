package io.github.polysmee.yroomActivityTests;

import android.os.Bundle;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.core.app.ApplicationProvider;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.FirebaseApp;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import io.github.polysmee.R;
import io.github.polysmee.calendar.googlecalendarsync.CalendarUtilities;
import io.github.polysmee.database.DatabaseSingleton;
import io.github.polysmee.login.AuthenticationSingleton;
import io.github.polysmee.login.MainUser;
import io.github.polysmee.room.fragments.RoomActivityParticipantsFragment;
import io.github.polysmee.notification.AppointmentReminderNotification;

import static com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed;
import static com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertNotDisplayed;
import static com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertNotExist;
import static com.schibsted.spain.barista.interaction.BaristaClickInteractions.clickOn;
import static com.schibsted.spain.barista.interaction.BaristaSleepInteractions.sleep;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@RunWith(JUnit4.class)
public class RoomActivityParticipantsFragmentNotOwnerTest {
    private static final String username1 = "Mathis L'utilisateur";
    private static final String id2 = "oierytuhjdfbsgvcwx";
    private static final String username2 = "Sami L'imposteur";
    private static final String userDescription2 = "Bonjour, je suis Mathis ou Sami?";

    private static final String appointmentTitle = "It's a title";
    private static final String appointmentId = "ahvwcxtdfytazazeiu";
    private static final String appointmentCourse = "Totally not SWENG";
    private static final long appointmentStart = 265655445;


    @BeforeClass
    public static void setUp() throws Exception {
        AppointmentReminderNotification.setIsNotificationSetterEnable(false);
        DatabaseSingleton.setLocal();
        AuthenticationSingleton.setLocal();
        FirebaseApp.clearInstancesForTest();
        CalendarUtilities.setTest(true, false);
        FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext());
        Tasks.await(AuthenticationSingleton.getAdaptedInstance().createUserWithEmailAndPassword("RoomActivityParticipantsFragmentNotOwnerTest@gmail.com", "fakePassword"));
        DatabaseSingleton.getAdaptedInstance().getReference("users").child(MainUser.getMainUser().getId()).child("name").setValue(username1);
        DatabaseSingleton.getAdaptedInstance().getReference("users").child(id2).child("name").setValue(username2);
        DatabaseSingleton.getAdaptedInstance().getReference("users").child(id2).child("description").setValue(userDescription2);

        DatabaseSingleton.getAdaptedInstance().getReference("appointments").child(appointmentId).child("title").setValue(appointmentTitle);
        DatabaseSingleton.getAdaptedInstance().getReference("appointments").child(appointmentId).child("course").setValue(appointmentCourse);
        DatabaseSingleton.getAdaptedInstance().getReference("appointments").child(appointmentId).child("owner").setValue(id2);
        DatabaseSingleton.getAdaptedInstance().getReference("appointments").child(appointmentId).child("start").setValue(appointmentStart);
        DatabaseSingleton.getAdaptedInstance().getReference("appointments").child(appointmentId).child("participants").child(MainUser.getMainUser().getId()).setValue(true);
        DatabaseSingleton.getAdaptedInstance().getReference("appointments").child(appointmentId).child("participants").child(id2).setValue(true);
    }

    @Test
    public void participantsAreCorrectlyDisplayed() {
        Bundle bundle = new Bundle();
        bundle.putString(RoomActivityParticipantsFragment.PARTICIPANTS_KEY, appointmentId);
        FragmentScenario.launchInContainer(RoomActivityParticipantsFragment.class, bundle);
        sleep(1, SECONDS);
        assertDisplayed("You");
        assertDisplayed(username2);
        assertNotDisplayed(R.id.roomActivityParticipantElementOwnerVoiceMenu);
    }

    @Test
    public void addingAndRemovingFriendFromRoomTest() throws ExecutionException, InterruptedException {
        Bundle bundle = new Bundle();
        bundle.putString(RoomActivityParticipantsFragment.PARTICIPANTS_KEY, appointmentId);
        FragmentScenario.launchInContainer(RoomActivityParticipantsFragment.class, bundle);
        Thread.sleep(1000);
        clickOn(R.id.roomActivityManageParticipantAsFriendButton);
        Thread.sleep(3000);
        HashMap usr = (HashMap) Tasks.await(DatabaseSingleton.getAdaptedInstance().getReference("users").child(id2).child("friendsInvites").get()).getValue();
        assertEquals(1, usr.size());
    }

    @Test
    public void clickingOnAnotherUserLaunchesProfile() {
        Bundle bundle = new Bundle();
        bundle.putString(RoomActivityParticipantsFragment.PARTICIPANTS_KEY, appointmentId);
        FragmentScenario.launchInContainer(RoomActivityParticipantsFragment.class, bundle);
        sleep(2, SECONDS);
        clickOn(username2);
        sleep(2, SECONDS);
        assertDisplayed(R.string.title_profile_user_name);
        assertDisplayed(username2);
        assertDisplayed(R.string.title_profile_user_description);
        assertDisplayed(userDescription2);
        assertNotExist(R.string.title_profile_user_email);
        assertNotExist(R.string.title_profile_main_user_friends);
    }
}