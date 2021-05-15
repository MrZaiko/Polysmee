package io.github.polysmee.database;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import io.github.polysmee.database.databaselisteners.DownloadValueListener;
import io.github.polysmee.database.databaselisteners.LoadValueListener;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;

public final class FirebaseUploadService implements UploadService {

    @Override
    public void uploadImage(@NonNull byte[] data, String fileName, LoadValueListener onSuccess, LoadValueListener onFailure) {
        String imageName = "" + System.currentTimeMillis() + fileName;

        try {
            addNewFile(imageName, data);
        } catch (IOException ignored) {} //silent failure if the cache cannot store more data

        StorageReference ref = FirebaseStorage.getInstance().getReference().child(imageName);
        ref
                .putBytes(data)
                .addOnSuccessListener(taskSnapshot -> onSuccess.onDone(imageName))
                .addOnFailureListener(ignored -> onFailure.onDone(ignored.getMessage()));
    }

    @Override
    public void downloadImage(String id, DownloadValueListener dvl, LoadValueListener fl) {
        byte[] data = getNewFile(id);
        if(data != null)
            dvl.onDone(data);
        else {
            StorageReference ref = FirebaseStorage.getInstance().getReference().child(id);
            ref
                    .getBytes(1024L * 1024L * 20L)
                    .addOnSuccessListener(dvl::onDone)
                    .addOnFailureListener(exc -> fl.onDone(exc.getMessage()));
        }
    }

    @Override
    public void deleteImage(String id, LoadValueListener onSuccess, LoadValueListener onFailure) {
        StorageReference ref = FirebaseStorage.getInstance().getReference().child(id);
        ref
                .delete()
                .addOnSuccessListener(vo_id -> onSuccess.onDone(id))
                .addOnFailureListener(exc -> onFailure.onDone(exc.getMessage()));
    }

    private void addNewFile(String name, byte[] data) throws IOException {
        ContentValues cv = new ContentValues();
        cv.put(MediaStore.MediaColumns.DISPLAY_NAME, name);
        cv.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DCIM);

        ContentResolver resolver = getApplicationContext().getContentResolver();
        Uri uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv);

        try(OutputStream os = resolver.openOutputStream(uri)){
            os.write(data);
        }
    }

    private byte[] getNewFile(String name) {
        ContentValues cv = new ContentValues();
        cv.put(MediaStore.MediaColumns.DISPLAY_NAME, name);
        cv.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg");
        cv.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DCIM);

        ContentResolver resolver = getApplicationContext().getContentResolver();
        Uri uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv);
        byte[] ret = null;

        try(InputStream is = resolver.openInputStream(uri)){
            is.read(ret);
        } catch (IOException ignored) {}

        return ret;
    }
}
