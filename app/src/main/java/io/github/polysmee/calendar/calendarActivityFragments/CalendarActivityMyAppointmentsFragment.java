package io.github.polysmee.calendar.calendarActivityFragments;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

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
import io.github.polysmee.calendar.CalendarAppointmentInfo;
import io.github.polysmee.calendar.DailyCalendar;
import io.github.polysmee.database.DatabaseAppointment;
import io.github.polysmee.database.decoys.FakeDatabaseAppointment;
import io.github.polysmee.database.decoys.FakeDatabaseUser;
import io.github.polysmee.database.Appointment;
import io.github.polysmee.database.User;
import io.github.polysmee.login.MainUserSingleton;
import io.github.polysmee.room.RoomActivity;


public class CalendarActivityMyAppointmentsFragment extends Fragment {


    private ViewGroup rootView;

    private LinearLayout scrollLayout;
    private LayoutInflater inflater;

    private User user;
    private final AtomicInteger childrenCounters = new AtomicInteger(0);

    private Map<String, View> appointmentIdsToView = new HashMap<>();
    private Set<String> appointmentSet = new HashSet<>();
    private Map<String, CalendarAppointmentInfo> appointmentInfoMap = new HashMap<>();


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = (ViewGroup) inflater.inflate(R.layout.fragment_calendar_activity_my_appointments, container, false);
        scrollLayout = (LinearLayout) rootView.findViewById(R.id.calendarActivityMyAppointmentsScrollLayout);
        this.inflater = inflater;
        setTodayDateInDailyCalendar();
        setDayText();
        user = MainUserSingleton.getInstance();

        rootView.findViewById(R.id.calendarActivityCreateAppointmentButton).setOnClickListener((v) -> createAppointment());

        rootView.findViewById(R.id.todayDateMyAppointmentsCalendarActivity).setOnClickListener((v) -> {
            chooseDate();
        });

