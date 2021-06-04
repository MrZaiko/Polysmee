package io.github.polysmee.database;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;

import io.github.polysmee.database.databaselisteners.valuelisteners.DownloadValueListener;
import io.github.polysmee.database.databaselisteners.valuelisteners.LoadValueListener;

public class LocalUploadService implements UploadService {
    static Map<String, byte[]> hash = new HashMap<>();

    @Override
    public void uploadImage(byte[] data, String fileName, LoadValueListener onSuccess, LoadValueListener onFailure, Context ctx) {
        hash.put(fileName, data);
        onSuccess.onDone(fileName);
    }

    @Override
    public void downloadImage(String id, DownloadValueListener onSuccess, LoadValueListener onFailure, Context ctx) {
        onSuccess.onDone(hash.get(id));
    }

    @Override
    public void deleteImage(String id, LoadValueListener onSuccess, LoadValueListener onFailure, Context ctx) {
        if (hash.remove(id) == null)
            onFailure.onDone("file not found");
        else
            onSuccess.onDone(id);
    }
}
