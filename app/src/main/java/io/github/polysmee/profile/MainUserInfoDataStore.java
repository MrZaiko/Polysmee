package io.github.polysmee.profile;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.PreferenceDataStore;

import io.github.polysmee.login.MainUser;

/**
 * A PreferenceDataStore that save values to the database, more precisely it represent a
 * PreferenceDataStore of some of the main user info.
 */
public final class MainUserInfoDataStore extends PreferenceDataStore {
    public static final String PREFERENCE_KEY_MAIN_USER_NAME = "preference_key_main_user_info_name";
    public static final String PREFERENCE_KEY_MAIN_USER_DESCRIPTION =
            "preference_key_main_user_info_description";

    /**
     * Support the store of preference with key presented in the constants definition of this
     * class.
     * <p>
     * Example : {@link #PREFERENCE_KEY_MAIN_USER_DESCRIPTION}.
     * <p>
     * {@inheritDoc}
     */
    @Override
    public void putString(@NonNull String key, @Nullable String value) {
        if (value == null) {
            return;
        }
        if (key.equals(PREFERENCE_KEY_MAIN_USER_NAME)) {
            MainUser.getMainUser().setName(value);
        } else if (key.equals(PREFERENCE_KEY_MAIN_USER_DESCRIPTION)) {
            MainUser.getMainUser().setDescription(value);
        }
    }


    /**
     * Not supported, always return the empty string.
     * <p>
     * {@inheritDoc}
     */
    @Override
    public String getString(@NonNull String key, @Nullable String defValue) {
        // Always return empty string as we do not support get operation. It do not return
        // defValue as the value may exist in the storage but we do not support it.
        return "";
    }
}
