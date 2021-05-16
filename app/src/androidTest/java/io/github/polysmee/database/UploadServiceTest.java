package io.github.polysmee.database;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class UploadServiceTest {
    @Test
    public void uploadDownloadDelete() {
        UploadServiceFactory.setTest(true);
        UploadService us = UploadServiceFactory.getAdaptedInstance();
        us.uploadImage(
                new byte[]{2, 3, 4},
                "nums",
                (name) -> assertTrue(name.contains("nums")),
                (exc) -> {
                }, getApplicationContext()
        );
        us.downloadImage(
                "nums",
                (gotten) -> assertArrayEquals(gotten, new byte[]{2, 3, 4}),
                (exc) -> {
                }, getApplicationContext()
        );
        us.deleteImage(
                "nums",
                (name) -> assertEquals(name, "nums"),
                (exc) -> {
                }, getApplicationContext()
        );

        UploadServiceFactory.setTest(false);
        us = UploadServiceFactory.getAdaptedInstance();

        UploadService finalUs = us;
        UploadService finalUs1 = us;

        us.uploadImage(
                new byte[]{2, 3, 4},
                "nums",
                (name) -> finalUs.downloadImage(
                    name,
                    (gotten) -> finalUs1.deleteImage(
                        "nums",
                        (nam) -> assertEquals(name, nam),
                        (exc) -> {throw new IllegalStateException("crashed in test 2");
                        }, getApplicationContext()
                    ),
                    (exc) -> {throw new IllegalStateException("crashed in test 3");
                    }, getApplicationContext()
                ),
                (exc) -> {throw new IllegalStateException("crashed in test 4");
                }, getApplicationContext()
        );
    }

    @Test
    public void exceptionOnDataNotFound(){
        UploadServiceFactory.setTest(false);
        UploadService us = UploadServiceFactory.getAdaptedInstance();

        us.downloadImage(
            "non-existent image",
            (data) -> {throw new IllegalStateException("should not be able to find data");},
            (exc) -> {},
            getApplicationContext()
        );
    }
}
