package io.github.polysmee.appointments;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.FirebaseApp;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Calendar;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import io.github.polysmee.R;
import io.github.polysmee.database.DatabaseFactory;
import io.github.polysmee.login.AuthenticationFactory;
import io.github.polysmee.login.MainUserSingleton;
import io.github.polysmee.znotification.AppointmentReminderNotificationSetupListener;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static com.schibsted.spain.barista.assertion.BaristaClickableAssertions.assertClickable;
import static com.schibsted.spain.barista.assertion.BaristaEnabledAssertions.assertEnabled;
import static com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed;
import static com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertNotDisplayed;
import static com.schibsted.spain.barista.interaction.BaristaClickInteractions.clickOn;
import static com.schibsted.spain.barista.interaction.BaristaEditTextInteractions.writeTo;
import static com.schibsted.spain.barista.interaction.BaristaPickerInteractions.setDateOnPicker;
import static com.schibsted.spain.barista.interaction.BaristaPickerInteractions.setTimeOnPicker;
import static com.schibsted.spain.barista.interaction.BaristaScrollInteractions.scrollTo;
import static com.schibsted.spain.barista.interaction.BaristaSleepInteractions.sleep;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(JUnit4.class)
public class AppointmentActivityAddModeTest {
    private static final String username1 = "Mathis L'utilisateur";
    private static String id2 = "bxcwviusergpoza";
    private static final String username2 = "Sami L'imposteur";
    private static String id3 = "sdflkhsfdlkhsfd";
    private static final String username3 = "Léo La fouine";

    private static final String course = "AquaPoney";
    private static final String title = "Aglouglou sur mon cheval";

