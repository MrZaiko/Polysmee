package io.github.polysmee.settings;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import io.github.polysmee.R;

public class AppointmentReminderSettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
    }
}