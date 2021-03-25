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

import java.util.Random;
import java.util.concurrent.ExecutionException;

import io.github.polysmee.R;
import io.github.polysmee.database.DatabaseFactory;
import io.github.polysmee.login.AuthenticationFactory;
import io.github.polysmee.login.MainUserSingleton;
import io.github.polysmee.room.fragments.RoomActivityMessagesFragment;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertContains;
import static com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed;
import static com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertNotContains;
import static com.schibsted.spain.barista.interaction.BaristaSleepInteractions.sleep;
import static java.util.concurrent.TimeUnit.SECONDS;

@RunWith(JUnit4.class)
public class RoomActivityMessagesFragmentTest {
    private static String userEmail;

    private static final String username1 = "Mathis L'utilisateur";
    private static String id2;
    private static final String username2 = "Sami L'imposteur";

    private static String appointmentId;
    private static String firstMessageId;
    private static final String firstMessage = "I'm a message";


    @BeforeClass
    public static void setUp() throws Exception {
        Random idGen = new Random();
        RoomActivityMessagesFragmentTest.id2 = Long.toString(idGen.nextLong());
        RoomActivityMessagesFragmentTest.appointmentId = Long.toString(idGen.nextLong());
        RoomActivityMessagesFragmentTest.firstMessageId = Long.toString(idGen.nextLong());
        RoomActivityMessagesFragmentTest.userEmail = idGen.nextInt(500) +"@gmail.com";

        DatabaseFactory.setTest();
        AuthenticationFactory.setTest();

        FirebaseApp.clearInstancesForTest();
        FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext());
        Tasks.await(AuthenticationFactory.getAdaptedInstance().createUserWithEmailAndPassword(userEmail, "fakePassword"));
        DatabaseFactory.getAdaptedInstance().getReference("users").child(MainUserSingleton.getInstance().getId()).child("name").setValue(username1);
        DatabaseFactory.getAdaptedInstance().getReference("users").child(id2).child("name").setValue(username2);

        DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId).child("participants").child(MainUserSingleton.getInstance().getId()).setValue(true);
        DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId).child("participants").child(id2).setValue(true);
        DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId).child("messages").child(firstMessageId).child("content").setValue(firstMessage);
        DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId).child("messages").child(firstMessageId).child("sender").setValue(id2);

    }

    @AfterClass
    public static void delete() throws ExecutionException, InterruptedException {
        Tasks.await(AuthenticationFactory.getAdaptedInstance().signInWithEmailAndPassword(userEmail, "fakePassword"));
        DatabaseFactory.getAdaptedInstance().getReference("users").child(MainUserSingleton.getInstance().getId()).setValue(null);
        DatabaseFactory.getAdaptedInstance().getReference("users").child(id2).setValue(null);
        DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId).setValue(null);
        Tasks.await(AuthenticationFactory.getAdaptedInstance().getCurrentUser().delete());
    }

    @Test
    public void messagesShouldBeDisplayed() {
        Bundle bundle = new Bundle();
        bundle.putString(RoomActivityMessagesFragment.MESSAGES_KEY, appointmentId);
        FragmentScenario.launchInContainer(RoomActivityMessagesFragment.class, bundle);
        sleep(1, SECONDS);
        assertDisplayed(firstMessage);
    }

    @Test
    public void sendButtonShouldClearMessageText() {
        Bundle bundle = new Bundle();
        bundle.putString(RoomActivityMessagesFragment.MESSAGES_KEY, appointmentId);
        FragmentScenario.launchInContainer(RoomActivityMessagesFragment.class, bundle);
        String message = "A message";
        onView(withId(R.id.roomActivityMessageText)).perform(typeText(message), closeSoftKeyboard());
        assertDisplayed(R.id.roomActivityMessageText, message);
        onView(withId(R.id.roomActivitySendMessageButton)).perform(click());
        assertContains(R.id.roomActivityMessageText, "");
    }

    @Test
    public void messageShouldCorrectlyBeSent() {
        Bundle bundle = new Bundle();
        bundle.putString(RoomActivityMessagesFragment.MESSAGES_KEY, appointmentId);
        FragmentScenario.launchInContainer(RoomActivityMessagesFragment.class, bundle);
        String message = "A message";
        onView(withId(R.id.roomActivityMessageText)).perform(typeText(message), closeSoftKeyboard());
        onView(withId(R.id.roomActivitySendMessageButton)).perform(click());
        assertNotContains(R.id.roomActivityMessageText, message);
        assertDisplayed(message);
    }
}
