package io.github.polysmee.calendar.calendarActivityFragments;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
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
import io.github.polysmee.calendar.CalendarAppointmentInfo;
import io.github.polysmee.calendar.DailyCalendar;
import io.github.polysmee.database.DatabaseAppointment;
import io.github.polysmee.database.Appointment;
import io.github.polysmee.database.User;
import io.github.polysmee.login.MainUserSingleton;
import io.github.polysmee.room.RoomActivity;
import static io.github.polysmee.calendar.calendarActivityFragments.CalendarActivityFragmentsHelpers.*;

public class CalendarActivityPublicAppointmentsFragment extends Fragment {


    private ViewGroup rootView;
    private LinearLayout scrollLayout ;
    private LayoutInflater inflater ;


    private User user;
    public static final String APPOINTMENT_DETAIL_CALENDAR_ID_FROM = "APPOINTMENT_DETAIL_CALENDAR_ID_FROM";
    private final AtomicInteger childrenCounters = new AtomicInteger(0);
    private final int CALENDAR_ENTRY_DETAIL_CODE = 51;

    private Set<String> appointmentSet = new HashSet<>();
    private Map<String, CalendarAppointmentInfo> appointmentInfoMap = new HashMap<>();
    private Map<String, View> appointmentIdsToView = new HashMap<>();

    private ArrayList<String> courses;
    private String currentCourse = "";
    AlertDialog.Builder builder;
    AutoCompleteTextView courseSelector;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = (ViewGroup) inflater.inflate(R.layout.fragment_calendar_activity_public_appointments, container, false);
        scrollLayout = (LinearLayout)rootView.findViewById(R.id.calendarActivityPublicAppointmentsScrollLayout);
        this.inflater = inflater;
        setTodayDateInDailyCalendar(true);
        setDayText(rootView,true);
        user = MainUserSingleton.getInstance();
        ((SwipeRefreshLayout)rootView.findViewById(R.id.calendarActivityPublicAppointmentSwipeScroll)).setOnRefreshListener(()->{
            getAllPublicAppointmentsForTheDay();
            ((SwipeRefreshLayout)rootView.findViewById(R.id.calendarActivityPublicAppointmentSwipeScroll)).setRefreshing(false);
        });
        rootView.findViewById(R.id.todayDatePublicAppointmentsCalendarActivity).setOnClickListener(v -> chooseDate());

        courseSelector = rootView.findViewById(R.id.calendarActivityPublicAppointmentsEditTxtCourse);

        //for now the database doesn't support courses so we use a fixed array
        courses = new ArrayList<>(Arrays.asList("SDP", "Sweng", "ICG", "Quantique", "AI", "Databases", "SHS", "Misc", "Analysis", "IntroProg", ""));

