package io.github.polysmee.database;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class UploadServiceTest {
    @Test
    public void uploadDownloadDelete() {
        UploadServiceFactory.setTest();
        UploadService us = UploadServiceFactory.getAdaptedInstance();
        us.uploadImage(
                new byte[]{2,3,4},
                "nums",
                (name) -> assertTrue(name.contains("nums")),
                (exc) -> {throw new IllegalStateException("crashed in test lol");}
        );
        us.downloadImage(
                "nums",
                (gotten) -> assertEquals(gotten, new byte[]{2,3,4}),
                (exc) -> {throw new IllegalStateException("failed in test lmao");}
        );
        us.deleteImage(
                "nums",
                (name) -> assertEquals(name, "nums"),
                (exc) -> {throw new IllegalStateException("crashed in test lol");}
        );
    }
}
