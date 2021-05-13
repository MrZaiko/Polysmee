package io.github.polysmee.room.fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;
import io.github.polysmee.R;
import io.github.polysmee.agora.video.Call;
import io.github.polysmee.database.Appointment;
import io.github.polysmee.database.DatabaseAppointment;
import io.github.polysmee.database.DatabaseUser;
import io.github.polysmee.database.UploadServiceFactory;
import io.github.polysmee.database.User;
import io.github.polysmee.database.databaselisteners.LongValueListener;
import io.github.polysmee.database.databaselisteners.StringSetValueListener;
import io.github.polysmee.database.databaselisteners.StringValueListener;
import io.github.polysmee.internet.connection.InternetConnection;
import io.github.polysmee.database.databaselisteners.BooleanChildListener;
import io.github.polysmee.login.MainUser;
import io.github.polysmee.profile.ProfileActivity;


/**
 * Fragment that display all participants given in argument
 */
public class RoomActivityParticipantsFragment extends Fragment implements VoiceTunerChoiceDialogFragment.VoiceTunerChoiceDialogFragmentListener {

    private ViewGroup rootView;
    private Appointment appointment;
    private LayoutInflater inflater;
    public static String PARTICIPANTS_KEY = "io.github.polysme.room.fragments.roomActivityParticipantsFragment.PARTICIPANTS_KEY";

    private boolean isMuted = false;
    private boolean isInCall = false;


    private DatabaseAppointment databaseAppointment;
    private RoomActivityVideoFragment videoFragment;

    private ActivityResultLauncher<String> requestPermissionLauncher;
    private Map<String, ConstraintLayout> participantsViews;
    private Set<String> inCall = new HashSet<String>();
    private Set<String> locallyMuted = new HashSet<String>();

    private Call call;
    private VoiceTunerChoiceDialogFragment voiceTunerChoiceDialog;

    //Commands to remove listeners
    private List<Command> commandsToRemoveListeners = new ArrayList<Command>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.rootView = (ViewGroup) inflater.inflate(R.layout.fragment_activity_room_participant, container, false);

        String appointmentId = getAppointmentId();
        databaseAppointment = new DatabaseAppointment(appointmentId);
        this.appointment = new DatabaseAppointment(appointmentId);
        this.inflater = getLayoutInflater();
        generateParticipantsView();
        initializePermissionRequester();
        initializeAndDisplayDatabase();

        if (getActivity().getSupportFragmentManager().getFragments().size() > 1)
            videoFragment = (RoomActivityVideoFragment) getActivity().getSupportFragmentManager().getFragments().get(1);

        if (call != null) {
            call.setCommand(this::setTalkingUser);
        }

