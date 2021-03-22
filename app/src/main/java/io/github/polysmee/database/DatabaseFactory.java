package io.github.polysmee.database;

import com.google.firebase.database.FirebaseDatabase;

import static io.github.polysmee.BuildConfig.DEBUG;

public final class DatabaseFactory {
    private DatabaseFactory(){}

    private static boolean isTest = false;

    public static FirebaseDatabase getAdaptedInstance(){
        if(isTest) {
            FirebaseDatabase fb = FirebaseDatabase.getInstance();
            fb.useEmulator("10.0.2.2", 9000);
            return fb;
        } else {
            return FirebaseDatabase.getInstance();
        }
    }

    public static void setTest() {
        isTest = true;
    }
}