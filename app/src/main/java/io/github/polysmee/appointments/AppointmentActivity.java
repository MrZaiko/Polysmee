package io.github.polysmee.appointments;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_creation);

        ViewPager2 pager = findViewById(R.id.aptCreationActivityPager);
        FragmentStateAdapter pagerAdapter = new AptCreationPagerAdapter(this);

        pager.setAdapter(pagerAdapter);

        TabLayout tabs = findViewById(R.id.aptCreationActivityTabs);
        new TabLayoutMediator(tabs, pager,
                (tab, position) -> tab.setText(AptCreationPagerAdapter.FRAGMENT_NAME[position])).attach();

        calendarStart = Calendar.getInstance();
        calendarEnd = Calendar.getInstance();
        title = "";
        course = "";
        invites = new ArrayList<>();
        bans = new ArrayList<>();
        isPrivate = false;
        done = false;

        //We need to know who is trying to create an appointment as they are the owner
        user = MainUserSingleton.getInstance();
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

    @Override
    protected void onDestroy() {
        if(done) {
            //create Appointment according to user input and return to the activity which called this one
            //bans and isPrivate not supported yet, will add them to the appointment when they are
            String aptID = user.createNewUserAppointment(calendarStart.getTimeInMillis()/1000, calendarEnd.getTimeInMillis()/1000 - calendarStart.getTimeInMillis()/1000, course, title);
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


        super.onDestroy();
    }
}