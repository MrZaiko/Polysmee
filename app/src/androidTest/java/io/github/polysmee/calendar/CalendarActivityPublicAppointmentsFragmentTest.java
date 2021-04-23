package io.github.polysmee.calendar;


import android.content.Intent;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.FirebaseApp;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import io.github.polysmee.R;
import io.github.polysmee.calendar.calendarActivityFragments.CalendarActivityPublicAppointmentsFragment;
import io.github.polysmee.database.DatabaseFactory;
import io.github.polysmee.database.DatabaseUser;
import io.github.polysmee.login.AuthenticationFactory;
import io.github.polysmee.login.MainUserSingleton;
import io.github.polysmee.notification.AppointmentReminderNotificationSetupListener;
import io.github.polysmee.room.fragments.RoomActivityMessagesFragment;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed;
import static com.schibsted.spain.barista.interaction.BaristaClickInteractions.clickOn;
import static com.schibsted.spain.barista.interaction.BaristaPickerInteractions.setDateOnPicker;
import static com.schibsted.spain.barista.interaction.BaristaSleepInteractions.sleep;
import static java.util.concurrent.TimeUnit.SECONDS;

@RunWith(AndroidJUnit4.class)
public class CalendarActivityPublicAppointmentsFragmentTest {

    private static int appointmentYear = 2042;
    private static int appointmentMonth = 10;
    private static int appointmentDay = 27;


    private static final String appointmentId = "ukcfjsqmcutn";
    private static final String username1 = "Youssef le gentil";
    private static String id2 = "uzeyazfedst";
    private static final String username2 = "amogus";

    private static final SimpleDateFormat dayFormatter = new SimpleDateFormat("d");
    private static final SimpleDateFormat letterDayFormatter = new SimpleDateFormat("EEEE");
    private static Calendar startTime;

    @BeforeClass
    public static void setUp() throws Exception {

        startTime = Calendar.getInstance();
        startTime.set(appointmentYear,appointmentMonth,appointmentDay,18,3,0);
        AppointmentReminderNotificationSetupListener.setIsNotificationSetterEnable(false);

        DatabaseFactory.setTest();
        AuthenticationFactory.setTest();
        FirebaseApp.clearInstancesForTest();
        FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext());

        Tasks.await(AuthenticationFactory.getAdaptedInstance().createUserWithEmailAndPassword("CalendarActivityPublicAppointmentsFragmentTest@gmail.com", "fakePassword"));
        DatabaseFactory.getAdaptedInstance().getReference("users").child(MainUserSingleton.getInstance().getId()).child("name").setValue(username1);
        DatabaseFactory.getAdaptedInstance().getReference("users").child(id2).child("name").setValue(username2);
    }


    @Before
    public void setupDailyCalendar(){
        Calendar calendar = Calendar.getInstance();
        DailyCalendar.setDayEpochTimeAtMidnight(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DATE),true);
    }

    @Test
    public void writtenDateIsCorrectPublicAppointments(){
        Date date = new Date(DailyCalendar.getDayEpochTimeAtMidnight(true));
        FragmentScenario.launchInContainer(CalendarActivityPublicAppointmentsFragment.class);
        sleep(3, TimeUnit.SECONDS);
        assertDisplayed(dayFormatter.format(date));
        assertDisplayed(letterDayFormatter.format(date));
    }



    @Test
    public void anotherUsersAppointmentIsVisible(){
        CalendarAppointmentInfo calendarAppointmentInfo = new CalendarAppointmentInfo("vent","amogus",DailyCalendar.getDayEpochTimeAtMidnight(true),60,appointmentId);
        addAppointmentOtherUser(calendarAppointmentInfo);
        FragmentScenario.launchInContainer(CalendarActivityPublicAppointmentsFragment.class);
        sleep(5,TimeUnit.SECONDS);
        assertDisplayed(calendarAppointmentInfo.getTitle());

        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
        Date startDate = new Date(calendarAppointmentInfo.getStartTime());
        Date endDate = new Date((calendarAppointmentInfo.getStartTime()+calendarAppointmentInfo.getDuration()));
        assertDisplayed(formatter.format(startDate) + " - " + formatter.format(endDate));
        assertDisplayed("Join");
    }

    @Test
    public void choosingAnotherDateInPublicAppointmentsDateChangesDisplayedDate(){
        FragmentScenario.launchInContainer(CalendarActivityPublicAppointmentsFragment.class);
        sleep(3,TimeUnit.SECONDS);

        clickOn(R.id.todayDatePublicAppointmentsCalendarActivity);
        setDateOnPicker(appointmentYear, appointmentMonth, appointmentDay);
        long epochTimeToday = DailyCalendar.getDayEpochTimeAtMidnight(true);
        Date date = new Date(epochTimeToday);
        assertDisplayed(dayFormatter.format(date));
        assertDisplayed(letterDayFormatter.format(date));
    }

    @Test
    public void addingAnAppointmentOnAnotherDayDisplaysItOnlyWhenChoosingThatDay(){

        CalendarAppointmentInfo calendarAppointmentInfo = new CalendarAppointmentInfo("BonjourGoogle","BonjourBing",startTime.getTimeInMillis(),60,appointmentId+1);
        addAppointmentOtherUser(calendarAppointmentInfo);
        FragmentScenario.launchInContainer(CalendarActivityPublicAppointmentsFragment.class);
        sleep(3,TimeUnit.SECONDS);


        clickOn(R.id.todayDatePublicAppointmentsCalendarActivity);
        setDateOnPicker(appointmentYear, appointmentMonth+1, appointmentDay);
        sleep(3,SECONDS);
        assertDisplayed(calendarAppointmentInfo.getTitle());

    }
    private void addAppointmentOtherUser(CalendarAppointmentInfo calendarAppointmentInfo){
        DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId).child("id").setValue(calendarAppointmentInfo.getId());
        DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId).child("duration").setValue(calendarAppointmentInfo.getDuration());
        DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId).child("private").setValue(false);
        DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId).child("owner").setValue(id2);
        DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId).child("participants").child(id2).setValue(true);
        DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId).child("title").setValue(calendarAppointmentInfo.getTitle());
        DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId).child("course").setValue(calendarAppointmentInfo.getCourse());
        DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId).child("start").setValue(calendarAppointmentInfo.getStartTime());
    }

}
