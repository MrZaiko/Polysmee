package io.github.polysmee.znotification;

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
    private static AlarmManager alarmManager;

    private static String getlocalSharedPreferenceName() {
        assert (mContext != null);
        return mContext.getResources().getString(R.string.sharedPreferenceKeyAppointmentReminderNotificationMaster);
    }

    /*
     * The return pendingIntent is uniquely identify by the android system by the reminder notification time of the appointment and the fact that
     * it is a broadcast for AppointmentReminderNotificationPublisher
     *
     * @param appointmentReminderNotificationTimeMin the epoch time in minutes of when the notification should appear
     *
     */
    private static PendingIntent getReminderNotificationPendingIntent(int appointmentReminderNotificationTimeMin) {
        assert mContext!=null;
        Intent notificationIntent = new Intent(mContext, AppointmentReminderNotificationPublisher.class);
        return PendingIntent.getBroadcast(mContext, appointmentReminderNotificationTimeMin, notificationIntent, 0);
    }


    /**
     * Remove the reminder notification appointment of the appointment from alarmManager and update the local state of setted reminder appointment.
     * If the reminder notification of the appointment is not setted up do nothing
     * @param appointmentId the appointmentId of the appointment to remove the reminder notification
     * @param localAppointmentsReminderTime the SharedPreference that contain all the reminder notification already setted up
     */
    private static void removeAppointmentReminderNotification(@NonNull String appointmentId, @NonNull SharedPreferences localAppointmentsReminderTime ){
        assert alarmManager!=null;
        int appointmentNotificationTimeMin = localAppointmentsReminderTime.getInt(appointmentId, -1);
        //not setted up if it take default value
        if (appointmentNotificationTimeMin==-1){
            return;
        }
        localAppointmentsReminderTime.edit().remove(appointmentId).apply();
        Set<String> appointmentsId = localAppointmentsReminderTime.getAll().keySet();
        for(String settedAppointmentId : appointmentsId){
            if ( appointmentNotificationTimeMin == localAppointmentsReminderTime.getInt(settedAppointmentId, -2)){
                return;
            }
        }
        alarmManager.cancel(getReminderNotificationPendingIntent( appointmentNotificationTimeMin));
        return;
    }

    //launch at the start so that the reminder set are consistent with the database. i.e remove the reminder that are set but that the user are no longer part of
    private static void onDone(Set<String> o) {
        //read the only public function first to understand more easily
        assert (alarmManager != null);
        assert (mContext != null);
        SharedPreferences localAppointmentsReminderTime = mContext.getSharedPreferences(getlocalSharedPreferenceName(), Context.MODE_PRIVATE);
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
        SharedPreferences.Editor localAppointmentsReminderTimeEditor = localAppointmentsReminderTime.edit();
        for (String appointmentId : o) {
            if (!appointmentStartTimeListeners.containsKey(appointmentId)) {
                LongValueListener startTimeValueListener = (long startTime) -> {
                    int appointmentReminderNotificationTimeMin = (int) (TimeUnit.MILLISECONDS.toMinutes(startTime) - PreferenceManager.getDefaultSharedPreferences(mContext).getInt(
                            mContext.getResources().getString(R.string.preference_key_appointments_reminder_notification_time_from_appointment_minutes),
                            mContext.getResources().getInteger(R.integer.default_appointment_reminder_notification__time_from_appointment_min)));
                    //I don't need to check if the time of reminder is already pass, indeed setExact, which has the same semantic in that point that set (methods of alarmManger),
                    //will trigger directly the alarm if the time passed as argument is greater than the current time
                    if (startTime > System.currentTimeMillis()) {
                        //if the appointment reminder is not setup at the correct time remove it
                        if(localAppointmentsReminderTime.getInt(appointmentId, -1)!=appointmentReminderNotificationTimeMin){
                            removeAppointmentReminderNotification(appointmentId, localAppointmentsReminderTime);
                            localAppointmentsReminderTimeEditor.putInt(appointmentId, appointmentReminderNotificationTimeMin).apply();
                        }
                        alarmManager.setExact(AlarmManager.RTC_WAKEUP, TimeUnit.MINUTES.toMillis(appointmentReminderNotificationTimeMin), getReminderNotificationPendingIntent(appointmentReminderNotificationTimeMin));
                    }
                };
                new DatabaseAppointment(appointmentId).getStartTimeAndThen(startTimeValueListener);
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
    public static void appointmentReminderNotificationSetListeners(@NonNull Context context, @NonNull AlarmManager alarmManager) {
        //to be sure that the listener will be setup only once, more robustness
        if (isListenerSetup) {
            return;
        }
        //set the variable that would be used in other function more specifically the done function
        mContext = context;
        AppointmentReminderNotificationSetupListener.alarmManager =alarmManager;
        MainUserSingleton.getInstance().getAppointmentsAndThen(AppointmentReminderNotificationSetupListener::onDone);
    }
}