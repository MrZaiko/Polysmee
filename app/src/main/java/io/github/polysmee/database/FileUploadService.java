package io.github.polysmee.database;

import androidx.annotation.NonNull;

import com.google.firebase.storage.StorageReference;

import java.io.File;

import io.github.polysmee.database.databaselisteners.DownloadValueListener;
import io.github.polysmee.database.databaselisteners.LoadValueListener;

public final class FileUploadService {
    private FileUploadService(){}

    public static void uploadImage(@NonNull byte[] data, String fileName, LoadValueListener onSuccess, LoadValueListener onFailure) {
        String imageName = "" + System.currentTimeMillis() + fileName;
        StorageReference ref = UploadServiceFactory.getAdaptedInstance().getReference().child(imageName);
        ref.putBytes(data)
                .addOnSuccessListener(taskSnapshot -> onSuccess.onDone(imageName))
                .addOnFailureListener(ignored      -> onFailure.onDone(ignored.getMessage()));
    }

    public static void downloadImage(String id, DownloadValueListener dvl, LoadValueListener fl) {
        StorageReference ref = UploadServiceFactory.getAdaptedInstance().getReference().child(id);
        ref.getBytes(1024L*1024L*20L)
            .addOnSuccessListener(dvl::onDone)
            .addOnFailureListener(ignored -> fl.onDone(ignored.getMessage()));
    }
}
