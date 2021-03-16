package io.github.polysmee.room;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import androidx.viewpager2.adapter.FragmentStateAdapter;

import io.github.polysmee.room.fragments.RoomActivityMessagesFragment;
import io.github.polysmee.room.fragments.RoomActivityParticipantsFragment;

/**
 * Pager adapter that handles the room specific fragments
 */
public class RoomPagerAdapter extends FragmentStateAdapter {
    private final String appointmentId;
    public static String[] FRAGMENT_NAME = new String[]{"MESSAGES", "PARTICIPANTS"};
    private final static int FRAGMENTS_NUMBER = 2;

    public RoomPagerAdapter(FragmentActivity fm, String appointmentId) {
        super(fm);
        this.appointmentId = appointmentId;
    }


    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new RoomActivityMessagesFragment();
            case 1:
                Fragment fragment = new RoomActivityParticipantsFragment();
                Bundle bundle = new Bundle();
                bundle.putString(RoomActivityParticipantsFragment.PARTICIPANTS_KEY, appointmentId);
                fragment.setArguments(bundle);
                return fragment;
            default:
                throw new IllegalArgumentException();
        }
    }

    @Override
    public int getItemCount() {
        return FRAGMENTS_NUMBER;
    }
}
