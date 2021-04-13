package io.github.polysmee.database;

import java.util.HashMap;
import java.util.Map;

import io.github.polysmee.database.databaselisteners.DownloadValueListener;
import io.github.polysmee.database.databaselisteners.LoadValueListener;

public class LocalUploadService implements UploadService {
    Map<String, byte[]> hash = new HashMap<>();

    @Override
    public void uploadImage(byte[] data, String fileName, LoadValueListener onSuccess, LoadValueListener onFailure) {
        String newName = "" + System.currentTimeMillis() + fileName;
        hash.put(newName, data);
        onSuccess.onDone(newName);
    }

    @Override
    public void downloadImage(String id, DownloadValueListener onSuccess, LoadValueListener onFailure) {
        onSuccess.onDone(hash.get(id));
    }
}
