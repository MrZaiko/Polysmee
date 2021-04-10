package io.github.polysmee.agora;

import android.Manifest;
import android.content.Context;
import androidx.activity.result.ActivityResultLauncher;
import io.agora.rtc.Constants;
import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.models.UserInfo;
import io.github.polysmee.login.MainUserSingleton;

public class VoiceCall {

    private static final String APP_ID = "a255f3c708ab4e27a52e0d31ec25ce56";
    private static final String APP_CERTIFICATE = "1b4283ea74394f209ccadd74ac467194";
    private static final int EXPIRATION_TIME = 3600;
    private RtcEngine mRtcEngine;
    private IRtcEngineEventHandler handler;
    private final String appointmentId;
    private final Context context;
    private ActivityResultLauncher<String> requestPermissionLauncher;


    public VoiceCall(String appointmentId, Context context, ActivityResultLauncher<String> requestPermissionLauncher) {
        this.appointmentId = appointmentId;
        this.context = context;
        this.requestPermissionLauncher = requestPermissionLauncher;
    }

    public void joinChannel() {
        if (mRtcEngine == null) {
            initializeHandler();
            if(true){//ContextCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO);
                //return;
            }

            if (true){//ContextCompat.checkSelfPermission(this.getContext(), Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                try {
                    mRtcEngine = RtcEngine.create(context, APP_ID, handler);
                    mRtcEngine.setChannelProfile(Constants.CHANNEL_PROFILE_COMMUNICATION);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    throw new RuntimeException(e.getMessage());
                }
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

        String userId =  MainUserSingleton.getInstance().getId();
        String token = generateToken(userId);
        mRtcEngine.joinChannelWithUserAccount(token,appointmentId,userId);
    }


    private String generateToken(String userId) {
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
                System.out.println("user joined : " + uid);
            }

            @Override
            public void onUserInfoUpdated(int uid, UserInfo userInfo) {
                System.out.println("user joined : " + userInfo.userAccount);
            }

        };
    }


}
