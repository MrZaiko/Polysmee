package io.github.polysmee.database;

import android.content.Context;

import io.github.polysmee.database.databaselisteners.valuelisteners.DownloadValueListener;
import io.github.polysmee.database.databaselisteners.valuelisteners.LoadValueListener;

public interface UploadService {


    /**
     * @param data      the byte array to upload on the service
     * @param fileName  the name of the picture
     * @param onSuccess a listener that will be triggered if the transfer is successful. It
     *                  will be given the id of the file on the server as an argument
     * @param onFailure a listener that will be triggered if the transfer fails. It
     *                  will be given the message of the error as an argument
     */
    void uploadImage(byte[] data, String fileName, LoadValueListener onSuccess, LoadValueListener onFailure, Context ctx);

    /**
     * @param id        the id of the file to retrieve
     * @param onSuccess a listener that will be run if the query is successful, will receive a byte array
     * @param onFailure a listener that will be triggered if the transfer fails. It
     *                  will be given the message of the error as an argument
     */
    void downloadImage(String id, DownloadValueListener onSuccess, LoadValueListener onFailure, Context ctx);

    /**
     * @param id        the id of the file to delete
     * @param onSuccess a listener that will be run if the deletion is successful.
     *                  It will be given the id of the file on the server as an argument
     * @param onFailure a listener that will be triggered if the deletion fails (who knows, right).
     *                  It will be given the message of the error as an argument
     */
    void deleteImage(String id, LoadValueListener onSuccess, LoadValueListener onFailure, Context ctx);
}
