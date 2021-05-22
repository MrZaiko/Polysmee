package io.github.polysmee.login;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Collections;
import java.util.List;

import io.github.polysmee.R;
import io.github.polysmee.calendar.CalendarActivity;
import io.github.polysmee.database.DatabaseFactory;

//Copyright 2017 github.com/firebase

//        Licensed under the Apache License, Version 2.0 (the "License");
//        you may not use this file except in compliance with the License.
//        You may obtain a copy of the License at
//
//        http://www.apache.org/licenses/LICENSE-2.0
//
//        Unless required by applicable law or agreed to in writing, software
//        distributed under the License is distributed on an "AS IS" BASIS,
//        WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//        See the License for the specific language governing permissions and
//        limitations under the License.

//SOURCE (copied from there and then reworked) : https://github.com/firebase/snippets-android/blob/05f35b44cee182242cea761d68dde58c8a0af3d6/auth/app/src/main/java/com/google/firebase/quickstart/auth/FirebaseUIActivity.java#L31-L45

public class LoginActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getWindow().getDecorView().setBackgroundColor(Color.WHITE);
        Button login_b = findViewById(R.id.login_button);
        login_b.setOnClickListener(
                v -> createSignInIntent()
        );
    }

    public void createSignInIntent() {
        // [START auth_fui_create_intent]
        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Collections.singletonList(
                new AuthUI.IdpConfig.GoogleBuilder().build());


        // Create and launch sign-in intent
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setIsSmartLockEnabled(false)
                        .build(),
                RC_SIGN_IN);
        // [END auth_fui_create_intent]
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode != RC_SIGN_IN) {
            return;
        }
        IdpResponse response = IdpResponse.fromResultIntent(data);

        if (resultCode == RESULT_OK) {

            // Successfully signed in
            FirebaseDatabase db = DatabaseFactory.getAdaptedInstance();
            DatabaseReference name = db.getReference("users").child(MainUser.getMainUser().getId()).child("name");
            //name.keepSynced(true);
            MainUser.getMainUser().getName_Once_AndThen((nam) -> {
                if(nam == null)
                    name.setValue(AuthenticationFactory.getAdaptedInstance().getCurrentUser().getDisplayName());
            });
            Intent intent = new Intent(this, CalendarActivity.class);

            startActivity(intent);
            finish();

        }
    }
}