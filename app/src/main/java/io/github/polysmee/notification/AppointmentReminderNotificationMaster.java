package io.github.polysmee.notification;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.PreferenceManager;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

import io.github.polysmee.R;
import io.github.polysmee.database.DatabaseFactory;
import io.github.polysmee.login.MainUserSingleton;

public final class AppointmentReminderNotificationMaster {
    private static Context appointmentReminderNotificationSetterAndRemoverContext;

    /*
     * The return pendingIntent is uniquely identify by the android system by the appointmentId and the fact that
     * it is a broadcast for AppointmentReminderNotificationPublisher
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
    public static void appointmentReminderNotificationSetListeners(@NonNull Context context){
        appointmentReminderNotificationSetterAndRemoverContext =context;
        //get once the value need to add a method to the user interface
        DatabaseReference databaseReferenceMainUserListOfAppointements= DatabaseFactory.getAdaptedInstance().getReference("users")
                .child(MainUserSingleton.getInstance().getId()).child("appointments");
        databaseReferenceMainUserListOfAppointements.addChildEventListener(AppointmentReminderNotificationMainUserAppointmentsListener);
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
            AlarmManager alarmManager = (AlarmManager) appointmentReminderNotificationSetterAndRemoverContext.getSystemService(Context.ALARM_SERVICE);
            String appointmentId =snapshot.getRef().getParent().getKey();
            //called when the data is deleted
            if (snapshot.exists()){
                long appointment_reminder_notification_time_ms = ((long)snapshot.getValue()) - TimeUnit.MINUTES.toMillis(PreferenceManager.getDefaultSharedPreferences(appointmentReminderNotificationSetterAndRemoverContext).getInt(
                        appointmentReminderNotificationSetterAndRemoverContext.getResources().getString(R.string.preference_key_appointments_reminder_notification_time_from_appointment_minutes),
                        appointmentReminderNotificationSetterAndRemoverContext.getResources().getInteger(R.integer.default_appointment_reminder_notification__time_from_appointment_min)));

                //I don't need to check if the time of reminder is already pass, indeed setExact, which has the same semantic in that point that set (methods of alarmManger),
                //will trigger directly the alarm if the time passed as argument is greater than the current time

                //TODO do not put notification when it is started
                alarmManager.setExact(AlarmManager.RTC_WAKEUP,appointment_reminder_notification_time_ms, getReminderNotificationPendingIntent(appointmentReminderNotificationSetterAndRemoverContext, appointmentId));
            }else{
                //handle the case when the appointment is deleted, the user might still have the appointment in his appointment list but it will
                //not be corrected here it is not the job of the notification
                alarmManager.cancel(getReminderNotificationPendingIntent(appointmentReminderNotificationSetterAndRemoverContext, appointmentId));
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


    /*
     *  A listener for the appointments of the main user
     */
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
            AlarmManager alarmManager = (AlarmManager) appointmentReminderNotificationSetterAndRemoverContext.getSystemService(Context.ALARM_SERVICE);
            String appointmentId =snapshot.getKey();
            alarmManager.cancel(getReminderNotificationPendingIntent(appointmentReminderNotificationSetterAndRemoverContext, appointmentId));
            snapshot.getRef().removeEventListener(this);
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
            //If there is a error retry
            AppointmentReminderNotificationMaster.appointmentReminderNotificationSetListeners(appointmentReminderNotificationSetterAndRemoverContext);
        }
    };
}
