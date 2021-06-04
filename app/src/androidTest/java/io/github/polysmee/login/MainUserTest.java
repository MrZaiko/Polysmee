package io.github.polysmee.login;

import androidx.test.core.app.ApplicationProvider;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.FirebaseApp;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.concurrent.ExecutionException;

import io.github.polysmee.calendar.googlecalendarsync.CalendarUtilities;
import io.github.polysmee.database.DatabaseSingleton;
import io.github.polysmee.notification.AppointmentReminderNotification;

import static org.junit.Assert.assertEquals;

//@RunWith(AndroidJUnit4.class)
public class MainUserTest {

    @BeforeClass
    public static void setUp() throws Exception {
        AppointmentReminderNotification.setIsNotificationSetterEnable(false);
        DatabaseSingleton.setLocal();
        CalendarUtilities.setTest(true, false);
        AuthenticationSingleton.setLocal();
        FirebaseApp.clearInstancesForTest();
        FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext());
        Tasks.await(AuthenticationSingleton.getAdaptedInstance().createUserWithEmailAndPassword("mainusersingleton@gmail.com", "fakePassword"));
    }

    @AfterClass
    public static void clean() {
        DatabaseSingleton.getAdaptedInstance().getReference().setValue(null);
    }

    @Test(expected = NullPointerException.class)
    public void getInstanceThrows() {
        AuthenticationSingleton.getAdaptedInstance().signOut();
        MainUser.getMainUser();
    }

    @Test
    public void getInstanceWorks() throws ExecutionException, InterruptedException {
        Tasks.await(AuthenticationSingleton.getAdaptedInstance().signInWithEmailAndPassword("mainusersingleton@gmail.com", "fakePassword"));
        assertEquals(MainUser.getMainUser().getId(), AuthenticationSingleton.getAdaptedInstance().getUid());
    }

    @Test
    public void getEmailWorks() throws ExecutionException, InterruptedException {
        Tasks.await(AuthenticationSingleton.getAdaptedInstance().signInWithEmailAndPassword("mainusersingleton@gmail.com", "fakePassword"));
        assertEquals("mainusersingleton@gmail.com", MainUser.getCurrentUserEmail());
    }
}