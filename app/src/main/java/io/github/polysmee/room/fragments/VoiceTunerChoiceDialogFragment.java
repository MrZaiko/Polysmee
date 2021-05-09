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
 * Inspired from https://developer.android.com/guide/topics/ui/dialogs
 */
public class VoiceTunerChoiceDialogFragment extends DialogFragment {

    private VoiceTunerChoiceDialogFragmentListener listener;
    public interface VoiceTunerChoiceDialogFragmentListener {
        void onDialogPositiveClick(int elementIndex);
    }

    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.title_voice_tuner_dialog)
                .setSingleChoiceItems(R.array.voices_tune_array, 0, (dialog, which) -> {
                    listener.onDialogPositiveClick(which);
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
