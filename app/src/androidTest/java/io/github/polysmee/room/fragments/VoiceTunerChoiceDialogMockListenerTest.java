package io.github.polysmee.room.fragments;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import org.junit.Assert;

public final class VoiceTunerChoiceDialogMockListenerTest extends AppCompatActivity implements VoiceTunerChoiceDialogFragment.VoiceTunerChoiceDialogFragmentListener {
    VoiceTunerChoiceDialogFragment voiceTunerChoiceDialog;
    public final static String buttonText = "button";
    public static int elementIndexTest=0;
    /**
     * The function called to handle a choice in the array R.array.voices_tune_array
     *
     * @param elementIndex the index of the chosen item in R.array_voices_tune_array
     */
    @Override
    public void onDialogChoiceSingleChoiceItems(int elementIndex) {
        elementIndexTest = elementIndex;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        Button button = new Button(this);
        button.setText(buttonText);
        button.setOnClickListener(l->showDialog());
        button.setLayoutParams(params);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ActionBar.LayoutParams.FILL_PARENT, ActionBar.LayoutParams.WRAP_CONTENT);
        this.addContentView(linearLayout, layoutParams);
    }

    public void showDialog(){
        if (voiceTunerChoiceDialog==null){
            voiceTunerChoiceDialog = new VoiceTunerChoiceDialogFragment();
        }
        voiceTunerChoiceDialog.show(getSupportFragmentManager(),"Voice_tuner_Choice_dialog");
    }
}
