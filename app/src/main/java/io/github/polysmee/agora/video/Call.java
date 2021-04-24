package io.github.polysmee.agora.video;

import android.app.Activity;
import android.content.Context;
import android.view.SurfaceView;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.util.HashMap;
import java.util.Map;
import io.agora.rtc.Constants;
import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.models.UserInfo;
import io.agora.rtc.video.VideoCanvas;
import io.agora.rtc.video.VideoEncoderConfiguration;
import io.github.polysmee.agora.RtcTokenBuilder;
import io.github.polysmee.agora.video.handlers.AGEventHandler;
import io.github.polysmee.agora.video.handlers.DuringCallEventHandler;
import io.github.polysmee.agora.video.handlers.VideoEngineEventHandler;
import io.github.polysmee.database.DatabaseAppointment;
import io.github.polysmee.database.DatabaseUser;
import io.github.polysmee.login.AuthenticationFactory;
import io.github.polysmee.login.MainUserSingleton;
import io.github.polysmee.room.fragments.RoomActivityParticipantsFragment;
import io.github.polysmee.room.fragments.RoomActivityVideoFragment;

/**
 * Back-end of the call feature
 */
public class Call {

    public static final int SUCCESS_CODE = 0;
    public static final int ERROR_CODE = 1;

    private static final String APP_ID = "a255f3c708ab4e27a52e0d31ec25ce56";
    private static final String APP_CERTIFICATE = "1b4283ea74394f209ccadd74ac467194";
    private static final int EXPIRATION_TIME = 3600;
    private RtcEngine mRtcEngine;
    private IRtcEngineEventHandler handler;
    private final String appointmentId;
    private final Context context;

    private RoomActivityParticipantsFragment room;
    private RoomActivityVideoFragment videoRoom;
    private boolean videoEnabled = false;
    private DatabaseAppointment appointment;



    public Call(String appointmentId, Context context){
        this.appointmentId = appointmentId;
        this.appointment = new DatabaseAppointment(appointmentId);
        initializeHandler();
        this.context = context;

        try {
            mRtcEngine = RtcEngine.create(context, APP_ID, handler);
            mRtcEngine.setChannelProfile(Constants.CHANNEL_PROFILE_COMMUNICATION);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Builds a VoiceCall instance for the corresponding room
     * @param room
     */
    public Call(@NonNull RoomActivityParticipantsFragment room) {
        this.appointmentId = room.getAppointmentId();
        this.context = room.getContext();
        this.room = room;
    }

    /**
     * alternative constructor for making tests
     *
     * @param appointmentId
     * @param context
     * @param handler
     */
    public Call(@NonNull String appointmentId, @NonNull Context context, @NonNull IRtcEngineEventHandler handler) {
        this.appointmentId = appointmentId;
        this.appointment = new DatabaseAppointment(appointmentId);
        this.context = context;
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
        String userId =  AuthenticationFactory.getAdaptedInstance().getUid();
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
    public int mute(boolean mute) {
        int result = mRtcEngine.muteLocalAudioStream(mute);
        appointment.muteUser(new DatabaseUser(MainUserSingleton.getInstance().getId()), mute);

        return result;
    }

    /**
     *
     * @param userId
     * @return a token generated using the userId and the appointmentId of the room as channel name
     */
    public String generateToken(@NonNull String userId) {
        RtcTokenBuilder token = new RtcTokenBuilder();
        int timestamp = (int)(System.currentTimeMillis() / 1000 + EXPIRATION_TIME);
        return token.buildTokenWithUserAccount(APP_ID,APP_CERTIFICATE,appointmentId,userId, RtcTokenBuilder.Role.Role_Publisher, timestamp);
    }

    /**
     * initializes the IRtcEngineEventHandler
     */
    private void initializeHandler() {

        handler = new IRtcEngineEventHandler() {
            @Override
            public void onWarning(int warn) {
                System.out.println("Warning: " + warn);
            }

            @Override
            public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
                System.out.println("Huge sucesss");
            }

            @Override
            public void onError(int err) {
                System.out.println("error : " + err);
            }

            @Override
            public void onUserJoined(int uid, int elapsed) {
                System.out.println("room null");
                System.out.println(room == null);
                System.out.println("lets go !!!");
                System.out.println("user joined : " + uid);
            }

            @Override
            public void onUserInfoUpdated(int uid, UserInfo userInfo) {
                System.out.println("user connected : " + userInfo.userAccount);
                onUserJoined(uid, 0);

            }

            @Override
            public void onUserOffline(int uid, int elapsed) {
                System.out.println("user offline : " + uid);
            }

        };
    }

    /**
     * Makes the user leave the channel and removes the handler
     */
    public void destroy() {
        leaveChannel();
        mRtcEngine.removeHandler(handler);
    }



    /**
     * Add functions for:
     * 1. Setting up the local video (part of it will be in the fragment); when a user enables his
     * local camera, setup the layout that will containt it
     * 2. When a remote user joins, add a layout with their video feed minimized
     */

    //======================== Video part ======================//

    /**
     * Lets the user decide wherever they want to share their video or not. If they don't,
     * they can still talk using audio.
     * @return error code: if the operation was successful or not
     */
    public int shareLocalVideo(){
        videoEnabled = !videoEnabled;
        return mRtcEngine.enableLocalVideo(videoEnabled);
    }

    public boolean isVideoEnabled(){
        return videoEnabled;
    }

    /**
     * Lets the user switch their camera if they want to.
     * @return error code: if the operation was successful or not
     */
    public int switchCamera(View view){
        return mRtcEngine.switchCamera();
    }


    public SurfaceView createRemoteUI(Context context, final int uid){
        SurfaceView surfaceV = RtcEngine.CreateRendererView(context);
        //surfaceV.setZOrderOnTop(true);
        //surfaceV.setZOrderMediaOverlay(true);
        mRtcEngine.setupRemoteVideo(new VideoCanvas(surfaceV, VideoCanvas.RENDER_MODE_HIDDEN,uid));
        surfaceV.layout(0,0,20,10);
        return surfaceV;
    }

    public SurfaceView createLocalUI(Context context){
        SurfaceView surfaceV = RtcEngine.CreateRendererView(context);
        surfaceV.setZOrderOnTop(true);
        surfaceV.setZOrderMediaOverlay(true);
        mRtcEngine.setupLocalVideo(new VideoCanvas(surfaceV));
        surfaceV.layout(0,0,20,10);
        return surfaceV;
    }


    //========================== Fragment setters ====================//

    public void setParticipantFragment(Fragment fragment){
        this.room = (RoomActivityParticipantsFragment) fragment;
    }

    public void setVideoFragment(Fragment fragment){
        this.videoRoom = (RoomActivityVideoFragment) fragment;

        VideoEngineEventHandler eventHandler = new VideoEngineEventHandler();
        eventHandler.addEventHandler(this.videoRoom);
        mRtcEngine.addHandler(eventHandler);

        mRtcEngine.enableVideo();
        mRtcEngine.setVideoEncoderConfiguration(new VideoEncoderConfiguration(VideoEncoderConfiguration.VD_1280x720, VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_30,
                VideoEncoderConfiguration.STANDARD_BITRATE,
                VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_FIXED_PORTRAIT));
        mRtcEngine.enableLocalVideo(false);
    }

    public Fragment getRoomActivity(){
        return room;
    }
}
