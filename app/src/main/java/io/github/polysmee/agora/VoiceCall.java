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
import io.github.polysmee.login.AuthenticationFactory;
import io.github.polysmee.room.fragments.RoomActivityParticipantsFragment;

public class VoiceCall {

    private static final String APP_ID = "a255f3c708ab4e27a52e0d31ec25ce56";
    private static final String APP_CERTIFICATE = "1b4283ea74394f209ccadd74ac467194";
    private static final int EXPIRATION_TIME = 3600;
    private RtcEngine mRtcEngine;
    private IRtcEngineEventHandler handler;
    private final String appointmentId;
    private final Context context;
    private ActivityResultLauncher<String> requestPermissionLauncher;
    private Map<Integer, String> usersConnected;
    private RoomActivityParticipantsFragment room;


    public VoiceCall(@NonNull RoomActivityParticipantsFragment room) {//@NonNull String appointmentId,@NonNull Context context, @NonNull ActivityResultLauncher<String> requestPermissionLauncher) {
        this.appointmentId = room.getAppointmentId();
        this.context = room.getContext();
        this.requestPermissionLauncher = room.getRequestPermissionLauncher();
        usersConnected = new HashMap<Integer, String>();
        this.room = room;
    }

    /**
     *
     * @param appointmentId
     * @param context
     * @param requestPermissionLauncher
     * @param handler
     * constructor with given handler for testing
     */
    public VoiceCall(@NonNull String appointmentId, @NonNull Context context, ActivityResultLauncher<String> requestPermissionLauncher, @NonNull IRtcEngineEventHandler handler) {
        this.appointmentId = appointmentId;
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

    public int joinChannel() {

        if (mRtcEngine == null) {
            initializeHandler();

            try {
                mRtcEngine = RtcEngine.create(context, APP_ID, handler);
                mRtcEngine.setChannelProfile(Constants.CHANNEL_PROFILE_COMMUNICATION);
            } catch (Exception e) {
                System.out.println(e.getMessage());
                throw new RuntimeException(e.getMessage());
            }

            System.out.println("nooooooooooooooooooooooooo");



            /*
              Enables the onAudioVolumeIndication callback at a set time interval to report on which
              users are speaking and the speakers' volume.
              Once this method is enabled, the SDK returns the volume indication in the
              onAudioVolumeIndication callback at the set time interval, regardless of whether any user
              is speaking in t0323
              he channel.
            */
            //mRtcEngine.enableAudioVolumeIndication(200, 3, false); // 200 ms

        }

        String userId =  AuthenticationFactory.getAdaptedInstance().getUid();
        String token = generateToken(userId);
        int joined = mRtcEngine.joinChannelWithUserAccount(token,appointmentId,userId);
        if(joined == 0) {
        }
        return joined;
    }

    public void leaveChannel() {
        if(mRtcEngine != null) {
            mRtcEngine.leaveChannel();
            usersConnected.remove(AuthenticationFactory.getAdaptedInstance().getUid());
        }
    }

    public void mute(boolean mute) {
        mRtcEngine.muteLocalAudioStream(mute);
    }


    private String generateToken(@NonNull String userId) {
        RtcTokenBuilder token = new RtcTokenBuilder();
        int timestamp = (int)(System.currentTimeMillis() / 1000 + EXPIRATION_TIME);
        return token.buildTokenWithUserAccount(APP_ID,APP_CERTIFICATE,appointmentId,userId, RtcTokenBuilder.Role.Role_Publisher, timestamp);
    }

    private void initializeHandler() {

        handler = new IRtcEngineEventHandler() {
            @Override
            public void onWarning(int warn) {
                System.out.println(warn);
            }

            @Override
            public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
                System.out.println("sucessssssssssssssssssssssssssssssssss");
            }

            @Override
            public void onError(int err) {
                System.out.println("error : " + err);
            }

            @Override
            public void onUserJoined(int uid, int elapsed) {
                System.out.println("room null");
                System.out.println(room == null);
                if(usersConnected.containsKey(uid) && room != null) {
                    System.out.println("lets go !!!");
                    room.setUserOnline(true, usersConnected.get(uid));
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
                if(room != null && usersConnected.containsKey(uid)) {
                    room.setUserOnline(false,usersConnected.get(uid));
                }
            }


        };
    }


}
