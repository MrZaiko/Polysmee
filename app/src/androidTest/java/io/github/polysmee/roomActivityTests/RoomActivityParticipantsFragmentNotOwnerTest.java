package io.github.polysmee.roomActivityTests;

import android.os.Bundle;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.core.app.ApplicationProvider;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.FirebaseApp;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.HashMap;
import java.util.concurrent.ExecutionException;


import io.github.polysmee.R;
import io.github.polysmee.database.DatabaseFactory;
import io.github.polysmee.login.AuthenticationFactory;
import io.github.polysmee.login.MainUserSingleton;
import io.github.polysmee.znotification.AppointmentReminderNotificationSetupListener;
import io.github.polysmee.room.fragments.RoomActivityParticipantsFragment;

import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed;
import static com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertNotDisplayed;
import static com.schibsted.spain.barista.interaction.BaristaClickInteractions.clickOn;
import static com.schibsted.spain.barista.interaction.BaristaSleepInteractions.sleep;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(JUnit4.class)
public class RoomActivityParticipantsFragmentNotOwnerTest {
    private static final String username1 = "Mathis L'utilisateur";
    private static String id2 = "oierytuhjdfbsgvcwx";
    private static final String username2 = "Sami L'imposteur";

    private static final String appointmentTitle = "It's a title";
    private static String appointmentId = "ahvwcxtdfytazazeiu";
    private static final String appointmentCourse = "Totally not SWENG";
    private static final long appointmentStart = 265655445;


    @BeforeClass
    public static void setUp() throws Exception {
        AppointmentReminderNotificationSetupListener.setIsNotificationSetterEnable(false);
        DatabaseFactory.setTest();
        AuthenticationFactory.setTest();
        FirebaseApp.clearInstancesForTest();
        FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext());
        Tasks.await(AuthenticationFactory.getAdaptedInstance().createUserWithEmailAndPassword("RoomActivityParticipantsFragmentNotOwnerTest@gmail.com", "fakePassword"));
        DatabaseFactory.getAdaptedInstance().getReference("users").child(MainUserSingleton.getInstance().getId()).child("name").setValue(username1);
        DatabaseFactory.getAdaptedInstance().getReference("users").child(id2).child("name").setValue(username2);

        DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId).child("title").setValue(appointmentTitle);
        DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId).child("course").setValue(appointmentCourse);
        DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId).child("owner").setValue(id2);
        DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId).child("start").setValue(appointmentStart);
        DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId).child("participants").child(MainUserSingleton.getInstance().getId()).setValue(true);
        DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId).child("participants").child(id2).setValue(true);
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
        HashMap usr = (HashMap) Tasks.await(DatabaseFactory.getAdaptedInstance().getReference("users").child(MainUserSingleton.getInstance().getId()).child("friends").get()).getValue();
        assertEquals(1, usr.size());
        clickOn(R.id.roomActivityManageParticipantAsFriendButton);
        Thread.sleep(3000);
        HashMap usr1 = (HashMap) Tasks.await(DatabaseFactory.getAdaptedInstance().getReference("users").child(MainUserSingleton.getInstance().getId()).child("friends").get()).getValue();
        assertNull(usr1);
    }
}