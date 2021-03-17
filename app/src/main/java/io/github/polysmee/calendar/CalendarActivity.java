package io.github.polysmee.calendar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import io.github.polysmee.R;
import io.github.polysmee.database.DatabaseAppointment;
import io.github.polysmee.interfaces.User;
import io.github.polysmee.login.MainUserSingleton;

public class CalendarActivity extends AppCompatActivity{

    private LinearLayout scrollLayout ;
    private LayoutInflater inflater ;
    private static final int constraintLayoutId = 284546;
    private User user = MainUserSingleton.getInstance();
    private int demo_indexer = 0;
    public static final String APPOINTMENT_DETAIL_CALENDAR = "APPOINTMENT_DETAIL_CALENDAR";
    private final List<CalendarAppointmentInfo> appointmentInfosToday = new ArrayList<>();
    private final AtomicInteger childrenCounters = new AtomicInteger(0);
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar2);
        addListenerToUserAppointments();
        scrollLayout = (LinearLayout)findViewById(R.id.calendarActivityScrollLayout);
        inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        setTodayDateText();


        /*Button refreshButton = (Button) findViewById(R.id.calendarActivityRefreshButton);
        refreshButton.setOnClickListener((v) -> {refresh();});*/

       /* Button demoButton    = (Button) findViewById(R.id.calendarActivityDemoButton);
        demoButton.setOnClickListener((v)->{demoAddAppointment();});*/
    }


/*    private void demoAddAppointment(){
        user.createNewUserAppointment(DailyCalendar.todayEpochTimeAtMidnight() + demo_indexer *60,50,"bonjaeo","rwougnwf");
        demo_indexer += 1;
    }*/


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    protected void goToAppointmentDetails(String id){
        Intent intent = new Intent(this,CalendarEntryDetailsActivity.class);
        intent.putExtra(APPOINTMENT_DETAIL_CALENDAR,id);
        startActivityForResult(intent,51);
    }


    /**
     * Changes the calendar's layout to show the user's daily appointments at the time
     * this method is called.
     */


    /**
     * Creates an appointment's textual description following a certain format
     * to show in the calendar
     * @param appointment the appointment's whose description is created
     * @return the textual representation of the appointment in the calendar
     */
    protected String createAppointmentDescription(CalendarAppointmentInfo appointment){
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
     * @param j integer parameter used to create unique ids (at least in the calendar's current layout) for the calendar entry
     */
    protected void addAppointmentToCalendarLayout(CalendarAppointmentInfo appointment, int j){
        //layout: on one part add description as text, on another button "details" to be able to join

        synchronized (scrollLayout){
        int i = appointment.getIndex() + constraintLayoutId;
        ConstraintLayout appointmentLayout = (ConstraintLayout) inflater.inflate(R.layout.activity_calendar_entry,null);
        TextView appointmentDescription = (TextView) appointmentLayout.findViewById(R.id.descriptionOfAppointmentCalendarEntry);
        Button detailsButton = (Button)appointmentLayout.findViewById(R.id.detailsButtonCalendarEntry);
        appointmentDescription.setText(createAppointmentDescription(appointment));
       // appointmentLayout.setId(constraintLayoutId + i);
        //appointmentDescription.setId(constraintLayoutId + i + 1);
        //detailsButton.setId(constraintLayoutId + i + 2);
        detailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToAppointmentDetails(appointment.getId());
            }
        });

        this.scrollLayout.addView(appointmentLayout);
        //childrenCounters.addAndGet(3);
        }

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
    /*protected void refresh(){
        scrollLayout.removeAllViewsInLayout();
        changeCurrentCalendarLayout();
        setTodayDateText();
    }*/

    protected void addListenerToUserAppointments(){
        user.getAppointmentsAndThen((setOfIds)->{
            scrollLayout.removeAllViewsInLayout();
            Set<CalendarAppointmentInfo> appointmentSet = new HashSet<>();
            for(String id : setOfIds){
                DatabaseAppointment appointment = new DatabaseAppointment(id);

                CalendarAppointmentInfo appointmentInfo = new CalendarAppointmentInfo("","",0,0,id,user,childrenCounters.getAndIncrement());

                appointment.getDurationAndThen((duration) ->{
                    appointmentInfo.setDuration(duration);
                    appointment.getStartTimeAndThen((start) ->{
                        appointmentInfo.setStartTime(start);
                        appointment.getTitleAndThen((title) ->{
                            appointmentInfo.setTitle((title));
                            appointment.getCourseAndThen((course) ->{
                                appointmentInfo.setCourse(course);
                                if(checkIfAlreadyInList(id)){
                                    scrollLayout.removeViewAt(appointmentInfo.getIndex());
                                    addAppointmentToCalendarLayout(appointmentInfo,childrenCounters.get());
                                }
                                else{
                                    addAppointmentToCalendarLayout(appointmentInfo,childrenCounters.get());
                                    appointmentInfosToday.add(appointmentInfo);
                                }
                            });
                        });
                    });
                });
            }
        });
    }

    private boolean checkIfAlreadyInList(String id){
        for(CalendarAppointmentInfo infos: appointmentInfosToday){
            if(infos.getId().equals(id)){
                return true;
            }
        }
        return false;
    }
}