package io.github.polysmee.login;

import com.google.firebase.auth.FirebaseAuth;

public final class AuthenticationSingleton {
    private AuthenticationSingleton() {
    }

    private static boolean runLocally = false;

    public static FirebaseAuth getAdaptedInstance() {
        if (runLocally) {
            FirebaseAuth fb = FirebaseAuth.getInstance();
            fb.useEmulator("10.0.2.2", 9099);
            return fb;
        } else {
            return FirebaseAuth.getInstance();
        }
    }

    public static void setLocal() {
        runLocally = true;
    }
}