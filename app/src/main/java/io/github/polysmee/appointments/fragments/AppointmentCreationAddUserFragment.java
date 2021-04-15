package io.github.polysmee.appointments.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


import io.github.polysmee.R;
import io.github.polysmee.appointments.AppointmentActivity;
import io.github.polysmee.database.DatabaseAppointment;
import io.github.polysmee.database.DatabaseUser;
import io.github.polysmee.database.Appointment;
import io.github.polysmee.database.User;
import io.github.polysmee.interfaces.DataPasser;
import io.github.polysmee.login.MainUserSingleton;

/**
 * Fragment used by AppointmentActivity to display, add and remove participants to an appointment
 *
 * ADD_MODE     ==> ADD participant
 * DETAIL_MODE  ==> display participants and if the current user is the owner allows them to remove
 *                  them and add more
 */
public class AppointmentCreationAddUserFragment extends Fragment {
    private View rootView;

    private AutoCompleteTextView searchInvite;
    private ImageView btnInvite;
    private LinearLayout invitesList;

    private Set<String> invites, removedInvites;
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

    /**
     * store all objects on the activity (buttons, textViews...) in variables
     */
    private void attributeSetters(View rootView) {
        users = new ArrayList<>();
        User.getAllUsersIdsAndThenOnce(this::UsersNamesGetter);
        searchInvite = rootView.findViewById(R.id.appointmentSettingsSearchAdd);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_dropdown_item_1line, users);
        searchInvite.setAdapter(adapter);
        btnInvite = rootView.findViewById(R.id.appointmentSettingsBtnAdd);
        invitesList = rootView.findViewById(R.id.appointmentCreationAddsList);
        invites = new HashSet<>();
        removedInvites = new HashSet<>();
        builder = new AlertDialog.Builder(getActivity());
    }

    private void UsersNamesGetter(Set<String> allIds) {
        //This function is called at the creation of the fragment
        //So here we only get the names at the beginning of the fragment's life cycle and don't update them later
        //While this may cause small problems if a user changes their name during this time,
        //the life cycle is expected to be pretty short so it should only very rarely occur,
        // and it would take a long time to check for this edge case so we don't cover it for now
        for(String userId : allIds){
            User user = new DatabaseUser(userId);
            user.getNameAndThen((name) -> users.add(name));
        }
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

        btnInvite.setOnClickListener(this::inviteButtonBehavior);
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

    /**
     * Reset all views to their default values
     */
    public void reset() {
        invites.clear();
        dataPasser.dataPass(invites, AppointmentActivity.INVITES);
        invitesList.removeAllViews();
    }

    /**
     * ADD_MODE     =>  Add the user with the specified name to the participants list
     * DETAIL_MODE  =>  Add the user with the specified name to the participants list and remove
     *                  it from the removed participant list
     */
    private void inviteButtonBehavior(View view) {
        String s = searchInvite.getText().toString();
        if(!users.contains(s)) {
            builder.setMessage("User not found")
                    .setCancelable(false)
                    .setPositiveButton("Ok", null);

            AlertDialog alert = builder.create();
            alert.setTitle("Error");
            alert.show();
        }

        else if(!invites.contains(s)) {
            invites.add(s);
            dataPasser.dataPass(invites, AppointmentActivity.INVITES);
            searchInvite.setText("");

            if (mode == AppointmentActivity.DETAIL_MODE) {
                removedInvites.remove(s);
                dataPasser.dataPass(removedInvites, AppointmentActivity.REMOVED_INVITES);
            }

            addInvite(s);
        }
        else {
            searchInvite.setText("");
        }
    }

    /**
     * Used by inviteButtonBehavior() to display the user added with a remove button
     *      ADD_MODE    =>  REMOVE_BUTTON removes the user form the participants list
     *                      REMOVE_BUTTON:VISIBLE
     *      DETAIL_MODE =>  REMOVE_BUTTON removes the user form the participants list and adds it
     *                      to the removedParticipants list
     *              isOwner  => REMOVE_BUTTON:VISIBLE
     *              !isOwner => REMOVE_BUTTON:GONE
     * @param userName name of the added user
     */
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

}
