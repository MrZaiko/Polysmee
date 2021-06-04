package io.github.polysmee.notification;

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
import io.github.polysmee.database.databaselisteners.valuelisteners.LongValueListener;
import io.github.polysmee.database.databaselisteners.valuelisteners.StringSetValueListener;
import io.github.polysmee.login.MainUser;


/**
 * It's a Service and it allows the appointment reminder notifications to be consistent with the
 * appointments the user have in the database. To start it use {@link
 * AppointmentReminderNotification} class. For it to work correctly it needs to be start with no
 * extra in the intent and once, as all the necessary sub call to the service would be set up by the
 * first call to the service.
 */
public final class AppointmentReminderNotificationService extends Service {
    private final static String INTENT_KEY_EXTRA_START_TIME = "IntentKeyExtraStartTime";
    private final static String INTENT_KEY_EXTRA_APPOINTMENT_ID = "IntentKeyExtraAppointmentId";
    private final static int NOT_SET_UP_APPOINTMENT_REMINDER_NOTIFICATION_TIME = -1;
    private final Map<String, LongValueListener> appointmentStartTimeListeners = new HashMap<>();
    private final StringSetValueListener mainUserStringSetValueListener =
            this::mainUserAppointmentsListenerUpdate;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //it only accept intent with a startTime and a appointmentId extra
        super.onStartCommand(intent, flags, startId);
        if (intent == null) {
            return START_STICKY;
        }
        long startTime = intent.getLongExtra(INTENT_KEY_EXTRA_START_TIME, -1);
        String appointmentId = intent.getStringExtra(INTENT_KEY_EXTRA_APPOINTMENT_ID);
        if (startTime == -1 || appointmentId == null) {
            return START_STICKY;
        }
        updateAppointmentReminderNotification(startTime, appointmentId);
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        MainUser.getMainUser().getAppointmentsAndThen(mainUserStringSetValueListener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MainUser.getMainUser().removeAppointmentsListener(mainUserStringSetValueListener);
        Set<Map.Entry<String, LongValueListener>> appointmentIdAndStartTimeListeners =
                appointmentStartTimeListeners.entrySet();
        for (Map.Entry<String, LongValueListener> appointmentIdAndStartTimeListener :
                appointmentIdAndStartTimeListeners) {
            new DatabaseAppointment(appointmentIdAndStartTimeListener.getKey())
                    .removeStartListener(appointmentIdAndStartTimeListener.getValue());
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        //we don't allow bindings
        return null;
    }

    /**
     * Gets the SharePreference used only by this class.
     *
     * @return the local SharePreferences used by this class.
     */
    @NonNull
    private SharedPreferences getLocalSharedPreference() {
        return this.getSharedPreferences(this.getResources().getString(
                R.string.shared_preference_key_appointment_reminder_notification_service),
                Context.MODE_PRIVATE);
    }

    /**
     * Removes the appointment reminder notification of the appointment from alarmManager and update
     * the local state of set up appointment reminder notifications. If the appointment reminder
     * notification of the appointment is not set up do nothing
     *
     * @param appointmentId          the appointmentId of the appointment to remove the appointment
     *                               reminder notification.
     * @param localSetUpAppointments the SharedPreference that contain all the appointment with
     *                               reminder notification already set up.
     */
    private void removeAppointmentReminderNotification(@NonNull String appointmentId,
                                                       @NonNull SharedPreferences localSetUpAppointments) {
        int appointmentNotificationTimeMin = localSetUpAppointments.getInt(appointmentId,
                NOT_SET_UP_APPOINTMENT_REMINDER_NOTIFICATION_TIME);
        //not set up if it take default value
        if (appointmentNotificationTimeMin == NOT_SET_UP_APPOINTMENT_REMINDER_NOTIFICATION_TIME) {
            return;
        }
        localSetUpAppointments.edit().remove(appointmentId).apply();
        Set<String> appointmentsId = localSetUpAppointments.getAll().keySet();
        //check if no other appointment need to be reminded at the same time
        for (String setUpAppointmentId : appointmentsId) {
            if (appointmentNotificationTimeMin ==
                    localSetUpAppointments.getInt(setUpAppointmentId,
                            NOT_SET_UP_APPOINTMENT_REMINDER_NOTIFICATION_TIME)) {
                return;
            }
        }
        AlarmManager alarmManager = getAlarmManager();
        alarmManager.cancel(getReminderNotificationPendingIntent(appointmentNotificationTimeMin));
    }

    private AlarmManager getAlarmManager() {
        return (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
    }

    /**
     * Returns the {@link AppointmentReminderNotificationPublisher} pendingIntent associated to a
     * appointment reminder notification that want to appear at the passed starting time.
     * <p>
     * The return pendingIntent is uniquely identify by the android system by the appointment
     * reminder notification time of the appointment and the fact that it is a broadcast intent for
     * AppointmentReminderNotificationPublisher
     *
     * @param appointmentReminderNotificationTimeMin the epoch time in minutes of when the
     *                                               notification should appear.
     */
    private PendingIntent getReminderNotificationPendingIntent(int appointmentReminderNotificationTimeMin) {
        Intent notificationIntent = new Intent(this,
                AppointmentReminderNotificationPublisher.class);
        return PendingIntent.getBroadcast(this, appointmentReminderNotificationTimeMin,
                notificationIntent, 0);
    }

    // Launched everyTime a update to the set of appointments of the main user change,
    // so that the appointment reminder notification time of apparition are consistent with
    // the database.
    private void mainUserAppointmentsListenerUpdate(@NonNull Set<String> o) {
        unsetSetUpAppointmentsThatTheUserHasLeave(o);
        //add listener to all the appointments that do not have listener set up yet
        setUpListenersForMainUserAppointments(o);
    }

    /**
     * Adds listener to all the appointments that do not have a listener set up yet
     *
     * @param userAppointmentsId the appointments id the main user has.
     */
    private void setUpListenersForMainUserAppointments(@NonNull Set<String> userAppointmentsId) {
        for (String appointmentId : userAppointmentsId) {
            if (!appointmentStartTimeListeners.containsKey(appointmentId)) {
                LongValueListener startTimeValueListener = (Long startTime) -> {
                    Intent updateNotification = new Intent(this,
                            AppointmentReminderNotificationService.class);
                    updateNotification.putExtra(INTENT_KEY_EXTRA_START_TIME, startTime);
                    updateNotification.putExtra(INTENT_KEY_EXTRA_APPOINTMENT_ID, appointmentId);
                    startService(updateNotification);
                };
                new DatabaseAppointment(appointmentId).getStartTimeAndThen(startTimeValueListener);
                appointmentStartTimeListeners.put(appointmentId, startTimeValueListener);

            }
        }
    }

    /**
     * Removes all the appointment reminder notifications that are set up in the system but that
     * doesn't exist for the main user anymore. And update correctly the data used by the service.
     *
     * @param userAppointmentsId the appointments id the main user has.
     */
    private void unsetSetUpAppointmentsThatTheUserHasLeave(@NonNull Set<String> userAppointmentsId) {
        SharedPreferences localSetUpAppointmentReminderNotifications = getLocalSharedPreference();
        ArrayList<String> appointmentToRemove = getSetUpAppointmentsToRemove(userAppointmentsId,
                localSetUpAppointmentReminderNotifications);
        removeSetUpAppointments(localSetUpAppointmentReminderNotifications, appointmentToRemove);
    }

    @NonNull
    private ArrayList<String> getSetUpAppointmentsToRemove(@NonNull Set<String> userAppointmentsId,
                                                           @NonNull SharedPreferences localSetUpAppointmentReminderNotifications) {

        Set<String> localSetUpAppointments =
                localSetUpAppointmentReminderNotifications.getAll().keySet();
        ArrayList<String> appointmentsToRemove = new ArrayList<>();
        for (String appointmentId : localSetUpAppointments) {
            if (!userAppointmentsId.contains(appointmentId)) {
                appointmentsToRemove.add(appointmentId);
            }
        }
        return appointmentsToRemove;
    }

    private void removeSetUpAppointments(@NonNull SharedPreferences localSetUpAppointmentReminderNotifications, @NonNull ArrayList<String> appointmentToRemove) {
        for (String appointmentId : appointmentToRemove) {
            if (appointmentStartTimeListeners.containsKey(appointmentId)) {
                new DatabaseAppointment(appointmentId)
                        .removeStartListener(appointmentStartTimeListeners.get(appointmentId));
                appointmentStartTimeListeners.remove(appointmentId);
            }
            removeAppointmentReminderNotification(appointmentId,
                    localSetUpAppointmentReminderNotifications);
        }
    }

    private void updateAppointmentReminderNotification(long startTime, String appointmentId) {
        if (startTime > System.currentTimeMillis()) {
            SharedPreferences localAppointmentsReminderTime = getLocalSharedPreference();
            int appointmentReminderNotificationTimeMin =
                    (int) (TimeUnit.MILLISECONDS.toMinutes(startTime) -
                            PreferenceManager.getDefaultSharedPreferences(this).getInt(
                                    this.getResources().getString(R.string.preference_key_appointments_reminder_notification_time_from_appointment_minutes),
                                    this.getResources().getInteger(R.integer.default_appointment_reminder_notification__time_from_appointment_min)));
            //if the appointment reminder is not setup at the correct time remove it
            if (localAppointmentsReminderTime.getInt(appointmentId, -1) != appointmentReminderNotificationTimeMin) {
                removeAppointmentReminderNotification(appointmentId, localAppointmentsReminderTime);
                localAppointmentsReminderTime.edit().putInt(appointmentId,
                        appointmentReminderNotificationTimeMin).apply();
            }
            AlarmManager alarmManager = getAlarmManager();
            // I don't need to check if the time of reminder is already pass, indeed
            // alarmManager.setExact, will trigger directly the alarm if the time passed as argument
            // is greater than the current time
            alarmManager.setExact(AlarmManager.RTC_WAKEUP,
                    TimeUnit.MINUTES.toMillis(appointmentReminderNotificationTimeMin),
                    getReminderNotificationPendingIntent(appointmentReminderNotificationTimeMin));
        }
    }

}