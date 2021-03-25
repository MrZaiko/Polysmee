package io.github.polysmee.room.fragments;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import io.github.polysmee.R;
import io.github.polysmee.database.DatabaseAppointment;
import io.github.polysmee.database.DatabaseUser;
import io.github.polysmee.interfaces.Appointment;
import io.github.polysmee.interfaces.User;
import io.github.polysmee.login.MainUserSingleton;

/**
 * Fragment that display all participants given in argument
 */
public class RoomActivityParticipantsFragment extends Fragment {

    private ViewGroup rootView;
    private Appointment appointment;
    public static String PARTICIPANTS_KEY = "io.github.polysme.room.fragments.roomActivityParticipantsFragment.PARTICIPANTS_KEY";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.rootView = (ViewGroup)inflater.inflate(R.layout.fragment_activity_room_participant, container, false);

        String appointmentId = requireArguments().getString(PARTICIPANTS_KEY);
        this.appointment = new DatabaseAppointment(appointmentId);

        generateParticipantsView();

        return rootView;
    }

    /*
     * Generate a text view for each participant
     */
    private void generateParticipantsView() {
        LinearLayout layout = rootView.findViewById(R.id.roomActivityParticipantsLayout);

        appointment.getParticipantsIdAndThen(p -> {
            layout.removeAllViewsInLayout();

            for (String id : p) {
                User user = new DatabaseUser(id);
                TextView participant = new TextView(rootView.getContext());
                user.getNameAndThen(participant::setText);
                participant.setTextSize(20);
                participant.setBackgroundColor(id.equals(MainUserSingleton.getInstance().getId()) ? Color.GRAY : Color.LTGRAY);
                participant.setClickable(true);
                participant.setOnClickListener(onClick -> generateEditParticipantDialog(user));

                layout.addView(participant);
                layout.addView(new TextView(rootView.getContext()));
            }
        });
    }

    private void generateEditParticipantDialog(User user) {
        AlertDialog.Builder builder = new AlertDialog.Builder(rootView.getContext());
        builder.setTitle("Edit participant");
        LayoutInflater inflater = getLayoutInflater();

        View dialogView = inflater.inflate(R.layout.dialog_room_participant_edit, null);
        Button removeButton = dialogView.findViewById(R.id.roomActivityParticipantDialogRemoveButton);

        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        removeButton.setVisibility(user.getId().equals(MainUserSingleton.getInstance().getId()) ? View.VISIBLE : View.GONE);
        removeButton.setText(user.getId().equals(MainUserSingleton.getInstance().getId()) ? "Quit" : "Remove");
        removeButton.setOnClickListener(s -> {
            appointment.removeParticipant(user);
            dialog.cancel();
        });

        appointment.getOwnerIdAndThen(id -> {
            if (MainUserSingleton.getInstance().getId().equals(id))
                removeButton.setVisibility(View.VISIBLE);
        });

        dialog.show();
    }
}
