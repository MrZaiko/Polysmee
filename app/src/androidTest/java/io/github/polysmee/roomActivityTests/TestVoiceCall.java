package io.github.polysmee.roomActivityTests;

import androidx.activity.result.ActivityResultLauncher;
import androidx.test.core.app.ApplicationProvider;

import com.google.firebase.FirebaseApp;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;

import static org.junit.Assert.*;


import io.agora.rtc.IRtcEngineEventHandler;
import io.github.polysmee.agora.VoiceCall;
import io.github.polysmee.login.AuthenticationFactory;
import io.github.polysmee.messages.Message;

@RunWith(JUnit4.class)
public class TestVoiceCall {

    @BeforeClass
    public static void setUp() throws Exception {
        AuthenticationFactory.setTest();
        FirebaseApp.clearInstancesForTest();
        FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext());
    }

    @Test
    public void test() {
        assertEquals(1,1);
    }

    @Test
    public void joinChannelWorks() {
        IRtcEngineEventHandler handler = new IRtcEngineEventHandler() {};
        String channelName = "test";
        VoiceCall voiceCall = new VoiceCall(channelName, ApplicationProvider.getApplicationContext(), null, handler);
        assertEquals(0, voiceCall.joinChannel());
    }

}