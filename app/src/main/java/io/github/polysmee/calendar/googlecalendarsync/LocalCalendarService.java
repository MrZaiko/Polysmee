package io.github.polysmee.calendar.googlecalendarsync;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class LocalCalendarService implements CalendarService {
    private static final Map<String, Map<String, Event>> calendarList = new HashMap<>();
    private boolean faulty = false;

    public LocalCalendarService(boolean faulty) {
        this.faulty = faulty;
    }

    @Override
    public void deleteCalendar(String calendarId) throws IOException {
        if (faulty)
            throw new IOException();

        if (!calendarList.containsKey(calendarId)) {
            calendarList.put(calendarId, new HashMap<>());
        }

        calendarList.remove(calendarId);
    }

    @Override
    public void deleteEvent(String calendarId, String eventId) throws IOException {
        if (faulty)
            throw new IOException();

        if (!calendarList.containsKey(calendarId)) {
            calendarList.put(calendarId, new HashMap<>());
            calendarList.get(calendarId).put(eventId, new Event());
        }

        calendarList.get(calendarId).remove(eventId);
    }

    @Override
    public void addUserToCalendar(String calendarId, String userEmail) throws IOException {
        if (faulty)
            throw new IOException();
    }

    @Override
    public String createCalendar(String userEmail) throws IOException {
        if (faulty)
            throw new IOException();

        calendarList.put(userEmail, new HashMap<>());
        return userEmail;
    }

    @Override
    public Event createEvent(String title, String course, long startTime, long duration) {
        Event event = new Event().setSummary(title);

        event.setId(String.valueOf(new Random().nextInt()));

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
        if (faulty)
            throw new IOException();

        if (!calendarList.containsKey(calendarId)) {
            calendarList.put(calendarId, new HashMap<>());
        }
        calendarList.get(calendarId).put(event.getId(), event);
        return event.getId();
    }

    @Override
    public void updateEvent(String calendarId, String eventId, String title, String course, Long startTime, Long duration) throws IOException {
        if (faulty)
            throw new IOException();

        if (!calendarList.containsKey(calendarId)) {
            calendarList.put(calendarId, new HashMap<>());
            calendarList.get(calendarId).put(eventId, new Event());
        }

        Event event = calendarList.get(calendarId).get(eventId);

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

        calendarList.get(calendarId).remove(eventId);
        calendarList.get(calendarId).put(eventId, event);
    }
}
