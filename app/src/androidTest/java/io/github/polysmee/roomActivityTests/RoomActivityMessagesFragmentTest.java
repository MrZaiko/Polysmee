package io.github.polysmee.roomActivityTests;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.intent.Intents;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.FirebaseApp;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import io.github.polysmee.R;
import io.github.polysmee.database.DatabaseFactory;
import io.github.polysmee.invites.InvitesManagementActivity;
import io.github.polysmee.login.AuthenticationFactory;
import io.github.polysmee.login.MainUserSingleton;
import io.github.polysmee.znotification.AppointmentReminderNotificationSetupListener;
import io.github.polysmee.room.fragments.RoomActivityMessagesFragment;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertContains;
import static com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed;
import static com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertNotContains;
import static com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertNotDisplayed;
import static com.schibsted.spain.barista.intents.BaristaIntents.mockAndroidCamera;
import static com.schibsted.spain.barista.interaction.BaristaClickInteractions.clickOn;
import static com.schibsted.spain.barista.interaction.BaristaClickInteractions.longClickOn;
import static com.schibsted.spain.barista.interaction.BaristaDialogInteractions.clickDialogPositiveButton;
import static com.schibsted.spain.barista.interaction.BaristaEditTextInteractions.writeTo;
import static com.schibsted.spain.barista.interaction.BaristaMenuClickInteractions.clickMenu;
import static com.schibsted.spain.barista.interaction.BaristaSleepInteractions.sleep;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertTrue;

@RunWith(JUnit4.class)
public class RoomActivityMessagesFragmentTest {
    private static final String username1 = "Mathis L'utilisateur";
    private static String id2 = "azeeazsqdsq";
    private static final String username2 = "Sami L'imposteur";

    private static String appointmentId = "lkdfjswxcuyt";
    private static String firstMessageId = "jkxwcoihjcwxp";
    private static final String firstMessage = "I'm a message";

    private static String secondMessageId = "poisdoufoiq";
    private static final String secondMessage = "I'm a better message";


    @BeforeClass
    public static void setUp() throws Exception {
        AppointmentReminderNotificationSetupListener.setIsNotificationSetterEnable(false);
        DatabaseFactory.setTest();
        AuthenticationFactory.setTest();

        FirebaseApp.clearInstancesForTest();
        FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext());
        Tasks.await(AuthenticationFactory.getAdaptedInstance().createUserWithEmailAndPassword("RoomActivityMessagesFragmentTest@gmail.com", "fakePassword"));
        DatabaseFactory.getAdaptedInstance().getReference("users").child(MainUserSingleton.getInstance().getId()).child("name").setValue(username1);
        DatabaseFactory.getAdaptedInstance().getReference("users").child(id2).child("name").setValue(username2);
        DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId).child("owner").setValue(MainUserSingleton.getInstance().getId());
        DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId).child("participants").child(MainUserSingleton.getInstance().getId()).setValue(true);
        DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId).child("participants").child(id2).setValue(true);
        DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId).child("messages").child(firstMessageId).child("content").setValue(firstMessage);
        DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId).child("messages").child(firstMessageId).child("sender").setValue(id2);
        DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId).child("messages").child(secondMessageId).child("content").setValue(secondMessage);
        DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId).child("messages").child(secondMessageId).child("sender").setValue(MainUserSingleton.getInstance().getId());
    }


    @AfterClass
    public static void clearUp(){
        DatabaseFactory.getAdaptedInstance().getReference("users").setValue(null);
        DatabaseFactory.getAdaptedInstance().getReference("appointments").setValue(null);
    }

    @Test
    public void messagesShouldBeDisplayed() {
        Bundle bundle = new Bundle();
        bundle.putString(RoomActivityMessagesFragment.MESSAGES_KEY, appointmentId);
        FragmentScenario.launchInContainer(RoomActivityMessagesFragment.class, bundle);
        sleep(1, SECONDS);
        assertDisplayed(firstMessage);
        assertDisplayed(secondMessage);
    }

    @Test
    public void currentUserCanEditItsMessages() {
        String newMsg = "OMG, it's working";
        Bundle bundle = new Bundle();
        bundle.putString(RoomActivityMessagesFragment.MESSAGES_KEY, appointmentId);
        FragmentScenario.launchInContainer(RoomActivityMessagesFragment.class, bundle);
        sleep(1, SECONDS);
        longClickOn(secondMessage);
        clickMenu(R.id.roomEditMessageMenuEdit);
        writeTo(R.id.roomActivityEditDialogText, newMsg);
        closeSoftKeyboard();
        clickDialogPositiveButton();
        assertDisplayed(newMsg);
        DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId).child("messages").child(secondMessageId).child("content").setValue(secondMessage);
    }

    @Test
    public void currentUserCanDeleteItsMessages() {
        Bundle bundle = new Bundle();
        bundle.putString(RoomActivityMessagesFragment.MESSAGES_KEY, appointmentId);
        FragmentScenario.launchInContainer(RoomActivityMessagesFragment.class, bundle);
        sleep(1, SECONDS);
        longClickOn(secondMessage);
        clickMenu(R.id.roomEditMessageMenuDelete);

        boolean thrown = false;
        try {
            onView(withText(secondMessage)).check(matches(isDisplayed()));
        } catch (NoMatchingViewException e) {
            thrown = true;
        }

        assertTrue(thrown);

        DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId).child("messages").child(secondMessageId).child("sender").setValue(MainUserSingleton.getInstance().getId());
        DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId).child("messages").child(secondMessageId).child("content").setValue(secondMessage);
    }

    @Test
    public void sendButtonShouldClearMessageText() {
        Bundle bundle = new Bundle();
        bundle.putString(RoomActivityMessagesFragment.MESSAGES_KEY, appointmentId);
        FragmentScenario.launchInContainer(RoomActivityMessagesFragment.class, bundle);
        sleep(1, SECONDS);
        String message = "Sent message 1";
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
        sleep(1, SECONDS);
        String message = "Sent message 2";
        onView(withId(R.id.roomActivityMessageText)).perform(typeText(message), closeSoftKeyboard());
        onView(withId(R.id.roomActivitySendMessageButton)).perform(click());
        assertNotContains(R.id.roomActivityMessageText, message);
        assertDisplayed(message);
    }

    @Test
    public void longClickOnItsMessagesShouldOpenEditMenu() {
        Bundle bundle = new Bundle();
        bundle.putString(RoomActivityMessagesFragment.MESSAGES_KEY, appointmentId);
        FragmentScenario.launchInContainer(RoomActivityMessagesFragment.class, bundle);
        sleep(1, SECONDS);
        longClickOn(secondMessage);
        assertDisplayed("Choose an option");
    }

    @Test
    public void openGalleryWorksCorrectly() {
        Bundle bundle = new Bundle();
        bundle.putString(RoomActivityMessagesFragment.MESSAGES_KEY, appointmentId);
        FragmentScenario.launchInContainer(RoomActivityMessagesFragment.class, bundle);

        Intents.init();
        clickOn(R.id.roomActivitySendPictureButton);
        intended(hasAction("android.intent.action.PICK"));
        Intents.release();
    }

    @Test
    public void openPhotoWorksCorrectly() {
        Bundle bundle = new Bundle();
        bundle.putString(RoomActivityMessagesFragment.MESSAGES_KEY, appointmentId);
        FragmentScenario.launchInContainer(RoomActivityMessagesFragment.class, bundle);

        Intents.init();
        //mockAndroidCamera();
        clickOn(R.id.roomActivityTakePictureButton);
        Intents.release();
    }
}