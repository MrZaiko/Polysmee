package io.github.polysmee.agora.video;


import android.content.Context;

import androidx.annotation.NonNull;

import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import io.github.polysmee.agora.RtcTokenBuilder;
import io.github.polysmee.login.AuthenticationFactory;

public class VideoCallHelpers {

    public int enableVideoModule(RtcEngine rtcEngine){
        return rtcEngine.enableVideo();
    }

    //================ OPERATIONS =============


    public int switchCamera(RtcEngine rtcEngine){
        return rtcEngine.switchCamera();
    }

    /**
     * Add functions for:
     * 1. Setting up the local video (part of it will be in the fragment)
     * 2.
     */


}
