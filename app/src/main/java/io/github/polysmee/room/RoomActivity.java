package io.github.polysmee.room;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import io.github.polysmee.R;
import io.github.polysmee.database.DatabaseAppointment;
import io.github.polysmee.interfaces.Appointment;
import io.github.polysmee.room.fragments.roomActivityMessagesFragment;
import io.github.polysmee.room.fragments.roomActivityParticipantsFragment;

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

        String appointmentKey = getIntent().getStringExtra(APPOINTMENT_KEY);
        this.appointment = new DatabaseAppointment(appointmentKey);

        appointment.getTitleAndThen(this::setTitle);


        //Fragment Creation
        List<Fragment> list = new ArrayList<>();
        list.add(new roomActivityMessagesFragment());
        //list.add(new roomActivityParticipantsFragment(appointment));


        ViewPager2 pager = findViewById(R.id.roomActivityPager);
        FragmentStateAdapter pagerAdapter = new RoomPagerAdapter(this, list);

        pager.setAdapter(pagerAdapter);

        TabLayout tabs = findViewById(R.id.roomActivityTabs);
        new TabLayoutMediator(tabs, pager,
                (tab, position) -> tab.setText(list.get(position).toString())).attach();
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
                Intent intent = new Intent(this, RoomActivityInfo.class);
                intent.putExtra(RoomActivityInfo.APPOINTMENT_KEY, appointment.getId());
                startActivityForResult(intent, RESULT_OK);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }




}