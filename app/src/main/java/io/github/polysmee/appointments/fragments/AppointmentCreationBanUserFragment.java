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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.github.polysmee.R;
import io.github.polysmee.appointments.AppointmentActivity;
import io.github.polysmee.database.DatabaseAppointment;
import io.github.polysmee.database.DatabaseUser;
import io.github.polysmee.interfaces.Appointment;
import io.github.polysmee.interfaces.User;
import io.github.polysmee.login.MainUserSingleton;

public class AppointmentCreationBanUserFragment extends Fragment {
    View rootView;

    private EditText searchBan;
    private ImageView btnBan;
    private LinearLayout bansList;
    private Set<String> bans, removedBans;

    DataPasser dataPasser;

    private int mode;
    private Appointment appointment;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        dataPasser = (DataPasser) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_appointment_creation_ban_user, container, false);
        attributeSetters(rootView);
        return rootView;
    }

    public void reset() {
        bans.clear();
        dataPasser.dataPass(bans, AppointmentActivity.BANS);
        bansList.removeAllViews();
    }

    public void launchSetup(int mode, String appointmentID) {
        this.mode = mode;

        if (mode == AppointmentActivity.DETAIL_MODE) {
            appointment = new DatabaseAppointment(appointmentID);
        }

        btnBan.setOnClickListener(btnBanListener);
        searchBan.setHint("Type names here");

        if (mode == AppointmentActivity.DETAIL_MODE) {
            View searchLayout = rootView.findViewById(R.id.appointmentSettingsSearchBanLayout);
            searchLayout.setVisibility(View.GONE);

            appointment.getBansAndThen(p -> {
                for (String id : p) {
                    User user = new DatabaseUser(id);
                    user.getNameAndThen(this::addBan);
                }
            });

            appointment.getOwnerIdAndThen(owner -> {
                if (owner.equals(MainUserSingleton.getInstance().getId()))
                    searchLayout.setVisibility(View.VISIBLE);
            });

        }
    }


    View.OnClickListener btnBanListener = v -> {
        //For now we only get the input from the SearchView without checking it as the objective wasn't to add the database component, this will be done later
        String s = searchBan.getText().toString();
        if(!bans.contains(s) && !s.isEmpty()) {
            bans.add(s);
            dataPasser.dataPass(bans, AppointmentActivity.BANS);
            searchBan.setText("");

            if (mode == AppointmentActivity.DETAIL_MODE) {
                removedBans.remove(s);
                dataPasser.dataPass(removedBans, AppointmentActivity.REMOVED_BANS);
            }

            addBan(s);
        }
    };

    private void addBan(String name) {
        ConstraintLayout newBanLayout = (ConstraintLayout) getLayoutInflater().inflate(R.layout.element_appointment_creation, null);
        ((TextView) newBanLayout.findViewById(R.id.appointmentCreationElementText)).setText(name);

        View removeButton = newBanLayout.findViewById(R.id.appointmentCreationElementRemove);

        if (mode == AppointmentActivity.DETAIL_MODE) {
            removeButton.setVisibility(View.GONE);

            appointment.getOwnerIdAndThen(owner -> {
                if (owner.equals(MainUserSingleton.getInstance().getId()))
                    removeButton.setVisibility(View.VISIBLE);
            });
        }

        removeButton.setOnClickListener(l -> {
            bans.remove(name);
            dataPasser.dataPass(bans, AppointmentActivity.BANS);

            if (mode == AppointmentActivity.DETAIL_MODE) {
                removedBans.add(name);
                dataPasser.dataPass(removedBans, AppointmentActivity.REMOVED_BANS);
            }

            bansList.removeView(newBanLayout);
        });

        bansList.addView(newBanLayout);
    }


    private void attributeSetters(View rootView) {
        searchBan = rootView.findViewById(R.id.appointmentSettingsSearchBan);
        btnBan = rootView.findViewById(R.id.appointmentSettingsBtnBan);
        bansList = rootView.findViewById(R.id.appointmentCreationBansList);
        bans = new HashSet<>();
        removedBans = new HashSet<>();
    }
}