package io.github.polysmee.photo.editing;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.view.View;
import android.widget.TextView;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.matcher.IntentMatchers;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.FirebaseApp;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.core.AllOf;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.ByteArrayOutputStream;

import io.github.polysmee.R;
import io.github.polysmee.database.DatabaseFactory;
import io.github.polysmee.database.UploadService;
import io.github.polysmee.database.UploadServiceFactory;
import io.github.polysmee.login.AuthenticationFactory;
import io.github.polysmee.notification.AppointmentReminderNotificationSetupListener;
import io.github.polysmee.room.RoomActivity;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.swipeDown;
import static androidx.test.espresso.action.ViewActions.swipeLeft;
import static androidx.test.espresso.contrib.ActivityResultMatchers.hasResultCode;
import static androidx.test.espresso.contrib.ActivityResultMatchers.hasResultData;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasData;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static com.schibsted.spain.barista.interaction.BaristaClickInteractions.clickOn;
import static com.schibsted.spain.barista.interaction.BaristaRadioButtonInteractions.clickRadioButtonItem;
import static com.schibsted.spain.barista.interaction.BaristaSeekBarInteractions.setProgressTo;
import static com.schibsted.spain.barista.interaction.BaristaSleepInteractions.sleep;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(JUnit4.class)
public class PictureEditActivityTest {

