package io.github.polysmee.login;

import com.google.firebase.auth.FirebaseAuth;

public final class AuthenticationFactory {
    private AuthenticationFactory() {
    }

    private static boolean isTest = false;

    public static FirebaseAuth getAdaptedInstance() {
        if (isTest) {
            FirebaseAuth fb = FirebaseAuth.getInstance();
            fb.useEmulator("10.0.2.2", 9099);
            return fb;
        } else {
            return FirebaseAuth.getInstance();
        }
    }

    public static void setTest() {
        isTest = true;
    }
}
