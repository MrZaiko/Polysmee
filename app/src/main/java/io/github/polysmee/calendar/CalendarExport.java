package io.github.polysmee.calendar;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.CalendarContract;
import android.util.Log;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.internal.AccountType;

import java.util.TimeZone;

import io.github.polysmee.database.Appointment;

import static android.provider.CalendarContract.ACCOUNT_TYPE_LOCAL;
import static androidx.activity.result.ActivityResultCallerKt.registerForActivityResult;

public class CalendarExport {

    public static void exportAppointment(Context context, CalendarAppointmentInfo appointment) {
        ContentResolver cr = context.getContentResolver();
        ContentValues values = new ContentValues();

        values.put(CalendarContract.Events.DTSTART, appointment.getStartTime());
        values.put(CalendarContract.Events.DTEND, appointment.getStartTime() + appointment.getDuration());
        values.put(CalendarContract.Events.TITLE, appointment.getTitle());
        values.put(CalendarContract.Events.DESCRIPTION, "Course : " + appointment.getCourse());

        TimeZone timeZone = TimeZone.getDefault();
        values.put(CalendarContract.Events.EVENT_TIMEZONE, timeZone.getID());

        // Default calendar
        values.put(CalendarContract.Events.CALENDAR_ID, 1);

        // Insert event to calendar
        Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);

        long eventID = Long.parseLong(uri.getLastPathSegment());
    }



    public static void sexportAppointment(Context context, CalendarAppointmentInfo appointment) {
        String description = "Course: " + appointment.getCourse();

        Intent intent = new Intent(Intent.ACTION_INSERT)
                .setData(CalendarContract.Events.CONTENT_URI)
                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, appointment.getStartTime())
                .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, appointment.getStartTime()+appointment.getDuration())
                .putExtra(CalendarContract.Events.TITLE, appointment.getTitle())
                .putExtra(CalendarContract.Events.DESCRIPTION, description);
        context.startActivity(intent);
    }
}
