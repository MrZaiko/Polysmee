package io.github.polysmee.login;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.ExecutionException;

import io.github.polysmee.MainActivity;

import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;

@RunWith(AndroidJUnit4.class)
public class LoginCheckActivityTest {

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

    @Test
    public void firesLoginWhenNotLoggedIn() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), LoginCheckActivity.class);
        Intents.init();
        try(ActivityScenario<LoginCheckActivity> ignored = ActivityScenario.launch(intent)){
            intending(hasComponent(LoginActivity.class.getName()));
        }
        Intents.release();
    }

    @Test
    public void firesMainWhenLoggedIn() throws ExecutionException, InterruptedException {
        Tasks.await(FirebaseAuth.getInstance().signInWithEmailAndPassword("polysmee1234@gmail.com", "fakePassword"));
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), MainActivity.class);
        Intents.init();
        try(ActivityScenario<LoginCheckActivity> ignored = ActivityScenario.launch(intent)){
            intending(hasComponent(MainActivity.class.getName()));
        }
        Intents.release();
    }
}