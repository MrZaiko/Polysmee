package io.github.polysmee.database;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import io.github.polysmee.R;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button login_b = findViewById(R.id.login_button);
        login_b.setOnClickListener(
            v -> Toast.makeText(getApplicationContext(),"clicked on login", Toast.LENGTH_SHORT).show()
        );
    }
}