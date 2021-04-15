package io.github.polysmee.notification;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;


import com.google.android.gms.tasks.Tasks;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.schibsted.spain.barista.rule.cleardata.ClearPreferencesRule;

import static com.schibsted.spain.barista.interaction.BaristaSleepInteractions.sleep;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.mockito.Mockito.*;



import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.github.polysmee.R;
import io.github.polysmee.appointments.AppointmentActivity;
import io.github.polysmee.calendar.CalendarActivity;
import io.github.polysmee.database.DatabaseFactory;
import io.github.polysmee.login.AuthenticationFactory;
import io.github.polysmee.login.MainUserSingleton;
import io.github.polysmee.settings.SettingsActivity;


@RunWith(AndroidJUnit4.class)
public class AppointmentReminderNotificationSetupListenerTest {
    private static final String mainAppointmentTitle = "It's a title";
    private static String mainAppointmentId = "nbcwxuhcjgvwxcuftyqf";
    private static final String mainAppointmentCourse = "Totally not SWENG";
    private static Context context = ApplicationProvider.getApplicationContext();

    // Clear all app's SharedPreferences
    @Rule
    public ClearPreferencesRule clearPreferencesRule = new ClearPreferencesRule();

    //TODO lance scnerario close scenario see mathis test

    @BeforeClass
    public static void setup() throws Exception{
        DatabaseFactory.setTest();
        AuthenticationFactory.setTest();
        FirebaseApp.clearInstancesForTest();
        FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext());
        Tasks.await(AuthenticationFactory.getAdaptedInstance().createUserWithEmailAndPassword("AppointmentReminderNotificationSetupListenerTestt@gmail.com", "fakePassword"));
        getTestedMainAppointementReference().child("title").setValue(mainAppointmentTitle);
        getTestedMainAppointementReference().child("course").setValue(mainAppointmentCourse);
        getTestedMainAppointementReference().child("owner").setValue(MainUserSingleton.getInstance().getId());
        getTestedMainAppointementReference().child("participants").child(MainUserSingleton.getInstance().getId()).setValue(true);

        DatabaseFactory.getAdaptedInstance().getReference("users").child(MainUserSingleton.getInstance().getId()).child("appointments").child(mainAppointmentId).setValue(true);
    }


    private static DatabaseReference getTestedMainAppointementReference(){
        return DatabaseFactory.getAdaptedInstance().getReference("appointments").child(mainAppointmentId);
    }


    @RequiresApi(api = Build.VERSION_CODES.Q)
    private static PendingIntent getReminderNotificationPendingIntent(@NonNull Context context, @NonNull String appointmentID) {
        Intent notificationIntent = new Intent(context, AppointmentReminderNotificationPublisher.class).setIdentifier(appointmentID);
        return PendingIntent.getBroadcast(context, 0, notificationIntent, 0);
    }

    private long appointmentReminderNotificationTimeMs(long mainAppointemntStartTime){
      return  mainAppointemntStartTime - TimeUnit.MINUTES.toMillis(context.getResources().getInteger(R.integer.default_appointment_reminder_notification__time_from_appointment_min));
    };



    @Test
    public void reminderSetupInAdvanceOfTheReminderWindow() {
        AlarmManager mockedAlarmManager = mock(AlarmManager.class);
        Intent intent = new Intent(context, SettingsActivity.class);
        long mainAppointemntStartTime =TimeUnit.MINUTES.toMillis(327)+ System.currentTimeMillis();
        getTestedMainAppointementReference().child("start").setValue(mainAppointemntStartTime);
        try (ActivityScenario<AppointmentActivity> ignored = ActivityScenario.launch(intent)) {
            AppointmentReminderNotificationSetupListener.appointmentReminderNotificationSetListeners(context, mockedAlarmManager);
            verify(mockedAlarmManager).setExact(anyChar(), anyChar(), any());
        }

        //TODO addNewAppointment
    }
/**
    @Test
    public void reminderSetupInTheReminderWindow(){
        AlarmManager mockedAlarmManager = mock(AlarmManager.class);
        Intent intent = new Intent(context, SettingsActivity.class);
        long mainAppointemntStartTime =System.currentTimeMillis()+TimeUnit.MINUTES.toMillis(context.getResources().getInteger(R.integer.default_appointment_reminder_notification__time_from_appointment_min))/2;
        getTestedMainAppointementReference().child("start").setValue(mainAppointemntStartTime);
        try (ActivityScenario<AppointmentActivity> ignored = ActivityScenario.launch(intent)) {
            AppointmentReminderNotificationSetupListener.appointmentReminderNotificationSetListeners(context, mockedAlarmManager);
            verify(mockedAlarmManager).setExact(AlarmManager.RTC_WAKEUP, appointmentReminderNotificationTimeMs(mainAppointemntStartTime), getReminderNotificationPendingIntent(context, mainAppointmentId));
        }
    }

    @Test
    public void reminderAppointmentRemoved(){
        AlarmManager mockedAlarmManager = mock(AlarmManager.class);
        Intent intent = new Intent(context, SettingsActivity.class);
        long mainAppointemntStartTime =TimeUnit.MINUTES.toMillis(34347)+ System.currentTimeMillis();
        getTestedMainAppointementReference().child("start").setValue(mainAppointemntStartTime);
        try (ActivityScenario<AppointmentActivity> ignored = ActivityScenario.launch(intent)) {
            AppointmentReminderNotificationSetupListener.appointmentReminderNotificationSetListeners(context, mockedAlarmManager);
            DatabaseFactory.getAdaptedInstance().getReference("users").child(MainUserSingleton.getInstance().getId()).child("appointments").child(mainAppointmentId).removeValue();
            sleep(2, SECONDS);
            verify(mockedAlarmManager).cancel(getReminderNotificationPendingIntent(context, mainAppointmentId));
        }
        DatabaseFactory.getAdaptedInstance().getReference("users").child(MainUserSingleton.getInstance().getId()).child("appointments").child(mainAppointmentId).setValue(true);
    }

    @Test public void reminderAppointmentRemovedWhileAppNotOppen(){
        Intent intent = new Intent(context, SettingsActivity.class);
        long mainAppointemntStartTime =TimeUnit.MINUTES.toMillis(34347)+ System.currentTimeMillis();
        getTestedMainAppointementReference().child("start").setValue(mainAppointemntStartTime);
        try (ActivityScenario<AppointmentActivity> ignored = ActivityScenario.launch(intent)) {
            AppointmentReminderNotificationSetupListener.appointmentReminderNotificationSetListeners(context, (AlarmManager) context.getSystemService(Context.ALARM_SERVICE));
        }
        AlarmManager mockedAlarmManager = mock(AlarmManager.class);
        DatabaseFactory.getAdaptedInstance().getReference("users").child(MainUserSingleton.getInstance().getId()).child("appointments").child(mainAppointmentId).removeValue();
        try (ActivityScenario<AppointmentActivity> ignored = ActivityScenario.launch(intent)) {
            AppointmentReminderNotificationSetupListener.appointmentReminderNotificationSetListeners(context, (AlarmManager) context.getSystemService(Context.ALARM_SERVICE));
            verify(mockedAlarmManager).cancel(getReminderNotificationPendingIntent(context, mainAppointmentId));
        }
        DatabaseFactory.getAdaptedInstance().getReference("users").child(MainUserSingleton.getInstance().getId()).child("appointments").child(mainAppointmentId).setValue(true);
    }

    @Test
    public void reminderAppointmentRemovedAndAppointmentsStartTimeChange() {

    }
**/

}
