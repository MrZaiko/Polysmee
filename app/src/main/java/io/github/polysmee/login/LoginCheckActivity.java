package io.github.polysmee.login;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
            if(isOnline()) {
                startActivity(new Intent(LoginCheckActivity.this, LoginActivity.class));
            } else {
                startActivity(new Intent(LoginCheckActivity.this, NoConnectionActivity.class));
            }
        } else {
            Intent intent = new Intent(this,CalendarActivity.class);
            startActivity(intent);
        }
        finish();
    }

    boolean isOnline() {
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }
}