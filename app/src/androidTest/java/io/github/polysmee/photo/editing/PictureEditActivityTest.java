package io.github.polysmee.photo.editing;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.net.Uri;

import androidx.core.content.FileProvider;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.NoActivityResumedException;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.intent.matcher.IntentMatchers;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.FirebaseApp;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import io.github.polysmee.BigYoshi;
import io.github.polysmee.R;
import io.github.polysmee.database.DatabaseFactory;
import io.github.polysmee.database.UploadServiceFactory;
import io.github.polysmee.login.AuthenticationFactory;
import io.github.polysmee.znotification.AppointmentReminderNotificationSetupListener;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.contrib.ActivityResultMatchers.hasResultCode;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasData;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static com.schibsted.spain.barista.interaction.BaristaClickInteractions.clickBack;
import static com.schibsted.spain.barista.interaction.BaristaClickInteractions.clickOn;
import static com.schibsted.spain.barista.interaction.BaristaDialogInteractions.clickDialogPositiveButton;
import static com.schibsted.spain.barista.interaction.BaristaRadioButtonInteractions.clickRadioButtonItem;
import static com.schibsted.spain.barista.interaction.BaristaScrollInteractions.scrollTo;
import static com.schibsted.spain.barista.interaction.BaristaSeekBarInteractions.setProgressTo;
import static com.schibsted.spain.barista.interaction.BaristaSleepInteractions.sleep;
import static org.hamcrest.CoreMatchers.allOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@RunWith(JUnit4.class)
public class PictureEditActivityTest {

    private final static byte[] bigYoshi = BigYoshi.getBytes();

    private static Bitmap bigYoshiBitmap;
    private static Uri bigYoshiUri;

    @BeforeClass
    public static void setUp() throws Exception {
        AppointmentReminderNotificationSetupListener.setIsNotificationSetterEnable(false);
        DatabaseFactory.setTest();
        AuthenticationFactory.setTest();
        UploadServiceFactory.setTest(true);
        FirebaseApp.clearInstancesForTest();
        FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext());
        Tasks.await(AuthenticationFactory.getAdaptedInstance().createUserWithEmailAndPassword("PictureEditActivityTest@gmail.com", "fakePassword"));

        //UploadServiceFactory.getAdaptedInstance().uploadImage(bigYoshi, "bigyoshi", l-> System.out.println("done"), l -> System.out.println("fail"));
        //Thread.sleep(5000);

        bigYoshiBitmap = BitmapFactory.decodeByteArray(bigYoshi, 0, bigYoshi.length);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bigYoshiBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        File photoFile = null;
        try {
            photoFile = FileHelper.createImageFile(ApplicationProvider.getApplicationContext());
            try (FileOutputStream fileOutputStream = new FileOutputStream(photoFile)) {
                fileOutputStream.write(byteArray);
                fileOutputStream.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        bigYoshiUri = FileProvider.getUriForFile(ApplicationProvider.getApplicationContext(),
                "com.example.android.fileprovider", photoFile);
    }


    @AfterClass
    public static void clean() {
        DatabaseFactory.getAdaptedInstance().getReference().setValue(null);
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
        intent.putExtra(PictureEditActivity.PICTURE_URI, bigYoshiUri);

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
        intent.putExtra(PictureEditActivity.PICTURE_URI, bigYoshiUri);

        try (ActivityScenario<PictureEditActivity> ignored = ActivityScenario.launch(intent)){
            scrollTo(R.id.pictureEditBinary);
            clickRadioButtonItem(R.id.pictureEditFilters, R.id.pictureEditBinary);
            bitmapMatcher(applyColorFilter(Filters.binaryFilter()));

            scrollTo(R.id.pictureEditSepia);
            clickRadioButtonItem(R.id.pictureEditFilters, R.id.pictureEditSepia);
            bitmapMatcher(applyColorFilter(Filters.sepiaFilter()));

            scrollTo(R.id.pictureEditInvert);
            clickRadioButtonItem(R.id.pictureEditFilters, R.id.pictureEditInvert);
            bitmapMatcher(applyColorFilter(Filters.invertFilter()));

            scrollTo(R.id.pictureEditNormal);
            clickRadioButtonItem(R.id.pictureEditFilters, R.id.pictureEditNormal);
            bitmapMatcher(bigYoshiBitmap);
        }
    }

    @Test
    public void colorsAreCorrectlyApplied() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), PictureEditActivity.class);
        intent.putExtra(PictureEditActivity.PICTURE_URI, bigYoshiUri);

        try (ActivityScenario<PictureEditActivity> ignored = ActivityScenario.launch(intent)){
            clickOn(R.id.pictureEditColorPicker);
            sleep(1, TimeUnit.SECONDS);
            //clickOn("Choose");
            colorMatcher(Color.RED);
        }
    }

    @Test
    public void backButtonCancelTheActivity() throws InterruptedException {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), PictureEditActivity.class);
        intent.putExtra(PictureEditActivity.PICTURE_URI, bigYoshiUri);

        ActivityScenario<PictureEditActivity> scenario = ActivityScenario.launch(intent);

        try {
            pressBack();
            fail("Should have thrown NoActivityResumedException");
        } catch (NoActivityResumedException expected) {
        }

        assertThat(scenario.getResult(), hasResultCode(Activity.RESULT_CANCELED));

        Thread.sleep(4000);

        scenario.close();
    }

    @Test
    public void strokeWidthIsCorrectlySet() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), PictureEditActivity.class);
        intent.putExtra(PictureEditActivity.PICTURE_URI, bigYoshiUri);

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
        intent.putExtra(PictureEditActivity.PICTURE_URI, bigYoshiUri);

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
        intent.putExtra(PictureEditActivity.PICTURE_URI, bigYoshiUri);

        ActivityScenario<PictureEditActivity> scenario = ActivityScenario.launch(intent);
        clickRadioButtonItem(R.id.pictureEditFilters, R.id.pictureEditSepia);
        clickOn(R.id.pictureEditDoneButton);

        assertThat(scenario.getResult(), hasResultCode(Activity.RESULT_OK));
        assertThat(scenario.getResult().getResultData(), IntentMatchers.hasExtraWithKey("data"));
        //The result is not the same file
        assertNotEquals(scenario.getResult().getResultData().getData(), bigYoshiUri);
        Thread.sleep(4000);
        scenario.close();
    }



}
