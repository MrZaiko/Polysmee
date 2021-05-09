package io.github.polysmee.room;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import androidx.viewpager2.adapter.FragmentStateAdapter;

import io.github.polysmee.R;
import io.github.polysmee.agora.video.Call;
import io.github.polysmee.room.fragments.RoomActivityMessagesFragment;
import io.github.polysmee.room.fragments.RoomActivityParticipantsFragment;
import io.github.polysmee.room.fragments.RoomActivityVideoFragment;

/**
 * Pager adapter that handles the room specific fragments
 */
public final class RoomPagerAdapter extends FragmentStateAdapter {
    private final String appointmentId;
    public static int[] FRAGMENT_NAME_ID = new int[]{R.string.roomTabMessagesText, R.string.roomTabVideoText, R.string.roomTabCallText};
    private final static int FRAGMENTS_NUMBER = 3;
    private final Call call;
    public RoomPagerAdapter(FragmentActivity fm, String appointmentId) {
        super(fm);
        this.appointmentId = appointmentId;
        call = new Call(appointmentId, fm.getApplicationContext());
    }


    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment fragment;
        Bundle bundle;

        switch (position) {
            case 0:
                fragment = new RoomActivityMessagesFragment();
                bundle = new Bundle();
                bundle.putString(RoomActivityMessagesFragment.MESSAGES_KEY, appointmentId);
                fragment.setArguments(bundle);
                return fragment;
            case 1:
                fragment = new RoomActivityVideoFragment(call);
                bundle = new Bundle();
                bundle.putString(RoomActivityVideoFragment.VIDEO_KEY, appointmentId);
                fragment.setArguments(bundle);
                call.setVideoFragment(fragment);
                return fragment;
            case 2:
                fragment = new RoomActivityParticipantsFragment(call);
                bundle = new Bundle();
                bundle.putString(RoomActivityParticipantsFragment.PARTICIPANTS_KEY, appointmentId);
                fragment.setArguments(bundle);
                call.setParticipantFragment(fragment);
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
