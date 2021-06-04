package io.github.polysmee.login;

import io.github.polysmee.database.DatabaseUser;
import io.github.polysmee.database.User;

public class MainUserFactory {

    public static User getInstance() {
        return new DatabaseUser(AuthenticationSingleton.getAdaptedInstance().getCurrentUser().getUid());
    }

    public static boolean isLoggedIn() {
        return AuthenticationSingleton.getAdaptedInstance().getCurrentUser() != null;
    }

}
