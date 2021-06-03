package io.github.polysmee.invites;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import io.github.polysmee.R;

@RequiresApi(api = Build.VERSION_CODES.N)
public class InvitesManagementActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invites_management);
        ViewPager2 pager = findViewById(R.id.invitesManagementActivityPager);
        FragmentStateAdapter pagerAdapter = new InvitesManagementActivityPagerAdapter(this);
        pager.setAdapter(pagerAdapter);

        TabLayout tabs = findViewById(R.id.invitesManagementActivityTabs);
        new TabLayoutMediator(tabs, pager,
                (tab, position) -> tab.setText(getString(InvitesManagementActivityPagerAdapter.FRAGMENT_NAME_ID[position]))).attach();
    }

}