package io.github.polysmee.room.fragments;

import android.content.Context;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class HelperImages {

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

    public static void showToast(String message, Context context) {
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, message, duration);
        toast.show();
    }

}
