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

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;


import io.github.polysmee.R;
import io.github.polysmee.appointments.AppointmentActivity;

public class AppointmentCreationAddUserFragment extends Fragment {
    private EditText searchInvite;
    private ImageView btnInvite;
    private LinearLayout invitesList;
    private ArrayList<String> invites;

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
        searchInvite.setHint("Type names here");

        return rootView;
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
        newBanLayout.findViewById(R.id.appointmentCreationElementRemove).setOnClickListener(l -> {
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
