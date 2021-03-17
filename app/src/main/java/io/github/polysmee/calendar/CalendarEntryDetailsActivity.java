package io.github.polysmee.calendar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import java.util.ArrayList;
import java.util.List;

import io.github.polysmee.R;
import io.github.polysmee.calendar.detailsFragments.calendarEntryDetailsGeneralFragment;
import io.github.polysmee.calendar.detailsFragments.calendarEntryDetailsParticipantsFragments;
import io.github.polysmee.database.DatabaseAppointment;
import io.github.polysmee.login.MainUserSingleton;
import io.github.polysmee.room.RoomPagerAdapter;

public class CalendarEntryDetailsActivity extends AppCompatActivity {

    private String appointmentId;
    private DatabaseAppointment appointment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_entry_details);

        appointmentId = (String)getIntent().getSerializableExtra(CalendarActivity.APPOINTMENT_DETAIL_CALENDAR);
        appointment = new DatabaseAppointment(appointmentId);
        List<Fragment> list = new ArrayList<>();
        list.add(new calendarEntryDetailsGeneralFragment(appointmentId));
        //list.add(new calendarEntryDetailsParticipantsFragments(appointmentId));

        ViewPager2 pager = findViewById(R.id.calendarEntryDetailActivityPager);
        FragmentStateAdapter pagerAdapter = new CalendarDetailPagerAdapter(this, list);

        Button button = (Button)findViewById(R.id.calendarEntryDetailActivityDoneModifyButton);
        appointment.getOwnerIdAndThen((id)->{
            if(MainUserSingleton.getInstance().getId().equals(id)){
                button.setOnClickListener((v)->{
                    ((calendarEntryDetailsGeneralFragment)list.get(0)).doneModifying();
                    onBackPressed();
                });
            }
        });

        pager.setAdapter(pagerAdapter);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this,CalendarActivity.class);
        setResult(Activity.RESULT_OK,intent);
        finish();
    }
}