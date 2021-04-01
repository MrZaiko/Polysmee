package io.github.polysmee.calendar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * This class will be used to show the user his daily appointments on
 * the base of all his appointments through static methods
 */
public class DailyCalendar {


    private static long midnightEpochTime;
    private DailyCalendar(){
        //private constructor to prevent creating instances of this class
    }


    /**
     * Gets the chosen's date time at midnight in seconds, and sets the
     * midnightEpochTime attribute to that; this function is used when the user
     * changes the day he wants to see his appointments
     * @param year the chosen's date year
     * @param month the chosen's date month
     * @param day the chosen's date day of the week
     */
    public static void setDayEpochTimeAtMidnight(int year, int month, int day){
        Calendar date = Calendar.getInstance();
        date.set(year,month,day);
        date.set(Calendar.HOUR_OF_DAY,0);
        date.set(Calendar.MINUTE,0);
        date.set(Calendar.SECOND,0);
        date.set(Calendar.MILLISECOND,0);
        midnightEpochTime = date.getTimeInMillis()/1000;
    }
    /**
     * Gets the date's time at midnight in seconds epoch as chosen by the user
     * @return the time at midnight in seconds
     */
    public static long getDayEpochTimeAtMidnight(){
        return midnightEpochTime; //get the day of interest's time at midnight in seconds epoch
    }

    /**
     * For a given set of user appointments, gets the ones that are programmed to happen
     * on the day the user chose on the calendar. The appointments are ordered according
     * to their start time.
     * @param userAppointments set of appointments of the user.
     * @throws IllegalArgumentException if the set given as argument is null
     * @return the list of ordered appointments of the user for the chosen day
     */
    public static List<CalendarAppointmentInfo> getAppointmentsForTheDay(Set<CalendarAppointmentInfo> userAppointments){
        if(userAppointments == null)
            throw new IllegalArgumentException();
        long todayMidnightTime = getDayEpochTimeAtMidnight();
        long nextDayMidnightTime = todayMidnightTime + 24 * 3600; //get the epoch time in seconds of next day at midnight
        List<CalendarAppointmentInfo> todaysAppointments = new ArrayList<>();
        for(CalendarAppointmentInfo appointment : userAppointments){
            if(appointment.getStartTime() >= todayMidnightTime && appointment.getStartTime() < nextDayMidnightTime){
                todaysAppointments.add(appointment);
            }
        }
        Collections.sort(todaysAppointments, (calendarAppointmentInfo, t1) -> Long.compare(calendarAppointmentInfo.getStartTime(),t1.getStartTime()));

        return Collections.unmodifiableList(todaysAppointments);
    }


}
