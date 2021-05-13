package io.github.polysmee.profile.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;

import com.google.firebase.auth.FirebaseUser;

import io.github.polysmee.R;
import io.github.polysmee.database.databaselisteners.StringValueListener;
import io.github.polysmee.login.AuthenticationFactory;
import io.github.polysmee.login.MainUser;
import io.github.polysmee.profile.ProfileActivity;
import io.github.polysmee.profile.FriendsActivity;
import io.github.polysmee.settings.UserInfoDataStore;

public class ProfileActivityInfosFragment extends PreferenceFragmentCompat {

    private StringValueListener nameListener;
    private UserInfoDataStore userInfoDataStore;
    private String visitingMode;
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        Context context = getPreferenceManager().getContext();
        PreferenceScreen screen = getPreferenceManager().createPreferenceScreen(context);
        visitingMode = this.getArguments().getString(ProfileActivity.PROFILE_VISIT_CODE);
        FirebaseUser user = AuthenticationFactory.getAdaptedInstance().getCurrentUser();
        if(visitingMode.equals(ProfileActivity.PROFILE_VISITING_MODE))
            userInfoDataStore = new UserInfoDataStore(this.getArguments().getString(ProfileActivity.PROFILE_ID_USER));
        else
            userInfoDataStore = new UserInfoDataStore();

        EditTextPreference userNameEditTextPreference = new EditTextPreference(context);
        userNameEditTextPreference.setPreferenceDataStore(userInfoDataStore);
        userNameEditTextPreference.setKey(UserInfoDataStore.preferenceKeyMainUserName);
        userNameEditTextPreference.setTitle(getString(R.string.title_settings_main_user_name));
        if(visitingMode.equals(ProfileActivity.PROFILE_VISITING_MODE)){
            userNameEditTextPreference.setEnabled(false);
            userNameEditTextPreference.setDefaultValue(getString(R.string.genericWaitText));
            userInfoDataStore.getName(userNameEditTextPreference::setSummary);
        }
        else{
            nameListener = getStringValueListenerForDefaultValue(userNameEditTextPreference);
            MainUser.getMainUser().getNameAndThen(nameListener);
        }


        EditTextPreference userEmailEditTextPreference = new EditTextPreference(context);
        userEmailEditTextPreference.setPreferenceDataStore(userInfoDataStore);
        userEmailEditTextPreference.setKey(UserInfoDataStore.preferenceKeyMainUserEmail);
        userEmailEditTextPreference.setTitle(getString(R.string.title_settings_main_user_email));
        userEmailEditTextPreference.setEnabled(false);
        userEmailEditTextPreference.setDefaultValue(getString(R.string.genericWaitText));
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


        /*EditTextPreference descriptionPreference = new EditTextPreference(context);
        descriptionPreference.setPreferenceDataStore(userInfoDataStore);
        descriptionPreference.setKey(UserInfoDataStore.PREFERENCE_KEY_MAIN_USER_DESCRIPTION);
        descriptionPreference.setTitle("Description:");*/

        screen.addPreference(userNameEditTextPreference);
        if(visitingMode.equals(ProfileActivity.PROFILE_OWNER_MODE)){
            screen.addPreference(userEmailEditTextPreference);
            screen.addPreference(friendManagerPreference);
        }
        //screen.addPreference(descriptionPreference);
        setPreferenceScreen(screen);


    }

    /**
     *
     * @param editTextPreference the preference to update the summary
     * @return a string value listener that at a event will set the summary of the editTextPreference to the value of the string
     */
    private static StringValueListener getStringValueListenerForDefaultValue(EditTextPreference editTextPreference){
        return editTextPreference::setSummary;
    }

    private void deleteNameListener(){
        assert nameListener !=null;
        if(visitingMode.equals(ProfileActivity.PROFILE_OWNER_MODE))
            MainUser.getMainUser().removeNameListener(nameListener);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        deleteNameListener();
    }

    @Override
    public void onStop() {
        super.onStop();
        deleteNameListener();
    }
}