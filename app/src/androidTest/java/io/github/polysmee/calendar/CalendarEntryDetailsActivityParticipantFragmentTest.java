package io.github.polysmee.calendar;

import android.os.Bundle;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.FirebaseApp;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import java.util.concurrent.ExecutionException;

import io.github.polysmee.calendar.detailsFragments.CalendarEntryDetailsParticipantsFragments;
import io.github.polysmee.database.DatabaseFactory;
import io.github.polysmee.login.AuthenticationFactory;
import io.github.polysmee.login.MainUserSingleton;

import static com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed;
import static com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertNotDisplayed;
import static com.schibsted.spain.barista.interaction.BaristaSleepInteractions.sleep;
import static java.util.concurrent.TimeUnit.SECONDS;

@RunWith(AndroidJUnit4.class)
public class CalendarEntryDetailsActivityParticipantFragmentTest {
    private static final String username1 = "Youssef le magnifique";

    private static final String id2 = "-SFDkjfddl";
    private static final String username2 = "Thomas la magouille";

    private static final String appointmentTitle = "Some titke";
    private static final String appointmentId = "-lsdqfkhfduisjhmf";
    private static final String appointmentCourse = "SDP";
    private static final long appointmentStart = 265655445;

    @BeforeClass
    public static void setUp() throws Exception {

        DatabaseFactory.setTest();
        AuthenticationFactory.setTest();
        FirebaseApp.clearInstancesForTest();
        FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext());
        Tasks.await(AuthenticationFactory.getAdaptedInstance().createUserWithEmailAndPassword("polysmee134@gmail.com", "fakePassword"));
        DatabaseFactory.getAdaptedInstance().getReference("users").child(MainUserSingleton.getInstance().getId()).child("name").setValue(username1);
        DatabaseFactory.getAdaptedInstance().getReference("users").child(id2).child("name").setValue(username2);

        DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId).child("title").setValue(appointmentTitle);
        DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId).child("course").setValue(appointmentCourse);
        DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId).child("start").setValue(appointmentStart);
        DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId).child("owner").child(MainUserSingleton.getInstance().getId()).setValue(true);
        DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId).child("participants").child(MainUserSingleton.getInstance().getId()).setValue(true);
        DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId).child("participants").child(id2).setValue(true);
    }

    @AfterClass
    public static void delete() throws ExecutionException, InterruptedException {
        Tasks.await(AuthenticationFactory.getAdaptedInstance().signInWithEmailAndPassword("polysmee134@gmail.com", "fakePassword"));
        DatabaseFactory.getAdaptedInstance().getReference("users").child(MainUserSingleton.getInstance().getId()).setValue(null);
        DatabaseFactory.getAdaptedInstance().getReference("users").child(id2).setValue(null);
        DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId).setValue(null);
        Tasks.await(AuthenticationFactory.getAdaptedInstance().getCurrentUser().delete());
    }

  @Test
  public void participantsNamesInCalendarEntryDetailsAreDisplayed(){
      Bundle bundle = new Bundle();

      bundle.putSerializable(CalendarActivity.UserTypeCode,"Real");
      bundle.putSerializable(CalendarEntryDetailsParticipantsFragments.APPOINTMENT_DETAIL_PARTICIPANT_ID,appointmentId);

      FragmentScenario.launchInContainer(CalendarEntryDetailsParticipantsFragments.class, bundle);
      sleep(5, SECONDS);
      assertDisplayed(username1);
      assertDisplayed(username2);

  }
  @Test
  public void kickingAUserInParticipantScreenInEntryDetailsWorks(){
      Bundle bundle = new Bundle();

      bundle.putSerializable(CalendarActivity.UserTypeCode,"Real");
      bundle.putSerializable(CalendarEntryDetailsParticipantsFragments.APPOINTMENT_DETAIL_PARTICIPANT_ID,appointmentId);

      FragmentScenario.launchInContainer(CalendarEntryDetailsParticipantsFragments.class, bundle);
      sleep(3,SECONDS);
      Espresso.onView(withText("Kick")).perform(ViewActions.click());
      sleep(3,SECONDS);
      assertDisplayed(username1);
      assertNotDisplayed(username2);
  }
}
