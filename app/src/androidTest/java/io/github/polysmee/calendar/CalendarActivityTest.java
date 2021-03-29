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


/*
    private static final Intent intent;
    static {
        intent = new Intent(getApplicationContext(), CalendarActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(CalendarActivity.UserTypeCode, "Fake");
        intent.putExtras(bundle);
    }
    @Rule
    public ActivityScenarioRule<CalendarActivity> testRule = new ActivityScenarioRule<>(intent);

    @Before
    public void initUser(){
        FakeDatabase.idGenerator = new AtomicLong(0);
    }


    @Before
    public void setTodayDateInDailyCalendar(){
        Calendar calendar = Calendar.getInstance();
        DailyCalendar.setDayEpochTimeAtMidnight(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DATE));
    }

    @Test
    public void choosingAnotherDateChangesDisplayedDate(){
        int year = 2021;
        int month = 1;
        int day = 13;
        try(ActivityScenario<CalendarActivity> ignored = ActivityScenario.launch(intent)){
            clickOn(R.id.todayDateCalendarActivity);
            setDateOnPicker(year,month,day);
            long epochTimeToday = DailyCalendar.getDayEpochTimeAtMidnight() * 1000;
            Date date = new Date(epochTimeToday);
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
            assertDisplayed("Appointments on the " + formatter.format(date) +" : ");
        }
    }

    @Test
    public void writtenDateIsCorrectTest(){

        Date date = new Date(DailyCalendar.getDayEpochTimeAtMidnight()*1000);
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        try(ActivityScenario<CalendarActivity> ignored = ActivityScenario.launch(intent)){
            assertDisplayed("Appointments on the " + formatter.format(date) +" : ");
        }
    }

    @Test
    public void scrollViewContentIsCoherentAfterAddingAppointments(){
        Random rand = new Random();
        int number_of_appointments = rand.nextInt(9) + 1;

        CalendarAppointmentInfo[] infos = new CalendarAppointmentInfo[number_of_appointments];
        for(int i = 0; i<number_of_appointments; ++i){
            infos[i] = new CalendarAppointmentInfo("FakeCourse" + i, "FakeTitle" + i,
                    DailyCalendar.getDayEpochTimeAtMidnight() + i*60,50,""+i,null,i);
        }

        try(ActivityScenario<CalendarActivity> ignored = ActivityScenario.launch(intent)){
            ViewInteraction demoButton = Espresso.onView(withId(R.id.calendarActivityCreateAppointmentButton));
            for(int i = 0; i < number_of_appointments; ++i)
                demoButton.perform(ViewActions.click()); //add the appointments to layout
            if(number_of_appointments > 6){
                for(int i = 0; i<6;++i){
                    assertDisplayed(formatAppointmentDescription(infos[i]));
                }
                ViewInteraction scrollLayout = Espresso.onView(ViewMatchers.withId(R.id.calendarActivityAppointmentScroll)).perform(ViewActions.swipeUp());
                for (int i = 6; i < number_of_appointments; ++i){
                    assertDisplayed(formatAppointmentDescription(infos[i]));
                }
            }

        }
    }*/


    private static final String username1 = "Youssef le dindon";

    private static final String appointmentTitle = "coucouchou";
    private static final String appointmentId = "-lsdqrhrrdtisjhmf";
    private static final String appointmentCourse = "SDP";
    private static final long appointmentStart = 265655445;
    @BeforeClass
    public static void setUp() throws Exception {

        DatabaseFactory.setTest();
        AuthenticationFactory.setTest();
        FirebaseApp.clearInstancesForTest();
        FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext());
        Tasks.await(AuthenticationFactory.getAdaptedInstance().createUserWithEmailAndPassword("polysmee241098@gmail.com", "fakePassword"));
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
    public void scrollViewContentIsCoherentAfterAddingAppointments(){

        Intent intent = new Intent(getApplicationContext(), CalendarActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(CalendarActivity.UserTypeCode, "Real");
        intent.putExtras(bundle);


        Calendar todayDate = Calendar.getInstance();
        todayDate.setTime(new Date(DailyCalendar.getDayEpochTimeAtMidnight()*1000));

        int number_of_appointments = 7;

        CalendarAppointmentInfo[] infos = new CalendarAppointmentInfo[number_of_appointments];
        for(int i = 0; i<number_of_appointments; ++i){
            infos[i] = new CalendarAppointmentInfo("FakeCourse" + i, "FakeTitle" + i,
                    DailyCalendar.getDayEpochTimeAtMidnight() + i*3600*2,3600*2,""+i,null,i);
        }

        try(ActivityScenario<CalendarActivity> ignored = ActivityScenario.launch(intent)){
            //ViewInteraction demoButton = Espresso.onView(withId(R.id.calendarActivityCreateAppointmentButton));
            for(int i = 0; i < number_of_appointments; ++i)
                createAppointment(todayDate.get(Calendar.DATE),todayDate.get(Calendar.MONTH),todayDate.get(Calendar.YEAR),
                        i*2,0, "FakeTitle" + i, "FakeCourse" + i ); //add the appointments to layout
                sleep(3, SECONDS);
            if(number_of_appointments > 6){
                for(int i = 0; i<6;++i){
                    assertDisplayed(formatAppointmentDescription(infos[i]));
                }
                ViewInteraction scrollLayout = Espresso.onView(ViewMatchers.withId(R.id.calendarActivityAppointmentScroll)).perform(ViewActions.swipeUp());
                for (int i = 6; i < number_of_appointments; ++i){
                    assertDisplayed(formatAppointmentDescription(infos[i]));
                }
            }

        }
    }

    private void createAppointment(int day, int month, int year, int hour, int minute, String title, String course){
        clickOn((R.id.calendarActivityCreateAppointmentButton));
        sleep(2,SECONDS);
        setStartTime(day,month,year,hour,minute);
        setEndTime(day,month,year,hour+2,minute);

        writeTo(R.id.appointmentCreationEditTxtAppointmentTitleSet, title);
        closeSoftKeyboard();

        writeTo(R.id.appointmentCreationEditTxtAppointmentCourseSet, course);
        closeSoftKeyboard();

        clickOn(R.id.appointmentCreationbtnDone);
    }

    private void setStartTime(int day, int month, int year, int hour, int minute){
        clickOn(R.id.appointmentCreationBtnStartTime);
        setDateOnPicker(year, month, day);
        setTimeOnPicker(hour, minute);
    }

    private void setEndTime(int day, int month, int year, int hour, int minute){
        clickOn(R.id.appointmentCreationBtnEndTime);
        setDateOnPicker(year, month, day);
        setTimeOnPicker(hour, minute);

    }
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
