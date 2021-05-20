package io.github.polysmee.calendar.googlecalendarsync;

import android.content.Context;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.AclRule;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;

import java.io.IOException;

public interface CalendarService {
    void deleteCalendar(String calendarId) throws IOException;

    void deleteEvent(String calendarId, String eventId) throws IOException;

    void addUserToCalendar(String calendarId, String userEmail) throws IOException;

    String createCalendar(String userEmail) throws IOException;

    Event createEvent(String title, String course, long startTime, long duration);

    String addEventToCalendar(String calendarId, Event event) throws IOException;

    void updateEvent(String calendarId, String eventId,
                     String title, String course, Long startTime, Long duration) throws IOException;
}
