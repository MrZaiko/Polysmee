package io.github.polysmee.login;

import com.google.firebase.auth.FirebaseAuth;

import io.github.polysmee.database.DatabaseUser;
import io.github.polysmee.database.User;

public class MainUser {

    /**
     * Method used to get the current user without having to send it between activities
     *
     * @return the current user
     */
    public static User getMainUser() {
        return new DatabaseUser(AuthenticationSingleton.getAdaptedInstance().getCurrentUser().getUid());
    }

    public static String getCurrentUserEmail() throws NullPointerException {
        return FirebaseAuth.getInstance().getCurrentUser().getEmail();
    }
}
