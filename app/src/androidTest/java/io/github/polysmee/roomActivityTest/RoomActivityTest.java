package io.github.polysmee.roomActivityTest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.LinearLayout;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.schibsted.spain.barista.internal.viewaction.SwipeActions;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashSet;
import java.util.Set;

import io.github.polysmee.R;
import io.github.polysmee.interfaces.User;
import io.github.polysmee.room.RoomActivity;
import io.github.polysmee.room.fragments.ActivityRoomParticipantsFragment;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertContains;
import static com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed;
import static com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertNotContains;
import static com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertNotDisplayed;
import static com.schibsted.spain.barista.interaction.BaristaClickInteractions.clickOn;
import static com.schibsted.spain.barista.interaction.BaristaEditTextInteractions.writeTo;
import static com.schibsted.spain.barista.interaction.BaristaSleepInteractions.sleep;
import static com.schibsted.spain.barista.internal.viewaction.SwipeActions.swipeLeft;
import static com.schibsted.spain.barista.internal.viewaction.SwipeActions.swipeRight;
import static java.util.concurrent.TimeUnit.SECONDS;

@RunWith(AndroidJUnit4.class)
public class RoomActivityTest {
    @Test
    public void titleOfTheActivityShouldBeTheAppointmentTitle() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), RoomActivity.class);
        String name = "This is a very long title";
        intent.putExtra(RoomActivity.APPOINTMENT_KEY, new TestAppointment(0,0,"", name, new HashSet<>()));

        try (ActivityScenario<RoomActivity> scenario = ActivityScenario.launch(intent)){
            assertContains(name);
        }
    }

    @Rule
    public ActivityScenarioRule<RoomActivity> testRule = new ActivityScenarioRule<>(RoomActivity.class);

    @Test
    public void sendButtonShouldClearMessageText() {
        String message = "A message";
        writeTo(R.id.roomActivityMessageText, message);
        assertDisplayed(R.id.roomActivityMessageText, message);
        clickOn(R.id.roomActivitySendMessageButton);
        assertContains(R.id.roomActivityMessageText, "");
    }

    @Test
    public void messageShouldCorrectlyBeSent() {
        String message = "A message";
        writeTo(R.id.roomActivityMessageText, message);
        assertDisplayed(R.id.roomActivityMessageText, message);
        clickOn(R.id.roomActivitySendMessageButton);
        assertNotContains(R.id.roomActivityMessageText, message);
        assertContains(message);
    }

    @Test
    public void receiveButtonShouldClearMessageText() {
        String message = "A message";
        writeTo(R.id.roomActivityMessageText, message);
        assertDisplayed(R.id.roomActivityMessageText, message);
        clickOn(R.id.roomActivityReceiveMessageButton);
        assertContains(R.id.roomActivityMessageText, "");
    }

    @Test
    public void messageShouldCorrectlyBeReceived() {
        String message = "A message";
        writeTo(R.id.roomActivityMessageText, message);
        assertDisplayed(R.id.roomActivityMessageText, message);
        clickOn(R.id.roomActivityReceiveMessageButton);
        assertNotContains(R.id.roomActivityMessageText, message);
        assertContains(message);
    }

    @Test
    public void aLeftSwipeShouldChangeTab() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), RoomActivity.class);
        String name1 = "This is a very long name";
        String surname1 = "kjugkhjgkhujg";
        User user1 = new TestUser("", name1, surname1, new HashSet<>());
        String name2 = "qdsqsddqsqdsdsqqsd";
        String surname2 = "azeazazeaze";
        User user2 = new TestUser("", name1, surname1, new HashSet<>());

        Set<User> set = new HashSet<>();
        set.add(user1);
        set.add(user2);

        intent.putExtra(RoomActivity.APPOINTMENT_KEY, new TestAppointment(0,0,"", "", set));

        try (ActivityScenario<RoomActivity> scenario = ActivityScenario.launch(intent)){
            //assertContains("SEND");
            onView(withId(R.id.roomActivityPager)).perform(SwipeActions.swipeLeft());
            //sleep(2, SECONDS);
            //assertNotDisplayed("SEND");
        }
    }
}
