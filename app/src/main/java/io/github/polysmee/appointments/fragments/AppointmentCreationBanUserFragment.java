package io.github.polysmee.appointments.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SearchView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import io.github.polysmee.R;
import io.github.polysmee.appointments.AppointmentActivity;

public class AppointmentCreationBanUserFragment extends Fragment {
    private EditText searchBan;
    private ImageView btnBan;
    private LinearLayout bansList;
    private ArrayList<String> bans;
    private boolean isPrivate;

    DataPasser dataPasser;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        dataPasser = (DataPasser) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_appointment_creation_ban_user, container, false);

        attributeSetters(rootView);
        btnBan.setOnClickListener(btnBanListener);
        searchBan.setHint("Type names here");

        return rootView;
    }


    View.OnClickListener btnBanListener = v -> {
        //For now we only get the input from the SearchView without checking it as the objective wasn't to add the database component, this will be done later
        String s = searchBan.getText().toString();
        if(!bans.contains(s) && !s.isEmpty()) {
            bans.add(s);
            dataPasser.dataPass(bans, AppointmentActivity.BANS);
            searchBan.setText("");
            addBan(s);
        }
    };

    private void addBan(String name) {
        ConstraintLayout newBanLayout = (ConstraintLayout) getLayoutInflater().inflate(R.layout.element_appointment_creation, null);
        ((TextView) newBanLayout.findViewById(R.id.appointmentCreationElementText)).setText(name);
        newBanLayout.findViewById(R.id.appointmentCreationElementRemove).setOnClickListener(l -> {
            bans.remove(name);
            dataPasser.dataPass(bans, AppointmentActivity.BANS);
            bansList.removeView(newBanLayout);
        });
        bansList.addView(newBanLayout);
    }


    private void attributeSetters(View rootView) {
        searchBan = rootView.findViewById(R.id.appointmentSettingsSearchBan);
        btnBan = rootView.findViewById(R.id.appointmentSettingsBtnBan);
        bansList = rootView.findViewById(R.id.appointmentCreationBansList);
        bans = new ArrayList<>();
        isPrivate = false;
    }
}