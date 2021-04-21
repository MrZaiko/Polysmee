package io.github.polysmee.settings;

import androidx.test.core.app.ApplicationProvider;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Calendar;

import io.github.polysmee.database.DatabaseFactory;
import io.github.polysmee.login.AuthenticationFactory;
import io.github.polysmee.login.MainUserSingleton;

import static org.junit.Assert.*;

public class UserInfoDataStoreTest {
    private final static String userEmail = "UserInfoDataStoreTest@gmail.com";
    private final static String userName = "UserInfoDataStoreTest";
    private final static String userPassword = "fakePassword";


    @BeforeClass
    public static void setUp() throws Exception {
        DatabaseFactory.setTest();
        AuthenticationFactory.setTest();
        FirebaseApp.clearInstancesForTest();
        FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext());
        Tasks.await(AuthenticationFactory.getAdaptedInstance().createUserWithEmailAndPassword(userEmail, userPassword));
        DatabaseFactory.getAdaptedInstance().getReference("users").child(MainUserSingleton.getInstance().getId()).child("name").setValue(userName);
    }

    public static void testNameDatabase(String value){
        Task<DataSnapshot> taskDataSnapshot = DatabaseFactory.getAdaptedInstance().getReference("users").child(MainUserSingleton.getInstance().getId()).child("name").get();
        OnSuccessListener<DataSnapshot> onSuccessListenerString = new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                assertEquals(dataSnapshot.getValue(),value);
                return;
            }
        };
        taskDataSnapshot.addOnSuccessListener(onSuccessListenerString);
    }
    @Test
    public void putString() {
        UserInfoDataStore userInfoDataStore = new UserInfoDataStore();
        String stringToPut = "string putted";
        userInfoDataStore.putString(UserInfoDataStore.preferenceKeyMainUserName, stringToPut);
        testNameDatabase(stringToPut);
        userInfoDataStore.putString(UserInfoDataStore.preferenceKeyMainUserName, userName);
        testNameDatabase(userName);
        userInfoDataStore.putString(null, stringToPut);
        testNameDatabase(userName);
        userInfoDataStore.putString(UserInfoDataStore.preferenceKeyMainUserEmail, stringToPut);
        testNameDatabase(userName);
        userInfoDataStore.putString("fkesjnfejsf", stringToPut);
        testNameDatabase(userName);
    }

    @Test
    public void getString() {
        UserInfoDataStore userInfoDataStore = new UserInfoDataStore();
        assertEquals(userInfoDataStore.getString(UserInfoDataStore.preferenceKeyMainUserName, "test"),"");
        assertEquals(userInfoDataStore.getString(UserInfoDataStore.preferenceKeyMainUserEmail, "test"), "");
        assertEquals(userInfoDataStore.getString("jfnsejfnes", "test"), "");

    }
}