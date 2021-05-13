package io.github.polysmee.database;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class UploadServiceTest {
    @Test
    public void uploadDownloadDelete() throws InterruptedException {
        UploadServiceFactory.setTest(true);
        UploadService us = UploadServiceFactory.getAdaptedInstance();
        us.uploadImage(
                new byte[]{2,3,4},
                "nums",
                (name) -> assertTrue(name.contains("nums")),
                (exc) -> {}
        );
        us.downloadImage(
                "nums",
                (gotten) -> assertArrayEquals(gotten, new byte[]{2,3,4}),
                (exc) -> {}
        );
        us.deleteImage(
                "nums",
                (name) -> assertEquals(name, "nums"),
                (exc) -> {}
        );

        UploadServiceFactory.setTest(false);
        us = UploadServiceFactory.getAdaptedInstance();
        us.uploadImage(
            new byte[]{2,3,4},
            "nums",
            (name) -> assertTrue(name.contains("nums")),
            (exc) -> {}
        );
        us.downloadImage(
            "nums",
            (gotten) -> assertArrayEquals(gotten, new byte[]{2,3,4}),
            (exc) -> {}
        );
        us.deleteImage(
            "nums",
            (name) -> assertEquals(name, "nums"),
            (exc) -> {}
        );
    }
}
