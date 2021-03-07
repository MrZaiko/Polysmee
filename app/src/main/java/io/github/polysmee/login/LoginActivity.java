package io.github.polysmee.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;

import java.util.Collections;
import java.util.List;

import io.github.polysmee.MainActivity;
import io.github.polysmee.R;

//SOURCE (copied from there and then reworked) : https://github.com/firebase/snippets-android/blob/05f35b44cee182242cea761d68dde58c8a0af3d6/auth/app/src/main/java/com/google/firebase/quickstart/auth/FirebaseUIActivity.java#L31-L45

public class LoginActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button login_b = findViewById(R.id.login_button);
        login_b.setOnClickListener(
            v -> createSignInIntent()
        );
    }

    public void createSignInIntent() {
        // [START auth_fui_create_intent]
        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Collections.singletonList(
            new AuthUI.IdpConfig.EmailBuilder().build());

        Toast.makeText(this, AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setIsSmartLockEnabled(false)
            .setAvailableProviders(providers)
            .build().getClass().toString(), Toast.LENGTH_LONG).show();

        // Create and launch sign-in intent
        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setIsSmartLockEnabled(false)
                .setAvailableProviders(providers)
                .build(),
            RC_SIGN_IN);
        // [END auth_fui_create_intent]
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {

                // Successfully signed in
                startActivity(new Intent(this, MainActivity.class));
                finish();

            } else if(response == null) {
                    Toast.makeText(this, "user canceled login", Toast.LENGTH_LONG).show();
            } else if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                    Toast.makeText(this, "no network is available", Toast.LENGTH_LONG).show();
            } else {

                Toast.makeText(this, "unknown error occured", Toast.LENGTH_LONG).show();

            }
        }
    }
}