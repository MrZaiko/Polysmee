package io.github.polysmee.room;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import io.github.polysmee.R;
import io.github.polysmee.database.DatabaseAppointment;
import io.github.polysmee.interfaces.Appointment;
import io.github.polysmee.login.MainUserSingleton;

/**
 * Activity showing all information about an appointment given in argument
 */
public class RoomActivityInfo extends AppCompatActivity {
    public static String APPOINTMENT_KEY = "io.github.polysmee.room.RoomInfoActivity.APPOINTMENT_KEY";
    private Appointment appointment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Room info");
        setContentView(R.layout.activity_room_info);
        String appointmentKey = getIntent().getStringExtra(APPOINTMENT_KEY);
        this.appointment = new DatabaseAppointment(appointmentKey);

        setupTitle();
        setupCourse();
        setupDate();

        appointment.getOwnerIdAndThen(id -> {
            if (id.equals(MainUserSingleton.getInstance().getId())) {
                findViewById(R.id.roomInfoCourseLayout).setOnClickListener(this::editCourse);
                findViewById(R.id.roomInfoCourseEditButton).setVisibility(View.VISIBLE);
                findViewById(R.id.roomInfoTitleLayout).setOnClickListener(this::editTitle);
                findViewById(R.id.roomInfoTitleEditButton).setVisibility(View.VISIBLE);
            }
        });
    }

    private void setupCourse() {
        ConstraintLayout courseLayout = findViewById(R.id.roomInfoCourseLayout);
        TextView courseView = findViewById(R.id.roomInfoCourseTextView);
        courseLayout.setBackgroundResource(R.drawable.room_info_element_background);
        this.appointment.getCourseAndThen(courseView::setText);
        courseLayout.setClickable(true);
        courseLayout.setOnClickListener(this::onlyOwnerToast);
    }

    private void setupDate() {
        TextView durationView = findViewById(R.id.roomInfoTimeRemaining);
        durationView.setBackgroundResource(R.drawable.room_info_element_background);
        this.appointment.getDurationAndThen(d -> {
            this.appointment.getStartTimeAndThen( st -> {
                String TIMESTAMP_PATTERN = "HH:mm";
                SimpleDateFormat formatter = new SimpleDateFormat(TIMESTAMP_PATTERN, Locale.ENGLISH);

                Date current = new Date(System.currentTimeMillis());
                Date start = new Date(st);
                Date end = new Date(st+d);

                String durationText;

                if (current.before(start))
                    durationText = "The appointment start at " + formatter.format(start);
                else if (current.after(end))
                    durationText = "The appointment has ended";
                else
                    durationText = "The appointment end at " + formatter.format(end);


                durationView.setText(durationText);
            });
        });
    }

    private void setupTitle() {
        ConstraintLayout titleLayout = findViewById(R.id.roomInfoTitleLayout);
        TextView titleView = findViewById(R.id.roomInfoTitleTextView);
        titleLayout.setBackgroundResource(R.drawable.room_info_element_background);
        this.appointment.getTitleAndThen(titleView::setText);
        titleLayout.setClickable(true);
        titleLayout.setOnClickListener(this::onlyOwnerToast);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public void onlyOwnerToast(View view) {
        Context context = getApplicationContext();
        CharSequence text = getString(R.string.roomInfoNotOwnerToastMessage);
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    public void editTitle(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit title");
        LayoutInflater inflater = getLayoutInflater();

        View dialogView = inflater.inflate(R.layout.dialog_room_info_edit, null);
        EditText editText = dialogView.findViewById(R.id.roomInfoDialogEdit);
        appointment.getTitleAndThen(editText::setHint);
        builder.setView(dialogView);


        builder.setPositiveButton("OK", (dialog, id) -> {
            appointment.setTitle(editText.getText().toString());
        });


        builder.setNegativeButton("Cancel", (dialog, id) -> {
            //Nothing to do
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void editCourse(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Course");
        LayoutInflater inflater = getLayoutInflater();

        View dialogView = inflater.inflate(R.layout.dialog_room_info_edit, null);
        EditText editText = dialogView.findViewById(R.id.roomInfoDialogEdit);
        appointment.getCourseAndThen(editText::setHint);

        builder.setView(dialogView);


        builder.setPositiveButton("OK", (dialog, id) -> {
            appointment.setCourse(editText.getText().toString());
        });


        builder.setNegativeButton("Cancel", (dialog, id) -> {
            //Nothing to do
        });

        AlertDialog dialog = builder.create();

        dialog.show();
    }

}
