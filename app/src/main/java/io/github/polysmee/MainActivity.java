package io.github.polysmee;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import io.github.polysmee.room.RoomActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void goToRoomActivity(View view) {
        Intent intent = new Intent(this, RoomActivity.class);
        startActivity(intent);
    }
}