package io.github.polysmee.calendar.googlecalendarsync;

import android.content.Context;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import io.github.polysmee.R;

public class GoogleCalendarService {
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static Calendar service;

    public static Calendar getService(Context context) {
        if (service == null) {
            final NetHttpTransport HTTP_TRANSPORT;
            HTTP_TRANSPORT = new NetHttpTransport();

            GoogleCredential credential = null;
            try {
                credential = GoogleCredential.fromStream(context.getResources().openRawResource(R.raw.credentials)).createScoped(Collections.singleton(CalendarScopes.CALENDAR));
            } catch (IOException e) {
                e.printStackTrace();
            }

            service = new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                    .setApplicationName(String.valueOf(R.string.app_name))
                    .build();

        }
        return service;
    }
}
