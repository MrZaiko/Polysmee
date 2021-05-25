package io.github.polysmee.calendar.fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import io.github.polysmee.R;
import io.github.polysmee.agora.Command;
import io.github.polysmee.calendar.CalendarAppointmentInfo;
import io.github.polysmee.calendar.DailyCalendar;
import io.github.polysmee.calendar.googlecalendarsync.CalendarUtilities;
import io.github.polysmee.database.Appointment;
import io.github.polysmee.database.Course;
import io.github.polysmee.database.DatabaseAppointment;
import io.github.polysmee.database.User;
import io.github.polysmee.database.databaselisteners.BooleanValueListener;
import io.github.polysmee.database.databaselisteners.LongValueListener;
import io.github.polysmee.database.databaselisteners.StringSetValueListener;
import io.github.polysmee.database.databaselisteners.StringValueListener;
import io.github.polysmee.login.MainUser;

import static io.github.polysmee.calendar.fragments.CalendarActivityFragmentsHelpers.goToAppointmentDetails;
import static io.github.polysmee.calendar.fragments.CalendarActivityFragmentsHelpers.setDayText;
import static io.github.polysmee.calendar.fragments.CalendarActivityFragmentsHelpers.setTodayDateInDailyCalendar;

public class CalendarActivityPublicAppointmentsFragment extends Fragment {


    private ViewGroup rootView;
    private LinearLayout scrollLayout;
    private LayoutInflater inflater;


    private User user;

    private final Set<String> appointmentSet = new HashSet<>();
    private final Map<String, CalendarAppointmentInfo> appointmentInfoMap = new HashMap<>();
    private final Map<String, View> appointmentIdsToView = new HashMap<>();

    private ArrayList<String> courses;
    private String currentCourse = "";
    AlertDialog.Builder builder;
    AutoCompleteTextView courseSelector;
    private boolean sortChronologically = true;
    private final static int SORT_CHRONOLOGICALLY_INDEX = 0;

    //Commands to remove listeners
    private List<Command> commandsToRemoveListeners = new ArrayList<Command>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = (ViewGroup) inflater.inflate(R.layout.fragment_calendar_activity_public_appointments, container, false);
        scrollLayout = rootView.findViewById(R.id.calendarActivityPublicAppointmentsScrollLayout);
        this.inflater = inflater;
        setTodayDateInDailyCalendar(true);
        setDayText(rootView, true);
        user = MainUser.getMainUser();
        ((SwipeRefreshLayout) rootView.findViewById(R.id.calendarActivityPublicAppointmentSwipeScroll)).setOnRefreshListener(() -> {
            getAllPublicAppointmentsForTheDay();
            ((SwipeRefreshLayout) rootView.findViewById(R.id.calendarActivityPublicAppointmentSwipeScroll)).setRefreshing(false);
        });
        rootView.findViewById(R.id.todayDatePublicAppointmentsCalendarActivity).setOnClickListener(v -> chooseDate());

        courseSelector = rootView.findViewById(R.id.calendarActivityPublicAppointmentsEditTxtCourse);

