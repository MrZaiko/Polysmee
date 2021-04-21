package io.github.polysmee.settings.fragments;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import io.github.polysmee.R;

/**
 * The fragment representing the main setting user interface
 */
public final class SettingsMainFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preference_fragment_settings_main, rootKey);
    }
}