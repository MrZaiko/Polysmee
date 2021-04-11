package io.github.polysmee.calendar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.app.DatePickerDialog;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import io.github.polysmee.R;
import io.github.polysmee.appointments.AppointmentActivity;
import io.github.polysmee.database.DatabaseAppointment;
import io.github.polysmee.database.decoys.FakeDatabaseAppointment;
import io.github.polysmee.database.decoys.FakeDatabaseUser;
import io.github.polysmee.interfaces.Appointment;
import io.github.polysmee.interfaces.User;
import io.github.polysmee.login.MainUserSingleton;
import io.github.polysmee.room.RoomActivity;
import io.github.polysmee.settings.SettingsActivity;

public class CalendarActivity extends AppCompatActivity{

    private LinearLayout scrollLayout ;
    private LayoutInflater inflater ;

    public static final int constraintLayoutIdForTests = 284546;

    private User user;
    public final static String UserTypeCode = "TYPE_OF_USER";
    private final AtomicInteger childrenCounters = new AtomicInteger(0);

    private Set<String> appointmentSet = new HashSet<>();
    private final Map<String,CalendarAppointmentInfo> appointmentInfoMap = new HashMap<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        user = MainUserSingleton.getInstance();
        scrollLayout = findViewById(R.id.calendarActivityScrollLayout);
        inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        setTodayDateInDailyCalendar();
        setDayText();

        findViewById(R.id.calendarActivityCreateAppointmentButton).setOnClickListener((v) -> createAppointment());

        findViewById(R.id.todayDateCalendarActivity).setOnClickListener((v) -> {
            chooseDate();
        });

