package io.github.polysmee.appointments.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import io.github.polysmee.R;
import io.github.polysmee.appointments.AppointmentActivity;
import io.github.polysmee.appointments.AppointmentsUtility;
import io.github.polysmee.database.DatabaseAppointment;
import io.github.polysmee.database.DatabaseUser;
import io.github.polysmee.database.Appointment;
import io.github.polysmee.appointments.DataPasser;
import io.github.polysmee.database.User;
import io.github.polysmee.login.MainUser;

/**
 * Fragment used by AppointmentActivity to display, add and remove banned participants to an appointment
 *
 * ADD_MODE     ==> Ban participant
 * DETAIL_MODE  ==> display participants and if the current user is the owner allows them to remove
 *                  them and add more
 */
public class AppointmentCreationBanUserFragment extends Fragment {
    View rootView;

    private AutoCompleteTextView searchBan;
    private ImageView btnBan;
    private LinearLayout bansList;
    private Set<String> bans, removedBans;
    private ArrayList<String> users;
    AlertDialog.Builder builder;

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

    /**
     * store all objects on the activity (buttons, textViews...) in variables
     */
    private void attributeSetters(View rootView) {
        users = new ArrayList<>();
        User.getAllUsersIds_Once_AndThen(s -> AppointmentsUtility.usersNamesGetter(s, users));
        searchBan = rootView.findViewById(R.id.appointmentSettingsSearchBan);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_dropdown_item_1line, users);
        searchBan.setAdapter(adapter);
        btnBan = rootView.findViewById(R.id.appointmentSettingsBtnBan);
        bansList = rootView.findViewById(R.id.appointmentCreationBansList);
        bans = new HashSet<>();
        removedBans = new HashSet<>();
        builder = new AlertDialog.Builder(getActivity());
    }

    /**
     * Reset all views to their default values
     */
    public void reset() {
        bans.clear();
        dataPasser.dataPass(bans, AppointmentActivity.BANS);
        bansList.removeAllViews();
    }

    /**
     * Setup the fragment for a particular mode
     *
     * @param mode DETAIL_MODE or ADD_MODE (see AppointmentActivity)
     * @param appointmentID used in DETAIL_MODE, the appointment to display participant from
     */
    public void launchSetup(int mode, String appointmentID) {
        this.mode = mode;

        if (mode == AppointmentActivity.DETAIL_MODE) {
            appointment = new DatabaseAppointment(appointmentID);
        }

        btnBan.setOnClickListener(this::banButtonBehavior);
        searchBan.setHint(getString(R.string.genericNamesHintText));

        if (mode == AppointmentActivity.DETAIL_MODE) {
            View searchLayout = rootView.findViewById(R.id.appointmentSettingsSearchBanLayout);
            searchLayout.setVisibility(View.GONE);

            appointment.getBans_Once_AndThen(p -> {
                for (String id : p) {
                    User user = new DatabaseUser(id);
                    user.getNameAndThen(this::addBan);
                }
            });

            appointment.getOwnerId_Once_AndThen(owner -> {
                if (owner.equals(MainUser.getMainUser().getId()))
                    searchLayout.setVisibility(View.VISIBLE);
            });

        }
    }

    /**
     * ADD_MODE     =>  Ban the user with the specified name to the banned participants list
     * DETAIL_MODE  =>  Ban the user with the specified name to the banned participants list and remove
     *                  it from the removed banned participant list
     */
    private void banButtonBehavior(View view) {
        String s = searchBan.getText().toString();
        if(!users.contains(s)) {
            builder.setMessage(getString(R.string.genericUserNotFoundText))
                    .setCancelable(false)
                    .setPositiveButton(getString(R.string.genericOkText), null);

            AlertDialog alert = builder.create();
            alert.setTitle(getString(R.string.genericErrorText));
            alert.show();
        }

        else if(!bans.contains(s)) {
            bans.add(s);
            dataPasser.dataPass(bans, AppointmentActivity.BANS);
            searchBan.setText("");

            if (mode == AppointmentActivity.DETAIL_MODE) {
                removedBans.remove(s);
                dataPasser.dataPass(removedBans, AppointmentActivity.REMOVED_BANS);
            }

            addBan(s);
        }
        else {
            searchBan.setText("");
        }
    }

    /**
     * Used by inviteButtonBehavior() to display the banned user with a remove button
     *      ADD_MODE    =>  REMOVE_BUTTON removes the user form the participants list
     *                      REMOVE_BUTTON:VISIBLE
     *      DETAIL_MODE =>  REMOVE_BUTTON removes the user form the banned participants list and adds it
     *                      to the removedBannedParticipants list
     *              isOwner  => REMOVE_BUTTON:VISIBLE
     *              !isOwner => REMOVE_BUTTON:GONE
     * @param name name of the banned user
     */
    private void addBan(String name) {
        ConstraintLayout newBanLayout = (ConstraintLayout) getLayoutInflater().inflate(R.layout.element_appointment_creation, null);
        ((TextView) newBanLayout.findViewById(R.id.appointmentCreationElementText)).setText(name);

        View removeButton = newBanLayout.findViewById(R.id.appointmentCreationElementRemove);

        if (mode == AppointmentActivity.DETAIL_MODE) {
            removeButton.setVisibility(View.GONE);

            appointment.getOwnerIdAndThen(owner -> {
                if (owner.equals(MainUser.getMainUser().getId()))
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
}