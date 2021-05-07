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
 * this class is used to setup the reminder notification.
 * The reminder notification with this class aer consistent with the appointments the user have in the database and the appointment instance in the database
 */
public final class AppointmentReminderNotification {
    private static boolean isNotificationSetterEnable = true;

    /**
     * Set the value of isNotificationSetterEnable to the given value. If isNotificationSetterEnable is false before calling appointmentReminderNotificationSetListeners
     * then the appointment reminder notification listeners will not be set. By default isNotificationSetterEnable has true value.
     *
     * @param value the value to give isNotificationSetterEnable
     */
    public static void setIsNotificationSetterEnable(boolean value) {
        isNotificationSetterEnable = value;
    }

    /**
     * This function should be called as soon as the MainUserCurrentrSingleton exist so that the reminder of appointments can be coherent with the database Value,
     * i.e. as soon as possible but it need MainUserCurrentSingleton
     * @param context The Context in which to perform the setup
     *
     */
    public static void appointmentReminderNotificationSetListeners(@NonNull Context context) {
        //to be sure that the listener will be setup only once, more robustness
        if (isNotificationSetterEnable) {
            context.startService(new Intent(context, AppointmentReminderNotificationService.class));
            return;
        }
    }

}