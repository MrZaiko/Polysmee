package io.github.polysmee.settings;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.PreferenceDataStore;

import io.github.polysmee.database.User;
import io.github.polysmee.login.MainUserSingleton;

/**
 * This class implement a PreferenceDataStore so that we can save values enter in a EditTextPreference to the database,
 * more precisely only the value corresponding to the main user name in the application
 */
public final class UserInfoDataStore extends PreferenceDataStore {
    private User dataBaseMainUser = MainUserSingleton.getInstance();
    public static final String preferenceKeyMainUserName = "preference_key_main_user_info_name";
    public static String preferenceKeyMainUserEmail = "preference_key_main_user_info_email";


    @Override
    /**
     * It only save one map, this map have the key for the main user name. It will save this value on the database
     */
    public void putString(@NonNull String key, @Nullable String value) {
        if(value==null){
            return;
        }
        switch (key){
            case preferenceKeyMainUserName :
                dataBaseMainUser.setName(value);
                break;
            default:
                return;
        }
    }


    @Override
    @Nullable
    /**
     * never return a value
     */
    public String getString(@NonNull String key, @Nullable String defValue) {
        return "";
    }
}
