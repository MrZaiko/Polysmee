package io.github.polysmee.roomActivityTests;

import android.content.Intent;

import io.github.polysmee.R;
import io.github.polysmee.interfaces.User;
import io.github.polysmee.login.MainUserSingleton;
import io.github.polysmee.room.RoomActivityInfo;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;

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
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed;
import static com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertNotDisplayed;
import static com.schibsted.spain.barista.interaction.BaristaClickInteractions.clickOn;
import static com.schibsted.spain.barista.interaction.BaristaDialogInteractions.clickDialogNegativeButton;
import static com.schibsted.spain.barista.interaction.BaristaDialogInteractions.clickDialogPositiveButton;
import static com.schibsted.spain.barista.interaction.BaristaEditTextInteractions.writeTo;
import static com.schibsted.spain.barista.interaction.BaristaSleepInteractions.sleep;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertEquals;

@RunWith(JUnit4.class)
public class RoomActivityInfoTest {
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
    public void appointmentShouldBeDisplayed() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), RoomActivityInfo.class);

        intent.putExtra(RoomActivityInfo.APPOINTMENT_KEY, appointmentId);

        try(ActivityScenario ignored = ActivityScenario.launch(intent)) {
            sleep(2, SECONDS);
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
            sleep(2, SECONDS);
            clickOn(R.id.roomInfoTitleEditButton);
            writeTo(R.id.roomInfoDialogEdit, newValue);
            closeSoftKeyboard();
            clickDialogPositiveButton();
            sleep(2, SECONDS);
            assertDisplayed(newValue);
        }

        FirebaseDatabase.getInstance().getReference("appointments").child(appointmentId).child("title").setValue(appointmentTitle);
    }

   @Test
    public void editCourseShouldEditDatabaseValue() {
        String newValue = "Ok, ok it's SWENG";
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), RoomActivityInfo.class);

        intent.putExtra(RoomActivityInfo.APPOINTMENT_KEY, appointmentId);

        try(ActivityScenario ignored = ActivityScenario.launch(intent)) {
            sleep(2, SECONDS);
            clickOn(R.id.roomInfoCourseEditButton);
            writeTo(R.id.roomInfoDialogEdit, newValue);
            closeSoftKeyboard();
            clickOn("OK");
            sleep(2, SECONDS);
            assertDisplayed(newValue);
        }

        FirebaseDatabase.getInstance().getReference("appointments").child(appointmentId).child("course").setValue(appointmentCourse);
    }
}
