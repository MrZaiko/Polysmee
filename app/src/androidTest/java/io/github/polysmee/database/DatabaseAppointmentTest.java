package io.github.polysmee.database;

import androidx.test.core.app.ApplicationProvider;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import io.github.polysmee.login.MainUserSingleton;

import static org.junit.Assert.*;

public class DatabaseAppointmentTest {

    private static final String username = "Mathis L'utilisateur";
    private static String apid;

    @BeforeClass
    public static void setUp() throws Exception {
        FirebaseApp.clearInstancesForTest();
        FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext());
        Tasks.await(FirebaseAuth.getInstance().createUserWithEmailAndPassword("polysmee14@gmail.com", "fakePassword"));
        FirebaseDatabase.getInstance().getReference("users").child(MainUserSingleton.getInstance().getId()).child("name").setValue(username);
        apid = MainUserSingleton.getInstance().createNewUserAppointment(0, 3600, "AU", "chihiro");
    }

    @AfterClass
    public static void delete() throws ExecutionException, InterruptedException {
        Tasks.await(FirebaseAuth.getInstance().signInWithEmailAndPassword("polysmee14@gmail.com", "fakePassword"));
        FirebaseDatabase.getInstance().getReference("users").child(MainUserSingleton.getInstance().getId()).setValue(null);
        Tasks.await(FirebaseAuth.getInstance().getCurrentUser().delete());
        FirebaseDatabase.getInstance().getReference("appointments").child(apid).setValue(null);
    }

    @Test
    public void getStartTimeAndThen() throws InterruptedException {
        ReentrantLock lock = new ReentrantLock();
        Condition cv = lock.newCondition();
        AtomicBoolean bool = new AtomicBoolean(false);
        AtomicLong start = new AtomicLong(-1);
        lock.lock();
        try {
            new DatabaseAppointment(apid).getStartTimeAndThen(
                    (star) -> {
                        lock.lock();
                        start.getAndSet(star);
                        bool.set(Boolean.TRUE);
                        cv.signal();
                        lock.unlock();
                    }
            );
            while(!bool.get())
                cv.await();
            assertEquals(start.get(), 0);
        } finally {
            lock.unlock();
        }
    }

    @Test
    public void getDurationAndThen() throws InterruptedException {
        ReentrantLock lock = new ReentrantLock();
        Condition cv = lock.newCondition();
        AtomicBoolean bool = new AtomicBoolean(false);
        AtomicLong duration = new AtomicLong(-1);
        lock.lock();
        try {
            new DatabaseAppointment(apid).getDurationAndThen(
                    (dura) -> {
                        lock.lock();
                        duration.getAndSet(dura);
                        bool.set(Boolean.TRUE);
                        cv.signal();
                        lock.unlock();
                    }
            );
            while(!bool.get())
                cv.await();
            assertEquals(duration.get(), 3600);
        } finally {
            lock.unlock();
        }
    }

    @Test
    public void getCourseAndThen() throws InterruptedException {
        ReentrantLock lock = new ReentrantLock();
        Condition cv = lock.newCondition();
        AtomicBoolean bool = new AtomicBoolean(false);
        AtomicReference<String> gotName = new AtomicReference<>("wrong name");
        lock.lock();
        try {
            new DatabaseAppointment(apid).getCourseAndThen(
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
            assertEquals("AU", gotName.get());
        } finally {
            lock.unlock();
        }
    }

    @Test
    public void getTitleAndThen() throws InterruptedException {
        ReentrantLock lock = new ReentrantLock();
        Condition cv = lock.newCondition();
        AtomicBoolean bool = new AtomicBoolean(false);
        AtomicReference<String> gotName = new AtomicReference<>("wrong name");
        lock.lock();
        try {
            new DatabaseAppointment(apid).getTitleAndThen(
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
            assertEquals("chihiro", gotName.get());
        } finally {
            lock.unlock();
        }
    }

    @Test
    public void getParticipantsIdAndThen() throws InterruptedException {
        ReentrantLock lock = new ReentrantLock();
        Condition cv = lock.newCondition();
        AtomicBoolean bool = new AtomicBoolean(false);
        AtomicBoolean onePerson = new AtomicBoolean(false);
        lock.lock();
        try {
            new DatabaseAppointment(apid).getParticipantsIdAndThen(
                    (name) -> {
                        lock.lock();
                        onePerson.set(name.size() == 1);
                        bool.set(Boolean.TRUE);
                        cv.signal();
                        lock.unlock();
                    }
            );
            while(!bool.get())
                cv.await();
            assertTrue(onePerson.get());
        } finally {
            lock.unlock();
        }
    }

    @Test
    public void getOwnerIdAndThen() throws InterruptedException {
        ReentrantLock lock = new ReentrantLock();
        Condition cv = lock.newCondition();
        AtomicBoolean bool = new AtomicBoolean(false);
        AtomicReference<String> gotName = new AtomicReference<>("wrong name");
        lock.lock();
        try {
            new DatabaseAppointment(apid).getOwnerIdAndThen(
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
            assertEquals(MainUserSingleton.getInstance().getId(), gotName.get());
        } finally {
            lock.unlock();
        }
    }

    @Test
    public void getId() {
        assertEquals(new DatabaseAppointment(apid).getId(), apid);
    }

    @Test
    public void getStartTime() {
        assertEquals(0, new DatabaseAppointment(apid).getStartTime());
    }

    @Test
    public void getDuration() {
        assertEquals(0, new DatabaseAppointment(apid).getDuration());
    }

    @Test
    public void getCourse() {
        assertNull(new DatabaseAppointment(apid).getCourse());
    }

    @Test
    public void getTitle() {
        assertNull(new DatabaseAppointment(apid).getTitle());
    }

    @Test
    public void getParticipants() {
        assertNull(new DatabaseAppointment(apid).getParticipants());
    }

    @Test
    public void getOwner() {
        assertNull(new DatabaseAppointment(apid).getOwner());
    }

    @Test
    public void setStartTime() throws InterruptedException {
        new DatabaseAppointment(apid).setStartTime(1999);
        new DatabaseAppointment(apid).setStartTime(0);
        getStartTimeAndThen();
    }

    @Test
    public void setDuration() throws InterruptedException {
        new DatabaseAppointment(apid).setDuration(3601);
        new DatabaseAppointment(apid).setDuration(3600);
        getDurationAndThen();
    }

    @Test
    public void setCourse() {
        new DatabaseAppointment(apid).setCourse("AZUIAH");
        new DatabaseAppointment(apid).setCourse("AU");
        getCourse();
    }

    @Test
    public void setTitle() {
        new DatabaseAppointment(apid).setTitle("AZUIAH");
        new DatabaseAppointment(apid).setTitle("chihiro");
        getTitle();
    }

    @Test
    public void addAndRemoveParticipant() throws InterruptedException {
        new DatabaseAppointment(apid).addParticipant(new DatabaseUser("3"));
        new DatabaseAppointment(apid).removeParticipant(new DatabaseUser("3"));
        getParticipantsIdAndThen();
    }

}