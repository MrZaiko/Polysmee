package io.github.polysmee.znotification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import io.github.polysmee.R;
import io.github.polysmee.login.LoginCheckActivity;


/**
 * greatly inspired by https://developer.android.com/training/notify-user/build-notification#java
 * It is the broadcast receiver class that will receive broadcasts at certain times (specified in
 * in the values resources, in appointmentReminderNotification.xml) before appointments, and will create
 * a notification at each broadcast received to remind the user that he/she has a appointment coming soon
 *
 **/
public class AppointmentReminderNotificationPublisher extends BroadcastReceiver {

    private final static int CHANEL_NOTIFICATION_PRIORITY = NotificationManager.IMPORTANCE_HIGH;
    private final static int NOTIFICATION_PRIORITY = NotificationCompat.PRIORITY_MAX;
    private final static int NOTIFICATION_LOCKSCREEN_VISIBILITY = NotificationCompat.VISIBILITY_PRIVATE;


    // From https://developer.android.com/training/notify-user/build-notification?hl=en#java :
    //"It's safe to call this repeatedly because creating an existing notification channel performs no operation."
    //Later when doing the notification with ressource file move it to the app launch as suggested
    private static void createNotificationChannel(Context context) {
        assert context != null;
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(context.getResources().getString(R.string.appointment_reminder_notification_chanel_id)
                    , context.getResources().getString(R.string.appointment_reminder_notification_chanel_name), CHANEL_NOTIFICATION_PRIORITY);
            channel.setDescription(context.getResources().getString(R.string.appointment_reminder_notification_chanel_description));
            channel.setLockscreenVisibility(NOTIFICATION_LOCKSCREEN_VISIBILITY);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }


    /**
     * Create a notification that remind the user, he/she has a appointment coming with the parameter
     * specified in the values resources, in appointmentReminderNotification.xml
     *
     * @param context The Context in which the receiver is running.
     * @param intent  The Intent being received.
     */
    @Override
    public void onReceive(@NonNull Context context,@NonNull Intent intent) {
        Intent fullScreenIntent = new Intent(context, LoginCheckActivity.class);
        PendingIntent fullScreenPendingIntent = PendingIntent.getActivity(context, 0,
                fullScreenIntent, 0);

        createNotificationChannel(context);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, context.getResources().getString(R.string.appointment_reminder_notification_chanel_id))
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(context.getResources().getString(R.string.appointment_reminder_notification_notification_title))
                .setContentText(context.getResources().getString(R.string.appointment_reminder_notification_notification_text))
                .setPriority(NOTIFICATION_PRIORITY)
                .setVisibility(NOTIFICATION_LOCKSCREEN_VISIBILITY)
                .setCategory(NotificationCompat.CATEGORY_EVENT)
                .setFullScreenIntent(fullScreenPendingIntent, true)
                .setSound(Settings.System.DEFAULT_RINGTONE_URI)
                .setContentIntent(fullScreenPendingIntent)
                .setAutoCancel(true);
        ;
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(context.getResources().getInteger(R.integer.appointment_reminder_notification_id), builder.build());
    }


}
