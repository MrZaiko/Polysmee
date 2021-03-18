package io.github.polysmee.calendar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

/**
 * This class will be used to show the user his daily appointments on
 * the base of all his appointments through static methods
 */
public class DailyCalendar {


    private DailyCalendar(){
        //private constructor to prevent creating instances of this class
    }

    /**
     * Gets today's time at midnight in seconds, according to the EPOCH standard.
     * @return the time at midnight in seconds
     */
    public static long todayEpochTimeAtMidnight(){
        Calendar calendar =  Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY,0);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);
        calendar.set(Calendar.MILLISECOND,0);
        return calendar.getTimeInMillis()/1000; //get today's time at midnight in seconds epoch
    }

    public static boolean appointmentIsToday(long startTime){
        long midnightTime = todayEpochTimeAtMidnight();
        return startTime >= midnightTime && startTime < (midnightTime + 3600*24);
    }

    /**
     * For a given set of user appointments, gets the ones that are programmed to happen
     * on the day the calendar is accessed. The appointments are ordered according
     * to their start time.
     * @param userAppointments set of appointments of the user.
     * @throws IllegalArgumentException if the set given as argument is null
     * @return the list of ordered appointments of the user for the day
     */
    public static List<CalendarAppointmentInfo> getAppointmentsForTheDay(Set<CalendarAppointmentInfo> userAppointments){
        if(userAppointments == null)
            throw new IllegalArgumentException();
        long todayMidnightTime = todayEpochTimeAtMidnight();
        long nextDayMidnightTime = todayMidnightTime + 24 * 3600; //get the epoch time in seconds of next day at midnight
        List<CalendarAppointmentInfo> todaysAppointments = new ArrayList<>();
        for(CalendarAppointmentInfo appointment : userAppointments){
            if(appointment.getStartTime() >= todayMidnightTime && appointment.getStartTime() < nextDayMidnightTime){
                todaysAppointments.add(appointment);
            }
        }
        Collections.sort(todaysAppointments, new Comparator<CalendarAppointmentInfo>() {
            @Override
            public int compare(CalendarAppointmentInfo calendarAppointmentInfo, CalendarAppointmentInfo t1) {
                return Long.compare(calendarAppointmentInfo.getStartTime(),t1.getStartTime());
            }
        });

        return Collections.unmodifiableList(todaysAppointments);
    }


}
