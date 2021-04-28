package io.github.polysmee.settings.fragments;

import android.content.res.Configuration;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreference;

import io.github.polysmee.R;

/**
 * The fragment representing the main setting user interface
 */
public final class SettingsMainFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preference_fragment_settings_main, rootKey);
        SwitchPreference switchPreference = findPreference(getContext().getResources().getString(R.string.preference_key_is_dark_mode));
        switchPreference.setOnPreferenceClickListener(x -> {
            this.onDarkModeClick();
            return true;
        });
    }

    public void onDarkModeClick(){
        boolean isDarkMode = PreferenceManager.getDefaultSharedPreferences(getContext()).getBoolean(getContext().getResources().getString(R.string.preference_key_is_dark_mode), false);
        if (isDarkMode==true){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }
}