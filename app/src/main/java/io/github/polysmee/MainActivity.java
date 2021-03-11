package io.github.polysmee;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import java.io.Serializable;

import io.github.polysmee.appointments.AppointmentActivity;
import io.github.polysmee.appointments.BasicAppointment;
import io.github.polysmee.appointments.TestUser;
import io.github.polysmee.calendar.CalendarActivity;
import io.github.polysmee.interfaces.Appointment;
import io.github.polysmee.interfaces.User;
import io.github.polysmee.room.RoomActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void goToRoomActivity(View view) {
        Intent intent = new Intent(this, RoomActivity.class);
        Appointment appo = new BasicAppointment(633636, 25, "AICC", "Révisions", new TestUser("baba", "bébé"));
        intent.putExtra(RoomActivity.APPOINTMENT_KEY, (Serializable) appo);
        startActivity(intent);
    }

    public void goToCalendar(View view){
        startActivity(new Intent(this, CalendarActivity.class));
    }

    public void goToCreate(View view) {
        Intent intent = new Intent(this, AppointmentActivity.class);
        User user = new TestUser("koko", "kéké");
        intent.putExtra(AppointmentActivity.EXTRA_USER, (Serializable) user);
        startActivity(intent);
    }
}