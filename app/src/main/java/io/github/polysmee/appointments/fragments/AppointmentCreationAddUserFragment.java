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
import java.util.HashSet;
import java.util.Set;


import io.github.polysmee.R;
import io.github.polysmee.appointments.AppointmentActivity;
import io.github.polysmee.database.DatabaseAppointment;
import io.github.polysmee.database.DatabaseUser;
import io.github.polysmee.interfaces.Appointment;
import io.github.polysmee.interfaces.User;
import io.github.polysmee.login.MainUserSingleton;

public class AppointmentCreationAddUserFragment extends Fragment {
    private View rootView;

    private EditText searchInvite;
    private ImageView btnInvite;
    private LinearLayout invitesList;
    private Set<String> invites, removedInvites;

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

        btnInvite.setOnClickListener(btnInviteListener);
        searchInvite.setHint("Type names here");

        if (mode == AppointmentActivity.DETAIL_MODE) {
            View searchLayout = rootView.findViewById(R.id.appointmentSettingsSearchAddLayout);
            searchLayout.setVisibility(View.GONE);

            appointment.getParticipantsIdAndThen(p -> {
                for (String id : p) {
                    User user = new DatabaseUser(id);
                    user.getNameAndThen(this::addInvite);
                }
            });

            appointment.getOwnerIdAndThen(owner -> {
                if (owner.equals(MainUserSingleton.getInstance().getId()))
                    searchLayout.setVisibility(View.VISIBLE);
            });
        }
    }

    public void reset() {
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

            if (mode == AppointmentActivity.DETAIL_MODE) {
                removedInvites.remove(s);
                dataPasser.dataPass(removedInvites, AppointmentActivity.REMOVED_INVITES);
            }

            addInvite(s);
        }
    };

    private void addInvite(String userName) {
        ConstraintLayout newBanLayout = (ConstraintLayout) getLayoutInflater().inflate(R.layout.element_appointment_creation, null);
        ((TextView) newBanLayout.findViewById(R.id.appointmentCreationElementText)).setText(userName);

        View removeButton = newBanLayout.findViewById(R.id.appointmentCreationElementRemove);

        if (mode == AppointmentActivity.DETAIL_MODE) {
            removeButton.setVisibility(View.GONE);

            appointment.getOwnerIdAndThen(owner -> {
                if (owner.equals(MainUserSingleton.getInstance().getId()))
                    removeButton.setVisibility(View.VISIBLE);
            });
        }

        removeButton.setOnClickListener(l -> {
            invites.remove(userName);
            dataPasser.dataPass(invites, AppointmentActivity.INVITES);

            if (mode == AppointmentActivity.DETAIL_MODE) {
                removedInvites.add(userName);
                dataPasser.dataPass(removedInvites, AppointmentActivity.REMOVED_INVITES);
            }

            invitesList.removeView(newBanLayout);
        });

        invitesList.addView(newBanLayout);
    }

    private void attributeSetters(View rootView) {
        searchInvite = rootView.findViewById(R.id.appointmentSettingsSearchAdd);
        btnInvite = rootView.findViewById(R.id.appointmentSettingsBtnAdd);
        invitesList = rootView.findViewById(R.id.appointmentCreationAddsList);
        invites = new HashSet<>();
        removedInvites = new HashSet<>();
    }
}