        addListenerToUserAppointments();
        return rootView;
    }

    /**
     * Sets the date to the day when the user launches the app at startup
     */
    protected void setTodayDateInDailyCalendar() {
        Calendar calendar = Calendar.getInstance();
        DailyCalendar.setDayEpochTimeAtMidnight(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE), false);
    }

    /**
     * Behavior of the appointment date button; will pop a date picker dialog to let the user
     * choose the date they want, and will add a listener to the user's appointment to show the appointments
     * they have that given day.
     */
    protected void chooseDate() {
        long epochTimeChosenDay = DailyCalendar.getDayEpochTimeAtMidnight(false);
        Date chosenDay = new Date(epochTimeChosenDay);

        Calendar calendarChosenDay = Calendar.getInstance();
        calendarChosenDay.setTime(chosenDay);

        new DatePickerDialog(getContext(), (view, year, monthOfYear, dayOfMonth) -> {
            DailyCalendar.setDayEpochTimeAtMidnight(year, monthOfYear, dayOfMonth, false);
            setDayText();
            scrollLayout.removeAllViewsInLayout();
            addListenerToUserAppointments();
        }, calendarChosenDay.get(Calendar.YEAR), calendarChosenDay.get(Calendar.MONTH), calendarChosenDay.get(Calendar.DATE)).show();

    }

    @Override
    public void onResume() {
        super.onResume();
        addListenerToUserAppointments();
    }

    /*
     * Behavior of the create appointment button, depending if the user is real or fake
     */
    private void createAppointment() {
        Intent intent = new Intent(rootView.getContext(), AppointmentActivity.class);
        startActivity(intent);
    }

    /**
     * Method that will launch the CalendarEntryDetailsActivity for the appointment
     * with the given id. It will launch when clicking on the "Details" button next
     * to the corresponding appointment.
     *
     * @param id the appointment of interest' id
     */
    protected void goToAppointmentDetails(String id) {
        Intent intent = new Intent(rootView.getContext(), AppointmentActivity.class);
        intent.putExtra(AppointmentActivity.LAUNCH_MODE, AppointmentActivity.DETAIL_MODE);
        intent.putExtra(AppointmentActivity.APPOINTMENT_ID, id);
        startActivity(intent);
    }


    /**
     * Changes the calendar's layout to show the user's daily appointments at the time
     * this method is called.
     */
    protected void changeCurrentCalendarLayout(Set<CalendarAppointmentInfo> infos) {
        List<CalendarAppointmentInfo> todayAppointments = DailyCalendar.getAppointmentsForTheDay(infos, false);
        if (!todayAppointments.isEmpty()) {
            for (CalendarAppointmentInfo appointment : todayAppointments) {
                addAppointmentToCalendarLayout(appointment);
            }
        }
    }

    /**
     * Creates an appointment's textual description following a certain format
     * to show in the calendar
     *
     * @param appointment the appointment's whose description is created
     * @return the textual representation of the appointment in the calendar
     */
    protected void createAppointmentEntry(CalendarAppointmentInfo appointment, View calendarEntry) {
        ((TextView) calendarEntry.findViewById(R.id.calendarEntryAppointmentTitle)).setText(appointment.getTitle());

        Date startDate = new Date(appointment.getStartTime());
        Date endDate = new Date((appointment.getStartTime() + appointment.getDuration()));
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
     *
     * @param appointmentId the appointment's id which will see its room launched
     *                      when clicking on its description.
     */
    protected void launchRoomActivityWhenClickingOnDescription(String appointmentId) {
        Intent roomActivityIntent = new Intent(rootView.getContext(), RoomActivity.class);
        roomActivityIntent.putExtra(RoomActivity.APPOINTMENT_KEY, appointmentId);
        startActivity(roomActivityIntent);
    }

    /**
     * Adds an appointment to the calendar layout, as a calendar entry
     *
     * @param appointment the appointment to add
     */
    protected void addAppointmentToCalendarLayout(CalendarAppointmentInfo appointment) {
        ConstraintLayout appointmentEntryLayout = (ConstraintLayout) inflater.inflate(R.layout.element_calendar_entry, null);
        createAppointmentEntry(appointment, appointmentEntryLayout);
        scrollLayout.addView(appointmentEntryLayout);
        appointmentIdsToView.put(appointment.getId(), appointmentEntryLayout);
        //scrollLayout.addView(new TextView(rootView.getContext()));
    }


    /**
     * Sets the text view on top of the calendar to the current day's date
     */
    protected void setDayText() {
        ConstraintLayout dateLayout = rootView.findViewById(R.id.todayDateMyAppointmentsCalendarActivity);
        TextView day = dateLayout.findViewById(R.id.activityCalendarDayMyAppointments);
        TextView month = dateLayout.findViewById(R.id.activityCalendarMonthMyAppointments);
        long epochTimeToday = DailyCalendar.getDayEpochTimeAtMidnight(false);
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

    protected void addListenerToUserAppointments() {
        user.getAppointmentsAndThen((setOfIds) -> {
            Set<String> deletedAppointments = new HashSet<>(appointmentSet);
            Set<String> newAppointments = new HashSet<>(setOfIds);

            //two loops: one for the appointments that are gone, and another for the new appointments

            deletedAppointments.removeAll(newAppointments); //keep the deleted appointments
            newAppointments.removeAll(appointmentSet); //keep the new appointmnets

            for (String oldAppointmentId : deletedAppointments) { //delete all old appointments
                appointmentSet.remove(oldAppointmentId);
                appointmentInfoMap.remove(oldAppointmentId);
                if (appointmentIdsToView.containsKey(oldAppointmentId))
                    scrollLayout.removeView(appointmentIdsToView.get(oldAppointmentId));
            }


            appointmentSet.addAll(newAppointments); //add all new appointments
            if(newAppointments.isEmpty()){
                scrollLayout.removeAllViewsInLayout();
                changeCurrentCalendarLayout(new HashSet<>(appointmentInfoMap.values()));
                return;
            }
            for (String id : newAppointments) { //iterate only on the new appointments, to set their listener
                Appointment appointment = new DatabaseAppointment(id);

                CalendarAppointmentInfo appointmentInfo = new CalendarAppointmentInfo("", "", 0, 0, id, user, childrenCounters.getAndIncrement());

                appointment.getStartTimeAndThen((start) -> {
                    appointmentInfo.setStartTime(start);
                    appointment.getDurationAndThen((duration) -> {
                        appointmentInfo.setDuration(duration);
                        appointment.getTitleAndThen((title) -> {
                            appointmentInfo.setTitle((title));
                            appointment.getCourseAndThen((course) -> {
                                appointmentInfo.setCourse(course);
                                if (!appointmentSet.contains(appointmentInfo.getId())) { //the appointment was removed; we thus have to remove it from the displayed appointments
                                    appointmentInfoMap.remove(appointmentInfo.getId());
                                    if (appointmentIdsToView.containsKey(id)){
                                        scrollLayout.removeView(appointmentIdsToView.get(id));
                                    }
                                } else {
                                    if (appointmentInfoMap.containsKey(appointmentInfo.getId())) { //the view is already there, we just need to update it.
                                        appointmentInfoMap.put(appointment.getId(), appointmentInfo);
                                        updateCalendarEntry(appointmentIdsToView.get(appointmentInfo.getId()), appointmentInfo);
                                    } else { //we add the new appointment and update the layout.
                                        appointmentInfoMap.put(appointment.getId(), appointmentInfo);
                                        scrollLayout.removeAllViewsInLayout();
                                        changeCurrentCalendarLayout(new HashSet<>(appointmentInfoMap.values()));
                                    }
                                }
                            });
                        });
                    });

                });
            }

        });
    }
    protected void updateCalendarEntry(View view, CalendarAppointmentInfo info){
        ConstraintLayout calendarEntry = (ConstraintLayout)view;

        Date current = new Date(System.currentTimeMillis());
        Date startDate = new Date(info.getStartTime());
        Date endDate = new Date((info.getStartTime() + info.getDuration()));
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm", Locale.US);
        String appointmentDate = formatter.format(startDate) + " - " + formatter.format(endDate);


        ImageView status = calendarEntry.findViewById(R.id.calendarEntryStatus);
        if (current.before(startDate))
            status.setImageResource(R.drawable.calendar_entry_incoming_dot);
        else if (current.after(endDate))
            status.setImageResource(R.drawable.calendar_entry_done_dot);
        else
            status.setImageResource(R.drawable.calendar_entry_ongoing_dot);
        ((TextView) calendarEntry.findViewById(R.id.calendarEntryAppointmentDate)).setText(appointmentDate);
        ((TextView) calendarEntry.findViewById(R.id.calendarEntryAppointmentTitle)).setText(info.getTitle());
    }
}