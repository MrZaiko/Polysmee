package io.github.polysmee.settings;


import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.FirebaseApp;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeUnit;

import io.github.polysmee.R;
import io.github.polysmee.database.DatabaseFactory;
import io.github.polysmee.login.AuthenticationFactory;
import io.github.polysmee.login.MainUser;
import io.github.polysmee.profile.FriendsActivity;
import io.github.polysmee.znotification.AppointmentReminderNotification;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed;
import static com.schibsted.spain.barista.interaction.BaristaClickInteractions.clickOn;
import static com.schibsted.spain.barista.interaction.BaristaDialogInteractions.clickDialogPositiveButton;
import static com.schibsted.spain.barista.interaction.BaristaEditTextInteractions.writeTo;
import static com.schibsted.spain.barista.interaction.BaristaSleepInteractions.sleep;

@RunWith(AndroidJUnit4.class)
public class FriendsActivityTest {

    private static final String username1 = "Cortex91DesPyramides";
    private static final String id2 = "yoiqsdaoqreidfoefbcxcc";
    private static final String username2 = "Cringe";

    @BeforeClass
    public static void setUp() throws Exception {
        AppointmentReminderNotification.setIsNotificationSetterEnable(false);
        DatabaseFactory.setTest();
        AuthenticationFactory.setTest();
        FirebaseApp.clearInstancesForTest();
        FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext());
        Tasks.await(AuthenticationFactory.getAdaptedInstance().createUserWithEmailAndPassword("FriendsActivityTest@gmail.com", "fakePassword"));
        DatabaseFactory.getAdaptedInstance().getReference("users").child(MainUser.getMainUser().getId()).child("name").setValue(username1);
        DatabaseFactory.getAdaptedInstance().getReference("users").child(id2).child("name").setValue(username2);
    }
    @AfterClass
    public static void clean() {
        DatabaseFactory.getAdaptedInstance().getReference().setValue(null);
    }

    @Test
    public void addingAndRemovingANewFriendTest() {
        Intent intent = new Intent(getApplicationContext(), FriendsActivity.class);
        try (ActivityScenario<FriendsActivity> ignored = ActivityScenario.launch(intent)) {
            sleep(5, TimeUnit.SECONDS);
            clickOn(R.id.friendAddTextView);
            writeTo(R.id.friendAddTextView, "Cringe");
            sleep(3, TimeUnit.SECONDS);
            closeSoftKeyboard();
            clickOn(R.id.friendActivityAddButton);
            sleep(2, TimeUnit.SECONDS);
            assertDisplayed(username2);
            clickOn(username2);
            sleep(2, TimeUnit.SECONDS);
            pressBack();
            clickOn(R.id.friendEntryRemoveFriendButton);
            sleep(2, TimeUnit.SECONDS);
            onView(withText(username2)).check(doesNotExist());
        }
    }

    @Test
    public void errorMessageWhenTryingToAddThemselves() {
        Intent intent = new Intent(getApplicationContext(), FriendsActivity.class);
        try (ActivityScenario<FriendsActivity> ignored = ActivityScenario.launch(intent)) {
            sleep(1, TimeUnit.SECONDS);
            writeTo(R.id.friendAddTextView, username1);
            closeSoftKeyboard();
            clickOn(R.id.friendActivityAddButton);
            sleep(1, TimeUnit.SECONDS);
            assertDisplayed("Oops");
            clickDialogPositiveButton();
        }
    }

    @Test
    public void errorMessageWhenTryingToAddNonexistentUser() {
        Intent intent = new Intent(getApplicationContext(), FriendsActivity.class);
        try (ActivityScenario<FriendsActivity> ignored = ActivityScenario.launch(intent)) {
            sleep(1, TimeUnit.SECONDS);
            writeTo(R.id.friendAddTextView, "PleaseLetMeGoBro");
            closeSoftKeyboard();
            clickOn(R.id.friendActivityAddButton);
            sleep(1, TimeUnit.SECONDS);
            assertDisplayed("OK");
            clickDialogPositiveButton();
        }
    }


}
