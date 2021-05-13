package io.github.polysmee.login;

import androidx.test.core.app.ApplicationProvider;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.FirebaseApp;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.concurrent.ExecutionException;

import io.github.polysmee.database.DatabaseFactory;
import io.github.polysmee.znotification.AppointmentReminderNotification;

import static org.junit.Assert.*;

//@RunWith(AndroidJUnit4.class)
public class MainUserTest {

    @BeforeClass
    public static void setUp() throws Exception {
        AppointmentReminderNotification.setIsNotificationSetterEnable(false);
        DatabaseFactory.setTest();
        AuthenticationFactory.setTest();
        FirebaseApp.clearInstancesForTest();
        FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext());
        Tasks.await(AuthenticationFactory.getAdaptedInstance().createUserWithEmailAndPassword("mainusersingleton@gmail.com", "fakePassword"));
    }

    @AfterClass
    public static void clean() {
        DatabaseFactory.getAdaptedInstance().getReference().setValue(null);
    }

    @Test(expected = NullPointerException.class)
    public void getInstanceThrows() {
        AuthenticationFactory.getAdaptedInstance().signOut();
        MainUser.getMainUser();
    }

    @Test
    public void getInstanceWorks() throws ExecutionException, InterruptedException {
        Tasks.await(AuthenticationFactory.getAdaptedInstance().signInWithEmailAndPassword("mainusersingleton@gmail.com", "fakePassword"));
        assertEquals(MainUser.getMainUser().getId(), AuthenticationFactory.getAdaptedInstance().getUid());
    }

    @Test
    public void getEmailWorks() throws ExecutionException, InterruptedException {
        Tasks.await(AuthenticationFactory.getAdaptedInstance().signInWithEmailAndPassword("mainusersingleton@gmail.com", "fakePassword"));
        assertEquals("mainusersingleton@gmail.com", MainUser.getCurrentUserEmail());
    }
}