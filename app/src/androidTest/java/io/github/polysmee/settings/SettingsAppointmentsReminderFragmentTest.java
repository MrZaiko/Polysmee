package io.github.polysmee.settings;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.preference.PreferenceManager;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.github.polysmee.R;
import io.github.polysmee.settings.fragments.SettingsAppointmentsReminderFragment;

import static com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed;
import static com.schibsted.spain.barista.interaction.BaristaSleepInteractions.sleep;
import static java.util.concurrent.TimeUnit.SECONDS;

@RunWith(AndroidJUnit4.class)
public class SettingsAppointmentsReminderFragmentTest {

    private static Context context(){
        return ApplicationProvider.getApplicationContext();
    }

    private static int getSettingsTimeFromAppointmentValueWithDefault0(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context());
        return sharedPreferences.getInt(context().getResources().getString(R.string.preference_key_appointments_reminder_notification_time_from_appointment_minutes), 0);
    }

    @Before
    @After
    public void resetPreference(){
        PreferenceManager.getDefaultSharedPreferences(context()).edit().putInt(
                context().getResources().getString(R.string.preference_key_appointments_reminder_notification_time_from_appointment_minutes),
                context().getResources().getInteger(R.integer.default_appointment_reminder_notification__time_from_appointment_min)).commit();
    }
    @Before
    public void createFragment(){
        FragmentScenario.launchInContainer(SettingsAppointmentsReminderFragment.class);
        sleep(1, SECONDS);
    }

    //used in tests (this test or any other) to know if the fragment is been displayed
    public static void checkFragmentIsDisplayed(){
        assertDisplayed(R.string.title_settings_appointments_reminder_notification_time_from_appointment);
        assertDisplayed(R.string.summary_settings_appointments_reminder_notification_time_from_appointment);
        assertDisplayed(""+ApplicationProvider.getApplicationContext().getResources().getInteger(R.integer.default_appointment_reminder_notification__time_from_appointment_min));
    }

    @Test
    public void checkFragmentIsWellDisplayed(){
        checkFragmentIsDisplayed();
    }

    @Test
    public void preference_time_from_appointment_default() {
        int expectedPreferenceValue = context().getResources().getInteger(R.integer.default_appointment_reminder_notification__time_from_appointment_min);
        int preference_value_time_from_appointment = getSettingsTimeFromAppointmentValueWithDefault0();
        Assert.assertEquals(expectedPreferenceValue, preference_value_time_from_appointment);
    }

  /**  @Test
    public void preference_time_from_appointment_change_settings_value_up() {
        Assert.assertEquals(context().getResources().getInteger(R.integer.default_appointment_reminder_notification__time_from_appointment_min), getSettingsTimeFromAppointmentValueWithDefault0());
        int expectedPreferenceValue = context().getResources().getInteger(R.integer.default_appointment_reminder_notification__time_from_appointment_min)+1;
        UiDevice device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        device.pressKeyCode(KeyEvent.KEYCODE_DPAD_RIGHT);
        sleep(1, SECONDS);
        int preference_value_time_from_appointment = getSettingsTimeFromAppointmentValueWithDefault0();
        Assert.assertEquals(expectedPreferenceValue, preference_value_time_from_appointment);
    }

    @Test
    public void preference_time_from_appointment_change_settings_value_down() {
        Assert.assertEquals(context().getResources().getInteger(R.integer.default_appointment_reminder_notification__time_from_appointment_min), getSettingsTimeFromAppointmentValueWithDefault0());
        int expectedPreferenceValue = context().getResources().getInteger(R.integer.default_appointment_reminder_notification__time_from_appointment_min)-1;
        UiDevice device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        device.pressKeyCode(KeyEvent.KEYCODE_DPAD_LEFT);
        sleep(1, SECONDS);
        int preference_value_time_from_appointment = getSettingsTimeFromAppointmentValueWithDefault0();
        Assert.assertEquals(expectedPreferenceValue, preference_value_time_from_appointment);

    }**/

}