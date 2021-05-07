package io.github.polysmee.znotification;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import io.github.polysmee.database.DatabaseAppointment;
import io.github.polysmee.database.databaselisteners.LongValueListener;
import io.github.polysmee.database.databaselisteners.StringSetValueListener;
import io.github.polysmee.login.MainUser;

public class AppointmentReminderNotificationListenerServiceSingleton extends Service {
    private final static Map<String, LongValueListener> appointmentStartTimeListeners = new HashMap<>();
    private static StringSetValueListener mainUserStringSetValueListener;
    private static boolean isListenerSetup = false;
    private static boolean isNotificationSetterEnable = true;

    protected void


}
