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

import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import io.github.polysmee.calendar.googlecalendarsync.CalendarUtilities;
import io.github.polysmee.database.databaselisteners.valuelisteners.StringSetValueListener;
import io.github.polysmee.database.databaselisteners.valuelisteners.StringValueListener;
import io.github.polysmee.login.AuthenticationSingleton;
import io.github.polysmee.login.MainUser;
import io.github.polysmee.znotification.AppointmentReminderNotification;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class DatabaseUserTest {

    private static final String username = "Mathis L'utilisateur";
    private static final String userDescription = "DatabaseUserTest description";


    @BeforeClass
    public static void setUp() throws Exception {
        AppointmentReminderNotification.setIsNotificationSetterEnable(false);
        DatabaseSingleton.setLocal();
        AuthenticationSingleton.setLocal();
        CalendarUtilities.setTest(true, false);
        FirebaseApp.clearInstancesForTest();
        FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext());
        Tasks.await(AuthenticationSingleton.getAdaptedInstance().createUserWithEmailAndPassword("DatabaseUserTest@gmail.com", "fakePassword"));
        DatabaseSingleton.getAdaptedInstance().getReference("users").child(MainUser.getMainUser().getId()).child("name").setValue(username);
        DatabaseSingleton.getAdaptedInstance().getReference("users").child(MainUser.getMainUser().getId()).child("picture").setValue(username);
        DatabaseSingleton.getAdaptedInstance().getReference("users").child(MainUser.getMainUser().getId()).child("description").setValue(userDescription);
        Thread.sleep(1000);
    }

    @AfterClass
    public static void clean() {
        DatabaseSingleton.getAdaptedInstance().getReference().setValue(null);
    }

    @Test
    public void getDescriptionAndThen() throws InterruptedException{
        DatabaseUser databaseUser= new DatabaseUser(MainUser.getMainUser().getId());
        ReentrantLock lock = new ReentrantLock();
        Condition cv = lock.newCondition();
        AtomicBoolean bool = new AtomicBoolean(false);
        AtomicReference<String> gotDescription = new AtomicReference<>("wrong description");
        StringValueListener sv = (description) -> {
            lock.lock();
            gotDescription.set(description);
            bool.set(Boolean.TRUE);
            cv.signal();
            lock.unlock();
        };
        lock.lock();
        try {
            databaseUser.getDescriptionAndThen(sv);
            while (!bool.get())
                cv.await();
            databaseUser.removeNameListener(sv);
            assertEquals(userDescription,gotDescription.get());
        } finally {
            lock.unlock();
            databaseUser.getDescription_Once_AndThen((e) -> {
            });
        }
    }

    @Test
    public void getId() {
        assertEquals(AuthenticationSingleton.getAdaptedInstance().getCurrentUser().getUid(), MainUser.getMainUser().getId());
    }

    @Test
    public void addAppointment() throws ExecutionException, InterruptedException {
        MainUser.getMainUser().addAppointment(new DatabaseAppointment("AZERTY"), "");
        FirebaseDatabase db = DatabaseSingleton.getAdaptedInstance();
        String id = Tasks.await(db.getReference()
                .child("users")
                .child(MainUser.getMainUser().getId())
                .child("appointments")
                .child("AZERTY").get()).getKey();
        assertNotNull(id);
        MainUser.getMainUser().removeAppointment(new DatabaseAppointment("AZERTY"));

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
            MainUser.getMainUser().getNameAndThen(sv);
            while (!bool.get())
                cv.await();
            MainUser.getMainUser().removeNameListener(sv);
            assertEquals(gotName.get(), username);
        } finally {
            lock.unlock();
            MainUser.getMainUser().getName_Once_AndThen((e) -> {
            });
        }
    }

    @Test
    public void createNewUserAppointment() {
        String id = MainUser.getMainUser().createNewUserAppointment(0, 1, "AICC", "rév", false);
        String ac = DatabaseSingleton.getAdaptedInstance().getReference("appointments").child(id).getKey();
        assertEquals(id, ac);
        DatabaseSingleton.getAdaptedInstance().getReference("appointments").child(id).setValue(null);
    }

    @Test
    public void getAppointmentsAndThen() throws InterruptedException, ExecutionException {
        ReentrantLock lock = new ReentrantLock();
        Condition cv = lock.newCondition();
        AtomicBoolean bool = new AtomicBoolean(false);
        AtomicBoolean oneElem = new AtomicBoolean(false);

        String apid = MainUser.getMainUser().createNewUserAppointment(3, 3, "AI", "HE", false);
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
            MainUser.getMainUser().getAppointmentsAndThen(ssv);
            while (!bool.get())
                cv.await();
            MainUser.getMainUser().removeAppointmentsListener(ssv);
            assertTrue(oneElem.get());
        } finally {
            lock.unlock();
            MainUser.getMainUser().getAppointments_Once_AndThen((e) -> {
            });
            Tasks.await(DatabaseSingleton.getAdaptedInstance().getReference("appointments").child(apid).removeValue());
        }
    }

    @Test
    public void getInvitesAndThen() throws InterruptedException, ExecutionException {
        ReentrantLock lock = new ReentrantLock();
        Condition cv = lock.newCondition();
        AtomicBoolean bool = new AtomicBoolean(false);
        AtomicBoolean oneElem = new AtomicBoolean(false);

        String apid = MainUser.getMainUser().createNewUserAppointment(3, 3, "AI", "HE", false);
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
            MainUser.getMainUser().getInvitesAndThen(ssv);
            while (!bool.get())
                cv.await();
            MainUser.getMainUser().removeInvitesListener(ssv);
            assertTrue(oneElem.get());
        } finally {
            lock.unlock();
            MainUser.getMainUser().getInvites_Once_AndThen((e) -> {
            });
            Tasks.await(DatabaseSingleton.getAdaptedInstance().getReference("appointments").child(apid).removeValue());
        }
    }

    @Test
    public void getFriendsAndThen() throws InterruptedException {
        ReentrantLock lock = new ReentrantLock();
        Condition cv = lock.newCondition();
        AtomicBoolean bool = new AtomicBoolean(false);
        AtomicBoolean oneElem = new AtomicBoolean(false);


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
            MainUser.getMainUser().getFriendsAndThen(ssv);
            while (!bool.get())
                cv.await();
            MainUser.getMainUser().removeFriendsListener(ssv);
            assertTrue(oneElem.get());
        } finally {
            lock.unlock();
            MainUser.getMainUser().getFriends_Once_And_Then((e) -> {
            });
        }
    }

    @Test
    public void getPictureAndThen() throws InterruptedException {
        ReentrantLock lock = new ReentrantLock();
        Condition cv = lock.newCondition();
        AtomicBoolean bool = new AtomicBoolean(false);
        AtomicReference<String> gotName = new AtomicReference<>("wrong name");
        StringValueListener sv = (picture) -> {
            lock.lock();
            gotName.set(picture);
            bool.set(Boolean.TRUE);
            cv.signal();
            lock.unlock();
        };
        lock.lock();
        try {
            MainUser.getMainUser().getProfilePictureAndThen(sv);
            while (!bool.get())
                cv.await();
            MainUser.getMainUser().removeProfilePictureListener(sv);
            assertEquals(gotName.get(), username);
        } finally {
            lock.unlock();
            MainUser.getMainUser().getProfilePicture_Once_And_Then((e) -> {
            });
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