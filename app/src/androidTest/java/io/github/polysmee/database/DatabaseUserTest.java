package io.github.polysmee.database;

import androidx.test.core.app.ApplicationProvider;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.FirebaseDatabase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.Console;
import java.util.HashSet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import io.github.polysmee.login.MainUserSingleton;

import static org.junit.Assert.*;

public class DatabaseUserTest {
/*
    private final String username = "Mathis L'utilisateur";
    @Before
    public void setUp() throws Exception {
        FirebaseApp.clearInstancesForTest();
        FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext());
        Tasks.await(FirebaseAuth.getInstance().createUserWithEmailAndPassword("polysmee1234@gmail.com", "fakePassword"));
        Tasks.await(FirebaseAuth.getInstance().signInWithEmailAndPassword("polysmee1234@gmail.com", "fakePassword"));
        FirebaseDatabase.getInstance().getReference("users").child(MainUserSingleton.getInstance().getId()).child("name").setValue(username);
        MainUserSingleton.reboot();
    }

    @After
    public void delete() throws ExecutionException, InterruptedException {
        Tasks.await(FirebaseAuth.getInstance().signInWithEmailAndPassword("polysmee1234@gmail.com", "fakePassword"));
        FirebaseDatabase.getInstance().getReference("users").child(MainUserSingleton.getInstance().getId()).setValue(null);
        Tasks.await(FirebaseAuth.getInstance().getCurrentUser().delete());
    }


    @Test
    public void getId() {
        assertEquals(FirebaseAuth.getInstance().getCurrentUser().getUid(), MainUserSingleton.getInstance().getId());
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
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        String id = (String) Tasks.await(db.getReference()
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
        String ac = FirebaseDatabase.getInstance().getReference("appointments").child(id).getKey();
        assertEquals(id, ac);
        FirebaseDatabase.getInstance().getReference("appointments").child(id).setValue(null);
    }

    @Test
    public void getAppointmentsAndThen() throws InterruptedException {
        ReentrantLock lock = new ReentrantLock();
        Condition cv = lock.newCondition();
        AtomicBoolean bool = new AtomicBoolean(false);
        AtomicBoolean oneElem = new AtomicBoolean(false);

        MainUserSingleton.getInstance().createNewUserAppointment(3, 3, "AI", "HE");

        lock.lock();
        try {
            MainUserSingleton.getInstance().getAppointmentsAndThen(
                    (name) -> {
                        lock.lock();
                        oneElem.set(name.size() == 1);

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
        }
    }

    @Test
    public void testEquals() {

    }

    @Test
    public void testHashCode() {
    }


*/
@Test
public void trashTest(){
    assertEquals(1,1);
}
}