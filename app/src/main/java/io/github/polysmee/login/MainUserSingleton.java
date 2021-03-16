package io.github.polysmee.login;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import io.github.polysmee.R;
import io.github.polysmee.database.DatabaseUser;
import io.github.polysmee.interfaces.Appointment;
import io.github.polysmee.interfaces.User;
import io.github.polysmee.notification.AppointmentReminderNotificationPublisher;

//The logged in user representation for the app
public final class MainUserSingleton implements User {

    private static DatabaseUser databaseUser;
    private static MainUserSingleton inst;
    private static Context mContext;

    private MainUserSingleton(Context context, String id) {
        databaseUser = new DatabaseUser(id);
        mContext = context.getApplicationContext();
    }

    //return the main user singleton object

    /**
     * @param context A context of the application
     * @return MainUserSingleton the singleton representing the logged in user of the app
     * @throws NullPointerException
     * @throws IllegalArgumentException Thrown if the argument is null
     */
    public static MainUserSingleton getInstance(Context context) throws NullPointerException { //maybe replace with optional ? throw is very rare so not sure
        if (context == null) {
            throw new IllegalArgumentException("The context argument passed to construct the MainUser singleton is null");
        }
        if (inst == null) {
            inst = new MainUserSingleton(context, FirebaseAuth.getInstance().getCurrentUser().getUid());
        }
        return inst;
    }

    /**
     * return the time of when the reminder notification should pop in EPOCH time as specified in
     * the values resources, in appointmentReminderNotification.xml or
     * the appointment time if the specification is not valid
     **/
    private static long getTimeOfReminderNotification(Appointment appointment) {
        assert appointment != null;
        assert mContext != null;
        long to_return = appointment.getStartTime() - mContext.getResources().getInteger(R.integer.appointment_reminder_notification_time_from_appointment_ms);
        return to_return < appointment.getStartTime() ? to_return : appointment.getStartTime();
    }

    /**
     * The function would give to any reminder notification with the same minute start time the same return value, two appointment with different minute start time
     * would not have the same appointmentReminderStartTimeId if the difference between the earliest and the furthest appointment is smaller than 2^32 minutes, which is a bit
     * more than 8171 year. So we consider for this application the appointmentReminderStartID to be a injective function from minutes to int.
     *
     * @param appointment The appointment to calculate the start Time id
     * @return the start time id of the appointment given it's current state
     */
    private static int appointmentReminderStartTimeId(Appointment appointment) {
        assert appointment != null;
        return (int) TimeUnit.MILLISECONDS.toMinutes(appointment.getDuration());
    }

    private boolean existAppointmentWithSameStartTimeId(Appointment appointment) {
        assert databaseUser != null;
        assert appointment != null;
        Set<Appointment> appointments = databaseUser.getAppointments();
        long startTimeId = appointmentReminderStartTimeId(appointment);
        for (Appointment userAppointment : appointments) {
            if (appointmentReminderStartTimeId(userAppointment) == startTimeId) {
                return true;
            }
        }
        return false;
    }

    /*
     * The return pendingIntent is uniquely identify by the android system by the start time of the appointemnt
     */
    private PendingIntent getReminderNotificationPendingIntent(Appointment appointment) {
        assert appointment != null;
        assert mContext != null;
        Intent notificationIntent = new Intent(mContext, AppointmentReminderNotificationPublisher.class);
        //TODO select the right flag to put in getBroadcast
        //appointmentReminderStartTimeId(appointment) is passed as requestCode so that the reminder notification pending Intent is uniquely determine by
        //the appointment start time
        return PendingIntent.getBroadcast(mContext, appointmentReminderStartTimeId(appointment), notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
    }


    /**
     * Retrieves the user's id
     *
     * @return user's id, not null
     */
    @Override
    public String getId() {
        assert databaseUser != null;
        return databaseUser.getId();
    }

    /**
     * Retrieves the user's name
     *
     * @return user's name
     */
    @Override
    public String getName() {
        assert databaseUser != null;
        return databaseUser.getName();
    }

    /**
     * Retrieves the user's surname
     *
     * @return user's surname
     */
    @Override
    public String getSurname() {
        assert databaseUser != null;
        return databaseUser.getSurname();
    }

    /**
     * Retrieves the set of the upcoming appointments for this user
     *
     * @return user's set of appointment in an unmodifiable set
     */
    @Override
    public Set<Appointment> getAppointments() {
        assert databaseUser != null;
        return databaseUser.getAppointments();
    }

    /**
     * Adds the given appointment to the set of appointments,
     * and set the reminder notification to appear at the time specified in specified in
     * the values resources, in appointmentReminderNotification.xml
     *
     * @param newAppointment the appointment to be added
     * @throws IllegalArgumentException Thrown if the argument is null
     */
    @Override
    public void addAppointment(Appointment newAppointment) {
        if (newAppointment == null) {
            throw new IllegalArgumentException("The appointment argument to add to the main user is null");
        }
        assert databaseUser != null;
        databaseUser.addAppointment(newAppointment);
        long timeOfNotification = getTimeOfReminderNotification(newAppointment);
        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(mContext.ALARM_SERVICE);
        alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timeOfNotification, getReminderNotificationPendingIntent(newAppointment));

    }


    /**
     * Removes the given appointment to the set of appointments of the user
     * and remove the associated reminder notification
     *
     * @param appointment the appointment to be removed
     * @throws IllegalArgumentException Thrown if the argument is null
     */
    @Override
    public void removeAppointment(Appointment appointment) {
        if (appointment == null) {
            throw new IllegalArgumentException("The appointment argument to remove from the main user is null");
        }
        assert databaseUser != null;
        databaseUser.removeAppointment(appointment);
        if (!existAppointmentWithSameStartTimeId(appointment)) {
            AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(mContext.ALARM_SERVICE);
            alarmManager.cancel(getReminderNotificationPendingIntent(appointment));
        }

    }
}