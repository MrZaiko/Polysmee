package io.github.polysmee.settings;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import junit.framework.TestCase;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.github.polysmee.R;
import io.github.polysmee.settings.fragments.SettingsMainFragment;
import io.github.polysmee.settings.fragments.SettingsUserInfoFragment;

import static com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed;
import static com.schibsted.spain.barista.interaction.BaristaClickInteractions.clickOn;

@RunWith(AndroidJUnit4.class)
public class SettingsActivityTest extends TestCase {

    @Rule
    public ActivityScenarioRule<SettingsActivity> testRule = new ActivityScenarioRule<SettingsActivity>(SettingsActivity.class);

    @Test
    public void appointmentsReminderSettingFragmentsIsLaunchWhenClickOnMain(){
        SettingsMainFragmentTest.checkFragmentIsDisplayed();
        clickOn(R.string.title_settings_appointments_reminder);
        SettingsAppointmentsReminderFragmentTest.checkFragmentIsDisplayed();
    }
    @Test
    public void settingsUserInfoFragmentIsLaunchWhenClickOnMain(){
        SettingsMainFragmentTest.checkFragmentIsDisplayed();
        clickOn(R.string.title_settings_main_user_info);
        SettingsUserInfoFragmentTest.checkFragmentIsDisplayed();
    }
}