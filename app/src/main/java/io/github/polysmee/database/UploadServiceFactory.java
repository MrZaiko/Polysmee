package io.github.polysmee.database;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

public final class UploadServiceFactory {

    private UploadServiceFactory(){}

    private static boolean isTest = false;

    public static FirebaseStorage getAdaptedInstance(){
        if(isTest) {
            throw new IllegalStateException("not implemented");
        } else {
            return FirebaseStorage.getInstance();
        }
    }

    public static void setTest() {
        isTest = true;
    }
}
