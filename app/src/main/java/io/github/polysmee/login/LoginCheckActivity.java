package io.github.polysmee.login;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.FirebaseDatabase;

import io.github.polysmee.R;
import io.github.polysmee.calendar.CalendarActivity;
import io.github.polysmee.internet.connection.InternetConnection;

public class LoginCheckActivity extends AppCompatActivity {

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_check);

        if (FirebaseApp.getApps(this).size() == 1) {
            try {
                FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            } catch (DatabaseException e) {
                Toast toast = Toast.makeText(this, getText(R.string.restartErrorText), Toast.LENGTH_SHORT);
                toast.show();
            }
        }


        FirebaseUser user = AuthenticationSingleton.getAdaptedInstance().getCurrentUser();

        InternetConnection.addConnectionListener(getApplicationContext());

        if(user == null) {
            if(isOnline()) {
                startActivity(new Intent(LoginCheckActivity.this, LoginActivity.class));
            } else {
                startActivity(new Intent(LoginCheckActivity.this, NoConnectionActivity.class));
            }
        } else {
            Intent intent = new Intent(this, CalendarActivity.class);
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