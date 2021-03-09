package io.github.polysmee.roomActivityTests;

import androidx.fragment.app.testing.FragmentScenario;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import io.github.polysmee.R;
import io.github.polysmee.room.fragments.roomActivityMessagesFragment;

import static com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertContains;
import static com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed;
import static com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertNotContains;
import static com.schibsted.spain.barista.interaction.BaristaClickInteractions.clickOn;
import static com.schibsted.spain.barista.interaction.BaristaEditTextInteractions.writeTo;

@RunWith(JUnit4.class)
public class roomActivityMessagesFragmentTest {
    @Test
    public void sendButtonShouldClearMessageText() {
        FragmentScenario.launchInContainer(roomActivityMessagesFragment.class);
        String message = "A message";
        writeTo(R.id.roomActivityMessageText, message);
        assertDisplayed(R.id.roomActivityMessageText, message);
        clickOn(R.id.roomActivitySendMessageButton);
        assertContains(R.id.roomActivityMessageText, "");
    }

    @Test
    public void messageShouldCorrectlyBeSent() {
        FragmentScenario.launchInContainer(roomActivityMessagesFragment.class);
        String message = "A message";
        writeTo(R.id.roomActivityMessageText, message);
        clickOn(R.id.roomActivitySendMessageButton);
        assertNotContains(R.id.roomActivityMessageText, message);
        assertDisplayed(message);
    }

    @Test
    public void receiveButtonShouldClearMessageText() {
        FragmentScenario.launchInContainer(roomActivityMessagesFragment.class);
        String message = "A message";
        writeTo(R.id.roomActivityMessageText, message);
        clickOn(R.id.roomActivityReceiveMessageButton);
        assertContains(R.id.roomActivityMessageText, "");
    }

    @Test
    public void messageShouldCorrectlyBeReceived() {
        FragmentScenario.launchInContainer(roomActivityMessagesFragment.class);
        String message = "A message";
        writeTo(R.id.roomActivityMessageText, message);
        assertDisplayed(R.id.roomActivityMessageText, message);
        clickOn(R.id.roomActivityReceiveMessageButton);
        assertNotContains(R.id.roomActivityMessageText, message);
        assertContains(message);
    }
}
