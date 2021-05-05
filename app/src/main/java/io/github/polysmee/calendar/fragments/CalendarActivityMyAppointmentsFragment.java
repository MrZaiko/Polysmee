package io.github.polysmee.calendar.fragments;

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
import io.github.polysmee.database.Appointment;
import io.github.polysmee.database.DatabaseAppointment;
import io.github.polysmee.database.User;
import io.github.polysmee.database.databaselisteners.StringSetValueListener;
import io.github.polysmee.login.MainUser;
import io.github.polysmee.room.RoomActivity;

import static io.github.polysmee.calendar.fragments.CalendarActivityFragmentsHelpers.*;

public class CalendarActivityMyAppointmentsFragment extends Fragment {


    private StringSetValueListener userAppointmentsListener;
    private ViewGroup rootView;

    private LinearLayout scrollLayout;
    private LayoutInflater inflater;

    private User user;
    private final AtomicInteger childrenCounters = new AtomicInteger(0);

    private final Map<String, View> appointmentIdsToView = new HashMap<>();
    private final Set<String> appointmentSet = new HashSet<>();
    private final Map<String, CalendarAppointmentInfo> appointmentInfoMap = new HashMap<>();


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = (ViewGroup) inflater.inflate(R.layout.fragment_calendar_activity_my_appointments, container, false);
        scrollLayout = (LinearLayout) rootView.findViewById(R.id.calendarActivityMyAppointmentsScrollLayout);
        this.inflater = inflater;
        setTodayDateInDailyCalendar(false);
        setDayText(rootView, false);
        user = MainUser.getMainUser();
        userAppointmentsListener = null;
        rootView.findViewById(R.id.calendarActivityCreateAppointmentButton).setOnClickListener((v) -> createAppointment());

        rootView.findViewById(R.id.todayDateMyAppointmentsCalendarActivity).setOnClickListener((v) -> {
            chooseDate();
        });

        setListenerUserAppointments();
        return rootView;
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
            setDayText(rootView, false);
            scrollLayout.removeAllViewsInLayout();
            setListenerUserAppointments();
        }, calendarChosenDay.get(Calendar.YEAR), calendarChosenDay.get(Calendar.MONTH), calendarChosenDay.get(Calendar.DATE)).show();

    }

    @Override
    public void onResume() {
        super.onResume();
        setListenerUserAppointments();
    }

    /*
     * Behavior of the create appointment button, depending if the user is real or fake
     */
    private void createAppointment() {
        Intent intent = new Intent(rootView.getContext(), AppointmentActivity.class);
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
     */
    protected void createAppointmentEntry(CalendarAppointmentInfo appointment, View calendarEntry) {
        ((TextView) calendarEntry.findViewById(R.id.calendarEntryAppointmentTitle)).setText(appointment.getTitle());

        Date startDate = new Date(appointment.getStartTime());
        Date endDate = new Date((appointment.getStartTime() + appointment.getDuration()));
        Date current = new Date(System.currentTimeMillis());

        if (current.before(startDate))
            calendarEntry.setOnClickListener(v -> goToAppointmentDetails(appointment.getId(), this, rootView));
        else
            calendarEntry.setOnClickListener((v) -> launchRoomActivityWhenClickingOnDescription(appointment.getId()));

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
        TextView emptySpace = new TextView(rootView.getContext());

        scrollLayout.addView(appointmentEntryLayout);
        scrollLayout.addView(emptySpace);
        appointmentIdsToView.put(appointment.getId(), appointmentEntryLayout);
        appointmentIdsToView.put(appointment.getId() + 1, emptySpace);
    }

    /**
     * Adds a listener to the user's appointments so that everytime one is added/removed, the layout
     * is updated. It also takes care of determining what should happen to the calendar's layout
     * if an appointment's parameters changes.
     */

    protected void setListenerUserAppointments() {
        if (userAppointmentsListener != null) {
            user.removeAppointmentsListener(userAppointmentsListener);
        }

        userAppointmentsListener = currentDayUserAppointmentsListener();
        user.getAppointmentsAndThen(userAppointmentsListener);
    }

    protected StringSetValueListener currentDayUserAppointmentsListener() {

        StringSetValueListener userAppointmentsListener = new StringSetValueListener() {
            @Override
            public void onDone(Set<String> setOfIds) {
                Set<String> deletedAppointments = new HashSet<>(appointmentSet);
                Set<String> newAppointments = new HashSet<>(setOfIds);

                //two loops: one for the appointments that are gone, and another for the new appointments

                deletedAppointments.removeAll(newAppointments); //keep the deleted appointments
                newAppointments.removeAll(appointmentSet); //keep the new appointmnets

                for (String oldAppointmentId : deletedAppointments) { //delete all old appointments
                    appointmentSet.remove(oldAppointmentId);
                    appointmentInfoMap.remove(oldAppointmentId);
                    if (appointmentIdsToView.containsKey(oldAppointmentId)) {
                        scrollLayout.removeView(appointmentIdsToView.get(oldAppointmentId));
                        scrollLayout.removeView(appointmentIdsToView.get(oldAppointmentId + 1));
                    }
                }


                appointmentSet.addAll(newAppointments); //add all new appointments
                if (newAppointments.isEmpty()) {
                    scrollLayout.removeAllViewsInLayout();
                    changeCurrentCalendarLayout(new HashSet<>(appointmentInfoMap.values()));
                    return;
                }
                for (String id : newAppointments) { //iterate only on the new appointments, to set their listener
                    Appointment appointment = new DatabaseAppointment(id);
                    CalendarAppointmentInfo appointmentInfo = new CalendarAppointmentInfo("", "", 0, 0, id);
                    appointment.getStartTimeAndThen((start) -> {
                        appointmentInfo.setStartTime(start);
                        appointment.getDurationAndThen((duration) -> {
                            appointmentInfo.setDuration(duration);
                            appointment.getTitleAndThen((title) -> {
                                appointmentInfo.setTitle((title));
                                if (!appointmentSet.contains(appointmentInfo.getId())) { //the appointment was removed; we thus have to remove it from the displayed appointments
                                    appointmentInfoMap.remove(appointmentInfo.getId());
                                    if (appointmentIdsToView.containsKey(id)) {
                                        scrollLayout.removeView(appointmentIdsToView.get(id));
                                        scrollLayout.removeView(appointmentIdsToView.get(id + 1));
                                        appointmentIdsToView.remove(id);
                                        appointmentIdsToView.remove(id + 1);
                                    }
                                } else {
                                    appointmentInfoMap.put(appointment.getId(), appointmentInfo);
                                    if (appointmentIdsToView.containsKey(appointmentInfo.getId())) { //the view is already there, we just need to update it.
                                        createAppointmentEntry(appointmentInfo,appointmentIdsToView.get(appointmentInfo.getId()));
                                    } else { //we add the new appointment and update the layout.
                                        scrollLayout.removeAllViewsInLayout();
                                        changeCurrentCalendarLayout(new HashSet<>(appointmentInfoMap.values()));
                                    }
                                }

                            });
                        });

                    });
                }
            }
        };

        return userAppointmentsListener;
    }


}