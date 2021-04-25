package io.github.polysmee.videoClassesTets;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import java.util.Random;
import io.agora.rtc.IRtcEngineEventHandler;
import io.github.polysmee.agora.video.handlers.DuringCallEventHandler;
import io.github.polysmee.agora.video.handlers.VideoEngineEventHandler;

@RunWith(JUnit4.class)
public class VideoEngineEventHandlerTest {
    private VideoEngineEventHandler engineEventHandler;
    private DuringCallEventHandler handler = new DuringCallEventHandler() {
        @Override
        public void onUserJoined(int uid) {
            testingVariable += uid;
        }

        @Override
        public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
            testingVariable += uid;
        }

        @Override
        public void onUserOffline(int uid, int reason) {
            testingVariable += uid;
        }

        @Override
        public void onRemoteVideoStateChanged(int uid, int state, int reason, int elapsed) {
            testingVariable += uid;
        }

        @Override
        public void onLocalVideoStateChanged(int localVideoState, int error) {
            testingVariable += localVideoState;
        }

        @Override
        public void onLeaveChannel(IRtcEngineEventHandler.RtcStats stats) {
            testingVariable += -1;
        }
    };;
    private int testingVariable = 0;

    @Before
    public void initTestingVariable(){
        engineEventHandler = new VideoEngineEventHandler();
        engineEventHandler.addEventHandler(handler);
        testingVariable = 0;
    }
    
    @Test
    public void onLocalVideoStateChangedLaunchesTheOtherHandlerMethod(){
        Random rand = new Random();
        int randomState = rand.nextInt();
        engineEventHandler.onLocalVideoStateChanged(randomState,-1);
        assertEquals(randomState,testingVariable);
    }

    @Test
    public void onRemoteVideoStateChangedLaunchesTheOtherHandlersMethod(){
        Random rand = new Random();
        int randomUid = rand.nextInt();
        engineEventHandler.onRemoteVideoStateChanged(randomUid,1,2,3);
        assertEquals(randomUid,testingVariable);
    }

    @Test
    public void addHandlerAddsTheGivenHandlerCorrectly(){
        VideoEngineEventHandler engineEventHandler1 = new VideoEngineEventHandler();
        engineEventHandler1.addEventHandler(handler);
        assertEquals(1,engineEventHandler1.getHandlers().size());
    }

    @Test
    public void onJoinChannelSuccessLaunchesTheOtherHandlersMethod(){
        Random rand = new Random();
        int randomUid = rand.nextInt();
        engineEventHandler.onJoinChannelSuccess("Bonjour",randomUid,10);
        assertEquals(randomUid,testingVariable);
    }

    @Test
    public void onLeaveChannelLaunchesTheOtherHandlersMethod(){
        engineEventHandler.onLeaveChannel(null);
        assertEquals(-1,testingVariable);
    }

    @Test
    public void onUserOfflineLaunchesTheOtherHandlersMethod(){
        Random rand = new Random();
        int randomUid = rand.nextInt();
        engineEventHandler.onUserOffline(randomUid,0);
        assertEquals(randomUid,testingVariable);
    }

}
