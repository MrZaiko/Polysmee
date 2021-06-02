package io.github.polysmee.room.fragments;

import android.content.Context;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class HelperImages {

    /**
     * Gets bytes from an input stream and returns them in an array
     * @param inputStream the provided input stream
     * @return the bytes obtained from the input stream, stored in an array
     * @throws IOException possibly thrown by the read on the input stream
     */
    public static byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    /**
     * Displays a message we want to show the user as an android toast
     * @param message the provided message
     * @param context a context is required to create the toast
     */
    public static void showToast(String message, Context context) {
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, message, duration);
        toast.show();
    }

}
