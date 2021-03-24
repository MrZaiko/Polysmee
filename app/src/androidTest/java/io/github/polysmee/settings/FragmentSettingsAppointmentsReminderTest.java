package io.github.polysmee.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.KeyEvent;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.preference.PreferenceManager;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.UiDevice;

import com.schibsted.spain.barista.rule.cleardata.ClearPreferencesRule;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.github.polysmee.R;

import static com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed;
import static com.schibsted.spain.barista.interaction.BaristaSleepInteractions.sleep;
import static java.util.concurrent.TimeUnit.SECONDS;

@RunWith(AndroidJUnit4.class)
public class FragmentSettingsAppointmentsReminderTest {

    private static Context context = ApplicationProvider.getApplicationContext();
    private static int getSettingsTimeFromAppointmentValueWithDefault0(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getInt(context.getResources().getString(R.string.preference_key_appointments_reminder_notification_time_from_appointment_minutes), 0);
    }

    @Before
    public void createFragment(){
        FragmentScenario.launchInContainer(FragmentSettingsAppointmentsReminder.class);
        sleep(1, SECONDS);
    }

    // Clear all app's SharedPreferences
    @Rule
    public ClearPreferencesRule clearPreferencesRule = new ClearPreferencesRule();


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
        int expectedPreferenceValue = context.getResources().getInteger(R.integer.default_appointment_reminder_notification__time_from_appointment_min);
        int preference_value_time_from_appointment = getSettingsTimeFromAppointmentValueWithDefault0();
        Assert.assertEquals(expectedPreferenceValue, preference_value_time_from_appointment);
    }

    @Test
    public void preference_time_from_appointment_change_settings_value_up() {
        int expectedPreferenceValue = context.getResources().getInteger(R.integer.default_appointment_reminder_notification__time_from_appointment_min)+1;
        UiDevice device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        device.pressKeyCode(KeyEvent.KEYCODE_DPAD_RIGHT);
        sleep(1, SECONDS);
        int preference_value_time_from_appointment = getSettingsTimeFromAppointmentValueWithDefault0();
        Assert.assertEquals(expectedPreferenceValue, preference_value_time_from_appointment);
    }

    @Test
    public void preference_time_from_appointment_change_settings_value_down() {

        int expectedPreferenceValue = context.getResources().getInteger(R.integer.default_appointment_reminder_notification__time_from_appointment_min)-1;
        UiDevice device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        device.pressKeyCode(KeyEvent.KEYCODE_DPAD_LEFT);
        sleep(1, SECONDS);
        int preference_value_time_from_appointment = getSettingsTimeFromAppointmentValueWithDefault0();
        Assert.assertEquals(expectedPreferenceValue, preference_value_time_from_appointment);

    }

}