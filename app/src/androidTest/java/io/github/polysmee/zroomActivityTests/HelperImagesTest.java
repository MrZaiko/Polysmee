package io.github.polysmee.zroomActivityTests;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import io.github.polysmee.room.fragments.HelperImages;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class HelperImagesTest {
    private static final String username1 = "Frez";

    @Test
    public void getBytesTest() {
        byte[] input = new byte[1024];
        int i = 0;
        while (i < input.length) {
            input[i] = (byte) (i % 256);
            i += 1;
        }
        InputStream inputStream = new ByteArrayInputStream(input);
        try {
            byte[] result = HelperImages.getBytes(inputStream);
            for (int j = 0; j < result.length; ++j) {
                assertEquals(input[j], result[j]);
            }
        } catch (IOException e) {

        }
    }


}
