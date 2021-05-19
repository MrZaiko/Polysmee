package io.github.polysmee.yroomActivityTests;

import androidx.test.core.app.ApplicationProvider;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.FirebaseApp;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import io.github.polysmee.database.DatabaseAppointment;
import io.github.polysmee.database.DatabaseFactory;
import io.github.polysmee.database.Message;
import io.github.polysmee.database.databaselisteners.StringSetValueListener;
import io.github.polysmee.internet.connection.InternetConnection;
import io.github.polysmee.login.AuthenticationFactory;
import io.github.polysmee.login.MainUser;
import io.github.polysmee.znotification.AppointmentReminderNotification;

import static com.schibsted.spain.barista.interaction.BaristaSleepInteractions.sleep;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(JUnit4.class)
public class TestMessages {

    final String NULL_ARG = "null argument";
    private static final String username1 = "Mathis L'utilisateur";
    private static final String id2 = "bxcwviusergpoza";
    private static final String username2 = "Sami L'imposteur";

    private static final String appointmentTitle = "It's a title";
    private static final String appointmentId = "cwxbihezroijgdf";
    private static final String appointmentCourse = "Totally not SWENG";
    private static final long appointmentStart = 265655445;


    @BeforeClass
    public static void setUp() throws Exception {
        AppointmentReminderNotification.setIsNotificationSetterEnable(false);
        DatabaseFactory.setTest();
        AuthenticationFactory.setTest();
        FirebaseApp.clearInstancesForTest();
        FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext());
        Tasks.await(AuthenticationFactory.getAdaptedInstance().createUserWithEmailAndPassword("MessagesTest@gmail.com", "fakePassword"));
        DatabaseFactory.getAdaptedInstance().getReference("users").child(MainUser.getMainUser().getId()).child("name").setValue(username1);
        DatabaseFactory.getAdaptedInstance().getReference("users").child(id2).child("name").setValue(username2);
        DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId).child("owner").setValue(MainUser.getMainUser().getId());
        DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId).child("title").setValue(appointmentTitle);
        DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId).child("course").setValue(appointmentCourse);
        DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId).child("start").setValue(appointmentStart);
        DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId).child("participants").child(MainUser.getMainUser().getId()).setValue(true);
        DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId).child("participants").child(id2).setValue(true);
    }

    @Test
    public void destroyAppointmentWork() {
        DatabaseAppointment databaseAppointment = new DatabaseAppointment(appointmentId);
        Set<String> participants = new HashSet<String>();
        databaseAppointment.selfDestroy();
        sleep(3, TimeUnit.SECONDS);
        databaseAppointment.getParticipantsIdAndThen(p  -> participants.addAll(p));
        sleep(3, TimeUnit.SECONDS);
        assertEquals(true, participants.isEmpty());
    }

    @Test
    public void gettersReturnTheRightValue() {
        String content = "content";
        String userId = "userId";
        long messageTime = 0L;

        Message message = new Message(userId, content, 0l, false);

        assertEquals(content, message.getContent());
        assertEquals(userId, message.getSender());
        assertEquals(messageTime, message.getMessageTime());
        assertEquals(true,InternetConnection.isOn());
    }

    @Test
    public void equalsMethodReturnsFalseWithOtherTypesOfObject() {
        Message message = new Message("userID", "content", 0, false);
        assertFalse(message.equals("test"));
        assertFalse(message.equals(new Integer(10)));
        assertFalse(message.equals(new ArrayList<>()));
    }

    @Test
    public void equalsMethodReturnsFalseWithNullArg() {
        Message message = new Message("userID", "content", 0, false);
        assertFalse(message.equals(null));
    }

    @Test
    public void equalsMethodReturnsFalseWhenOneAttributDiffers() {
        Message message = new Message("userID", "content", 0, false);
        Message message1 = new Message("userID1", "content", 0, false);
        Message message2 = new Message("userID", "content1", 0, false);
        Message message3 = new Message("userID", "content", 1, false);

        assertFalse(message.equals(message1));
        assertFalse(message.equals(message2));
        assertFalse(message.equals(message3));
    }


    @Test
    public void equalsReturnsTrueWhenSameValues() {
        Message message1 = new Message("userID", "content", 0, false);
        Message message2 = new Message("userID", "content", 0, false);
        assertTrue(message1.equals(message2));
    }

}
