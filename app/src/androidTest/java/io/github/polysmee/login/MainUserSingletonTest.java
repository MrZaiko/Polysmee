package io.github.polysmee.login;

import androidx.test.core.app.ApplicationProvider;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.FirebaseApp;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.concurrent.ExecutionException;

import io.github.polysmee.database.DatabaseFactory;
import io.github.polysmee.notification.AppointmentReminderNotificationSetupListener;

import static org.junit.Assert.*;

//@RunWith(AndroidJUnit4.class)
public class MainUserSingletonTest {

    @BeforeClass
    public static void setUp() throws Exception {
        AppointmentReminderNotificationSetupListener.setIsNotificationSetterEnable(false);
        DatabaseFactory.setTest();
        AuthenticationFactory.setTest();
        FirebaseApp.clearInstancesForTest();
        FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext());
        Tasks.await(AuthenticationFactory.getAdaptedInstance().createUserWithEmailAndPassword("MainUserSingletonTest@gmail.com", "fakePassword"));
    }

    @Test(expected = NullPointerException.class)
    public void getInstanceThrows() {
        AuthenticationFactory.getAdaptedInstance().signOut();
        MainUserSingleton.getInstance();
    }

    @Test
    public void getInstanceWorks() throws ExecutionException, InterruptedException {
        Tasks.await(AuthenticationFactory.getAdaptedInstance().signInWithEmailAndPassword("MainUserSingletonTest@gmail.com", "fakePassword"));
        assertEquals(MainUserSingleton.getInstance().getId(), AuthenticationFactory.getAdaptedInstance().getUid());
    }
}