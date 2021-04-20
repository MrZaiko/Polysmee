package io.github.polysmee.invites;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import io.github.polysmee.R;
import io.github.polysmee.appointments.AppointmentActivity;
import io.github.polysmee.calendar.CalendarAppointmentInfo;
import io.github.polysmee.calendar.DailyCalendar;
import io.github.polysmee.database.Appointment;
import io.github.polysmee.database.DatabaseAppointment;
import io.github.polysmee.database.User;
import io.github.polysmee.database.databaselisteners.StringSetValueListener;
import io.github.polysmee.login.MainUserSingleton;

public class InvitesManagementActivity extends AppCompatActivity {

    private StringSetValueListener userInvitesListener;

    private LinearLayout scrollLayout;
    private LayoutInflater inflater;

    private User user;

    private final Map<String, View> appointmentIdsToView = new HashMap<>();
    private final Set<String> appointmentSet = new HashSet<>();
    private final Map<String, CalendarAppointmentInfo> appointmentInfoMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invites_management);
        inflater = getLayoutInflater();

        scrollLayout = findViewById(R.id.InvitesManagementScrollLayout);
        user = MainUserSingleton.getInstance();

        setListenerUserAppointments();
    }

    /**
     * Creates an appointment's textual description following a certain format
     * to show in the calendar
     *
     * @param appointment the appointment's whose description is created
     * @return the textual representation of the appointment in the calendar
     */
    protected void makeInviteEntry(CalendarAppointmentInfo appointment, View InviteEntry) {
        ((TextView) InviteEntry.findViewById(R.id.InvitationEntryAppointmentTitle)).setText(appointment.getTitle());

        Date startDate = new Date(appointment.getStartTime());
        Date endDate = new Date((appointment.getStartTime() + appointment.getDuration()));
        Date current = new Date(System.currentTimeMillis());

        InviteEntry.setOnClickListener(v -> goToAppointmentDetails(appointment.getId()));

        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm", Locale.US);
        String appointmentDate = formatter.format(startDate) + " - " + formatter.format(endDate);
        ((TextView) InviteEntry.findViewById(R.id.InvitationEntryAppointmentDate)).setText(appointmentDate);

        ImageView status = InviteEntry.findViewById(R.id.InvitationEntryStatus);
        if (current.before(startDate))
            status.setImageResource(R.drawable.calendar_entry_incoming_dot);
        else if (current.after(endDate))
            status.setImageResource(R.drawable.calendar_entry_done_dot);
        else
            status.setImageResource(R.drawable.calendar_entry_ongoing_dot);
    }

    public void goToAppointmentDetails(String id) {
        Intent intent = new Intent(this, AppointmentActivity.class);
        intent.putExtra(AppointmentActivity.LAUNCH_MODE, AppointmentActivity.DETAIL_MODE);
        intent.putExtra(AppointmentActivity.APPOINTMENT_ID, id);
        startActivity(intent);
    }

    /**
     * Adds an appointment to the calendar layout, as a calendar entry
     *
     * @param appointment the appointment to add
     */
    protected void addAppointmentToInviteLayout(CalendarAppointmentInfo appointment) {
        ConstraintLayout appointmentEntryLayout = (ConstraintLayout) inflater.inflate(R.layout.element_invitation_entry, null);
        makeInviteEntry(appointment, appointmentEntryLayout);
        TextView emptySpace = new TextView(this);

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
        if (userInvitesListener != null) {
            user.removeAppointmentsListener(userInvitesListener);
        }

        userInvitesListener = currentUserInvitesListener();
        user.getAppointmentsAndThen(userInvitesListener);
    }

    /**
     * Changes the calendar's layout to show the user's daily appointments at the time
     * this method is called.
     */
    protected void changeCurrentInvitesLayout(Set<CalendarAppointmentInfo> invites) {
        if (!invites.isEmpty()) {
            for (CalendarAppointmentInfo appointment : invites) {
                addAppointmentToInviteLayout(appointment);
            }
        }
    }

    protected StringSetValueListener currentUserInvitesListener() {

        StringSetValueListener userAppointmentsListener = setOfIds -> {
            Set<String> deletedAppointments = new HashSet<>(appointmentSet);
            Set<String> newAppointments = new HashSet<>(setOfIds);

            //two loops: one for the appointments that are gone, and another for the new appointments

            deletedAppointments.removeAll(newAppointments); //keep the deleted appointments
            newAppointments.removeAll(appointmentSet); //keep the new appointments

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
                changeCurrentInvitesLayout(new HashSet<>(appointmentInfoMap.values()));
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
                            if (!appointmentSet.contains(id)) { //the appointment was removed; we thus have to remove it from the displayed appointments
                                appointmentInfoMap.remove(id);
                                if (appointmentIdsToView.containsKey(id)) {
                                    scrollLayout.removeView(appointmentIdsToView.get(id));
                                    scrollLayout.removeView(appointmentIdsToView.get(id + 1));
                                    appointmentIdsToView.remove(id);
                                    appointmentIdsToView.remove(id + 1);
                                }
                            } else {
                                appointmentInfoMap.put(appointment.getId(), appointmentInfo);
                                if (appointmentIdsToView.containsKey(appointmentInfo.getId())) { //the view is already there, we just need to update it
                                    makeInviteEntry(appointmentInfo,appointmentIdsToView.get(appointmentInfo.getId()));
                                } else { //we add the new appointment and update the layout
                                    scrollLayout.removeAllViewsInLayout();
                                    changeCurrentInvitesLayout(new HashSet<>(appointmentInfoMap.values()));
                                }
                            }

                        });
                    });

                });
            }
        };

        return userAppointmentsListener;
    }
}