package io.github.polysmee.roomActivityTests;

import android.os.Bundle;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.NoMatchingViewException;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import junit.framework.AssertionFailedError;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.concurrent.ExecutionException;

import io.github.polysmee.login.MainUserSingleton;
import io.github.polysmee.room.fragments.RoomActivityParticipantsFragment;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed;
import static com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertNotDisplayed;
import static com.schibsted.spain.barista.interaction.BaristaClickInteractions.clickOn;
import static com.schibsted.spain.barista.interaction.BaristaSleepInteractions.sleep;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertTrue;

@RunWith(JUnit4.class)
public class RoomActivityParticipantsFragmentTest {


    private static final String username1 = "Mathis L'utilisateur";
    private static final String id2 = "-SFDkjsfdl";
    private static final String username2 = "Sami L'imposteur";

    private static final String appointmentTitle = "It's a title";
    private static final String appointmentId = "-lsdqfkhfdlksjhmf";
    private static final String appointmentCourse = "Totally not SWENG";
    private static final long appointmentStart = 265655445;


    @BeforeClass
    public static void setUp() throws Exception {
        FirebaseApp.clearInstancesForTest();
        FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext());
        Tasks.await(FirebaseAuth.getInstance().createUserWithEmailAndPassword("polysmee134@gmail.com", "fakePassword"));
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
        Tasks.await(FirebaseAuth.getInstance().signInWithEmailAndPassword("polysmee134@gmail.com", "fakePassword"));
        FirebaseDatabase.getInstance().getReference("users").child(MainUserSingleton.getInstance().getId()).setValue(null);
        FirebaseDatabase.getInstance().getReference("users").child(id2).setValue(null);
        FirebaseDatabase.getInstance().getReference("appointments").child(appointmentId).setValue(null);
        Tasks.await(FirebaseAuth.getInstance().getCurrentUser().delete());
    }

    @Test
    public void participantsAreCorrectlyDisplayed() {
        Bundle bundle = new Bundle();
        bundle.putString(RoomActivityParticipantsFragment.PARTICIPANTS_KEY, appointmentId);
        FragmentScenario.launchInContainer(RoomActivityParticipantsFragment.class, bundle);
        sleep(3, SECONDS);
        assertDisplayed(username1);
        assertDisplayed(username2);
    }

    @Test
    public void removeButtonShouldRemoveTheParticipant() {
        Bundle bundle = new Bundle();
        bundle.putString(RoomActivityParticipantsFragment.PARTICIPANTS_KEY, appointmentId);
        FragmentScenario.launchInContainer(RoomActivityParticipantsFragment.class, bundle);
        sleep(2, SECONDS);
        clickOn(username2);
        clickOn("Remove");
        sleep(2, SECONDS);

        boolean thrown = false;

        try {
            onView(withText(username2)).check(matches(isDisplayed()));
        } catch (NoMatchingViewException e) {
            thrown = true;
        }

        assertTrue(thrown);

        FirebaseDatabase.getInstance().getReference("appointments").child(appointmentId).child("participants").child(id2).setValue(true);
    }

}
