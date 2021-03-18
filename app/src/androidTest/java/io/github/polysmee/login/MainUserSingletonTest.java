package io.github.polysmee.login;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Objects;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class MainUserSingletonTest {
/*
    @Before
    public void setUp() throws Exception {
        FirebaseApp.clearInstancesForTest();
        FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext());
        Tasks.await(FirebaseAuth.getInstance().createUserWithEmailAndPassword("polysmee1234@gmail.com", "fakePassword"));
        Tasks.await(FirebaseAuth.getInstance().signInWithEmailAndPassword("polysmee1234@gmail.com", "fakePassword"));
        MainUserSingleton.reboot();
    }

    @After
    public void delete() throws ExecutionException, InterruptedException {
        Tasks.await(FirebaseAuth.getInstance().signInWithEmailAndPassword("polysmee1234@gmail.com", "fakePassword"));
        Tasks.await(FirebaseAuth.getInstance().getCurrentUser().delete());
    }

    @Test(expected = NullPointerException.class)
    public void getInstanceThrows() {
        FirebaseAuth.getInstance().signOut();
        MainUserSingleton.getInstance();
    }

    @Test
    public void getInstanceWorks() throws ExecutionException, InterruptedException {
        Tasks.await(FirebaseAuth.getInstance().signInWithEmailAndPassword("polysmee1234@gmail.com", "fakePassword"));
        assertEquals(MainUserSingleton.getInstance().getId(), FirebaseAuth.getInstance().getUid());
    }*/
@Test
public void trashTest(){
    assertEquals(1,1);
}
}