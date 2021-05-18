package io.github.polysmee.zroomActivityTests;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.FirebaseApp;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.CyclicBufferAppender;
import io.github.polysmee.R;
import io.github.polysmee.calendar.googlecalendarsync.CalendarUtilities;
import io.github.polysmee.database.DatabaseAppointment;
import io.github.polysmee.database.DatabaseFactory;
import io.github.polysmee.database.databaselisteners.BooleanChildListener;
import io.github.polysmee.login.AuthenticationFactory;
import io.github.polysmee.login.MainUser;
import io.github.polysmee.room.RoomActivity;
import io.github.polysmee.room.fragments.RoomActivityVideoFragment;
import io.github.polysmee.znotification.AppointmentReminderNotification;

import static com.schibsted.spain.barista.interaction.BaristaClickInteractions.clickOn;
import static com.schibsted.spain.barista.interaction.BaristaSleepInteractions.sleep;
import static com.schibsted.spain.barista.interaction.BaristaViewPagerInteractions.swipeViewPagerForward;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class RoomActivityVideoFragmentTest {

    private static final String username1 = "Aitta fessjoY";

    private static final String appointmentTitle = "Please";
    private static final String appointmentId = "ydbiuyaroijgpm";
    private static final String appointmentCourse = "Shut the door";
    private static final long appointmentStart = 265655445;

    @BeforeClass
    public static void setUp() throws Exception {
        AppointmentReminderNotification.setIsNotificationSetterEnable(false);
        DatabaseFactory.setTest();
        AuthenticationFactory.setTest();
        CalendarUtilities.setTest(true);
        FirebaseApp.clearInstancesForTest();
        FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext());
        Tasks.await(AuthenticationFactory.getAdaptedInstance().createUserWithEmailAndPassword("RoomActivityVideoFragmentTest@gmail.com", "fakePassword"));
        DatabaseFactory.getAdaptedInstance().getReference("users").child(MainUser.getMainUser().getId()).child("name").setValue(username1);

        DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId).child("title").setValue(appointmentTitle);
        DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId).child("course").setValue(appointmentCourse);
        DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId).child("start").setValue(appointmentStart);
        DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId).child("participants").child(MainUser.getMainUser().getId()).setValue(true);

    }


    @Test
    public void localVideoCallbacksAreCalledSuccessfully() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), RoomActivity.class);
        intent.putExtra(RoomActivity.APPOINTMENT_KEY, appointmentId);

        List usersInCall = new ArrayList<String>();
        DatabaseAppointment appointment = new DatabaseAppointment(appointmentId);
        appointment.addInCallListener(new BooleanChildListener() {
            @Override
            public void childAdded(String key, boolean value) {
                usersInCall.add(key);
            }
        });

        try (ActivityScenario<RoomActivity> ignored = ActivityScenario.launch(intent)) {

            Logger videoFragmentLogger = (Logger) LoggerFactory.getLogger(RoomActivityVideoFragment.class);
            CyclicBufferAppender<ILoggingEvent> cyclicBufferAppender = new CyclicBufferAppender<>();
            cyclicBufferAppender.start();
            videoFragmentLogger.addAppender(cyclicBufferAppender);
            List<String> logBackMessages = new ArrayList<>();


            swipeViewPagerForward();
            sleep(1, TimeUnit.SECONDS);
            swipeViewPagerForward();
            sleep(2, TimeUnit.SECONDS);
            clickOn(R.id.roomActivityParticipantElementCallButton);
            sleep(2, SECONDS);
            clickOn(R.id.roomActivityParticipantElementVideoButton);
            sleep(2, SECONDS);

            //clickOn(R.id.roomActivityParticipantElementVideoButton);
            //sleep(2, SECONDS);
            clickOn(R.id.roomActivityParticipantElementCallButton);
            sleep(2, SECONDS);
            for (int i = 0; i < cyclicBufferAppender.getLength(); ++i) {
                logBackMessages.add(cyclicBufferAppender.get(i).getMessage());
            }

            assertTrue(logBackMessages.contains("I successfully joined the call"));
            assertTrue(logBackMessages.contains("I left the channel"));
        }
    }
}
