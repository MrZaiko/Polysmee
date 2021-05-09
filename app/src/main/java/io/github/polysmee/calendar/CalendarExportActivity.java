package io.github.polysmee.calendar;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.Calendar;

import com.google.api.services.calendar.model.CalendarList;
import com.google.api.services.calendar.model.CalendarListEntry;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Events;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.FutureTask;

import io.github.polysmee.R;

public class CalendarExportActivity extends AppCompatActivity {

    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    private static final int RC_SIGN_IN = 100;

    private GoogleSignInClient mGoogleSignInClient;
    private GoogleSignInAccount account;



    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_export);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestServerAuthCode(getString(R.string.default_web_client_id))
                .requestScopes(new Scope(CalendarScopes.CALENDAR))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        findViewById(R.id.sign_in_button).setOnClickListener(this::signIn);
        findViewById(R.id.sign_out_button).setOnClickListener(this::signOut);

        findViewById(R.id.calendarExportActivityExportButton).setOnClickListener(l -> {

            final NetHttpTransport HTTP_TRANSPORT;
            HTTP_TRANSPORT = new NetHttpTransport();

            GoogleCredential credential = new GoogleCredential.Builder()
                    .setTransport(HTTP_TRANSPORT)
                    .setJsonFactory(JSON_FACTORY)
                    .build();

            Thread bestThread = new Thread(() -> {
                String token = null;
                try {
                    token = GoogleAuthUtil.getToken(this, account.getAccount(), CalendarScopes.CALENDAR);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (GoogleAuthException e) {
                    e.printStackTrace();
                }
                credential.setAccessToken(token);

                Calendar finalService = new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                        .setApplicationName(String.valueOf(R.string.app_name))
                        .build();

                ApplicationInfo ai = null;
                try {
                    ai = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                Bundle bundle = ai.metaData;
                String myApiKey = bundle.getString("com.google.android.calendar.v2.API_KEY");

                Thread thread = new Thread(() -> {
                    try {
                    /*DateTime now = new DateTime(System.currentTimeMillis());
                    Events events = finalService.events().list("primary")
                            .setKey(myApiKey)
                            .execute();

                    List<Event> items = events.getItems();
                    for (Event entry : items) {
                        System.out.println(entry.toPrettyString());
                    }*/

                        Event event = new Event()
                                .setSummary("TEST")
                                .setDescription("A test");

                        DateTime startDateTime = new DateTime(System.currentTimeMillis());
                        EventDateTime start = new EventDateTime().setDateTime(startDateTime);
                        event.setStart(start);

                        DateTime endDateTime = new DateTime(System.currentTimeMillis() + 3600000);
                        EventDateTime end = new EventDateTime()
                                .setDateTime(endDateTime);
                        event.setEnd(end);

                        String calendarId = "primary";
                        event = finalService.events().insert(calendarId, event).execute();
                        System.out.printf("Event created: %s\n", event.getHtmlLink());

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });

                thread.start();

            });

            bestThread.start();
        });


    }

    private void signOut(View view) {
        mGoogleSignInClient.signOut().addOnCompleteListener(this, task -> {
            account = null;
            ((Button) findViewById(R.id.calendarExportActivityExportButton)).setText("Export");
            findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
            findViewById(R.id.sign_out_button).setVisibility(View.GONE);
        });
    }

    private void signIn(View view) {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            this.account = completedTask.getResult(ApiException.class);
            String text = "Export to " + account.getEmail();
            ((Button) findViewById(R.id.calendarExportActivityExportButton)).setText(text);
            findViewById(R.id.sign_in_button).setVisibility(View.GONE);
            findViewById(R.id.sign_out_button).setVisibility(View.VISIBLE);

        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("Sign in", "signInResult:failed code=" + e.getStatusCode());
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        account = GoogleSignIn.getLastSignedInAccount(this);
        if (account == null) {
            findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
            findViewById(R.id.sign_out_button).setVisibility(View.GONE);
        }
        else {
            String text = "Export to " + account.getEmail();
            ((Button) findViewById(R.id.calendarExportActivityExportButton)).setText(text);
        }
    }



}
