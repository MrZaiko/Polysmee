package io.github.polysmee.login;

import io.github.polysmee.database.DatabaseUser;
import io.github.polysmee.database.User;

public class MainUserSingleton {

    public static User getInstance() {
        return new DatabaseUser(AuthenticationFactory.getAdaptedInstance().getCurrentUser().getUid());
    }

}
