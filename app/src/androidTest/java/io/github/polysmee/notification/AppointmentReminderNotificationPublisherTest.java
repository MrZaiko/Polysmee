package io.github.polysmee.notification;

import android.content.Intent;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject2;
import androidx.test.uiautomator.Until;


import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeUnit;

import io.github.polysmee.R;
import io.github.polysmee.notification.AppointmentReminderNotificationPublisher;

import static org.junit.Assert.assertNotNull;

@RunWith(AndroidJUnit4.class)
public class AppointmentReminderNotificationPublisherTest {
    public final String notification_text = ApplicationProvider.getApplicationContext().getResources().getString(R.string.appointment_reminder_notification_notification_text_prepend_time_left)+" "+
            TimeUnit.MILLISECONDS.toMinutes(ApplicationProvider.getApplicationContext().getResources().getInteger(R.integer.appointment_reminder_notification_time_from_appointment_ms))+
            ApplicationProvider.getApplicationContext().getResources().getString(R.string.appointment_reminder_notification_notification_text_append_time_left);

    private final static long TIMEOUT = TimeUnit.SECONDS.toMillis(10);
    private void clearAllNotifications(UiDevice uiDevice) {
        uiDevice.openNotification();
        UiObject2 clear_all_notification = uiDevice.findObject(By.desc("Clear all notifications."));
        if (clear_all_notification!=null){
            clear_all_notification.click();
        }
    }
    @Test
    public void notification_launch_with_good_title_and_text(){
        UiDevice uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        //clearAllNotifications(uiDevice);
        AppointmentReminderNotificationPublisher publisher = new AppointmentReminderNotificationPublisher();
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), AppointmentReminderNotificationPublisher.class);
        publisher.onReceive(ApplicationProvider.getApplicationContext(), intent);
        String expectedAppName = ApplicationProvider.getApplicationContext().getString(R.string.app_name);
        uiDevice.openNotification();
        assertNotNull(uiDevice.wait(Until.hasObject(By.textStartsWith(expectedAppName)),TIMEOUT));
        assertNotNull(uiDevice.findObject(By.text(notification_text)));
        //clearAllNotifications(uiDevice);
    }
}