        return rootView;
    }


    public RoomActivityParticipantsFragment() {
        // Required empty public constructor
    }

    public RoomActivityParticipantsFragment(Call call) {
        this.call = call;
    }

    @Override
    public void onDestroy() {
        if (call != null) {
            call.destroy();
        }

        Object dummyArgument = null;
        for(Command command : commandsToRemoveListeners) {
            command.execute(dummyArgument,dummyArgument);
        }

        super.onDestroy();
    }


    /*
     * Generate a text view for each participant
     */
    private void generateParticipantsView() {
        LinearLayout layout = rootView.findViewById(R.id.roomActivityParticipantsLayout);

        participantsViews = new HashMap<String, ConstraintLayout>();
        StringSetValueListener participantListener = p -> {
            layout.removeAllViewsInLayout();

            for (String id : p) {
                User user = new DatabaseUser(id);

                appointment.getTimeCodeOnceAndThen(user, o -> {
                    if (System.currentTimeMillis() - o > Call.INVALID_TIME_CODE_TIME) {
                        appointment.removeOfCall(user);
                    }
                });

                ConstraintLayout participantsLayout = (ConstraintLayout) inflater.inflate(R.layout.element_room_activity_participant, null);
                participantsViews.put(id, participantsLayout);
                participantsLayout.setBackgroundColor(Color.LTGRAY);
                participantsLayout.setBackgroundResource(R.drawable.background_participant_element);

                TextView participantName = participantsLayout.findViewById(R.id.roomActivityParticipantElementName);
                View participantsButtonLayout = participantsLayout.findViewById(R.id.roomActivityParticipantElementButtons);

                CircleImageView profilePicture = participantsLayout.findViewById(R.id.roomActivityParticipantElementProfilePicture);
                user.getProfilePicture_Once_And_Then(pictureId -> downloadPicture(pictureId, profilePicture));
                profilePicture.setOnClickListener(v -> visitProfile(id, MainUser.getMainUser().getId().equals(id)));

                TextView ownerTag = participantsLayout.findViewById(R.id.roomActivityParticipantElementOwnerText);
                StringValueListener ownerListener = owner -> {
                    if (owner.equals(id))
                        ownerTag.setVisibility(View.VISIBLE);
                };
                appointment.getOwnerIdAndThen(ownerListener);
                commandsToRemoveListeners.add((x,y) -> appointment.removeOwnerListener(ownerListener));

                ImageView muteButton = participantsLayout.findViewById(R.id.roomActivityParticipantElementMuteButton);

                ImageView videoButton = participantsLayout.findViewById(R.id.roomActivityParticipantElementVideoButton);
                videoButton.setTag(R.drawable.baseline_video_off);

                ImageView callButton = participantsLayout.findViewById(R.id.roomActivityParticipantElementCallButton);

                ImageView friendshipButton = participantsButtonLayout.findViewById(R.id.roomActivityManageParticipantAsFriendButton);

                participantsButtonLayout.setVisibility(View.VISIBLE);
                callButton.setVisibility(View.GONE);
                String userId = MainUser.getMainUser().getId();

                if (id.equals(userId)) {
                    //set the participants layout for the user using the app
                    ImageView audioTune = participantsLayout.findViewById(R.id.roomActivityParticipantElementOwnerVoiceMenu);
                    audioTune.setVisibility(View.VISIBLE);
                    audioTune.setOnClickListener(v -> {
                        if (voiceTunerChoiceDialog == null) {
                            voiceTunerChoiceDialog = new VoiceTunerChoiceDialogFragment(this);
                        }
                        voiceTunerChoiceDialog.show(getActivity().getSupportFragmentManager(), "Voice_tuner_Choice_dialog");

                    });
                    participantName.setText(getString(R.string.genericYouText));
                    callButton.setVisibility(View.VISIBLE);

                    callButton.setOnClickListener(v -> {
                        if (isInCall) {
                            leaveChannel();
                        } else {
                            joinChannel();
                        }
                    });

                    muteButton.setOnClickListener(v -> muteUser());
                    videoButton.setOnClickListener(this::shareVideoBehavior);
                } else {
                    ImageView speakerButton = participantsLayout.findViewById(R.id.roomActivityParticipantElementSpeakerButton);
                    speakerButton.setOnClickListener(v -> muteUserLocally(!locallyMuted.contains(id),id));
                    StringValueListener nameListener = participantName::setText;
                    user.getNameAndThen(nameListener);
                    commandsToRemoveListeners.add((x,y) -> user.removeNameListener(nameListener));
                    MainUser.getMainUser().getFriends_Once_And_Then((friendsIds)->{
                        if(friendsIds.contains(id)){
                            friendshipButton.setImageResource(R.drawable.baseline_remove);
                        } else {
                            friendshipButton.setImageResource(R.drawable.baseline_add);
                        }
                    });
                    friendshipButton.setVisibility(View.VISIBLE);
                    friendshipButton.setOnClickListener((v) -> {
                        friendshipButtonBehavior(v, id);
                    });
                }


                layout.addView(participantsLayout);

                //Add a blank textView to add space between participant entries
                layout.addView(new TextView(rootView.getContext()));
            }
            refreshViews();
        };
        appointment.getParticipantsIdAndThen(participantListener);
        commandsToRemoveListeners.add((x,y) -> appointment.removeParticipantsListener(participantListener));

    }

    private void downloadPicture(String pictureId, CircleImageView profilePicture) {
        if (pictureId != null && !pictureId.equals("")) {
            UploadServiceFactory.getAdaptedInstance().downloadImage(pictureId, imageBytes -> {
                Bitmap bmp = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                profilePicture.setImageBitmap(Bitmap.createBitmap(bmp));
            }, ss -> HelperImages.showToast(getString(R.string.genericErrorText), getContext()));
        }
    }

    private void visitProfile(String userId, boolean isOwner) {
        Intent profileIntent = new Intent(getContext(), ProfileActivity.class);
        profileIntent.putExtra(ProfileActivity.PROFILE_VISIT_CODE, isOwner ? ProfileActivity.PROFILE_OWNER_MODE : ProfileActivity.PROFILE_VISITING_MODE);
        profileIntent.putExtra(ProfileActivity.PROFILE_ID_USER, userId);
        startActivityForResult(profileIntent, ProfileActivity.VISIT_MODE_REQUEST_CODE);
    }

    /**
     * regenerates the participant views in the room
     */
    private void refreshViews() {
        LinearLayout layout = rootView.findViewById(R.id.roomActivityParticipantsLayout);
        layout.removeAllViews();
        String userId = MainUser.getMainUser().getId();
        //add current user for it to appear first
        layout.addView(participantsViews.get(userId));
        layout.addView(new TextView(rootView.getContext()));

        Set<String> nowInCall = new HashSet<String>(inCall);
        Set<String> notInCall = new HashSet<String>();

        //make the users that are connected to the call appear on the top of the screen
        for (String id : participantsViews.keySet()) {
            if (!id.equals(userId)) {
                if (nowInCall.contains(id)) {
                    layout.addView(participantsViews.get(id));
                    //Add a blank textView to add space between participant entries
                    layout.addView(new TextView(rootView.getContext()));
                } else {
                    notInCall.add(id);
                }
            }

        }

        for (String id : notInCall) {

            layout.addView(participantsViews.get(id));
            //Add a blank textView to add space between participant entries
            layout.addView(new TextView(rootView.getContext()));
        }
    }


    private void friendshipButtonBehavior(View friendshipButton, String userId) {
        MainUser.getMainUser().getFriends_Once_And_Then((friendsIds) -> {
            if (friendsIds.contains(userId)) {
                ((ImageView) friendshipButton).setImageResource(R.drawable.baseline_add);
                MainUser.getMainUser().removeFriend(new DatabaseUser(userId));
            } else {
                ((ImageView) friendshipButton).setImageResource(R.drawable.baseline_remove);
                MainUser.getMainUser().addFriend(new DatabaseUser(userId));
            }
        });
    }

    private void shareVideoBehavior(View cameraButton) {
        if (call.isVideoEnabled()) {
            //disable button
            ((ImageView) cameraButton).setImageResource(R.drawable.baseline_video_off);
            cameraButton.setTag(R.drawable.baseline_video_off);
        } else {
            ((ImageView) cameraButton).setImageResource(R.drawable.baseline_video);
            cameraButton.setTag(R.drawable.baseline_video);
        }
        call.shareLocalVideo();
    }

    private void joinChannel() {
        if(InternetConnection.isOn()) {
            if(!checkPermission(Manifest.permission.RECORD_AUDIO)) {
                requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO);
                return;
            }

            if(!checkPermission(Manifest.permission.BLUETOOTH)) {
                requestPermissionLauncher.launch(Manifest.permission.BLUETOOTH);
                return;
            }

            if(!checkPermission(Manifest.permission.CAMERA)){
                requestPermissionLauncher.launch(Manifest.permission.CAMERA);
            }

            if(call == null){
                call = new Call(getAppointmentId(), getContext());
                call.setCommand(this::setTalkingUser);
            }
            call.joinChannel();
            int preference = PreferenceManager.getDefaultSharedPreferences(getContext()).getInt(getResources().getString(R.string.preference_key_voice_tuner_current_voice_tune),0);
            setAudioEffect(preference);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setMessage(R.string.offline_call);

            //add ok button
            builder.setPositiveButton(R.string.offline_ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            builder.show();
        }
    }


    private void leaveChannel() {
        if (call != null) {
            call.leaveChannel();
        }
    }

    /**
     * Mutes current user in the call if he is not muted and unmutes him otherwise
     */
    private void muteUser() {
        if (call != null) {
            call.mute(!isMuted);
        }
    }


    /**
     * Make the user whose id is given appear as online (offline) in the room frontend if online is set to true (false)
     *
     * @param online decides how the user will appear in the room frontend
     * @param id     the user's id
     */
    public void setUserOnline(boolean online, @NonNull String id) {

        ConstraintLayout participantsLayout = participantsViews.get(id);
        View muteButton = participantsLayout.findViewById(R.id.roomActivityParticipantElementMuteButton);
        View videoButton = participantsLayout.findViewById(R.id.roomActivityParticipantElementVideoButton);
        View speakerButton = participantsLayout.findViewById(R.id.roomActivityParticipantElementSpeakerButton);
        if (online) {
            participantsLayout.setBackgroundResource(R.drawable.background_participant_in_call_element);
            muteButton.setVisibility(View.VISIBLE);
            videoButton.setVisibility(View.VISIBLE);
            if (id.equals(MainUser.getMainUser().getId())) {
                ImageView callButton = participantsLayout.findViewById(R.id.roomActivityParticipantElementCallButton);
                callButton.setImageResource(R.drawable.baseline_call_end);
                isInCall = true;
                System.out.println("child added");

            } else {
                inCall.add(id);
                if (isInCall) {
                    speakerButton.setVisibility(View.VISIBLE);
                }


            }
        } else {
            participantsLayout.setBackgroundResource(R.drawable.background_participant_element);
            muteButton.setVisibility(View.GONE);
            videoButton.setVisibility(View.GONE);
            if (id.equals(MainUser.getMainUser().getId())) {
                isInCall = false;
                locallyMuted.clear();
                ImageView callButton = participantsLayout.findViewById(R.id.roomActivityParticipantElementCallButton);
                callButton.setImageResource(R.drawable.baseline_call);
            } else {
                speakerButton.setVisibility(View.GONE);
                inCall.remove(id);

            }

        }

        if (id.equals(MainUser.getMainUser().getId())) {
            for (String userId : inCall) {
                displaySpeakerButton(online, userId);
            }
        }
    }


    /**
     * mute (unmute) the user whose id is given if muted is set to true (false)
     *
     * @param muted decides to mute or unmuste the user
     * @param id    the user's id
     */
    public void setMutedUser(boolean muted, @NonNull String id) {
        ConstraintLayout participantsLayout = participantsViews.get(id);
        ImageView muteButton = participantsLayout.findViewById(R.id.roomActivityParticipantElementMuteButton);
        if (muted) {
            muteButton.setImageResource(R.drawable.baseline_mic_off);
            if (id.equals(MainUser.getMainUser().getId())) {
                isMuted = true;
            }
        } else {
            if (!locallyMuted.contains(id)) {
                muteButton.setImageResource(R.drawable.baseline_mic);
            }

            if (id.equals(MainUser.getMainUser().getId())) {
                isMuted = false;
            }
        }
    }

    /**
     * Mutes (unmutes) the given user locally if muted is set to true (false)
     *
     * @param muted
     * @param id
     */
    private void muteUserLocally(boolean muted, @NonNull String id) {
        ConstraintLayout participantsLayout = participantsViews.get(id);
        ImageView speakerButton = participantsLayout.findViewById(R.id.roomActivityParticipantElementSpeakerButton);
        call.muteUserLocally(muted, id);
        if (muted) {
            locallyMuted.add(id);
            speakerButton.setImageResource(R.drawable.outline_volume_off);
        } else {
            locallyMuted.remove(id);
            speakerButton.setImageResource(R.drawable.outline_volume_up);
        }


    }

    /**
     * Sets the UI so that the given user appears as talking if talking is set to true, is set to normal in call background otherwise
     *
     * @param talking if the user is talking or not
     * @param id      the id of the user
     */
    public void setTalkingUser(boolean talking, @NonNull String id) {
        int uid = call.getUid(id);
        if (call != null && uid != -1) {
            videoFragment.setTalking(uid, talking);
        }

        ConstraintLayout participantsLayout = participantsViews.get(id);
        if (talking) {
            participantsLayout.setBackgroundResource(R.drawable.background_participant_talking_element);
        } else if (inCall.contains(id)) {
            participantsLayout.setBackgroundResource(R.drawable.background_participant_in_call_element);
        } else {
            participantsLayout.setBackgroundResource(R.drawable.background_participant_element);
        }

    }

    /**
     * Displays the speaker button of the given user in the call if on is set to true and makes it disappear otherwise
     *
     * @param on
     * @param id
     */
    private void displaySpeakerButton(boolean on, @NonNull String id) {
        ConstraintLayout participantsLayout = participantsViews.get(id);
        View speakerButton = participantsLayout.findViewById(R.id.roomActivityParticipantElementSpeakerButton);
        speakerButton.setVisibility(on ? View.VISIBLE : View.GONE);
    }

    /**
     * Initializes the request permission requester
     */
    private void initializePermissionRequester() {
        requestPermissionLauncher =
                this.registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                    //joins the channel if granted and do nothing otherwise
                    if (isGranted) {
                        joinChannel();

                    } else {
                        System.out.println("not granted");
                    }
                });
    }

    /**
     * @param permission the permission we're checking
     * @return true if the permission given is granted by the user and false otherwise
     */
    private boolean checkPermission(String permission) {
        return ContextCompat.checkSelfPermission(getContext(), permission) == PackageManager.PERMISSION_GRANTED;
    }


    /**
     * @return the id of the appointment
     */
    public String getAppointmentId() {
        return requireArguments().getString(PARTICIPANTS_KEY);
    }


    /**
     * Initializes the inCall listener and adds it to the appointment
     */
    private void initializeAndDisplayDatabase() {
        BooleanChildListener inCallListener = new BooleanChildListener() {
            @Override
            public void childAdded(String key, boolean value) {
                setUserOnline(true, key);
                setMutedUser(value, key);
                refreshViews();
            }

            @Override
            public void childRemoved(String key, boolean value) {
                setUserOnline(false, key);
                refreshViews();
            }

            @Override
            public void childChanged(String key, boolean value) {
                setMutedUser(value, key);
            }
        };

        databaseAppointment.addInCallListener(inCallListener);
        commandsToRemoveListeners.add((x,y) -> databaseAppointment.removeInCallListener(inCallListener));
    }

    @Override
    public void onDialogChoiceSingleChoiceItems(int elementIndex) {
        assert call != null;
        call.setVoiceEffect(elementIndex);
    }

}