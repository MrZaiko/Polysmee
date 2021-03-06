package io.github.polysmee.yroomActivityTests;

import android.os.Bundle;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.espresso.NoMatchingViewException;
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
import io.github.polysmee.internet.connection.InternetConnection;
import io.github.polysmee.database.UploadServiceFactory;
import io.github.polysmee.login.AuthenticationSingleton;
import io.github.polysmee.login.MainUser;
import io.github.polysmee.room.fragments.RoomActivityMessagesFragment;
import io.github.polysmee.znotification.AppointmentReminderNotification;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertContains;
import static com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed;
import static com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertNotContains;
import static com.schibsted.spain.barista.interaction.BaristaClickInteractions.clickOn;
import static com.schibsted.spain.barista.interaction.BaristaClickInteractions.longClickOn;
import static com.schibsted.spain.barista.interaction.BaristaDialogInteractions.clickDialogPositiveButton;
import static com.schibsted.spain.barista.interaction.BaristaEditTextInteractions.writeTo;
import static com.schibsted.spain.barista.interaction.BaristaMenuClickInteractions.clickMenu;
import static com.schibsted.spain.barista.interaction.BaristaScrollInteractions.scrollTo;
import static com.schibsted.spain.barista.interaction.BaristaSleepInteractions.sleep;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertTrue;

@RunWith(JUnit4.class)
public class RoomActivityMessagesFragmentTest {
    private static final String username1 = "Mathis L'utilisateur";
    private static final String id2 = "azeeazsqdsq";
    private static final String username2 = "Sami L'imposteur";

    private static final String appointmentId = "lkdfjswxcuyt";
    private static final String firstMessageId = "jkxwcoihjcwxp";
    private static final String firstMessage = "I'm a message";

    private static final String secondMessageId = "poisdoufoiq";
    private static final String secondMessage = "I'm a better message";

    private static final String thirdMessageId = "sdflskdfmlsdf";
    private static final String pictureId = "bigYOSHI";


    @BeforeClass
    public static void setUp() throws Exception {
        AppointmentReminderNotification.setIsNotificationSetterEnable(false);
        DatabaseSingleton.setLocal();
        AuthenticationSingleton.setLocal();
        CalendarUtilities.setTest(true, false);
        InternetConnection.setManuallyInternetConnectionForTests(true);
        UploadServiceFactory.setLocal(true);
        UploadServiceFactory.getAdaptedInstance().uploadImage(BigYoshi.getBytes(), pictureId, s -> {
        }, s -> {
        }, getApplicationContext());

        FirebaseApp.clearInstancesForTest();
        FirebaseApp.initializeApp(getApplicationContext());
        Tasks.await(AuthenticationSingleton.getAdaptedInstance().createUserWithEmailAndPassword("RoomActivityMessagesFragmentTest@gmail.com", "fakePassword"));
        DatabaseSingleton.getAdaptedInstance().getReference("users").child(MainUser.getMainUser().getId()).child("name").setValue(username1);
        DatabaseSingleton.getAdaptedInstance().getReference("users").child(MainUser.getMainUser().getId()).child("picture").setValue(pictureId);
        DatabaseSingleton.getAdaptedInstance().getReference("users").child(id2).child("name").setValue(username2);
        DatabaseSingleton.getAdaptedInstance().getReference("users").child(id2).child("picture").setValue(pictureId);

        DatabaseSingleton.getAdaptedInstance().getReference("appointments").child(appointmentId).child("owner").setValue(MainUser.getMainUser().getId());
        DatabaseSingleton.getAdaptedInstance().getReference("appointments").child(appointmentId).child("participants").child(MainUser.getMainUser().getId()).setValue(true);
        DatabaseSingleton.getAdaptedInstance().getReference("appointments").child(appointmentId).child("participants").child(id2).setValue(true);
        DatabaseSingleton.getAdaptedInstance().getReference("appointments").child(appointmentId).child("messages").child(thirdMessageId).child("sender").setValue(MainUser.getMainUser().getId());
        DatabaseSingleton.getAdaptedInstance().getReference("appointments").child(appointmentId).child("messages").child(thirdMessageId).child("content").setValue(pictureId);
        DatabaseSingleton.getAdaptedInstance().getReference("appointments").child(appointmentId).child("messages").child(thirdMessageId).child("isAPicture").setValue(true);
        DatabaseSingleton.getAdaptedInstance().getReference("appointments").child(appointmentId).child("messages").child(firstMessageId).child("content").setValue(firstMessage);
        DatabaseSingleton.getAdaptedInstance().getReference("appointments").child(appointmentId).child("messages").child(firstMessageId).child("sender").setValue(id2);
        DatabaseSingleton.getAdaptedInstance().getReference("appointments").child(appointmentId).child("messages").child(secondMessageId).child("content").setValue(secondMessage);
        DatabaseSingleton.getAdaptedInstance().getReference("appointments").child(appointmentId).child("messages").child(secondMessageId).child("sender").setValue(MainUser.getMainUser().getId());
    }

    @Test
    public void messagesShouldBeDisplayedAndClickOnPictureShouldWork() {
        Bundle bundle = new Bundle();
        bundle.putString(RoomActivityMessagesFragment.MESSAGES_KEY, appointmentId);
        FragmentScenario.launchInContainer(RoomActivityMessagesFragment.class, bundle);
        sleep(1, SECONDS);
        scrollTo(firstMessage);
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
        DatabaseSingleton.getAdaptedInstance().getReference("appointments").child(appointmentId).child("messages").child(secondMessageId).child("content").setValue(secondMessage);
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

        DatabaseSingleton.getAdaptedInstance().getReference("appointments").child(appointmentId).child("messages").child(secondMessageId).child("sender").setValue(MainUser.getMainUser().getId());
        DatabaseSingleton.getAdaptedInstance().getReference("appointments").child(appointmentId).child("messages").child(secondMessageId).child("content").setValue(secondMessage);
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