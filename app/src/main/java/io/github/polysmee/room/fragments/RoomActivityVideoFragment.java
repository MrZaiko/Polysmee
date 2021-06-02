package io.github.polysmee.room.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

import io.agora.rtc.Constants;
import io.agora.rtc.IRtcEngineEventHandler;
import io.github.polysmee.R;
import io.github.polysmee.agora.video.Call;
import io.github.polysmee.agora.video.handlers.DuringCallEventHandler;

public class RoomActivityVideoFragment extends Fragment implements DuringCallEventHandler {

    private ViewGroup rootView;
    public final static String VIDEO_KEY = "io.github.polysme.room.fragments.roomActivityVideoFragment.VIDEO_KEY";
    private final HashMap<Integer, FrameLayout> idsToVideoFrames = new HashMap<>();
    private final HashMap<FrameLayout, Integer> videoFramesToIds = new HashMap<>();
    private FrameLayout bigVideoContainer;
    private final Call call;
    private LinearLayout videoLayout;
    private LayoutInflater inflater;
    static final Logger LOGGER = LoggerFactory.getLogger(RoomActivityVideoFragment.class);

    public RoomActivityVideoFragment(Call call) {
        this.call = call;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        rootView = (ViewGroup) inflater.inflate(R.layout.fragment_room_activity_video, container, false);
        videoLayout = rootView.findViewById(R.id.roomActivityVideosLayout);
        bigVideoContainer = rootView.findViewById(R.id.roomActivityFocusedVideoFrame);
        rootView.findViewById(R.id.roomActivitySwitchVideoButton).setOnClickListener((view) -> call.switchCamera());
        this.inflater = inflater;
        return rootView;
    }

    @Override
    public void onUserJoined(int uid) {
        System.out.println("A remote user joined the call");
    }