        addListenerToUserAppointments();
    }

    /**
     * Sets the date to the day when the user launches the app at startup
     */
    protected void setTodayDateInDailyCalendar(){
        Calendar calendar = Calendar.getInstance();
        DailyCalendar.setDayEpochTimeAtMidnight(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DATE));
    }

    /**
     * Behavior of the appointment date button; will pop a date picker dialog to let the user
     * choose the date they want, and will add a listener to the user's appointment to show the appointments
     * they have that given day.
     */
    protected void chooseDate(){
        long epochTimeChosenDay = DailyCalendar.getDayEpochTimeAtMidnight() * 1000;
        Date chosenDay = new Date(epochTimeChosenDay);

        Calendar calendarChosenDay = Calendar.getInstance();
        calendarChosenDay.setTime(chosenDay);

        new DatePickerDialog(CalendarActivity.this, (view, year, monthOfYear, dayOfMonth) -> {
            DailyCalendar.setDayEpochTimeAtMidnight(year,monthOfYear,dayOfMonth);
            setDayText();
            scrollLayout.removeAllViewsInLayout();
            addListenerToUserAppointments();
        }, calendarChosenDay.get(Calendar.YEAR), calendarChosenDay.get(Calendar.MONTH), calendarChosenDay.get(Calendar.DATE)).show();

    }

    @Override
    protected void onResume() {
        super.onResume();
        addListenerToUserAppointments();
    }

    /*
     * Behavior of the create appointment button, depending if the user is real or fake
     */
    private void createAppointment(){
        Intent intent = new Intent(this, AppointmentActivity.class);
        startActivity(intent);
    }

    /**
     * Method that will launch the CalendarEntryDetailsActivity for the appointment
     * with the given id. It will launch when clicking on the "Details" button next
     * to the corresponding appointment.
     * @param id the appointment of interest' id
     */
    protected void goToAppointmentDetails(String id){
        Intent intent = new Intent(this, AppointmentActivity.class);
        intent.putExtra(AppointmentActivity.LAUNCH_MODE, AppointmentActivity.DETAIL_MODE);
        intent.putExtra(AppointmentActivity.APPOINTMENT_ID, id);
        startActivity(intent);
    }


    /**
     * Changes the calendar's layout to show the user's daily appointments at the time
     * this method is called.
     */
    protected void changeCurrentCalendarLayout(Set<CalendarAppointmentInfo> infos){
        List<CalendarAppointmentInfo> todayAppointments = DailyCalendar.getAppointmentsForTheDay(infos);
        if(!todayAppointments.isEmpty()){
            int i = 0;
            for(CalendarAppointmentInfo appointment : todayAppointments){
                addAppointmentToCalendarLayout(appointment,i);
                i+=3;
            }
        }
    }

    /**
     * Creates an appointment's textual description following a certain format
     * to show in the calendar
     * @param appointment the appointment's whose description is created
     * @return the textual representation of the appointment in the calendar
     */
    protected void createAppointmentEntry(CalendarAppointmentInfo appointment, View calendarEntry){
        ((TextView) calendarEntry.findViewById(R.id.calendarEntryAppointmentTitle)).setText(appointment.getTitle());

        Date startDate = new Date(appointment.getStartTime() * 1000);
        Date endDate = new Date((appointment.getStartTime()+appointment.getDuration())*1000);
        Date current = new Date(System.currentTimeMillis());

        if (current.before(startDate))
            calendarEntry.setOnClickListener(v -> goToAppointmentDetails(appointment.getId()));
        else
            calendarEntry.setOnClickListener((v) -> launchRoomActivityWhenClickingOnDescription(appointment.getId()));

        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm", Locale.US);
        String appointmentDate = formatter.format(startDate) + " - " + formatter.format(endDate);
        ((TextView) calendarEntry.findViewById(R.id.calendarEntryAppointmentDate)).setText(appointmentDate);

        ImageView status = calendarEntry.findViewById(R.id.calendarEntryStatus);
        if (current.before(startDate))
            status.setImageResource(R.drawable.calendar_entry_incoming_dot);
        else if (current.after(endDate))
            status.setImageResource(R.drawable.calendar_entry_done_dot);
        else
            status.setImageResource(R.drawable.calendar_entry_ongoing_dot);
    }


    /**
     * Everytime the user clicks on an appointment's description in his daily, the corresponding
     * room activity is launched.
     * @param appointmentId the appointment's id which will see its room launched
     * when clicking on its description.
     */
    protected void launchRoomActivityWhenClickingOnDescription(String appointmentId){
        Intent roomActivityIntent = new Intent(this, RoomActivity.class);
        roomActivityIntent.putExtra(RoomActivity.APPOINTMENT_KEY,appointmentId);
        startActivity(roomActivityIntent);
    }
    /**
     * Adds an appointment to the calendar layout, as a calendar entry
     * @param appointment the appointment to add
     * @param i integer parameter used to create unique ids (at least in the calendar's current layout) for the calendar entry
     */
    protected void addAppointmentToCalendarLayout(CalendarAppointmentInfo appointment, int i){
        ConstraintLayout appointmentEntryLayout = (ConstraintLayout) inflater.inflate(R.layout.element_calendar_entry,null);
        ConstraintLayout appointmentEntryDetailsLayout = appointmentEntryLayout.findViewById(R.id.calendarEntryDetailsLayout);
        //Button detailsButton = appointmentLayout.findViewById(R.id.detailsButtonCalendarEntry);
        createAppointmentEntry(appointment, appointmentEntryLayout);
        appointmentEntryLayout.setId(constraintLayoutIdForTests + i);
        appointmentEntryDetailsLayout.setId(constraintLayoutIdForTests + i + 1);
        //detailsButton.setId(constraintLayoutIdForTests + i + 2);

        /*detailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToAppointmentDetails(appointment.getId());
            }
        });*/

        scrollLayout.addView(appointmentEntryLayout);
        //blankSpace
        scrollLayout.addView(new TextView(this));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.calendar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.calendarMenuSettings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Sets the text view on top of the calendar to the current day's date
     */
    protected void setDayText(){
        ConstraintLayout dateLayout = findViewById(R.id.todayDateCalendarActivity);
        TextView day = dateLayout.findViewById(R.id.activityCalendarDay);
        TextView month = dateLayout.findViewById(R.id.activityCalendarMonth);
        long epochTimeToday = DailyCalendar.getDayEpochTimeAtMidnight() * 1000;
        Date today = new Date(epochTimeToday);

        SimpleDateFormat dayFormat = new SimpleDateFormat("d", Locale.US);
        day.setText(dayFormat.format(today));

        SimpleDateFormat monthFormat = new SimpleDateFormat("EEEE", Locale.US);
        month.setText(monthFormat.format(today));
    }

    /**
     * Adds a listener to the user's appointments so that everytime one is added/removed, the layout
     * is updated. It also takes care of determining what should happen to the calendar's layout
     * if an appointment's parameters changes.
     */

    protected void addListenerToUserAppointments(){
        user.getAppointmentsAndThen((setOfIds)->{
            appointmentSet = new HashSet<>(setOfIds);
            scrollLayout.removeAllViewsInLayout();
            for(String id : setOfIds){
                Appointment appointment;
                if(user.getClass() == FakeDatabaseUser.class)
                    appointment = new FakeDatabaseAppointment(id);
                else
                    appointment = new DatabaseAppointment(id);

                CalendarAppointmentInfo appointmentInfo = new CalendarAppointmentInfo("","",0,0,id,user,childrenCounters.getAndIncrement());

                appointment.getStartTimeAndThen((start)->{
                    appointmentInfo.setStartTime(start);
                    appointment.getDurationAndThen((duration) -> {
                        appointmentInfo.setDuration(duration);
                        appointment.getTitleAndThen((title) ->{
                            appointmentInfo.setTitle((title));
                            appointment.getCourseAndThen((course) ->{
                                appointmentInfo.setCourse(course);
                                scrollLayout.removeAllViewsInLayout();
                                if(!appointmentSet.contains(appointmentInfo.getId())){
                                    appointmentInfoMap.remove(appointmentInfo.getId());
                                }
                                else{
                                appointmentInfoMap.put(appointment.getId(),appointmentInfo);
                                changeCurrentCalendarLayout(new HashSet<>(appointmentInfoMap.values()));
                                }
                            });
                        });
                    });

                });

            }
        });
    }

}