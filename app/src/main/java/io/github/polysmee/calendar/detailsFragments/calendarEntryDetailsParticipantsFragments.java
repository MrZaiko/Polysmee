package io.github.polysmee.calendar.detailsFragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import io.github.polysmee.R;
import io.github.polysmee.database.DatabaseUser;
import io.github.polysmee.database.decoys.FakeDatabaseAppointment;
import io.github.polysmee.database.decoys.FakeDatabaseUser;
import io.github.polysmee.interfaces.User;


public class calendarEntryDetailsParticipantsFragments extends Fragment {
    private ViewGroup rootView;
    private Set<String> usersIds;
    private FakeDatabaseAppointment appointment;
    private LayoutInflater fragmentInflater;
    public calendarEntryDetailsParticipantsFragments(String appointmentId){
        appointment = new FakeDatabaseAppointment(appointmentId);
        usersIds = new HashSet<>();
    }
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.rootView = (ViewGroup)inflater.inflate(R.layout.activity_calendar_entry_detail_participants_fragments, container, false);
        fragmentInflater = inflater;
        LinearLayout linearLayout = rootView.findViewById(R.id.calendarEntryDetailActivityParticipantsLayout);
        appointment.getParticipantsIdAndThen((setOfParticipants) ->{
            linearLayout.removeAllViewsInLayout();
            for(String id : setOfParticipants){
                ConstraintLayout layout = (ConstraintLayout) inflater.inflate(R.layout.activity_calendar_entry_detail_participant_box,null);
                TextView textView = layout.findViewById(R.id.calendarEntryDetailActivityUserId);
                usersIds.add(id);
                textView.setText(id);
                textView.setTextSize(20);
                Button button = layout.findViewById(R.id.calendarEntryDetailActivityBanButton);
                button.setOnClickListener((v) -> banUserButton(id));
                button.setBackgroundColor(Color.BLACK);
                linearLayout.addView(layout);
                TextView emptySpace = new TextView(rootView.getContext());
                emptySpace.setText("");
                emptySpace.setWidth(306);
                emptySpace.setHeight(20);
                linearLayout.addView(emptySpace);
            }
        });

        return rootView;
    }

    private void demoRefreshed(){
        LinearLayout linearLayout = rootView.findViewById(R.id.calendarEntryDetailActivityParticipantsLayout);
        linearLayout.removeAllViewsInLayout();
        for(String id: usersIds){
            ConstraintLayout layout = (ConstraintLayout) fragmentInflater.inflate(R.layout.activity_calendar_entry_detail_participant_box,null);
            TextView textView = layout.findViewById(R.id.calendarEntryDetailActivityUserId);
            usersIds.add(id);
            textView.setText(id);
            textView.setTextSize(20);
            Button button = layout.findViewById(R.id.calendarEntryDetailActivityBanButton);
            button.setOnClickListener((v) -> banUserButton(id));
            button.setBackgroundColor(Color.BLACK);
            linearLayout.addView(layout);
            TextView emptySpace = new TextView(rootView.getContext());
            emptySpace.setText("");
            emptySpace.setWidth(306);
            emptySpace.setHeight(20);
            linearLayout.addView(emptySpace);
        }
    }
    protected void banUserButton(String id){
        usersIds.remove(id);
        User user = new FakeDatabaseUser(id,"");
        user.removeAppointment(appointment);
        appointment.removeParticipant(user);
        demoRefreshed();
    }

}