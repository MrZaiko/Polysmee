package io.github.polysmee.notification;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import io.github.polysmee.R;
import io.github.polysmee.database.DatabaseAppointment;
import io.github.polysmee.database.databaselisteners.LongValueListener;
import io.github.polysmee.login.MainUserSingleton;


public final class AppointmentReminderNotificationSetupListener {
    private final static Map<String, LongValueListener> appointmentStartTimeListeners = new HashMap<>();
    private static boolean isListenerSetup = false;

    //need to have those variable as we cannot pass Context and CurrentTime to the done function who need to be of a specified form since it will
    // be use as a lambda to have a StringSetValueListener
    private static Context mContext;
    private static CurrentTime currentTime;

    private static String getlocalSharedPreferenceName() {
        assert (mContext != null);
        return mContext.getResources().getString(R.string.sharedPreferenceKeyAppointmentReminderNotificationMaster);
    }

    /*
     * The return pendingIntent is uniquely identify by the android system by the appointmentId and the fact that
     * it is a broadcast for AppointmentReminderNotificationPublisher
     *
     * we need a android version Q to be able to use setIdentifier on the intent
     */
    @RequiresApi(api = Build.VERSION_CODES.Q)
    private static PendingIntent getReminderNotificationPendingIntent(@NonNull Context context, @NonNull String appointmentID) {
        Intent notificationIntent = new Intent(context, AppointmentReminderNotificationPublisher.class).setIdentifier(appointmentID);
        return PendingIntent.getBroadcast(context, 0, notificationIntent, 0);
    }


    //launch at the start so that the reminder set are consistent with the database. i.e remove the reminder that are set but that the user are no longer part of
    @RequiresApi(api = Build.VERSION_CODES.Q)
    private static void onDone(Set<String> o) {
        //read the only public function first to understand more easily
        assert (currentTime != null);
        assert (mContext != null);
        SharedPreferences localAppointmentsReminderSettedUp = mContext.getSharedPreferences(getlocalSharedPreferenceName(), Context.MODE_PRIVATE);
        SharedPreferences.Editor localAppointmentsStateEditor = localAppointmentsReminderSettedUp.edit();
        //remove all the appointments reminder that are setup in the system but that doesn't exist for the main user anymore
        Set<String> localAppointments = localAppointmentsReminderSettedUp.getAll().keySet();
        ArrayList<String> toRemove = new ArrayList<>();
        for (String appointmentId : localAppointments) {
            if (!o.contains(appointmentId)) {
                toRemove.add(appointmentId);
            }
        }
        for (String appointmentId : toRemove) {
            AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
            alarmManager.cancel(getReminderNotificationPendingIntent(mContext, appointmentId));
            localAppointmentsStateEditor.remove(appointmentId);
            if (appointmentStartTimeListeners.containsKey(appointmentId)) {
                new DatabaseAppointment(appointmentId).removeStartListener(appointmentStartTimeListeners.get(appointmentId));
                appointmentStartTimeListeners.remove(appointmentId);
            }
        }
        localAppointmentsStateEditor.apply();
        //add listener to all the appointments that do not have listener set up yet
        for (String appointmentId : o) {
            if (!appointmentStartTimeListeners.containsKey(appointmentId)) {
                DatabaseAppointment databaseAppointment = new DatabaseAppointment(appointmentId);
                LongValueListener startTimeValueListener = (long startTime) -> {
                    AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
                    long appointment_reminder_notification_time_ms = startTime - TimeUnit.MINUTES.toMillis(PreferenceManager.getDefaultSharedPreferences(mContext).getInt(
                            mContext.getResources().getString(R.string.preference_key_appointments_reminder_notification_time_from_appointment_minutes),
                            mContext.getResources().getInteger(R.integer.default_appointment_reminder_notification__time_from_appointment_min)));
                    //I don't need to check if the time of reminder is already pass, indeed setExact, which has the same semantic in that point that set (methods of alarmManger),
                    //will trigger directly the alarm if the time passed as argument is greater than the current time
                    if (startTime > currentTime.get()) {
                        alarmManager.setExact(AlarmManager.RTC_WAKEUP, appointment_reminder_notification_time_ms, getReminderNotificationPendingIntent(mContext, appointmentId));
                        localAppointmentsStateEditor.putBoolean(appointmentId, true).apply();
                    }
                };
                databaseAppointment.getStartTimeAndThen(startTimeValueListener);
                appointmentStartTimeListeners.put(appointmentId, startTimeValueListener);

            }
        }
        isListenerSetup = true;
    }

    /*
     * This function should be called as soon as the MainUserCurrentrSingleton exist so that the reminder of appointments can be coherent with the database Value
     * @param context The Context in which to perform the setup
     *
     */
    public static void appointmentReminderNotificationSetListeners(@NonNull Context context, @NonNull CurrentTime currentTime) {
        //to be sure that the listener will be setup only once, more robustness
        if (isListenerSetup) {
            return;
        }
        //set the variable that would be used in other function more specifically the done function
        mContext = context;
        AppointmentReminderNotificationSetupListener.currentTime = currentTime;
        MainUserSingleton.getInstance().getAppointmentsAndThen(AppointmentReminderNotificationSetupListener::onDone);
    }
}