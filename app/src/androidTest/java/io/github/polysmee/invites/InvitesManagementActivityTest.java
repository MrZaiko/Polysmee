package io.github.polysmee.invites;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.FirebaseApp;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Calendar;

import io.github.polysmee.R;
import io.github.polysmee.appointments.AppointmentActivity;
import io.github.polysmee.calendar.googlecalendarsync.CalendarUtilities;
import io.github.polysmee.database.DatabaseFactory;
import io.github.polysmee.login.AuthenticationFactory;
import io.github.polysmee.login.MainUser;
import io.github.polysmee.znotification.AppointmentReminderNotification;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed;
import static com.schibsted.spain.barista.interaction.BaristaClickInteractions.clickOn;
import static com.schibsted.spain.barista.interaction.BaristaSleepInteractions.sleep;
import static com.schibsted.spain.barista.interaction.BaristaViewPagerInteractions.swipeViewPagerForward;
import static java.util.concurrent.TimeUnit.SECONDS;

@RunWith(JUnit4.class)
public class InvitesManagementActivityTest {
    private static final String username1 = "Mathis L'utilisateur";
    private static final String calendarId = "invitesmanagementactivitytest@gmail.com";
    private static final String id2 = "pzerotujrtpoiu";
    private static final String username2 = "Sami L'imposteur";
    private static final String id3 = "&éhmhsiogsfdsdgf";
    private static final String username3 = "Léo La fouine";

    private static final String appointmentId = "opkdsfmcvx";
    private static final String appointmentId2 = "frthaopsdjoigrer";
    private static final String appointmentId3 = "frthaopsdjoigrwfxssdver";
    private static final String course = "AquaPoney";
    private static final String title = "Aglouglou sur mon cheval";
    private static final String course2 = "AquaPoney2";
    private static final String title2 = "Aglouglou sur mon cheval2";
    private static final String course3 = "AquaPoney3";
    private static final String title3 = "Aglouglou sur mon cheval3";
    private static final long duration = 3600000;

    @BeforeClass
    public static void setUp() throws Exception {
        Calendar startTime = Calendar.getInstance();
        startTime.set(2022, 4, 22, 18, 3, 0);
        Calendar endTime = (Calendar) startTime.clone();
        endTime.add(Calendar.MILLISECOND, (int) duration);
        AppointmentReminderNotification.setIsNotificationSetterEnable(false);
        DatabaseFactory.setTest();
        AuthenticationFactory.setTest();
        FirebaseApp.clearInstancesForTest();
        CalendarUtilities.setTest(true, false);
        FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext());
        Tasks.await(AuthenticationFactory.getAdaptedInstance().createUserWithEmailAndPassword("InvitesManagementActivityTest@gmail.com", "fakePassword"));
        DatabaseFactory.getAdaptedInstance().getReference("users").child(MainUser.getMainUser().getId()).child("name").setValue(username1);
        DatabaseFactory.getAdaptedInstance().getReference("users").child(MainUser.getMainUser().getId()).child("calendarId").setValue(calendarId);
        DatabaseFactory.getAdaptedInstance().getReference("users").child(id2).child("name").setValue(username2);
        DatabaseFactory.getAdaptedInstance().getReference("users").child(id3).child("name").setValue(username3);
        DatabaseFactory.getAdaptedInstance().getReference("users").child(MainUser.getMainUser().getId()).child("invites").child(appointmentId).setValue(true);
        DatabaseFactory.getAdaptedInstance().getReference("courses").child(course).setValue(course);
        DatabaseFactory.getAdaptedInstance().getReference("courses").child(course2).setValue(course2);
        DatabaseFactory.getAdaptedInstance().getReference("courses").child(course3).setValue(course3);

        DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId).child("title").setValue(title);
        DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId).child("course").setValue(course);
        DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId).child("start").setValue(startTime.getTimeInMillis());
        DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId).child("duration").setValue(duration);
        DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId).child("invites").child(MainUser.getMainUser().getId()).setValue(true);
        DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId).child("participants").child(id2).setValue(true);
        DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId).child("owner").setValue(id2);
        DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId).child("private").setValue(false);

        DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId2).child("title").setValue(title2);
        DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId2).child("course").setValue(course2);
        DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId2).child("start").setValue(startTime.getTimeInMillis());
        DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId2).child("duration").setValue(duration);
        DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId2).child("invites").child(MainUser.getMainUser().getId()).setValue(true);
        DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId2).child("participants").child(id2).setValue(true);
        DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId2).child("owner").setValue(id2);
        DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId2).child("private").setValue(false);

        DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId3).child("title").setValue(title3);
        DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId3).child("course").setValue(course3);
        DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId3).child("start").setValue(startTime.getTimeInMillis());
        DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId3).child("duration").setValue(duration);
        DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId3).child("invites").child(MainUser.getMainUser().getId()).setValue(true);
        DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId3).child("participants").child(id2).setValue(true);
        DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId3).child("owner").setValue(id2);
        DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId3).child("private").setValue(false);
    }

    @AfterClass
    public static void clean() {
        DatabaseFactory.getAdaptedInstance().getReference().setValue(null);
    }

    @Test
    public void InvitesShouldProperlyDisplay() throws InterruptedException {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), InvitesManagementActivity.class);

        ActivityScenario<AppointmentActivity> scenario = ActivityScenario.launch(intent);

        sleep(1, SECONDS);
        assertDisplayed("Current invitations");
        assertDisplayed(title);
        assertDisplayed("Course : " + course);
        onView(withText(title2)).check(doesNotExist());
        onView(withText(title3)).check(doesNotExist());
        clickOn(R.id.invitationEntryImgAccept);
        sleep(1, SECONDS);
        onView(withText(title)).check(doesNotExist());
        DatabaseFactory.getAdaptedInstance().getReference("users").child(MainUser.getMainUser().getId()).child("invites").child(appointmentId2).setValue(true);
        sleep(1, SECONDS);
        assertDisplayed(title2);
        assertDisplayed("Course : " + course2);
        clickOn(R.id.invitationEntryImgRefuse);
        DatabaseFactory.getAdaptedInstance().getReference("users").child(MainUser.getMainUser().getId()).child("invites").child(appointmentId3).setValue(true);
        sleep(1, SECONDS);
        assertDisplayed(title3);
        assertDisplayed("Course : " + course3);
        onView(withText(title2)).check(doesNotExist());

        swipeViewPagerForward();
        DatabaseFactory.getAdaptedInstance().getReference("users").child(MainUser.getMainUser().getId()).child("friendsInvites").child(id2).setValue(true);
        sleep(2,SECONDS);
        clickOn(R.id.friendEntryAcceptFriendButton);
        DatabaseFactory.getAdaptedInstance().getReference("users").child(MainUser.getMainUser().getId()).child("friendsInvites").child(id3).setValue(true);
        sleep(1,SECONDS);
        clickOn(R.id.friendEntryRemoveFriendButton);

    }
}
