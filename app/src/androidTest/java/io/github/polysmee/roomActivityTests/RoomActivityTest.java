package io.github.polysmee.roomActivityTests;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.swipeLeft;
import static androidx.test.espresso.intent.Intents.intended;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import io.github.polysmee.R;
import io.github.polysmee.interfaces.User;
import io.github.polysmee.login.MainUserSingleton;
import io.github.polysmee.room.RoomActivity;
import io.github.polysmee.room.RoomActivityInfo;

import static androidx.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static androidx.test.espresso.matcher.ViewMatchers.isChecked;
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
import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class RoomActivityTest {
    private static String userEmail;

    private static final String username1 = "Mathis L'utilisateur";
    private static String id2;
    private static final String username2 = "Sami L'imposteur";

    private static final String appointmentTitle = "It's a title";
    private static String appointmentId;
    private static final String appointmentCourse = "Totally not SWENG";
    private static final long appointmentStart = 265655445;


    @BeforeClass
    public static void setUp() throws Exception {
        Random idGen = new Random();
        RoomActivityTest.id2 = Long.toString(idGen.nextLong());
        RoomActivityTest.appointmentId = Long.toString(idGen.nextLong());
        RoomActivityTest.userEmail = idGen.nextInt(500) +"@gmail.com";

        FirebaseApp.clearInstancesForTest();
        FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext());
        Tasks.await(FirebaseAuth.getInstance().createUserWithEmailAndPassword(userEmail, "fakePassword"));
        FirebaseDatabase.getInstance().getReference("users").child(MainUserSingleton.getInstance().getId()).child("name").setValue(username1);
        FirebaseDatabase.getInstance().getReference("users").child(id2).child("name").setValue(username2);

        FirebaseDatabase.getInstance().getReference("appointments").child(appointmentId).child("title").setValue(appointmentTitle);
        FirebaseDatabase.getInstance().getReference("appointments").child(appointmentId).child("course").setValue(appointmentCourse);
        FirebaseDatabase.getInstance().getReference("appointments").child(appointmentId).child("start").setValue(appointmentStart);
        FirebaseDatabase.getInstance().getReference("appointments").child(appointmentId).child("participants").child(MainUserSingleton.getInstance().getId()).setValue(true);
        FirebaseDatabase.getInstance().getReference("appointments").child(appointmentId).child("participants").child(id2).setValue(true);
    }

    @AfterClass
    public static void delete() throws ExecutionException, InterruptedException {
        Tasks.await(FirebaseAuth.getInstance().signInWithEmailAndPassword(userEmail, "fakePassword"));
        FirebaseDatabase.getInstance().getReference("users").child(MainUserSingleton.getInstance().getId()).setValue(null);
        FirebaseDatabase.getInstance().getReference("users").child(id2).setValue(null);
        FirebaseDatabase.getInstance().getReference("appointments").child(appointmentId).setValue(null);
        Tasks.await(FirebaseAuth.getInstance().getCurrentUser().delete());
    }


    @Test
    public void titleOfTheActivityShouldBeTheAppointmentTitle() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), RoomActivity.class);
        intent.putExtra(RoomActivity.APPOINTMENT_KEY, appointmentId);

        try (ActivityScenario<RoomActivity> ignored = ActivityScenario.launch(intent)){
            assertContains(appointmentTitle);
        }
    }

    @Test
    public void infoItemMenuShouldFireAnIntentWithTheCurrentAppointment() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), RoomActivity.class);

        intent.putExtra(RoomActivity.APPOINTMENT_KEY, appointmentId);

        try (ActivityScenario<RoomActivity> ignored = ActivityScenario.launch(intent)){
            openActionBarOverflowOrOptionsMenu(ApplicationProvider.getApplicationContext());
            Intents.init();
            onView(withText("Info")).perform(click());
            intended(hasExtra(RoomActivityInfo.APPOINTMENT_KEY, appointmentId));
            Intents.release();
        }
    }



    @Test
    public void participantsAreCorrectlyDisplayed() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), RoomActivity.class);

        intent.putExtra(RoomActivity.APPOINTMENT_KEY, appointmentId);

        try (ActivityScenario<RoomActivity> ignored = ActivityScenario.launch(intent)){
            swipeViewPagerForward();
            sleep(2, TimeUnit.SECONDS);
            assertDisplayed(username1);
            assertDisplayed(username2);
        }
    }
}