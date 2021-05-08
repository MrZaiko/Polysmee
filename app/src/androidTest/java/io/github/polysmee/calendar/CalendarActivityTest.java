package io.github.polysmee.calendar;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.FirebaseApp;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import io.github.polysmee.R;
import io.github.polysmee.database.DatabaseFactory;
import io.github.polysmee.invites.InvitesManagementActivity;
import io.github.polysmee.login.AuthenticationFactory;
import io.github.polysmee.login.MainUser;
import io.github.polysmee.profile.ProfileActivity;
import io.github.polysmee.room.RoomActivity;
import io.github.polysmee.znotification.AppointmentReminderNotificationSetupListener;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withHint;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed;
import static com.schibsted.spain.barista.interaction.BaristaClickInteractions.clickOn;
import static com.schibsted.spain.barista.interaction.BaristaEditTextInteractions.writeTo;
import static com.schibsted.spain.barista.interaction.BaristaPickerInteractions.setDateOnPicker;
import static com.schibsted.spain.barista.interaction.BaristaScrollInteractions.scrollTo;
import static com.schibsted.spain.barista.interaction.BaristaSleepInteractions.sleep;
import static com.schibsted.spain.barista.interaction.BaristaViewPagerInteractions.swipeViewPagerBack;
import static com.schibsted.spain.barista.interaction.BaristaViewPagerInteractions.swipeViewPagerForward;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class CalendarActivityTest {

    private static final String username1 = "Youssef le dindon";
    private static final String appointmentTitle = "J'adore le surf";
    private static final String appointmentCourse = "SDP";
    private static final String appointmentId = "-lsdqrhrrdtisjhmf";

    private static Calendar startTime;
    private static int appointmentYear = 2022;
    private static int appointmentMonth = 3;
    private static int appointmentDay = 7;

    private static final SimpleDateFormat dayFormatter = new SimpleDateFormat("d");
    private static final SimpleDateFormat letterDayFormatter = new SimpleDateFormat("EEEE");

    @BeforeClass
    public static void setUp() throws Exception {
        startTime = Calendar.getInstance();
        startTime.set(appointmentYear,appointmentMonth,appointmentDay,18,3,0);
        AppointmentReminderNotificationSetupListener.setIsNotificationSetterEnable(false);
        DatabaseFactory.setTest();
        AuthenticationFactory.setTest();
        FirebaseApp.clearInstancesForTest();
        FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext());
        Tasks.await(AuthenticationFactory.getAdaptedInstance().createUserWithEmailAndPassword("CalendarActivityTest@gmail.com", "fakePassword"));
        DatabaseFactory.getAdaptedInstance().getReference("users").child(MainUser.getMainUser().getId()).child("name").setValue(username1);
        DatabaseFactory.getAdaptedInstance().getReference("users").child(MainUser.getMainUser().getId()).child("appointments").child(appointmentId).setValue(true);
        DatabaseFactory.getAdaptedInstance().getReference("courses").child(appointmentCourse).setValue(appointmentCourse);
    }


    @AfterClass
    public static void clean() {
        DatabaseFactory.getAdaptedInstance().getReference().setValue(null);
    }

    @Before
    public void setTodayDateInDailyCalendar(){
        Calendar calendar = Calendar.getInstance();
        DailyCalendar.setDayEpochTimeAtMidnight(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DATE),false);
    }
    @Test
    public void allCalendarTest(){
        Intent intent = new Intent(getApplicationContext(), CalendarActivity.class);

        Calendar calendar = Calendar.getInstance();
        try(ActivityScenario<CalendarActivity> ignored = ActivityScenario.launch(intent)){
            Date date = new Date(DailyCalendar.getDayEpochTimeAtMidnight(false));

            //modifyingTitleIsSeenOnTheCalendar
            String title = "NewTitle";
            long startTime = calendar.getTimeInMillis() + 60*1000;
            CalendarAppointmentInfo info = new CalendarAppointmentInfo("SDP", "ClickMeBoi" ,
                    startTime ,3600*6*1000,appointmentId+5);
            MainUser.getMainUser().createNewUserAppointment(info.getStartTime(),
                    info.getDuration(), info.getCourse(), info.getTitle(), false);
            sleep(3,SECONDS);
            clickOn(info.getTitle());
            sleep(1,SECONDS);
            scrollTo(R.id.appointmentCreationEditTxtAppointmentTitleSet);
            writeTo(R.id.appointmentCreationEditTxtAppointmentTitleSet, title);
            closeSoftKeyboard();
            clickOn(R.id.appointmentCreationbtnDone);
            sleep(2,SECONDS);
            assertDisplayed(title);

            //clickingOnAnAppointmentLaunchesItsDetailsWhenItsBeforeItsTime
            CalendarAppointmentInfo info1 = new CalendarAppointmentInfo("SDP", "ClickMe" ,
                    calendar.getTimeInMillis() + 60*1000 ,3600*6*1000,appointmentId+5);
            MainUser.getMainUser().createNewUserAppointment(info1.getStartTime(),
                    info1.getDuration(), info1.getCourse(), info1.getTitle(), false);
            sleep(3,SECONDS);
            scrollTo(info1.getTitle());
            clickOn(info1.getTitle());
            assertDisplayed(withHint(info1.getTitle()));
            assertDisplayed(withText(info1.getCourse()));
            pressBack();
            sleep(2,SECONDS);
            //writtenDateIsCorrectTest
            assertDisplayed(dayFormatter.format(date));
            assertDisplayed(letterDayFormatter.format(date));


            //scrollViewContentsIsCoherentAfterAddingAppointments
            int number_of_appointments = 2;

            CalendarAppointmentInfo[] infos = new CalendarAppointmentInfo[number_of_appointments];
            for(int i = 0; i<number_of_appointments; ++i){
                infos[i] = new CalendarAppointmentInfo("SDP" + i, "FakeTitle" + i,
                        DailyCalendar.getDayEpochTimeAtMidnight(false) + i*3600*6*1000,3600*6*1000,appointmentId+i);

            }

            for(int i = 0; i< number_of_appointments; ++i){
                MainUser.getMainUser().createNewUserAppointment(infos[i].getStartTime(),
                        infos[i].getDuration(), infos[i].getCourse(), infos[i].getTitle(), i%2==0);
                sleep(3,SECONDS);
            }

            for(int i = 0; i<number_of_appointments;++i){
                scrollTo(infos[i].getTitle());
                assertDisplayed(infos[i].getTitle());
                SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
                Date startDate = new Date(infos[i].getStartTime());
                Date endDate = new Date((infos[i].getStartTime()+infos[i].getDuration()));
                scrollTo(formatter.format(startDate) + " - " + formatter.format(endDate));
                assertDisplayed(formatter.format(startDate) + " - " + formatter.format(endDate));
            }

            swipeViewPagerForward();
            sleep(3,SECONDS);
            for(int i = 0; i<number_of_appointments;++i){
                if(i%2 != 0){
                    assertDisplayed(infos[i].getTitle());
                    SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
                    Date startDate = new Date(infos[i].getStartTime());
                    Date endDate = new Date((infos[i].getStartTime()+infos[i].getDuration()));
                    scrollTo(formatter.format(startDate) + " - " + formatter.format(endDate));
                    assertDisplayed(formatter.format(startDate) + " - " + formatter.format(endDate));
                }
            }
            swipeViewPagerBack();

            sleep(3,SECONDS);

            //addingAnAppointmentOnAnotherDayDisplaysItOnlyWhenChoosingThatDay

            MainUser.getMainUser().createNewUserAppointment(CalendarActivityTest.startTime.getTimeInMillis(),
                    3600, appointmentCourse, appointmentTitle, false);
            sleep(5,SECONDS);

            boolean thrown = false;
            try {
                onView(withText(appointmentTitle)).check(matches(isDisplayed()));
            } catch (NoMatchingViewException e) {
                thrown = true;
            }
            assertTrue(thrown);

            clickOn(R.id.activityCalendarMonthMyAppointments);
            setDateOnPicker(appointmentYear, appointmentMonth+1, appointmentDay);
            sleep(2,SECONDS);
            assertDisplayed(appointmentTitle);
            sleep(2,SECONDS);
            sleep(2,SECONDS);
            //profileButtonShouldOpenProfile
            try{
                clickOn(R.id.calendarMenuProfile);
                assertDisplayed(username1);
            }catch(Exception  e){
                openActionBarOverflowOrOptionsMenu(getApplicationContext());
                sleep(2,SECONDS);
                clickOn(R.id.calendarMenuProfile);
                assertDisplayed(username1);
            }
            sleep(2,SECONDS);
            assertDisplayed(username1);
            pressBack();
            //notificationButtonShouldOpenInvites
            sleep(2,SECONDS);
            try{
                clickOn(R.id.calendarMenuNotifications);
                sleep(1,SECONDS);
                assertDisplayed("Current invitations");
            }catch(Exception  e){
                openActionBarOverflowOrOptionsMenu(getApplicationContext());
                sleep(2,SECONDS);
                clickOn("Notifications");
                sleep(1,SECONDS);
                assertDisplayed("Current invitations");
            }
            pressBack();
            //clickingSettingsButtonLaunchesSettingsActivity
            sleep(2,SECONDS);
            try{
                clickOn(R.id.calendarMenuSettings);
                assertDisplayed("Appointments reminder settings");
            }catch(Exception  e){
                openActionBarOverflowOrOptionsMenu(getApplicationContext());
                sleep(2,SECONDS);
                clickOn("Settings");
                assertDisplayed("Appointments reminder settings");
            }

        }
    }

}