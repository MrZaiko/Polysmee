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
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import io.github.polysmee.R;
import io.github.polysmee.database.DatabaseAppointment;
import io.github.polysmee.database.databaselisteners.LongValueListener;
import io.github.polysmee.database.databaselisteners.StringSetValueListener;
import io.github.polysmee.login.MainUser;


public final class AppointmentReminderNotificationSetupListener extends Service{
    private final static Map<String, LongValueListener> appointmentStartTimeListeners = new HashMap<>();
    private static StringSetValueListener mainUserStringSetValueListener;
    private static boolean isListenerSetup = false;
    private static boolean isNotificationSetterEnable = true;

    //need to have those variable as we cannot pass Context and CurrentTime to the done function who need to be of a specified form since it will
    // be use as a lambda to have a StringSetValueListener
    private static AlarmManager alarmManager;

    /**
     *
     * @return the local SharePreferences used by this class
     */
    private SharedPreferences getlocalSharedPreference() {
        assert (mContext != null);

        return  mContext.getSharedPreferences(mContext.getResources().getString(R.string.sharedPreferenceKeyAppointmentReminderNotificationSetupListener), Context.MODE_PRIVATE);
    }

    /*
     * The return pendingIntent is uniquely identify by the android system by the reminder notification time of the appointment and the fact that
     * it is a broadcast for AppointmentReminderNotificationPublisher
     *
     * @param appointmentReminderNotificationTimeMin the epoch time in minutes of when the notification should appear
     *
     */
    private PendingIntent getReminderNotificationPendingIntent(int appointmentReminderNotificationTimeMin) {
        assert mContext!=null;
        Intent notificationIntent = new Intent(mContext, AppointmentReminderNotificationPublisher.class);
        return PendingIntent.getBroadcast(mContext, appointmentReminderNotificationTimeMin, notificationIntent, 0);
    }


    /**
     * Remove the reminder notification appointment of the appointment from alarmManager and update the local state of setted reminder appointment.
     * If the reminder notification of the appointment is not setted up do nothing
     * @param appointmentId the appointmentId of the appointment to remove the reminder notification
     * @param localAppointmentsReminderTime the SharedPreference that contain all the reminder notification already setted up
     */
    private void removeAppointmentReminderNotification(@NonNull String appointmentId, @NonNull SharedPreferences localAppointmentsReminderTime ){
        assert alarmManager!=null;
        int appointmentNotificationTimeMin = localAppointmentsReminderTime.getInt(appointmentId, -1);

        //not setted up if it take default value
        if (appointmentNotificationTimeMin==-1){
            return;
        }
        localAppointmentsReminderTime.edit().remove(appointmentId).apply();
        Set<String> appointmentsId = localAppointmentsReminderTime.getAll().keySet();
        for(String settedAppointmentId : appointmentsId){
            if ( appointmentNotificationTimeMin == localAppointmentsReminderTime.getInt(settedAppointmentId, -2)){
                return;
            }
        }
        alarmManager.cancel(getReminderNotificationPendingIntent( appointmentNotificationTimeMin));
    }

