package io.github.polysmee.room.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import io.github.polysmee.R;
import io.github.polysmee.agora.VoiceCall;
import io.github.polysmee.database.DatabaseAppointment;
import io.github.polysmee.database.DatabaseUser;
import io.github.polysmee.interfaces.Appointment;
import io.github.polysmee.interfaces.User;
import io.github.polysmee.login.MainUserSingleton;

import static io.github.polysmee.room.fragments.RoomActivityMessagesFragment.MESSAGES_KEY;

/**
 * Fragment that display all participants given in argument
 */
public class RoomActivityParticipantsFragment extends Fragment {

    private ViewGroup rootView;
    private Appointment appointment;
    private LayoutInflater inflater;
    public static String PARTICIPANTS_KEY = "io.github.polysme.room.fragments.roomActivityParticipantsFragment.PARTICIPANTS_KEY";

    private boolean isMuted = false;
    private boolean isInCall = false;

    private VoiceCall voiceCall;
    private ActivityResultLauncher<String> requestPermissionLauncher;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.rootView = (ViewGroup)inflater.inflate(R.layout.fragment_activity_room_participant, container, false);

        String appointmentId = requireArguments().getString(PARTICIPANTS_KEY);
        this.appointment = new DatabaseAppointment(appointmentId);
        this.inflater = getLayoutInflater();
        generateParticipantsView();
        initializePermissionRequester();

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
                ConstraintLayout participantsLayout = (ConstraintLayout) inflater.inflate(R.layout.element_room_activity_participant, null);
                participantsLayout.setBackgroundColor(Color.LTGRAY);
                participantsLayout.setBackgroundResource(R.drawable.background_participant_element);

                TextView participantName = participantsLayout.findViewById(R.id.roomActivityParticipantElementName);
                View participantsButtonLayout = participantsLayout.findViewById(R.id.roomActivityParticipantElementButtons);

                if (id.equals(MainUserSingleton.getInstance().getId())) {
                    participantName.setText("You");
                    participantsButtonLayout.setVisibility(View.VISIBLE);
                } else {
                    user.getNameAndThen(participantName::setText);
                    participantsButtonLayout.setVisibility(View.GONE);
                }

                TextView ownerTag = participantsLayout.findViewById(R.id.roomActivityParticipantElementOwnerText);
                appointment.getOwnerIdAndThen(owner -> {
                    if (owner.equals(id))
                        ownerTag.setVisibility(View.VISIBLE);
                });

                ImageView muteButton = participantsLayout.findViewById(R.id.roomActivityParticipantElementMuteButton);
                muteButton.setOnClickListener(this::muteButtonBehavior);

                ImageView callButton = participantsLayout.findViewById(R.id.roomActivityParticipantElementCallButton);
                callButton.setOnClickListener(v -> callButtonBehavior(callButton, muteButton, participantsLayout));

                layout.addView(participantsLayout);

                //Add a blank textView to add space between participant entries
                layout.addView(new TextView(rootView.getContext()));
            }
        });
    }

    private void muteButtonBehavior(View muteButton) {
        System.out.println("MUTE");
        if (isMuted) {
            isMuted = false;
            ((ImageView) muteButton).setImageResource(R.drawable.baseline_mic);
        } else {
            isMuted = true;
            ((ImageView) muteButton).setImageResource(R.drawable.baseline_mic_off);
        }
    }

    private void callButtonBehavior(View callButton, View muteButton, View layout) {
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) callButton.getLayoutParams();

        if (isInCall) {
            isInCall = false;
            ((ImageView) callButton).setImageResource(R.drawable.baseline_call);
            //params.horizontalBias =  1f;
            layout.setBackgroundResource(R.drawable.background_participant_element);
            muteButton.setVisibility(View.GONE);
        } else {
            isInCall = true;
            ((ImageView) callButton).setImageResource(R.drawable.baseline_call_end);
            //params.horizontalBias =  0f;
            layout.setBackgroundResource(R.drawable.background_participant_in_call_element);
            muteButton.setVisibility(View.VISIBLE);

        }

        callButton.setLayoutParams(params);

    }

    private void joinChannel() {

        if(voiceCall == null) {
            String appointmentId = requireArguments().getString(MESSAGES_KEY);

            voiceCall = new VoiceCall(appointmentId, getContext(), requestPermissionLauncher);
        }

        voiceCall.joinChannel();

    }

    private void initializePermissionRequester() {
        requestPermissionLauncher =
                registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                    if (isGranted) {
                        System.out.println("granted");
                    } else {
                        System.out.println("not granted");
                    }
                });
    }
}
