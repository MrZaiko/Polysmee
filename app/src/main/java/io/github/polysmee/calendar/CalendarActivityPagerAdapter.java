package io.github.polysmee.calendar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import io.github.polysmee.R;
import io.github.polysmee.calendar.calendarActivityFragments.CalendarActivityMyAppointmentsFragment;
import io.github.polysmee.calendar.calendarActivityFragments.CalendarActivityPublicAppointmentsFragment;

public class CalendarActivityPagerAdapter extends FragmentStateAdapter {

    public static int[] FRAGMENT_NAME_ID = new int[]{R.string.calendarActivityMyAppointments, R.string.calendarActivityPublicAppointments};
    private final static int FRAGMENTS_NUMBER = 2;

    public CalendarActivityPagerAdapter(FragmentActivity fm) {
        super(fm);
    }
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment fragment;
        switch (position){
            case 0:
                fragment = new CalendarActivityMyAppointmentsFragment();
                return fragment;
            case 1:
                fragment = new CalendarActivityPublicAppointmentsFragment();
                return fragment;
            default:
                throw new IllegalArgumentException("There are only two fragments possible for the calendar activity.");
        }
    }

    @Override
    public int getItemCount() {
        return FRAGMENTS_NUMBER;
    }
}
