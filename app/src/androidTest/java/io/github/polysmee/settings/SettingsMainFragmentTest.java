package io.github.polysmee.settings;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.schibsted.spain.barista.rule.cleardata.ClearPreferencesRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.github.polysmee.R;
import io.github.polysmee.settings.fragments.SettingsMainFragment;

import static com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed;
import static com.schibsted.spain.barista.interaction.BaristaSleepInteractions.sleep;
import static java.util.concurrent.TimeUnit.SECONDS;

@RunWith(AndroidJUnit4.class)
public class SettingsMainFragmentTest {

    // Clear all app's SharedPreferences
    @Rule
    public ClearPreferencesRule clearPreferencesRule = new ClearPreferencesRule();

    @Before
    public void createFragment() {
        FragmentScenario.launchInContainer(SettingsMainFragment.class);
        sleep(1, SECONDS);
    }

    //used in tests (this test or any other) to know if the fragment is been displayed
    public static void checkFragmentIsDisplayed() {
        assertDisplayed(R.string.title_settings_appointments_reminder);
    }

    @Test
    public void checkFragmentIsWellDisplayed() {
        checkFragmentIsDisplayed();
    }
}