package io.github.polysmee.room.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
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
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.util.HashMap;
import java.util.Map;

import io.github.polysmee.R;
import io.github.polysmee.agora.VoiceCall;
import io.github.polysmee.database.DatabaseAppointment;
import io.github.polysmee.database.DatabaseUser;
import io.github.polysmee.database.databaselisteners.BooleanChildListener;
import io.github.polysmee.login.AuthenticationFactory;
import io.github.polysmee.database.Appointment;
import io.github.polysmee.database.User;
import io.github.polysmee.login.MainUserSingleton;


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
    private DatabaseAppointment databaseAppointment;
    private VoiceCall voiceCall;
    private ActivityResultLauncher<String> requestPermissionLauncher;
    private Map<String, ConstraintLayout> participantsViews;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.rootView = (ViewGroup)inflater.inflate(R.layout.fragment_activity_room_participant, container, false);

        String appointmentId = getAppointmentId();
        databaseAppointment = new DatabaseAppointment(appointmentId);
        this.appointment = new DatabaseAppointment(appointmentId);
        this.inflater = getLayoutInflater();
        generateParticipantsView();
        initializePermissionRequester();
        initializeAndDisplayDatabase();

        return rootView;
    }


    /*
     * Generate a text view for each participant
     */
    private void generateParticipantsView() {
        LinearLayout layout = rootView.findViewById(R.id.roomActivityParticipantsLayout);
        participantsViews = new HashMap<String, ConstraintLayout>();

        appointment.getParticipantsIdAndThen(p -> {
            layout.removeAllViewsInLayout();

            for (String id : p) {
                User user = new DatabaseUser(id);
                ConstraintLayout participantsLayout = (ConstraintLayout) inflater.inflate(R.layout.element_room_activity_participant, null);
                participantsViews.put(id,participantsLayout);
                participantsLayout.setBackgroundColor(Color.LTGRAY);
                participantsLayout.setBackgroundResource(R.drawable.background_participant_element);

                TextView participantName = participantsLayout.findViewById(R.id.roomActivityParticipantElementName);
                View participantsButtonLayout = participantsLayout.findViewById(R.id.roomActivityParticipantElementButtons);

                /*if (id.equals(MainUserSingleton.getInstance().getId())) {
                    participantName.setText("You");
                    participantsButtonLayout.setVisibility(View.VISIBLE);
                } else {
                    user.getNameAndThen(participantName::setText);
                    participantsButtonLayout.setVisibility(View.VISIBLE);
                }*/

                TextView ownerTag = participantsLayout.findViewById(R.id.roomActivityParticipantElementOwnerText);
                appointment.getOwnerIdAndThen(owner -> {
                    if (owner.equals(id))
                        ownerTag.setVisibility(View.VISIBLE);
                });

                ImageView muteButton = participantsLayout.findViewById(R.id.roomActivityParticipantElementMuteButton);
                //muteButton.setOnClickListener(this::muteButtonBehavior);

                ImageView callButton = participantsLayout.findViewById(R.id.roomActivityParticipantElementCallButton);
                //callButton.setOnClickListener(v -> joinChannel());//callButtonBehavior(callButton, muteButton, participantsLayout));
                participantsButtonLayout.setVisibility(View.VISIBLE);
                callButton.setVisibility(View.GONE);
                String userId = MainUserSingleton.getInstance().getId();
                if (id.equals(userId)) {
                    participantName.setText("You");
                    callButton.setVisibility(View.VISIBLE);
                    callButton.setOnClickListener(v ->  {
                        if(isInCall) {
                            leaveChannel();
                        }
                        else {
                            joinChannel();
                        }
                    });
                    muteButton.setOnClickListener(v -> muteUser());
                } else {
                    user.getNameAndThen(participantName::setText);
                }

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
            voiceCall.mute(false);
        } else {
            isMuted = true;
            ((ImageView) muteButton).setImageResource(R.drawable.baseline_mic_off);
            voiceCall.mute(true);
        }
    }

    private void callButtonBehavior(View callButton, View muteButton, View layout) {
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) callButton.getLayoutParams();

        //check permissions for bluetooth and microphone

        if(!checkPermission(Manifest.permission.RECORD_AUDIO)) {
            requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO);
            return;
        }

        if(!checkPermission(Manifest.permission.BLUETOOTH)) {
            requestPermissionLauncher.launch(Manifest.permission.BLUETOOTH);
            return;
        }

        if (isInCall) {
                isInCall = false;
                ((ImageView) callButton).setImageResource(R.drawable.baseline_call);
                //params.horizontalBias =  1f;
                layout.setBackgroundResource(R.drawable.background_participant_element);
                muteButton.setVisibility(View.GONE);
                leaveChannel();

        } else {

                isInCall = true;
                ((ImageView) callButton).setImageResource(R.drawable.baseline_call_end);
                //params.horizontalBias =  0f;
                layout.setBackgroundResource(R.drawable.background_participant_in_call_element);
                muteButton.setVisibility(View.VISIBLE);
                joinChannel();

        }

        callButton.setLayoutParams(params);

    }


    /**
     *
     * @return true if the channel is successfully joined ad false otherwise
     */
    private void joinChannel() {

        if(voiceCall == null) {

            voiceCall = new VoiceCall(databaseAppointment, this.getContext());
        }

        voiceCall.joinChannel();

    }

    /**
     *
     * @return true if the channel is successfully left and false otherwise
     */
    private void leaveChannel() {
        if(voiceCall != null) {
            voiceCall.leaveChannel();
        }

    }

    private void muteUser() {
        if(voiceCall != null) {
            voiceCall.mute(!isMuted);
        }
    }

    /**
     * Make the user whose id is given appear as online (offline) in the room frontend if online is set to true (false)
     * @param online
     * @param id
     */
    public void setUserOnline(boolean online, @NonNull String id) {

        ConstraintLayout participantsLayout = participantsViews.get(id);
        View muteButton = participantsLayout.findViewById(R.id.roomActivityParticipantElementMuteButton);
        if(online) {
            participantsLayout.setBackgroundResource(R.drawable.background_participant_in_call_element);
            muteButton.setVisibility(View.VISIBLE);
            if(id.equals(MainUserSingleton.getInstance().getId())) {
                ImageView callButton = participantsLayout.findViewById(R.id.roomActivityParticipantElementCallButton);
                callButton.setImageResource(R.drawable.baseline_call_end);
                isInCall = true;
                //joinChannel();
            }
        }
        else {
            participantsLayout.setBackgroundResource(R.drawable.background_participant_element);
            muteButton.setVisibility(View.GONE);
            if(id.equals(MainUserSingleton.getInstance().getId())) {
                isInCall = false;
                ImageView callButton = participantsLayout.findViewById(R.id.roomActivityParticipantElementCallButton);
                callButton.setImageResource(R.drawable.baseline_call);
                //leaveChannel();
            }
        }
    }


    /**
     * mute (unmute) the user whose id is given if muted is set to true (false)
     * @param muted
     * @param id
     */
    public void setMutedUser(boolean muted, @NonNull String id) {
        ConstraintLayout participantsLayout = participantsViews.get(id);
        ImageView muteButton = participantsLayout.findViewById(R.id.roomActivityParticipantElementMuteButton);
        if(muted) {
            muteButton.setImageResource(R.drawable.baseline_mic_off);
            if(id.equals(MainUserSingleton.getInstance().getId())) {
                isMuted = true;
            }
        } else {
            muteButton.setImageResource(R.drawable.baseline_mic);
            if(id.equals(MainUserSingleton.getInstance().getId())) {
                isMuted = false;
            }
        }
    }

    /**
     * Initializes the request permission requester
     */
    private void initializePermissionRequester() {
        requestPermissionLauncher =
                this.registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                    //joins the channel if granted and do nothing otherwise
                    if (isGranted) {
                        ConstraintLayout participantsLayout = participantsViews.get(AuthenticationFactory.getAdaptedInstance().getUid());
                        ImageView callButton = participantsLayout.findViewById(R.id.roomActivityParticipantElementCallButton);
                        callButton.callOnClick();

                    } else {
                        System.out.println("not granted");
                    }
                });
    }

    /**
     * return true if the permission given is granted by the user and false otherwise
     * @param permission
     * @return
     */
    private boolean checkPermission(String permission) {
        return ContextCompat.checkSelfPermission(getContext(), permission) == PackageManager.PERMISSION_GRANTED;
    }


    /**
     *
     * @return the id of the appointment
     */
    public String getAppointmentId() {
        return requireArguments().getString(PARTICIPANTS_KEY);
    }

    /**
     *
     * @return the request permission requester
     */
    public ActivityResultLauncher<String> getRequestPermissionLauncher() {
        return requestPermissionLauncher;
    }

    private void initializeAndDisplayDatabase() {
        databaseAppointment.getInCallAndThen(new BooleanChildListener() {
            @Override
            public void childAdded(String key, boolean value) {
                setUserOnline(true, key);
                setMutedUser(value, key);
            }

            @Override
            public void childRemoved(String key, boolean value) {
                setUserOnline(false, key);
            }

            @Override
            public void childChanged(String key, boolean value) {
                setMutedUser(value, key);
            }
        }
        );
    }

}
