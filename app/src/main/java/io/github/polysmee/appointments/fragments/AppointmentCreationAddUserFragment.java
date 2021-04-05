package io.github.polysmee.appointments.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;


import io.github.polysmee.R;
import io.github.polysmee.appointments.AppointmentActivity;
import io.github.polysmee.database.DatabaseAppointment;
import io.github.polysmee.database.DatabaseUser;
import io.github.polysmee.interfaces.Appointment;
import io.github.polysmee.interfaces.User;

public class AppointmentCreationAddUserFragment extends Fragment {
    private View rootView;

    private EditText searchInvite;
    private ImageView btnInvite;
    private LinearLayout invitesList;
    private ArrayList<String> invites;

    DataPasser dataPasser;

    private int mode;
    private Appointment appointment;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        dataPasser = (DataPasser) context;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_appointment_creation_add_user, container, false);
        attributeSetters(rootView);
        return rootView;
    }

    public void launchSetup(int mode, String appointmentID) {
        this.mode = mode;

        if (mode == AppointmentActivity.DETAIL_MODE) {
            appointment = new DatabaseAppointment(appointmentID);
        }

        rootView.findViewById(R.id.appointmentCreationAddUserFragmentReset).setOnClickListener(this::reset);
        btnInvite.setOnClickListener(btnInviteListener);
        searchInvite.setHint("Type names here");

        if (mode == AppointmentActivity.DETAIL_MODE) {
            rootView.findViewById(R.id.appointmentSettingsSearchAddLayout).setVisibility(View.GONE);

            appointment.getParticipantsIdAndThen(p -> {
                for (String id : p) {
                    User user = new DatabaseUser(id);
                    user.getNameAndThen(this::addInvite);
                }
            });
        }
    }

    private void reset(View view) {
        invites.clear();
        dataPasser.dataPass(invites, AppointmentActivity.INVITES);
        invitesList.removeAllViews();
    }

    View.OnClickListener btnInviteListener = v -> {
        //For now we only get the input from the SearchView without checking it as the objective wasn't to add the database component, this will be done later
        String s = searchInvite.getText().toString();
        if(!invites.contains(s) && !s.isEmpty()) {
            invites.add(s);
            dataPasser.dataPass(invites, AppointmentActivity.INVITES);
            searchInvite.setText("");

            addInvite(s);
        }
    };

    private void addInvite(String userName) {
        ConstraintLayout newBanLayout = (ConstraintLayout) getLayoutInflater().inflate(R.layout.element_appointment_creation, null);
        ((TextView) newBanLayout.findViewById(R.id.appointmentCreationElementText)).setText(userName);

        View removeButton = newBanLayout.findViewById(R.id.appointmentCreationElementRemove);

        if (mode == AppointmentActivity.DETAIL_MODE)
            removeButton.setVisibility(View.GONE);

        removeButton.setOnClickListener(l -> {
            invites.remove(userName);
            dataPasser.dataPass(invites, AppointmentActivity.INVITES);
            invitesList.removeView(newBanLayout);
        });

        invitesList.addView(newBanLayout);
    }

    private void attributeSetters(View rootView) {
        searchInvite = rootView.findViewById(R.id.appointmentSettingsSearchAdd);
        btnInvite = rootView.findViewById(R.id.appointmentSettingsBtnAdd);
        invitesList = rootView.findViewById(R.id.appointmentCreationAddsList);
        invites = new ArrayList<>();
    }
}
