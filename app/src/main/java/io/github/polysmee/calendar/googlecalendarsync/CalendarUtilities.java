package io.github.polysmee.calendar.googlecalendarsync;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.google.api.services.calendar.model.Event;

import java.io.IOException;
import java.util.function.Consumer;

import io.github.polysmee.R;
import io.github.polysmee.database.Appointment;
import io.github.polysmee.database.User;
import io.github.polysmee.login.MainUser;


@RequiresApi(api = Build.VERSION_CODES.N)
public class CalendarUtilities {

    public static void deleteAppointmentFromCalendar(Context context, Appointment appointment, String calendarId, String eventId, Consumer<IOException> onError) {
        new Thread(() -> {
            try {
                GoogleCalendarUtilities.deleteEvent(context, calendarId, eventId);
                MainUser.getMainUser().setAppointmentEventId(appointment, "");
            } catch (IOException e) {
                onError.accept(e);
            }
        }).start();
    }

    public static void updateAppointmentOnCalendar(Context context, String calendarId, String eventId,
                                                   String title, String course, Long startTime, Long duration,
                                                   Consumer<IOException> onError) {
        new Thread(() -> {
            try {
                GoogleCalendarUtilities.updateEvent(context, calendarId, eventId,
                        title, course, startTime,duration);
            } catch (IOException e) {
                onError.accept(e);
            }
        }).start();
    }

    public static void addAppointmentToCalendar(Context context, String calendarId, Appointment apt,
                                                User user, Consumer<IOException> onError) {

        apt.getTitle_Once_AndThen(title -> {
            apt.getCourse_Once_AndThen( course -> {
                apt.getStartTime_Once_AndThen( startTime -> {
                    apt.getDuration_Once_AndThen( duration -> {
                        new Thread(() -> {
                            try {
                                Event createdEvent = GoogleCalendarUtilities.createEvent(title, course, startTime, duration);
                                String eventId = GoogleCalendarUtilities.addEventToCalendar(context, calendarId, createdEvent);
                                Log.d("CALENDAR", "HERE");
                                user.setAppointmentEventId(apt, eventId);
                            } catch (IOException e) {
                                onError.accept(e);
                            }
                        }).start();
                    });
                });
            });
        });
    }

    public static void addAppointmentToCalendar(Context context, Appointment appointment, String calendarId,
                                                String title, String course, long startTime,
                                                long duration, Consumer<IOException> onError) {
        new Thread(() -> {
            Event newApt = GoogleCalendarUtilities.createEvent(title, course, startTime, duration);
            try {
                String eventId = GoogleCalendarUtilities.addEventToCalendar(context, calendarId, newApt);
                MainUser.getMainUser().setAppointmentEventId(appointment, eventId);
            } catch (IOException e) {
                onError.accept(e);
            }
        }).start();
    }
}
