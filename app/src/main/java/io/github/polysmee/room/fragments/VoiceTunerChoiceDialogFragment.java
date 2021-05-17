package io.github.polysmee.room.fragments;


import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;


import io.github.polysmee.R;

/**
 * Inspired from https://developer.android.com/guide/topics/ui/dialogs.
 * This dialog remember the choice made before on the hosting activity lifecycle, if it is the first
 * choice the default choice is the first element of the array R.array_voices_tune_array
 */
public final class VoiceTunerChoiceDialogFragment extends DialogFragment {
    private int previousChoice = 0;
    private final VoiceTunerChoiceDialogFragmentListener listener;

    /**
     * Create a instance of the class with the provided argument as a listener
     *
     * @param voiceTunerChoiceDialogFragmentListener the VoiceTunerChoiceDialogFramgent listener
     */
    public VoiceTunerChoiceDialogFragment(@NonNull VoiceTunerChoiceDialogFragmentListener voiceTunerChoiceDialogFragmentListener) {
        listener = voiceTunerChoiceDialogFragmentListener;
    }

    /**
     * The interface of a listener of this dialog, for a class to become a listener it must implements it
     */
    public interface VoiceTunerChoiceDialogFragmentListener {
        /**
         * The function called to handle a choice in the array R.array.voices_tune_array
         *
         * @param elementIndex the index of the chosen item in R.array_voices_tune_array
         */
        void onDialogChoiceSingleChoiceItems(int elementIndex);
    }

    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        getContext().setTheme(R.style.Theme_Polysmee);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.title_voice_tuner_dialog)
                .setSingleChoiceItems(R.array.voices_tune_array, previousChoice, (dialog, which) -> {
                    assert listener != null;
                    listener.onDialogChoiceSingleChoiceItems(which);
                    previousChoice = which;
                    dismiss();
                });
        return builder.create();
    }


}
