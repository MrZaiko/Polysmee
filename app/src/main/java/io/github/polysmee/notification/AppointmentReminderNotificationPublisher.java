package io.github.polysmee.notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import io.github.polysmee.R;
import io.github.polysmee.login.LoginCheckActivity;


/**
 * A broadcast receiver class that create the appointment reminder notifications.
 * <p>
 * Inspired by https://developer.android.com/training/notify-user/build-notification#java.
 **/
public final class AppointmentReminderNotificationPublisher extends BroadcastReceiver {

    private final static int CHANEL_NOTIFICATION_PRIORITY = NotificationManager.IMPORTANCE_HIGH;
    private final static int NOTIFICATION_PRIORITY = NotificationCompat.PRIORITY_MAX;
    private final static int NOTIFICATION_LOCK_SCREEN_VISIBILITY =
            NotificationCompat.VISIBILITY_PRIVATE;
    private final static long[] VIBRATION_PATTERN = {0, 250, 250, 250};

    @Override
    public void onReceive(@NonNull Context context, @NonNull Intent intent) {
        Intent fullScreenIntent = new Intent(context, LoginCheckActivity.class);
        PendingIntent fullScreenPendingIntent = PendingIntent.getActivity(context, 0,
                fullScreenIntent, 0);
        createNotificationChannel(context);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,
                context.getResources().getString(R.string.appointment_reminder_notification_chanel_id))
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(context.getResources().getString(R.string.title_appointment_reminder_notification_notification))
                .setContentText(context.getResources().getString(R.string.text_appointment_reminder_notification_notification))
                .setPriority(NOTIFICATION_PRIORITY)
                .setVisibility(NOTIFICATION_LOCK_SCREEN_VISIBILITY)
                .setCategory(NotificationCompat.CATEGORY_EVENT)
                .setFullScreenIntent(fullScreenPendingIntent, true)
                .setAutoCancel(true)
                .setVibrate(VIBRATION_PATTERN);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(context.getResources().getInteger(R.integer.appointment_reminder_notification_id), builder.build());
    }

    /**
     * Creates the notification channel for the reminder notifications.
     * <p>
     * From https://developer.android.com/training/notify-user/build-notification?hl=en#java : "It's
     * safe to call this repeatedly because creating an existing notification channel performs no
     * operation." Later when doing the notification with resource file move it to the app launch as
     * suggested.
     */
    private static void createNotificationChannel(Context context) {
        assert context != null;
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel =
                    new NotificationChannel(context.getResources().getString(R.string.appointment_reminder_notification_chanel_id)
                            ,
                            context.getResources().getString(R.string.appointment_reminder_notification_chanel_name), CHANEL_NOTIFICATION_PRIORITY);
            channel.setDescription(context.getResources().getString(R.string.appointment_reminder_notification_chanel_description));
            channel.setLockscreenVisibility(NOTIFICATION_LOCK_SCREEN_VISIBILITY);
            channel.enableVibration(true);
            channel.setVibrationPattern(VIBRATION_PATTERN);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager =
                    context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }


}
