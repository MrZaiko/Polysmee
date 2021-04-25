package io.github.polysmee.agora;

import android.content.Context;
import androidx.annotation.NonNull;
import io.agora.rtc.Constants;
import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import io.github.polysmee.database.DatabaseAppointment;
import io.github.polysmee.database.DatabaseUser;
import io.github.polysmee.login.MainUserSingleton;

/**
 * Back-end of the voice call feature
 */
public class VoiceCall {

   /* public static final int SUCCESS_CODE = 0;
    public static final int ERROR_CODE = 1;

    private static final String APP_ID = "a255f3c708ab4e27a52e0d31ec25ce56";
    private static final String APP_CERTIFICATE = "1b4283ea74394f209ccadd74ac467194";
    private static final int EXPIRATION_TIME = 3600;
    private RtcEngine mRtcEngine;
    private IRtcEngineEventHandler handler;
    private DatabaseAppointment appointment;



    //Builds a VoiceCall instance for the corresponding room
    public VoiceCall(DatabaseAppointment appointment, Context context) {
        this.appointment = appointment;
        initializeHandler();
        try {
            mRtcEngine = RtcEngine.create(context, APP_ID, handler);
            mRtcEngine.setChannelProfile(Constants.CHANNEL_PROFILE_COMMUNICATION);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }


    }*/

    /**
     * alternative constructor for tests
     *
     * @param appointment
     * @param context
     * @param handler
     */
    /*public VoiceCall(@NonNull DatabaseAppointment appointment, @NonNull Context context, @NonNull IRtcEngineEventHandler handler) {
        this.appointment = appointment;
        try {
            mRtcEngine = RtcEngine.create(context, APP_ID, handler);
            mRtcEngine.setChannelProfile(Constants.CHANNEL_PROFILE_COMMUNICATION);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }*/

    /**
     * Joins the channel of the room
     * @return 0 if the channel is successfully joined
     */
    /*public int joinChannel() {

        String userId =  MainUserSingleton.getInstance().getId();
        String token = generateToken(userId);

        int joinStatus = mRtcEngine.joinChannelWithUserAccount(token,appointment.getId(),userId);
        if(joinStatus == SUCCESS_CODE) {
            appointment.addInCallUser(new DatabaseUser(userId));
            return SUCCESS_CODE;
        }
        return ERROR_CODE;
    }*/

    /**
     * leaves the channel
     */
/*    public int leaveChannel() {
            int leaveStatus = mRtcEngine.leaveChannel();
            if(leaveStatus == SUCCESS_CODE) {
                appointment.removeOfCall(new DatabaseUser(MainUserSingleton.getInstance().getId()));
                return SUCCESS_CODE;
        }

        //fail
        return ERROR_CODE;
    }*/

    /**
     * mute (unmute) local user if mute arg is set to true (false)
     * @param mute
     * @return the result of the mute method of RtcEngine (0 if success, not 0 otherwise)
     */
    /*public int mute(boolean mute) {
        int result = mRtcEngine.muteLocalAudioStream(mute);
        appointment.muteUser(new DatabaseUser(MainUserSingleton.getInstance().getId()), mute);

        return result;
    }*/


    /**
     *
     * @param userId
     * @return a token generated using the userId and the appointmentId of the room as channel name
     */
    /*public String generateToken(@NonNull String userId) {
        RtcTokenBuilder token = new RtcTokenBuilder();
        int timestamp = (int)(System.currentTimeMillis() / 1000 + EXPIRATION_TIME);
        return token.buildTokenWithUserAccount(APP_ID,APP_CERTIFICATE,appointment.getId(),userId, RtcTokenBuilder.Role.Role_Publisher, timestamp);
    }*/

    /**
     * Makes the user leave the channel and removes the handler
     */
    /*public void destroy() {
        leaveChannel();
        mRtcEngine.removeHandler(handler);

    }*/


    /**
     * initializes the IRtcEngineEventHandler
     */
/*    private void initializeHandler() {

        handler = new IRtcEngineEventHandler() {

            @Override
            public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
                System.out.println("sucesss");
            }
        };
    }*/



}
