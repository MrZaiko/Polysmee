package io.github.polysmee.settings;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.PreferenceDataStore;

import io.github.polysmee.database.User;
import io.github.polysmee.login.MainUserSingleton;

public class UserInfoDataStore extends PreferenceDataStore {
    private User dataBaseMainUser = MainUserSingleton.getInstance();
    public static final String preferenceKeyMainUserName = "preference_key_main_user_info_name";
    public static String preferenceKeyMainUserEmail = "preference_key_main_user_info_email";
    public static String PREFERENCE_KEY_MAIN_USER_FRIENDS = "preference_key_main_user_info_friends";


    @Override
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
    public String getString(@NonNull String key, @Nullable String defValue) {
        return "";
    }
}
