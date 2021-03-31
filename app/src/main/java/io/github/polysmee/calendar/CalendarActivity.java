package io.github.polysmee.calendar;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.app.DatePickerDialog;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
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

    private static final int constraintLayoutIdForTests = 284546;

    private User user;
    public final static String UserTypeCode = "TYPE_OF_USER";
    private String userType ;
    private int demo_indexer = 0;
    public static final String APPOINTMENT_DETAIL_CALENDAR_ID_FROM = "APPOINTMENT_DETAIL_CALENDAR_ID_FROM";
    private final List<CalendarAppointmentInfo> appointmentInfos = new ArrayList<>();
    private final AtomicInteger childrenCounters = new AtomicInteger(0);
    private final int CALENDAR_ENTRY_DETAIL_CODE = 51;

    private Set<String> appointmentSet = new HashSet<>();
    private Map<String,CalendarAppointmentInfo> appointmentInfoMap = new HashMap<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userType = (String) getIntent().getSerializableExtra(UserTypeCode);
        setContentView(R.layout.activity_calendar2);
        if(userType.equals("Real")){
            user = MainUserSingleton.getInstance();
        }
        else{
            user = FakeDatabaseUser.getInstance();
        }
        scrollLayout = (LinearLayout)findViewById(R.id.calendarActivityScrollLayout);
        inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        setTodayDateInDailyCalendar();
        setDayText();


        Button createAppointmentButton    = findViewById(R.id.calendarActivityCreateAppointmentButton);
        createAppointmentButton.setOnClickListener((v) -> createAppointment());

        Button chosenDateButton = findViewById(R.id.todayDateCalendarActivity);
        chosenDateButton.setOnClickListener((v) -> {
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
        if (user.getClass() == FakeDatabaseUser.class) {
            user.createNewUserAppointment(DailyCalendar.getDayEpochTimeAtMidnight() + demo_indexer *60, 50 ,
                    "FakeCourse" + demo_indexer,"FakeTitle" + demo_indexer);
            demo_indexer += 1;
            addListenerToUserAppointments();
        } else {
            Intent intent = new Intent(this, AppointmentActivity.class);
            startActivity(intent);
        }
    }

    public void launchSettings(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //FOR THE TESTS ONLY
        if(user.getClass() == FakeDatabaseUser.class){
            String title = data.getStringExtra(CalendarEntryDetailsActivity.APPOINTMENT_DETAIL_CALENDAR_MODIFY_TITLE);
            String course = data.getStringExtra(CalendarEntryDetailsActivity.APPOINTMENT_DETAIL_CALENDAR_MODIFY_COURSE);
            String id = data.getStringExtra(CalendarEntryDetailsActivity.APPOINTMENT_DETAIL_CALENDAR_ID_TO);
            System.out.println(title); System.out.println(course);
            CalendarAppointmentInfo info = appointmentInfoMap.get(id);
            info.setCourse(course); info.setTitle(title); addListenerToUserAppointments();
        }
    }

    /**
     * Method that will launch the CalendarEntryDetailsActivity for the appointment
     * with the given id. It will launch when clicking on the "Details" button next
     * to the corresponding appointment.
     * @param id the appointment of interest' id
     */
    protected void goToAppointmentDetails(String id){
        Intent intent = new Intent(this,CalendarEntryDetailsActivity.class);
        intent.putExtra(APPOINTMENT_DETAIL_CALENDAR_ID_FROM,id);
        intent.putExtra(UserTypeCode,userType);
        startActivityForResult(intent, CALENDAR_ENTRY_DETAIL_CODE);
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
        ConstraintLayout appointmentLayout = (ConstraintLayout) inflater.inflate(R.layout.activity_calendar_entry,null);
        Button appointmentDescription = appointmentLayout.findViewById(R.id.descriptionOfAppointmentCalendarEntry);
        appointmentDescription.setOnClickListener((v) -> launchRoomActivityWhenClickingOnDescription(appointment.getId()));
        Button detailsButton = (Button)appointmentLayout.findViewById(R.id.detailsButtonCalendarEntry);
        appointmentDescription.setText(createAppointmentDescription(appointment));
        appointmentLayout.setId(constraintLayoutIdForTests + i);
        appointmentDescription.setId(constraintLayoutIdForTests + i + 1);
        detailsButton.setId(constraintLayoutIdForTests + i + 2);

        detailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToAppointmentDetails(appointment.getId());
            }
        });

        this.scrollLayout.addView(appointmentLayout);



    }

    /**
     * Sets the text view on top of the calendar to the current day's date
     */
    protected void setDayText(){
        Button dateText = findViewById(R.id.todayDateCalendarActivity);
        dateText.setText(""); //clear it first
        long epochTimeToday = DailyCalendar.getDayEpochTimeAtMidnight() * 1000;
        Date today = new Date(epochTimeToday);
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        dateText.setText(String.format("Appointments on the %s : ", formatter.format(today)));
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
                                /*if(checkIfAlreadyInList(id)){
                                    scrollLayout.removeAllViewsInLayout();
                                    appointmentSet.remove(getElementInList(id));
                                    appointmentInfos.remove(getElementInList(id));
                                    appointmentSet.add(appointmentInfo);
                                    appointmentInfos.add(appointmentInfo);
                                }
                                else{
                                    scrollLayout.removeAllViewsInLayout();
                                    appointmentInfos.add(appointmentInfo);
                                    appointmentSet.add(appointmentInfo);
                                }*/

                            });
                        });
                    });

                });

            }
        });
    }

    /**
     * Function to be used in pair with "getElementInList" method to manage the
     * calendar appointment infos. This function checks if the appointment description
     * with the corresponding id was already added to the list of all description.
     * It is used to update the set of descriptions when needed
     * @param id the appointment's id whose description we want to check the existence of in the list
     * @return true if and only if the appointment's description was already added to the list
     * */
    protected boolean checkIfAlreadyInList(String id){
        for(CalendarAppointmentInfo infos: appointmentInfos){
            if(infos.getId().equals(id)){
                return true;
            }
        }
        return false;
    }

    /**
     * Function to be used in pair with "checkIfAlreadyInList". This function will return
     * the appointment's description stored in the list of all descriptions, so we can update
     * it.
     * @param id the appointment's id whose description we want to get
     * @return the appointment's description, or null if it wasn't added
     */
    protected CalendarAppointmentInfo getElementInList(String id){
        for(CalendarAppointmentInfo infos: appointmentInfos){
            if(infos.getId().equals(id)){
                return infos;
            }
        }
        return null;
    }
}