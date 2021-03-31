package io.github.polysmee.appointments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import io.github.polysmee.appointments.fragments.AppointmentCreationAddUserFragment;
import io.github.polysmee.appointments.fragments.AppointmentCreationBanUserFragment;


public class AptCreationPagerAdapter extends FragmentStateAdapter {
    public static String[] FRAGMENT_NAME = new String[]{"Invite user", "Ban user"};
    private final static int FRAGMENTS_NUMBER = 2;

    public AptCreationPagerAdapter(FragmentActivity fm) {
        super(fm);
    }


    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment fragment;

        switch (position) {

            case 0:
                fragment = new AppointmentCreationAddUserFragment();
                return fragment;
            case 1:
                fragment = new AppointmentCreationBanUserFragment();
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