    private final static byte[] bigYoshi = {
            -1, -40, -1, -32, 0, 16, 74, 70, 73, 70, 0, 1, 1, 0, 0, 1, 0, 1, 0, 0, -1, -37, 0, -124, 0, 9,
            6, 7, 8, 7, 6, 9, 8, 7, 8, 10, 10, 9, 11, 13, 22, 15, 13, 12, 12, 13, 27, 20, 21, 16, 22, 32,
            29, 34, 34, 32, 29, 31, 31, 36, 40, 52, 44, 36, 38, 49, 39, 31, 31, 45, 61, 45, 49, 53, 55, 58, 58, 58,
            35, 43, 63, 68, 63, 56, 67, 52, 57, 58, 55, 1, 10, 10, 10, 13, 12, 13, 26, 15, 15, 26, 55, 37, 31, 37,
            55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55,
            55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, -1, -64,
            0, 17, 8, 0, 101, 0, -70, 3, 1, 34, 0, 2, 17, 1, 3, 17, 1, -1, -60, 0, 28, 0, 1, 0, 2, 2,
            3, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 6, 7, 4, 5, 1, 3, 8, 2, -1, -60, 0, 53, 16,
            0, 1, 4, 1, 2, 3, 4, 9, 2, 7, 1, 0, 0, 0, 0, 0, 1, 0, 2, 3, 4, 17, 5, 6, 18, 33,
            49, 19, 20, 65, 81, 7, 34, 50, 97, 113, -127, -111, -95, -79, 66, 82, 21, 35, 36, 98, -63, -47, -16, 22, -1, -60,
            0, 25, 1, 1, 0, 3, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 4, 5, 2, 1, -1,
            -60, 0, 35, 17, 1, 0, 2, 2, 1, 4, 2, 3, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 2, 3, 17, 4,
            18, 19, 33, 49, 65, 81, 5, 34, 50, -79, -1, -38, 0, 12, 3, 1, 0, 2, 17, 3, 17, 0, 63, 0, -68, 81,
            17, 1, 17, 16, 17, 17, 1, 17, 16, 17, 112, 74, -118, -18, -3, -41, 30, -108, -42, 84, -87, 43, 77, -71, 95, -63,
            -112, 11, -69, 63, -89, -113, -31, 121, 105, -120, -115, -53, -55, -76, 86, 55, 41, 52, -45, 71, 11, 11, -92, 112, 0, 15,
            -81, -63, 86, 58, -2, -23, -43, -59, -89, 74, 36, -102, -109, 90, 120, -94, -121, -121, -124, -122, -8, 113, 3, -44, -3, -106,
            1, -98, 87, 90, 109, -71, -98, 102, -103, -89, -117, -118, 83, -100, -97, 15, -70, -56, -73, -86, -9, -83, 30, -124, 90, -37,
            -97, 113, -44, -61, -92, -98, 76, 0, -23, 113, -105, 96, 17, -114, -72, 3, -63, 65, 25, 34, -1, 0, 58, 65, 55, -18,
            70, -94, 116, -33, -42, -12, -125, 19, 106, -42, 117, -54, 79, 14, 44, 111, 110, -26, -112, 48, -17, 18, -47, -27, -13, 83,
            74, 118, -96, -71, 90, 59, 21, 100, 108, -80, -56, -34, 38, 61, -89, 32, -123, 80, 111, 104, -24, 69, -89, 83, -67, -89,
            -57, 37, 113, 102, 6, 72, -22, -46, 28, -106, 23, 0, 64, 30, -15, -98, 125, 71, -110, 109, 77, 102, -26, -35, 101, 114,
            73, 124, 78, 107, 123, -60, 4, -14, 62, -15, -28, -32, -92, -22, -102, -1, 0, 79, 35, 37, -87, 109, 93, 115, 34, -57,
            -93, 110, 27, -43, 98, -77, 89, -31, -16, -54, -48, -26, -71, 100, 41, 22, 68, 68, 64, 68, 68, 4, 68, 64, 68, 68,
            4, 68, 64, 68, 68, 4, 68, 64, 68, 92, 20, 26, 45, -35, -85, 63, 76, -45, -61, 96, 56, -79, 57, -32, 97, -3,
            -93, -60, -4, -65, 36, 42, -28, -7, -109, -109, -26, 84, -109, 127, -56, 78, -79, 90, 51, -20, -74, -66, 71, -60, -72, -25,
            -16, 20, 111, -55, 80, -28, 90, 102, -38, 82, -51, 109, -37, 78, -101, 80, -66, 88, -117, 34, -99, -16, 19, -6, -40, 6,
            126, -21, -118, -79, 73, 13, 118, -57, 60, -58, 119, -127, -50, 66, 48, 74, -17, 43, -87, -17, 13, 105, -26, -95, -9, -31,
            22, -40, -105, 89, -37, -51, 31, 108, -25, 57, -79, -13, 104, 39, -95, 93, 19, -16, -128, 87, -51, -101, 35, -73, 99, 115,
            -41, 63, -123, -80, -81, -94, -51, 61, 94, -13, 110, 94, -17, 11, -122, 88, 49, -105, 56, 121, -29, -64, 43, 85, -84, -50,
            -74, 68, 91, 39, -120, 109, -67, 28, 110, 110, -31, -86, 51, 69, -74, -1, 0, -23, -19, -72, -9, 124, -2, -119, 113, -98,
            31, -127, 0, -4, -57, -67, 90, -53, -52, 90, -52, -50, -45, 117, -3, 62, 90, -46, -10, -115, -122, -60, 114, -73, -106, 15,
            19, 92, 8, 94, -99, 28, -64, 62, 106, -44, 120, -123, -36, 81, 104, -82, -91, -54, 34, 47, 82, -120, -120, -128, -120, -120,
            8, -120, -128, -120, -120, 8, -120, -128, -120, -120, 11, -123, -54, -7, 36, 32, -121, 122, 72, -45, -28, -109, 79, -113, 82, -127,
            -96, -70, -82, 123, 95, 62, -52, -8, -4, -118, -81, -94, -65, 25, 29, 84, -21, 127, -22, 110, 51, 69, -90, 48, -31, -128,
            9, 37, -9, -2, -48, 127, 63, 69, 28, -113, -47, -3, -67, 75, 78, 109, -22, 23, 25, 4, -46, 122, -62, 9, 90, 120,
            8, -16, -26, 57, -125, -14, 33, 85, -73, 69, -17, -91, 44, -75, -101, -33, -11, 106, 101, -66, -64, -34, 78, 11, 10, 123,
            -93, -128, -27, -64, 123, -41, 23, -10, 118, -14, -82, -14, -63, -92, 62, 81, -32, -8, 101, 99, -102, 126, -7, 88, 50, -20,
            -83, -31, 38, 4, -6, 85, -106, 49, -35, 72, -61, -66, -64, -107, -35, 112, -42, 28, -58, 11, -49, -73, -50, -125, 98, 45,
            67, 116, 87, -114, 83, -102, -15, -121, 61, -1, 0, -35, -127, -45, -16, -92, 91, -89, 113, -57, -62, -24, -29, 112, -58, 49,
            -127, -8, 90, -71, -74, -75, -83, 35, 78, -113, 20, 110, 65, 106, 71, 16, 109, -52, 67, 90, 71, -19, 12, -1, 0, -118,
            -41, -77, 109, -55, 52, -68, 86, -18, 58, 76, 126, -106, 55, 31, 115, -2, -108, -99, 84, -123, -102, -51, 49, 70, -102, 26,
            118, -94, -101, 115, -23, 82, 106, 60, 78, -84, -37, -111, 62, 108, 126, -48, -32, -67, 96, 58, 47, 56, -22, 122, 45, 126,
            -28, 105, -106, 6, -76, -73, -111, 3, -104, 62, 121, -13, 86, 118, -115, -23, 13, -82, 100, 113, -22, 53, -57, 32, 3, -91,
            -120, -13, -49, -97, 10, 69, -30, 92, -57, 38, -97, 62, 22, 10, 46, -86, -13, 71, 102, 22, 77, 11, -125, -30, -111, -95,
            -52, 112, -24, 65, 93, -85, -75, -127, 17, 16, 17, 17, 1, 17, 16, 17, 17, 1, 17, 16, 17, 17, 6, 22, -81, 125,
            -102, 117, 9, 109, 60, 23, 6, 116, 3, -60, -98, -118, -88, -36, 49, -22, 86, -101, -4, 69, -70, -91, -117, 32, -100, -56,
            -58, -71, -52, -20, 79, -71, -96, -14, 10, -44, -41, 41, -9, -3, 50, 122, -51, -10, -36, -36, -77, 63, -72, 115, 10, -78,
            107, -97, 4, -114, 24, 45, 120, -11, 92, -45, -8, 43, 55, -103, 124, -107, -68, 107, -45, 103, -15, 83, 90, -18, -1, 0,
            49, -2, 53, 20, 45, 88, -72, -46, -5, 83, 62, 105, 0, -31, -29, -112, -28, -32, 116, 25, -15, 87, 14, -121, -61, -4,
            58, -65, 15, 78, -51, -72, -6, 42, 98, 59, -112, -67, -110, -10, 33, -83, 107, 15, -83, -114, 64, 31, 16, -83, 45, -89,
            119, -76, -46, 41, -98, -83, 116, 45, 32, -4, 64, 82, 86, 38, 39, 114, -61, -66, 74, -28, -28, 100, -67, 35, 81, 50,
            -109, -124, 93, 45, -99, -66, 120, 95, 50, -38, -114, 54, 23, 57, -32, 1, -52, -109, -32, -84, 117, -58, -99, -19, -86, -34,
            116, -5, -34, -127, 99, 3, 47, -121, -7, -83, -7, 117, -5, 101, 86, 80, -128, -42, -105, -8, -107, 49, -41, 55, 121, 115,
            100, -125, 79, -115, -91, -92, 22, -103, 95, -52, 17, -18, 31, -19, 66, 44, 78, 35, -123, -39, -16, 10, -75, -25, -86, 124,
            41, 103, -102, -51, -73, 13, 102, -87, 99, 55, 99, 96, 62, 101, 97, -55, 104, 67, 46, 91, -29, -56, -123, -81, -75, 112,
            -66, -45, -26, 102, 94, 93, -22, 52, 14, 127, 28, 121, -84, 75, -52, -67, 8, 108, -106, 43, 73, 27, 9, -28, 79, -7,
            -14, -7, -85, 49, 93, 68, 35, -82, 61, -49, -105, -93, 118, 35, -98, -3, -99, -92, 62, 76, -15, 58, -77, 93, -49, -33,
            -51, 111, -106, -121, 97, -50, -21, 59, 51, 69, -103, -8, -53, -87, -57, -100, 124, 22, -7, 76, -48, -113, 16, 34, 34, 61,
            17, 17, 1, 17, 16, 17, 17, 1, 17, 16, 17, 17, 7, 4, 101, 65, -9, -42, -98, -56, 108, 67, 114, 38, 6, -10,
            -71, 108, -124, 116, 46, 29, 62, -39, -6, 41, -54, -61, -43, 40, 69, -87, 83, -110, -76, -29, -43, 112, -28, -31, -43, -89,
            -64, -123, 14, 124, 125, -54, 77, 83, -15, -77, 118, -78, 69, -108, 62, -65, -92, 127, 46, 73, -30, -78, -10, -57, 43, -8,
            -90, -119, -96, 12, 103, -56, -7, 41, 15, -93, -3, -57, 23, 116, 20, 75, -16, 106, 127, 45, -96, -100, -6, -96, 114, -25,
            -16, -4, 45, -74, -81, -24, -1, 0, 84, -44, 63, -91, -118, -36, 21, -29, -30, -55, -80, 65, 118, 91, -125, -53, -121, -49,
            -89, -120, -8, -87, 110, -97, -76, -12, -22, 91, 98, 29, 0, 49, -46, 87, -115, -104, -19, 14, 4, -123, -39, -49, 30, 124,
            -14, 114, -94, -61, 75, -50, 63, -35, 103, -103, 126, 60, -8, -59, 88, -33, -67, -79, -92, -44, -93, 120, -56, 63, 69, 31,
            -36, -70, -104, 48, 54, 6, 72, 65, 121, -53, -71, -8, 121, 45, 46, -29, -85, -82, 109, 71, -72, -40, 99, -19, -23, -7,
            -11, 45, -58, -34, 64, 121, 60, 15, 100, -3, -108, 67, 83, -36, -79, 91, 111, 19, 73, 118, 91, -6, 79, -48, -81, 123,
            118, -12, -53, -53, 19, -45, -31, -71, -44, 45, -55, 31, 11, 96, 107, -91, -111, -60, 53, -84, 111, 82, 86, 62, -89, -94,
            -22, 111, -85, -38, -38, -83, 105, -112, 99, 47, 33, -72, 24, -4, -31, 99, 109, -115, 71, -65, -37, 113, -107, -95, -77, 71,
            16, 32, -121, 117, -49, 35, -113, -5, -59, 75, 96, -69, 98, 14, 76, -111, -36, 39, -85, 73, -56, 42, -90, 108, -106, -59,
            110, -102, -75, 127, 23, -62, -57, -37, -18, 100, -92, 90, 103, -19, 19, -46, 27, 82, -89, 19, -56, 105, -80, 61, -114, 92,
            -102, -33, 114, -24, -44, -98, -21, -74, 32, -89, -57, -64, -21, 82, 54, 22, 100, 103, 5, -60, 12, -29, -26, -70, -73, 61,
            -104, -24, 94, 46, -120, 112, -15, 122, -51, 103, -106, 127, -62, -55, -12, 93, -92, -37, -36, -101, -50, -75, -55, 26, -29, 86,
            -117, -59, -119, -97, -113, 84, 17, -20, 51, -30, 78, 14, 60, -127, 87, 49, 71, 92, 69, -27, 83, -14, 60, 60, 88, -7,
            59, -59, -4, -3, 125, 61, 3, -92, -48, -121, 75, -45, 106, -23, -11, 70, 32, -83, 19, 98, -116, 123, -102, 48, -78, -47,
            21, -108, 34, 34, 32, 34, 34, 2, 34, 32, 34, 34, 2, 34, 32, 34, 34, 2, 34, 32, 34, 34, 14, 8, 7, -86,
            -45, 92, -38, 91, 118, -21, -52, -106, -76, 74, 18, 60, -11, 113, -127, -96, -97, -98, 22, -23, 16, 87, -101, -85, 108, 104,
            -6, 52, 85, -20, 105, 58, 85, 122, -50, 115, -53, 95, 44, 109, -25, -45, -112, -4, -88, -31, 56, 28, -6, 43, 122, -19,
            72, 110, -41, 125, 123, 12, 15, -115, -29, -104, 42, 35, 62, -57, -109, -73, -2, -98, -21, 123, 31, -17, 111, -84, 62, -99,
            126, -53, 63, -109, -57, -67, -81, -43, 86, -81, 11, -105, -113, 30, 62, -117, -8, 86, -38, 110, -35, -1, 0, -35, 110, -90,
            -57, 3, -29, -2, 27, -89, -16, 119, -39, 28, 78, 94, 9, 39, -123, -93, 30, 32, 17, -31, -114, -65, 27, -53, 74, -46,
            -24, -23, 53, 27, 87, 76, -85, 21, 90, -19, 36, -120, -30, 111, 8, -49, -97, -59, 97, -19, -115, -71, 71, 109, -46, 125,
            106, 45, 37, -46, -68, -55, 52, -81, -10, -92, 113, -15, 63, 46, 64, 45, -54, -69, -114, -67, 53, -118, -61, 63, 62, 94,
            -19, -26, -62, 34, 46, -47, 8, -120, -128, -120, -120, 8, -120, -128, -120, -120, 8, -120, -128, -120, -120, 8, -120, -128, -120, -120,
            8, -120, -128, -120, -120, 8, -120, -128, -120, -120, 8, -120, -128, -120, -120, 8, -120, -125, -1, -39};


