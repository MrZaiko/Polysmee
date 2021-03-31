package io.github.polysmee.appointments.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Switch;

import java.util.ArrayList;
import java.util.List;

import io.github.polysmee.R;
import io.github.polysmee.appointments.AppointmentActivity;

public class AptCreationSettingsFragment extends Fragment {
    private Switch switchPrivate;
    private SearchView searchInvite, searchBan;
    private Button btnInvite, btnBan, btnCheckInvites, btnCheckBans;
    private ArrayList<String> invites;
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
        View rootView = inflater.inflate(R.layout.fragment_appointment_creation_add_user, container, false);

        attributeSetters(rootView);
        btnInvite.setOnClickListener(btnInviteListener);
        btnBan.setOnClickListener(btnBanListener);
        btnCheckInvites.setOnClickListener(btnSeeInvitesListener);
        btnCheckBans.setOnClickListener(btnSeeBansListener);
        switchPrivate.setOnCheckedChangeListener(switchPrivateListener);
        searchInvite.setQueryHint("Type names here");
        searchBan.setQueryHint("Type names here");

        return rootView;
    }

    View.OnClickListener btnInviteListener = v -> {
        //For now we only get the input from the SearchView without checking it as the objective wasn't to add the database component, this will be done later
        String s = searchInvite.getQuery().toString();
        if(!invites.contains(s) && !s.isEmpty()) {
            invites.add(s);
            dataPasser.dataPass(invites, AppointmentActivity.INVITES);
            int id = searchInvite.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
            EditText txtInvite = searchInvite.findViewById(id);
            txtInvite.setText("");
        }
    };

    View.OnClickListener btnBanListener = v -> {
        //For now we only get the input from the SearchView without checking it as the objective wasn't to add the database component, this will be done later
        String s = searchBan.getQuery().toString();
        if(!bans.contains(s) && !s.isEmpty()) {
            bans.add(s);
            dataPasser.dataPass(bans, AppointmentActivity.BANS);
            int id = searchBan.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
            EditText txtBan = searchBan.findViewById(id);
            txtBan.setText("");
        }
    };

    View.OnClickListener btnSeeInvitesListener = v -> {
        //For now we only get the input from the SearchView without checking it as the objective wasn't to add the database component, this will be done later
        showUsersList(invites, "invites", AppointmentActivity.INVITES);
    };

    View.OnClickListener btnSeeBansListener = v -> {
        //For now we only get the input from the SearchView without checking it as the objective wasn't to add the database component, this will be done later
        showUsersList(bans, "bans", AppointmentActivity.BANS);
    };

    CompoundButton.OnCheckedChangeListener switchPrivateListener = (buttonView, isChecked) -> {
        if (isChecked) {
            isPrivate = true;
            dataPasser.dataPass(isPrivate, AppointmentActivity.PRIVATE);
        }
        else {
            isPrivate = false;
            dataPasser.dataPass(isPrivate, AppointmentActivity.PRIVATE);
        }
    };

    private void attributeSetters(View rootView) {
        switchPrivate = rootView.findViewById(R.id
        searchBan = rootView.findViewById(R.id.appointmentSettingsSearchBan);
        searchInvite = rootView.findViewById(R.id.appointmentSettingsSearchInvite);
        btnInvite = rootView.findViewById(R.id.appointmentSettingsBtnInvite);
        btnBan = rootView.findViewById(R.id.appointmentSettingsBtnBan);
        btnCheckInvites = rootView.findViewById(R.id.appointmentSettingsBtnSeeInvites);
        btnCheckBans = rootView.findViewById(R.id.appointmentSettingsBtnSeeBans);
        invites = new ArrayList<>();
        bans = new ArrayList<>();
        isPrivate = false;
    }

    private void showUsersList(ArrayList<String> users, String type, String id) {
        List<Integer> selectedItems = new ArrayList<>();

        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Select which " + type + " to remove");
        builder.setMultiChoiceItems(users.toArray(new CharSequence[0]), null, (dialog, which, isChecked) -> {
            if (isChecked) {
                // If the user checked the item, add it to the selected items
                selectedItems.add(which);
            } else if (selectedItems.contains(which)) {
                // Else, if the item is already in the array, remove it
                selectedItems.remove(Integer.valueOf(which));
            }
        });

        // add OK and Cancel buttons
        builder.setPositiveButton("OK", (dialog, which) -> {
            for (int i : selectedItems) {
                users.remove(i);
                dataPasser.dataPass(users, id);
            }
        });
        builder.setNegativeButton("Cancel", null);

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}