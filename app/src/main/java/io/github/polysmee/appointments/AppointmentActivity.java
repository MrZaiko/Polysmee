package io.github.polysmee.appointments;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.Calendar;

import io.github.polysmee.R;
import io.github.polysmee.appointments.fragments.AppointmentCreationAddUserFragment;
import io.github.polysmee.appointments.fragments.DataPasser;
import io.github.polysmee.database.DatabaseAppointment;
import io.github.polysmee.database.DatabaseUser;
import io.github.polysmee.interfaces.Appointment;
import io.github.polysmee.interfaces.User;
import io.github.polysmee.login.MainUserSingleton;

import static android.content.ContentValues.TAG;

public class AppointmentActivity extends AppCompatActivity implements DataPasser {

    public static final String INVITES = "INVITES";
    public static final String BANS = "BANS";

    private Calendar calendarStart;
    private Calendar calendarEnd;
    private String title;
    private String course;
    private ArrayList<String> invites;
    private ArrayList<String> bans;
    private boolean isPrivate;
    boolean isKeyboardShowing = false;

    private User user;

    public static final String ERROR_TXT = "Error : Start and end time must result in a correct time slot";
    private EditText editTitle, editCourse;
    private Button btnDone, btnReset;
    private Calendar calendarStartTime, calendarEndTime;
    private TextView txtError, txtStartTime, txtEndTime, txtAdd, txtBan;
    private SwitchCompat privateSelector;
    private ImageView showAdd, showBan;
    private View addFragment, banFragment, bottomBar;
    private boolean isAddShown, isBanShown;

    //A calendar is a wait to get time using year/month... and allows to transform it to epoch time
    private Calendar date;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_creation);

        setupKeyboardDetection();

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

        addBanSetup();

        btnDone.setOnClickListener(doneClickListener);
        btnReset.setOnClickListener(resetClickListener);
    }

    private void setupKeyboardDetection() {
        final ViewGroup contentView = (ViewGroup) ((ViewGroup) this
                .findViewById(android.R.id.content)).getChildAt(0);

        // ContentView is the root view of the layout of this activity/fragment
        contentView.getViewTreeObserver().addOnGlobalLayoutListener(() -> {

                    Rect r = new Rect();
                    contentView.getWindowVisibleDisplayFrame(r);
                    int screenHeight = contentView.getRootView().getHeight();

                    // r.bottom is the position above soft keypad or device button.
                    // if keypad is shown, the r.bottom is smaller than that before.
                    int keypadHeight = screenHeight - r.bottom;

                    if (keypadHeight > screenHeight * 0.15) { // 0.15 ratio is perhaps enough to determine keypad height.
                        // keyboard is opened
                        if (!isKeyboardShowing) {
                            isKeyboardShowing = true;
                            bottomBar.setVisibility(View.INVISIBLE);
                        }
                    }
                    else {
                        // keyboard is closed
                        if (isKeyboardShowing) {
                            isKeyboardShowing = false;
                            bottomBar.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }


    private void addBanSetup() {
        addFragment.setVisibility(View.GONE);
        showAdd.setOnClickListener(l -> {
            if (isAddShown) {
                addFragment.setVisibility(View.GONE);
                showAdd.setImageResource(R.drawable.baseline_arrow_right);
            } else {
                addFragment.setVisibility(View.VISIBLE);
                showAdd.setImageResource(R.drawable.baseline_arrow_drop_down);
            }
            isAddShown = !isAddShown;
        });
        txtAdd.setOnClickListener(l -> {
                showAdd.performClick();
        });

        banFragment.setVisibility(View.GONE);
        showBan.setOnClickListener(s -> {
            if (isBanShown) {
                banFragment.setVisibility(View.GONE);
                showBan.setImageResource(R.drawable.baseline_arrow_right);
            } else {
                banFragment.setVisibility(View.VISIBLE);
                showBan.setImageResource(R.drawable.baseline_arrow_drop_down);
            }
            isBanShown = !isBanShown;
        });
        txtBan.setOnClickListener(l -> {
                showBan.performClick();
        });
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
            addFragment.findViewById(R.id.appointmentCreationAddUserFragmentReset).performClick();
            banFragment.findViewById(R.id.appointmentCreationBanUserFragmentReset).performClick();

        }
    };

    private void attributeSetters() {
        calendarStart = Calendar.getInstance();
        calendarEnd = Calendar.getInstance();
        title = "";
        course = "";
        invites = new ArrayList<>();
        bans = new ArrayList<>();
        isAddShown = false;
        isBanShown = false;
        isPrivate = false;

        //We need to know who is trying to create an appointment as they are the owner
        user = MainUserSingleton.getInstance();

        privateSelector = findViewById(R.id.appointmentCreationPrivateSelector);
        bottomBar = findViewById(R.id.appointmentCreationBottomBar);
        showAdd = findViewById(R.id.appointmentCreationShowAdd);
        addFragment = findViewById(R.id.appointmentCreationAddUserFragment);
        showBan = findViewById(R.id.appointmentCreationShowBan);
        banFragment = findViewById(R.id.appointmentCreationBanUserFragment);
        editTitle = findViewById(R.id.appointmentCreationEditTxtAppointmentTitleSet);
        editCourse = findViewById(R.id.appointmentCreationEditTxtAppointmentCourseSet);
        btnDone = findViewById(R.id.appointmentCreationbtnDone);
        btnReset = findViewById(R.id.appointementCreationBtnReset);
        txtError = findViewById(R.id.appointmentCreationtxtError);
        txtStartTime = findViewById(R.id.appointmentCreationStartTime);
        txtEndTime = findViewById(R.id.appointmentCreationEndTime);
        txtAdd = findViewById(R.id.appointmentCreationAddTextView);
        txtBan = findViewById(R.id.appointmentCreationBanTextView);
        calendarStartTime = Calendar.getInstance();
        calendarEndTime = Calendar.getInstance();
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

}