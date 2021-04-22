package io.github.polysmee.settings;

import android.content.res.Configuration;
import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreference;

import io.github.polysmee.R;

/**
 * The fragment representing the main setting user interface
 */
public final class FragmentSettingsMain extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.fragment_settings_main, rootKey);
    }
}