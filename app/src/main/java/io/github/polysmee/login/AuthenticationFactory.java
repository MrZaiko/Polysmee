package io.github.polysmee.login;

import com.google.firebase.auth.FirebaseAuth;

import static io.github.polysmee.BuildConfig.DEBUG;

public final class AuthenticationFactory {
    private AuthenticationFactory(){}

    private static final boolean isTest = DEBUG;

    public static FirebaseAuth getAdaptedInstance(){
        if(isTest) {
            FirebaseAuth fb = FirebaseAuth.getInstance();
            fb.useEmulator("localhost", 8080);
            return fb;
        } else {
            return FirebaseAuth.getInstance();
        }
    }
}
