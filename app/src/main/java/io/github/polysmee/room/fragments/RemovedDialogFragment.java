package io.github.polysmee.room.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import io.github.polysmee.R;

/**
 * Dialog not cancellable that blocks access to a room activity
 */
public class RemovedDialogFragment extends Fragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Disable back button
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                // Ignored
            }
        };

        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout to use as dialog or embedded fragment
        View rootView = inflater.inflate(R.layout.fragment_room_activity_removed, container, false);

        rootView.setBackgroundColor(Color.WHITE);
        Button quitButton = rootView.findViewById(R.id.roomActivityRemovedDialogQuitButton);
        quitButton.setOnClickListener(this::quitAppointment);

        rootView.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                return keyCode == KeyEvent.KEYCODE_BACK;
            }
            return false;
        });

        return rootView;
    }

    public void quitAppointment(View view) {
        getActivity().finish();
    }

}
