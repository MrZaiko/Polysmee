package io.github.polysmee.database;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.FirebaseApp;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import io.github.polysmee.calendar.googlecalendarsync.CalendarUtilities;
import io.github.polysmee.database.databaselisteners.BooleanValueListener;
import io.github.polysmee.database.databaselisteners.LongValueListener;
import io.github.polysmee.database.databaselisteners.StringSetValueListener;
import io.github.polysmee.database.databaselisteners.StringValueListener;
import io.github.polysmee.login.AuthenticationFactory;
import io.github.polysmee.login.MainUser;
import io.github.polysmee.znotification.AppointmentReminderNotification;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class DatabaseAppointmentTest {

    private static final String username = "Mathis L'utilisateur";
    private static String apid;

    @BeforeClass
    public static void setUp() throws Exception {
        AppointmentReminderNotification.setIsNotificationSetterEnable(false);
        DatabaseFactory.setTest();
        AuthenticationFactory.setTest();
        CalendarUtilities.setTest(true, false);

        FirebaseApp.clearInstancesForTest();
        FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext());

        Tasks.await(AuthenticationFactory.getAdaptedInstance().createUserWithEmailAndPassword("DatabaseAppointmentTest@gmail.com", "fakePassword"));
        DatabaseFactory.getAdaptedInstance().getReference("users").child(MainUser.getMainUser().getId()).child("name").setValue(username);
        apid = MainUser.getMainUser().createNewUserAppointment(0, 3600, "AU", "chihiro", false);
        Thread.sleep(1000);
    }


    @AfterClass
    public static void clean() {
        DatabaseFactory.getAdaptedInstance().getReference().setValue(null);
    }

    @Test
    public void getStartTimeAndThen() throws InterruptedException {
        ReentrantLock lock = new ReentrantLock();
        Condition cv = lock.newCondition();
        AtomicBoolean bool = new AtomicBoolean(false);
        AtomicLong start = new AtomicLong(-1);
        LongValueListener ll = (star) -> {
            lock.lock();
            start.getAndSet(star);
            bool.set(Boolean.TRUE);
            cv.signal();
            lock.unlock();
        };
        lock.lock();
        try {
            new DatabaseAppointment(apid).getStartTimeAndThen(ll);
            while (!bool.get())
                cv.await();
            new DatabaseAppointment(apid).removeStartListener(ll);
            assertEquals(start.get(), 0);
        } finally {
            lock.unlock();
            new DatabaseAppointment(apid).getStartTime_Once_AndThen((l) -> {
            });
        }
    }

    @Test
    public void getDurationAndThen() throws InterruptedException {
        ReentrantLock lock = new ReentrantLock();
        Condition cv = lock.newCondition();
        AtomicBoolean bool = new AtomicBoolean(false);
        AtomicLong duration = new AtomicLong(-1);
        LongValueListener ll = (dura) -> {
            lock.lock();
            duration.getAndSet(dura);
            bool.set(Boolean.TRUE);
            cv.signal();
            lock.unlock();
        };
        lock.lock();
        try {
            new DatabaseAppointment(apid).getDurationAndThen(ll);
            while (!bool.get())
                cv.await();
            new DatabaseAppointment(apid).removeDurationListener(ll);
            assertEquals(duration.get(), 3600);
        } finally {
            lock.unlock();
            new DatabaseAppointment(apid).getDuration_Once_AndThen((l) -> {
            });

        }
    }

    @Test
    public void getCourseAndThen() throws InterruptedException {
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
            new DatabaseAppointment(apid).getCourseAndThen(sv);
            while (!bool.get())
                cv.await();
            new DatabaseAppointment(apid).removeCourseListener(sv);
            assertEquals("AU", gotName.get());
        } finally {
            lock.unlock();
            new DatabaseAppointment(apid).getCourse_Once_AndThen((l) -> {
            });
        }
    }

    @Test
    public void getTitleAndThen() throws InterruptedException {
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
            new DatabaseAppointment(apid).getTitleAndThen(sv);
            while (!bool.get())
                cv.await();
            new DatabaseAppointment(apid).removeTitleListener(sv);
            assertEquals("chihiro", gotName.get());
        } finally {
            lock.unlock();
            new DatabaseAppointment(apid).getTitle_Once_AndThen((l) -> {
            });
        }
    }

    @Test
    public void getOwnerIdAndThen() throws InterruptedException {
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
            new DatabaseAppointment(apid).getOwnerIdAndThen(sv);
            while (!bool.get())
                cv.await();
            new DatabaseAppointment(apid).removeOwnerListener(sv);
            assertEquals(MainUser.getMainUser().getId(), gotName.get());
        } finally {
            lock.unlock();
            new DatabaseAppointment(apid).getOwnerId_Once_AndThen((l) -> {
            });
        }
    }

    @Test
    public void getId() {
        assertEquals(new DatabaseAppointment(apid).getId(), apid);
    }


    @Test
    public void getAllPublicAppointmentsOnce() {
        Appointment.getAllPublicAppointmentsOnce((ss) -> assertTrue(ss.size() >= 1));
    }

    @Test
    public void getParticipantsIdAndThen() throws InterruptedException {
        ReentrantLock lock = new ReentrantLock();
        Condition cv = lock.newCondition();
        AtomicBoolean bool = new AtomicBoolean(false);
        AtomicBoolean listenerRan = new AtomicBoolean(false);
        StringSetValueListener sv = (ids) -> {
            lock.lock();
            listenerRan.set(Boolean.TRUE);
            bool.set(Boolean.TRUE);
            cv.signal();
            lock.unlock();
        };
        lock.lock();
        try {
            new DatabaseAppointment(apid).getParticipantsIdAndThen(sv);
            while (!bool.get())
                cv.await();
            new DatabaseAppointment(apid).removeParticipantsListener(sv);
            assertTrue(listenerRan.get());
        } finally {
            lock.unlock();
            new DatabaseAppointment(apid).getParticipantsId_Once_AndThen((l) -> {
            });
        }
    }

    @Test
    public void getInvitesIdAndThen() throws InterruptedException {
        ReentrantLock lock = new ReentrantLock();
        Condition cv = lock.newCondition();
        AtomicBoolean bool = new AtomicBoolean(false);
        AtomicBoolean listenerRan = new AtomicBoolean(false);
        StringSetValueListener sv = (ids) -> {
            lock.lock();
            listenerRan.set(Boolean.TRUE);
            bool.set(Boolean.TRUE);
            cv.signal();
            lock.unlock();
        };
        lock.lock();
        try {
            new DatabaseAppointment(apid).getInvitesIdAndThen(sv);
            while (!bool.get())
                cv.await();
            new DatabaseAppointment(apid).removeInvitesListener(sv);
            assertTrue(listenerRan.get());
        } finally {
            lock.unlock();
            new DatabaseAppointment(apid).getInvitesId_Once_AndThen((l) -> {
            });
        }
    }

    @Test
    public void addParticipant() {
        Appointment ap = new DatabaseAppointment(apid);
        User ck = new DatabaseUser("ck");
        ap.addParticipant(ck);
        ap.removeParticipant(ck);
        ap.addBan(ck);
        ap.removeBan(ck);
    }

    @Test
    public void getBansAndThen() throws InterruptedException {
        ReentrantLock lock = new ReentrantLock();
        Condition cv = lock.newCondition();
        AtomicBoolean bool = new AtomicBoolean(false);
        AtomicBoolean moreThanOne = new AtomicBoolean(false);
        StringSetValueListener sv = (ids) -> {
            lock.lock();
            moreThanOne.set(Boolean.TRUE);
            bool.set(Boolean.TRUE);
            cv.signal();
            lock.unlock();
        };
        lock.lock();
        try {
            new DatabaseAppointment(apid).getBansAndThen(sv);
            while (!bool.get())
                cv.await();
            new DatabaseAppointment(apid).removeBansListener(sv);
            assertTrue(moreThanOne.get());
        } finally {
            lock.unlock();
            new DatabaseAppointment(apid).getBans_Once_AndThen((l) -> {
            });
        }
    }

    @Test
    public void getPrivateAndThen() throws InterruptedException {
        ReentrantLock lock = new ReentrantLock();
        Condition cv = lock.newCondition();
        AtomicBoolean bool = new AtomicBoolean(false);
        AtomicBoolean isPrivate = new AtomicBoolean(false);
        BooleanValueListener sv = (ids) -> {
            lock.lock();
            isPrivate.set(!ids);
            bool.set(Boolean.TRUE);
            cv.signal();
            lock.unlock();
        };
        lock.lock();
        try {
            new DatabaseAppointment(apid).setPrivate(false);
            new DatabaseAppointment(apid).getPrivateAndThen(sv);
            while (!bool.get())
                cv.await();
            new DatabaseAppointment(apid).removePrivateListener(sv);
            assertTrue(isPrivate.get());

        } finally {
            lock.unlock();
            new DatabaseAppointment(apid).getPrivate_Once_AndThen((l) -> {
            });
        }
    }
}