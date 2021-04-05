package io.github.polysmee.appointments;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import io.github.polysmee.R;
import io.github.polysmee.appointments.fragments.AppointmentCreationAddUserFragment;
import io.github.polysmee.appointments.fragments.AppointmentCreationBanUserFragment;
import io.github.polysmee.appointments.fragments.DataPasser;
import io.github.polysmee.database.DatabaseAppointment;
import io.github.polysmee.database.DatabaseUser;
import io.github.polysmee.interfaces.Appointment;
import io.github.polysmee.interfaces.User;
import io.github.polysmee.login.MainUserSingleton;

/*
 * Mode :   0 ==> Add (Default)
 *          1 ==> Detail
 *
 */
public class AppointmentActivity extends AppCompatActivity implements DataPasser {

    public static final int ADD_MODE = 0;
    public static final int DETAIL_MODE = 1;

    public static final String INVITES = "INVITES";
    public static final String BANS = "BANS";

    private Calendar calendarStart, calendarEnd;
    private String title;
    private String course;
    private ArrayList<String> invites;
    private ArrayList<String> bans;
    private boolean isPrivate, isOwner;
    boolean isKeyboardShowing = false;

    private User user;

    public static final String ERROR_TXT = "Error : Start and end time must result in a correct time slot";
    private EditText editTitle, editCourse;
    private Button btnDone, btnReset;
    private Calendar calendarStartTime, calendarEndTime;
    private TextView txtError, txtStartTime, txtEndTime, txtAdd, txtBan;
    private SwitchCompat privateSelector;
    private ImageView showAdd, showBan;
    private View addFragment, banFragment, bottomBar, startTimeLayout, endTimeLayout;
    private boolean isAddShown, isBanShown;

    //A calendar is a wait to get time using year/month... and allows to transform it to epoch time
    private Calendar date;

    private int mode;
    private Appointment appointment;
    public static String LAUNCH_MODE = "io.github.polysmee.appointments.AppointmentActivity.APPOINTMENT_ACTIVITY_LAUNCH_MODE";
    public static String APPOINTMENT_ID = "io.github.polysmee.appointments.AppointmentActivity.APPOINTMENT_ACTIVITY_APPOINTMENT_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_creation);

        //========================================INIT==============================================

        //store all objects on the activity (buttons, textViews...) in variables
        attributeSetters();

        mode = getIntent().getIntExtra(LAUNCH_MODE, ADD_MODE);

        String appointmentId = "";
        if (mode == DETAIL_MODE) {
            appointmentId = getIntent().getStringExtra(APPOINTMENT_ID);
            appointment = new DatabaseAppointment(appointmentId);
        }

        AppointmentCreationAddUserFragment addFragment =
                (AppointmentCreationAddUserFragment) getSupportFragmentManager().findFragmentById(R.id.appointmentCreationAddUserFragment);
        addFragment.launchSetup(mode, appointmentId);

        AppointmentCreationBanUserFragment banFragment =
                (AppointmentCreationBanUserFragment) getSupportFragmentManager().findFragmentById(R.id.appointmentCreationBanUserFragment);
        banFragment.launchSetup(mode, appointmentId);

        setupKeyboardDetection();

        //==========================================================================================


        txtError.setVisibility(View.INVISIBLE);

        startTimeEndTimeSelectorsSetup();
        privateSelector.setOnCheckedChangeListener((l,newValue) -> isPrivate = newValue);

        addBanSetup();
        bottomBarSetup(false);

