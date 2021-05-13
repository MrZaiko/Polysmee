package io.github.polysmee.agora.video.handlers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.agora.rtc.IRtcEngineEventHandler;

public class VideoEngineEventHandler extends IRtcEngineEventHandler {

    private final List<AGEventHandler> handlers;

    public VideoEngineEventHandler() {
        handlers = new ArrayList<>();
    }

    /**
     * Adds the specified event handler to the list of handlers that will execute
     * code when the right callbacks are called.
     *
     * @param handler the handler we'll be adding
     * @return true iff the change was successful
     */
    public boolean addEventHandler(AGEventHandler handler) {
        return handlers.add(handler);
    }

    /**
     * @return the list of handlers
     */
    public List<AGEventHandler> getHandlers() {
        return Collections.unmodifiableList(handlers);
    }

    @Override
    public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
        for (AGEventHandler handler : handlers) {
            if (handler instanceof DuringCallEventHandler) {
                ((DuringCallEventHandler) handler).onJoinChannelSuccess(channel, uid, elapsed);
            }
        }
    }

    @Override
    public void onLeaveChannel(RtcStats rtcStats) {
        for (AGEventHandler handler : handlers) {
            if (handler instanceof DuringCallEventHandler) {
                ((DuringCallEventHandler) handler).onLeaveChannel(rtcStats);
            }
        }
    }

    @Override
    public void onUserOffline(int uid, int reason) {
        for (AGEventHandler handler : handlers) {
            if (handler instanceof DuringCallEventHandler) {
                ((DuringCallEventHandler) handler).onUserOffline(uid, reason);
            }
        }
    }

    @Override
    public void onRemoteVideoStateChanged(int uid, int state, int reason, int elapsed) {
        System.out.println("onRemoteUserStateChanged " + (uid & 0xFFFFFFFFL) + " " + reason + " " + elapsed);
        for (AGEventHandler handler : handlers) {
            if (handler instanceof DuringCallEventHandler) {
                ((DuringCallEventHandler) handler).onRemoteVideoStateChanged(uid, state, reason, elapsed);
            }
        }
    }


    @Override
    public void onLocalVideoStateChanged(int localVideoState, int error) {
        System.out.println("onLocalVideoStateChanged" + localVideoState);
        for (AGEventHandler handler : handlers) {
            if (handler instanceof DuringCallEventHandler) {
                ((DuringCallEventHandler) handler).onLocalVideoStateChanged(localVideoState, error);
            }
        }
    }

}
