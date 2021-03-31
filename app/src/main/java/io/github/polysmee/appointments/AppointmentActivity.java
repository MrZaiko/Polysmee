package io.github.polysmee.appointments;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.Calendar;

import io.github.polysmee.R;
import io.github.polysmee.appointments.fragments.DataPasser;
import io.github.polysmee.database.DatabaseAppointment;
import io.github.polysmee.database.DatabaseUser;
import io.github.polysmee.interfaces.Appointment;
import io.github.polysmee.interfaces.User;
import io.github.polysmee.login.MainUserSingleton;

public class AppointmentActivity extends AppCompatActivity implements DataPasser {

    public static final String CALENDAR_START = "CALENDAR_START";
    public static final String CALENDAR_END = "CALENDAR_END";
    public static final String TITLE = "TITLE";
    public static final String COURSE = "COURSE";
    public static final String INVITES = "INVITES";
    public static final String BANS = "BANS";
    public static final String PRIVATE = "PRIVATE";
    public static final String DONE = "DONE";

    private Calendar calendarStart;
    private Calendar calendarEnd;
    private String title;
    private String course;
    private ArrayList<String> invites;
    private ArrayList<String> bans;
    private boolean isPrivate;
    private boolean done;

    private User user;

    public static final String ERROR_TXT = "Error : Start and end time must result in a correct time slot";
    private EditText editTitle, editCourse;
    private Button btnDone, btnReset;
    private Calendar calendarStartTime, calendarEndTime;
    private TextView txtError, txtStartTime, txtEndTime;
    private SwitchCompat privateSelector;
    private ImageView showAddBan;
    private boolean isAddBanShown;

    DataPasser dataPasser;

