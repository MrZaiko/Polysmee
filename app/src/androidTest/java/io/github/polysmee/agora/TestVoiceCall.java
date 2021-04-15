package io.github.polysmee.agora;

import androidx.test.core.app.ApplicationProvider;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.FirebaseApp;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.concurrent.ExecutionException;

import static org.junit.Assert.*;


import io.agora.rtc.IRtcEngineEventHandler;
import io.github.polysmee.login.AuthenticationFactory;

@RunWith(JUnit4.class)
public class TestVoiceCall {

    @BeforeClass
    public static void setUp() throws ExecutionException, InterruptedException {
        AuthenticationFactory.setTest();
        FirebaseApp.clearInstancesForTest();
        FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext());
        Tasks.await(AuthenticationFactory.getAdaptedInstance().createUserWithEmailAndPassword("TestVoiceCall@gmail.com", "fakePassword"));
    }


    @Test
    public void joinChannelWorks() {
        IRtcEngineEventHandler handler = new IRtcEngineEventHandler() {};
        String channelName = "test";
        VoiceCall voiceCall = new VoiceCall(channelName, ApplicationProvider.getApplicationContext(), null, handler);
        assertEquals(0, voiceCall.joinChannel());
        voiceCall.leaveChannel();
    }

    @Test
    public void leaveChannelWorks() {
        IRtcEngineEventHandler handler = new IRtcEngineEventHandler() {};
        String channelName = "test";
        VoiceCall voiceCall = new VoiceCall(channelName, ApplicationProvider.getApplicationContext(), null, handler);
        voiceCall.joinChannel();
        assertEquals(0, voiceCall.leaveChannel());
    }


}
