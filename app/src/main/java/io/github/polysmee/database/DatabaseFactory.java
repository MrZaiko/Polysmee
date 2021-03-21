package io.github.polysmee.database;

import com.google.firebase.database.FirebaseDatabase;

import static io.github.polysmee.BuildConfig.DEBUG;

public final class DatabaseFactory {
    private DatabaseFactory(){}

    private static final boolean isTest = DEBUG;

    public static FirebaseDatabase getAdaptedInstance(){
        if(isTest) {
            FirebaseDatabase fb = FirebaseDatabase.getInstance();
            fb.useEmulator("localhost", 8080);
            return fb;
        } else {
            return FirebaseDatabase.getInstance();
        }
    }
}