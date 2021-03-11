package io.github.polysmee.calendar;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import io.github.polysmee.R;
import io.github.polysmee.interfaces.Appointment;
import io.github.polysmee.interfaces.User;

public class CalendarActivity extends AppCompatActivity{

    private LinearLayout scrollLayout ;
    private LayoutInflater inflater ;
    private static final int constraintLayoutId = 284546;
    private User user = PseudoLoggedUser.getSingletonPseudoUser("idMagique"); //replace Mathis's singleton until I get access to it

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar2);
        scrollLayout = (LinearLayout)findViewById(R.id.calendarActivityScrollLayout);
        inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        setTodayDateText();
        changeCurrentCalendarLayout();

        Button refreshButton = (Button) findViewById(R.id.calendarActivityRefreshButton);
        refreshButton.setOnClickListener((v) -> {refresh();});


    }
    /**
     * Changes the calendar's layout to show the user's daily appointments at the time
     * this method is called.
     */
    protected void changeCurrentCalendarLayout(){
        List<Appointment> todayAppointments = DailyCalendar.getAppointmentsForTheDay(user.getAppointments());
        int i = 0;
        for(Appointment appointment : todayAppointments){
            addAppointmentToCalendarLayout(appointment,i);
            i+=3;
        }
    }

    /**
     * Creates an appointment's textual description following a certain format
     * to show in the calendar
     * @param appointment the appointment's whose description is created
     * @return the textual representation of the appointment in the calendar
     */
    protected String createAppointmentDescription(Appointment appointment){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Reunion name : ").append(appointment.getTitle());
        stringBuilder.append("\n");
        stringBuilder.append("Course name  : ").append(appointment.getCourse());
        stringBuilder.append("\n");
        Date date = new Date(appointment.getStartTime() * 1000);
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        stringBuilder.append("Start time : ").append(formatter.format(date));
        return stringBuilder.toString();
    }

    /**
     * Adds an appointment to the calendar layout, as a calendar entry
     * @param appointment the appointment to add
     * @param i integer parameter used to create unique ids (at least in the calendar's current layout) for the calendar entry
     */
    protected void addAppointmentToCalendarLayout(Appointment appointment, int i){
        //layout: on one part add description as text, on another button "details" to be able to join
        ConstraintLayout appointmentLayout = (ConstraintLayout) inflater.inflate(R.layout.calendar_entry,null);
        TextView appointmentDescription = (TextView) appointmentLayout.findViewById(R.id.descriptionOfAppointmentCalendarEntry);
        Button detailsButton = (Button)appointmentLayout.findViewById(R.id.detailsButtonCalendarEntry);
        appointmentDescription.setText(createAppointmentDescription(appointment));
        appointmentLayout.setId(constraintLayoutId + i);
        appointmentDescription.setId(constraintLayoutId + i + 1);
        detailsButton.setId(constraintLayoutId + i + 2);
        this.scrollLayout.addView(appointmentLayout);
    }

    /**
     * Sets the text view on top of the calendar to the current day's date
     */
    protected void setTodayDateText(){
        TextView dateText = (TextView)findViewById(R.id.todayDateCalendarActivity);
        long epochTimeToday = DailyCalendar.todayEpochTimeAtMidnight() * 1000;
        Date today = new Date(epochTimeToday);
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        dateText.setText(String.format("Appointments on the %s : ", formatter.format(today)));
    }

    /**
     * Refreshes the calendar's layout and the date on top if the day changes
     */
    protected void refresh(){
        scrollLayout.removeAllViewsInLayout();
        changeCurrentCalendarLayout();
        setTodayDateText();
    }
}