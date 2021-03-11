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
    private EditText editTitle, editCourse;
    private Button btnStartTime, btnEndTime, btnCreate, btnReset;
    private Calendar calendarStartTime, calendarEndTime;
    private TextView txtError, txtStartTime, txtEndTime;

    private User user;

    //A calendar is a wait to get time using year/month... and allows to transform it to epoch time
    private Calendar date;
    //Function which first displays a DatePicker then a TimePicker and stores all the information in Calendar date
    public void showDateTimePicker(TextView textView, boolean isStart) {
        final Calendar currentDate = Calendar.getInstance();
        date = Calendar.getInstance();
        new DatePickerDialog(AppointmentActivity.this, (view, year, monthOfYear, dayOfMonth) -> {
            date.set(year, monthOfYear + 1, dayOfMonth);
            new TimePickerDialog(AppointmentActivity.this, (view1, hourOfDay, minute) -> {
                date.set(Calendar.HOUR_OF_DAY, hourOfDay);
                date.set(Calendar.MINUTE, minute);
                date.set(Calendar.SECOND, 0);
                date.set(Calendar.MILLISECOND, 0);
                updateCalendar(textView, isStart);
            }, currentDate.get(Calendar.HOUR_OF_DAY), currentDate.get(Calendar.MINUTE), true).show();
        }, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DATE)).show();
    }

    private void updateCalendar(TextView textView, boolean isStart) {
        //Update start or end time with user input
        if(isStart) {
            calendarStartTime = (Calendar) date.clone();
        } else {
            calendarEndTime = (Calendar) date.clone();
        }
        //If there was an error setting a new start or end time can fix it so remove the error message as the user knows they need to fix it
        txtError.setVisibility(View.INVISIBLE);
        //Display the time on screen so that the user can know their input has been taken
        String strTime = date.get(Calendar.DAY_OF_MONTH) + "/" + String.format("%02d", date.get(Calendar.MONTH)) + "/" + date.get(Calendar.YEAR) + "   -   " + String.format("%02d", date.get(Calendar.HOUR_OF_DAY)) + ":" + String.format("%02d", date.get(Calendar.MINUTE));
        textView.setText(strTime);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment);

        //store all objects on the activity (buttons, textViews...) in variables
        attributeSetters();

        txtError.setVisibility(View.INVISIBLE);

        //We need to know who is trying to create an appointment as they are the owner, see BasicAppointment implementation
        user = (User) getIntent().getSerializableExtra(EXTRA_USER);

        btnStartTime.setOnClickListener(v -> {
            showDateTimePicker(txtStartTime, true);
        });

        btnEndTime.setOnClickListener(v -> {
            showDateTimePicker(txtEndTime, false);
        });

        btnCreate.setOnClickListener(createClickListener);

        btnReset.setOnClickListener(resetClickListener);
    }

    View.OnClickListener createClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //if startTime is bigger than endTime we have a negative duration which doesn't work
            //It isn't possible to create an appointment scheduled before the current time
            if (calendarStartTime.getTimeInMillis() >= calendarEndTime.getTimeInMillis() || calendarStartTime.getTimeInMillis() <= Calendar.getInstance().getTimeInMillis()){
                txtError.setVisibility(View.VISIBLE);
                txtError.setText(ERROR_TXT);
            } else {
                //create Appointment according to user input and return to the activity which called this one
                String title = editTitle.getText().toString();
                String course = editCourse.getText().toString();
                BasicAppointment appointment = new BasicAppointment(calendarStartTime.getTimeInMillis(), calendarEndTime.getTimeInMillis() - calendarStartTime.getTimeInMillis(), course, title, user);
                Intent returnIntent = new Intent();
                returnIntent.putExtra(EXTRA_APPOINTMENT, appointment);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        }
    };

    View.OnClickListener resetClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //reset every input text field and both start and end times
            editTitle.setText("Appointment Title");
            editCourse.setText("Appointment Course");
            txtError.setText("");
            calendarEndTime = Calendar.getInstance();
            txtEndTime.setText("End Time");
            calendarStartTime = Calendar.getInstance();
            txtStartTime.setText("Start Time");
        }
    };

    private void attributeSetters() {
        editTitle = findViewById(R.id.appointmentCreationEditTxtAppointmentTitleSet);
        editCourse = findViewById(R.id.appointmentCreationEditTxtAppointmentCourseSet);
        btnStartTime = findViewById(R.id.appointmentCreationBtnStartTime);
        btnEndTime = findViewById(R.id.appointmentCreationBtnEndTime);
        btnCreate = findViewById(R.id.appointmentCreationbtnCreateAppointment);
        btnReset = findViewById(R.id.appointementCreationBtnReset);
        txtError = findViewById(R.id.appointmentCreationtxtError);
        txtStartTime = findViewById(R.id.appointmentCreationTxtStartTime);
        txtEndTime = findViewById(R.id.appointmentCreationTxtEndTime);
    }
}