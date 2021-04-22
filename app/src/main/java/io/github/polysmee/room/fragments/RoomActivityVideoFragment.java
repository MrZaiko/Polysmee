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
import io.agora.rtc.IRtcEngineEventHandler;
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
        runOnUiThread(this::setupLocalVideoView);
    }

    @Override
    public void onUserOffline(int uid, int reason){
        System.out.println("A remote user quit the call");
        runOnUiThread(()->removeVideo(R.id.bg_video_container));
    }

    @Override
    public void onExtraCallback(int type, Object... data) {

    }

    @Override
    public void onRemoteVideoStateChanged(int uid, int state, int reason, int elapsed) {
        if(state == Constants.REMOTE_VIDEO_STATE_STOPPED && reason == Constants.REMOTE_VIDEO_STATE_REASON_REMOTE_MUTED){
            runOnUiThread(()->
            ((FrameLayout)rootView.findViewById(R.id.bg_video_container)).getChildAt(0).setVisibility(View.GONE));

        }
        else if(state == Constants.REMOTE_VIDEO_STATE_STARTING){
            System.out.println("Remote started sharing video");
            runOnUiThread(()->{
            SurfaceView remoteView = call.createRemoteUI(getActivity().getBaseContext(),uid);
            ((FrameLayout)rootView.findViewById(R.id.bg_video_container)).addView(remoteView);
            });
        }
        else if(state == Constants.REMOTE_VIDEO_STATE_DECODING){
            runOnUiThread(()->
            ((FrameLayout)rootView.findViewById(R.id.bg_video_container)).getChildAt(0).setVisibility(View.VISIBLE));
        }
    }

    @Override
    public void onLocalVideoStateChanged(int localVideoState, int error) {
        if(error == Constants.LOCAL_VIDEO_STREAM_ERROR_OK){
            if(localVideoState == Constants.LOCAL_VIDEO_STREAM_STATE_STOPPED){
                runOnUiThread(()->
                ((FrameLayout)rootView.findViewById(R.id.floating_video_container)).getChildAt(0).setVisibility(View.GONE));
            }
            else if(localVideoState == Constants.LOCAL_VIDEO_STREAM_STATE_CAPTURING){
                System.out.println("CAPTURING WHOOOOOOOOOOOOOO");
                runOnUiThread(() ->
                        ((FrameLayout)rootView.findViewById(R.id.floating_video_container)).getChildAt(0).setVisibility(View.VISIBLE)
                );
            }
        }
        else{
            System.out.println("ERROR ERROR ERROR");
        }
    }

    @Override
    public void onLeaveChannel(IRtcEngineEventHandler.RtcStats stats) {
        removeVideo(R.id.floating_video_container);
        removeVideo(R.id.bg_video_container);
    }

    public String getAppointmentId(){
        return requireArguments().getString(VIDEO_KEY);
    }


    private void removeVideo(int containerID) {
        FrameLayout videoContainer = rootView.findViewById(containerID);
        videoContainer.removeAllViews();
        videoContainer.removeAllViewsInLayout();
    }

    private void setupLocalVideoView(){
        SurfaceView localView = call.createLocalUI(getActivity().getBaseContext());
        localView.setVisibility(View.GONE);
        ((FrameLayout)rootView.findViewById(R.id.floating_video_container)).addView(localView);
    }


    private void runOnUiThread(Runnable runnable){
        getActivity().runOnUiThread(runnable);
    }
}