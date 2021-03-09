package io.github.polysmee.appointments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

import io.github.polysmee.R;
import io.github.polysmee.interfaces.User;

public class AppointmentActivity extends AppCompatActivity {
    public static final String EXTRA_USER = "user";
    public static final String EXTRA_APPOINTMENT = "appointment";
    public static final String ERROR_TXT = "Error : Start and end time must result in a correct time slot";
    EditText editTitle, editCourse;
    Button btnStartTime, btnEndTime, btnCreate, btnReset;
    Calendar clndrStartTime, clndrEndTime;
    TextView txtError, txtStartTime, txtEndTime;

    User user;

    Calendar date;
    public void showDateTimePicker(boolean isStart) {
        final Calendar currentDate = Calendar.getInstance();
        date = Calendar.getInstance();
        new DatePickerDialog(AppointmentActivity.this, (view, year, monthOfYear, dayOfMonth) -> {
            date.set(year, monthOfYear + 1, dayOfMonth);
            new TimePickerDialog(AppointmentActivity.this, (view1, hourOfDay, minute) -> {
                date.set(Calendar.HOUR_OF_DAY, hourOfDay);
                date.set(Calendar.MINUTE, minute);
                date.set(Calendar.SECOND, 0);
                date.set(Calendar.MILLISECOND, 0);
                updateCalendar(isStart);
            }, currentDate.get(Calendar.HOUR_OF_DAY), currentDate.get(Calendar.MINUTE), true).show();
        }, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DATE)).show();
    }

    private void updateCalendar(boolean isStart) {
        txtError.setVisibility(View.INVISIBLE);
        if(isStart) {
            clndrStartTime = (Calendar) date.clone();
            String strStartTime = clndrStartTime.get(Calendar.DAY_OF_MONTH) + "/" + String.format("%02d", clndrStartTime.get(Calendar.MONTH)) + "/" + clndrStartTime.get(Calendar.YEAR) + "   -   " + String.format("%02d", clndrStartTime.get(Calendar.HOUR_OF_DAY)) + ":" + String.format("%02d", clndrStartTime.get(Calendar.MINUTE));
            txtStartTime.setText(strStartTime);
        } else {
            clndrEndTime = (Calendar) date.clone();
            String strEndTime = clndrEndTime.get(Calendar.DAY_OF_MONTH) + "/" + String.format("%02d", clndrEndTime.get(Calendar.MONTH)) + "/" + clndrEndTime.get(Calendar.YEAR) + "   -   " + String.format("%02d", clndrEndTime.get(Calendar.HOUR_OF_DAY)) + ":" + String.format("%02d", clndrEndTime.get(Calendar.MINUTE));
            txtEndTime.setText(strEndTime);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment);

        editTitle = findViewById(R.id.editTxtAppointmentTitleSet);
        editCourse = findViewById(R.id.editTxtAppointmentCourseSet);
        btnStartTime = findViewById(R.id.btnStartTime);
        btnEndTime = findViewById(R.id.btnEndTime);
        btnCreate = findViewById(R.id.btnCreateAppointment);
        btnReset = findViewById(R.id.btnReset);
        txtError = findViewById(R.id.txtError);
        txtStartTime = findViewById(R.id.txtStartTime);
        txtEndTime = findViewById(R.id.txtEndTime);

        txtError.setVisibility(View.INVISIBLE);

        //We need to know who is trying to create an appointment as they are the owner, see BasicAppointment implementation
        user = (User) getIntent().getSerializableExtra(EXTRA_USER);

        btnStartTime.setOnClickListener(v -> {
            showDateTimePicker(true);
        });

        btnEndTime.setOnClickListener(v -> {
            showDateTimePicker(false);
        });

        btnCreate.setOnClickListener(v -> {
            if(clndrStartTime.getTimeInMillis() >= clndrEndTime.getTimeInMillis() || clndrStartTime.getTimeInMillis() <= Calendar.getInstance().getTimeInMillis()) {
                txtError.setVisibility(View.VISIBLE);
                txtError.setText(ERROR_TXT);
            } else {
                String title = editTitle.getText().toString();
                String course = editCourse.getText().toString();
                BasicAppointment appointment = new BasicAppointment(clndrStartTime.getTimeInMillis(), clndrEndTime.getTimeInMillis() - clndrStartTime.getTimeInMillis(), course, title, user);
                Intent returnIntent = new Intent();
                returnIntent.putExtra(EXTRA_APPOINTMENT, appointment);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });

        btnReset.setOnClickListener(v -> {
            editTitle.setText("Appointment Title");
            editCourse.setText("Appointment Course");
            txtError.setText("");
            clndrEndTime = Calendar.getInstance();
            txtEndTime.setText("End Time");
            clndrStartTime = Calendar.getInstance();
            txtStartTime.setText("Start Time");
        });
    }
}