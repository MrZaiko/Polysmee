package io.github.polysmee.agora.video.handlers;

import io.agora.rtc.IRtcEngineEventHandler;

public interface DuringCallEventHandler extends AGEventHandler {


    void onUserJoined(int uid);

    void onJoinChannelSuccess(String channel, int uid, int elapsed);

    void onUserOffline(int uid, int reason);

    void onExtraCallback(int type, Object... data);

    void onRemoteVideoStateChanged(int uid, int state, int reason, int elapsed);

    void onLocalVideoStateChanged(int localVideoState,int error);

    void onLeaveChannel(IRtcEngineEventHandler.RtcStats stats);
}
