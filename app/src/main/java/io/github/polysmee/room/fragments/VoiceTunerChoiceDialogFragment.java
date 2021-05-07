package io.github.polysmee.room.fragments;


import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;


import androidx.fragment.app.DialogFragment;

import io.github.polysmee.R;

public class VoiceTunerChoiceDialogFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.title_voice_tuner_dialog)
                .setSingleChoiceItems(R.array.voices_tune_array, 0, new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                });
    }
}
