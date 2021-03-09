package io.github.polysmee.room.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.github.polysmee.R;
import io.github.polysmee.interfaces.User;

public class roomActivityParticipantsFragment extends Fragment {

    private ViewGroup rootView;
    private Set<User> participants;

    public roomActivityParticipantsFragment(Set<User> participants) {
        this.participants = participants != null ? participants : new HashSet<>();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.rootView = (ViewGroup)inflater.inflate(R.layout.activity_room_participant_fragment, container, false);
        generateParticipantsView();
        return rootView;
    }

    @Override
    public String toString() {
        return "Participants";
    }

    public void generateParticipantsView() {
        LinearLayout layout = rootView.findViewById(R.id.roomActivityParticipantsLayout);
        for (User user: participants) {
            TextView participant = new TextView(rootView.getContext());
            participant.setText(user.getName() + " " + user.getSurname());
            participant.setTextSize(20);
            participant.setBackgroundColor(Color.GRAY);
            layout.addView(participant);
            layout.addView(new TextView(rootView.getContext()));
        }
    }
}
