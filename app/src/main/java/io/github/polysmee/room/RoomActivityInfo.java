package io.github.polysmee.room;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Date;

import io.github.polysmee.R;
import io.github.polysmee.interfaces.Appointment;

/**
 * Activity showing all information about an appointment given in argument
 */
public class RoomActivityInfo extends AppCompatActivity {
    public static String APPOINTMENT_KEY = "io.github.polysmee.room.RoomInfoActivity.APPOINTMENT_KEY";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_info);
        Appointment appointment = (Appointment) getIntent().getSerializableExtra(APPOINTMENT_KEY);
        ((TextView) findViewById(R.id.roomInfoTitle)).setText(appointment.getTitle());
        ((TextView) findViewById(R.id.roomInfoCourse)).setText(appointment.getCourse());
        ((TextView) findViewById(R.id.roomInfoStartDate)).setText(new Date(appointment.getStartTime()).toString());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(Activity.RESULT_OK);
        finish();
    }
}
