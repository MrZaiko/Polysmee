package io.github.polysmee.appointments.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.github.polysmee.R;
import io.github.polysmee.appointments.AppointmentActivity;
import io.github.polysmee.appointments.AppointmentsUtility;
import io.github.polysmee.appointments.DataPasser;
import io.github.polysmee.database.Appointment;
import io.github.polysmee.database.DatabaseAppointment;
import io.github.polysmee.database.DatabaseUser;
import io.github.polysmee.database.User;
import io.github.polysmee.login.MainUser;

/**
 * Fragment used by AppointmentActivity to display, add and remove participants to an appointment
 * <p>
 * ADD_MODE     ==> ADD participant
 * DETAIL_MODE  ==> display participants and if the current user is the owner allows them to remove
 * them and add more
 */
public class AppointmentCreationAddUserFragment extends Fragment {
    private View rootView;

    private AutoCompleteTextView searchInvite;
    private ImageView btnInvite, btnFriendInvite;
    private LinearLayout invitesList;

    private Set<String> invites, removedInvites;
    private ArrayList<String> users;
    AlertDialog.Builder builder;

    DataPasser dataPasser;

    private int mode;
    private Appointment appointment;
    private List<String> friendUsernames;


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
        User.getAllUsersIds_Once_AndThen(s -> AppointmentsUtility.usersNamesGetter(s, users));
        searchInvite = rootView.findViewById(R.id.appointmentSettingsSearchAdd);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_dropdown_item_1line, users);
        searchInvite.setAdapter(adapter);
        btnInvite = rootView.findViewById(R.id.appointmentSettingsBtnAdd);
        invitesList = rootView.findViewById(R.id.appointmentCreationAddsList);
        invites = new HashSet<>();
        removedInvites = new HashSet<>();
        builder = new AlertDialog.Builder(getActivity());
        btnFriendInvite = rootView.findViewById(R.id.appointmentSettingsBtnAddFriend);
        friendUsernames = new ArrayList<>();
        MainUser.getMainUser().getFriends_Once_And_Then((friendsIds) -> {
            for (String id : friendsIds) {
                (new DatabaseUser(id)).getName_Once_AndThen((name) -> {
                    friendUsernames.add(name);
                });
            }
        });
    }

    /**
     * Setup the fragment for a particular mode
     *
     * @param mode          DETAIL_MODE or ADD_MODE (see AppointmentActivity)
     * @param appointmentID used in DETAIL_MODE, the appointment to display participant from
     */
    public void launchSetup(int mode, String appointmentID) {
        this.mode = mode;

        if (mode == AppointmentActivity.DETAIL_MODE) {
            appointment = new DatabaseAppointment(appointmentID);
        }

        btnInvite.setOnClickListener(this::inviteButtonBehavior);
        btnFriendInvite.setOnClickListener(this::inviteFriendButtonBehavior);
        searchInvite.setHint(getString(R.string.genericNamesHintText));

        if (mode == AppointmentActivity.DETAIL_MODE) {
            View searchLayout = rootView.findViewById(R.id.appointmentSettingsSearchAddLayout);
            searchLayout.setVisibility(View.GONE);

            appointment.getParticipantsId_Once_AndThen(p -> {
                for (String id : p) {
                    User user = new DatabaseUser(id);
                    user.getName_Once_AndThen(this::addInvite);
                }
            });

            appointment.getOwnerId_Once_AndThen(owner -> {
                if (owner.equals(MainUser.getMainUser().getId()))
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

    private void inviteFriendButtonBehavior(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        if (friendUsernames.size() == 0) {
            createErrorMessage(builder, "Add some friends first :( ");
            return;
        }
        List<Integer> friendsToInvite = new ArrayList<>();
        boolean[] alreadyInvitedFriends = new boolean[friendUsernames.size()];
        for (int i = 0; i < alreadyInvitedFriends.length; ++i) {
            alreadyInvitedFriends[i] = invites.contains(friendUsernames.get(i));
        }
        builder.setTitle("Select which friend(s) to invite");
        builder.setMultiChoiceItems(friendUsernames.toArray(new CharSequence[0]), alreadyInvitedFriends, (dialog, which, isChecked) -> {
            if (isChecked) {
                friendsToInvite.add(which);
            } else if (friendsToInvite.contains(which)) {
                friendsToInvite.remove(which);
            }
        });
        builder.setPositiveButton(getString(R.string.genericOkText), (dialog, which) -> {
            for (int index : friendsToInvite) {
                String s = friendUsernames.get(index);
                if (!invites.contains(s)) {
                    addNewInvite(s);
                }
            }
        });
        builder.setNegativeButton(getString(R.string.genericCancelText), null);
        builder.create().show();
    }

    /**
     * ADD_MODE     =>  Add the user with the specified name to the participants list
     * DETAIL_MODE  =>  Add the user with the specified name to the participants list and remove
     * it from the removed participant list
     */
    private void inviteButtonBehavior(View view) {
        String s = searchInvite.getText().toString();
        if (!users.contains(s)) {
            createErrorMessage(builder, getString(R.string.genericUserNotFoundText));
        } else if (!invites.contains(s)) {
            addNewInvite(s);
        } else {
            searchInvite.setText("");
        }
    }

    /**
     * Used by inviteButtonBehavior() to display the user added with a remove button
     * ADD_MODE    =>  REMOVE_BUTTON removes the user form the participants list
     * REMOVE_BUTTON:VISIBLE
     * DETAIL_MODE =>  REMOVE_BUTTON removes the user form the participants list and adds it
     * to the removedParticipants list
     * isOwner  => REMOVE_BUTTON:VISIBLE
     * !isOwner => REMOVE_BUTTON:GONE
     *
     * @param userName name of the added user
     */
    private void addInvite(String userName) {
        ConstraintLayout newBanLayout = (ConstraintLayout) getLayoutInflater().inflate(R.layout.element_appointment_creation, null);
        ((TextView) newBanLayout.findViewById(R.id.appointmentCreationElementText)).setText(userName);

        View removeButton = newBanLayout.findViewById(R.id.appointmentCreationElementRemove);

        if (mode == AppointmentActivity.DETAIL_MODE) {
            removeButton.setVisibility(View.GONE);

            appointment.getOwnerId_Once_AndThen(owner -> {
                if (owner.equals(MainUser.getMainUser().getId()))
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

    protected void addNewInvite(String s) {
        invites.add(s);
        dataPasser.dataPass(invites, AppointmentActivity.INVITES);
        searchInvite.setText("");

        if (mode == AppointmentActivity.DETAIL_MODE) {
            removedInvites.remove(s);
            dataPasser.dataPass(removedInvites, AppointmentActivity.REMOVED_INVITES);
        }

        addInvite(s);
    }

    protected void createErrorMessage(AlertDialog.Builder builder, String errorMessage) {
        builder.setMessage(errorMessage)
                .setCancelable(false)
                .setPositiveButton(getString(R.string.genericOkText), null);

        AlertDialog alert = builder.create();
        alert.setTitle(getString(R.string.genericErrorText));
        alert.show();
    }
}
