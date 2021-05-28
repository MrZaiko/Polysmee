package io.github.polysmee.calendar.fragments;

import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import io.github.polysmee.R;
import io.github.polysmee.appointments.AppointmentActivity;
import io.github.polysmee.calendar.DailyCalendar;

public class CalendarActivityFragmentsHelpers {

    private CalendarActivityFragmentsHelpers() {

    }
/**
 * Sets the text view on top of the chosen calendar fragment to the current day's date
 */
    /**
     * Sets the text view on top of the chosen calendar fragment to the current day's date
     * @param rootView the rootView containing the date layout
     * @param publicApp determines if it's the public appointments fragment or not, so we can pass the right ids
     */
    public static void setDayText(ViewGroup rootView, boolean publicApp) {
        ConstraintLayout dateLayout;
        TextView day;
        TextView month;
        if (!publicApp) {
            dateLayout = rootView.findViewById(R.id.todayDateMyAppointmentsCalendarActivity);
            day = dateLayout.findViewById(R.id.activityCalendarDayMyAppointments);
            month = dateLayout.findViewById(R.id.activityCalendarMonthMyAppointments);
        } else {
            dateLayout = rootView.findViewById(R.id.todayDatePublicAppointmentsCalendarActivity);
            day = dateLayout.findViewById(R.id.activityCalendarDayPublicAppointments);
            month = dateLayout.findViewById(R.id.activityCalendarMonthPublicAppointments);
        }
        long epochTimeToday = DailyCalendar.getDayEpochTimeAtMidnight(publicApp);
        Date today = new Date(epochTimeToday);

        SimpleDateFormat dayFormat = new SimpleDateFormat("d", Locale.getDefault());
        day.setText(dayFormat.format(today));

        SimpleDateFormat monthFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
        month.setText(monthFormat.format(today));
    }

    /**
     * Sets the date to the day when the user launches the app at startup
     *
     * @param publicApp boolean that decides which value is set; the one for the public calendar,
     *                  or the one for the personal one
     */
    public static void setTodayDateInDailyCalendar(boolean publicApp) {
        Calendar calendar = Calendar.getInstance();
        DailyCalendar.setDayEpochTimeAtMidnight(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE), publicApp);
    }

    /**
     * Method that will show the details for the appointment with the given id.
     * It will launch when clicking on the appointment's calendar entry when the appointment
     * hasn't begun yet.
     *
     * @param id               the appointment of interest' id
     * @param calendarFragment the calendar fragment this method will be launched from
     * @param rootView         the fragment's rootview; needed to get the context to launch the intent.
     */
    public static void goToAppointmentDetails(String id, Fragment calendarFragment, ViewGroup rootView) {
        Intent intent = new Intent(rootView.getContext(), AppointmentActivity.class);
        intent.putExtra(AppointmentActivity.LAUNCH_MODE, AppointmentActivity.DETAIL_MODE);
        intent.putExtra(AppointmentActivity.APPOINTMENT_ID, id);
        calendarFragment.startActivity(intent);
    }

    /**
     * Sets the image status of the appointment depending on if the appointment has ended, has yet to begin, or is happening
     * right now
     * @param status the imageview containing the image to update
     * @param current the current time
     * @param startDate the appointment's start time
     * @param endDate the appointment's end time
     */
    public static void setStatusImage(ImageView status, Date current, Date startDate , Date endDate){
        if (current.before(startDate))
            status.setImageResource(R.drawable.calendar_entry_incoming_dot);
        else if (current.after(endDate))
            status.setImageResource(R.drawable.calendar_entry_done_dot);
        else
            status.setImageResource(R.drawable.calendar_entry_ongoing_dot);
    }


}
