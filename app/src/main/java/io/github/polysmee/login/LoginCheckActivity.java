package io.github.polysmee.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseUser;

import io.github.polysmee.R;
import io.github.polysmee.calendar.CalendarActivity;

public class LoginCheckActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_check);
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        FirebaseUser user = AuthenticationFactory.getAdaptedInstance().getCurrentUser();
        if(user == null) {
            startActivity(new Intent(this, LoginActivity.class));
        } else {
            Intent intent = new Intent(this,CalendarActivity.class);
            startActivity(intent);
        }
        finish();
    }
}