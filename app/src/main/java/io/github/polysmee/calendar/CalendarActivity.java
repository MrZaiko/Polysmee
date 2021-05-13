package io.github.polysmee.calendar;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import io.github.polysmee.R;
import io.github.polysmee.agora.Command;
import io.github.polysmee.internet.connection.InternetConnection;
import io.github.polysmee.invites.InvitesManagementActivity;
import io.github.polysmee.login.MainUser;
import io.github.polysmee.profile.ProfileActivity;
import io.github.polysmee.settings.SettingsActivity;
import io.github.polysmee.znotification.AppointmentReminderNotification;

public class CalendarActivity extends AppCompatActivity {

    private ActivityResultLauncher<String> requestPermissionLauncher;

    private MenuItem wifiLogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        boolean isDarkMode = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean(getApplicationContext().getResources().getString(R.string.preference_key_is_dark_mode), false);
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        ViewPager2 pager = findViewById(R.id.calendarActivityPager);
        FragmentStateAdapter pagerAdapter = new CalendarActivityPagerAdapter(this);
        pager.setAdapter(pagerAdapter);

        TabLayout tabs = findViewById(R.id.calendarActivityTabs);
        new TabLayoutMediator(tabs, pager,
                (tab, position) -> tab.setText(getString(CalendarActivityPagerAdapter.FRAGMENT_NAME_ID[position]))).attach();

        AppointmentReminderNotification.appointmentReminderNotificationSetListeners(this);

        initializePermissionRequester();
    }

    /**
     * Initializes the request permission requester
     */
    private void initializePermissionRequester() {
        requestPermissionLauncher =
                this.registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                    if (isGranted) {
                        checkCalendarPerms();
                    }
                });
    }

    /**
     * @param permission the permission we're checking
     * @return true if the permission given is granted by the user and false otherwise
     */
    private boolean checkPermission(String permission) {
        return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED;
    }

    public boolean checkCalendarPerms() {
        if (!checkPermission(Manifest.permission.WRITE_CALENDAR)) {
            requestPermissionLauncher.launch(Manifest.permission.WRITE_CALENDAR);
            return false;
        }

        if (!checkPermission(Manifest.permission.READ_CALENDAR)) {
            requestPermissionLauncher.launch(Manifest.permission.READ_CALENDAR);
            return false;
        }

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        if(wifiLogo != null) {
            wifiLogo.setVisible(!InternetConnection.isOn());
            InternetConnection.setCommand(((value, key) -> runOnUiThread(() -> wifiLogo.setVisible(key))));
        }


        super.onResume();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.calendar_menu, menu);
        wifiLogo = menu.findItem(R.id.calendarMenuOffline);
        if(InternetConnection.isOn()) {
            wifiLogo.setVisible(false);
        }
        InternetConnection.setCommand(((value, key) -> runOnUiThread(() -> wifiLogo.setVisible(key))));
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.calendarMenuNotifications);
        MainUser.getMainUser().getInvitesAndThen(s -> {
            if (!s.isEmpty()) {
                item.setIcon(R.drawable.baseline_notification_active);
            } else {
                item.setIcon(R.drawable.baseline_notifications);
            }
        });
        return super.onPrepareOptionsMenu(menu);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.calendarMenuProfile:
                Intent profileIntent = new Intent(this, ProfileActivity.class);
                profileIntent.putExtra(ProfileActivity.PROFILE_VISIT_CODE, ProfileActivity.PROFILE_OWNER_MODE);
                startActivity(profileIntent);
                return true;
            case R.id.calendarMenuSettings:
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);
                return true;
            case R.id.calendarMenuNotifications:
                Intent notificationsIntent = new Intent(this, InvitesManagementActivity.class);
                startActivity(notificationsIntent);
                return true;
            /*case R.id.calendarMenuExport:
                Intent exportIntent = new Intent(this, CalendarExportActivity.class);
                startActivity(exportIntent);
                return true;*/
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}