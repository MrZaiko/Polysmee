package io.github.polysmee.calendar.googlecalendarsync;

import android.content.Context;
import android.icu.util.Calendar;

import com.google.api.services.calendar.model.Event;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.function.Consumer;

import io.github.polysmee.agora.video.Call;
import io.github.polysmee.database.Appointment;
import io.github.polysmee.database.User;


public class CalendarUtilities {
    private static boolean test = false;
    private static CalendarService service = null;

    public static void setTest(boolean test) {
        CalendarUtilities.test = test;
    }

    private static void createService(Context context) {
        if (service == null) {
            if (!test)
                service = new GoogleCalendarService(context);
            else
                service = new LocalCalendarService();
        }
    }

    public static void deleteAppointmentFromCalendar(Context context, String calendarId, String eventId,
                                                     Runnable onSuccess, Runnable onError) {
        createService(context);

        new Thread(() -> {
            try {
                service.deleteEvent(calendarId, eventId);
            } catch (IOException e) {
                onError.run();
                return;
            }
            onSuccess.run();
        }).start();
    }

    public static void createCalendar(Context context, String email, Consumer<String> onSuccess, Runnable onError) {
        createService(context);

        new Thread(() -> {
            String calendarId = "";
            try {
                calendarId = service.createCalendar(email);
            } catch (IOException e) {
                onError.run();
                return;
            }
            onSuccess.accept(calendarId);
        }).start();
    }

    public static void deleteCalendar(Context context, String calendarId, Runnable onSuccess, Runnable onError) {
        createService(context);

        new Thread(() -> {
            try {
                service.deleteCalendar(calendarId);
            } catch (IOException e) {
                onError.run();
                return;
            }
            onSuccess.run();
        }).start();
    }

    public static void addUserToCalendar(Context context, String calendarId, String email, Runnable onSuccess, Runnable onError) {
        createService(context);

        new Thread(() -> {
            try {
                service.addUserToCalendar(calendarId, email);
            } catch (IOException e) {
                onError.run();
                return;
            }
            onSuccess.run();
        }).start();
    }

    public static void updateAppointmentOnCalendar(Context context, String calendarId, String eventId,
                                                   String title, String course, Long startTime, Long duration,
                                                   Runnable onSuccess, Runnable onError) {
        createService(context);

        new Thread(() -> {
            try {
                service.updateEvent(calendarId, eventId,
                        title, course, startTime,duration);
            } catch (IOException e) {
                onError.run();
                return;
            }
            onSuccess.run();
        }).start();
    }

    public static void addAppointmentToCalendar(Context context, String calendarId, String title, String course,
                                                long startTime, long duration, Consumer<String> onSuccess, Runnable onError) {
        createService(context);

        new Thread(() -> {
            Event newApt = service.createEvent(title, course, startTime, duration);
            String eventId = "";
            try {
                eventId = service.addEventToCalendar(calendarId, newApt);
            } catch (IOException e) {
                onError.run();
            }
            onSuccess.accept(eventId);
        }).start();
    }
}
