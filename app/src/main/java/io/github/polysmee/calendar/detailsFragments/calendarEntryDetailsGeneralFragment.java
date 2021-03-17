package io.github.polysmee.calendar.detailsFragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

import io.github.polysmee.R;
import io.github.polysmee.calendar.CalendarActivity;
import io.github.polysmee.calendar.CalendarEntryDetailsActivity;
import io.github.polysmee.database.DatabaseAppointment;
import io.github.polysmee.database.decoys.FakeDatabaseAppointment;


public class calendarEntryDetailsGeneralFragment extends Fragment {

    private ViewGroup rootView;
    private final String appointmentId;
    private DatabaseAppointment appointment;
    public calendarEntryDetailsGeneralFragment(String id_appointment){
        this.appointmentId = id_appointment;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.rootView = (ViewGroup)inflater.inflate(R.layout.activity_calendar_entry_detail_general_fragment, container, false);
        appointment = new DatabaseAppointment(appointmentId);
        appointment.getStartTimeAndThen((start)->{
            TextView textView = rootView.findViewById(R.id.calendarEntryDetailActivityStart);
            Date startTime = new Date(start * 1000);
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm");
            String startText = "Start time: " + formatter.format(startTime);
            textView.setTextSize(20);
            textView.setText(startText);
        });
        appointment.getTitleAndThen((title)->{
            TextView textView = rootView.findViewById(R.id.calendarEntryDetailActivityTitle);
            String titleText = "Title: ";
            EditText editText = rootView.findViewById(R.id.calendarEntryDetailActivityTitleSet);
            editText.setText(title);
            textView.setTextSize(20);
            textView.setText(titleText);
        });
        appointment.getCourseAndThen((course)->{
            TextView textView = rootView.findViewById(R.id.calendarEntryDetailActivityCourse);
            String courseText = "Course:";
            EditText editText = rootView.findViewById(R.id.calendarEntryDetailActivityCourseSet);
            editText.setText(course);
            textView.setTextSize(20);
            textView.setText(courseText);
        });
        appointment.getDurationAndThen((duration) ->{
            long number_of_hours = duration / 3600;
            long number_of_minutes = (duration - number_of_hours*3600)/60;
            String duration_text = "" + number_of_hours + ":";
            if(number_of_minutes > 10)
                duration_text = duration_text + number_of_minutes;
            else
                duration_text = duration_text + "0" + number_of_minutes;
            TextView textView = rootView.findViewById(R.id.calendarEntryDetailActivityDuration);
            duration_text = "Duration: " + duration_text;
            textView.setTextSize(20);
            textView.setText(duration_text);
        });

        return rootView;
    }

    public void doneModifying(){
        appointment.setCourse(((EditText)rootView.findViewById(R.id.calendarEntryDetailActivityCourseSet)).getText().toString());
        appointment.setTitle(((EditText)rootView.findViewById(R.id.calendarEntryDetailActivityTitleSet)).getText().toString());
    }
}