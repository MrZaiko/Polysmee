package io.github.polysmee.room;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.PreferenceManager;
import androidx.viewpager2.adapter.FragmentStateAdapter;
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
import io.github.polysmee.login.MainUserSingleton;
import io.github.polysmee.room.fragments.RemovedDialogFragment;

/**
 * Activity representing all room related operations
 * The appointment related to this room is given in argument
 */
public class RoomActivity extends AppCompatActivity {

    private Appointment appointment;
    public static String APPOINTMENT_KEY = "io.github.polysmee.room.RoomActivity.APPOINTMENT_KEY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);

        String appointmentKey = getIntent().getStringExtra(APPOINTMENT_KEY);//"-MXxN7Keu6_hMrtHsTH8";
        this.appointment = new DatabaseAppointment(appointmentKey);

        appointment.getTitleAndThen(this::setTitle);
        checkIfParticipant();

        ViewPager2 pager = findViewById(R.id.roomActivityPager);
        FragmentStateAdapter pagerAdapter = new RoomPagerAdapter(this, appointmentKey);

        pager.setAdapter(pagerAdapter);

        TabLayout tabs = findViewById(R.id.roomActivityTabs);
        new TabLayoutMediator(tabs, pager,
                (tab, position) -> tab.setText(RoomPagerAdapter.FRAGMENT_NAME[position])).attach();
        //put the current voice tune to default value 0 which correspond to the index of no tune voice in the array used for the spinner used
        PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putInt(getResources().getString(R.string.preference_key_voice_tuner_current_voice_tune),0).apply();
    }

    private void checkIfParticipant() {
        appointment.getParticipantsIdAndThen(p -> {
            if (!p.contains(MainUserSingleton.getInstance().getId())) {
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




}