package io.github.polysmee.login;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.FirebaseApp;

import org.hamcrest.Matchers;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.github.polysmee.R;
import io.github.polysmee.database.DatabaseFactory;
import io.github.polysmee.znotification.AppointmentReminderNotificationSetupListener;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed;
import static com.schibsted.spain.barista.interaction.BaristaClickInteractions.clickOn;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class LoginActivityTest {

    @Test
    public void goesBackToLoginAfterCancel() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), LoginActivity.class);
        Intents.init();
        try(ActivityScenario<LoginActivity> ignored = ActivityScenario.launch(intent)){
            clickOn("LOGIN");
            pressBack();
            assertDisplayed("LOGIN");
        }
        Intents.release();
    }
}