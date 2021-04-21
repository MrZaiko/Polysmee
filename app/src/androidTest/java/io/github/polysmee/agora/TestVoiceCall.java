package io.github.polysmee.agora;

import androidx.test.core.app.ApplicationProvider;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.FirebaseApp;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.schibsted.spain.barista.interaction.BaristaSleepInteractions.sleep;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.*;


import io.agora.rtc.IRtcEngineEventHandler;
import io.github.polysmee.database.DatabaseAppointment;
import io.github.polysmee.database.DatabaseFactory;
import io.github.polysmee.database.databaselisteners.BooleanChildListener;
import io.github.polysmee.login.AuthenticationFactory;
import io.github.polysmee.login.MainUserSingleton;

@RunWith(JUnit4.class)
public class TestVoiceCall {

    private String appointmentId = "test";

    @BeforeClass
    public static void setUp() throws ExecutionException, InterruptedException {
        DatabaseFactory.setTest();
        AuthenticationFactory.setTest();
        FirebaseApp.clearInstancesForTest();
        FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext());
        Tasks.await(AuthenticationFactory.getAdaptedInstance().createUserWithEmailAndPassword("TestVoiceCall@gmail.com", "fakePassword"));

    }


    @Test
    public void joinChannelWorks() {
        IRtcEngineEventHandler handler = new IRtcEngineEventHandler() {};
        List usersInCall = new ArrayList<String>();
        DatabaseAppointment appointment = new DatabaseAppointment(appointmentId);
        appointment.addInCallListener(new BooleanChildListener() {
            @Override
            public void childAdded(String key, boolean value) {
                usersInCall.add(key);
            }
        });
        VoiceCall voiceCall = new VoiceCall(appointment, ApplicationProvider.getApplicationContext(), handler);
        assertEquals(VoiceCall.SUCCESS_CODE, voiceCall.joinChannel());
        sleep(1, SECONDS);
        assert(!usersInCall.isEmpty());
        assertEquals(MainUserSingleton.getInstance().getId(),usersInCall.get(0));

        voiceCall.leaveChannel();
    }

    @Test
    public void leaveChannelWorks() {
        IRtcEngineEventHandler handler = new IRtcEngineEventHandler() {};
        DatabaseAppointment appointment = new DatabaseAppointment("test");
        List usersLeft = new ArrayList<String>();
        appointment.addInCallListener(new BooleanChildListener() {
            @Override
            public void childRemoved(String key, boolean value) {
                usersLeft.add(key);
            }
        });
        VoiceCall voiceCall = new VoiceCall(appointment, ApplicationProvider.getApplicationContext(), handler);
        voiceCall.joinChannel();
        assertEquals(VoiceCall.SUCCESS_CODE, voiceCall.leaveChannel());
        sleep(1, SECONDS);
        assert(!usersLeft.isEmpty());
        assertEquals(MainUserSingleton.getInstance().getId(),usersLeft.get(0));
    }

    @Test
    public void muteWorks() {
        IRtcEngineEventHandler handler = new IRtcEngineEventHandler() {};
        DatabaseAppointment appointment = new DatabaseAppointment("test");
        List usersMuted = new ArrayList<String>();
        appointment.addInCallListener(new BooleanChildListener() {
            @Override
            public void childChanged(String key, boolean value) {
                if(value) {
                    usersMuted.add(key);
                }

            }
        });
        VoiceCall voiceCall = new VoiceCall(appointment, ApplicationProvider.getApplicationContext(), handler);
        voiceCall.joinChannel();
        assertEquals(VoiceCall.SUCCESS_CODE, voiceCall.mute(true));
        sleep(1, SECONDS);
        assert(!usersMuted.isEmpty());
        assertEquals(MainUserSingleton.getInstance().getId(),usersMuted.get(0));
    }


}
