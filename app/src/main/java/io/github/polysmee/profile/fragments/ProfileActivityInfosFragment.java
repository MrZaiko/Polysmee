package io.github.polysmee.profile.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseUser;

import io.github.polysmee.R;
import io.github.polysmee.database.databaselisteners.StringValueListener;
import io.github.polysmee.login.AuthenticationFactory;
import io.github.polysmee.login.MainUser;
import io.github.polysmee.settings.FriendsActivity;
import io.github.polysmee.settings.UserInfoDataStore;

public class ProfileActivityInfosFragment extends PreferenceFragmentCompat {

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
        userNameEditTextPreference.setTitle(getString(R.string.title_settings_main_user_name));
        nameListener = getStringValuetListenerForDefaultValue(userNameEditTextPreference);
        MainUser.getMainUser().getNameAndThen(nameListener);


        EditTextPreference userEmailEditTextPreference = new EditTextPreference(context);
        userEmailEditTextPreference.setPreferenceDataStore(userInfoDataStore);
        userEmailEditTextPreference.setKey(UserInfoDataStore.preferenceKeyMainUserEmail);
        userEmailEditTextPreference.setTitle(getString(R.string.title_settings_main_user_email));
        userEmailEditTextPreference.setEnabled(false);
        userEmailEditTextPreference.setDefaultValue(getString(R.string.genericWaitText));
        FirebaseUser user = AuthenticationFactory.getAdaptedInstance().getCurrentUser();
        if(user!=null){
            userEmailEditTextPreference.setSummary(user.getEmail());
        }

        Preference friendManagerPreference =  new Preference(context);
        friendManagerPreference.setPreferenceDataStore(userInfoDataStore);
        friendManagerPreference.setTitle(getContext().getResources().getString(R.string.title_settings_main_user_friends));
        friendManagerPreference.setKey(UserInfoDataStore.PREFERENCE_KEY_MAIN_USER_FRIENDS);
        friendManagerPreference.setOnPreferenceClickListener((v) ->{
            Intent intent = new Intent(getContext(), FriendsActivity.class);
            startActivity(intent);
            return false;
        });


        EditTextPreference descriptionPreference = new EditTextPreference(context);
        descriptionPreference.setPreferenceDataStore(userInfoDataStore);
        descriptionPreference.setKey(UserInfoDataStore.PREFERENCE_KEY_MAIN_USER_DESCRIPTION);
        descriptionPreference.setTitle("Description:");

        screen.addPreference(userNameEditTextPreference);
        screen.addPreference(userEmailEditTextPreference);
        screen.addPreference(friendManagerPreference);
        screen.addPreference(descriptionPreference);
        setPreferenceScreen(screen);


    }

    /**
     *
     * @param editTextPreference the preference to update the summary
     * @return a string value listener that at a event will set the summary of the editTextPreference to the value of the string
     */
    private static StringValueListener getStringValuetListenerForDefaultValue(EditTextPreference editTextPreference){
        return editTextPreference::setSummary;
    }

    @Override
    public void onPause() {
        assert nameListener !=null;
        super.onPause();
        MainUser.getMainUser().removeNameListener(nameListener);
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
            MainUser.getMainUser().getNameAndThen(nameListener);
        }
    }

    @Override
    public void onStop() {
        assert nameListener !=null;
        super.onStop();
        MainUser.getMainUser().removeNameListener(nameListener);
    }
}