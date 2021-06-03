package io.github.polysmee.invites;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import io.github.polysmee.R;
import io.github.polysmee.invites.fragments.AppointmentsInvitesFragment;
import io.github.polysmee.invites.fragments.FriendInvitesFragment;

public class InvitesManagementActivityPagerAdapter extends FragmentStateAdapter {

    public static int[] FRAGMENT_NAME_ID = new int[]{R.string.invites_management_current_invitations_appointments, R.string.invites_management_current_invitations_Friends};
    private final static int FRAGMENTS_NUMBER = 2;

    public InvitesManagementActivityPagerAdapter(FragmentActivity fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment fragment;
        switch (position) {
            case 0:
                fragment = new AppointmentsInvitesFragment();
                return fragment;
            case 1:
                fragment = new FriendInvitesFragment();
                return fragment;
            default:
                throw new IllegalArgumentException("There are only two fragments possible for the invites activity.");
        }
    }

    @Override
    public int getItemCount() {
        return FRAGMENTS_NUMBER;
    }
}
