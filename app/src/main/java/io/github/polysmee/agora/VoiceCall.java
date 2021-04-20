package io.github.polysmee.agora;

import android.content.Context;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;
import io.agora.rtc.Constants;
import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.models.UserInfo;
import io.github.polysmee.database.DatabaseAppointment;
import io.github.polysmee.database.DatabaseUser;
import io.github.polysmee.login.AuthenticationFactory;
import io.github.polysmee.login.MainUserSingleton;
import io.github.polysmee.room.fragments.RoomActivityParticipantsFragment;

/**
 * Back-end of the voice call feature
 */
public class VoiceCall {

    public static final int SUCCESS_CODE = 0;
    public static final int ERROR_CODE = 1;

    private static final String APP_ID = "a255f3c708ab4e27a52e0d31ec25ce56";
    private static final String APP_CERTIFICATE = "1b4283ea74394f209ccadd74ac467194";
    private static final int EXPIRATION_TIME = 3600;
    private RtcEngine mRtcEngine;
    private IRtcEngineEventHandler handler;
    private final Context context;
    private ActivityResultLauncher<String> requestPermissionLauncher;
    private Map<Integer, String> usersConnected;
    private DatabaseAppointment appointment;



    //Builds a VoiceCall instance for the corresponding room
    public VoiceCall(DatabaseAppointment appointment, Context context) {
        this.context = context;
        this.appointment = appointment;
        initializeHandler();
        try {
            mRtcEngine = RtcEngine.create(context, APP_ID, handler);
            mRtcEngine.setChannelProfile(Constants.CHANNEL_PROFILE_COMMUNICATION);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }


    }

    /*public VoiceCall(@NonNull RoomActivityParticipantsFragment room) {
        this.appointmentId = room.getAppointmentId();
        this.context = room.getContext();
        this.requestPermissionLauncher = room.getRequestPermissionLauncher();
        usersConnected = new HashMap<Integer, String>();
        this.room = room;
    }*/

    /**
     * alternative constructor for tests
     *
     * @param appointment
     * @param context
     * @param requestPermissionLauncher
     * @param handler
     */
    public VoiceCall(@NonNull DatabaseAppointment appointment, @NonNull Context context, ActivityResultLauncher<String> requestPermissionLauncher, @NonNull IRtcEngineEventHandler handler) {
        this.appointment = appointment;
        this.context = context;
        this.requestPermissionLauncher = requestPermissionLauncher;
        usersConnected = new HashMap<Integer, String>();
        try {
            mRtcEngine = RtcEngine.create(context, APP_ID, handler);
            mRtcEngine.setChannelProfile(Constants.CHANNEL_PROFILE_COMMUNICATION);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Joins the channel of the room
     * @return 0 if the channel is successfully joined
     */
    public int joinChannel() {

        String userId =  MainUserSingleton.getInstance().getId();
        String token = generateToken(userId);

        int joinStatus = mRtcEngine.joinChannelWithUserAccount(token,appointment.getId(),userId);
        if(joinStatus == SUCCESS_CODE) {
            appointment.addInCallUser(new DatabaseUser(userId));
            return SUCCESS_CODE;
        }
        return ERROR_CODE;
    }

    /**
     * leaves the channel
     */
    public int leaveChannel() {
            int leaveStatus = mRtcEngine.leaveChannel();
            if(leaveStatus == SUCCESS_CODE) {
                appointment.removeOfCall(new DatabaseUser(MainUserSingleton.getInstance().getId()));
                return SUCCESS_CODE;
        }

        //fail
        return ERROR_CODE;
    }

    /**
     * mute (unmute) local user if mute arg is set to true (false)
     * @param mute
     */
    public void mute(boolean mute) {
        mRtcEngine.muteLocalAudioStream(mute);
        appointment.muteUser(new DatabaseUser(MainUserSingleton.getInstance().getId()), mute);
    }


    /**
     *
     * @param userId
     * @return a token generated using the userId and the appointmentId of the room as channel name
     */
    public String generateToken(@NonNull String userId) {
        RtcTokenBuilder token = new RtcTokenBuilder();
        int timestamp = (int)(System.currentTimeMillis() / 1000 + EXPIRATION_TIME);
        return token.buildTokenWithUserAccount(APP_ID,APP_CERTIFICATE,appointment.getId(),userId, RtcTokenBuilder.Role.Role_Publisher, timestamp);
    }

    public void destroy() {
        leaveChannel();
        mRtcEngine.removeHandler(handler);
        //mRtcEngine = null;
        //handler = null;
    }


    /**
     * initializes the IRtcEngineEventHandler
     */
    private void initializeHandler() {

        handler = new IRtcEngineEventHandler() {
            @Override
            public void onWarning(int warn) {
                System.out.println(warn);
            }

            @Override
            public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
                System.out.println("sucesss");
            }

            @Override
            public void onError(int err) {
                System.out.println("error : " + err);
            }

            @Override
            public void onUserJoined(int uid, int elapsed) {
                if(usersConnected.containsKey(uid)) {
                    appointment.addInCallUser(new DatabaseUser(usersConnected.get(uid)));
                }
                System.out.println("user joined : " + uid);
            }

            @Override
            public void onUserInfoUpdated(int uid, UserInfo userInfo) {
                System.out.println("user connected : " + userInfo.userAccount);
                usersConnected.put(uid, userInfo.userAccount);
                onUserJoined(uid, 0);

            }

            @Override
            public void onUserOffline(int uid, int elapsed) {
                System.out.println("user offline : " + usersConnected.get(uid));
                if(usersConnected.containsKey(uid)) {
                    appointment.removeOfCall(new DatabaseUser(usersConnected.get(uid)));
                }
            }

            @Override
            public void onRemoteAudioStateChanged(int uid, int state, int reason, int elapsed) {
                String userId = usersConnected.get(uid);
                switch (reason) {
                    case Constants.REMOTE_AUDIO_REASON_REMOTE_MUTED :
                        System.out.println("yakakakakkakakakakakkaa");
                        //room.muteUser(true, userId);
                        break;
                    case Constants.REMOTE_AUDIO_REASON_LOCAL_UNMUTED :
                        //room.muteUser(false, userId);
                        System.out.println("yokokokokokokokokokokokoko");
                }
            }

            @Override
            public void onConnectionLost() {
                appointment.removeOfCall(new DatabaseUser(MainUserSingleton.getInstance().getId()));
            }


        };
    }



}
