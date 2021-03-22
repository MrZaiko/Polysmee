package io.github.polysmee.login;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Objects;
import java.util.concurrent.ExecutionException;

import io.github.polysmee.database.DatabaseFactory;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class MainUserSingletonTest {

    @BeforeClass
    public static void setUp() throws Exception {
        DatabaseFactory.setTest();
        AuthenticationFactory.setTest();
        FirebaseApp.clearInstancesForTest();
        FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext());
        Tasks.await(AuthenticationFactory.getAdaptedInstance().createUserWithEmailAndPassword("polysmee1234@gmail.com", "fakePassword"));
        Tasks.await(AuthenticationFactory.getAdaptedInstance().signInWithEmailAndPassword("polysmee1234@gmail.com", "fakePassword"));
    }

    @AfterClass
    public static void delete() throws ExecutionException, InterruptedException {
        Tasks.await(AuthenticationFactory.getAdaptedInstance().signInWithEmailAndPassword("polysmee1234@gmail.com", "fakePassword"));
        Tasks.await(AuthenticationFactory.getAdaptedInstance().getCurrentUser().delete());
    }

    @Test(expected = NullPointerException.class)
    public void getInstanceThrows() {
        AuthenticationFactory.getAdaptedInstance().signOut();
        MainUserSingleton.getInstance();
    }

    @Test
    public void getInstanceWorks() throws ExecutionException, InterruptedException {
        Tasks.await(AuthenticationFactory.getAdaptedInstance().signInWithEmailAndPassword("polysmee1234@gmail.com", "fakePassword"));
        assertEquals(MainUserSingleton.getInstance().getId(), AuthenticationFactory.getAdaptedInstance().getUid());
    }
}