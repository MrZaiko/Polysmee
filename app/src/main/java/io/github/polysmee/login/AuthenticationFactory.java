package io.github.polysmee.login;

import com.google.firebase.auth.FirebaseAuth;

import static io.github.polysmee.BuildConfig.DEBUG;

public final class AuthenticationFactory {
    private AuthenticationFactory(){}

    private static final boolean isTest = DEBUG;

    public static FirebaseAuth getAdaptedInstance(){
        if(isTest) {
            FirebaseAuth fb = FirebaseAuth.getInstance();
            fb.useEmulator("10.0.2.2", 9099);
            return fb;
        } else {
            return FirebaseAuth.getInstance();
        }
    }
}
