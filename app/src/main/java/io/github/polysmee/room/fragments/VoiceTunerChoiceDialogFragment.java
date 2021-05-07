package io.github.polysmee.room.fragments;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;


import androidx.fragment.app.DialogFragment;

import io.github.polysmee.R;

/**
 * Inspired from https://developer.android.com/guide/topics/ui/dialogs
 */
public class VoiceTunerChoiceDialogFragment extends DialogFragment {

    private VoiceTunerChoiceDialogFragmentListener listener;
    public interface VoiceTunerChoiceDialogFragmentListener {
        public void onDialogPositiveClick(int elementIndex);
    }

    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.title_voice_tuner_dialog)
                .setSingleChoiceItems(R.array.voices_tune_array, 0, new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.onDialogPositiveClick(which);
                    }
                });
        return builder.create();
    };

    @Override
    public void onAttach(Context context) {
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
