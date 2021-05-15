package io.github.polysmee.calendar;

import android.content.Context;
import android.content.Intent;
import android.provider.CalendarContract;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.AclRule;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;

import java.io.IOException;


public class CalendarUtilities {

    public static void addUserToCalendar(Calendar service, String calendarId, String userEmail) throws IOException {
        AclRule rule = new AclRule();
        com.google.api.services.calendar.model.AclRule.Scope scope = new com.google.api.services.calendar.model.AclRule.Scope();
        scope.setType("user").setValue(userEmail);
        rule.setScope(scope).setRole("reader");

        // Insert new access rule
        service.acl().insert(calendarId, rule).execute();
    }

    public static String createCalendar(Calendar service, String userEmail) throws IOException {
        com.google.api.services.calendar.model.Calendar newCalendar = new com.google.api.services.calendar.model.Calendar();
        String calendarName = "Polysmee appointments - " + userEmail;
        newCalendar.setSummary(calendarName);
        com.google.api.services.calendar.model.Calendar createdCalendar = service.calendars().insert(newCalendar).execute();

        return createdCalendar.getId();
    }

    public static Event createEvent(String title, String course, long startTime, long duration) {
        Event event = new Event().setSummary(title);

        String description = "Course : " + course;
        event.setDescription(description);

        DateTime startDateTime = new DateTime(startTime);
        EventDateTime start = new EventDateTime().setDateTime(startDateTime);
        event.setStart(start);

        DateTime endDateTime = new DateTime(startTime+duration);
        EventDateTime end = new EventDateTime().setDateTime(endDateTime);
        event.setEnd(end);

        return event;
    }

    public static String addEventToCalendar(Calendar service, String calendarId, Event event) throws IOException {
        Event addedEvent = service.events().insert(calendarId, event).execute();
        return addedEvent.getId();
    }

    public static void updateEvent(Calendar service, String calendarId, String eventId,
                                   String title, String course, Long startTime, Long duration) throws IOException {
        Event event = service.events().get(calendarId, eventId).execute();

        if (title != null)
            event.setSummary(title);

        if (course != null) {
            String description = "Course : " + course;
            event.setDescription(description);
        }

        if (startTime != null) {
            DateTime startDateTime = new DateTime(startTime);
            EventDateTime start = new EventDateTime().setDateTime(startDateTime);
            event.setStart(start);
        }

        if (duration != null) {
            DateTime endDateTime = new DateTime(startTime+duration);
            EventDateTime end = new EventDateTime().setDateTime(endDateTime);
            event.setEnd(end);
        }

        service.events().update(calendarId, event.getId(), event).execute();
    }

    public static void exportAppointment(Context context, CalendarAppointmentInfo appointment) {
        String description = "Course: " + appointment.getCourse();

        Intent intent = new Intent(Intent.ACTION_INSERT)
                .setData(CalendarContract.Events.CONTENT_URI)
                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, appointment.getStartTime())
                .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, appointment.getStartTime() + appointment.getDuration())
                .putExtra(CalendarContract.Events.TITLE, appointment.getTitle())
                .putExtra(CalendarContract.Events.DESCRIPTION, description);
        context.startActivity(intent);
    }
}
