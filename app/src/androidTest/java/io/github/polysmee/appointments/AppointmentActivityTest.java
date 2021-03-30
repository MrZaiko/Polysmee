package io.github.polysmee.appointments;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import org.hamcrest.Matcher;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import io.github.polysmee.R;
import io.github.polysmee.appointments.fragments.MainAppointmentCreationFragment;
import io.github.polysmee.database.DatabaseAppointment;
import io.github.polysmee.database.DatabaseFactory;
import io.github.polysmee.database.DatabaseUser;
import io.github.polysmee.database.databaselisteners.LongValueListener;
import io.github.polysmee.database.databaselisteners.StringSetValueListener;
import io.github.polysmee.database.databaselisteners.StringValueListener;
import io.github.polysmee.interfaces.Appointment;
import io.github.polysmee.interfaces.User;
import io.github.polysmee.login.AuthenticationFactory;
import io.github.polysmee.login.MainUserSingleton;
import io.github.polysmee.room.RoomActivity;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.contrib.ActivityResultMatchers.hasResultCode;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import static com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed;
import static com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertNotDisplayed;
import static com.schibsted.spain.barista.interaction.BaristaClickInteractions.clickOn;
import static com.schibsted.spain.barista.interaction.BaristaEditTextInteractions.writeTo;
import static com.schibsted.spain.barista.interaction.BaristaPickerInteractions.setDateOnPicker;
import static com.schibsted.spain.barista.interaction.BaristaPickerInteractions.setTimeOnPicker;
import static com.schibsted.spain.barista.interaction.BaristaSleepInteractions.sleep;
import static org.hamcrest.Matchers.allOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class AppointmentActivityTest {
    String title = "title";
    String course = "course";
    String startTime = "23/03/2022   -   17:02";
    String endTime = "23/03/2022   -   18:02";

    private static final String username1 = "Mathis aptCreation";
    private static final String id2 = "-SFDkjsfewfwerferagdfgfyfrddl";
    private static final String id3 = "-SFDkjsfdkwefwef";
    private static final String username2 = "Sami aptCreation";
    private static final String username3 = "Leo aptCreation";
    private static final String id4 = "-SFDkjsfdltzuluizlghjkglgiluilglglgkjlqwd";
    private static final String id5 = "-SFDkjsfdkwefwefasdaew";
    private static final String username4 = "Thomas aptCreation";
    private static final String username5 = "Adrien aptCreation";


    @BeforeClass
    public static void setUp() throws Exception {
        DatabaseFactory.setTest();
        AuthenticationFactory.setTest();
        FirebaseApp.clearInstancesForTest();
        FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext());
        Tasks.await(AuthenticationFactory.getAdaptedInstance().createUserWithEmailAndPassword("AppointmentActivityTest@gmail.com", "fakePassword"));
        DatabaseFactory.getAdaptedInstance().getReference("users").child(MainUserSingleton.getInstance().getId()).child("name").setValue(username1);
        DatabaseFactory.getAdaptedInstance().getReference("users").child(id2).child("name").setValue(username2);
        DatabaseFactory.getAdaptedInstance().getReference("users").child(id3).child("name").setValue(username3);
        DatabaseFactory.getAdaptedInstance().getReference("users").child(id4).child("name").setValue(username4);
        DatabaseFactory.getAdaptedInstance().getReference("users").child(id5).child("name").setValue(username5);
    }

    @Rule
    public ActivityScenarioRule<AppointmentActivity> testRule = new ActivityScenarioRule<>(AppointmentActivity.class);

    @Test
    public void btnCreateSaysErrorHappenedOnIncorrectStartAndEndTimeAndBtnResetResets() {
        clickOn(R.id.appointmentCreationBtnStartTime);
        setDateOnPicker(2022, 3, 23);
        setTimeOnPicker(17, 2);

        clickOn(R.id.appointmentCreationBtnEndTime);
        setDateOnPicker(2022, 3, 23);
        setTimeOnPicker(16, 2);

        writeTo(R.id.appointmentCreationEditTxtAppointmentTitleSet, title);
        closeSoftKeyboard();

        writeTo(R.id.appointmentCreationEditTxtAppointmentCourseSet, course);
        closeSoftKeyboard();

        clickOn(R.id.appointmentCreationbtnDone);
        assertDisplayed(R.id.appointmentCreationtxtError, MainAppointmentCreationFragment.ERROR_TXT);

        clickOn(R.id.appointementCreationBtnReset);
        assertDisplayed(R.id.appointmentCreationTxtStartTime, "Start Time");
        assertNotDisplayed(R.id.appointmentCreationtxtError);
        assertDisplayed(R.id.appointmentCreationTxtEndTime, "End Time");
        assertDisplayed(R.id.appointmentCreationEditTxtAppointmentCourseSet, "");
        assertDisplayed(R.id.appointmentCreationEditTxtAppointmentTitleSet, "");
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

    @Test
    public void btnSettingsLaunchesActivityAndActivityReturnsCorrectSettings() throws Exception{
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), AppointmentActivity.class);
        ActivityScenario<AppointmentActivity> scenario = ActivityScenario.launch(intent);
        clickOn("SETTINGS");
        onView(withId(R.id.appointmentSettingsSearchInvite)).perform(typeSearchViewText(username3));
        closeSoftKeyboard();
        onView(withId(R.id.appointmentSettingsSearchBan)).perform(typeSearchViewText(username2));
        closeSoftKeyboard();
        clickOn(R.id.appointmentSettingsBtnInvite);
        clickOn(R.id.appointmentSettingsBtnBan);
        onView(withId(R.id.appointmentSettingsSearchInvite)).perform(typeSearchViewText(username4));
        closeSoftKeyboard();
        onView(withId(R.id.appointmentSettingsSearchBan)).perform(typeSearchViewText(username5));
        closeSoftKeyboard();
        clickOn(R.id.appointmentSettingsBtnInvite);
        clickOn(R.id.appointmentSettingsBtnBan);

        clickOn(R.id.appointmentSettingsBtnSeeInvites);
        clickOn(username4);
        clickOn("OK");

        clickOn(R.id.appointmentSettingsBtnSeeBans);
        clickOn(username5);
        clickOn("OK");

        clickOn(R.id.appointmentSettingsSwitchPrivate);
        clickOn("MAIN");

        writeTo(R.id.appointmentCreationEditTxtAppointmentTitleSet, title);
        closeSoftKeyboard();

        writeTo(R.id.appointmentCreationEditTxtAppointmentCourseSet, course);
        closeSoftKeyboard();

        clickOn(R.id.appointmentCreationBtnStartTime);
        setDateOnPicker(2022, 3, 23);
        setTimeOnPicker(17, 2);

        clickOn(R.id.appointmentCreationBtnEndTime);
        setDateOnPicker(2022, 3, 23);
        setTimeOnPicker(18, 2);

        clickOn(R.id.appointmentCreationbtnDone);

        Thread.sleep(10000);
        HashMap aptId = (HashMap) Tasks.await(DatabaseFactory.getAdaptedInstance().getReference("users").child(MainUserSingleton.getInstance().getId()).child("appointments").get()).getValue();
        assertNotNull(aptId);
        DatabaseAppointment appointment = new DatabaseAppointment((String) aptId.keySet().iterator().next());
        appointment.getTitleAndThen(o -> assertEquals(title, o));

        scenario.close();
    }
}