    //A calendar is a wait to get time using year/month... and allows to transform it to epoch time
    private Calendar date;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_creation);

        //store all objects on the activity (buttons, textViews...) in variables
        attributeSetters();

        txtError.setVisibility(View.INVISIBLE);

        findViewById(R.id.appointmentCreationStartTimeLayout).setOnClickListener(v -> {
            showDateTimePicker(txtStartTime, true);
        });

        findViewById(R.id.appointmentCreationEndTimeLayout).setOnClickListener(v -> {
            showDateTimePicker(txtEndTime, false);
        });


        privateSelector.setOnCheckedChangeListener((l,newValue) -> isPrivate = newValue);

        //ADD - BAN Section
        ViewPager2 pager = findViewById(R.id.appointmentCreationAddPager);
        FragmentStateAdapter pagerAdapter = new AptCreationPagerAdapter(this);
        pager.setAdapter(pagerAdapter);
        pager.setVisibility(View.GONE);

        TabLayout tabs = findViewById(R.id.appointmentCreationAddTabLayout);
        new TabLayoutMediator(tabs, pager,
                (tab, position) -> tab.setText(AptCreationPagerAdapter.FRAGMENT_NAME[position])).attach();
        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (!isAddBanShown)
                    showAddBan.performClick();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                if (!isAddBanShown)
                    showAddBan.performClick();
            }
        });

        showAddBan.setOnClickListener(s -> {
            if (isAddBanShown) {
                pager.setVisibility(View.GONE);
                showAddBan.setImageResource(R.drawable.baseline_arrow_right);
            } else {
                pager.setVisibility(View.VISIBLE);
                showAddBan.setImageResource(R.drawable.baseline_arrow_drop_down);
            }
            isAddBanShown = !isAddBanShown;
        });

        btnDone.setOnClickListener(doneClickListener);

        btnReset.setOnClickListener(resetClickListener);
    }

    //Function which first displays a DatePicker then a TimePicker and stores all the information in Calendar date
    private void showDateTimePicker(TextView textView, boolean isStart) {
        final Calendar currentDate = Calendar.getInstance();
        date = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, monthOfYear, dayOfMonth) -> {
            date.set(year, monthOfYear, dayOfMonth);
            new TimePickerDialog(this, (view1, hourOfDay, minute) -> {
                date.set(Calendar.HOUR_OF_DAY, hourOfDay);
                date.set(Calendar.MINUTE, minute);
                date.set(Calendar.SECOND, 0);
                date.set(Calendar.MILLISECOND, 0);
                updateCalendar(textView, isStart);
            }, currentDate.get(Calendar.HOUR_OF_DAY), currentDate.get(Calendar.MINUTE), true).show();
        }, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DATE)).show();
    }

    //only to be used by function showDateTimePicker
    private void updateCalendar(TextView textView, boolean isStart) {
        //Update start or end time with user input
        if(isStart) {
            calendarStartTime = (Calendar) date.clone();
        } else {
            calendarEndTime = (Calendar) date.clone();
        }
        //If there was an error setting a new start or end time can fix it so remove the error message as the user knows they need to fix it
        txtError.setVisibility(View.INVISIBLE);
        //Display the time on screen so that the user can know their input has been taken
        String strTime = date.get(Calendar.DAY_OF_MONTH) + "/" + String.format("%02d", date.get(Calendar.MONTH) + 1) + "/" + date.get(Calendar.YEAR) + "   -   " + String.format("%02d", date.get(Calendar.HOUR_OF_DAY)) + ":" + String.format("%02d", date.get(Calendar.MINUTE));
        textView.setText(strTime);
    }


    View.OnClickListener doneClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //if startTime is bigger than endTime we have a negative duration which doesn't work
            //It isn't possible to create an appointment scheduled before the current time
            Calendar c = Calendar.getInstance();
            c.set(Calendar.MONTH, c.get(Calendar.MONTH) - 1);
            if (calendarStartTime.getTimeInMillis() >= calendarEndTime.getTimeInMillis() || calendarStartTime.getTimeInMillis() <= c.getTimeInMillis()){
                txtError.setVisibility(View.VISIBLE);
                txtError.setText(ERROR_TXT);
            } else {
                title = editTitle.getText().toString();
                course = editCourse.getText().toString();
                sendData();
                finish();
            }
        }
    };

    private void sendData() {
        String aptID = user.createNewUserAppointment(calendarStart.getTimeInMillis(), calendarEnd.getTimeInMillis() - calendarStart.getTimeInMillis(), course, title);
        Appointment appointment = new DatabaseAppointment(aptID);
        User.getAllUsersIdsAndThenOnce((setOfUserIds) -> {
            for(String userId : setOfUserIds){
                User user = new DatabaseUser(userId);
                for(String inviteName : invites) {
                    user.getNameAndThen((name) -> {
                        if (name.equals(inviteName)) {
                            user.addAppointment(appointment);
                            appointment.addParticipant(user);
                        }
                    });
                }
            }
        });
    }

    View.OnClickListener resetClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //reset every input text field and both start and end times
            editTitle.setText("");
            editCourse.setText("");
            txtError.setText("");
            calendarEndTime = Calendar.getInstance();
            txtEndTime.setText(getString(R.string.appointment_creation_pick_end_time));
            calendarStartTime = Calendar.getInstance();
            txtStartTime.setText(getString(R.string.appointment_creation_pick_start_time));
        }
    };

    private void attributeSetters() {
        calendarStart = Calendar.getInstance();
        calendarEnd = Calendar.getInstance();
        title = "";
        course = "";
        invites = new ArrayList<>();
        bans = new ArrayList<>();
        isAddBanShown = false;
        isPrivate = false;
        done = false;

        //We need to know who is trying to create an appointment as they are the owner
        user = MainUserSingleton.getInstance();

        privateSelector = findViewById(R.id.appointmentCreationPrivateSelector);
        showAddBan = findViewById(R.id.appointmentCreationShowAddBan);
        editTitle = findViewById(R.id.appointmentCreationEditTxtAppointmentTitleSet);
        editCourse = findViewById(R.id.appointmentCreationEditTxtAppointmentCourseSet);
        btnDone = findViewById(R.id.appointmentCreationbtnDone);
        btnReset = findViewById(R.id.appointementCreationBtnReset);
        txtError = findViewById(R.id.appointmentCreationtxtError);
        txtStartTime = findViewById(R.id.appointmentCreationStartTime);
        txtEndTime = findViewById(R.id.appointmentCreationEndTime);
        calendarStartTime = Calendar.getInstance();
        calendarEndTime = Calendar.getInstance();
    }

    @Override
    public void dataPass(boolean data, String id) {
        if(id.equals(PRIVATE)) {
            isPrivate = data;
        }
        if(id.equals(DONE)) {
            done = data;
        }
    }

    @Override
    public void dataPass(ArrayList<String> data, String id) {
        if(id.equals(INVITES)) {
            invites = new ArrayList<>(data);
        }
        if(id.equals(BANS)) {
            bans = new ArrayList<>(data);
        }
    }

    @Override
    public void dataPass(String data, String id) {
        if(id.equals(TITLE)) {
            title = data;
        }
        if(id.equals(COURSE)) {
            course = data;
        }
    }

    @Override
    public void dataPass(Calendar data, String id) {
        if(id.equals(CALENDAR_START)) {
            calendarStart = data;
        }
        if(id.equals(CALENDAR_END)) {
            calendarEnd = data;
        }
    }

}