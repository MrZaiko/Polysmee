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
import io.github.polysmee.calendar.detailsFragments.CalendarEntryDetailAddBanParticipantsFragment;
import io.github.polysmee.calendar.detailsFragments.CalendarEntryDetailsGeneralFragment;
import io.github.polysmee.calendar.detailsFragments.CalendarEntryDetailsParticipantsFragments;
import io.github.polysmee.database.DatabaseAppointment;
import io.github.polysmee.database.decoys.FakeDatabaseAppointment;
import io.github.polysmee.interfaces.Appointment;

public class CalendarEntryDetailsActivity extends AppCompatActivity {

    private String appointmentId;
    private Appointment appointment;
    private String userType;
    //Only for the fake user
    public final static String APPOINTMENT_DETAIL_CALENDAR_MODIFY_TITLE  = "APPOINTMENT_MODIFY_TITLE";
    public final static String APPOINTMENT_DETAIL_CALENDAR_MODIFY_COURSE = "APPOINTMENT_MODIFY_COURSE";
    public static final String APPOINTMENT_DETAIL_CALENDAR_ID_TO = "APPOINTMENT_DETAIL_CALENDAR_ID_TO";

    protected Bundle createBundle(String key){
        Bundle bundle = new Bundle();
        bundle.putSerializable(CalendarActivity.UserTypeCode,userType);
        bundle.putSerializable(key,appointmentId);
        return bundle;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_entry_details);

        appointmentId = (String)getIntent().getSerializableExtra(CalendarActivity.APPOINTMENT_DETAIL_CALENDAR_ID_FROM);
        userType = getIntent().getStringExtra(CalendarActivity.UserTypeCode);
        if(userType.equals("Real"))
            appointment = new DatabaseAppointment(appointmentId);
        else
            appointment = new FakeDatabaseAppointment(appointmentId);
        List<Fragment> list = new ArrayList<>();

        CalendarEntryDetailsGeneralFragment detailsGeneralFragment = new CalendarEntryDetailsGeneralFragment();

        CalendarEntryDetailsParticipantsFragments participantsFragments = new CalendarEntryDetailsParticipantsFragments();

       CalendarEntryDetailAddBanParticipantsFragment manageParticipantsFragment = new CalendarEntryDetailAddBanParticipantsFragment();

        detailsGeneralFragment.setArguments(createBundle(CalendarEntryDetailsGeneralFragment.APPOINTMENT_DETAIL_GENERAL_ID));
        participantsFragments.setArguments(createBundle(CalendarEntryDetailsParticipantsFragments.APPOINTMENT_DETAIL_PARTICIPANT_ID));
        manageParticipantsFragment.setArguments(createBundle(CalendarEntryDetailAddBanParticipantsFragment.APPOINTMENT_DETAIL_ADD_PARTICIPANT_ID));

        list.add(detailsGeneralFragment);
        list.add(participantsFragments);
        list.add(manageParticipantsFragment);

        ViewPager2 pager = findViewById(R.id.calendarEntryDetailActivityPager);
        FragmentStateAdapter pagerAdapter = new CalendarDetailPagerAdapter(this, list);

        Button button = (Button)findViewById(R.id.calendarEntryDetailActivityDoneModifyButton);
        appointment.getOwnerIdAndThen((id)->{
                button.setOnClickListener((v)->{
                    ((CalendarEntryDetailsGeneralFragment)list.get(0)).doneModifying();
                    onBackPressed();
                });
        });

        pager.setAdapter(pagerAdapter);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        if(!userType.equals("Real")){
            appointment.getTitleAndThen((title) ->{
                intent.putExtra(APPOINTMENT_DETAIL_CALENDAR_MODIFY_TITLE, title);
            });
            appointment.getCourseAndThen((course) ->{
                intent.putExtra(APPOINTMENT_DETAIL_CALENDAR_MODIFY_COURSE, course);
            });
            intent.putExtra(APPOINTMENT_DETAIL_CALENDAR_ID_TO,appointmentId);
        }
        setResult(Activity.RESULT_OK,intent);
        finish();
    }
}