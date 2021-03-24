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

        Bundle bundle = new Bundle();
        bundle.putSerializable(CalendarActivity.UserTypeCode,userType);
        bundle.putSerializable(CalendarEntryDetailsGeneralFragment.APPOINTMENT_DETAIL_GENERAL_ID,appointmentId);
        CalendarEntryDetailsGeneralFragment detailsGeneralFragment = new CalendarEntryDetailsGeneralFragment();

        Bundle bundle2 = new Bundle();
        bundle2.putSerializable(CalendarActivity.UserTypeCode,userType);
        bundle2.putSerializable(CalendarEntryDetailsParticipantsFragments.APPOINTMENT_DETAIL_PARTICIPANT_ID,appointmentId);
        CalendarEntryDetailsParticipantsFragments participantsFragments = new CalendarEntryDetailsParticipantsFragments();

        Bundle bundle3 = new Bundle();
        bundle3.putSerializable(CalendarActivity.UserTypeCode,userType);
        bundle3.putSerializable(CalendarEntryDetailAddBanParticipantsFragment.APPOINTMENT_DETAIL_ADD_PARTICIPANT_ID,appointmentId);
        CalendarEntryDetailAddBanParticipantsFragment manageParticipantsFragment = new CalendarEntryDetailAddBanParticipantsFragment();

        detailsGeneralFragment.setArguments(bundle);
        participantsFragments.setArguments(bundle2);
        manageParticipantsFragment.setArguments(bundle3);

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