        Course.getAllCourses_Once_AndThen(s -> {
                    courses = new ArrayList<>(s);
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                            android.R.layout.simple_dropdown_item_1line, courses);
                    courseSelector.setAdapter(adapter);
                }
        );

        builder = new AlertDialog.Builder(getActivity());
        rootView.findViewById(R.id.calendarActivityPublicAppointmentsFilterBtn).setOnClickListener(v -> filter());

        getAllPublicAppointmentsForTheDay();

        //Initialize spinner
        Spinner spinner = (Spinner) rootView.findViewById(R.id.sortPublicAppointmentsSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.sort_public_appointments_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(SORT_CHRONOLOGICALLY_INDEX);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sortChronologically = position == SORT_CHRONOLOGICALLY_INDEX;
                getAllPublicAppointmentsForTheDay();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return rootView;
    }

    private void filter() {
        String s = courseSelector.getText().toString();
        if (!courses.contains(s)) {
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
    protected void chooseDate() {
        long epochTimeChosenDay = DailyCalendar.getDayEpochTimeAtMidnight(true);
        Date chosenDay = new Date(epochTimeChosenDay);

        Calendar calendarChosenDay = Calendar.getInstance();
        calendarChosenDay.setTime(chosenDay);

        new DatePickerDialog(getContext(), (view, year, monthOfYear, dayOfMonth) -> {
            DailyCalendar.setDayEpochTimeAtMidnight(year, monthOfYear, dayOfMonth, true);
            setDayText(rootView, true);
            scrollLayout.removeAllViewsInLayout();
            getAllPublicAppointmentsForTheDay();
        }, calendarChosenDay.get(Calendar.YEAR), calendarChosenDay.get(Calendar.MONTH), calendarChosenDay.get(Calendar.DATE)).show();

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        Object dummyArgument = null;
        for(Command command: commandsToRemoveListeners) {
            command.execute(dummyArgument,dummyArgument);
        }

        super.onDestroy();
    }

    /**
     * Changes the calendar's layout to show the user's daily appointments at the time
     * this method is called.
     */
    protected void changeCurrentCalendarLayout(Set<CalendarAppointmentInfo> infos) {
        List<CalendarAppointmentInfo> todayAppointments = DailyCalendar.getAppointmentsForTheDay(infos, true, sortChronologically);
        if (!todayAppointments.isEmpty()) {
            for (CalendarAppointmentInfo appointment : todayAppointments) {
                if (!currentCourse.equals("")) {
                    if (appointment.getCourse().equals(currentCourse)) {
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
     *
     * @param appointment the appointment's whose description is created
     */
    protected void createAppointmentEntry(CalendarAppointmentInfo appointment, View calendarEntry) {
        ((TextView) calendarEntry.findViewById(R.id.calendarEntryAppointmentTitle)).setText(appointment.getTitle());
        ((TextView) calendarEntry.findViewById(R.id.calendarEntryNumberOfParticipants)).setText("participants : " + Integer.toString(appointment.getNumberOfParticipants()));

        Appointment appointment1 = new DatabaseAppointment(appointment.getId());
        StringValueListener ownerListener = (ownerId) ->{
            if(!ownerId.equals(user.getId())){
                calendarEntry.findViewById(R.id.publicCalendarEntryButtonJoin).setVisibility(View.VISIBLE);
                calendarEntry.findViewById(R.id.publicCalendarEntryButtonJoin).setOnClickListener((v) -> joinPublicAppointmentWhenClickingOnJoin(appointment.getId()));
            }

        };
        appointment1.getOwnerIdAndThen(ownerListener);
        commandsToRemoveListeners.add((x,y) -> appointment1.removeOwnerListener(ownerListener));
        Date startDate = new Date(appointment.getStartTime());
        Date endDate = new Date((appointment.getStartTime() + appointment.getDuration()));
        Date current = new Date(System.currentTimeMillis());
        calendarEntry.setOnClickListener(v -> goToAppointmentDetails(appointment.getId(), this, rootView));

        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm", Locale.getDefault());
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
     *
     * @param appointment the appointment to add
     */
    protected void addAppointmentToCalendarLayout(CalendarAppointmentInfo appointment) {

        ConstraintLayout appointmentEntryLayout = (ConstraintLayout) inflater.inflate(R.layout.element_calendar_entry_public, null);
        createAppointmentEntry(appointment, appointmentEntryLayout);
        TextView emptySpace = new TextView(rootView.getContext());

        scrollLayout.addView(appointmentEntryLayout);
        scrollLayout.addView(emptySpace);
        appointmentIdsToView.put(appointment.getId(), appointmentEntryLayout);
        appointmentIdsToView.put(appointment.getId() + 1, emptySpace);
    }


    /**
     * Method called when the user clicks on the "join" button next an appointment description in the public appointments.
     * The user joins the appointment by doing so
     *
     * @param appointmentId the appointment's id which the user will join .
     */
    protected void joinPublicAppointmentWhenClickingOnJoin(String appointmentId) {
        Appointment appointment = new DatabaseAppointment(appointmentId);

        user.getCalendarId_Once_AndThen(calendarId -> {
            if (calendarId != null && !calendarId.equals("")) {
                appointment.addParticipant(user);
                appointment.getTitle_Once_AndThen(title ->
                        appointment.getCourse_Once_AndThen( course ->
                                appointment.getStartTime_Once_AndThen( startTime ->
                                        appointment.getDuration_Once_AndThen( duration ->
                                            CalendarUtilities.addAppointmentToCalendar(getContext(),
                                                    calendarId, title, course, startTime, duration,
                                                    eventId -> MainUser.getMainUser().addAppointment(appointment, eventId),
                                                    () -> getActivity().runOnUiThread( () ->{
                                                        Toast toast = Toast.makeText(getContext(), getText(R.string.genericErrorText), Toast.LENGTH_SHORT);
                                                        toast.show();
                                                    })
                                            )
                                        )
                                )
                        )
                );
            } else {
                user.addAppointment(appointment, "");
                appointment.addParticipant(user);
            }
        });
    }

    /**
     * Gets all public appointments once, display only the ones on the selected day; to be called
     * when the fragment is loaded for the first time, when clicking on the refresh button or when filtering.
     */
    protected void getAllPublicAppointmentsForTheDay() {
        Appointment.getAllPublicAppointmentsOnce((allPublicAppointmentsIds) -> {

            Set<String> deletedAppointments = new HashSet<>(appointmentSet);
            Set<String> newAppointments = new HashSet<>(allPublicAppointmentsIds);
            scrollLayout.removeAllViewsInLayout();

            deletedAppointments.removeAll(newAppointments); //keep the deleted appointments
            newAppointments.removeAll(appointmentSet); //keep the new appointmnets

            for (String oldAppointmentId : deletedAppointments) { //delete all old appointments
                appointmentSet.remove(oldAppointmentId);
                appointmentInfoMap.remove(oldAppointmentId);

            }
            if (newAppointments.isEmpty()) {
                changeCurrentCalendarLayout(new HashSet<>(appointmentInfoMap.values()));
            } else {
                appointmentSet.addAll(newAppointments); //add all new appointments

                for (String id : newAppointments) { //iterate only on the new appointments, to set their listener when they're added, not everytime we delete/add an appointment
                    Appointment appointment = new DatabaseAppointment(id);

                    BooleanValueListener privateListener = (isPrivate) ->{
                        if(!isPrivate){
                            CalendarAppointmentInfo appointmentInfo = new CalendarAppointmentInfo("","",0,0,id,0);
                            LongValueListener startListener = (start)->{

                                appointmentInfo.setStartTime(start);
                                LongValueListener durationListener = (duration) -> {
                                    appointmentInfo.setDuration(duration);
                                  
                                    StringValueListener titleListener = (title) ->{
                                        appointmentInfo.setTitle((title));
                                        StringValueListener courseListener = (course) ->{
                                            appointmentInfo.setCourse(course);
                                            StringSetValueListener participantListener = (participants) -> {
                                                appointmentInfo.setNumberOfParticipants(participants.size());
                                                scrollLayout.removeAllViewsInLayout();
                                                if (!appointmentSet.contains(appointmentInfo.getId())) {
                                                    appointmentInfoMap.remove(appointmentInfo.getId());
                                                } else {
                                                    appointmentInfoMap.put(appointment.getId(), appointmentInfo);
                                                }
                                                changeCurrentCalendarLayout(new HashSet<>(appointmentInfoMap.values()));
                                            };

                                            appointment.getParticipantsIdAndThen(participantListener);
                                            commandsToRemoveListeners.add((x,y) -> appointment.removeParticipantsListener(participantListener));

                                        };
                                        appointment.getCourseAndThen(courseListener);
                                        commandsToRemoveListeners.add((x,y) -> appointment.removeCourseListener(courseListener));
                                    };
                                    appointment.getTitleAndThen(titleListener);
                                    commandsToRemoveListeners.add((x,y) -> appointment.removeTitleListener(titleListener));
                                };
                                appointment.getDurationAndThen(durationListener);
                                commandsToRemoveListeners.add((x,y) -> appointment.removeDurationListener(durationListener));

                            };
                            appointment.getStartTimeAndThen(startListener);
                            commandsToRemoveListeners.add((x,y) -> appointment.removeStartListener(startListener));
                        }
                        else{
                            appointmentInfoMap.remove(id);
                            if (appointmentIdsToView.containsKey(id)) {
                                scrollLayout.removeView(appointmentIdsToView.get(id));
                                scrollLayout.removeView(appointmentIdsToView.get(id + 1));
                            }
                        }
                    };
                    appointment.getPrivateAndThen(privateListener);
                    commandsToRemoveListeners.add((x,y) -> appointment.removePrivateListener(privateListener));
                }
            }

        });
    }
}