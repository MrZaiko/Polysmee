package io.github.polysmee.agora.video.handlers;

import java.util.ArrayList;
import java.util.List;

import io.agora.rtc.IRtcEngineEventHandler;

public class VideoEngineEventHandler extends IRtcEngineEventHandler {


    private int EVENT_TYPE_ON_APP_ERROR = 13;
    public static final int NO_CONNECTION_ERROR = 3;
    private List<AGEventHandler> handlers;

    public VideoEngineEventHandler(){
        handlers = new ArrayList<>();
    }

    public boolean addEventHandler(AGEventHandler handler){
        return handlers.add(handler);
    }


    @Override
    public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
        for(AGEventHandler handler: handlers){
            if(handler instanceof DuringCallEventHandler){
                ((DuringCallEventHandler) handler).onJoinChannelSuccess(channel,uid,elapsed);
            }
        }
    }

    @Override
    public void onLeaveChannel(RtcStats rtcStats) {
        for(AGEventHandler handler: handlers){
            if(handler instanceof DuringCallEventHandler){
                ((DuringCallEventHandler) handler).onLeaveChannel(rtcStats);
            }
        }
    }

    @Override
    public void onUserOffline(int uid, int reason) {
        for(AGEventHandler handler: handlers){
            if(handler instanceof DuringCallEventHandler){
                ((DuringCallEventHandler) handler).onUserOffline(uid,reason);
            }
        }
    }

    @Override
    public void onRemoteVideoStateChanged(int uid, int state, int reason, int elapsed) {
        System.out.println("onRemoteUserStateChanged " + (uid & 0xFFFFFFFFL) + " "+ reason + " " +  elapsed);
        for(AGEventHandler handler: handlers){
            if(handler instanceof DuringCallEventHandler){
                ((DuringCallEventHandler) handler).onRemoteVideoStateChanged(uid,state,reason,elapsed);
            }
        }
    }


    @Override
    public void onLocalVideoStateChanged(int localVideoState, int error) {
        System.out.println("onLocalVideoStateChanged" + localVideoState );
        for(AGEventHandler handler : handlers){
            if(handler instanceof DuringCallEventHandler){
                ((DuringCallEventHandler) handler).onLocalVideoStateChanged(localVideoState,error);
            }
        }
    }

}