        builder = new AlertDialog.Builder(getActivity());

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_dropdown_item_1line, courses);
        courseSelector.setAdapter(adapter);
        rootView.findViewById(R.id.calendarActivityPublicAppointmentsFilterBtn).setOnClickListener(v -> filter());

        getAllPublicAppointmentsForTheDay();
        return rootView;
    }

    private void filter(){
        String s = courseSelector.getText().toString();
        if(!courses.contains(s)) {
            builder.setMessage(getString(R.string.genericCourseNotFoundText))
                    .setCancelable(false)
                    .setPositiveButton(getString(R.string.genericOkText), null);

            AlertDialog alert = builder.create();
            alert.setTitle(getString(R.string.genericErrorText));
            alert.show();
        } else {
            currentCourse = s;
            getAllPublicAppointmentsForTheDay();
        }
    }


    /**
     * Behavior of the appointment date button; will pop a date picker dialog to let the user
     * choose the date they want, and will add a listener to the user's appointment to show the appointments
     * they have that given day.
     */
    protected void chooseDate(){
        long epochTimeChosenDay = DailyCalendar.getDayEpochTimeAtMidnight(true) ;
        Date chosenDay = new Date(epochTimeChosenDay);

        Calendar calendarChosenDay = Calendar.getInstance();
        calendarChosenDay.setTime(chosenDay);

        new DatePickerDialog(getContext(), (view, year, monthOfYear, dayOfMonth) -> {
            DailyCalendar.setDayEpochTimeAtMidnight(year,monthOfYear,dayOfMonth,true);
            setDayText(rootView,true);
            scrollLayout.removeAllViewsInLayout();
            getAllPublicAppointmentsForTheDay();
        }, calendarChosenDay.get(Calendar.YEAR), calendarChosenDay.get(Calendar.MONTH), calendarChosenDay.get(Calendar.DATE)).show();

    }
    @Override
    public void onResume() {
        super.onResume();
    }

    /**
     * Changes the calendar's layout to show the user's daily appointments at the time
     * this method is called.
     */
    protected void changeCurrentCalendarLayout(Set<CalendarAppointmentInfo> infos){
        List<CalendarAppointmentInfo> todayAppointments = DailyCalendar.getAppointmentsForTheDay(infos,true);
        if(!todayAppointments.isEmpty()){
            for(CalendarAppointmentInfo appointment : todayAppointments){
                if(!currentCourse.equals("")) {
                    if(appointment.getCourse().equals(currentCourse)) {
                        addAppointmentToCalendarLayout(appointment);
                    }
                } else {
                    addAppointmentToCalendarLayout(appointment);
                }
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

        Appointment appointment1 = new DatabaseAppointment(appointment.getId());
        appointment1.getOwnerIdAndThen((ownerId) ->{
            if(!ownerId.equals(user.getId())){
                calendarEntry.findViewById(R.id.publicCalendarEntryButtonJoin).setVisibility(View.VISIBLE);
                calendarEntry.findViewById(R.id.publicCalendarEntryButtonJoin).setOnClickListener((v) -> joinPublicAppointmentWhenClickingOnJoin(appointment.getId()));
            }

        });
        Date startDate = new Date(appointment.getStartTime());
        Date endDate = new Date((appointment.getStartTime()+appointment.getDuration()));
        Date current = new Date(System.currentTimeMillis());
        calendarEntry.setOnClickListener(v -> goToAppointmentDetails(appointment.getId(),this,rootView));

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
     * Adds an appointment to the calendar layout, as a calendar entry
     * @param appointment the appointment to add
     */
    protected void addAppointmentToCalendarLayout(CalendarAppointmentInfo appointment){

        ConstraintLayout appointmentEntryLayout = (ConstraintLayout) inflater.inflate(R.layout.element_calendar_entry_public,null);
        createAppointmentEntry(appointment, appointmentEntryLayout);
        TextView emptySpace = new TextView(rootView.getContext());

        scrollLayout.addView(appointmentEntryLayout);
        scrollLayout.addView(emptySpace);
        appointmentIdsToView.put(appointment.getId(),appointmentEntryLayout);
        appointmentIdsToView.put(appointment.getId() + 1,emptySpace);
    }


    /**
     * Method called when the user clicks on the "join" button next an appointment description in the public appointments.
     * The user joins the appointment by doing so
     * @param appointmentId the appointment's id which the user will join .
     */
    protected void joinPublicAppointmentWhenClickingOnJoin(String appointmentId){
        Appointment appointment = new DatabaseAppointment(appointmentId);
        appointment.addParticipant(user);
        user.addAppointment(appointment);
    }
    /**
     * Gets all public appointments once, display only the ones on the selected day; to be called
     * when the fragment is loaded for the first time, when clicking on the refresh button or when filtering.
     */
    protected void getAllPublicAppointmentsForTheDay() {
        Appointment.getAllPublicAppointmentsOnce((allPublicAppointmentsIds) ->{

            Set<String> deletedAppointments = new HashSet<>(appointmentSet);
            Set<String> newAppointments = new HashSet<>(allPublicAppointmentsIds);
            scrollLayout.removeAllViewsInLayout();

            deletedAppointments.removeAll(newAppointments); //keep the deleted appointments
            newAppointments.removeAll(appointmentSet); //keep the new appointmnets

            for(String oldAppointmentId: deletedAppointments){ //delete all old appointments
                appointmentSet.remove(oldAppointmentId);
                appointmentInfoMap.remove(oldAppointmentId);

            }
            if(newAppointments.isEmpty()){
                changeCurrentCalendarLayout(new HashSet<>(appointmentInfoMap.values()));
            }
            else{
                appointmentSet.addAll(newAppointments); //add all new appointments

                for(String id: newAppointments){ //iterate only on the new appointments, to set their listener when they're added, not everytime we delete/add an appointment
                    Appointment appointment = new DatabaseAppointment(id);

                    appointment.getPrivateAndThen((isPrivate) ->{
                        if(!isPrivate){
                            CalendarAppointmentInfo appointmentInfo = new CalendarAppointmentInfo("","",0,0,id);
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
                                            }
                                            changeCurrentCalendarLayout(new HashSet<>(appointmentInfoMap.values()));
                                        });
                                    });
                                });

                            });
                        }
                        else{
                            appointmentInfoMap.remove(id);
                            if(appointmentIdsToView.containsKey(id)){
                                scrollLayout.removeView(appointmentIdsToView.get(id));
                                scrollLayout.removeView(appointmentIdsToView.get(id+1));
                            }
                        }
                    });
                }
            }

        });
    }
}