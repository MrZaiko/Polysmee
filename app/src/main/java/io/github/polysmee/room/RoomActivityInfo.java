package io.github.polysmee.room;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Date;

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

        TextView titleView = findViewById(R.id.roomInfoTitleTextView);
        this.appointment.getTitleAndThen(titleView::setText);
        titleView.setClickable(true);
        titleView.setOnClickListener(this::onlyOwnerToast);

        TextView courseView = findViewById(R.id.roomInfoCourseTextView);
        this.appointment.getCourseAndThen(courseView::setText);
        courseView.setClickable(true);
        courseView.setOnClickListener(this::onlyOwnerToast);

        appointment.getOwnerIdAndThen(id -> {
            if (id.equals(MainUserSingleton.getInstance().getId())) {
                courseView.setOnClickListener(this::editCourse);
                titleView.setOnClickListener(this::editTitle);
            }
        });
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
