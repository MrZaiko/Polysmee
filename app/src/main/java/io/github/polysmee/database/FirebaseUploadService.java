package io.github.polysmee.database;

import android.content.Context;
import android.os.Build;

import androidx.annotation.NonNull;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Paths;

import io.github.polysmee.database.databaselisteners.valuelisteners.DownloadValueListener;
import io.github.polysmee.database.databaselisteners.valuelisteners.LoadValueListener;

import static java.nio.file.Files.readAllBytes;

public final class FirebaseUploadService implements UploadService {

    @Override
    public void uploadImage(@NonNull byte[] data, String fileName, LoadValueListener onSuccess, LoadValueListener onFailure, Context ctx) {
        String imageName = "" + System.currentTimeMillis() + fileName;
        addNewFileToCache(imageName, data, ctx);
        StorageReference ref = FirebaseStorage.getInstance().getReference().child(imageName);
        ref
            .putBytes(data)
            .addOnSuccessListener(taskSnapshot -> onSuccess.onDone(imageName))
            .addOnFailureListener(ignored -> onFailure.onDone(ignored.getMessage()));
    }

    @Override
    public void downloadImage(String id, DownloadValueListener dvl, LoadValueListener fl, Context ctx) {
        byte[] data = null;
        try {
            File fi = new File(ctx.getCacheDir(), id);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && fi.exists())
                data = readAllBytes(Paths.get(fi.getPath()));
        } catch (IOException ignored) {}

        if(data != null)
            dvl.onDone(data);
        else {
            StorageReference ref = FirebaseStorage.getInstance().getReference().child(id);
            ref
                .getBytes(1024L * 1024L * 20L)
                .addOnSuccessListener(s -> { addNewFileToCache(id, s, ctx); dvl.onDone(s); })
                .addOnFailureListener(exc -> fl.onDone(exc.getMessage()));
        }
    }

    @Override
    public void deleteImage(String id, LoadValueListener onSuccess, LoadValueListener onFailure, Context ctx) {
        StorageReference ref = FirebaseStorage.getInstance().getReference().child(id);
        ref
            .delete()
            .addOnSuccessListener(vo_id -> onSuccess.onDone(id))
            .addOnFailureListener(exc -> onFailure.onDone(exc.getMessage()));
    }

    private void addNewFileToCache(String name, byte[] data, Context ctx) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) //cache does not work for api below 26, it will cache miss at 100%
            try(OutputStream os = new FileOutputStream(new File(ctx.getCacheDir(), name))) {
                os.write(data);
            } catch (IOException ignored) {}
    }
}
