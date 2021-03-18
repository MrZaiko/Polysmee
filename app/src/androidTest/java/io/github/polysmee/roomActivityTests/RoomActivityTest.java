package io.github.polysmee.roomActivityTests;

import android.content.Intent;

import androidx.test.InstrumentationRegistry;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.intent.Intents.intended;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import io.github.polysmee.R;
import io.github.polysmee.interfaces.User;
import io.github.polysmee.room.RoomActivity;
import io.github.polysmee.room.RoomActivityInfo;

import static androidx.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static androidx.test.espresso.matcher.ViewMatchers.isChecked;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isNotChecked;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertContains;
import static com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed;
import static com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertNotContains;
import static com.schibsted.spain.barista.interaction.BaristaClickInteractions.clickOn;
import static com.schibsted.spain.barista.interaction.BaristaMenuClickInteractions.clickMenu;
import static com.schibsted.spain.barista.interaction.BaristaSleepInteractions.sleep;
import static com.schibsted.spain.barista.interaction.BaristaViewPagerInteractions.swipeViewPagerForward;
import static com.schibsted.spain.barista.internal.viewaction.SwipeActions.swipeLeft;

@RunWith(AndroidJUnit4.class)
public class RoomActivityTest {
    @Rule
    public ActivityScenarioRule<RoomActivity> testRule = new ActivityScenarioRule<>(RoomActivity.class);

    @Test
    public void titleOfTheActivityShouldBeTheAppointmentTitle() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), RoomActivity.class);
        String name = "This is a very long title";
        intent.putExtra(RoomActivity.APPOINTMENT_KEY, new TestAppointment(0,0,"", name, new HashSet<>()));

        try (ActivityScenario<RoomActivity> ignored = ActivityScenario.launch(intent)){
            assertContains(name);
        }
    }

    @Test
    public void swipeLeftShouldSelectParticipantTab() {
        withId(R.id.roomActivityMessagesTab).matches(isChecked());
        withId(R.id.roomActivityParticipantsTab).matches(isNotChecked());
        swipeViewPagerForward(R.id.roomActivityPager);
        withId(R.id.roomActivityParticipantsTab).matches(isChecked());
        withId(R.id.roomActivityMessagesTab).matches(isNotChecked());
    }

    @Test
    public void infoItemMenuShouldFireAnIntentWithTheCurrentAppointment() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), RoomActivity.class);
        User testUser = new TestUser("jhbjk", "hoh", "lkjklj", null);
        Set<User> set = new HashSet<>();
        set.add(testUser);
        TestAppointment expectedAppointment = new TestAppointment(10,4654564,"fdsdfsfs", "kljkjsdhfjklfsd", set);

        intent.putExtra(RoomActivity.APPOINTMENT_KEY, expectedAppointment);

        try (ActivityScenario<RoomActivity> ignored = ActivityScenario.launch(intent)){
            openActionBarOverflowOrOptionsMenu(ApplicationProvider.getApplicationContext());
            Intents.init();
            onView(withText("Info")).perform(click());
            intended(hasExtra(RoomActivityInfo.APPOINTMENT_KEY, expectedAppointment));
            Intents.release();
        }
    }

}
