package io.github.polysmee.appointments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.function.LongFunction;

import io.github.polysmee.R;
import io.github.polysmee.database.DatabaseAppointment;
import io.github.polysmee.interfaces.Appointment;
import io.github.polysmee.interfaces.User;
import io.github.polysmee.database.DatabaseUser;
import io.github.polysmee.login.MainUserSingleton;

public class AppointmentActivity extends AppCompatActivity {
    public static final String EXTRA_APPOINTMENT = "appointment";
    public static final String ERROR_TXT = "Error : Start and end time must result in a correct time slot";
    private static final int SETTINGS_ACTIVITY_CODE = 1;
    private EditText editTitle, editCourse;
    private Button btnStartTime, btnEndTime, btnDone, btnReset, btnSettings;
    private Calendar calendarStartTime, calendarEndTime;
    private TextView txtError, txtStartTime, txtEndTime;
    private boolean isPrivate;
    private User user;
    private ArrayList<String> invites;
    private ArrayList<String> bans;

    //A calendar is a wait to get time using year/month... and allows to transform it to epoch time
    private Calendar date;
    //Function which first displays a DatePicker then a TimePicker and stores all the information in Calendar date
    private void showDateTimePicker(TextView textView, boolean isStart) {
        final Calendar currentDate = Calendar.getInstance();
        date = Calendar.getInstance();
        new DatePickerDialog(AppointmentActivity.this, (view, year, monthOfYear, dayOfMonth) -> {
            date.set(year, monthOfYear, dayOfMonth);
            new TimePickerDialog(AppointmentActivity.this, (view1, hourOfDay, minute) -> {
                date.set(Calendar.HOUR_OF_DAY, hourOfDay);
                date.set(Calendar.MINUTE, minute);
                date.set(Calendar.SECOND, 0);
                date.set(Calendar.MILLISECOND, 0);
                updateCalendar(textView, isStart);
            }, currentDate.get(Calendar.HOUR_OF_DAY), currentDate.get(Calendar.MINUTE), true).show();
        }, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DATE)).show();
    }

    //only to be used by function showDateTimePicker
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
        String strTime = date.get(Calendar.DAY_OF_MONTH) + "/" + String.format("%02d", date.get(Calendar.MONTH) + 1) + "/" + date.get(Calendar.YEAR) + "   -   " + String.format("%02d", date.get(Calendar.HOUR_OF_DAY)) + ":" + String.format("%02d", date.get(Calendar.MINUTE));
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
        user = MainUserSingleton.getInstance();

        btnStartTime.setOnClickListener(v -> {
            showDateTimePicker(txtStartTime, true);
        });

        btnEndTime.setOnClickListener(v -> {
            showDateTimePicker(txtEndTime, false);
        });

        btnDone.setOnClickListener(doneClickListener);

        btnReset.setOnClickListener(resetClickListener);

        btnSettings.setOnClickListener(settingsClickListener);
    }

    View.OnClickListener doneClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //if startTime is bigger than endTime we have a negative duration which doesn't work
            //It isn't possible to create an appointment scheduled before the current time
            Calendar c = Calendar.getInstance();
            c.set(Calendar.MONTH, c.get(Calendar.MONTH) - 1);
            if (calendarStartTime.getTimeInMillis() >= calendarEndTime.getTimeInMillis() || calendarStartTime.getTimeInMillis() <= c.getTimeInMillis()){
                txtError.setVisibility(View.VISIBLE);
                txtError.setText(ERROR_TXT);
            } else {
                //create Appointment according to user input and return to the activity which called this one
                String title = editTitle.getText().toString();
                String course = editCourse.getText().toString();
                //bans and isPrivate not supported yet, will add them to the appointment when they are
                String aptID = user.createNewUserAppointment(calendarStartTime.getTimeInMillis()/1000, calendarEndTime.getTimeInMillis()/1000 - calendarStartTime.getTimeInMillis()/1000, course, title);
                Appointment appointment = new DatabaseAppointment(aptID);
                appointment.addParticipant(user);
                User.getAllUsersIdsAndThenOnce((setOfUserIds) -> {
                    for(String userId : setOfUserIds){
                        User user = new DatabaseUser(userId);
                        for(String inviteName : invites) {
                            user.getNameAndThen((name) -> {
                                if (name.equals(inviteName)) {
                                    user.addAppointment(appointment);
                                    appointment.addParticipant(user);
                                }
                            });
                        }
                    }
                });

                Intent returnIntent = new Intent();
                returnIntent.putExtra(EXTRA_APPOINTMENT, aptID);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        }
    };

    View.OnClickListener settingsClickListener = v -> {
        //go to the settings activity
        Intent settingsIntent = new Intent(AppointmentActivity.this, AppointmentSettingsActivity.class);
        startActivityForResult(settingsIntent, SETTINGS_ACTIVITY_CODE);
    };

    View.OnClickListener resetClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //reset every input text field and both start and end times
            editTitle.setText("");
            editCourse.setText("");
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
        btnDone = findViewById(R.id.appointmentCreationbtnDone);
        btnReset = findViewById(R.id.appointementCreationBtnReset);
        btnSettings = findViewById(R.id.appointmentCreationBtnSettings);
        txtError = findViewById(R.id.appointmentCreationtxtError);
        txtStartTime = findViewById(R.id.appointmentCreationTxtStartTime);
        txtEndTime = findViewById(R.id.appointmentCreationTxtEndTime);
        isPrivate = false;
        invites = new ArrayList<>();
        bans = new ArrayList<>();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //this method is called when the settings activity finishes with a result, so we get this data and store it to later create the appointment
        if (requestCode == SETTINGS_ACTIVITY_CODE) {
            if(resultCode == Activity.RESULT_OK) {
                if(data != null) {
                    isPrivate = data.getBooleanExtra("private", false);
                    //it is possible to put an arrayList of strings but not a set in an intent, so we need to create these sets now
                    invites = data.getStringArrayListExtra("invites");
                    bans = data.getStringArrayListExtra("bans");
                }
            }
        }
    }
}