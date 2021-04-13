package io.github.polysmee.notification;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import io.github.polysmee.R;
import io.github.polysmee.database.DatabaseAppointment;
import io.github.polysmee.login.LoginCheckActivity;
import io.github.polysmee.login.MainUserSingleton;

import static java.lang.Thread.sleep;

public final class AppointmentReminderNotificationSetupListener {
    private static Context mContext;
    private final static String keyCurrentNotListenedBoolean = "keyCurrentNotListenedBoolean";


    private static String getlocalSharedPreferenceName(){
        return mContext.getResources().getString(R.string.sharedPreferenceKeyAppointmentReminderNotificationMaster);
    }

    /*
     * The return pendingIntent is uniquely identify by the android system by the appointmentId and the fact that
     * it is a broadcast for AppointmentReminderNotificationPublisher
     */
    @RequiresApi(api = Build.VERSION_CODES.Q)
    private static PendingIntent getReminderNotificationPendingIntent(@NonNull Context context, @NonNull String appointmentID) {
        Intent notificationIntent = new Intent(context, AppointmentReminderNotificationPublisher.class).setIdentifier(appointmentID);
        return PendingIntent.getBroadcast(context, 0, notificationIntent, 0);
    }



    public static void createNotificationToUnderstand(String string, int notificationNumber) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(mContext.getResources().getString(R.string.appointment_reminder_notification_chanel_id)
                    , mContext.getResources().getString(R.string.appointment_reminder_notification_chanel_name), NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription(mContext.getResources().getString(R.string.appointment_reminder_notification_chanel_description));
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = mContext.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
        Intent fullScreenIntent = new Intent(mContext, LoginCheckActivity.class);
        PendingIntent fullScreenPendingIntent = PendingIntent.getActivity(mContext, 0,
                fullScreenIntent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, mContext.getResources().getString(R.string.appointment_reminder_notification_chanel_id))
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(string)
                .setContentText(string)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
                .setCategory(NotificationCompat.CATEGORY_EVENT)
                .setFullScreenIntent(fullScreenPendingIntent, true)
                .setSound(Settings.System.DEFAULT_RINGTONE_URI)
                .setContentIntent(fullScreenPendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(mContext);
        notificationManager.notify(notificationNumber, builder.build());
    }


    //launch at the start so that the reminder set are consistent with the database. i.e remove the reminder that are set but that the user are no longer part of
    @RequiresApi(api = Build.VERSION_CODES.Q)
    private static void onDone(Set<String> o) {
        SharedPreferences localAppointmentsState = mContext.getSharedPreferences(getlocalSharedPreferenceName(), Context.MODE_PRIVATE);
        SharedPreferences.Editor localAppointmentsStateEditor = localAppointmentsState.edit();
        //remove all the appointments reminder that are setup in the system but that doesn't exist for the main user anymore
        Set<String> localAppointments = localAppointmentsState.getAll().keySet();

        ArrayList<String> toRemove = new ArrayList<>();
        for (String appointmentId : localAppointments) {
            if (!o.contains(appointmentId) && (appointmentId !=keyCurrentNotListenedBoolean)) {
                toRemove.add(appointmentId);
            }
        }
        boolean currentNotListenedAppointmentBoolean = localAppointmentsState.getBoolean(keyCurrentNotListenedBoolean, false);
        for (String appointmentId : toRemove) {
            AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
            alarmManager.cancel(getReminderNotificationPendingIntent(mContext, appointmentId));
            localAppointmentsStateEditor.remove(appointmentId);
            if(localAppointmentsState.getBoolean(appointmentId, currentNotListenedAppointmentBoolean)!=currentNotListenedAppointmentBoolean){
                //TODO remove listener from the database
            }
        }
        localAppointmentsStateEditor.apply();
        //add listener to all the appointments that do not have listener set up yet
        for (String appointmentId : o) {
            if (localAppointmentsState.getBoolean(appointmentId, currentNotListenedAppointmentBoolean) == currentNotListenedAppointmentBoolean) {
                new DatabaseAppointment(appointmentId).getStartTimeAndThen(startTime -> {
                    AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
                    //TODO remove second to milis
                    long appointment_reminder_notification_time_ms = TimeUnit.SECONDS.toMillis(startTime) - TimeUnit.MINUTES.toMillis(PreferenceManager.getDefaultSharedPreferences(mContext).getInt(
                            mContext.getResources().getString(R.string.preference_key_appointments_reminder_notification_time_from_appointment_minutes),
                            mContext.getResources().getInteger(R.integer.default_appointment_reminder_notification__time_from_appointment_min)));
                    //I don't need to check if the time of reminder is already pass, indeed setExact, which has the same semantic in that point that set (methods of alarmManger),
                    //will trigger directly the alarm if the time passed as argument is greater than the current time
                    if ( TimeUnit.SECONDS.toMillis(startTime)>System.currentTimeMillis()) {
                        alarmManager.setExact(AlarmManager.RTC_WAKEUP, appointment_reminder_notification_time_ms, getReminderNotificationPendingIntent(mContext, appointmentId));
                    }
                });
                localAppointmentsStateEditor.putBoolean(appointmentId, !currentNotListenedAppointmentBoolean);
            }
        }
        localAppointmentsStateEditor.apply();
    }

    /*
     * This function should be called as soon as the MainUserSingleton exist so that the reminder of appointments can be coherent with the database Value
     * @param context The Context in which to perform the setup
     *
     */
    public static void appointmentReminderNotificationSetListeners(@NonNull Context context) {
        mContext = context;
        SharedPreferences localAppointmentsState = mContext.getSharedPreferences(getlocalSharedPreferenceName(), Context.MODE_PRIVATE);
        boolean newCurrentNotListenedBoolean = !(localAppointmentsState.getBoolean(keyCurrentNotListenedBoolean, true));
        localAppointmentsState.edit().putBoolean(keyCurrentNotListenedBoolean, newCurrentNotListenedBoolean).apply();
        MainUserSingleton.getInstance().getAppointmentsAndThen(AppointmentReminderNotificationSetupListener::onDone);
    }
}