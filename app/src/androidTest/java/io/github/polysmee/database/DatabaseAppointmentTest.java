package io.github.polysmee.database;

import android.util.Log;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;


import java.security.SecureRandom;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import io.github.polysmee.login.AuthenticationFactory;
import io.github.polysmee.login.MainUserSingleton;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class DatabaseAppointmentTest {

    private static final String username = "Mathis L'utilisateur";
    private static String apid;

    @BeforeClass
    public static void setUp() throws Exception {
        DatabaseFactory.setTest();
        AuthenticationFactory.setTest();

        FirebaseApp.clearInstancesForTest();
        FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext());

        Tasks.await(AuthenticationFactory.getAdaptedInstance().createUserWithEmailAndPassword("DatabaseAppointmentTest@gmail.com", "fakePassword"));
        DatabaseFactory.getAdaptedInstance().getReference("users").child(MainUserSingleton.getInstance().getId()).child("name").setValue(username);
        apid = MainUserSingleton.getInstance().createNewUserAppointment(0, 3600, "AU", "chihiro", false);
        Thread.sleep(1000);
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


}