    //TODO comment this function
    private void mainUserAppointmentsListenerUpdate(){

    }
    //launch at the start so that the reminder set are consistent with the database. i.e remove the reminder that are set but that the user are no longer part of
    private static void onDone(Set<String> o) {
        //read the only public function first to understand more easily
        assert (alarmManager != null);
        assert (mContext != null);
        SharedPreferences localAppointmentsReminderTime = getlocalSharedPreference();
        //remove all the appointments reminder that are setup in the system but that doesn't exist for the main user anymore
        Set<String> localAppointments = localAppointmentsReminderTime.getAll().keySet();
        ArrayList<String> toRemove = new ArrayList<>();
        for (String appointmentId : localAppointments) {
            if (!o.contains(appointmentId)) {
                toRemove.add(appointmentId);
            }
        }
        for (String appointmentId : toRemove) {
            if (appointmentStartTimeListeners.containsKey(appointmentId)) {
                new DatabaseAppointment(appointmentId).removeStartListener(appointmentStartTimeListeners.get(appointmentId));
                appointmentStartTimeListeners.remove(appointmentId);
            }
            removeAppointmentReminderNotification(appointmentId, localAppointmentsReminderTime);
        }
        //add listener to all the appointments that do not have listener set up yet
        SharedPreferences.Editor localAppointmentsReminderTimeEditor = localAppointmentsReminderTime.edit();
        for (String appointmentId : o) {
            if (!appointmentStartTimeListeners.containsKey(appointmentId)) {
                LongValueListener startTimeValueListener = (long startTime) -> {
                    //TODO do the same as main user appointments listener to pass a correct listener
                    int appointmentReminderNotificationTimeMin = (int) (TimeUnit.MILLISECONDS.toMinutes(startTime) - PreferenceManager.getDefaultSharedPreferences(mContext).getInt(
                            mContext.getResources().getString(R.string.preference_key_appointments_reminder_notification_time_from_appointment_minutes),
                            mContext.getResources().getInteger(R.integer.default_appointment_reminder_notification__time_from_appointment_min)));
                    //I don't need to check if the time of reminder is already pass, indeed setExact, which has the same semantic in that point that set (methods of alarmManger),
                    //will trigger directly the alarm if the time passed as argument is greater than the current time
                    if (startTime > System.currentTimeMillis()) {
                        //if the appointment reminder is not setup at the correct time remove it
                        if(localAppointmentsReminderTime.getInt(appointmentId, -1)!=appointmentReminderNotificationTimeMin){
                            removeAppointmentReminderNotification(appointmentId, localAppointmentsReminderTime);
                            localAppointmentsReminderTimeEditor.putInt(appointmentId, appointmentReminderNotificationTimeMin).apply();
                        }
                        alarmManager.setExact(AlarmManager.RTC_WAKEUP, TimeUnit.MINUTES.toMillis(appointmentReminderNotificationTimeMin), getReminderNotificationPendingIntent(appointmentReminderNotificationTimeMin));
                    }
                };
                new DatabaseAppointment(appointmentId).getStartTimeAndThen(startTimeValueListener);
                appointmentStartTimeListeners.put(appointmentId, startTimeValueListener);

            }
        }
    }

    /**
     * This function should be called as soon as the MainUserCurrentrSingleton exist so that the reminder of appointments can be coherent with the database Value,
     * i.e. as soon as possible but it need MainUsercurrentSingleton
     * @param context The Context in which to perform the setup
     * @param alarmManager the AlarmManager to use that will trigger a AppointmentReminderNotificaitonPublisher
     *
     */
    public static void appointmentReminderNotificationSetListeners(@NonNull Context context, @NonNull AlarmManager alarmManager) {
        //to be sure that the listener will be setup only once, more robustness
        if (isListenerSetup || !isNotificationSetterEnable) {
            return;
        }
        //set the variable that would be used in other function more specifically the done function
        mContext = context;
        AppointmentReminderNotificationSetupListener.alarmManager =alarmManager;
        mainUserStringSetValueListener = AppointmentReminderNotificationSetupListener::onDone;
        MainUser.getMainUser().getAppointmentsAndThen(mainUserStringSetValueListener);
        isListenerSetup = true;
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        assert mainUserStringSetValueListener!=null;
        super.onDestroy();
        MainUser.getMainUser().removeAppointmentsListener(mainUserStringSetValueListener);
        Set<Map.Entry<String, LongValueListener>> appointmentIdAndStartTimeListeners = appointmentStartTimeListeners.entrySet();
        for(Map.Entry<String,LongValueListener> appointmentIdAndStartTimeListener : appointmentIdAndStartTimeListeners){
            new DatabaseAppointment(appointmentIdAndStartTimeListener.getKey()).removeStartListener(appointmentIdAndStartTimeListener.getValue());
        }
    }

    /**
     * Set the value of isNotificationSetterEnable to the given value. If isNotificationSetterEnable is false before calling appointmentReminderNotificationSetListeners
     * then the appointment reminder notification listeners will not be set. By default isNotificationSetterEnable has true value.
     *
     * @param value the value to give isNotificationSetterEnable
     */
    public static void setIsNotificationSetterEnable(boolean value){
        isNotificationSetterEnable = value;
    }

    @Nullable
    @Override
    //we don't allow bindings
    public IBinder onBind(Intent intent) {
        return null;
    }

}