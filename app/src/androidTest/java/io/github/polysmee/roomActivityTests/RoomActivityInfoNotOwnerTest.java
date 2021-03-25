package io.github.polysmee.roomActivityTests;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.NoMatchingViewException;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.FirebaseApp;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.security.SecureRandom;
import java.util.Random;

import io.github.polysmee.R;
import io.github.polysmee.database.DatabaseFactory;
import io.github.polysmee.login.AuthenticationFactory;
import io.github.polysmee.login.MainUserSingleton;
import io.github.polysmee.room.RoomActivityInfo;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed;
import static com.schibsted.spain.barista.interaction.BaristaClickInteractions.clickOn;
import static com.schibsted.spain.barista.interaction.BaristaDialogInteractions.clickDialogPositiveButton;
import static com.schibsted.spain.barista.interaction.BaristaEditTextInteractions.writeTo;
import static com.schibsted.spain.barista.interaction.BaristaSleepInteractions.sleep;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertTrue;

@RunWith(JUnit4.class)
public class RoomActivityInfoNotOwnerTest {
    private static final String username1 = "Mathis L'utilisateur";
    private static String id2;
    private static final String username2 = "Sami L'imposteur";

    private static final String appointmentTitle = "It's a title";
    private static String appointmentId;
    private static final String appointmentCourse = "Totally not SWENG";
    private static final long appointmentStart = 265655445;


    @BeforeClass
    public static void setUp() throws Exception {
        Random idGen = new SecureRandom();
        id2 = Long.toString(idGen.nextLong());
        appointmentId = Long.toString(idGen.nextLong());

        DatabaseFactory.setTest();
        AuthenticationFactory.setTest();
        FirebaseApp.clearInstancesForTest();
        FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext());
        Tasks.await(AuthenticationFactory.getAdaptedInstance().createUserWithEmailAndPassword("RoomActivityInfoNotOwnerTest@gmail.com", "fakePassword"));
        DatabaseFactory.getAdaptedInstance().getReference("users").child(MainUserSingleton.getInstance().getId()).child("name").setValue(username1);
        DatabaseFactory.getAdaptedInstance().getReference("users").child(id2).child("name").setValue(username2);
        DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId).child("title").setValue(appointmentTitle);
        DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId).child("course").setValue(appointmentCourse);
        DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId).child("start").setValue(appointmentStart);
        DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId).child("owner").setValue(id2);
        DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId).child("participants").child(MainUserSingleton.getInstance().getId()).setValue(true);
        DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId).child("participants").child(id2).setValue(true);
    }

    @Test
    public void appointmentShouldBeDisplayed() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), RoomActivityInfo.class);

        intent.putExtra(RoomActivityInfo.APPOINTMENT_KEY, appointmentId);

        try(ActivityScenario ignored = ActivityScenario.launch(intent)) {
            sleep(1, SECONDS);
            assertDisplayed(appointmentCourse);
            assertDisplayed(appointmentTitle);
        }
    }


    @Test
    public void onlyTheOwnerCanChangeRoomSettings() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), RoomActivityInfo.class);
        intent.putExtra(RoomActivityInfo.APPOINTMENT_KEY, appointmentId);

        try(ActivityScenario ignored = ActivityScenario.launch(intent)) {

            sleep(2, SECONDS);
            clickOn(appointmentCourse);

            boolean thrown = false;
            try {
                onView(withId(R.id.roomInfoDialogEdit)).check(matches(isDisplayed()));
            } catch (NoMatchingViewException e) {
                thrown = true;
            }
            assertTrue(thrown);
            thrown = false;
            clickOn(appointmentTitle);
            try {
                onView(withId(R.id.roomInfoDialogEdit)).check(matches(isDisplayed()));
            } catch (NoMatchingViewException e) {
                thrown = true;
            }
            assertTrue(thrown);
        }
    }
}