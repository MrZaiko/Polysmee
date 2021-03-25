package io.github.polysmee.calendar.detailsFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import io.github.polysmee.R;
import io.github.polysmee.database.DatabaseAppointment;
import io.github.polysmee.database.DatabaseUser;
import io.github.polysmee.interfaces.Appointment;
import io.github.polysmee.interfaces.User;

public class CalendarEntryDetailAddBanParticipantsFragment extends Fragment {

    private ViewGroup rootView;
    private Appointment appointment;
    public static String APPOINTMENT_DETAIL_ADD_PARTICIPANT_ID = "APPOINTMENT_DETAIL_ADD_PARTICIPANT_ID";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.rootView = (ViewGroup)inflater.inflate(R.layout.activity_calendar_entry_detail_add_ban_participants_fragment,container,false);
        appointment = new DatabaseAppointment((String) getArguments().getSerializable(APPOINTMENT_DETAIL_ADD_PARTICIPANT_ID));
        setInviteAndBanSearchBehavior();
        return rootView;
    }


    /**
     * Defines the behavior of the add and ban users search bars; after entering the
     * correct username in the corresponding search bar and clicking on the right button,
     * it either adds or invites the user.
     * NOTE: the banning system not having been implemented yet, the ban part
     * does nothing for now
     */
    protected void setInviteAndBanSearchBehavior(){

        Button inviteButton = rootView.findViewById(R.id.calendarEntryDetailActivityInviteButton);
        Button banButton    = rootView.findViewById(R.id.calendarEntryDetailActivityBanButton);
        SearchView inviteSearch = rootView.findViewById(R.id.calendarEntryDetailActivityInviteSearch);
        SearchView banSearch = rootView.findViewById(R.id.calendarEntryDetailActivityBanSearch);

        inviteButton.setOnClickListener((v)->{
            String inviteName = inviteSearch.getQuery().toString();
            inviteSearch.setQuery("",false);
            inviteSearch.clearFocus();
            User.getAllUsersIdsAndThenOnce((setOfUserIds) -> {
                appointment.getParticipantsIdAndThen((participants) ->{
                    for(String userId : setOfUserIds){
                        User user = new DatabaseUser(userId);
                        user.getNameAndThen((name) ->{
                            if(name.equals(inviteName) && !participants.contains(userId)){
                                user.addAppointment(appointment);
                                appointment.addParticipant(user);
                            }
                        });
                    }
                });

            });

        });

        banButton.setOnClickListener((v)->{
            String bannedName = banSearch.getQuery().toString();
            User.getAllUsersIdsAndThenOnce((setOfUserIds) -> {
                for(String userId: setOfUserIds){
                    User user = new DatabaseUser(userId);
                    user.getNameAndThen((name) -> {
                        if(name.equals(bannedName)){
                            appointment.addBan(user);
                        }
                    });
                }
            });
        });
    }

}