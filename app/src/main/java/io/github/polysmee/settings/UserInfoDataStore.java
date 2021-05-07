package io.github.polysmee.settings;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.PreferenceDataStore;

import io.github.polysmee.database.User;
import io.github.polysmee.login.MainUser;

/**
 * This class implement a PreferenceDataStore so that we can save values enter in a EditTextPreference to the database,
 * more precisely only the value corresponding to the main user name in the application
 */
public final class UserInfoDataStore extends PreferenceDataStore {
    private final User dataBaseMainUser = MainUser.getMainUser();
    public static final String preferenceKeyMainUserName = "preference_key_main_user_info_name";
    public static final String preferenceKeyMainUserEmail = "preference_key_main_user_info_email";
    public static String PREFERENCE_KEY_MAIN_USER_FRIENDS = "preference_key_main_user_info_friends";
    public static String PREFERENCE_KEY_MAIN_USER_DESCRIPTION = "preference_key_main_user_info_description";


    @Override
    //It only save one map, this map have the key for the main user name. It will save this value on the database
    public void putString(@NonNull String key, @Nullable String value) {
        if(value==null){
            return;
        }
        if(key.equals(preferenceKeyMainUserName)){
            dataBaseMainUser.setName(value);
        }
    }


    @Override
    //always return empty string
    public String getString(@NonNull String key, @Nullable String defValue) {
        return "";
    }
}
