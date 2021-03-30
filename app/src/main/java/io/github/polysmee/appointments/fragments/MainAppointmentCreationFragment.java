package io.github.polysmee.appointments.fragments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Calendar;

import io.github.polysmee.R;
import io.github.polysmee.appointments.AppointmentActivity;

public class MainAppointmentCreationFragment extends Fragment {

    public static final String EXTRA_APPOINTMENT = "appointment";
    public static final String ERROR_TXT = "Error : Start and end time must result in a correct time slot";
    private static final int SETTINGS_ACTIVITY_CODE = 1;
    private EditText editTitle, editCourse;
    private Button btnStartTime, btnEndTime, btnDone, btnReset;
    private Calendar calendarStartTime, calendarEndTime;
    private TextView txtError, txtStartTime, txtEndTime;

    DataPasser dataPasser;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        dataPasser = (DataPasser) context;
    }

    //A calendar is a wait to get time using year/month... and allows to transform it to epoch time
    private Calendar date;
    //Function which first displays a DatePicker then a TimePicker and stores all the information in Calendar date
    private void showDateTimePicker(TextView textView, boolean isStart) {
        final Calendar currentDate = Calendar.getInstance();
        date = Calendar.getInstance();
        new DatePickerDialog(getActivity(), (view, year, monthOfYear, dayOfMonth) -> {
            date.set(year, monthOfYear, dayOfMonth);
            new TimePickerDialog(getActivity(), (view1, hourOfDay, minute) -> {
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_main_appointment_creation, container, false);

        //store all objects on the activity (buttons, textViews...) in variables
        attributeSetters(rootView);

        txtError.setVisibility(View.INVISIBLE);

        btnStartTime.setOnClickListener(v -> {
            showDateTimePicker(txtStartTime, true);
        });

        btnEndTime.setOnClickListener(v -> {
            showDateTimePicker(txtEndTime, false);
        });

        btnDone.setOnClickListener(doneClickListener);

        btnReset.setOnClickListener(resetClickListener);

        return rootView;
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
                dataPasser.dataPass(editTitle.getText().toString(), AppointmentActivity.TITLE);
                dataPasser.dataPass(editCourse.getText().toString(), AppointmentActivity.COURSE);
                dataPasser.dataPass(calendarStartTime, AppointmentActivity.CALENDAR_START);
                dataPasser.dataPass(calendarEndTime, AppointmentActivity.CALENDAR_END);
                dataPasser.dataPass(true, AppointmentActivity.DONE);
                getActivity().finish();
            }
        }
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

    private void attributeSetters(View rootView) {
        editTitle = rootView.findViewById(R.id.appointmentCreationEditTxtAppointmentTitleSet);
        editCourse = rootView.findViewById(R.id.appointmentCreationEditTxtAppointmentCourseSet);
        btnStartTime = rootView.findViewById(R.id.appointmentCreationBtnStartTime);
        btnEndTime = rootView.findViewById(R.id.appointmentCreationBtnEndTime);
        btnDone = rootView.findViewById(R.id.appointmentCreationbtnDone);
        btnReset = rootView.findViewById(R.id.appointementCreationBtnReset);
        txtError = rootView.findViewById(R.id.appointmentCreationtxtError);
        txtStartTime = rootView.findViewById(R.id.appointmentCreationTxtStartTime);
        txtEndTime = rootView.findViewById(R.id.appointmentCreationTxtEndTime);
        calendarStartTime = Calendar.getInstance();
        calendarEndTime = Calendar.getInstance();
    }
}