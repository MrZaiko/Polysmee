package io.github.polysmee.room;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import io.github.polysmee.R;
import io.github.polysmee.appointments.AppointmentActivity;
import io.github.polysmee.database.DatabaseAppointment;
import io.github.polysmee.database.Appointment;
import io.github.polysmee.login.MainUser;
import io.github.polysmee.room.fragments.RemovedDialogFragment;
import io.github.polysmee.room.fragments.VoiceTunerChoiceDialogFragment;

/**
 * Activity representing all room related operations
 * The appointment related to this room is given in argument
 */
public class RoomActivity extends AppCompatActivity implements VoiceTunerChoiceDialogFragment.VoiceTunerChoiceDialogFragmentListener{

    private Appointment appointment;
    private RoomPagerAdapter roomPagerAdapter;
    public final static String APPOINTMENT_KEY = "io.github.polysmee.room.RoomActivity.APPOINTMENT_KEY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);

        String appointmentKey = getIntent().getStringExtra(APPOINTMENT_KEY);//"-MXxN7Keu6_hMrtHsTH8";
        this.appointment = new DatabaseAppointment(appointmentKey);

        appointment.getTitleAndThen(this::setTitle);
        checkIfParticipant();

        ViewPager2 pager = findViewById(R.id.roomActivityPager);
        roomPagerAdapter = new RoomPagerAdapter(this, appointmentKey);

        pager.setAdapter(roomPagerAdapter);

        TabLayout tabs = findViewById(R.id.roomActivityTabs);
        new TabLayoutMediator(tabs, pager,
                (tab, position) -> tab.setText(getString(RoomPagerAdapter.FRAGMENT_NAME_ID[position]))).attach();
    }

    private void checkIfParticipant() {
        appointment.getParticipantsIdAndThen(p -> {
            if (!p.contains(MainUser.getMainUser().getId())) {
                generateRemovedDialog();
            }
        });
    }

    private void generateRemovedDialog() {
        FragmentManager fragmentManager = getSupportFragmentManager();
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.room_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.roomMenuInfo:
                Intent intent = new Intent(this, AppointmentActivity.class);
                intent.putExtra(AppointmentActivity.LAUNCH_MODE, AppointmentActivity.DETAIL_MODE);
                intent.putExtra(AppointmentActivity.APPOINTMENT_ID, appointment.getId());
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onDialogChoiceSingleChoiceItems(int elementIndex) {
        assert roomPagerAdapter !=null;
        roomPagerAdapter.setCallAudioEffect(elementIndex);
    }
}