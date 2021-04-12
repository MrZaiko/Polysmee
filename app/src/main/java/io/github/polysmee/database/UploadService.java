package io.github.polysmee.database;

import io.github.polysmee.database.databaselisteners.DownloadValueListener;
import io.github.polysmee.database.databaselisteners.LoadValueListener;

public interface UploadService {

    void uploadImage(byte[] data, String fileName, LoadValueListener onSuccess, LoadValueListener onFailure);

    void downloadImage(String id, DownloadValueListener onSuccess, LoadValueListener onFailure);
}
