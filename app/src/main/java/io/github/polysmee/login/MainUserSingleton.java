package io.github.polysmee.login;

import com.google.firebase.auth.FirebaseAuth;

import io.github.polysmee.database.DatabaseUser;
import io.github.polysmee.interfaces.User;

public class MainUserSingleton {

    private static User inst = null;

    public static User getInstance() throws NullPointerException { //maybe replace with optional ? throw is very rare so not sure
        if(inst == null) {
            inst = new DatabaseUser(FirebaseAuth.getInstance().getCurrentUser().getUid());
        }
        return inst;
    }
}
