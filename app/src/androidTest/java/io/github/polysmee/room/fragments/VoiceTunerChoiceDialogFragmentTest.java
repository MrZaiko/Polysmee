package io.github.polysmee.room.fragments;

import android.content.Intent;

import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.github.polysmee.R;

import static com.schibsted.spain.barista.interaction.BaristaClickInteractions.clickOn;

@RunWith(AndroidJUnit4.class)
public class VoiceTunerChoiceDialogFragmentTest {

    @Test
    public void voiceTunerChoiceTest(){
        Intent intent = new Intent(InstrumentationRegistry.getInstrumentation().getContext(), VoiceTunerChoiceDialogMockListenerTest.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        InstrumentationRegistry.getInstrumentation().getContext().startActivity(intent);
        String[] voicesTune = ApplicationProvider.getApplicationContext().getResources().getStringArray(R.array.voices_tune_array);
        for (int i = 0; i < voicesTune.length; i++) {
            clickOn(VoiceTunerChoiceDialogMockListenerTest.buttonText);
            clickOn(voicesTune[i]);
            Assert.assertEquals(i,VoiceTunerChoiceDialogMockListenerTest.elementIndexTest);

        }
    }


}