package io.github.polysmee.login;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.FirebaseApp;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.ExecutionException;

import io.github.polysmee.MainActivity;
import io.github.polysmee.database.DatabaseFactory;
import io.github.polysmee.notification.AppointmentReminderNotificationSetupListener;

import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;

@RunWith(AndroidJUnit4.class)
public class LoginCheckActivityTest {

    @BeforeClass
    public static void setUp() throws Exception {
        AppointmentReminderNotificationSetupListener.setIsNotificationSetterEnable(false);
        DatabaseFactory.setTest();
        AuthenticationFactory.setTest();
        FirebaseApp.clearInstancesForTest();
        FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext());
        Tasks.await(AuthenticationFactory.getAdaptedInstance().createUserWithEmailAndPassword("LoginCheckActivityTest@gmail.com", "fakePassword"));
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
        Tasks.await(AuthenticationFactory.getAdaptedInstance().signInWithEmailAndPassword("LoginCheckActivityTest@gmail.com", "fakePassword"));
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), MainActivity.class);
        Intents.init();
        try(ActivityScenario<LoginCheckActivity> ignored = ActivityScenario.launch(intent)){
            intending(hasComponent(MainActivity.class.getName()));
        }
        Intents.release();
    }
}