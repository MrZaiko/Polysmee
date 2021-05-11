package io.github.polysmee.roomActivityTests;

import android.content.Intent;
import android.os.Looper;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.FirebaseApp;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import io.github.polysmee.database.DatabaseFactory;
import io.github.polysmee.login.AuthenticationFactory;
import io.github.polysmee.login.MainUser;
import io.github.polysmee.profile.FriendsActivity;
import io.github.polysmee.profile.ProfileActivity;
import io.github.polysmee.room.fragments.HelperImages;
import io.github.polysmee.znotification.AppointmentReminderNotificationSetupListener;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed;
import static com.schibsted.spain.barista.interaction.BaristaSleepInteractions.sleep;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class HelperImagesTest {
    private static final String username1 = "Frez";

    @Test
    public void getBytesTest(){
        byte[] input = new byte[1024];
        int i = 0;
        while(i<input.length){
            input[i] = (byte) (i % 256);
            i+=1;
        }
        InputStream inputStream = new ByteArrayInputStream(input);
        try{
            byte [] result = HelperImages.getBytes(inputStream);
            for(int j = 0; j < result.length;++j){
                assertEquals(input[j],result[j]);
            }
        }
        catch (IOException e){

        }
    }


}
