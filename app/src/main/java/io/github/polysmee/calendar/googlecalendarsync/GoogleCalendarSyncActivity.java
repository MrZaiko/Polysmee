package io.github.polysmee.calendar.googlecalendarsync;


import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

import io.github.polysmee.R;
import io.github.polysmee.login.MainUser;

public class GoogleCalendarSyncActivity extends AppCompatActivity {

    private String calendarId;
    private TextView calendarIdText;
    private Button exportToCalendar, copyToClipboard, deleteCalendar;

    private ClipboardManager clipboard;

    @RequiresApi(api = Build.VERSION_CODES.O)
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
        new Thread(() -> {
            view.setClickable(false);

            try {
                calendarId = GoogleCalendarUtilities.createCalendar(this, MainUser.getCurrentUserEmail());
                GoogleCalendarUtilities.addUserToCalendar(this, calendarId, MainUser.getCurrentUserEmail());
            } catch (IOException e) {
                runOnUiThread(() -> {
                    showToast(getString(R.string.genericErrorText));
                    view.setClickable(true);
                });
                return;
            }

            MainUser.getMainUser().setCalendarId(calendarId);
            runOnUiThread(this::showCalendarId);
        }).start();
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
        new Thread(() -> {
            try {
                GoogleCalendarUtilities.deleteCalendar(this, calendarId);
            } catch (IOException e) {
                runOnUiThread(() -> {
                    showToast(getString(R.string.genericErrorText));
                    view.setClickable(true);
                });
                return;
            }

            calendarId = "";
            MainUser.getMainUser().setCalendarId(calendarId);
            runOnUiThread(this::showCreateCalendar);
        }).start();
    }

    private void showToast(String message) {
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(this, message, duration);
        toast.show();
    }

}
