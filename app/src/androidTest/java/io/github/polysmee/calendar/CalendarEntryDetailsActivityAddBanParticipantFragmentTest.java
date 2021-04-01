package io.github.polysmee.calendar;


import android.os.Bundle;
import android.view.View;
import android.widget.SearchView;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.FirebaseApp;

import org.hamcrest.Matcher;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Set;
import java.util.concurrent.ExecutionException;

import io.github.polysmee.R;
import io.github.polysmee.calendar.detailsFragments.CalendarEntryDetailAddBanParticipantsFragment;
import io.github.polysmee.database.DatabaseFactory;
import io.github.polysmee.database.databaselisteners.StringSetValueListener;
import io.github.polysmee.login.AuthenticationFactory;
import io.github.polysmee.login.MainUserSingleton;

import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static com.schibsted.spain.barista.interaction.BaristaSleepInteractions.sleep;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.hamcrest.Matchers.allOf;
import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class CalendarEntryDetailsActivityAddBanParticipantFragmentTest {


    private static final String username1 = "Youssef le magnifique";

    private static final String id2 = "-BUDDkjfddl";
    private static final String username2 = "Thomas le beau";

    private static final String appointmentTitle = "Some titke";
    private static final String appointmentId = "-lsdqrhrhreisjhmf";
    private static final String appointmentCourse = "SDP";
    private static final long appointmentStart = 265655445;
    @BeforeClass
    public static void setUp() throws Exception {

        DatabaseFactory.setTest();
        AuthenticationFactory.setTest();
        FirebaseApp.clearInstancesForTest();
        FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext());
        Tasks.await(AuthenticationFactory.getAdaptedInstance().createUserWithEmailAndPassword("polysmee2410@gmail.com", "fakePassword"));
        DatabaseFactory.getAdaptedInstance().getReference("users").child(MainUserSingleton.getInstance().getId()).child("name").setValue(username1);
        DatabaseFactory.getAdaptedInstance().getReference("users").child(id2).child("name").setValue(username2);

        DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId).child("title").setValue(appointmentTitle);
        DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId).child("course").setValue(appointmentCourse);
        DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId).child("start").setValue(appointmentStart);

        DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId).child("owner").setValue(MainUserSingleton.getInstance().getId());

        DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId).child("participants").child(MainUserSingleton.getInstance().getId()).setValue(true);
        DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId).child("participants").child(id2).setValue(true);
    }

   /* @AfterClass
    public static void delete() throws ExecutionException, InterruptedException {
        Tasks.await(AuthenticationFactory.getAdaptedInstance().signInWithEmailAndPassword("polysmee2410@gmail.com", "fakePassword"));
        DatabaseFactory.getAdaptedInstance().getReference("users").child(MainUserSingleton.getInstance().getId()).setValue(null);
        DatabaseFactory.getAdaptedInstance().getReference("users").child(id2).setValue(null);
        DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId).setValue(null);
        //Tasks.await(AuthenticationFactory.getAdaptedInstance().getCurrentUser().delete());
    }*/

    @Test
    public void addingUserThroughSearchAddsTheUser(){
        FragmentScenario.launchInContainer(CalendarEntryDetailAddBanParticipantsFragment.class);
        sleep(5, SECONDS);
        Espresso.onView(withId(R.id.calendarEntryDetailActivityInviteSearch)).perform(typeSearchViewText(username2));
        Espresso.onView(withId(R.id.calendarEntryDetailActivityInviteButton)).perform(ViewActions.click());
        sleep(3,SECONDS);
        DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId).child("participants").addListenerForSingleValueEvent(new StringSetValueListener() {
            @Override
            public void onDone(Set<String> o) {
                assertEquals(true,o.contains(id2));
                assertEquals(true,o.contains(MainUserSingleton.getInstance().getId()));
            }
        });
    }

    public static ViewAction typeSearchViewText(final String text){
        return new ViewAction(){
            @Override
            public Matcher<View> getConstraints() {
                //Ensure that only apply if it is a SearchView and if it is visible.
                return allOf(isDisplayed(), isAssignableFrom(SearchView.class));
            }

            @Override
            public String getDescription() {
                return "Change view text";
            }

            @Override
            public void perform(UiController uiController, View view) {
                ((SearchView) view).setQuery(text,false);
            }
        };
    }
}