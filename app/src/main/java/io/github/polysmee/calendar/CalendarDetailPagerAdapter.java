package io.github.polysmee.calendar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.List;

public class CalendarDetailPagerAdapter extends FragmentStateAdapter {

    private final List<Fragment> fragmentList;
    public CalendarDetailPagerAdapter(FragmentActivity fm, List<Fragment> list) {
        super(fm);
        this.fragmentList = list;
    }
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getItemCount() {
        return fragmentList.size();
    }
}
