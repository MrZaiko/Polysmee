package io.github.polysmee.calendar.googlecalendarsync;


import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.function.Consumer;

import io.github.polysmee.R;
import io.github.polysmee.login.MainUser;

public class GoogleCalendarSyncActivity extends AppCompatActivity {

    private String calendarId;
    private TextView calendarIdText;
    private Button exportToCalendar, copyToClipboard, deleteCalendar;

    private ClipboardManager clipboard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_export);

        calendarIdText = findViewById(R.id.calendarSyncActivityCalendarIdText);

        copyToClipboard = findViewById(R.id.calendarSyncActivityCopyButton);
        copyToClipboard.setOnClickListener(this::copyCalendarIdToClipboard);
        copyToClipboard.setClickable(false);

        deleteCalendar = findViewById(R.id.calendarSyncActivityDeleteButton);
        deleteCalendar.setOnClickListener(this::deleteCalendarBehavior);
        deleteCalendar.setClickable(false);

        exportToCalendar = findViewById(R.id.calendarSyncActivitySyncButton);
        exportToCalendar.setOnClickListener(this::createCalendar);
        exportToCalendar.setClickable(false);

        MainUser.getMainUser().getCalendarId_Once_AndThen(id -> {
            if (id != null && !id.equals("")) {
               calendarId = id;
               showCalendarId();
            } else {
               showCreateCalendar();
            }
        });

        clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
    }

    private void createCalendar(View view) {
        view.setClickable(false);
        Runnable onError = () -> runOnUiThread(() -> {
            showToast(getString(R.string.genericErrorText));
            view.setClickable(true);
        });
        Consumer<String> onCalendarIdCreated = newCalendarId -> CalendarUtilities.addUserToCalendar(this, newCalendarId,
                MainUser.getCurrentUserEmail(), () -> {
                    calendarId = newCalendarId;
                    MainUser.getMainUser().setCalendarId(calendarId);
                    runOnUiThread(this::showCalendarId);
                }, onError);

        CalendarUtilities.createCalendar(this, MainUser.getCurrentUserEmail(), onCalendarIdCreated, onError);
    }

    private void copyCalendarIdToClipboard(View view) {
        ClipData clip = ClipData.newPlainText("calendar id", calendarId);
        clipboard.setPrimaryClip(clip);
    }

    private void showCalendarId() {
        exportToCalendar.setVisibility(View.GONE);
        exportToCalendar.setClickable(false);

        calendarIdText.setText(calendarId);
        calendarIdText.setVisibility(View.VISIBLE);

        copyToClipboard.setVisibility(View.VISIBLE);
        copyToClipboard.setClickable(true);

        deleteCalendar.setVisibility(View.VISIBLE);
        deleteCalendar.setClickable(true);
    }

    private void showCreateCalendar() {
        exportToCalendar.setVisibility(View.VISIBLE);
        exportToCalendar.setClickable(true);

        calendarIdText.setVisibility(View.GONE);

        copyToClipboard.setVisibility(View.GONE);
        copyToClipboard.setClickable(false);

        deleteCalendar.setVisibility(View.GONE);
        deleteCalendar.setClickable(false);
    }

    private void deleteCalendarBehavior(View view) {
        view.setClickable(false);

        CalendarUtilities.deleteCalendar(this, calendarId, () -> {
            calendarId = "";
            MainUser.getMainUser().setCalendarId(calendarId);
            runOnUiThread(this::showCreateCalendar);
        }, () -> runOnUiThread(() -> {
            showToast(getString(R.string.genericErrorText));
            view.setClickable(true);
        }));
    }

    private void showToast(String message) {
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(this, message, duration);
        toast.show();
    }

}
