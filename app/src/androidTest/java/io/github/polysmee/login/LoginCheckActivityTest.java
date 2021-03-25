package io.github.polysmee.login;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.security.SecureRandom;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import io.github.polysmee.MainActivity;
import io.github.polysmee.database.DatabaseFactory;
import io.github.polysmee.roomActivityTests.RoomActivityInfoNotOwnerTest;

import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;

//@RunWith(AndroidJUnit4.class)
public class LoginCheckActivityTest {
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
    }

    @Test
    public void firesLoginWhenNotLoggedIn() {
        AuthenticationFactory.getAdaptedInstance().signOut();
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), LoginCheckActivity.class);
        Intents.init();
        try(ActivityScenario<LoginCheckActivity> ignored = ActivityScenario.launch(intent)){
            intending(hasComponent(LoginActivity.class.getName()));
        }
        Intents.release();
    }

    @Test
    public void firesMainWhenLoggedIn() throws ExecutionException, InterruptedException {
        Tasks.await(AuthenticationFactory.getAdaptedInstance().signInWithEmailAndPassword(userEmail+"@gmail.com", "fakePassword"));
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), MainActivity.class);
        Intents.init();
        try(ActivityScenario<LoginCheckActivity> ignored = ActivityScenario.launch(intent)){
            intending(hasComponent(MainActivity.class.getName()));
        }
        Intents.release();
    }
}