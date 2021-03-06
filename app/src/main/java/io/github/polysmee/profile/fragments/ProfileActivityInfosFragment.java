package io.github.polysmee.profile.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;

import io.github.polysmee.R;
import io.github.polysmee.database.DatabaseUser;
import io.github.polysmee.database.User;
import io.github.polysmee.database.databaselisteners.valuelisteners.StringValueListener;
import io.github.polysmee.login.MainUser;
import io.github.polysmee.profile.FriendsActivity;
import io.github.polysmee.profile.ProfileActivity;
import io.github.polysmee.profile.MainUserInfoDataStore;

public final class ProfileActivityInfosFragment extends PreferenceFragmentCompat {

    private StringValueListener nameListener;
    private StringValueListener descriptionListener;
    private String visitingMode;

    @Override
    // There is 1 argument to pass by the Bundle, and 1 optional argument if the value of PROFILE_VISIT_CODE needed argument is PROFILE_VISITING_MODE
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        Context context = getPreferenceManager().getContext();
        PreferenceScreen screen = getPreferenceManager().createPreferenceScreen(context);
        visitingMode = this.getArguments().getString(ProfileActivity.PROFILE_VISIT_CODE);
        if (!visitingMode.equals(ProfileActivity.PROFILE_VISITING_MODE) && !visitingMode.equals(ProfileActivity.PROFILE_OWNER_MODE)){
            //argument passed are not good
            throw new IllegalArgumentException("The bundle passed should have as PROFILE_VISIT_CODE value {PROFILE_VISITING_MODE, PROFILE_OWNER_MODE}");
        }
        String visitedUserId = this.getArguments().getString(ProfileActivity.PROFILE_ID_USER);
        if(visitingMode.equals(ProfileActivity.PROFILE_VISITING_MODE) && visitedUserId == null ){
            //argument passed are not good
            throw new IllegalArgumentException("The bundle passed should have a PROFILE_ID_USER argument");
        }
        User visitedUser = null;
        if (visitingMode.equals(ProfileActivity.PROFILE_VISITING_MODE)){
            visitedUser = new DatabaseUser(visitedUserId);
        }

        Preference userNameEditTextPreference = getUserNamePreference(context, visitedUser);

        Preference userEmailEditTextPreference = getUserMailPreference(context);

        Preference userDescriptionEditTextPreference = getUserDescriptionPreference(context, visitedUser);

        Preference friendManagerPreference = getFiendManagerPreference(context);

        screen.addPreference(userNameEditTextPreference);
        if (visitingMode.equals(ProfileActivity.PROFILE_OWNER_MODE)) {
            screen.addPreference(userEmailEditTextPreference);
            screen.addPreference(friendManagerPreference);
        }
        screen.addPreference(userDescriptionEditTextPreference);
        setPreferenceScreen(screen);
    }


    private Preference getFiendManagerPreference(Context context) {
        Preference friendManagerPreference = new Preference(context);
        friendManagerPreference.setTitle(getContext().getResources().getString(R.string.title_profile_main_user_friends));
        friendManagerPreference.setOnPreferenceClickListener((v) -> {
            Intent intent = new Intent(getContext(), FriendsActivity.class);
            startActivity(intent);
            return false;
        });
        return friendManagerPreference;
    }

    private Preference getUserMailPreference(Context context) {
        EditTextPreference userEmailEditTextPreference = new EditTextPreference(context);
        userEmailEditTextPreference.setTitle(getString(R.string.title_profile_user_email));
        userEmailEditTextPreference.setEnabled(false);
        userEmailEditTextPreference.setDefaultValue(getString(R.string.genericWaitText));
        userEmailEditTextPreference.setSummary(MainUser.getCurrentUserEmail());
        return userEmailEditTextPreference;
    }

    private Preference getUserNamePreference(Context context, User visitedUser) {
        EditTextPreference userNameEditTextPreference = new EditTextPreference(context);
        userNameEditTextPreference.setTitle(getString(R.string.title_profile_user_name));
        userNameEditTextPreference.setSummary(getString(R.string.genericWaitText));
        if (visitingMode.equals(ProfileActivity.PROFILE_VISITING_MODE)) {
            assert visitedUser!=null;
            userNameEditTextPreference.setEnabled(false);
            visitedUser.getName_Once_AndThen(userNameEditTextPreference::setSummary);
        } else {
            userNameEditTextPreference.setPreferenceDataStore(new MainUserInfoDataStore());
            userNameEditTextPreference.setKey(MainUserInfoDataStore.PREFERENCE_KEY_MAIN_USER_NAME);
            nameListener = userNameEditTextPreference::setSummary;
        }
        return userNameEditTextPreference;
    }

    private Preference getUserDescriptionPreference(Context context, User visitedUser) {
        EditTextPreference userDescriptionEditTextPreference = new EditTextPreference(context);
        userDescriptionEditTextPreference.setTitle(getString(R.string.title_profile_user_description));
        userDescriptionEditTextPreference.setSummary(getString(R.string.genericWaitText));
        if (visitingMode.equals(ProfileActivity.PROFILE_VISITING_MODE)){
            assert visitedUser!=null;
            userDescriptionEditTextPreference.setEnabled(false);
            visitedUser.getDescription_Once_AndThen(userDescriptionEditTextPreference::setSummary);
        }else{
            userDescriptionEditTextPreference.setPreferenceDataStore(new MainUserInfoDataStore());
            userDescriptionEditTextPreference.setKey(MainUserInfoDataStore.PREFERENCE_KEY_MAIN_USER_DESCRIPTION);
            descriptionListener = userDescriptionEditTextPreference::setSummary;
        }
        return userDescriptionEditTextPreference;
    }

    private void addNameListener(){
        if(visitingMode.equals(ProfileActivity.PROFILE_OWNER_MODE)){
            assert nameListener!=null;
            MainUser.getMainUser().getNameAndThen(nameListener);
        }
    }

    private void addDescriptionListener(){
        if(visitingMode.equals(ProfileActivity.PROFILE_OWNER_MODE)){
            assert descriptionListener!=null;
            MainUser.getMainUser().getDescriptionAndThen(descriptionListener);
        }
    }

    private void deleteNameListener() {
        if (visitingMode.equals(ProfileActivity.PROFILE_OWNER_MODE)){
            assert nameListener!=null;
            MainUser.getMainUser().removeNameListener(nameListener);
        }
    }

    private void deleteDescriptionListener(){
        if (visitingMode.equals(ProfileActivity.PROFILE_OWNER_MODE)){
            assert descriptionListener!=null;
            MainUser.getMainUser().removeNameListener(descriptionListener);
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        deleteNameListener();
        deleteDescriptionListener();
    }

    @Override
    public void onResume() {
        super.onResume();
        addNameListener();
        addDescriptionListener();
    }
}