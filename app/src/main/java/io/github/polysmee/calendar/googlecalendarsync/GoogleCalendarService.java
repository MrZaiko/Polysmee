package io.github.polysmee.calendar.googlecalendarsync;

import android.content.Context;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.AclRule;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;

import java.io.IOException;
import java.util.Collections;

import io.github.polysmee.R;

public class GoogleCalendarService implements CalendarService{
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static Calendar service;

    public GoogleCalendarService(Context context) {
        createService(context);
    }

    private static void createService(Context context) {
        if (service == null) {
            final NetHttpTransport HTTP_TRANSPORT;
            HTTP_TRANSPORT = new NetHttpTransport();

            GoogleCredential credential = null;
            try {
                credential = GoogleCredential
                        .fromStream(context.getResources().openRawResource(R.raw.credentials))
                        .createScoped(Collections.singleton(CalendarScopes.CALENDAR));
            } catch (IOException e) {
                e.printStackTrace();
            }

            service = new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                    .setApplicationName(String.valueOf(R.string.app_name))
                    .build();

        }
    }

    @Override
    public void deleteCalendar(String calendarId) throws IOException {
        service.calendars().delete(calendarId).execute();
    }

    @Override
    public void deleteEvent(String calendarId, String eventId) throws IOException {
        service.events().delete(calendarId, eventId).execute();
    }

    @Override
    public void addUserToCalendar(String calendarId, String userEmail) throws IOException {
        AclRule rule = new AclRule();
        com.google.api.services.calendar.model.AclRule.Scope scope = new com.google.api.services.calendar.model.AclRule.Scope();
        scope.setType("user").setValue(userEmail);
        rule.setScope(scope).setRole("reader");

        // Insert new access rule
        service.acl().insert(calendarId, rule).execute();
    }

    @Override
    public String createCalendar(String userEmail) throws IOException {
        com.google.api.services.calendar.model.Calendar newCalendar = new com.google.api.services.calendar.model.Calendar();
        String calendarName = "Polysmee appointments - " + userEmail;
        newCalendar.setSummary(calendarName);
        com.google.api.services.calendar.model.Calendar createdCalendar = service.calendars().insert(newCalendar).execute();

        return createdCalendar.getId();
    }

    @Override
    public Event createEvent(String title, String course, long startTime, long duration) {
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

    @Override
    public String addEventToCalendar(String calendarId, Event event) throws IOException {
        Event addedEvent = service.events().insert(calendarId, event).execute();
        return addedEvent.getId();
    }

    @Override
    public void updateEvent(String calendarId, String eventId, String title, String course,
                            Long startTime, Long duration) throws IOException {
        Event event = service.events().get(calendarId, eventId).execute();

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

        service.events().update(calendarId, event.getId(), event).execute();
    }
}
