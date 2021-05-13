package io.github.polysmee.agora.video.handlers.settings;

import androidx.preference.PreferenceManager;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.schibsted.spain.barista.rule.cleardata.ClearPreferencesRule;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.github.polysmee.R;

import static com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed;
import static com.schibsted.spain.barista.interaction.BaristaSpinnerInteractions.clickSpinnerItem;

@RunWith(AndroidJUnit4.class)
public class VoiceTunerActivityTest {


    @Rule
    public ActivityScenarioRule<VoiceTunerActivity> testRule = new ActivityScenarioRule<>(VoiceTunerActivity.class);

    @Rule
    public ClearPreferencesRule clearPreferencesRule = new ClearPreferencesRule();

    @Test
    public void onCreate() {
        assertDisplayed(R.id.voiceTunerSpinner);
        assertDisplayed(R.string.voice_no_tune);
        String[] voicesTune = ApplicationProvider.getApplicationContext().getResources().getStringArray(R.array.voices_tune_array);
        for (int i = 0; i < voicesTune.length; i++) {
            clickSpinnerItem(R.id.voiceTunerSpinner, i);
            assertDisplayed(voicesTune[i]);
        }
    }

    @Test
    public void onItemSelected() {
        String[] voicesTune = ApplicationProvider.getApplicationContext().getResources().getStringArray(R.array.voices_tune_array);
        for (int i = 0; i < voicesTune.length; i++) {
            clickSpinnerItem(R.id.voiceTunerSpinner, i);
            int currentVoicePosition = PreferenceManager.getDefaultSharedPreferences(ApplicationProvider.getApplicationContext()).getInt(
                    ApplicationProvider.getApplicationContext().getResources().getString(R.string.preference_key_voice_tuner_current_voice_tune), 0);
            Assert.assertEquals(currentVoicePosition, i);
        }

    }
}