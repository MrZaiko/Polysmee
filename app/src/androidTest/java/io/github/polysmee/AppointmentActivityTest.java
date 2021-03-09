package io.github.polysmee;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Calendar;
import java.util.GregorianCalendar;

import io.github.polysmee.appointments.AppointmentActivity;
import io.github.polysmee.appointments.TestUser;
import io.github.polysmee.interfaces.Appointment;

import static com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed;
import static com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertNotDisplayed;
import static com.schibsted.spain.barista.interaction.BaristaClickInteractions.clickOn;
import static com.schibsted.spain.barista.interaction.BaristaEditTextInteractions.writeTo;
import static com.schibsted.spain.barista.interaction.BaristaPickerInteractions.setDateOnPicker;
import static com.schibsted.spain.barista.interaction.BaristaPickerInteractions.setTimeOnPicker;
import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class AppointmentActivityTest {
    private static final TestUser TEST_USER = new TestUser("username");
    String title = "title";
    String course = "course";
    String startTime = "23/03/2021   -   17:02";
    String endTime = "23/03/2021   -   18:02";

    @Rule
    public ActivityScenarioRule<AppointmentActivity> testRule = new ActivityScenarioRule<>(AppointmentActivity.class);

    @Test
    public void btnCreateCreatesCorrectAppointment() {
        clickOn(R.id.btnStartTime);
        setDateOnPicker(2021, 3, 23);
        setTimeOnPicker(17, 2);

        clickOn(R.id.btnEndTime);
        setDateOnPicker(2021, 3, 23);
        setTimeOnPicker(18, 2);

        writeTo(R.id.editTxtAppointmentTitleSet, title);

        writeTo(R.id.editTxtAppointmentCourseSet, course);

        clickOn(R.id.btnCreateAppointment);

        Appointment appointment = (Appointment) testRule.getScenario().getResult().getResultData().getSerializableExtra(AppointmentActivity.EXTRA_APPOINTMENT);
        assertEquals(appointment.getTitle(), title);
        assertEquals(appointment.getCourse(), course);
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
        clickOn(R.id.btnStartTime);
        setDateOnPicker(2021, 3, 23);
        setTimeOnPicker(17, 2);

        clickOn(R.id.btnEndTime);
        setDateOnPicker(2021, 3, 23);
        setTimeOnPicker(16, 2);

        writeTo(R.id.editTxtAppointmentTitleSet, title);

        writeTo(R.id.editTxtAppointmentCourseSet, course);

        clickOn(R.id.btnCreateAppointment);
        assertDisplayed(R.id.txtError, AppointmentActivity.ERROR_TXT);
    }

    @Test
    public void btnStartTimeGetsTime() {
        clickOn(R.id.btnStartTime);
        setDateOnPicker(2021, 3, 23);
        setTimeOnPicker(17, 2);
        assertDisplayed(R.id.txtStartTime, startTime);
        assertNotDisplayed(R.id.txtError);
    }

    @Test
    public void btnEndTimeGetsTime(){
        clickOn(R.id.btnEndTime);
        setDateOnPicker(2021, 3, 23);
        setTimeOnPicker(18, 2);
        assertDisplayed(R.id.txtEndTime, endTime);
        assertNotDisplayed(R.id.txtError);
    }

    @Test
    public void btnResetResets() {
        clickOn(R.id.btnStartTime);
        setDateOnPicker(2021, 3, 23);
        setTimeOnPicker(17, 2);

        clickOn(R.id.btnEndTime);
        setDateOnPicker(2021, 3, 23);
        setTimeOnPicker(18, 2);

        writeTo(R.id.editTxtAppointmentCourseSet, course);

        writeTo(R.id.editTxtAppointmentTitleSet, title);

        clickOn(R.id.btnReset);
        assertDisplayed(R.id.txtStartTime, "Start Time");
        assertNotDisplayed(R.id.txtError);
        assertDisplayed(R.id.txtEndTime, "End Time");
        assertDisplayed(R.id.editTxtAppointmentCourseSet, "Appointment Course");
        assertDisplayed(R.id.editTxtAppointmentTitleSet, "Appointment Title");
    }
}
