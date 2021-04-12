package io.github.polysmee.database;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

public final class UploadServiceFactory {

    private UploadServiceFactory(){}

    private static boolean isTest = false;

    public static UploadService getAdaptedInstance(){
        if(isTest) {
            return new LocalUploadService();
        } else {
            return new FirebaseUploadService();
        }
    }

    public static void setTest() {
        isTest = true;
    }
}
