package io.github.polysmee.appointments;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

import io.github.polysmee.R;
import io.github.polysmee.interfaces.Appointment;
import io.github.polysmee.interfaces.User;

public class AppointmentActivity extends AppCompatActivity {
    private static final String TAG = "AppointmentActivity";
    EditText editTitle, editCourse;
    Button btnStartTime, btnEndTime, btnCreate, btnReset;
    Calendar clndrStartTime, clndrEndTime;
    TextView txtError;
    User user;

    Calendar date;
    public void showDateTimePicker() {
        final Calendar currentDate = Calendar.getInstance();
        date = Calendar.getInstance();
        new DatePickerDialog(AppointmentActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                date.set(year, monthOfYear, dayOfMonth);
                new TimePickerDialog(AppointmentActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        date.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        date.set(Calendar.MINUTE, minute);
                        Log.v(TAG, "The chosen one " + date.getTime());
                    }
                }, currentDate.get(Calendar.HOUR_OF_DAY), currentDate.get(Calendar.MINUTE), true).show();
            }
        }, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DATE)).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment);

        editTitle = (EditText) findViewById(R.id.editTxtAppointmentTitleSet);
        editCourse = (EditText) findViewById(R.id.editTxtAppointmentCourseSet);
        btnStartTime = (Button) findViewById(R.id.btnStartTime);
        btnEndTime = (Button) findViewById(R.id.btnEndTime);
        btnCreate = (Button) findViewById(R.id.btnCreateAppointment);
        btnReset = (Button) findViewById(R.id.btnReset);
        txtError = (TextView) findViewById(R.id.txtError);

        //We need to know who is trying to create an appointment as they are the owner, see BasicAppointment implementation
        user = (User) getIntent().getSerializableExtra("user");

        btnStartTime.setOnClickListener(v -> {
            showDateTimePicker();
            clndrStartTime = (Calendar) date.clone();
        });

        btnEndTime.setOnClickListener(v -> {
            showDateTimePicker();
            clndrEndTime = (Calendar) date.clone();
        });

        btnCreate.setOnClickListener(v -> {
            if(clndrStartTime.getTimeInMillis() >= clndrEndTime.getTimeInMillis() || clndrStartTime.getTimeInMillis() <= Calendar.getInstance().getTimeInMillis()) {
                txtError.setText("Error : Start and end time must result in a correct time slot");
            } else {
                String title = editTitle.getText().toString();
                String course = editCourse.getText().toString();
                BasicAppointment appointment = new BasicAppointment(clndrStartTime.getTimeInMillis(), clndrEndTime.getTimeInMillis(), course, title, user);
                Intent returnIntent = new Intent();
                returnIntent.putExtra("appointment", appointment);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });

        btnReset.setOnClickListener(v -> {
            editTitle.setText("");
            editCourse.setText("");
            clndrEndTime = Calendar.getInstance();
            clndrStartTime = Calendar.getInstance();
        });
    }
}