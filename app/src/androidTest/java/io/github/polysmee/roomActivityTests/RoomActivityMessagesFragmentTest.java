package io.github.polysmee.roomActivityTests;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.core.app.ApplicationProvider;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

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
import static com.schibsted.spain.barista.interaction.BaristaClickInteractions.clickOn;
import static com.schibsted.spain.barista.internal.viewaction.SleepViewAction.sleep;

@RunWith(JUnit4.class)
public class RoomActivityMessagesFragmentTest {

    private static final String username1 = "Mathis L'utilisateur";

    @BeforeClass
    public static void setUp() throws Exception {
        FirebaseApp.clearInstancesForTest();
        FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext());
        Tasks.await(AuthenticationFactory.getAdaptedInstance().createUserWithEmailAndPassword("polysmee134@gmail.com", "fakePassword"));
        DatabaseFactory.getAdaptedInstance().getReference("users").child(MainUserSingleton.getInstance().getId()).child("name").setValue(username1);
    }

    @AfterClass
    public static void delete() throws ExecutionException, InterruptedException {
        Tasks.await(AuthenticationFactory.getAdaptedInstance().signInWithEmailAndPassword("polysmee134@gmail.com", "fakePassword"));
        DatabaseFactory.getAdaptedInstance().getReference("users").child(MainUserSingleton.getInstance().getId()).setValue(null);
        Tasks.await(AuthenticationFactory.getAdaptedInstance().getCurrentUser().delete());
    }

    @Test
    public void sendButtonShouldClearMessageText() {
        FragmentScenario.launchInContainer(RoomActivityMessagesFragment.class);
        String message = "A message";
        onView(withId(R.id.roomActivityMessageText)).perform(typeText(message), closeSoftKeyboard());
        assertDisplayed(R.id.roomActivityMessageText, message);
        onView(withId(R.id.roomActivitySendMessageButton)).perform(click());
        assertContains(R.id.roomActivityMessageText, "");
    }

    @Test
    public void messageShouldCorrectlyBeSent() {
        FragmentScenario.launchInContainer(RoomActivityMessagesFragment.class);
        String message = "A message";
        onView(withId(R.id.roomActivityMessageText)).perform(typeText(message), closeSoftKeyboard());
        onView(withId(R.id.roomActivitySendMessageButton)).perform(click());
        assertNotContains(R.id.roomActivityMessageText, message);
        sleep(2000);
        assertDisplayed(message);
    }


    @Test
    public void receiveButtonShouldClearMessageText() {
        FragmentScenario.launchInContainer(RoomActivityMessagesFragment.class);
        String message = "A message";
        onView(withId(R.id.roomActivityMessageText)).perform(typeText(message), closeSoftKeyboard());
        onView(withId(R.id.roomActivityReceiveMessageButton)).perform(click());
        assertContains(R.id.roomActivityMessageText, "");
    }

    @Test
    public void messageShouldCorrectlyBeReceived() {
        FragmentScenario.launchInContainer(RoomActivityMessagesFragment.class);
        String message = "A message";
        onView(withId(R.id.roomActivityMessageText)).perform(typeText(message), closeSoftKeyboard());
        assertDisplayed(R.id.roomActivityMessageText, message);
        onView(withId(R.id.roomActivityReceiveMessageButton)).perform(click());
        assertNotContains(R.id.roomActivityMessageText, message);
        sleep(2000);
        assertContains(message);
    }
}
