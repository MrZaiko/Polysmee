package io.github.polysmee.appointments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.github.polysmee.R;
import io.github.polysmee.agora.Command;
import io.github.polysmee.appointments.fragments.AppointmentCreationAddUserFragment;
import io.github.polysmee.appointments.fragments.AppointmentCreationBanUserFragment;
import io.github.polysmee.calendar.googlecalendarsync.CalendarUtilities;
import io.github.polysmee.database.Appointment;
import io.github.polysmee.database.Course;
import io.github.polysmee.database.DatabaseAppointment;
import io.github.polysmee.database.DatabaseUser;
import io.github.polysmee.database.User;
import io.github.polysmee.database.databaselisteners.valuelisteners.BooleanValueListener;
import io.github.polysmee.database.databaselisteners.valuelisteners.LongValueListener;
import io.github.polysmee.database.databaselisteners.valuelisteners.StringValueListener;
import io.github.polysmee.internet.connection.InternetConnection;
import io.github.polysmee.login.MainUser;

/**
 * Activity to interact with appointment
 * <p>
 * MODES :
 * ADD_MODE    ==>  create an appointment for the current user
 * DETAIL_MODE ==>  display information about an appointment (ID given in Intent). If the
 * current user is the owner, they can update the appointment from this
 * activity.
 * <p>
 * INTENTS :
 * LAUNCH_MODE (NEEDED) :  0 ==> Add
 * 1 ==> Detail
 * Note => USE STATIC ATTRIBUTES FOR THE MODE SELECTION
 * <p>
 * APPOINTMENT_ID (NEEDED in DETAIL_MODE) : the appointment to be displayed
 */
public class AppointmentActivity extends AppCompatActivity implements DataPasser {

    // Intents related attributes
    public static final String LAUNCH_MODE = "io.github.polysmee.appointments.AppointmentActivity.APPOINTMENT_ACTIVITY_LAUNCH_MODE";
    public static final String APPOINTMENT_ID = "io.github.polysmee.appointments.AppointmentActivity.APPOINTMENT_ACTIVITY_APPOINTMENT_ID";

    public static final int ADD_MODE = 0;
    public static final int DETAIL_MODE = 1;
    private int mode;
    private Appointment appointment;

    // IDs for communication with fragments
    public static final String INVITES = "INVITES";
    public static final String REMOVED_INVITES = "REMOVED INVITES";
    public static final String BANS = "BANS";
    public static final String REMOVED_BANS = "REMOVED BANS";

    //DATE related attributes
    private final String dateFormat = "dd/MM/yyyy - HH:mm";
    private Calendar date, calendarStartTime, calendarEndTime;
    private boolean startTimeUpdated, endTimeUpdated;

    //Fragments related attributes
    private Set<String> invites, bans, removedInvites, removedBans;
    private boolean isAddShown, isBanShown;

    //Courses
    private ArrayList<String> courses;
    AlertDialog.Builder builder;

    // Misc
    private boolean isOwner;
    private boolean isKeyboardShowing;
    private String calendarId;

    //Commands to remove listeners
    private List<Command> commandsToRemoveListeners = new ArrayList<Command>();


    // Layout related attributes
    private EditText editTitle;
    private AutoCompleteTextView editCourse;
    private Button btnDone, btnReset;
    private TextView txtTimeError, txtAddBanError, txtStartTime, txtEndTime, txtAdd, txtBan;
    private SwitchCompat privateSelector;
    private ImageView showAdd, showBan;
    private View addFragment, banFragment, bottomBar, startTimeLayout, endTimeLayout;

