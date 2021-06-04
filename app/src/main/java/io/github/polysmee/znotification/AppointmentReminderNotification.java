package io.github.polysmee.znotification;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;


/**
 * Used to start the {@link AppointmentReminderNotificationService appointment reminder znotification
 * service}. It allow to easily disable the appointment reminder service if needed.
 */
public final class AppointmentReminderNotification {
    private static boolean isNotificationSetterEnable = true;

    /**
     * Sets the value of isNotificationSetterEnable to the given value. If
     * isNotificationSetterEnable is false before calling appointmentReminderNotificationSetListeners
     * then the appointment reminder znotification listeners will not be set. By default,
     * isNotificationSetterEnable has true value. Use this function in tests where UI is used to
     * disable znotification and might avoid the tests to fails because the znotification is taking
     * the screen.
     *
     * @param newValue the value to set isNotificationSetterEnable to.
     */
    public static void setIsNotificationSetterEnable(boolean newValue) {
        isNotificationSetterEnable = newValue;
    }

    /**
     * Start the {@link AppointmentReminderNotificationService appointment reminder znotification
     * services}.
     * <p>
     * This function should be called as soon as the user is logged in, so that the reminder of
     * appointments can be coherent with the database Value as soon as possible. To be specific as
     * soon as possible but it need the user to be logged in.
     *
     * @param context The Context in which to perform the setup.
     */
    public static void appointmentReminderNotificationSetListeners(@NonNull Context context) {
        //to be sure that the listener will be setup only once, more robustness
        if (isNotificationSetterEnable) {
            context.startService(new Intent(context, AppointmentReminderNotificationService.class));
        }
    }

}