    @Override
    public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
        LOGGER.info("I successfully joined the call");
        runOnUiThread(this::setupLocalVideoView);
    }

    @Override
    public void onUserOffline(int uid, int reason) {
        System.out.println("A remote user quit the call");
        runOnUiThread(() -> removeVideo(uid));
    }

    @Override
    public void onRemoteVideoStateChanged(int uid, int state, int reason, int elapsed) {
        if (state == Constants.REMOTE_VIDEO_STATE_STOPPED && reason == Constants.REMOTE_VIDEO_STATE_REASON_REMOTE_MUTED) {
            runOnUiThread(() ->
                    idsToVideoFrames.get(uid).getChildAt(0).setVisibility(View.GONE));
            //((FrameLayout)rootView.findViewById(R.id.bg_video_container)).getChildAt(0).setVisibility(View.GONE));

        } else if (state == Constants.REMOTE_VIDEO_STATE_STARTING) {
            System.out.println("Remote started sharing video " + uid);
            runOnUiThread(() -> {
                SurfaceView remoteView = call.createRemoteUI(getActivity().getBaseContext(), uid);

                ConstraintLayout remoteContainer = (ConstraintLayout) inflater.inflate(R.layout.element_room_activity_video, null);
                FrameLayout remoteVideoContainer = remoteContainer.findViewById(R.id.roomActivityVideoElement);
                remoteVideoContainer.addView(remoteView);
                remoteContainer.removeView(remoteVideoContainer);

                idsToVideoFrames.put(uid, remoteVideoContainer);
                videoFramesToIds.put(remoteVideoContainer, uid);

                videoLayout.addView(remoteVideoContainer);

                remoteVideoContainer.setOnClickListener((view) -> {
                    View bigVideo = bigVideoContainer.getChildAt(0);
                    bigVideoContainer.removeAllViewsInLayout();
                    View smallVideo = remoteVideoContainer.getChildAt(0);
                    remoteVideoContainer.removeAllViewsInLayout();

                    int smallVideoOwner = videoFramesToIds.get(remoteVideoContainer);
                    int bigVideoOwner = videoFramesToIds.get(bigVideoContainer);

                    videoFramesToIds.put(bigVideoContainer, smallVideoOwner);
                    videoFramesToIds.put(remoteVideoContainer, bigVideoOwner);

                    idsToVideoFrames.put(smallVideoOwner, bigVideoContainer);
                    idsToVideoFrames.put(bigVideoOwner, remoteVideoContainer);

                    bigVideoContainer.addView(smallVideo);
                    remoteVideoContainer.addView(bigVideo);
                });

            });
        } else if (state == Constants.REMOTE_VIDEO_STATE_DECODING) {
            runOnUiThread(() ->
                    idsToVideoFrames.get(uid).getChildAt(0).setVisibility(View.VISIBLE));
            //((FrameLayout)rootView.findViewById(R.id.bg_video_container)).getChildAt(0).setVisibility(View.VISIBLE));
        }
    }

    @Override
    public void onLocalVideoStateChanged(int localVideoState, int error) {
        if (error == Constants.LOCAL_VIDEO_STREAM_ERROR_OK) {
            if (localVideoState == Constants.LOCAL_VIDEO_STREAM_STATE_STOPPED) {
                runOnUiThread(() -> {
                    rootView.findViewById(R.id.roomActivitySwitchVideoButton).setVisibility(View.GONE);
                    idsToVideoFrames.get(0).getChildAt(0).setVisibility(View.GONE);
                });
                LOGGER.info("Local video stopped");
                //((FrameLayout)rootView.findViewById(R.id.floating_video_container)).getChildAt(0).setVisibility(View.GONE));
            } else if (localVideoState == Constants.LOCAL_VIDEO_STREAM_STATE_CAPTURING) {
                runOnUiThread(() -> {
                    rootView.findViewById(R.id.roomActivitySwitchVideoButton).setVisibility(View.VISIBLE);
                    idsToVideoFrames.get(0).getChildAt(0).setVisibility(View.VISIBLE);
                });
                LOGGER.info("Local video fired");
            }
        } else {
            System.out.println("ERROR ERROR ERROR");
        }
    }

    @Override
    public void onLeaveChannel(IRtcEngineEventHandler.RtcStats stats) {
        removeVideo(-1);
        LOGGER.info("I left the channel");
    }

    public String getAppointmentId() {
        return requireArguments().getString(VIDEO_KEY);
    }

    /**
     * Remove videos frames according to this logic:
     * - If uid == -1, this means that the local user quit the call; this deletes
     * all video views and empties the frames
     * - Otherwise, it means the user with the given uid has quit the call. If their video
     * was focused on by the local user, the local user's video (whether it's running or not)
     * replaces theirs. After that, the frame containing the remote video is deleted.
     * If the video was not focused on, it's simply deleted.
     *
     * @param uid specifies which videos to delete: if -1, delete all; otherwise, delete only the specified
     *            user's video
     */
    protected void removeVideo(int uid) {
        if (uid == -1) {
            idsToVideoFrames.clear();
            videoFramesToIds.clear();
            videoLayout.removeAllViewsInLayout();
            bigVideoContainer.removeAllViewsInLayout();
            return;
        }
        FrameLayout videoToDelete = idsToVideoFrames.get(uid);
        if (videoToDelete == bigVideoContainer) {
            View bigVideo = bigVideoContainer.getChildAt(0);
            bigVideoContainer.removeAllViewsInLayout();
            FrameLayout localVideoContainer = idsToVideoFrames.get(0);
            View smallVideo = localVideoContainer.getChildAt(0);
            localVideoContainer.removeAllViewsInLayout();

            int smallVideoOwner = videoFramesToIds.get(localVideoContainer);
            int bigVideoOwner = videoFramesToIds.get(bigVideoContainer);

            videoFramesToIds.put(bigVideoContainer, smallVideoOwner);
            videoFramesToIds.put(localVideoContainer, bigVideoOwner);

            idsToVideoFrames.put(smallVideoOwner, bigVideoContainer);
            idsToVideoFrames.put(bigVideoOwner, localVideoContainer);

            bigVideoContainer.addView(smallVideo);
            localVideoContainer.addView(bigVideo);

            videoToDelete = idsToVideoFrames.get(uid);
        }

        videoLayout.removeView(videoToDelete);
        videoFramesToIds.remove(videoToDelete);
        idsToVideoFrames.remove(uid);

    }


    /**
     * Called when the local user joins the call; sets up the local video frame,
     * which is the big one.
     */
    protected void setupLocalVideoView() {
        SurfaceView localView = call.createLocalUI(getActivity().getBaseContext());
        localView.setId(R.id.roomActivityVideoElement);
        localView.setVisibility(View.GONE);

        bigVideoContainer.addView(localView);

        idsToVideoFrames.put(0, bigVideoContainer);
        videoFramesToIds.put(bigVideoContainer, 0);
    }


    /**
     * Shortcut method to run the code I need to run on UI thread faster.
     *
     * @param runnable the code I want to run, wrapped in a runnable.
     */
    protected void runOnUiThread(Runnable runnable) {
        getActivity().runOnUiThread(runnable);
    }

    public void setTalking(int id, boolean isTalking) {
        SurfaceView surfaceView = (SurfaceView) idsToVideoFrames.get(id).getChildAt(0);
        if(surfaceView != null){
        if (isTalking)
            runOnUiThread(() -> surfaceView.setBackgroundResource(R.drawable.background_participant_talking_video));
        else
            runOnUiThread(() -> surfaceView.setBackgroundResource(0));
        }
    }

}