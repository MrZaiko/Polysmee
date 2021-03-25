package io.github.polysmee.database;

import android.util.Log;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;


import java.security.SecureRandom;
import java.util.HashSet;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import io.github.polysmee.login.AuthenticationFactory;
import io.github.polysmee.login.MainUserSingleton;
import io.github.polysmee.roomActivityTests.RoomActivityInfoNotOwnerTest;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class DatabaseUserTest {

    private static final String username = "Mathis L'utilisateur";

    private static String userEmail;


    @BeforeClass
    public static void setUp() throws Exception {
        Random idGen = new SecureRandom();
        userEmail = idGen.nextInt(2000) +"@gmail.com";

        DatabaseFactory.setTest();
        AuthenticationFactory.setTest();
        FirebaseApp.clearInstancesForTest();
        FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext());
        Tasks.await(AuthenticationFactory.getAdaptedInstance().createUserWithEmailAndPassword(userEmail+"@gmail.com", "fakePassword"));
        DatabaseFactory.getAdaptedInstance().getReference("users").child(MainUserSingleton.getInstance().getId()).child("name").setValue(username);
    }

    @Test
    public void getId() {
        assertEquals(AuthenticationFactory.getAdaptedInstance().getCurrentUser().getUid(), MainUserSingleton.getInstance().getId());
    }

    @Test
    public void getName() {
        assertEquals(MainUserSingleton.getInstance().getName(), "YOU USED GETNAME");
    }

    @Test
    public void getSurname() {
        assertEquals(MainUserSingleton.getInstance().getSurname(), "YOU USED GETSURNAME");
    }

    @Test
    public void getAppointments() {
        assertEquals(MainUserSingleton.getInstance().getAppointments(), new HashSet<>());
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
        lock.lock();
        try {
            MainUserSingleton.getInstance().getNameAndThen(
                    (name) -> {
                        lock.lock();
                        gotName.set(name);
                        bool.set(Boolean.TRUE);
                        cv.signal();
                        lock.unlock();
                    }
            );
            while(!bool.get())
                cv.await();
            assertEquals(gotName.get(), username);
        } finally {
            lock.unlock();
        }
    }

    @Test
    public void createNewUserAppointment() {
        String id = MainUserSingleton.getInstance().createNewUserAppointment(0, 1, "AICC", "rév");
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

        String apid = MainUserSingleton.getInstance().createNewUserAppointment(3, 3, "AI", "HE");

        lock.lock();
        try {
            MainUserSingleton.getInstance().getAppointmentsAndThen(
                    (set) -> {
                        lock.lock();
                        oneElem.set(set.size() > 0);
                        Log.d("METAAPP", "" + oneElem.get());
                        bool.set(Boolean.TRUE);
                        cv.signal();
                        lock.unlock();
                    }
            );
            while(!bool.get())
                cv.await();
            assertTrue(oneElem.get());
        } finally {
            lock.unlock();
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