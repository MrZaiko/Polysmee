package io.github.polysmee.database;

import com.google.firebase.database.FirebaseDatabase;

public final class DatabaseSingleton {
    private DatabaseSingleton() {
    }
    private static FirebaseDatabase firebaseDatabase=null;
    private static boolean runLocally = false;

    /**
     * @return a firebase database that may use a local emulator or not,
     * depending on state.
     */
    public static FirebaseDatabase getAdaptedInstance() {
        if(firebaseDatabase==null){
            if (runLocally) {
                FirebaseDatabase fb = FirebaseDatabase.getInstance();
                fb.useEmulator("10.0.2.2", 9000);
                firebaseDatabase = fb;
            } else {
                firebaseDatabase = FirebaseDatabase.getInstance();
            }
        }
        return firebaseDatabase;
    }

    public static void setLocal() {
        runLocally = true;
        firebaseDatabase = null;
    }
}