        if (mode == DETAIL_MODE) {
            setupClickable(false);
            listenersSetup();
        }
    }

    public void setupClickable(boolean isClickable) {
        editTitle.setEnabled(isClickable);
        editCourse.setEnabled(isClickable);

        startTimeLayout.setClickable(isClickable);
        endTimeLayout.setClickable(isClickable);
        privateSelector.setClickable(isClickable);
    }

    //TODO Fix time s -> ms
    private void listenersSetup() {
        appointment.getStartTimeAndThen(start -> {
            Date startDate = new Date(start*1000);
            date.setTime(startDate);
            updateCalendar(txtStartTime, true);
            appointment.getDurationAndThen(duration -> {
                Date endDate = new Date((start+duration)*1000);
                date.setTime(endDate);
                updateCalendar(txtEndTime, false);
            });
        });

        appointment.getTitleAndThen(title -> editTitle.setHint(title));

        appointment.getCourseAndThen(course -> editCourse.setHint(course));

        appointment.getOwnerIdAndThen(owner -> {
            if (owner.equals(MainUserSingleton.getInstance().getId())) {
                setupClickable(true);
                isOwner = true;
                bottomBarSetup(true);
            }
        });

        appointment.getPrivateAndThen(val -> privateSelector.setChecked(val));
    }

    private void startTimeEndTimeSelectorsSetup() {
        startTimeLayout.setOnClickListener(v -> showDateTimePicker(txtStartTime, true));
        endTimeLayout.setOnClickListener(v -> showDateTimePicker(txtEndTime, false));
    }

    private void bottomBarSetup(boolean isOwner) {
        if (mode == DETAIL_MODE) {
            btnDone.setText("Apply changes");
            btnReset.setVisibility(View.INVISIBLE);
            btnReset.setClickable(false);
            if (isOwner)
                bottomBar.setVisibility(View.VISIBLE);
            else
                bottomBar.setVisibility(View.GONE);
        }

        btnDone.setOnClickListener(v -> {
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
        });

        btnReset.setOnClickListener(v -> {
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
        });
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
                            updateBottomBar();
                        }
                    }
                    else {
                        // keyboard is closed
                        if (isKeyboardShowing) {
                            isKeyboardShowing = false;
                            updateBottomBar();
                        }
                    }
                });
    }

    private void updateBottomBar() {
        if (!isKeyboardShowing && (mode != DETAIL_MODE || isOwner))
            bottomBar.setVisibility(View.VISIBLE);
        else if (mode != DETAIL_MODE || isOwner)
            bottomBar.setVisibility(View.GONE);
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
        if (mode == DETAIL_MODE)
            txtAdd.setText("Participants");

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
        if (mode == DETAIL_MODE)
            txtBan.setText("Banned participants");
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

    private void updateCalendar(TextView textView, boolean isStart) {
        //Update start or end time with user input
        if (isStart) {
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


    private void sendData() {
        if (mode == ADD_MODE) {
            String aptID = user.createNewUserAppointment(calendarStart.getTimeInMillis(), calendarEnd.getTimeInMillis() - calendarStart.getTimeInMillis(), course, title, isPrivate);
            appointment = new DatabaseAppointment(aptID);

            //TODO mb fix ?
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
        } else {
            //TODO mb a new function to change everything at once
            //TODO fix time s -> ms
            if (!editTitle.getText().toString().equals(""))
                appointment.setTitle(editTitle.getText().toString());
            if (!editCourse.getText().toString().equals(""))
                appointment.setCourse(editCourse.getText().toString());
            appointment.setStartTime(calendarStart.getTimeInMillis()/1000);
            long diff = calendarEndTime.getTimeInMillis()-calendarStartTime.getTimeInMillis();
            appointment.setDuration(diff/1000);
            appointment.setPrivate(isPrivate);
        }

    }

    private void attributeSetters() {
        calendarStart = Calendar.getInstance();
        calendarEnd = Calendar.getInstance();
        date = Calendar.getInstance();
        title = "";
        course = "";
        invites = new ArrayList<>();
        bans = new ArrayList<>();
        isAddShown = false;
        isBanShown = false;
        isPrivate = false;
        isOwner = false;

        //We need to know who is trying to create an appointment as they are the owner
        user = MainUserSingleton.getInstance();

        startTimeLayout = findViewById(R.id.appointmentCreationStartTimeLayout);
        endTimeLayout = findViewById(R.id.appointmentCreationEndTimeLayout);
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