package io.github.polysmee.profile;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.PreferenceDataStore;

import io.github.polysmee.database.DatabaseUser;
import io.github.polysmee.database.User;
import io.github.polysmee.login.MainUser;

/**
 * A PreferenceDataStore that save values to the database,
 * more precisely it represent a PreferenceDataStore of the main user info.
 */
public final class MainUserInfoDataStore extends PreferenceDataStore {
    public static final String PREFERENCE_KEY_MAIN_USER_NAME = "preference_key_main_user_info_name";
    public static final String PREFERENCE_KEY_MAIN_USER_DESCRIPTION = "preference_key_main_user_info_description";

    @Override
    //It only save one map, this map have the key for the main user name. It will save this value on the database
    public void putString(@NonNull String key, @Nullable String value) {
        if (value == null) {
            return;
        }
        if (key.equals(PREFERENCE_KEY_MAIN_USER_NAME)) {
            MainUser.getMainUser().setName(value);
        }else if(key.equals(PREFERENCE_KEY_MAIN_USER_DESCRIPTION)){
            MainUser.getMainUser().setDescription(value);
        }
    }

    @Override
    //always return empty string
    public String getString(@NonNull String key, @Nullable String defValue) {
        return "";
    }
}
