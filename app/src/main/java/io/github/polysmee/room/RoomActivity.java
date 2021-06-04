package io.github.polysmee.room;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

import io.github.polysmee.R;
import io.github.polysmee.agora.Command;
import io.github.polysmee.appointments.AppointmentActivity;
import io.github.polysmee.calendar.googlecalendarsync.CalendarUtilities;
import io.github.polysmee.database.Appointment;
import io.github.polysmee.database.DatabaseUser;
import io.github.polysmee.database.User;
import io.github.polysmee.database.databaselisteners.valuelisteners.StringSetValueListener;
import io.github.polysmee.database.databaselisteners.valuelisteners.StringValueListener;
import io.github.polysmee.internet.connection.InternetConnection;
import io.github.polysmee.database.DatabaseAppointment;
import io.github.polysmee.login.MainUser;
import io.github.polysmee.room.fragments.RemovedDialogFragment;
import io.github.polysmee.room.fragments.RoomActivityParticipantsFragment;

/**
 * Activity representing all room related operations
 * The appointment related to this room is given in argument
 */
public class RoomActivity extends AppCompatActivity {

    private Appointment appointment;
    public final static String APPOINTMENT_KEY = "io.github.polysmee.room.RoomActivity.APPOINTMENT_KEY";
    private Context context;
    private boolean paused, left, inCall;

    //Commands to remove listeners
    private final List<Command> commandsToRemoveListeners = new ArrayList<Command>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);

        String appointmentKey = getIntent().getStringExtra(APPOINTMENT_KEY);//"-MXxN7Keu6_hMrtHsTH8";
        this.appointment = new DatabaseAppointment(appointmentKey);

        StringValueListener titleListener = this::setTitle;
        appointment.getTitleAndThen(titleListener);
        commandsToRemoveListeners.add((x,y) -> appointment.removeTitleListener(titleListener));

        left = false;
        paused = false;
        inCall = false;

        checkIfParticipant();

        ViewPager2 pager = findViewById(R.id.roomActivityPager);
        FragmentStateAdapter pagerAdapter = new RoomPagerAdapter(this, appointmentKey);

        pager.setAdapter(pagerAdapter);

        TabLayout tabs = findViewById(R.id.roomActivityTabs);
        new TabLayoutMediator(tabs, pager,
                (tab, position) -> tab.setText(getString(RoomPagerAdapter.FRAGMENT_NAME_ID[position]))).attach();
    }


    public void onDestroy() {

        Object dummyArgument = null;

        for(Command command : commandsToRemoveListeners) {
            command.execute(dummyArgument,dummyArgument);
        }

        super.onDestroy();
    }


    @Override
    public void onResume() {
        super.onResume();
        System.out.println("RESUME");
        if(paused) {
            appointment.getParticipantsId_Once_AndThen(participants -> {
                if(!participants.contains(MainUser.getMainUser().getId())) {
                    generateRemovedDialog();
                }
            });
        }


        paused = false;
    }

    /**
     * Checks if a user is a member of the appointment. If not, generates a RemovedDialogFragment
     */
    private void checkIfParticipant() {
        StringSetValueListener participantListener = p -> {
            if (!paused && !p.contains(MainUser.getMainUser().getId())) {
                generateRemovedDialog();
            }
        };
        appointment.getParticipantsIdAndThen(participantListener);
        commandsToRemoveListeners.add((x,y) -> appointment.removeParticipantsListener(participantListener));
    }

    /**
     * @see RemovedDialogFragment
     */
    private void generateRemovedDialog() {
        left = true;
        FragmentManager fragmentManager = getSupportFragmentManager();

        try {
            RoomActivityParticipantsFragment participantsFragment = (RoomActivityParticipantsFragment) fragmentManager.getFragments().get(2);
            if (participantsFragment != null)
                participantsFragment.onDestroy();
        } catch (IndexOutOfBoundsException ignored) {}

        RemovedDialogFragment newFragment = new RemovedDialogFragment();

        // The device is smaller, so show the fragment fullscreen
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        // For a little polish, specify a transition animation
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        // To make it fullscreen, use the 'content' root view as the container
        // for the fragment, which is always the root view for the activity
        transaction.add(android.R.id.content, newFragment)
                .addToBackStack(null).commit();
    }

    /**
     * Set context attribute (only used to avoid a strange crash in tests)
     * @param context new context value
     */
    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.room_menu, menu);
        MenuItem item = menu.findItem(R.id.roomMenuOffline);
        if(InternetConnection.isOn()) {
            item.setVisible(false);
        }
        InternetConnection.setCommand(((value, key) -> runOnUiThread(() -> item.setVisible(key))));
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.roomMenuInfo:
                if (!inCall) {
                    paused = true;
                    Intent intent = new Intent(this, AppointmentActivity.class);
                    intent.putExtra(AppointmentActivity.LAUNCH_MODE, AppointmentActivity.DETAIL_MODE);
                    intent.putExtra(AppointmentActivity.APPOINTMENT_ID, appointment.getId());
                    startActivity(intent);
                } else {
                    Toast toast = Toast.makeText(this, getText(R.string.inCallError), Toast.LENGTH_SHORT);
                    toast.show();
                }
                return true;
            case R.id.roomMenuLeave:
                if(!left && context != null && !inCall) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage(R.string.leave_message);
                    builder.setPositiveButton(R.string.leave, (dialog, which) -> removeUserFromAppointment());

                    builder.setNegativeButton(R.string.genericCancelText, (dialog, which) -> {});
                    builder.show();
                } else if (inCall) {
                    Toast toast = Toast.makeText(this, getText(R.string.inCallError), Toast.LENGTH_SHORT);
                    toast.show();
                }

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void setInCall(boolean inCall) {
        this.inCall = inCall;
    }

    /**
     * Remove the current user from the current appointment.
     * If the user is the last participant, the appointment is removed.
     * If the user is the owner, the ownership is transferred to another participant.
     */
    private void removeUserFromAppointment() {
        appointment.getParticipantsId_Once_AndThen(participants -> {
            if(participants.size() <= 1) {
                for (String partId : participants) {
                    removeAppointmentFromCalendar(new DatabaseUser(partId));
                }
                appointment.selfDestroy();
            } else {
                appointment.removeParticipant(MainUser.getMainUser());
                MainUser.getMainUser().removeAppointment(appointment);
                removeAppointmentFromCalendar(MainUser.getMainUser());
                appointment.getOwnerId_Once_AndThen(owner -> {
                    if(owner.equals(MainUser.getMainUser().getId())) {
                        for(String uid : participants) {
                            if(!uid.equals(owner)) {
                                appointment.setOwner(new DatabaseUser(uid));
                                break;
                            }
                        }

                    }
                });
            }
        });
    }

    /**
     * Remove the appointment from the calendar of the specified user if the calendar exist
     * @param user user to remove appointment from
     */
    private void removeAppointmentFromCalendar(User user) {
        user.getAppointmentEventId_Once_AndThen(appointment, eventId -> {
            if (eventId != null && !eventId.equals(""))
                user.getCalendarId_Once_AndThen(calendarId ->
                        CalendarUtilities.deleteAppointmentFromCalendar(getApplicationContext(), calendarId,
                                eventId, ()->{}, () -> runOnUiThread( () ->{
                                    Toast toast = Toast.makeText(getApplicationContext(), getText(R.string.genericErrorText), Toast.LENGTH_SHORT);
                                    toast.show();
                                })
                        )
                );
        });
    }
}