package io.github.polysmee.room;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Date;

import io.github.polysmee.R;
import io.github.polysmee.database.DatabaseAppointment;
import io.github.polysmee.interfaces.Appointment;

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

        this.appointment.getTitleAndThen((title) -> ((TextView) findViewById(R.id.roomInfoTitleTextView)).setText(title));
        this.appointment.getCourseAndThen((course) -> ((TextView) findViewById(R.id.roomInfoCourseTextView)).setText(course));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(Activity.RESULT_OK);
        finish();
    }

    public void editTitle(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit title");
        LayoutInflater inflater = getLayoutInflater();

        View dialogView = inflater.inflate(R.layout.dialog_room_info_edit, null);
        EditText editText = (EditText) dialogView.findViewById(R.id.roomInfoDialogEdit);

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
        builder.setTitle("Edit title");
        LayoutInflater inflater = getLayoutInflater();

        View dialogView = inflater.inflate(R.layout.dialog_room_info_edit, null);
        EditText editText = (EditText) dialogView.findViewById(R.id.roomInfoDialogEdit);

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
