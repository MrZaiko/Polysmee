package io.github.polysmee.database;

import android.util.Log;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;


import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import io.github.polysmee.database.databaselisteners.StringSetValueListener;
import io.github.polysmee.database.databaselisteners.StringValueListener;
import io.github.polysmee.login.AuthenticationFactory;
import io.github.polysmee.login.MainUserSingleton;
import io.github.polysmee.znotification.AppointmentReminderNotificationSetupListener;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class DatabaseUserTest {

    private static final String username = "Mathis L'utilisateur";


    @BeforeClass
    public static void setUp() throws Exception {
        AppointmentReminderNotificationSetupListener.setIsNotificationSetterEnable(false);
        DatabaseFactory.setTest();
        AuthenticationFactory.setTest();
        FirebaseApp.clearInstancesForTest();
        FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext());
        Tasks.await(AuthenticationFactory.getAdaptedInstance().createUserWithEmailAndPassword("DatabaseUserTest@gmail.com", "fakePassword"));
        DatabaseFactory.getAdaptedInstance().getReference("users").child(MainUserSingleton.getInstance().getId()).child("name").setValue(username);
        Thread.sleep(1000);
    }

    @Test
    public void getId() {
        assertEquals(AuthenticationFactory.getAdaptedInstance().getCurrentUser().getUid(), MainUserSingleton.getInstance().getId());
    }

    @Test
    public void addAppointment() throws ExecutionException, InterruptedException {
        MainUserSingleton.getInstance().addAppointment(new DatabaseAppointment("AZERTY"));
        FirebaseDatabase db = DatabaseFactory.getAdaptedInstance();
        String id = Tasks.await(db.getReference()
                .child("users")
                .child(MainUserSingleton.getInstance().getId())
                .child("appointments")
                .child("AZERTY").get()).getKey();
        assertNotNull(id);
        MainUserSingleton.getInstance().removeAppointment(new DatabaseAppointment("AZERTY"));

    }

    @Test
    public void getNameAndThen() throws InterruptedException {
        ReentrantLock lock = new ReentrantLock();
        Condition cv = lock.newCondition();
        AtomicBoolean bool = new AtomicBoolean(false);
        AtomicReference<String> gotName = new AtomicReference<>("wrong name");
        StringValueListener sv = (name) -> {
            lock.lock();
            gotName.set(name);
            bool.set(Boolean.TRUE);
            cv.signal();
            lock.unlock();
        };
        lock.lock();
        try {
            MainUserSingleton.getInstance().getNameAndThen(sv);
            while(!bool.get())
                cv.await();
            MainUserSingleton.getInstance().removeNameListener(sv);
            assertEquals(gotName.get(), username);
        } finally {
            lock.unlock();
            MainUserSingleton.getInstance().getName_Once_AndThen((e) -> {});
        }
    }

    @Test
    public void createNewUserAppointment() {
        String id = MainUserSingleton.getInstance().createNewUserAppointment(0, 1, "AICC", "rév", false);
        String ac = DatabaseFactory.getAdaptedInstance().getReference("appointments").child(id).getKey();
        assertEquals(id, ac);
        DatabaseFactory.getAdaptedInstance().getReference("appointments").child(id).setValue(null);
    }

    @Test
    public void getAppointmentsAndThen() throws InterruptedException, ExecutionException {
        ReentrantLock lock = new ReentrantLock();
        Condition cv = lock.newCondition();
        AtomicBoolean bool = new AtomicBoolean(false);
        AtomicBoolean oneElem = new AtomicBoolean(false);

        String apid = MainUserSingleton.getInstance().createNewUserAppointment(3, 3, "AI", "HE", false);
        StringSetValueListener ssv = (set) -> {
            lock.lock();
            oneElem.set(set.size() > 0);
            Log.d("METAAPP", "" + oneElem.get());
            bool.set(Boolean.TRUE);
            cv.signal();
            lock.unlock();
        };

        lock.lock();
        try {
            MainUserSingleton.getInstance().getAppointmentsAndThen(ssv);
            while(!bool.get())
                cv.await();
            MainUserSingleton.getInstance().removeAppointmentsListener(ssv);
            assertTrue(oneElem.get());
        } finally {
            lock.unlock();
            MainUserSingleton.getInstance().getAppointments_Once_AndThen((e) -> {});
            Tasks.await(DatabaseFactory.getAdaptedInstance().getReference("appointments").child(apid).removeValue());
        }
    }

    @Test
    public void getInvitesAndThen() throws InterruptedException, ExecutionException {
        ReentrantLock lock = new ReentrantLock();
        Condition cv = lock.newCondition();
        AtomicBoolean bool = new AtomicBoolean(false);
        AtomicBoolean oneElem = new AtomicBoolean(false);

        String apid = MainUserSingleton.getInstance().createNewUserAppointment(3, 3, "AI", "HE", false);
        StringSetValueListener ssv = (set) -> {
            lock.lock();
            oneElem.set(Boolean.TRUE);
            Log.d("METAAPP", "" + oneElem.get());
            bool.set(Boolean.TRUE);
            cv.signal();
            lock.unlock();
        };

        lock.lock();
        try {
            MainUserSingleton.getInstance().getInvitesAndThen(ssv);
            while(!bool.get())
                cv.await();
            MainUserSingleton.getInstance().removeInvitesListener(ssv);
            assertTrue(oneElem.get());
        } finally {
            lock.unlock();
            MainUserSingleton.getInstance().getInvites_Once_AndThen((e) -> {});
            Tasks.await(DatabaseFactory.getAdaptedInstance().getReference("appointments").child(apid).removeValue());
        }
    }

    @Test
    public void testEquals() {
        assertEquals(new DatabaseUser("hello"), new DatabaseUser("hello"));
    }

    @Test
    public void testHashCode() {
        assertEquals(new DatabaseUser("hello").hashCode(), new DatabaseUser("hello").hashCode());
    }

}