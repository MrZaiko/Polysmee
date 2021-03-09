package io.github.polysmee;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.concurrent.TimeUnit;


//greatly inspired by https://developer.android.com/training/notify-user/build-notification#java
public class AppointmentReminderNotificationPublisher extends BroadcastReceiver {

    private final static int CHANEL_NOTIFICATION_PRIORITY = NotificationManager.IMPORTANCE_HIGH;
    private final static int NOTIFICATION_PRIORITY = NotificationCompat.PRIORITY_MAX;
    private final static int NOTIFICATION_LOCKSCREEN_VISIBILITY = NotificationCompat.VISIBILITY_PRIVATE;

    // From https://developer.android.com/training/notify-user/build-notification?hl=en#java :
    //"It's safe to call this repeatedly because creating an existing notification channel performs no operation."
    //Later when doing the notification with ressource file move it to the app launch as suggered
    private void createNotificationChannel(Context context) {
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
     * @param context The Context in which the receiver is running.
     * @param intent  The Intent being received.
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent fullScreenIntent = new Intent(context, MainActivity.class);
        PendingIntent fullScreenPendingIntent = PendingIntent.getActivity(context, 0,
                fullScreenIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        createNotificationChannel(context);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, context.getResources().getString(R.string.appointment_reminder_notification_chanel_id))
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(context.getResources().getString(R.string.appointment_reminder_notification_notification_title))
                .setContentText(context.getResources().getString(R.string.appointment_reminder_notification_notification_text_prepend_time_left)+" "+
                        TimeUnit.MILLISECONDS.toMinutes(context.getResources().getInteger(R.integer.appointment_reminder_notification_time_from_appointment_ms))+
                        context.getResources().getString(R.string.appointment_reminder_notification_notification_text_append_time_left))
                .setPriority(NOTIFICATION_PRIORITY)
                .setVisibility(NOTIFICATION_LOCKSCREEN_VISIBILITY)
                .setCategory(NotificationCompat.CATEGORY_EVENT)
                .setFullScreenIntent(fullScreenPendingIntent, true)
                .setSound(Settings.System.DEFAULT_RINGTONE_URI);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(context.getResources().getInteger(R.integer.appointment_reminder_notification_id), builder.build());
    }
}