    //Manage appointment deletion
    private MenuItem bin;
    private Context fragmentContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_creation);

        //========================================INIT==============================================

        attributeSetters();

        mode = getIntent().getIntExtra(LAUNCH_MODE, ADD_MODE);

        String appointmentId = "";
        if (mode == DETAIL_MODE) {
            appointmentId = getIntent().getStringExtra(APPOINTMENT_ID);
            appointment = new DatabaseAppointment(appointmentId);
        }

        //Fragment setup
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

        addBanSetup();
        bottomBarSetup(false);

        if (mode == DETAIL_MODE) {
            setupClickable(false);
            listenersSetup();
        }


    }

    @Override
    public void onDestroy() {

        Object dummyArgument = null;

        for(Command command: commandsToRemoveListeners) {
            command.execute(dummyArgument,dummyArgument);
        }

        super.onDestroy();
    }

    /**
     * store all objects on the activity (buttons, textViews...) in variables
     */
    private void attributeSetters() {
        calendarStartTime = Calendar.getInstance();
        calendarEndTime = Calendar.getInstance();
        date = Calendar.getInstance();
        startTimeUpdated = false;
        endTimeUpdated = false;

        invites = new HashSet<>();
        bans = new HashSet<>();
        removedInvites = new HashSet<>();
        removedBans = new HashSet<>();

        isAddShown = false;
        isBanShown = false;
        isOwner = false;
        isKeyboardShowing = false;

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

        //Only the app creators can change the courses so it will happen very rarely, therefore getting them only once is better
        Course.getAllCourses_Once_AndThen(s -> {
                    courses = new ArrayList<>(s);
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                            android.R.layout.simple_dropdown_item_1line, courses);
                    editCourse.setAdapter(adapter);
                }
        );

        MainUser.getMainUser().getCalendarId_Once_AndThen(id -> calendarId = id);

        builder = new AlertDialog.Builder(this);
    }

    /**
     * Make the whole activity clickable or not according to isClickable
     *
     * @param isClickable true if the UI must be clickable
     */
    private void setupClickable(boolean isClickable) {
        editTitle.setEnabled(isClickable);
        editCourse.setEnabled(isClickable);

        startTimeLayout.setClickable(isClickable);
        endTimeLayout.setClickable(isClickable);
        privateSelector.setClickable(isClickable);
    }

    /**
     * Used in DETAIL_MODE to display the values of the appointment
     */
    private void listenersSetup() {

        //Initialize listeners
        LongValueListener startTimeListener = start -> {
            Date startDate = new Date(start);
            calendarStartTime.setTime(startDate);
            txtStartTime.setText(DateFormat.format(dateFormat, startDate));

            LongValueListener durationListener = duration -> {
                Date endDate = new Date(start+duration);
                calendarEndTime.setTime(endDate);
                txtEndTime.setText(DateFormat.format(dateFormat, endDate));
            };

            appointment.getDurationAndThen(durationListener);

            commandsToRemoveListeners.add((x,y) -> appointment.removeDurationListener(durationListener));
        };

        StringValueListener titleListener = title -> editTitle.setHint(title);
        StringValueListener courseListener = course -> editCourse.setText(course);
        StringValueListener ownerListener = owner -> {
            if (owner.equals(MainUser.getMainUser().getId())) {
                setupClickable(true);
                isOwner = true;
                bottomBarSetup(true);
                if(bin != null) {
                    bin.setVisible(true);
                }
            }
        };

        BooleanValueListener privateListener = val -> privateSelector.setChecked(val);



        //Add listeners to the appointment
        appointment.getStartTimeAndThen(startTimeListener);
        appointment.getTitleAndThen(titleListener);
        appointment.getCourseAndThen(courseListener);
        appointment.getOwnerIdAndThen(ownerListener);
        appointment.getPrivateAndThen(privateListener);


        //Add listeners to the command list in order to remove them later
        commandsToRemoveListeners.add((x,y) -> appointment.removeStartListener(startTimeListener));
        commandsToRemoveListeners.add((x,y) -> appointment.removeTitleListener(titleListener));
        commandsToRemoveListeners.add((x,y) -> appointment.removeCourseListener(courseListener));
        commandsToRemoveListeners.add((x,y) -> appointment.removeOwnerListener(ownerListener));
        commandsToRemoveListeners.add((x,y) -> appointment.removePrivateListener(privateListener));
    }

    /**
     * Connect the startTime and endTime layouts to the date and time picker
     */
    private void startTimeEndTimeSelectorsSetup() {
        startTimeLayout.setOnClickListener(v -> showDateTimePicker(txtStartTime, true));
        endTimeLayout.setOnClickListener(v -> showDateTimePicker(txtEndTime, false));
    }

    /**
     * ADD_MODE => BAR:VISIBLE - RESET:VISIBLE - DONE:VISIBLE
     * DETAIL_MODE =>
     * isOwner => BAR:VISIBLE - RESET:GONE - DONE:VISIBLE
     * !isOwner => BAR:GONE - RESET:GONE - DONE:GONE
     *
     * @param isOwner used in detail mode to show the bottom bar
     */
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

        btnDone.setOnClickListener(this::doneButtonBehavior);

        btnReset.setOnClickListener(this::resetButtonBehavior);
    }

    /**
     * Call sendData() and finish the activity if no error.
     * Error =>
     * TIME ERROR      => start time before current time of end time before start time
     * ADD BAN ERROR   => a user is in the participant list and in the ban list
     */
    private void doneButtonBehavior(View view) {
        boolean error = false;

        Calendar c = Calendar.getInstance();
        c.set(Calendar.MONTH, c.get(Calendar.MONTH) - 1);

        Set<String> invitesInterBans = new HashSet<>(invites);
        invitesInterBans.retainAll(bans);

        //if startTime is bigger than endTime we have a negative duration which doesn't work
        //It isn't possible to create an appointment scheduled before the current time
        if (calendarStartTime.getTimeInMillis() >= calendarEndTime.getTimeInMillis() || calendarEndTime.getTimeInMillis() <= System.currentTimeMillis()) {//calendarStartTime.getTimeInMillis() <= System.currentTimeMillis()){
            txtTimeError.setVisibility(View.VISIBLE);
            error = true;
        }

        // A user cannot be banned and added at the same time
        if (!invitesInterBans.isEmpty()) {
            txtAddBanError.setVisibility(View.VISIBLE);
            error = true;
        }
        if(InternetConnection.isOn()) {
            String s = editCourse.getText().toString();
            if(!courses.contains(s)) {
                builder.setMessage(getString(R.string.genericCourseNotFoundText))
                        .setCancelable(false)
                        .setPositiveButton(getString(R.string.genericOkText), null);

                AlertDialog alert = builder.create();
                alert.setTitle(getString(R.string.genericErrorText));
                alert.show();
                error = true;
            }
        }


        if (!error) {

            sendData();
            finish();


        }
    }

    /**
     * Reset all fields to their default value
     */
    private void resetButtonBehavior(View view) {
        //reset every input text field and both start and end times
        editTitle.setText("");
        editCourse.setText("");
        txtAddBanError.setVisibility(View.GONE);
        txtTimeError.setVisibility(View.GONE);
        calendarEndTime = Calendar.getInstance();
        txtEndTime.setText(getString(R.string.appointment_creation_pick_end_time));
        calendarStartTime = Calendar.getInstance();
        txtStartTime.setText(getString(R.string.appointment_creation_pick_start_time));

        if (isAddShown) {
            showAdd.performClick();
            isAddShown = false;
        }
        AppointmentCreationAddUserFragment addFragment =
                (AppointmentCreationAddUserFragment) getSupportFragmentManager().findFragmentById(R.id.appointmentCreationAddUserFragment);
        addFragment.reset();

        if (isBanShown) {
            showBan.performClick();
            isBanShown = false;
        }
        AppointmentCreationBanUserFragment banFragment =
                (AppointmentCreationBanUserFragment) getSupportFragmentManager().findFragmentById(R.id.appointmentCreationBanUserFragment);
        banFragment.reset();
    }

    /**
     * Setup the add and ban layout to show or hide the corresponding fragment.
     * Change the show[Add/Ban] drawable accordingly.
     */
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

    /**
     * Detect if the keyboard is shown or not and set the attribute isKeyboardShowing
     * accordingly.
     * Call the method updateBottomBar() when the keyboard is opened or closed.
     */
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
            } else {
                // keyboard is closed
                if (isKeyboardShowing) {
                    isKeyboardShowing = false;
                    updateBottomBar();
                }
            }
        });
    }

    /**
     * Display the bottomBar if the keyboard is not shown to the user
     */
    private void updateBottomBar() {
        if (!isKeyboardShowing && (mode != DETAIL_MODE || isOwner))
            bottomBar.setVisibility(View.VISIBLE);
        else if (mode != DETAIL_MODE || isOwner)
            bottomBar.setVisibility(View.GONE);
    }

    /**
     * Display a date and time picker. Store the value selected in date attribute and set the text
     * of textView accordingly
     *
     * @param textView View showing the selected date
     * @param isStart  True if the view corresponds to the startDate
     */
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

    /**
     * Used in showDateTimePicker to update the textView and the date attribute ( calendarStartTime or calendarEndTime)
     */
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

    /**
     * Create (ADD_MODE) or update (DETAIL_MODE) the appointment
     */
    private void sendData() {
        //Avoid conversion problem
        long startTime = calendarStartTime.getTimeInMillis();
        long duration = calendarEndTime.getTimeInMillis() - calendarStartTime.getTimeInMillis();
        String title = editTitle.getText().toString();
        String course = editCourse.getText().toString();
        boolean isPrivate = privateSelector.isChecked();

        if (mode == ADD_MODE) {
            String aptID = MainUser.getMainUser().createNewUserAppointment(startTime, duration, course, title, isPrivate);
            appointment = new DatabaseAppointment(aptID);

            if (calendarId != null && !calendarId.equals("")) {
                CalendarUtilities.addAppointmentToCalendar(this,
                        calendarId, title, course, startTime, duration,
                        eventId -> MainUser.getMainUser().setAppointmentEventId(appointment, eventId),
                        () -> runOnUiThread( () ->{
                            Toast toast = Toast.makeText(this, getText(R.string.genericErrorText), Toast.LENGTH_SHORT);
                            toast.show();
                        }));
            }

        } else {
            //TODO mb a new function to edit everything at once
            if (!title.equals(""))
                appointment.setTitle(title);

            if (!course.equals(""))
                appointment.setCourse(course);

            if (startTimeUpdated)
                appointment.setStartTime(startTime);

            if (startTimeUpdated || endTimeUpdated)
                appointment.setDuration(duration);

            if (calendarId != null && !calendarId.equals("")) {
                MainUser.getMainUser().getAppointmentEventId_Once_AndThen(appointment, eventId -> {
                    if (eventId != null && !eventId.equals(""))
                        CalendarUtilities.updateAppointmentOnCalendar(this, calendarId, eventId,
                                title, course,
                                startTimeUpdated ? startTime : null,
                                startTimeUpdated || endTimeUpdated ? duration : null,
                                () -> {},
                                () -> runOnUiThread(() -> {
                                    Toast toast = Toast.makeText(this, getText(R.string.genericErrorText), Toast.LENGTH_SHORT);
                                    toast.show();
                                }));
                });
            }

            appointment.setPrivate(isPrivate);
        }

        User.getAllUsersIds_Once_AndThen(this::updateAppointmentParticipantsAndBans);
    }

    /**
     * Add all banned user to the ban list of the appointment.
     * Add the new appointment to all users that are not banned. If a banned user is in the invite list,
     * this user is skipped.
     * <p>
     * DETAIL_MODE => also remove participants or banned if needed
     *
     * @param allIds set of all Users
     */
    private void updateAppointmentParticipantsAndBans(Set<String> allIds) {

        for (String userId : allIds) {
            User user = new DatabaseUser(userId);
            user.getName_Once_AndThen((name) -> {   
                if (mode == DETAIL_MODE) {
                    user.getAppointmentEventId_Once_AndThen(appointment, eventId -> {
                        if (eventId != null && !eventId.equals("")) {
                            user.getCalendarId_Once_AndThen(userCalendarId ->
                                    updateParticipants(name, eventId, userCalendarId, user)
                            );
                        } else {
                            updateParticipants(name, null, null, user);
                        }

                    });
                } else {
                   banAndInvite(name, user);
                }
            });
        }
    }

    private void updateParticipants(String name, String eventId, String userCalendarId, User user) {
        for (String removedParticipant : removedInvites) {
            if (name.equals(removedParticipant)) {
                if (eventId != null && !eventId.equals(""))
                    CalendarUtilities.deleteAppointmentFromCalendar(this, userCalendarId, eventId,
                            () -> MainUser.getMainUser().setAppointmentEventId(appointment, ""), () -> {});
                user.removeAppointment(appointment);
                appointment.removeParticipant(user);
            }
        }
        for (String addedBan : bans) {
            if (name.equals(addedBan)) {
                if (eventId != null && !eventId.equals(""))
                    CalendarUtilities.deleteAppointmentFromCalendar(this, userCalendarId, eventId,
                            () -> MainUser.getMainUser().setAppointmentEventId(appointment, ""), () -> {});
                user.removeAppointment(appointment);
                appointment.removeParticipant(user);
            }
        }
        for (String removedBan : removedBans) {
            if (name.equals(removedBan)) {
                appointment.removeBan(user);
            }
        }

        banAndInvite(name, user);
    }

    private void banAndInvite(String name, User user) {
        for (String banName : bans) {
            if (name.equals(banName)) {
                appointment.addBan(user);
            }
        }

        appointment.getBans_Once_AndThen(bannedUsers -> {
            for (String inviteName : invites) {
                if (name.equals(inviteName) && !bannedUsers.contains(user.getId())) {
                    user.addInvite(appointment);
                    appointment.addInvite(user);
                }
            }
        });
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.appointment_menu, menu);
        MenuItem item = menu.findItem(R.id.appointmentMenuOffline);
        bin = menu.findItem(R.id.appointmentMenuDelete);
        bin.setVisible(false);

        if(appointment != null) {
            appointment.getOwnerId_Once_AndThen(owner -> {
                if(owner.equals(MainUser.getMainUser().getId())) {
                    bin.setVisible(true);
                }
            });
        }

        if(InternetConnection.isOn()) {
            item.setVisible(false);
        }
        InternetConnection.setCommand(((value, key) -> runOnUiThread(() -> item.setVisible(key))));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.appointmentMenuDelete:
                if(fragmentContext != null) {
                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(fragmentContext);
                    builder.setMessage(R.string.delete_message);
                    builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                            appointment.getParticipantsId_Once_AndThen(participants -> {
                                appointment.selfDestroy();

                                for (String partId : participants) {
                                    User part = new DatabaseUser(partId);
                                    part.getAppointmentEventId_Once_AndThen(appointment, eventId -> {
                                        if (eventId != null && !eventId.equals(""))
                                            part.getCalendarId_Once_AndThen(calendarId ->
                                                    CalendarUtilities.deleteAppointmentFromCalendar(getApplicationContext(), calendarId,
                                                            eventId, () -> {}, () -> runOnUiThread( () ->{
                                                                Toast toast = Toast.makeText(getApplicationContext(), getText(R.string.genericErrorText), Toast.LENGTH_SHORT);
                                                                toast.show();
                                                            })
                                                    )
                                            );
                                    });
                                }
                            });
                        }
                    });

                    builder.setNegativeButton(R.string.genericCancelText, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    builder.show();

                    return true;
                }

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Sets the value of the attribute fragmentContext to the one given
     * @param context
     */
    public void setContext(Context context) {
        this.fragmentContext = context;
    }

}