package io.github.polysmee.calendar.googlecalendarsync;

import android.content.Context;
import android.content.Intent;
import android.provider.CalendarContract;
import android.util.Log;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.AclRule;
import com.google.api.services.calendar.model.CalendarList;
import com.google.api.services.calendar.model.CalendarListEntry;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;

import java.io.IOException;

import io.github.polysmee.calendar.CalendarAppointmentInfo;


public class GoogleCalendarUtilities {

    public static void printAllCalendar(Context context) throws IOException {
        CalendarList list = GoogleCalendarService.getService(context).calendarList().list().execute();
        for (CalendarListEntry entry : list.getItems())
            Log.d("Calendar", entry.toPrettyString());
    }

    public static void deleteCalendar(Context context, String calendarId) throws IOException {
        GoogleCalendarService.getService(context).calendars().delete(calendarId).execute();
    }

    public static void deleteEvent(Context context, String calendarId, String eventId) throws IOException {
        GoogleCalendarService.getService(context).events().delete(calendarId, eventId).execute();
    }

    public static void addUserToCalendar(Context context, String calendarId, String userEmail) throws IOException {
        AclRule rule = new AclRule();
        com.google.api.services.calendar.model.AclRule.Scope scope = new com.google.api.services.calendar.model.AclRule.Scope();
        scope.setType("user").setValue(userEmail);
        rule.setScope(scope).setRole("reader");

        // Insert new access rule
        GoogleCalendarService.getService(context).acl().insert(calendarId, rule).execute();
    }

    public static String createCalendar(Context context, String userEmail) throws IOException {
        com.google.api.services.calendar.model.Calendar newCalendar = new com.google.api.services.calendar.model.Calendar();
        String calendarName = "Polysmee appointments - " + userEmail;
        newCalendar.setSummary(calendarName);
        com.google.api.services.calendar.model.Calendar createdCalendar = GoogleCalendarService.getService(context).calendars().insert(newCalendar).execute();

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

    public static String addEventToCalendar(Context context, String calendarId, Event event) throws IOException {
        Event addedEvent = GoogleCalendarService.getService(context).events().insert(calendarId, event).execute();
        return addedEvent.getId();
    }

    public static void updateEvent(Context context, String calendarId, String eventId,
                                   String title, String course, Long startTime, Long duration) throws IOException {
        Event event = GoogleCalendarService.getService(context).events().get(calendarId, eventId).execute();

        if (title != null && !title.equals(""))
            event.setSummary(title);

        if (course != null && !course.equals("")) {
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

        GoogleCalendarService.getService(context).events().update(calendarId, event.getId(), event).execute();
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
