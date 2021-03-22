package io.github.polysmee.appointments;

import android.view.View;
import android.widget.SearchView;

import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Calendar;
import java.util.GregorianCalendar;

import io.github.polysmee.R;
import io.github.polysmee.interfaces.Appointment;
import io.github.polysmee.login.DatabaseUser;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import static com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed;
import static com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertNotDisplayed;
import static com.schibsted.spain.barista.interaction.BaristaClickInteractions.clickOn;
import static com.schibsted.spain.barista.interaction.BaristaEditTextInteractions.writeTo;
import static com.schibsted.spain.barista.interaction.BaristaPickerInteractions.setDateOnPicker;
import static com.schibsted.spain.barista.interaction.BaristaPickerInteractions.setTimeOnPicker;
import static org.hamcrest.Matchers.allOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class AppointmentActivityTest {
    String title = "title";
    String course = "course";
    String startTime = "23/03/2021   -   17:02";
    String endTime = "23/03/2021   -   18:02";

    @Rule
    public ActivityScenarioRule<AppointmentActivity> testRule = new ActivityScenarioRule<>(AppointmentActivity.class);

    @Test
    public void btnCreateCreatesCorrectAppointment() {
        clickOn(R.id.appointmentCreationBtnStartTime);
        setDateOnPicker(2021, 3, 23);
        setTimeOnPicker(17, 2);

        clickOn(R.id.appointmentCreationBtnEndTime);
        setDateOnPicker(2021, 3, 23);
        setTimeOnPicker(18, 2);

        writeTo(R.id.appointmentCreationEditTxtAppointmentTitleSet, title);
        closeSoftKeyboard();

        writeTo(R.id.appointmentCreationEditTxtAppointmentCourseSet, course);
        closeSoftKeyboard();

        clickOn(R.id.appointmentCreationbtnCreateAppointment);

        Appointment appointment = (Appointment) testRule.getScenario().getResult().getResultData().getSerializableExtra(AppointmentActivity.EXTRA_APPOINTMENT);
        assertEquals(appointment.getTitle(), title);
        assertEquals(appointment.getCourse(), course);
        assertFalse(appointment.isPrivate());
        assertEquals(1, appointment.getParticipants().size());
        assertEquals(0, appointment.getBans().size());
        Calendar startCalendar = new GregorianCalendar();
        startCalendar.set(2021, 3, 23, 17, 2, 0);
        startCalendar.set(Calendar.MILLISECOND, 0);
        Calendar endCalendar = new GregorianCalendar();
        endCalendar.set(2021, 3, 23, 18, 2, 0);
        endCalendar.set(Calendar.MILLISECOND, 0);
        assertEquals(appointment.getStartTime(), startCalendar.getTimeInMillis());
        assertEquals(appointment.getDuration(), endCalendar.getTimeInMillis() - startCalendar.getTimeInMillis());
    }

    @Test
    public void btnCreateSaysErrorHappenedOnIncorrectStartAndEndTime() {
        clickOn(R.id.appointmentCreationBtnStartTime);
        setDateOnPicker(2021, 3, 23);
        setTimeOnPicker(17, 2);

        clickOn(R.id.appointmentCreationBtnEndTime);
        setDateOnPicker(2021, 3, 23);
        setTimeOnPicker(16, 2);

        writeTo(R.id.appointmentCreationEditTxtAppointmentTitleSet, title);
        closeSoftKeyboard();

        writeTo(R.id.appointmentCreationEditTxtAppointmentCourseSet, course);
        closeSoftKeyboard();

        clickOn(R.id.appointmentCreationbtnCreateAppointment);
        assertDisplayed(R.id.appointmentCreationtxtError, AppointmentActivity.ERROR_TXT);
    }

    @Test
    public void btnStartTimeGetsTime() {
        clickOn(R.id.appointmentCreationBtnStartTime);
        setDateOnPicker(2021, 3, 23);
        setTimeOnPicker(17, 2);
        assertDisplayed(R.id.appointmentCreationTxtStartTime, startTime);
        assertNotDisplayed(R.id.appointmentCreationtxtError);
    }

    @Test
    public void btnEndTimeGetsTime(){
        clickOn(R.id.appointmentCreationBtnEndTime);
        setDateOnPicker(2021, 3, 23);
        setTimeOnPicker(18, 2);
        assertDisplayed(R.id.appointmentCreationTxtEndTime, endTime);
        assertNotDisplayed(R.id.appointmentCreationtxtError);
    }

    @Test
    public void btnResetResets() {
        clickOn(R.id.appointmentCreationBtnStartTime);
        setDateOnPicker(2021, 3, 23);
        setTimeOnPicker(17, 2);

        clickOn(R.id.appointmentCreationBtnEndTime);
        setDateOnPicker(2021, 3, 23);
        setTimeOnPicker(18, 2);

        writeTo(R.id.appointmentCreationEditTxtAppointmentCourseSet, course);
        closeSoftKeyboard();

        writeTo(R.id.appointmentCreationEditTxtAppointmentTitleSet, title);
        closeSoftKeyboard();

        clickOn(R.id.appointementCreationBtnReset);
        assertDisplayed(R.id.appointmentCreationTxtStartTime, "Start Time");
        assertNotDisplayed(R.id.appointmentCreationtxtError);
        assertDisplayed(R.id.appointmentCreationTxtEndTime, "End Time");
        assertDisplayed(R.id.appointmentCreationEditTxtAppointmentCourseSet, "");
        assertDisplayed(R.id.appointmentCreationEditTxtAppointmentTitleSet, "");
    }

    @Test
    public void trashTest(){
        assertEquals(1,1);
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
    public void btnSettingsLaunchesActivityAndActivityReturnsCorrectSettings() {
        clickOn(R.id.appointmentCreationBtnSettings);
        onView(withId(R.id.appointmentSettingsSearchInvite)).perform(typeSearchViewText("Youssef"));
        closeSoftKeyboard();
        onView(withId(R.id.appointmentSettingsSearchBan)).perform(typeSearchViewText("Voldemort"));
        closeSoftKeyboard();
        clickOn(R.id.appointmentSettingsBtnInvite);
        clickOn(R.id.appointmentSettingsBtnBan);
        clickOn(R.id.appointmentSettingsSwitchPrivate);
        clickOn(R.id.appointmentSettingsBtnDone);

        clickOn(R.id.appointmentCreationBtnStartTime);
        setDateOnPicker(2021, 3, 23);
        setTimeOnPicker(17, 2);

        clickOn(R.id.appointmentCreationBtnEndTime);
        setDateOnPicker(2021, 3, 23);
        setTimeOnPicker(18, 2);

        clickOn(R.id.appointmentCreationbtnCreateAppointment);
        Appointment appointment = (Appointment) testRule.getScenario().getResult().getResultData().getSerializableExtra(AppointmentActivity.EXTRA_APPOINTMENT);
        assertTrue(appointment.isPrivate());
        assertEquals(2, appointment.getParticipants().size());
        assertEquals(1, appointment.getBans().size());
        assertTrue(appointment.getParticipants().contains(new DatabaseUser("Youssef")));
        assertTrue(appointment.getBans().contains(new DatabaseUser("Voldemort")));
    }
}
