package io.github.polysmee.calendar.googlecalendarsync;

import com.google.api.services.calendar.model.Event;

import java.io.IOException;

public class LocalCalendarService implements CalendarService {
    @Override
    public void deleteCalendar(String calendarId) throws IOException {

    }

    @Override
    public void deleteEvent(String calendarId, String eventId) throws IOException {

    }

    @Override
    public void addUserToCalendar(String calendarId, String userEmail) throws IOException {

    }

    @Override
    public String createCalendar(String userEmail) throws IOException {
        return null;
    }

    @Override
    public Event createEvent(String title, String course, long startTime, long duration) {
        return null;
    }

    @Override
    public String addEventToCalendar(String calendarId, Event event) throws IOException {
        return null;
    }

    @Override
    public void updateEvent(String calendarId, String eventId, String title, String course, Long startTime, Long duration) throws IOException {

    }
}
