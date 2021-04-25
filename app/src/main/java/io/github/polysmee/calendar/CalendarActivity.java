package io.github.polysmee.calendar;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import io.github.polysmee.R;

import io.github.polysmee.invites.InvitesManagementActivity;

import io.github.polysmee.znotification.AppointmentReminderNotificationSetupListener;
import io.github.polysmee.settings.SettingsActivity;

public class CalendarActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        ViewPager2 pager = findViewById(R.id.calendarActivityPager);
        FragmentStateAdapter pagerAdapter = new CalendarActivityPagerAdapter(this);
        pager.setAdapter(pagerAdapter);

        TabLayout tabs = findViewById(R.id.calendarActivityTabs);
        new TabLayoutMediator(tabs, pager,
                (tab, position) -> tab.setText(CalendarActivityPagerAdapter.FRAGMENT_NAME[position])).attach();

        AppointmentReminderNotificationSetupListener.appointmentReminderNotificationSetListeners(
                getApplicationContext(),
                (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE));

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.calendar_menu, menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.calendarMenuSettings:
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);
                return true;
            case R.id.calendarMenuNotifications:
                Intent notificationsIntent = new Intent(this, InvitesManagementActivity.class);
                startActivity(notificationsIntent);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}