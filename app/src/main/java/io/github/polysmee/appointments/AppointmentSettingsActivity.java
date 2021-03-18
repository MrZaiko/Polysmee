package io.github.polysmee.appointments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.SearchView;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import io.github.polysmee.R;

public class AppointmentSettingsActivity extends AppCompatActivity {
    private Switch switchPrivate;
    private SearchView searchInvite, searchBan;
    private Button btnInvite, btnBan, btnDone;
    private List<String> invites;
    private List<String> bans;
    private boolean isPrivate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_settings);

        attributeSetters();
        btnInvite.setOnClickListener(btnInviteListener);
        btnBan.setOnClickListener(btnBanListener);
        btnDone.setOnClickListener(btnDoneListener);
        switchPrivate.setOnCheckedChangeListener(switchPrivateListener);
    }

    View.OnClickListener btnInviteListener = v -> {
        //For now we only get the input from the SearchView without checking it as the objective wasn't to add the database component, this will be done later
        String s = searchInvite.getQuery().toString();
        if(!invites.contains(s)) {
            invites.add(s);
        }
    };

    View.OnClickListener btnBanListener = v -> {
        //For now we only get the input from the SearchView without checking it as the objective wasn't to add the database component, this will be done later
        String s = searchBan.getQuery().toString();
        if(!bans.contains(s)) {
            bans.add(s);
        }
    };

    View.OnClickListener btnDoneListener = v -> {
        //Create the return intent and attach all necessary information to it before finishing
        Intent returnIntent = new Intent();
        returnIntent.putExtra("private", isPrivate);
        returnIntent.putStringArrayListExtra("invites", (ArrayList<String>) invites);
        returnIntent.putStringArrayListExtra("bans", (ArrayList<String>) bans);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    };

    CompoundButton.OnCheckedChangeListener switchPrivateListener = (buttonView, isChecked) -> {
        if (isChecked) {
            isPrivate = true;
        }
        else {
            isPrivate = false;
        }
    };

    private void attributeSetters() {
        switchPrivate = findViewById(R.id.appointmentSettingsSwitchPrivate);
        searchBan = findViewById(R.id.appointmentSettingsSearchBan);
        searchInvite = findViewById(R.id.appointmentSettingsSearchInvite);
        btnInvite = findViewById(R.id.appointmentSettingsBtnInvite);
        btnBan = findViewById(R.id.appointmentSettingsBtnBan);
        btnDone = findViewById(R.id.appointmentSettingsBtnDone);
        invites = new ArrayList<>();
        bans = new ArrayList<>();
        isPrivate = false;
    }
}