package io.github.polysmee.settings.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.EditTextPreference;
import androidx.preference.PreferenceDataStore;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;

import com.google.firebase.auth.FirebaseUser;

import io.github.polysmee.R;
import io.github.polysmee.database.DatabaseFactory;
import io.github.polysmee.database.User;
import io.github.polysmee.database.databaselisteners.StringValueListener;
import io.github.polysmee.login.AuthenticationFactory;
import io.github.polysmee.login.MainUserSingleton;
import io.github.polysmee.settings.UserInfoDataStore;

/**
 * The fragment representing the user info setting user interface
 */
public final class SettingsUserInfoFragment  extends PreferenceFragmentCompat {
    private StringValueListener nameListener;
    private UserInfoDataStore userInfoDataStore;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        Context context = getPreferenceManager().getContext();
        PreferenceScreen screen = getPreferenceManager().createPreferenceScreen(context);

        EditTextPreference userNameEditTextPreference = new EditTextPreference(context);
        userInfoDataStore = new UserInfoDataStore();
        userNameEditTextPreference.setPreferenceDataStore(userInfoDataStore);
        userNameEditTextPreference.setKey(UserInfoDataStore.preferenceKeyMainUserName);
        userNameEditTextPreference.setTitle(getContext().getResources().getString(R.string.title_settings_main_user_name));
        nameListener = getStringValuetListenerForDefaultValue(userNameEditTextPreference);
        MainUserSingleton.getInstance().getNameAndThen(nameListener);


        EditTextPreference userEmailEditTextPreference = new EditTextPreference(context);
        userEmailEditTextPreference.setPreferenceDataStore(userInfoDataStore);
        userEmailEditTextPreference.setKey(UserInfoDataStore.preferenceKeyMainUserEmail);
        userEmailEditTextPreference.setTitle(getContext().getResources().getString(R.string.title_settings_main_user_email));
        userEmailEditTextPreference.setEnabled(false);
        userEmailEditTextPreference.setDefaultValue("Please wait");
        FirebaseUser user = AuthenticationFactory.getAdaptedInstance().getCurrentUser();
        if(user!=null){
            userEmailEditTextPreference.setSummary(user.getEmail());
        }

        screen.addPreference(userNameEditTextPreference);
        screen.addPreference(userEmailEditTextPreference);
        setPreferenceScreen(screen);

    }

    private static StringValueListener getStringValuetListenerForDefaultValue(EditTextPreference editTextPreference){
        return string -> editTextPreference.setSummary(string);
    }

    @Override
    public void onPause() {
        assert nameListener !=null;
        super.onPause();
        MainUserSingleton.getInstance().removeNameListener(nameListener);
    }

    @Override
    public void onResume() {
        super.onResume();
        FirebaseUser user = AuthenticationFactory.getAdaptedInstance().getCurrentUser();
        if(user !=null){
            EditTextPreference userEmailEditTextPreference = findPreference(UserInfoDataStore.preferenceKeyMainUserEmail);
            if(userEmailEditTextPreference!=null){
                userEmailEditTextPreference.setPreferenceDataStore(userInfoDataStore);
                userEmailEditTextPreference.setSummary(user.getEmail());
            }
        }
        EditTextPreference userNameEditTextPreference = findPreference(UserInfoDataStore.preferenceKeyMainUserName);
        if(userNameEditTextPreference!=null){
            userNameEditTextPreference.setPreferenceDataStore(userInfoDataStore);
            nameListener = getStringValuetListenerForDefaultValue(userNameEditTextPreference);;
            MainUserSingleton.getInstance().getNameAndThen(nameListener);
        }
    }

    @Override
    public void onStop() {
        assert nameListener !=null;
        super.onStop();
        MainUserSingleton.getInstance().removeNameListener(nameListener);
    }


}
