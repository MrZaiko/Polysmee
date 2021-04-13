package io.github.polysmee.appointments.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

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

    /**
     * store all objects on the activity (buttons, textViews...) in variables
     */
    private void attributeSetters(View rootView) {
        searchInvite = rootView.findViewById(R.id.appointmentSettingsSearchAdd);
        btnInvite = rootView.findViewById(R.id.appointmentSettingsBtnAdd);
        invitesList = rootView.findViewById(R.id.appointmentCreationAddsList);
        invites = new HashSet<>();
        removedInvites = new HashSet<>();
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
