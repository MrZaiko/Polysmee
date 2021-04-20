package io.github.polysmee.room.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.util.HashMap;

import io.agora.rtc.Constants;
import io.github.polysmee.R;
import io.github.polysmee.agora.video.Call;
import io.github.polysmee.agora.video.handlers.DuringCallEventHandler;

public class RoomActivityVideoFragment extends Fragment implements DuringCallEventHandler {

    private ViewGroup rootView;
    public final static String VIDEO_KEY = "io.github.polysme.room.fragments.roomActivityVideoFragment.VIDEO_KEY";
    private final HashMap<Integer, SurfaceView> mUidsList = new HashMap<>();
    private final HashMap<Integer, String> uidsToNames = new HashMap<>();
    private Call call;

    public RoomActivityVideoFragment() {
        // Required empty public constructor
    }

    public RoomActivityVideoFragment(Call call){
        this.call = call;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        rootView = (ViewGroup) inflater.inflate(R.layout.fragment_room_activity_video, container, false);
        return rootView;
    }

    @Override
    public void onUserJoined(int uid) {
        System.out.println("A remote user joined the call");
    }


    @Override
    public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
        System.out.println("I successfully joined the call");
    }

    @Override
    public void onUserOffline(int uid, int reason){
        System.out.println("A remote user quit the call");
        ((FrameLayout)rootView.findViewById(R.id.bg_video_container)).removeAllViewsInLayout();
    }

    @Override
    public void onExtraCallback(int type, Object... data) {

    }

    @Override
    public void onRemoteVideoStateChanged(int uid, int state, int reason, int elapsed) {
        if(state == Constants.REMOTE_VIDEO_STATE_STOPPED){
            ((FrameLayout)rootView.findViewById(R.id.bg_video_container)).removeAllViewsInLayout();
        }
        else if(state == Constants.REMOTE_VIDEO_STATE_STARTING || state == Constants.REMOTE_VIDEO_STATE_DECODING){
            SurfaceView remoteView = call.createRemoteUI(getContext(),uid);
            ((FrameLayout)rootView.findViewById(R.id.bg_video_container)).addView(remoteView);
        }
    }

    @Override
    public void onLocalVideoStateChanged(int localVideoState, int error) {
        if(error == Constants.LOCAL_VIDEO_STREAM_ERROR_OK){
            if(localVideoState == Constants.LOCAL_VIDEO_STREAM_STATE_STOPPED){
                //delete surfaceView
                ((FrameLayout)rootView.findViewById(R.id.floating_video_container)).removeAllViewsInLayout();
            }
            else if(localVideoState == Constants.LOCAL_VIDEO_STREAM_STATE_CAPTURING){
                //fill the surfaceView
                SurfaceView localView = call.createLocalUI(getContext());
                ((FrameLayout)rootView.findViewById(R.id.floating_video_container)).addView(localView);
            }
        }
    }

    public String getAppointmentId(){
        return requireArguments().getString(VIDEO_KEY);
    }

}