package io.github.polysmee.agora.video.handlers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import io.agora.rtc.Constants;
import io.agora.rtc.IRtcEngineEventHandler;
import io.github.polysmee.agora.video.VideoConstantsApp;

public class VideoEngineEventHandler extends IRtcEngineEventHandler {

    private Map<Integer, String> usersConnected;
    private Map<Integer, String> usersConnectedToVideo;
    private List<AGEventHandler> handlers;

    public VideoEngineEventHandler(Map<Integer, String> usersConnected, Map<Integer, String> usersConnectedToVideo){
        this.usersConnected = usersConnected;
        this.usersConnectedToVideo = usersConnectedToVideo;
        handlers = new ArrayList<>();
    }

    public boolean addEventHandler(AGEventHandler handler){
        return handlers.add(handler);
    }

    public boolean removeEventHandler(AGEventHandler handler){
        return handlers.remove(handler);
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
    public void onRemoteVideoStateChanged(int uid, int state, int reason, int elapsed) {
        System.out.println("onRemoteUserStateChanged " + (uid & 0xFFFFFFFFL) + " "+ reason + " " +  elapsed);
        for(AGEventHandler handler: handlers){
            if(handler instanceof DuringCallEventHandler){
                ((DuringCallEventHandler) handler).onRemoteVideoStateChanged(uid,state,reason,elapsed);
            }
        }
    }

    @Override
    public void onFirstLocalVideoFrame(int width, int height, int elapsed) {
        System.out.println("onFirstLocalVideoFrame " + width + " " + height + " " + elapsed);
    }

    @Override
    public void onError(int i) {
        System.out.println("Video error");
    }

    @Override
    public void onConnectionLost() {
        System.out.println("onConnectionLost");
        for(AGEventHandler handler: handlers) {
            if (handler instanceof DuringCallEventHandler) {
                ((DuringCallEventHandler) handler).onExtraCallback(AGEventHandler.EVENT_TYPE_ON_APP_ERROR, VideoConstantsApp.AppError.NO_CONNECTION_ERROR);
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
