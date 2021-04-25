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
        AtomicReference<String> id = new AtomicReference<>(null);
        us.uploadImage(
                new byte[]{2,3,4},
                "nums",
                (name) -> {
                    id.set(name);
                    assertTrue(name.contains("nums"));
                },
                (exc) -> {throw new IllegalStateException("crashed in test lol");}
        );
        us.downloadImage(
                "nums",
                (gotten) -> assertEquals(gotten, new byte[]{2,3,4}),
                (exc) -> {throw new IllegalStateException("failed in test lmao");}
        );
        us.deleteImage(
                id.get(),
                (name) -> assertEquals(name, id.get()),
                (exc) -> {throw new IllegalStateException("crashed in test lol");}
        );
    }
}
