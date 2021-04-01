package io.github.polysmee.calendar;

import android.content.Intent;
import android.os.Bundle;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.FirebaseApp;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertNotDisplayed;
import static com.schibsted.spain.barista.interaction.BaristaClickInteractions.clickOn;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

import io.github.polysmee.R;
import io.github.polysmee.database.DatabaseFactory;
import io.github.polysmee.database.decoys.FakeDatabase;
import io.github.polysmee.login.AuthenticationFactory;
import io.github.polysmee.login.MainUserSingleton;

import static com.schibsted.spain.barista.interaction.BaristaEditTextInteractions.writeTo;
import static com.schibsted.spain.barista.interaction.BaristaPickerInteractions.setDateOnPicker;
import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed;
import static com.schibsted.spain.barista.interaction.BaristaPickerInteractions.setTimeOnPicker;
import static com.schibsted.spain.barista.interaction.BaristaSleepInteractions.sleep;
import static java.util.concurrent.TimeUnit.SECONDS;

@RunWith(AndroidJUnit4.class)
public class CalendarActivityTest {

    private static final String username1 = "Youssef le dindon";
    private static final String appointmentId = "-lsdqrhrrdtisjhmf";

    @BeforeClass
    public static void setUp() throws Exception {

        DatabaseFactory.setTest();
        AuthenticationFactory.setTest();
        FirebaseApp.clearInstancesForTest();
        FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext());
        Tasks.await(AuthenticationFactory.getAdaptedInstance().createUserWithEmailAndPassword("polysmee271098@gmail.com", "fakePassword"));
        DatabaseFactory.getAdaptedInstance().getReference("users").child(MainUserSingleton.getInstance().getId()).child("name").setValue(username1);

        /*DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId).child("title").setValue(appointmentTitle);
        DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId).child("course").setValue(appointmentCourse);
        DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId).child("start").setValue(appointmentStart);
        DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId).child("owner").setValue(MainUserSingleton.getInstance().getId());
        DatabaseFactory.getAdaptedInstance().getReference("appointments").child(appointmentId).child("participants").child(MainUserSingleton.getInstance().getId()).setValue(true);*/


    }

    @Before
    public void setTodayDateInDailyCalendar(){
        Calendar calendar = Calendar.getInstance();
        DailyCalendar.setDayEpochTimeAtMidnight(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DATE));
    }

    @Test
    public void writtenDateIsCorrectTest(){
        Intent intent = new Intent(getApplicationContext(), CalendarActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(CalendarActivity.UserTypeCode, "Real");
        intent.putExtras(bundle);
        Date date = new Date(DailyCalendar.getDayEpochTimeAtMidnight()*1000);
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        try(ActivityScenario<CalendarActivity> ignored = ActivityScenario.launch(intent)){
            assertDisplayed("Appointments on the " + formatter.format(date) +" : ");
        }
    }

    @Test
    public void choosingAnotherDateChangesDisplayedDate(){
        int year = 2021;
        int month = 1;
        int day = 13;
        Intent intent = new Intent(getApplicationContext(), CalendarActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(CalendarActivity.UserTypeCode, "Real");
        intent.putExtras(bundle);


        try(ActivityScenario<CalendarActivity> ignored = ActivityScenario.launch(intent)){
            clickOn(R.id.todayDateCalendarActivity);
            setDateOnPicker(year,month,day);
            sleep(3,SECONDS);
            long epochTimeToday = DailyCalendar.getDayEpochTimeAtMidnight() * 1000;
            Date date = new Date(epochTimeToday);
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
            assertDisplayed("Appointments on the " + formatter.format(date) +" : ");
        }
    }

    @Test
    public void addingAnAppointmentOnAnotherDayDisplaysItOnlyWhenChoosingThatDay(){
        Intent intent = new Intent(getApplicationContext(), CalendarActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(CalendarActivity.UserTypeCode, "Real");
        intent.putExtras(bundle);

        int year = 2021;
        int month = 1;
        int day = 13;

        DailyCalendar.setDayEpochTimeAtMidnight(year,month,day);
        long epochTimeOfThatDay = DailyCalendar.getDayEpochTimeAtMidnight();

        try(ActivityScenario<CalendarActivity> ignored = ActivityScenario.launch(intent)){
            CalendarAppointmentInfo info = new CalendarAppointmentInfo("FakeCourse", "FakeTitle",
                    epochTimeOfThatDay,3600*2,appointmentId+11,null,0);

            MainUserSingleton.getInstance().createNewUserAppointment(info.getStartTime(),info.getDuration(),info.getCourse(),info.getTitle());

            sleep(3,SECONDS);
            Date date = new Date(epochTimeOfThatDay*1000);
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");

            Espresso.onView(withText("Appointments on the " + formatter.format(date) +" : ")).check(doesNotExist());

            clickOn(R.id.todayDateCalendarActivity);
            setDateOnPicker(year,month,day);
            sleep(3,SECONDS);
            long epochTimeToday = DailyCalendar.getDayEpochTimeAtMidnight() * 1000;
            date = new Date(epochTimeToday);
            assertDisplayed("Appointments on the " + formatter.format(date) +" : ");
        }
    }

    @Test
    public void scrollViewContentIsCoherentAfterAddingAppointments(){

        Intent intent = new Intent(getApplicationContext(), CalendarActivity.class);

        Calendar todayDate = Calendar.getInstance();
        todayDate.setTime(new Date(DailyCalendar.getDayEpochTimeAtMidnight()*1000));
        int number_of_appointments = 4;

        CalendarAppointmentInfo[] infos = new CalendarAppointmentInfo[number_of_appointments];
        for(int i = 0; i<number_of_appointments; ++i){
            infos[i] = new CalendarAppointmentInfo("FakeCourse" + i, "FakeTitle" + i,
                    DailyCalendar.getDayEpochTimeAtMidnight() + i*3600*2,3600*2,appointmentId+i,null,i);

        }

        try(ActivityScenario<CalendarActivity> ignored = ActivityScenario.launch(intent)){

            for(int i = 0; i< number_of_appointments; ++i){
                MainUserSingleton.getInstance().createNewUserAppointment(infos[i].getStartTime(),infos[i].getDuration(),infos[i].getCourse(),infos[i].getTitle());
                sleep(3,SECONDS);
            }
            for(int i = 0; i<number_of_appointments;++i){
                assertDisplayed(formatAppointmentDescription(infos[i]));
            }



        }
    }*/

    private String formatAppointmentDescription(CalendarAppointmentInfo appointment){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Reunion name : ").append(appointment.getTitle());
        stringBuilder.append("\n");
        stringBuilder.append("Course name  : ").append(appointment.getCourse());
        stringBuilder.append("\n");
        Date date = new Date(appointment.getStartTime() * 1000);
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        stringBuilder.append("Start time : ").append(formatter.format(date));
        return stringBuilder.toString();
    }
}