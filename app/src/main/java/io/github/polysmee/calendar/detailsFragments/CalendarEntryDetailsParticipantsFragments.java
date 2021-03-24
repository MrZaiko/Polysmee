package io.github.polysmee.calendar.detailsFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import io.github.polysmee.R;
import io.github.polysmee.calendar.CalendarActivity;
import io.github.polysmee.database.DatabaseAppointment;
import io.github.polysmee.database.DatabaseUser;
import io.github.polysmee.database.decoys.FakeDatabaseAppointment;
import io.github.polysmee.interfaces.Appointment;
import io.github.polysmee.interfaces.User;

public class CalendarEntryDetailsParticipantsFragments extends Fragment {
    private ViewGroup rootView;

    private String appointmentId;
    private Appointment appointment;

    public static String APPOINTMENT_DETAIL_PARTICIPANT_ID = "APPOINTMENT_DETAIL_PARTICIPANT_ID";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.rootView = (ViewGroup)inflater.inflate(R.layout.activity_calendar_entry_detail_participants_fragments, container, false);

        LinearLayout linearLayout = rootView.findViewById(R.id.calendarEntryDetailActivityParticipantsLayout);

        Bundle bundle = this.getArguments();
        String userType = (String)bundle.getSerializable(CalendarActivity.UserTypeCode);
        appointmentId = (String)bundle.getSerializable(APPOINTMENT_DETAIL_PARTICIPANT_ID);
        if(userType.equals("Real"))
            appointment = new DatabaseAppointment(appointmentId);
        else
            appointment = new FakeDatabaseAppointment(appointmentId);

        appointment.getParticipantsIdAndThen((setOfParticipants) ->{
            linearLayout.removeAllViewsInLayout();
            for(String id : setOfParticipants){
                User user = new DatabaseUser(id);
                ConstraintLayout layout = (ConstraintLayout) inflater.inflate(R.layout.activity_calendar_entry_detail_participant_box,null);
                TextView usernameTextView = layout.findViewById(R.id.calendarEntryDetailActivityUserId);
                user.getNameAndThen((name) -> {
                    usernameTextView.setText(name);
                    usernameTextView.setTextSize(20);
                });
                Button button = layout.findViewById(R.id.calendarEntryDetailActivityKickButton);

                button.setVisibility(View.INVISIBLE);
                button.setText("NotKickable");
                button.setClickable(false);

                appointment.getOwnerIdAndThen((ownerId) ->{
                    if(ownerId.equals(user.getId())){
                        if(!id.equals(user.getId())){
                            button.setVisibility(View.VISIBLE);
                            button.setText("Kick");
                            button.setClickable(true);
                            button.setOnClickListener((v) ->{
                                kickUserButton(id);
                            });
                        }

                    }
                });

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

    protected void kickUserButton(String id){
        User user = new DatabaseUser(id);
        user.removeAppointment(appointment);
        appointment.removeParticipant(user);
    }

}