    @BeforeClass
    public static void setUp() throws Exception {
        AppointmentReminderNotificationSetupListener.setIsNotificationSetterEnable(false);
        DatabaseFactory.setTest();
        AuthenticationFactory.setTest();
        FirebaseApp.clearInstancesForTest();
        FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext());
        Tasks.await(AuthenticationFactory.getAdaptedInstance().createUserWithEmailAndPassword("AppointmentActivityAddModeTest@gmail.com", "fakePassword"));
        DatabaseFactory.getAdaptedInstance().getReference("users").child(MainUserSingleton.getInstance().getId()).child("name").setValue(username1);
        DatabaseFactory.getAdaptedInstance().getReference("users").child(id2).child("name").setValue(username2);
        DatabaseFactory.getAdaptedInstance().getReference("users").child(id3).child("name").setValue(username3);
    }


    @Test
    public void allTestsAtOnce() throws InterruptedException {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), AppointmentActivity.class);
        intent.putExtra(AppointmentActivity.LAUNCH_MODE, AppointmentActivity.ADD_MODE);

        try (ActivityScenario<AppointmentActivity> ignored = ActivityScenario.launch(intent)) {
            assertNotDisplayed(R.id.appointmentCreationTimeError);
            assertNotDisplayed(R.id.appointmentCreationAddBanError);

            assertEnabled(R.id.appointmentCreationEditTxtAppointmentTitleSet);
            assertClickable(R.id.appointmentCreationStartTimeLayout);
            assertClickable(R.id.appointmentCreationEndTimeLayout);
            assertEnabled(R.id.appointmentCreationEditTxtAppointmentCourseSet);
            assertClickable(R.id.appointmentCreationPrivateSelector);

            Thread.sleep(1000);

            assertNotDisplayed(R.id.appointmentCreationAddUserFragment);
            scrollTo(R.id.appointmentCreationTxtWarning);
            clickOn(R.id.appointmentCreationShowAdd);
            scrollTo(R.id.appointmentCreationTxtWarning);

            assertDisplayed(R.id.appointmentCreationAddUserFragment);
            clickOn(R.id.appointmentCreationShowAdd);
            assertNotDisplayed(R.id.appointmentCreationAddUserFragment);

            scrollTo(R.id.appointmentCreationTxtWarning);
            clickOn(R.id.appointmentCreationAddTextView);
            scrollTo(R.id.appointmentCreationTxtWarning);
            assertDisplayed(R.id.appointmentCreationAddUserFragment);
            clickOn(R.id.appointmentCreationAddTextView);
            assertNotDisplayed(R.id.appointmentCreationAddUserFragment);

            Thread.sleep(1000);

            assertNotDisplayed(R.id.appointmentCreationBanUserFragment);
            scrollTo(R.id.appointmentCreationTxtWarning);
            clickOn(R.id.appointmentCreationShowBan);
            scrollTo(R.id.appointmentCreationTxtWarning);
            assertDisplayed(R.id.appointmentCreationBanUserFragment);
            scrollTo(R.id.appointmentCreationShowBan);
            clickOn(R.id.appointmentCreationShowBan);
            assertNotDisplayed(R.id.appointmentCreationBanUserFragment);

            scrollTo(R.id.appointmentCreationTxtWarning);
            clickOn(R.id.appointmentCreationBanTextView);
            scrollTo(R.id.appointmentCreationTxtWarning);
            assertDisplayed(R.id.appointmentCreationBanUserFragment);
            scrollTo(R.id.appointmentCreationBanTextView);
            clickOn(R.id.appointmentCreationBanTextView);
            assertNotDisplayed(R.id.appointmentCreationBanUserFragment);

            Thread.sleep(1000);

            scrollTo(R.id.appointmentCreationStartTimeLayout);
            clickOn(R.id.appointmentCreationStartTimeLayout);
            setDateOnPicker(2022, 3, 23);
            setTimeOnPicker(17, 2);

            scrollTo(R.id.appointmentCreationEndTimeLayout);
            clickOn(R.id.appointmentCreationEndTimeLayout);
            setDateOnPicker(2022, 3, 23);
            setTimeOnPicker(16, 2);

            clickOn(R.id.appointmentCreationbtnDone);
            scrollTo(R.id.appointmentCreationTxtWarning);
            assertDisplayed(R.string.appointmentCreationTimeError);
            clickOn(R.string.appointment_creation_reset_btn_txt);

            Thread.sleep(1000);

            scrollTo(R.id.appointmentCreationStartTimeLayout);
            clickOn(R.id.appointmentCreationStartTimeLayout);
            setDateOnPicker(1990, 3, 23);
            setTimeOnPicker(17, 2);

            scrollTo(R.id.appointmentCreationEndTimeLayout);
            clickOn(R.id.appointmentCreationEndTimeLayout);
            setDateOnPicker(1990, 3, 23);
            setTimeOnPicker(18, 2);

            clickOn(R.id.appointmentCreationbtnDone);
            scrollTo(R.id.appointmentCreationTxtWarning);
            assertDisplayed(R.string.appointmentCreationTimeError);
            clickOn(R.string.appointment_creation_reset_btn_txt);

            Thread.sleep(1000);

            scrollTo(R.id.appointmentCreationTxtWarning);
            clickOn(R.id.appointmentCreationShowAdd);
            scrollTo(R.id.appointmentCreationTxtWarning);
            writeTo(R.id.appointmentSettingsSearchAdd, username2);
            closeSoftKeyboard();
            clickOn(R.id.appointmentSettingsBtnAdd);
            scrollTo(R.id.appointmentCreationTxtWarning);
            assertDisplayed(R.id.appointmentSettingsSearchAdd, "");
            assertDisplayed(username2);
            clickOn(R.string.appointment_creation_reset_btn_txt);

            Thread.sleep(1000);

            scrollTo(R.id.appointmentCreationShowBan);
            clickOn(R.id.appointmentCreationShowBan);
            writeTo(R.id.appointmentSettingsSearchBan, username2);
            closeSoftKeyboard();
            scrollTo(R.id.appointmentSettingsBtnBan);
            clickOn(R.id.appointmentSettingsBtnBan);
            assertDisplayed(R.id.appointmentSettingsSearchBan, "");
            assertDisplayed(username2);
            clickOn(R.string.appointment_creation_reset_btn_txt);

            Thread.sleep(1000);

            scrollTo(R.id.appointmentCreationShowBan);
            clickOn(R.id.appointmentCreationShowBan);
            writeTo(R.id.appointmentSettingsSearchBan, "ewfiuhwefasfd");
            closeSoftKeyboard();
            scrollTo(R.id.appointmentSettingsBtnBan);
            clickOn(R.id.appointmentSettingsBtnBan);
            assertDisplayed("Error");
            clickOn("OK");

            writeTo(R.id.appointmentSettingsSearchBan, username2);
            closeSoftKeyboard();
            scrollTo(R.id.appointmentSettingsBtnBan);
            clickOn(R.id.appointmentSettingsBtnBan);

            scrollTo(R.id.appointmentCreationShowAdd);
            clickOn(R.id.appointmentCreationShowAdd);
            writeTo(R.id.appointmentSettingsSearchAdd, "awewefkhgasd");
            closeSoftKeyboard();
            scrollTo(R.id.appointmentSettingsBtnAdd);
            clickOn(R.id.appointmentSettingsBtnAdd);
            assertDisplayed("Error");
            clickOn("OK");

            writeTo(R.id.appointmentSettingsSearchAdd, username2);
            closeSoftKeyboard();
            scrollTo(R.id.appointmentSettingsBtnAdd);
            clickOn(R.id.appointmentSettingsBtnAdd);

            clickOn(R.id.appointmentCreationbtnDone);
            scrollTo(R.id.appointmentCreationTxtWarning);
            assertDisplayed(R.string.appointmentCreationAddBanError);
            clickOn(R.string.appointment_creation_reset_btn_txt);

            Thread.sleep(1000);
        }
    }

    @Test
    public void doneButtonCreateTheCorrectAppointmentInTheDatabase() throws InterruptedException, ExecutionException {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), AppointmentActivity.class);
        intent.putExtra(AppointmentActivity.LAUNCH_MODE, AppointmentActivity.ADD_MODE);

        ActivityScenario<AppointmentActivity> scenario = ActivityScenario.launch(intent);

        //COURSE
        scrollTo(R.id.appointmentCreationEditTxtAppointmentCourseSet);
        writeTo(R.id.appointmentCreationEditTxtAppointmentCourseSet, course);
        closeSoftKeyboard();

        //TITLE
        scrollTo(R.id.appointmentCreationEditTxtAppointmentTitleSet);
        writeTo(R.id.appointmentCreationEditTxtAppointmentTitleSet, title);
        closeSoftKeyboard();

        //START TIME
        scrollTo(R.id.appointmentCreationStartTimeLayout);
        clickOn(R.id.appointmentCreationStartTimeLayout);
        setDateOnPicker(2022, 3, 23);
        setTimeOnPicker(17, 2);

        //END TIME
        scrollTo(R.id.appointmentCreationEndTimeLayout);
        clickOn(R.id.appointmentCreationEndTimeLayout);
        setDateOnPicker(2022, 3, 23);
        setTimeOnPicker(18, 2);

        //PRIVATE = true
        scrollTo(R.id.appointmentCreationShowBan);
        clickOn(R.id.appointmentCreationPrivateSelector);

        //ADD user 2
        scrollTo(R.id.appointmentCreationTxtWarning);
        clickOn(R.id.appointmentCreationShowAdd);
        scrollTo(R.id.appointmentCreationTxtWarning);
        writeTo(R.id.appointmentSettingsSearchAdd, username2);
        closeSoftKeyboard();
        clickOn(R.id.appointmentSettingsBtnAdd);

        //BAN user 3
        scrollTo(R.id.appointmentCreationTxtWarning);
        clickOn(R.id.appointmentCreationShowBan);
        scrollTo(R.id.appointmentCreationTxtWarning);
        writeTo(R.id.appointmentSettingsSearchBan, username3);
        closeSoftKeyboard();
        clickOn(R.id.appointmentSettingsBtnBan);

        clickOn(R.id.appointmentCreationbtnDone);

        Thread.sleep(2000);

        HashMap aptId = (HashMap) Tasks.await(DatabaseFactory.getAdaptedInstance().getReference("users").child(MainUserSingleton.getInstance().getId()).child("appointments").get()).getValue();
        assertNotNull(aptId);
        assertEquals(1, aptId.keySet().size());


        for (Object id : aptId.keySet()) {
            HashMap apt = (HashMap) Tasks.await(DatabaseFactory.getAdaptedInstance().getReference("appointments").child((String) id).get()).getValue();

            assertEquals(title, apt.get("title"));
            assertTrue(((HashMap) apt.get("banned")).containsKey(id3));
            assertTrue(((HashMap) apt.get("participants")).containsKey(id2));
            assertEquals(course, apt.get("course"));
            //assertTrue(((boolean) apt.get("private")));
            assertEquals(3600000, ((long) apt.get("duration")), 1000);
            Calendar expectedDate = Calendar.getInstance();
            expectedDate.set(2022, 2, 23, 17, 2, 0);
            assertEquals(expectedDate.getTimeInMillis(), ((long) apt.get("start")), 1000);
        }

        scenario.close();

    }
}
