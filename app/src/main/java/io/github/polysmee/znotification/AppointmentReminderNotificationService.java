package io.github.polysmee.znotification;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import io.github.polysmee.R;
import io.github.polysmee.database.DatabaseAppointment;
import io.github.polysmee.database.databaselisteners.LongValueListener;
import io.github.polysmee.database.databaselisteners.StringSetValueListener;
import io.github.polysmee.login.MainUser;

/**
 * This class is a subclass of Service and it allow the reminder notification to be consistent
 * with the appointments the user have in the database and the appointment instance in the database.
 * For it to work correctly it need to be call with no extra in the intent, as all the necessary sub call to the service would be setted up by the first call to the service
 */
public final class AppointmentReminderNotificationService extends Service {
    private final Map<String, LongValueListener> appointmentStartTimeListeners = new HashMap<>();
    private final static String intentKeyExtraStartTime = "intentKeyExtraStartTime";
    private final static String intentKeyExtraAppointmentId = "IntentKeyExtraAppointmentId";
    private StringSetValueListener mainUserStringSetValueListener = null;


    /**
     * @return the local SharePreferences used by this class
     */
    private SharedPreferences getLocalSharedPreference() {
        return this.getSharedPreferences(this.getResources().getString(R.string.sharedPreferenceKeyAppointmentReminderNotificationSetupListener), Context.MODE_PRIVATE);
    }


    /**
     * Remove the reminder notification appointment of the appointment from alarmManager and update the local state of setted reminder appointment.
     * If the reminder notification of the appointment is not setted up do nothing
     *
     * @param appointmentId                 the appointmentId of the appointment to remove the reminder notification
     * @param localAppointmentsReminderTime the SharedPreference that contain all the reminder notification already setted up
     */
    private void removeAppointmentReminderNotification(@NonNull String appointmentId, @NonNull SharedPreferences localAppointmentsReminderTime) {
        int appointmentNotificationTimeMin = localAppointmentsReminderTime.getInt(appointmentId, -1);
        //not setted up if it take default value
        if (appointmentNotificationTimeMin == -1) {
            return;
        }
        localAppointmentsReminderTime.edit().remove(appointmentId).apply();
        Set<String> appointmentsId = localAppointmentsReminderTime.getAll().keySet();
        for (String settedAppointmentId : appointmentsId) {
            if (appointmentNotificationTimeMin == localAppointmentsReminderTime.getInt(settedAppointmentId, -2)) {
                return;
            }
        }
        AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(getReminderNotificationPendingIntent(appointmentNotificationTimeMin));
    }

    /*
     * The return pendingIntent is uniquely identify by the android system by the reminder notification time of the appointment and the fact that
     * it is a broadcast for AppointmentReminderNotificationPublisher
     *
     * @param appointmentReminderNotificationTimeMin the epoch time in minutes of when the notification should appear
     *
     */
    private PendingIntent getReminderNotificationPendingIntent(int appointmentReminderNotificationTimeMin) {
        Intent notificationIntent = new Intent(this, AppointmentReminderNotificationPublisher.class);
        return PendingIntent.getBroadcast(this, appointmentReminderNotificationTimeMin, notificationIntent, 0);
    }


    //launch everyTime a update to the set of appointments of the main user change,
    //so that the reminder time of apparition setted are consistent with the database. i.e remove the reminder that are set but that the user are no longer part of
    private void mainUserAppointmentsListenerUpdate(Set<String> o) {
        SharedPreferences localAppointmentsReminderTime = getLocalSharedPreference();
        //remove all the appointments reminder that are setup in the system but that doesn't exist for the main user anymore
        Set<String> localAppointments = localAppointmentsReminderTime.getAll().keySet();
        ArrayList<String> toRemove = new ArrayList<>();
        for (String appointmentId : localAppointments) {
            if (!o.contains(appointmentId)) {
                toRemove.add(appointmentId);
            }
        }
        for (String appointmentId : toRemove) {
            if (appointmentStartTimeListeners.containsKey(appointmentId)) {
                new DatabaseAppointment(appointmentId).removeStartListener(appointmentStartTimeListeners.get(appointmentId));
                appointmentStartTimeListeners.remove(appointmentId);
            }
            removeAppointmentReminderNotification(appointmentId, localAppointmentsReminderTime);
        }
        //add listener to all the appointments that do not have listener set up yet
        for (String appointmentId : o) {
            if (!appointmentStartTimeListeners.containsKey(appointmentId)) {
                LongValueListener startTimeValueListener = (long startTime) -> {
                    Intent updateNotification = new Intent(this, AppointmentReminderNotification.class);
                    updateNotification.putExtra(intentKeyExtraStartTime, startTime);
                    updateNotification.putExtra(intentKeyExtraAppointmentId, appointmentId);
                    startService(updateNotification);
                };
                new DatabaseAppointment(appointmentId).getStartTimeAndThen(startTimeValueListener);
                appointmentStartTimeListeners.put(appointmentId, startTimeValueListener);

            }
        }
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        long startTime = intent.getLongExtra(intentKeyExtraStartTime, -1);
        String appointmentId = intent.getStringExtra(intentKeyExtraAppointmentId);
        if (startTime == -1 || appointmentId == null) {
            //TODO
            return START_STICKY;
        }
        SharedPreferences localAppointmentsReminderTime = getLocalSharedPreference();
        int appointmentReminderNotificationTimeMin = (int) (TimeUnit.MILLISECONDS.toMinutes(startTime) - PreferenceManager.getDefaultSharedPreferences(this).getInt(
                this.getResources().getString(R.string.preference_key_appointments_reminder_notification_time_from_appointment_minutes),
                this.getResources().getInteger(R.integer.default_appointment_reminder_notification__time_from_appointment_min)));
        //I don't need to check if the time of reminder is already pass, indeed setExact, which has the same semantic in that point that set (methods of alarmManger),
        //will trigger directly the alarm if the time passed as argument is greater than the current time
        if (startTime > System.currentTimeMillis()) {
            //if the appointment reminder is not setup at the correct time remove it
            if (localAppointmentsReminderTime.getInt(appointmentId, -1) != appointmentReminderNotificationTimeMin) {
                removeAppointmentReminderNotification(appointmentId, localAppointmentsReminderTime);
                localAppointmentsReminderTime.edit().putInt(appointmentId, appointmentReminderNotificationTimeMin).apply();
            }
            AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, TimeUnit.MINUTES.toMillis(appointmentReminderNotificationTimeMin), getReminderNotificationPendingIntent(appointmentReminderNotificationTimeMin));
        }
        //TODO check start_sticky is good
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mainUserStringSetValueListener = x -> this.mainUserAppointmentsListenerUpdate(x);
        MainUser.getMainUser().getAppointmentsAndThen(mainUserStringSetValueListener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MainUser.getMainUser().removeAppointmentsListener(mainUserStringSetValueListener);
        Set<Map.Entry<String, LongValueListener>> appointmentIdAndStartTimeListeners = appointmentStartTimeListeners.entrySet();
        for (Map.Entry<String, LongValueListener> appointmentIdAndStartTimeListener : appointmentIdAndStartTimeListeners) {
            new DatabaseAppointment(appointmentIdAndStartTimeListener.getKey()).removeStartListener(appointmentIdAndStartTimeListener.getValue());
        }
    }


    @Nullable
    @Override
    //we don't allow bindings
    public IBinder onBind(Intent intent) {
        return null;
    }

}