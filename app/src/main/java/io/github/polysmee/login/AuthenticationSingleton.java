package io.github.polysmee.login;

import com.google.firebase.auth.FirebaseAuth;

public final class AuthenticationSingleton {
    private AuthenticationSingleton() {
    }

    private static boolean runLocally = false;
    private static FirebaseAuth firebaseAuth = null;

    public static FirebaseAuth getAdaptedInstance() {
        if (firebaseAuth == null){
            if (runLocally) {
                FirebaseAuth fb = FirebaseAuth.getInstance();
                fb.useEmulator("10.0.2.2", 9099);
                firebaseAuth = fb;
            } else {
                firebaseAuth = FirebaseAuth.getInstance();
            }
        }
        return firebaseAuth;
    }

    public static void setLocal() {
        runLocally = true;
        firebaseAuth = null;
    }
}