    private static Bitmap bigYoshiBitmap;

    @BeforeClass
    public static void setUp() throws Exception {
        AppointmentReminderNotificationSetupListener.setIsNotificationSetterEnable(false);
        DatabaseFactory.setTest();
        AuthenticationFactory.setTest();
        UploadServiceFactory.setTest();
        FirebaseApp.clearInstancesForTest();
        FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext());
        Tasks.await(AuthenticationFactory.getAdaptedInstance().createUserWithEmailAndPassword("PictureEditActivityTest@gmail.com", "fakePassword"));

        //UploadServiceFactory.getAdaptedInstance().uploadImage(bigYoshi, "bigyoshi", l-> System.out.println("done"), l -> System.out.println("fail"));
        //Thread.sleep(5000);

        bigYoshiBitmap = BitmapFactory.decodeByteArray(bigYoshi, 0, bigYoshi.length);
    }

    public void bitmapMatcher(Bitmap bitmap) {
        allOf(withId(R.id.pictureEditPicture)).matches(new BaseMatcher<Object>() {
            @Override
            public void describeTo(Description description) {

            }

            @Override
            public boolean matches(Object item) {
                if (item instanceof DrawableImageView) {
                    return ((DrawableImageView) item).getAlteredPicture().equals(bitmap);
                }
                return false;
            }
        });
    }

    public void strokeWidthMatcher(float expectedStrokeWidth) {
        allOf(withId(R.id.pictureEditPicture)).matches(new BaseMatcher<Object>() {
            @Override
            public void describeTo(Description description) {

            }

            @Override
            public boolean matches(Object item) {
                if (item instanceof DrawableImageView) {
                    return ((DrawableImageView) item).getCurrentStrokeWidth() == expectedStrokeWidth;
                }
                return false;
            }
        });
    }

    public void colorMatcher(int expectedColorId) {
        allOf(withId(R.id.pictureEditPicture)).matches(new BaseMatcher<Object>() {
            @Override
            public void describeTo(Description description) {

            }

            @Override
            public boolean matches(Object item) {
                if (item instanceof DrawableImageView) {
                    return ((DrawableImageView) item).getCurrentColorId() == expectedColorId;
                }
                return false;
            }
        });
    }

    @Test
    public void bigYoshiIsCorrectlyDisplayed() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), PictureEditActivity.class);
        intent.putExtra(PictureEditActivity.PICTURE_BYTES_KEY, bigYoshi);

        try (ActivityScenario<PictureEditActivity> ignored = ActivityScenario.launch(intent)){
            bitmapMatcher(bigYoshiBitmap);
        }
    }

    private Bitmap applyColorFilter(ColorMatrix matrix) {
        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(matrix));

        Bitmap newPicture = Bitmap.createBitmap(bigYoshiBitmap.getWidth(), bigYoshiBitmap.getHeight(), bigYoshiBitmap.getConfig());
        Canvas canvas = new Canvas(newPicture);
        canvas.drawBitmap(bigYoshiBitmap, 0, 0, paint);

        return newPicture;
    }

    @Test
    public void filtersAreCorrectlyApplied() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), PictureEditActivity.class);
        intent.putExtra(PictureEditActivity.PICTURE_BYTES_KEY, bigYoshi);

        try (ActivityScenario<PictureEditActivity> ignored = ActivityScenario.launch(intent)){
            clickRadioButtonItem(R.id.pictureEditFilters, R.id.pictureEditBinary);
            bitmapMatcher(applyColorFilter(Filters.binaryFilter()));
            clickRadioButtonItem(R.id.pictureEditFilters, R.id.pictureEditSepia);
            bitmapMatcher(applyColorFilter(Filters.sepiaFilter()));
            clickRadioButtonItem(R.id.pictureEditFilters, R.id.pictureEditInvert);
            bitmapMatcher(applyColorFilter(Filters.invertFilter()));
            clickRadioButtonItem(R.id.pictureEditFilters, R.id.pictureEditNormal);
            bitmapMatcher(bigYoshiBitmap);
        }
    }

    @Test
    public void colorsAreCorrectlyApplied() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), PictureEditActivity.class);
        intent.putExtra(PictureEditActivity.PICTURE_BYTES_KEY, bigYoshi);

        try (ActivityScenario<PictureEditActivity> ignored = ActivityScenario.launch(intent)){
            clickRadioButtonItem(R.id.pictureEditColors, R.id.pictureEditBlue);
            colorMatcher(R.color.blue);
            clickRadioButtonItem(R.id.pictureEditColors, R.id.pictureEditRed);
            colorMatcher(R.color.red);
            clickRadioButtonItem(R.id.pictureEditColors, R.id.pictureEditYellow);
            colorMatcher(R.color.yellow);
            clickRadioButtonItem(R.id.pictureEditColors, R.id.pictureEditGreen);
            colorMatcher(R.color.green);
            clickRadioButtonItem(R.id.pictureEditColors, R.id.pictureEditBlack);
            colorMatcher(R.color.black);
        }
    }

    @Test
    public void strokeWidthIsCorrectlySet() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), PictureEditActivity.class);
        intent.putExtra(PictureEditActivity.PICTURE_BYTES_KEY, bigYoshi);

        try (ActivityScenario<PictureEditActivity> ignored = ActivityScenario.launch(intent)){
            setProgressTo(R.id.pictureEditStrokeWidthBar, 0);
            strokeWidthMatcher(0);
            setProgressTo(R.id.pictureEditStrokeWidthBar, 5);
            strokeWidthMatcher(5);
            setProgressTo(R.id.pictureEditStrokeWidthBar, 45);
            strokeWidthMatcher(45);
            setProgressTo(R.id.pictureEditStrokeWidthBar, 75);
            strokeWidthMatcher(75);
            setProgressTo(R.id.pictureEditStrokeWidthBar, 100);
            strokeWidthMatcher(100);
        }
    }

    @Test
    public void resetButtonWorks() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), PictureEditActivity.class);
        intent.putExtra(PictureEditActivity.PICTURE_BYTES_KEY, bigYoshi);

        try (ActivityScenario<PictureEditActivity> ignored = ActivityScenario.launch(intent)){
            clickRadioButtonItem(R.id.pictureEditFilters, R.id.pictureEditSepia);
            clickOn(R.id.pictureEditResetButton);
            strokeWidthMatcher(0);
            colorMatcher(R.color.black);
            bitmapMatcher(bigYoshiBitmap);
        }
    }

    @Test
    public void returnedPictureIsTheAlteredPicture() throws InterruptedException {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), PictureEditActivity.class);
        intent.putExtra(PictureEditActivity.PICTURE_BYTES_KEY, bigYoshi);

        ActivityScenario<PictureEditActivity> scenario = ActivityScenario.launch(intent);
        clickRadioButtonItem(R.id.pictureEditFilters, R.id.pictureEditSepia);
        clickOn(R.id.pictureEditDoneButton);

        assertThat(scenario.getResult(), hasResultCode(Activity.RESULT_OK));
        assertThat(scenario.getResult().getResultData(), IntentMatchers.hasExtraWithKey("data"));
        assertNotEquals(scenario.getResult().getResultData().getByteArrayExtra("data"), bigYoshi);
        Thread.sleep(4000);
        scenario.close();
    }



}
