package io.github.polysmee;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import java.io.Serializable;

import io.github.polysmee.appointments.AppointmentActivity;
import io.github.polysmee.appointments.TestUser;
import io.github.polysmee.calendar.CalendarActivity;
<<<<<<< HEAD
=======
import io.github.polysmee.database.DatabaseAppointment;
import io.github.polysmee.database.DatabaseUser;
import io.github.polysmee.interfaces.Appointment;
>>>>>>> origin/database_retry
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
        //String id = MainUserSingleton.getInstance().createNewUserAppointment(231321321, 3600, "AICC", "AHAHAHAHA");
        intent.putExtra(RoomActivity.APPOINTMENT_KEY, "-MVvNR1OPXM82pXEKYtc");
        startActivity(intent);
    }

    public void goToCalendar(View view){
        Intent intent = new Intent(this,CalendarActivity.class);
        intent.putExtra(CalendarActivity.UserTypeCode,"Real");
        startActivity(intent);
    }

    public void goToCreate(View view) {
        Intent intent = new Intent(this, AppointmentActivity.class);
        User user = new TestUser("koko", "kéké");
        intent.putExtra(AppointmentActivity.EXTRA_USER, (Serializable) user);
        startActivity(intent);
    }
}