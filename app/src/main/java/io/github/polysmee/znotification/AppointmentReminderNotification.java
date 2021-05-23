package io.github.polysmee.znotification;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;


/**
 * this class is used to setup the reminder notification.
 * The reminder notification with this class aer consistent with the appointments the user have in the database and the appointment instance in the database
 */
public final class AppointmentReminderNotification {
    private static boolean isNotificationSetterEnable = true;

    /**
     * If the last called before calling appointmentReminderNotificationSetListeners to isNotificationSetterEnable is called with false
     * then the appointment reminder notification listeners will not be set when calling appointmentReminderNotificationSetListeners.
     *
     * @param value see the function description to know what value to pass
     */
    public static void setIsNotificationSetterEnable(boolean value) {
        isNotificationSetterEnable = value;
    }

    /**
     * This function should be called as soon as the MainUserCurrentrSingleton exist so that the reminder of appointments can be coherent with the database Value,
     * i.e. as soon as possible but it need MainUserCurrentSingleton
     *
     * @param context The Context in which to perform the setup
     */
    public static void appointmentReminderNotificationSetListeners(@NonNull Context context) {
        //to be sure that the listener will be setup only once, more robustness
        if (isNotificationSetterEnable) {
            context.startService(new Intent(context, AppointmentReminderNotificationService.class));
            return;
        }
    }

}