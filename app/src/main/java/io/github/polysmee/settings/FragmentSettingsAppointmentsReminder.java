package io.github.polysmee.settings;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import io.github.polysmee.R;

/*
 * The fragment representing the appointments reminder settings user interface
 */
public class FragmentSettingsAppointmentsReminder extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.fragment_settings_appointments_reminder, rootKey);
    }
}