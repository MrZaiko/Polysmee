package io.github.polysmee.appointments;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

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
    public static final String REMOVED_INVITES = "REMOVED INVITES";
    public static final String BANS = "BANS";
    public static final String REMOVED_BANS = "REMOVED BANS";

    private Calendar calendarStartTime, calendarEndTime;
    private boolean startTimeUpdated, endTimeUpdated;
    private String title;
    private String course;
    private Set<String> invites, bans, removedInvites, removedBans;
    private boolean isPrivate, isOwner;
    boolean isKeyboardShowing = false;

    private EditText editTitle, editCourse;
    private Button btnDone, btnReset;
    private TextView txtTimeError, txtAddBanError, txtStartTime, txtEndTime, txtAdd, txtBan;
    private SwitchCompat privateSelector;
    private ImageView showAdd, showBan;
    private View addFragment, banFragment, bottomBar, startTimeLayout, endTimeLayout;
    private boolean isAddShown, isBanShown;

    //A calendar is a wait to get time using year/month... and allows to transform it to epoch time
    private final String dateFormat = "dd/MM/yyyy - HH:mm";
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


        txtTimeError.setVisibility(View.GONE);
        txtAddBanError.setVisibility(View.GONE);

        startTimeEndTimeSelectorsSetup();
        privateSelector.setOnCheckedChangeListener((l,newValue) -> isPrivate = newValue);

        addBanSetup();
        bottomBarSetup(false);

        if (mode == DETAIL_MODE) {
            setupClickable(false);
            listenersSetup();
        }
    }

    private void setupClickable(boolean isClickable) {
        editTitle.setEnabled(isClickable);
        editCourse.setEnabled(isClickable);

        startTimeLayout.setClickable(isClickable);
        endTimeLayout.setClickable(isClickable);
        privateSelector.setClickable(isClickable);
    }

    private void listenersSetup() {
        appointment.getStartTimeAndThen(start -> {
            Date startDate = new Date(start);
            calendarStartTime.setTime(startDate);
            txtStartTime.setText(DateFormat.format(dateFormat, startDate));
            appointment.getDurationAndThen(duration -> {
                Date endDate = new Date(start+duration);
                calendarEndTime.setTime(endDate);
                txtEndTime.setText(DateFormat.format(dateFormat, endDate));
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
            btnDone.setText(R.string.AppointmentActivityDetailModeApplyChanges);
            btnReset.setVisibility(View.GONE);
            btnReset.setClickable(false);
            if (isOwner)
                bottomBar.setVisibility(View.VISIBLE);
            else
                bottomBar.setVisibility(View.GONE);
        }

        btnDone.setOnClickListener(v -> {
            boolean error = false;

            //if startTime is bigger than endTime we have a negative duration which doesn't work
            //It isn't possible to create an appointment scheduled before the current time
            Calendar c = Calendar.getInstance();
            c.set(Calendar.MONTH, c.get(Calendar.MONTH) - 1);

            Set<String> invitesInterBans = new HashSet<>(invites);
            invitesInterBans.retainAll(bans);

            if (calendarStartTime.getTimeInMillis() >= calendarEndTime.getTimeInMillis() || calendarStartTime.getTimeInMillis() <= c.getTimeInMillis()){
                txtTimeError.setVisibility(View.VISIBLE);
                error = true;
            }

            if (!invitesInterBans.isEmpty()) {
                txtAddBanError.setVisibility(View.VISIBLE);
                error = true;
            }

            if (!error) {
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
            txtTimeError.setText("");
            calendarEndTime = Calendar.getInstance();
            txtEndTime.setText(getString(R.string.appointment_creation_pick_end_time));
            calendarStartTime = Calendar.getInstance();
            txtStartTime.setText(getString(R.string.appointment_creation_pick_start_time));

            AppointmentCreationAddUserFragment addFragment =
                    (AppointmentCreationAddUserFragment) getSupportFragmentManager().findFragmentById(R.id.appointmentCreationAddUserFragment);
            addFragment.reset();

            AppointmentCreationBanUserFragment banFragment =
                    (AppointmentCreationBanUserFragment) getSupportFragmentManager().findFragmentById(R.id.appointmentCreationBanUserFragment);
            banFragment.reset();
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
            txtAdd.setText(R.string.AppointmentActivityDetailModeParticipants);

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
            txtBan.setText(R.string.AppointmentActivityDetailModeBannedParticipants);
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
            startTimeUpdated = true;
            calendarStartTime = (Calendar) date.clone();
        } else {
            endTimeUpdated = true;
            calendarEndTime = (Calendar) date.clone();
        }

        //If there was an error setting a new start or end time can fix it so remove the error message as the user knows they need to fix it
        txtTimeError.setVisibility(View.GONE);
        //Display the time on screen so that the user can know their input has been taken
        textView.setText(DateFormat.format(dateFormat, date.getTime()));
    }


    private void sendData() {
        //Avoid conversion problem
        long startTime = calendarStartTime.getTimeInMillis();
        long duration = calendarEndTime.getTimeInMillis() - calendarStartTime.getTimeInMillis();

        if (mode == ADD_MODE) {
            String aptID = MainUserSingleton.getInstance().createNewUserAppointment(startTime, duration, course, title, isPrivate);
            appointment = new DatabaseAppointment(aptID);
        } else {
            //TODO mb a new function to edit everything at once
            if (!editTitle.getText().toString().equals(""))
                appointment.setTitle(editTitle.getText().toString());

            if (!editCourse.getText().toString().equals(""))
                appointment.setCourse(editCourse.getText().toString());

            if (startTimeUpdated)
                appointment.setStartTime(startTime);

            if (startTimeUpdated || endTimeUpdated)
                appointment.setDuration(duration);

            appointment.setPrivate(isPrivate);
        }

        User.getAllUsersIdsAndThenOnce(this::updateParticipantsAndBans);
    }

    //not efficient at all
    private void updateParticipantsAndBans(@NotNull Set<String> allIds) {
        for(String userId : allIds){
            User user = new DatabaseUser(userId);
            user.getNameAndThen((name) -> {
                if (mode == DETAIL_MODE) {
                    for (String removedParticipant : removedInvites) {
                        if (name.equals(removedParticipant)) {
                            user.removeAppointment(appointment);
                            appointment.removeParticipant(user);
                        }
                    }

                    for (String removedBan : removedBans) {
                        if (name.equals(removedBan)) {
                            appointment.removeBan(user);
                        }
                    }
                }

                for (String banName : bans) {
                    if (name.equals(banName)) {
                        appointment.addBan(user);
                    }
                }

                appointment.getBansAndThen( bannedUsers -> {
                    for(String inviteName : invites) {
                        if (name.equals(inviteName) && !bannedUsers.contains(user.getId())) {
                            user.addAppointment(appointment);
                            appointment.addParticipant(user);
                        }
                    }
                });
            });
        }
    }

    private void attributeSetters() {
        calendarStartTime = Calendar.getInstance();
        calendarEndTime = Calendar.getInstance();
        date = Calendar.getInstance();
        startTimeUpdated = false;
        endTimeUpdated = false;

        title = "";
        course = "";

        invites = new HashSet<>();
        bans = new HashSet<>();
        removedInvites = new HashSet<>();
        removedBans = new HashSet<>();

        isAddShown = false;
        isBanShown = false;
        isPrivate = false;
        isOwner = false;

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
        txtTimeError = findViewById(R.id.appointmentCreationTimeError);
        txtAddBanError = findViewById(R.id.appointmentCreationAddBanError);
        txtStartTime = findViewById(R.id.appointmentCreationStartTime);
        txtEndTime = findViewById(R.id.appointmentCreationEndTime);
        txtAdd = findViewById(R.id.appointmentCreationAddTextView);
        txtBan = findViewById(R.id.appointmentCreationBanTextView);

    }

    @Override
    public void dataPass(Set<String> data, String id) {
        //Any changes to the sets may correct the error
        txtAddBanError.setVisibility(View.GONE);
        switch (id) {
            case INVITES:
                invites = new HashSet<>(data);
                break;
            case REMOVED_INVITES:
                removedInvites = new HashSet<>(data);
                break;
            case REMOVED_BANS:
                removedBans = new HashSet<>(data);
                break;
            case BANS:
                bans = new HashSet<>(data);
                break;
        }
    }

}