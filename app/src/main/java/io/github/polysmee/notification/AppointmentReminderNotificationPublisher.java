package io.github.polysmee.notification;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.preference.PreferenceManager;

import com.google.android.gms.tasks.SuccessContinuation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.net.ConnectException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import io.github.polysmee.MainActivity;
import io.github.polysmee.R;
import io.github.polysmee.database.DatabaseFactory;
import io.github.polysmee.interfaces.Appointment;
import io.github.polysmee.login.LoginCheckActivity;
import io.github.polysmee.login.MainUserSingleton;


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

    /*
     * The return pendingIntent is uniquely identify by the android system by the start time of the appointemnt
     */
    private static PendingIntent getReminderNotificationPendingIntent(@NonNull Context context ) {
        Intent notificationIntent = new Intent(context, AppointmentReminderNotificationPublisher.class);
        return PendingIntent.getBroadcast(context, 0, notificationIntent, 0);
    }

   public static void appointmentTimeSetNotification(@NonNull Set<Long> appointmentTimeSet, @NonNull Context context){
       AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
       alarmManager.cancel(getReminderNotificationPendingIntent(context));
       for(long time : appointmentTimeSet){
           long appointment_reminder_notification_time_from_appointment_ms = TimeUnit.MINUTES.toMillis(PreferenceManager.getDefaultSharedPreferences(context).getInt(
                   context.getResources().getString(R.string.preference_key_appointments_reminder_notification_time_from_appointment_minutes),
                   context.getResources().getInteger(R.integer.default_appointment_reminder_notification__time_from_appointment_min)));
           long timeOfNotification = time - appointment_reminder_notification_time_from_appointment_ms;
           if(time>=appointment_reminder_notification_time_from_appointment_ms && timeOfNotification > System.currentTimeMillis()){
               alarmManager.setExact(AlarmManager.RTC_WAKEUP, timeOfNotification, getReminderNotificationPendingIntent(context));
           }
       }
   }


   /*
    * This function is really slow it try to get a value from the server, keep that in mind when calling it in android function
    * @param context The Context in which to perform the setup
    *
    */
   public static void appointmentReminderNotificationSetListener(@NonNull Context context){
       helpContext=context;
       Task<DataSnapshot> taskGetAppointmentsOfMainUser = DatabaseFactory.getAdaptedInstance().getReference("users")
               .child(MainUserSingleton.getInstance().getId()).child("appointments").get();
       taskGetAppointmentsOfMainUser.continueWith(task -> {
                    DataSnapshot dataSnapshot = task.getResult();
                    if (dataSnapshot.exists()) {
                        HashMap<String, Object> retrieved = (HashMap<String, Object>) dataSnapshot.getValue();
                        if (retrieved== null){
                            return false;
                        }
                        createNotificationToUnderstand(context, "mallFormed");
                        for (String appointmentId : retrieved.keySet()) {
                            DatabaseFactory.getAdaptedInstance().getReference("appointments")
                                    .child(appointmentId).child("start").addValueEventListener(AppointmentReminderNotificationTimeChangeListener);
                        }
                        return true;
                    } else {
                        return false;
                    }
                }
        );
       taskGetAppointmentsOfMainUser.addOnFailureListener(command -> createNotificationToUnderstand(context,"get appointements failed "));
       taskGetAppointmentsOfMainUser.addOnCanceledListener(() -> createNotificationToUnderstand(context,"get appointements canceled"));
       //TODO check the case where there is no appointement or a error occured during the retrieval
   }

   public static void createNotificationToUnderstand(Context context, String string){
       createNotificationChannel(context);
       NotificationCompat.Builder builder = new NotificationCompat.Builder(context, context.getResources().getString(R.string.appointment_reminder_notification_chanel_id))
               .setSmallIcon(R.drawable.ic_launcher_foreground)
               .setContentText(string);
       NotificationManagerCompat.from(context).notify(0,builder.build());
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
                .setContentText(context.getResources().getString(R.string.appointment_reminder_notification_notification_text_prepend_time_left) + " "
                        + PreferenceManager.getDefaultSharedPreferences(context).getInt(
                        context.getResources().getString(R.string.preference_key_appointments_reminder_notification_time_from_appointment_minutes)
                        , context.getResources().getInteger(R.integer.default_appointment_reminder_notification__time_from_appointment_min))
                        + context.getResources().getString(R.string.appointment_reminder_notification_notification_text_append_time_left))
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

    //TODO remove that
    private static Context helpContext;

    private static ValueEventListener AppointmentReminderNotificationTimeChangeListener= new ValueEventListener(){

        /**
         * This method will be called with a snapshot of the data at this location. It will also be called
         * each time that data changes.
         *
         * @param snapshot The current data at the location
         */
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            createNotificationToUnderstand(helpContext,snapshot.toString());
            //TODO really implement this funciton
        }

        /**
         * This method will be triggered in the event that this listener either failed at the server, or
         * is removed as a result of the security and Firebase Database rules. For more information on
         * securing your data, see: <a
         * href="https://firebase.google.com/docs/database/security/quickstart" target="_blank"> Security
         * Quickstart</a>
         *
         * @param error A description of the error that occurred
         */
        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            createNotificationToUnderstand(helpContext,error.toString());
            //TODO remove this just to understand
        }
    };

}
