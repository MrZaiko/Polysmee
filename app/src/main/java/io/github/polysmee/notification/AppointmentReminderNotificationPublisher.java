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

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.github.polysmee.R;
import io.github.polysmee.database.DatabaseFactory;
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
    private static Context mContext;

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
    private static PendingIntent getReminderNotificationPendingIntent(@NonNull Context context, @NonNull String appointmentID ) {
        Intent notificationIntent = new Intent(context, AppointmentReminderNotificationPublisher.class).setIdentifier(appointmentID);
        return PendingIntent.getBroadcast(context, 0, notificationIntent, 0);
    }

   /*
    * This function is really slow it try to get a value from the server, keep that in mind when calling it in android function
    * @param context The Context in which to perform the setup
    *
    */
   public static void appointmentReminderNotificationSetListener(@NonNull Context context){
       mContext=context;
       //get once the value need to add a method to the user interface
       DatabaseReference databaseReferenceMainUserListOfAppointements=DatabaseFactory.getAdaptedInstance().getReference("users")
               .child(MainUserSingleton.getInstance().getId()).child("appointments");
       Task<DataSnapshot> taskGetAppointmentsOfMainUser = databaseReferenceMainUserListOfAppointements.get();
       taskGetAppointmentsOfMainUser.continueWith(task -> {
                    DataSnapshot dataSnapshot = task.getResult();
                    //when no appointments exist, i.e a value search in the database doesn't exist exist value is false
                    if (dataSnapshot.exists()) {
                        //here we are sure the hashmap exist, we handle the case where main user has some appointments
                        HashMap<String, Object> retrieved = (HashMap<String, Object>) dataSnapshot.getValue();
                        for (String appointmentId : retrieved.keySet()) {
                            //has to use this as with current user interface if the appointment is deleted it will never execute my function for handling the deletion i.e they do .exist() with the value
                            //and since it is deleted it return false and it doesn't execute so it doesn't let me handle that case
                            DatabaseFactory.getAdaptedInstance().getReference("appointments")
                                    .child(appointmentId).child("start").addValueEventListener(AppointmentReminderNotificationTimeChangeListener);
                        }
                    } else {
                        //handle case when the main user has no appointment
                    }
                    // we retun null as we don't care about the return value
                    return null;
                }
        );
       //if couldn't retrieve data, i.e a failure try again
       taskGetAppointmentsOfMainUser.addOnFailureListener(exception -> AppointmentReminderNotificationPublisher.appointmentReminderNotificationSetListener(context));
       taskGetAppointmentsOfMainUser.addOnCanceledListener(() -> AppointmentReminderNotificationPublisher.appointmentReminderNotificationSetListener(context));
       //TODO add listener to the list of appointment of the user
       databaseReferenceMainUserListOfAppointements.addChildEventListener(AppointmentReminderNotificationMainUserAppointmentsListener);
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

    /*
     *  A listener for the appointment time, if the time change it will do the appropriate action to set the appointment reminder notification
     *  at the right time.
     */
    private static ValueEventListener AppointmentReminderNotificationTimeChangeListener= new ValueEventListener(){

        /**
         * This method will be called with a snapshot of the data at this location. It will also be called
         * each time that data changes.
         *
         * @param snapshot The current data at the location
         */
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            //TODO remove next line
            createNotificationToUnderstand(mContext, "call timeListenerDataChange");
            AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
            String appointmentId =snapshot.getRef().getParent().getKey();
            //called when the data is deleted
            if (snapshot.exists()){
                long appointment_reminder_notification_time_ms = ((long)snapshot.getValue()) - TimeUnit.MINUTES.toMillis(PreferenceManager.getDefaultSharedPreferences(mContext).getInt(
                        mContext.getResources().getString(R.string.preference_key_appointments_reminder_notification_time_from_appointment_minutes),
                        mContext.getResources().getInteger(R.integer.default_appointment_reminder_notification__time_from_appointment_min)));

                //I don't need to check if the time of reminder is already pass, indeed setExact, which has the same semantic in that point that set (methods of alarmManger),
                //will trigger directly the alarm if the time passed as argument is greater than the current time
                alarmManager.setExact(AlarmManager.RTC_WAKEUP,appointment_reminder_notification_time_ms, getReminderNotificationPendingIntent(mContext, appointmentId));
            }else{
                //handle the case when the appointment is deleted, the user might still have the appointment in his appointment list but it will
                //not be corrected here it is not the job of the notification
                alarmManager.cancel(getReminderNotificationPendingIntent(mContext, appointmentId));
                snapshot.getRef().removeEventListener(this);
            }
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
            //Not handled, I do not know how to handle it. I think it is better to do nothing like in other implementations of listener of Firebase realtime database
        }
    };

    private static ChildEventListener AppointmentReminderNotificationMainUserAppointmentsListener = new ChildEventListener() {

        /**
         * This method is triggered when a new child is added to the location to which this listener was
         * added.
         *
         * @param snapshot          An immutable snapshot of the data at the new child location
         * @param previousChildName The key name of sibling location ordered before the new child. This
         */
        @Override
        public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            DatabaseFactory.getAdaptedInstance().getReference("appointments")
                    .child(snapshot.getKey()).child("start").addValueEventListener(AppointmentReminderNotificationTimeChangeListener);
        }

        /**
         * This method is triggered when the data at a child location has changed.
         *
         * @param snapshot          An immutable snapshot of the data at the new data at the child location
         * @param previousChildName The key name of sibling location ordered before the child. This will
         */
        @Override
        public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            //don't care
        }

        /**
         * This method is triggered when a child is removed from the location to which this listener was
         * added.
         *
         * @param snapshot An immutable snapshot of the data at the child that was removed.
         */
        @Override
        public void onChildRemoved(@NonNull DataSnapshot snapshot) {
            //TODO test with notification the value of snapshot.getKey()
            DatabaseFactory.getAdaptedInstance().getReference("appointments")
                    .child(snapshot.getKey()).child("start").removeEventListener(AppointmentReminderNotificationTimeChangeListener);
        }

        /**
         * This method is triggered when a child location's priority changes. See {@link
         * DatabaseReference#setPriority(Object)} and <a
         * href="https://firebase.google.com/docs/database/android/retrieve-data#data_order"
         * target="_blank">Ordered Data</a> for more information on priorities and ordering data.
         *
         * @param snapshot          An immutable snapshot of the data at the location that moved.
         * @param previousChildName The key name of the sibling location ordered before the child
         */
        @Override
        public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            //don't care
        }

        /**
         * This method will be triggered in the event that this listener either failed at the server, or
         * is removed as a result of the security and Firebase rules. For more information on securing
         * your data, see: <a href="https://firebase.google.com/docs/database/security/quickstart"
         * target="_blank"> Security Quickstart</a>
         *
         * @param error A description of the error that occurred
         */
        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            //Not handled, I do not know how to handle it. I think it is better to do nothing like in other implementations of listener of Firebase realtime database
        }
    };

}
