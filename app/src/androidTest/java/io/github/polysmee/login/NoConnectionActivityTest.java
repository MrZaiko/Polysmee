package io.github.polysmee.login;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed;
import static com.schibsted.spain.barista.interaction.BaristaClickInteractions.clickOn;

@RunWith(AndroidJUnit4.class)
public class NoConnectionActivityTest {

    @Test
    public void goesBackToLoginAfterNetworkFailure() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), NoConnectionActivity.class);
        Intents.init();
        try(ActivityScenario<NoConnectionActivity> ignored = ActivityScenario.launch(intent)){
            clickOn("RETRY");
            assertDisplayed("LOGIN");
        }
        Intents.release();
    }
}
