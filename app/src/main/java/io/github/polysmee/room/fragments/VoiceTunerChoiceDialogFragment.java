package io.github.polysmee.room.fragments;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;


import androidx.fragment.app.DialogFragment;

import org.jetbrains.annotations.NotNull;

import io.github.polysmee.R;

/**
 * Inspired from https://developer.android.com/guide/topics/ui/dialogs.
 * This dialog remember the choice made before on the hosting activity lifecycle, if it is the first
 * choice the default choice is the first element of the array R.array_voices_tune_array
 */
public class VoiceTunerChoiceDialogFragment extends DialogFragment {
    private int previousChoice=0;
    private VoiceTunerChoiceDialogFragmentListener listener;

    /**
     * The interface of a listener of this dialog, for a activity to become a listener it must implements it
     */
    public interface VoiceTunerChoiceDialogFragmentListener {
        /**
         * The function called to handle a choice in the array R.array.voices_tune_array
         * @param elementIndex the index of the chosen item in R.array_voices_tune_array
         */
        void onDialogChoiceSingleChoiceItems(int elementIndex);
    }

    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.title_voice_tuner_dialog)
                .setSingleChoiceItems(R.array.voices_tune_array, previousChoice, (dialog, which) -> {
                    listener.onDialogChoiceSingleChoiceItems(which);
                    previousChoice=which;
                    dismiss();
                    });
        return builder.create();
    }

    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            listener = (VoiceTunerChoiceDialogFragmentListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(context.toString()
                    + " must implement NoticeDialogListener");
        }
    }


}
