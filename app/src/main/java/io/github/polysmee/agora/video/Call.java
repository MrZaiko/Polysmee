package io.github.polysmee.agora.video;

import android.app.Activity;
import android.content.Context;
import android.view.SurfaceView;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import io.agora.rtc.Constants;
import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.models.UserInfo;
import io.agora.rtc.video.VideoCanvas;
import io.agora.rtc.video.VideoEncoderConfiguration;
import io.github.polysmee.agora.Command;
import io.github.polysmee.agora.RtcTokenBuilder;
import io.github.polysmee.agora.video.handlers.AGEventHandler;
import io.github.polysmee.agora.video.handlers.DuringCallEventHandler;
import io.github.polysmee.agora.video.handlers.VideoEngineEventHandler;
import io.github.polysmee.database.DatabaseAppointment;
import io.github.polysmee.database.DatabaseUser;
import io.github.polysmee.database.databaselisteners.StringValueListener;
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
    public static final int TIME_CODE_FREQUENCY = 30;
    public static final int INVALID_TIME_CODE_TIME = 90000;
    private int timeCodeIndicator = 0;
    private static final String APP_ID = "a255f3c708ab4e27a52e0d31ec25ce56";
    private static final String APP_CERTIFICATE = "1b4283ea74394f209ccadd74ac467194";
    private static final int EXPIRATION_TIME = 3600;
    private RtcEngine mRtcEngine;
    private IRtcEngineEventHandler handler;
    private RoomActivityParticipantsFragment room;
    private RoomActivityVideoFragment videoRoom;
    private boolean videoEnabled = false;
    private DatabaseAppointment appointment;
    private final Map<Integer, String> usersCallId;
    private final Set<Integer> usersInCall;
    private final Set<Integer> talking;
    private  Command<Boolean, String> command;
    private String token;

    public Call(String appointmentId, Context context){
        this.appointment = new DatabaseAppointment(appointmentId);
        usersCallId = new HashMap<Integer,String>();
        usersInCall = new HashSet<Integer>();
        talking = new HashSet<Integer>();
        initializeHandler();
        appointment.getTokenOnceAndThen(new DatabaseUser(MainUserSingleton.getInstance().getId()), new StringValueListener() {
            @Override
            public void onDone(String o) {
                if(!o.isEmpty()) {
                    token = o;
                }
                else {
                    token = generateToken(MainUserSingleton.getInstance().getId());
                }
            }
        });

        try {
            mRtcEngine = RtcEngine.create(context, APP_ID, handler);
            mRtcEngine.setChannelProfile(Constants.CHANNEL_PROFILE_COMMUNICATION);
            mRtcEngine.enableAudioVolumeIndication(100, 3, true);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }



    /**
     * Joins the channel of the room
     * @return 0 if the channel is successfully joined
     */
    public void joinChannel() {
        String userId = AuthenticationFactory.getAdaptedInstance().getUid();
        String token1 = generateToken(userId);
        int joinStatus = mRtcEngine.joinChannelWithUserAccount(token1, appointment.getId(), userId);
        if (joinStatus == SUCCESS_CODE) {
            appointment.addInCallUser(new DatabaseUser(userId));
        }
    }

    /**
     * Leaves the channel of the room
     * @return 0 if the channel is successfully left
     */
    public void leaveChannel() {
        mRtcEngine.leaveChannel();
        appointment.removeOfCall(new DatabaseUser(MainUserSingleton.getInstance().getId()));
    }

    /**
     * Mute (unmute) local user if mute arg is set to true (false)
     * @param mute specifies if the user should be muted or not.
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
        RtcTokenBuilder tokenBuilder = new RtcTokenBuilder();
        int timestamp = (int)(System.currentTimeMillis() / 1000 + EXPIRATION_TIME);
        return tokenBuilder.buildTokenWithUserAccount(APP_ID,APP_CERTIFICATE,appointment.getId(),userId, RtcTokenBuilder.Role.Role_Publisher, timestamp);
    }

    /**
     * Sets the command attribute to the value given
     * @param command the new value of command
     */
    public void setCommand(@NonNull Command<Boolean,String> command) {
        this.command = command;
    }

    /**
     * Initializes the IRtcEngineEventHandler
     */
    private void initializeHandler() {

        handler = new IRtcEngineEventHandler() {

            @Override
            public void onUserJoined(int uid, int elapsed) {
                usersInCall.add(uid);
            }

            @Override
            public void onUserOffline(int uid, int reason) {
                usersInCall.remove(uid);
            }

            @Override
            public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
                System.out.println("sucesss");
            }

            @Override
            public void onUserInfoUpdated(int uid, UserInfo userInfo) {
                System.out.println(uid + " => user account : " + userInfo.userAccount);
                usersCallId.put(uid, userInfo.userAccount);
            }

            @Override
            public void onAudioVolumeIndication(AudioVolumeInfo[] speakers, int totalVolume) {
                Set<Integer> newUsersInCall = new HashSet<Integer>();
                if(speakers != null) {
                    for(int i = 0; i < speakers.length; ++i) {
                        AudioVolumeInfo audioVolumeInfo = speakers[i];
                        int uid = audioVolumeInfo.uid;
                        if(audioVolumeInfo.volume > 0 && usersCallId.containsKey(uid)) {
                            String userId = usersCallId.get(uid);
                            //System.out.println(audioVolumeInfo.uid + " => user " + userId + " has volume : " + audioVolumeInfo.volume);
                            command.execute(true, userId);
                            newUsersInCall.add(uid);
                            System.out.println("talking");
                        }
                    }
                }

                for(int uid : usersInCall) {
                    if(!newUsersInCall.contains(uid)) {

                        if(talking.contains(uid)) {
                            talking.remove(uid);
                        }
                        else {
                            System.out.println("not talking");
                            command.execute(false, usersCallId.get(uid));
                        }

                    }
                }
                talking.addAll(newUsersInCall);

            }

            @Override
            public void onTokenPrivilegeWillExpire(String token) {
                onRequestToken();
            }

            @Override
            public void onRequestToken() {
                System.out.println("noooooooooooooooooooooooooooo");
                String uid = MainUserSingleton.getInstance().getId();
                token = generateToken(uid);
                appointment.setToken(new DatabaseUser(uid), token);
                mRtcEngine.renewToken(token);
            }

            @Override
            public void onLocalAudioStats(LocalAudioStats stats) {
                if(timeCodeIndicator % TIME_CODE_FREQUENCY == 0) {
                    appointment.setTimeCode(new DatabaseUser(MainUserSingleton.getInstance().getId()), System.currentTimeMillis());
                }
                timeCodeIndicator += 1;
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

    /**
     * @return whether the video is enabled or not.
     */
    public boolean isVideoEnabled(){
        return videoEnabled;
    }

    /**
     * Lets the user switch their camera if they want to.
     * @return error code: if the operation was successful or not
     */
    public int switchCamera(){
        return mRtcEngine.switchCamera();
    }

    /**
     * Creates a remote user's video UI when they join the video call
     * @param context the context in which the video view is created
     * @param uid the user's id
     * @return the surface view containing the remote video of specified remote user
     */
    public SurfaceView createRemoteUI(Context context, final int uid){
        SurfaceView surfaceV = RtcEngine.CreateRendererView(context);
        mRtcEngine.setupRemoteVideo(new VideoCanvas(surfaceV, VideoCanvas.RENDER_MODE_HIDDEN,uid));
        surfaceV.layout(0,0,20,10);
        return surfaceV;
    }

    /**
     * Create the local user's video UI
     * @param context the context in which the video view is created
     * @return the surface view containing the local video
     */
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
}
