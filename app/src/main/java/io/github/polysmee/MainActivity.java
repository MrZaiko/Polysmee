package io.github.polysmee;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import io.github.polysmee.appointments.AppointmentActivity;
import io.github.polysmee.calendar.CalendarActivity;
import io.github.polysmee.notification.AppointmentReminderNotificationPublisher;
import io.github.polysmee.room.RoomActivity;
import io.github.polysmee.settings.SettingsActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void goToRoomActivity(View view) {
        Intent intent = new Intent(this, RoomActivity.class);
        //String id = MainUserSingleton.getInstance().createNewUserAppointment(231321321, 3600, "AICC", "AHAHAHAHA");
        intent.putExtra(RoomActivity.APPOINTMENT_KEY, "-MXxN7Keu6_hMrtHsTH8");
        startActivity(intent);
    }

    public void goToCalendar(View view){
        Intent intent = new Intent(this,CalendarActivity.class);
        startActivity(intent);
    }

    public void goToCreate(View view) {
        Intent intent = new Intent(this, AppointmentActivity.class);
        startActivity(intent);
    }

    public void goToSettings(View view){
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);


    }
}