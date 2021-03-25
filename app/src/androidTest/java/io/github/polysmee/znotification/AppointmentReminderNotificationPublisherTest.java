package io.github.polysmee.znotification;

import android.content.Context;
import android.content.Intent;

import androidx.preference.PreferenceManager;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject2;
import androidx.test.uiautomator.Until;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeUnit;

import io.github.polysmee.R;
import io.github.polysmee.notification.AppointmentReminderNotificationPublisher;

import static org.junit.Assert.assertNotNull;

@RunWith(AndroidJUnit4.class)
public class AppointmentReminderNotificationPublisherTest {

    private final static long TIMEOUT = TimeUnit.SECONDS.toMillis(10);
    public Context context = ApplicationProvider.getApplicationContext();
    public final String notification_text = context.getResources().getString(R.string.appointment_reminder_notification_notification_text_prepend_time_left) + " "
            + PreferenceManager.getDefaultSharedPreferences(context).getInt(
            context.getResources().getString(R.string.preference_key_appointments_reminder_notification_time_from_appointment_minutes),
            context.getResources().getInteger(R.integer.default_appointment_reminder_notification__time_from_appointment_min))
            + context.getResources().getString(R.string.appointment_reminder_notification_notification_text_append_time_left);

    //@Before
    //@After
    public void resetStateNotification() {
        UiDevice uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        uiDevice.openNotification();
        UiObject2 clear_all_notification = uiDevice.findObject(By.desc("Clear all notifications."));
        if (clear_all_notification != null) {
            clear_all_notification.click();
        }
        Intent closeIntent = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        context.sendBroadcast(closeIntent);
    }

    @Test
    public void notification_launch_with_good_title_and_text() {
        UiDevice uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        AppointmentReminderNotificationPublisher publisher = new AppointmentReminderNotificationPublisher();
        Intent intent = new Intent(context, AppointmentReminderNotificationPublisher.class);
        publisher.onReceive(context, intent);
        String expectedAppName = context.getString(R.string.app_name);
        //uiDevice.openNotification();
        assertNotNull(uiDevice.wait(Until.hasObject(By.textStartsWith(expectedAppName)), TIMEOUT));
        assertNotNull(uiDevice.findObject(By.text(notification_text)));
    }
}