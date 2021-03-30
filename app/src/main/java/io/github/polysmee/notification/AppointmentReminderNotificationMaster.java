package io.github.polysmee.notification;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.preference.PreferenceManager;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import io.github.polysmee.R;
import io.github.polysmee.database.DatabaseAppointment;
import io.github.polysmee.database.DatabaseFactory;
import io.github.polysmee.login.MainUserSingleton;

public final class AppointmentReminderNotificationMaster {
    private static Context mContext;
    private static String keyCurrentNotListenedBoolean = "keyCurrentNotListenedBoolean";

    /*
     * The return pendingIntent is uniquely identify by the android system by the appointmentId and the fact that
     * it is a broadcast for AppointmentReminderNotificationPublisher
     */
    private static PendingIntent getReminderNotificationPendingIntent(@NonNull Context context, @NonNull String appointmentID) {
        Intent notificationIntent = new Intent(context, AppointmentReminderNotificationPublisher.class).setIdentifier(appointmentID);
        return PendingIntent.getBroadcast(context, 0, notificationIntent, 0);
    }


    private static void createNotificationToUnderstand(String string) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, mContext.getResources().getString(R.string.appointment_reminder_notification_chanel_id))
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(string);
        NotificationManagerCompat.from(mContext).notify(0, builder.build());
    }


    //launch at the start so that the reminder set are consistent with the database. i.e remove the reminder that are set but that the user are no longer part of
    private static void onDone(Set<String> o) {
        SharedPreferences localAppointmentsState = mContext.getSharedPreferences(AppointmentReminderNotificationMaster.class.getName(), Context.MODE_PRIVATE);
        SharedPreferences.Editor localAppointmentsStateEditor = localAppointmentsState.edit();
        //remove all the appointments reminder that are setup in the system but that doesn't exist for the main user anymore
        Set<String> localAppointments = localAppointmentsState.getAll().keySet();
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
        }
        localAppointmentsStateEditor.apply();
        //add listener to all the appointments who don't have a appointemnt yet
        boolean currentNotListenedBoolean = localAppointmentsState.getBoolean(keyCurrentNotListenedBoolean, false);
        o.removeAll(localAppointmentsState.getAll().keySet());
        for (String appointmentId : o) {
            if (localAppointmentsState.getBoolean(appointmentId, currentNotListenedBoolean) == currentNotListenedBoolean) {
                new DatabaseAppointment(appointmentId).getStartTimeAndThen(startTime -> {
                    AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
                    long appointment_reminder_notification_time_ms = startTime - TimeUnit.MINUTES.toMillis(PreferenceManager.getDefaultSharedPreferences(mContext).getInt(
                            mContext.getResources().getString(R.string.preference_key_appointments_reminder_notification_time_from_appointment_minutes),
                            mContext.getResources().getInteger(R.integer.default_appointment_reminder_notification__time_from_appointment_min)));
                    //I don't need to check if the time of reminder is already pass, indeed setExact, which has the same semantic in that point that set (methods of alarmManger),
                    //will trigger directly the alarm if the time passed as argument is greater than the current time
                    if (appointment_reminder_notification_time_ms < System.currentTimeMillis()) {
                        alarmManager.setExact(AlarmManager.RTC_WAKEUP, appointment_reminder_notification_time_ms, getReminderNotificationPendingIntent(mContext, appointmentId));
                    }
                });
                localAppointmentsStateEditor.putBoolean(appointmentId, !currentNotListenedBoolean);
            }
        }
        localAppointmentsStateEditor.apply();
    }

    /*
     * This function should be called as soon as the MainUserSingleton exist so that the reminder of appointments can be coherent with the database Value
     * //TODO create a getAppointmentsAndThenServer to get value from server or nothing
     * @param context The Context in which to perform the setup
     *
     */
    public static void appointmentReminderNotificationSetListeners(@NonNull Context context) {
        mContext = context;
        SharedPreferences localAppointmentsState = mContext.getSharedPreferences(AppointmentReminderNotificationMaster.class.getName(), Context.MODE_PRIVATE);
        boolean newCurrentNotListenedBoolean = !(localAppointmentsState.getBoolean(keyCurrentNotListenedBoolean, false));
        localAppointmentsState.edit().putBoolean(keyCurrentNotListenedBoolean, newCurrentNotListenedBoolean).apply();
        MainUserSingleton.getInstance().getAppointmentsAndThen(AppointmentReminderNotificationMaster::onDone);
    }
}