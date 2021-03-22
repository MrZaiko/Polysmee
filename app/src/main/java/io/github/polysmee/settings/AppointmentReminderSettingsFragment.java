package io.github.polysmee.settings;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;
import androidx.preference.SeekBarPreference;

import io.github.polysmee.R;

public class AppointmentReminderSettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        Context context = getPreferenceManager().getContext();
        PreferenceScreen screen = getPreferenceManager().createPreferenceScreen(context);

        SeekBarPreference reminderTimePreference = new SeekBarPreference(context);
        reminderTimePreference.setKey("appointment_reminder_notification_time_from_appointment_min");
        reminderTimePreference.setTitle("Remind me before");
        reminderTimePreference.setSummary("Set how much time before a appointment you should be reminded (in minutes)");
        reminderTimePreference.setShowSeekBarValue(true);
        reminderTimePreference.setMax(120);
        reminderTimePreference.setMin(1);
        reminderTimePreference.setDefaultValue(getContext().getResources().getInteger(R.integer.appointment_reminder_notification_default_time_from_appointment_min));



        screen.addPreference(reminderTimePreference);

        setPreferenceScreen(screen);

    }
}