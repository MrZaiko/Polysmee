package io.github.polysmee.database;

import com.google.firebase.database.FirebaseDatabase;

public final class DatabaseSingleton {
    private DatabaseSingleton() {
    }

    private static FirebaseDatabase fb = null;

    private static boolean isTest = false;

    /**
     * @return a firebase database that may use a local emulator or not,
     * depending on state.
     */
    public static FirebaseDatabase getAdaptedInstance() {
        if(fb != null)
            return fb;
        if (isTest) {
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