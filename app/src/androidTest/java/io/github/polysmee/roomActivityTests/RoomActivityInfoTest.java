package io.github.polysmee.roomActivityTests;

import android.content.Intent;

import io.github.polysmee.R;
import io.github.polysmee.database.DatabaseFactory;
import io.github.polysmee.interfaces.User;
import io.github.polysmee.login.AuthenticationFactory;
import io.github.polysmee.login.MainUserSingleton;
import io.github.polysmee.room.RoomActivityInfo;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.NoMatchingViewException;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.Serializable;
import java.security.SecureRandom;
import java.util.Date;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed;
import static com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertNotDisplayed;
import static com.schibsted.spain.barista.interaction.BaristaClickInteractions.clickOn;
import static com.schibsted.spain.barista.interaction.BaristaDialogInteractions.clickDialogNegativeButton;
import static com.schibsted.spain.barista.interaction.BaristaDialogInteractions.clickDialogPositiveButton;
import static com.schibsted.spain.barista.interaction.BaristaEditTextInteractions.writeTo;
import static com.schibsted.spain.barista.interaction.BaristaSleepInteractions.sleep;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(JUnit4.class)
public class RoomActivityInfoTest {
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
        Random idGen = new SecureRandom();
        RoomActivityInfoTest.id2 = Long.toString(idGen.nextLong());
        RoomActivityInfoTest.appointmentId = Long.toString(idGen.nextLong());
        RoomActivityInfoTest.userEmail = idGen.nextInt(200) +"@gmail.com";

        DatabaseFactory.setTest();
        AuthenticationFactory.setTest();
        FirebaseApp.clearInstancesForTest();
        FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext());
        Tasks.await(AuthenticationFactory.getAdaptedInstance().createUserWithEmailAndPassword(userEmail, "fakePassword"));
        DatabaseFactory.getAdaptedInstance().getReference("users").child(MainUserSingleton.getInstance().getId()).child("name").setValue(username1);
        DatabaseFactory.getAdaptedInstance().getReference("users").child(id2).child("name").setValue(username2);
        DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId).child("title").setValue(appointmentTitle);
        DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId).child("course").setValue(appointmentCourse);
        DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId).child("start").setValue(appointmentStart);
        DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId).child("owner").setValue(MainUserSingleton.getInstance().getId());
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
    public void editTitleShouldEditDatabaseValue() {
        String newValue = "Hey hey, I'm a new title";
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), RoomActivityInfo.class);

        intent.putExtra(RoomActivityInfo.APPOINTMENT_KEY, appointmentId);

        try(ActivityScenario ignored = ActivityScenario.launch(intent)) {
            sleep(1, SECONDS);
            clickOn(appointmentTitle);
            writeTo(R.id.roomInfoDialogEdit, newValue);
            closeSoftKeyboard();
            clickDialogPositiveButton();
            sleep(2, SECONDS);
            assertDisplayed(newValue);
        }

        DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId).child("title").setValue(appointmentTitle);
    }

    @Test
    public void editCourseShouldEditDatabaseValue() {
        String newValue = "Ok, ok it's SWENG";
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), RoomActivityInfo.class);

        intent.putExtra(RoomActivityInfo.APPOINTMENT_KEY, appointmentId);

        try(ActivityScenario ignored = ActivityScenario.launch(intent)) {
            sleep(1, SECONDS);
            clickOn(appointmentCourse);
            writeTo(R.id.roomInfoDialogEdit, newValue);
            closeSoftKeyboard();
            clickOn("OK");
            sleep(2, SECONDS);
            assertDisplayed(newValue);
        }

        DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId).child("course").setValue(appointmentCourse);
    }
}