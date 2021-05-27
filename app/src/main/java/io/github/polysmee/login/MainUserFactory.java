package io.github.polysmee.login;

import com.google.firebase.auth.FirebaseAuth;

import io.github.polysmee.database.DatabaseUser;
import io.github.polysmee.database.User;

public class MainUserFactory {

    public static User getInstance() {
        return new DatabaseUser(AuthenticationFactory.getAdaptedInstance().getCurrentUser().getUid());
    }

    public static boolean isLoggedIn() {
        return AuthenticationFactory.getAdaptedInstance().getCurrentUser() != null;
    }

}
