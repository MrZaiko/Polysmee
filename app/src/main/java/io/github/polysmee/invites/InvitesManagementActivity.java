package io.github.polysmee.invites;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import io.github.polysmee.R;

@RequiresApi(api = Build.VERSION_CODES.N)
public class InvitesManagementActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invites_management);
        ViewPager2 pager = findViewById(R.id.invitesManagementActivityPager);
        FragmentStateAdapter pagerAdapter = new InvitesManagementActivityPagerAdapter(this);
        pager.setAdapter(pagerAdapter);

        TabLayout tabs = findViewById(R.id.invitesManagementActivityTabs);
        new TabLayoutMediator(tabs, pager,
                (tab, position) -> tab.setText(getString(InvitesManagementActivityPagerAdapter.FRAGMENT_NAME_ID[position]))).attach();
    }



<<<<<<< HEAD
            //two loops: one for the appointments that are gone, and another for the new appointments

            deletedAppointments.removeAll(newAppointments); //keep the deleted appointments
            newAppointments.removeAll(appointmentSet); //keep the new appointments

            for (String oldAppointmentId : deletedAppointments) { //delete all old appointments
                appointmentSet.remove(oldAppointmentId);
                appointmentInfoMap.remove(oldAppointmentId);
                if (appointmentIdsToView.containsKey(oldAppointmentId)) {
                    scrollLayout.removeView(appointmentIdsToView.get(oldAppointmentId));
                    scrollLayout.removeView(appointmentIdsToView.get(oldAppointmentId + 1));
                }
            }


            appointmentSet.addAll(newAppointments); //add all new appointments
            if (newAppointments.isEmpty()) {
                scrollLayout.removeAllViewsInLayout();
                changeCurrentInvitesLayout(new HashSet<>(appointmentInfoMap.values()));
                return;
            }
            for (String id : newAppointments) { //iterate only on the new appointments, to set their listener
                Appointment appointment = new DatabaseAppointment(id);
                CalendarAppointmentInfo appointmentInfo = new CalendarAppointmentInfo("", "", 0, 0, id,0);
                appointment.getStartTime_Once_AndThen((start) -> {
                    appointmentInfo.setStartTime(start);
                    appointment.getDuration_Once_AndThen((duration) -> {
                        appointmentInfo.setDuration(duration);
                        appointment.getTitle_Once_AndThen((title) -> {
                            appointmentInfo.setTitle((title));
                            appointment.getCourse_Once_AndThen((course) -> {
                                appointmentInfo.setCourse(course);
                                if (!appointmentSet.contains(id)) { //the appointment was removed; we thus have to remove it from the displayed appointments
                                    appointmentInfoMap.remove(id);
                                    if (appointmentIdsToView.containsKey(id)) {
                                        scrollLayout.removeView(appointmentIdsToView.get(id));
                                        appointmentIdsToView.remove(id);
                                    }
                                } else {
                                    appointmentInfoMap.put(appointment.getId(), appointmentInfo);
                                    if (appointmentIdsToView.containsKey(appointmentInfo.getId())) { //the view is already there, we just need to update it
                                        makeInviteEntry(appointmentInfo, appointmentIdsToView.get(appointmentInfo.getId()));
                                    } else { //we add the new appointment and update the layout
                                        scrollLayout.removeAllViewsInLayout();
                                        changeCurrentInvitesLayout(new HashSet<>(appointmentInfoMap.values()));
                                    }
                                }
                            });
                        });
                    });
                });
            }
        };
    }
=======
>>>>>>> main
}