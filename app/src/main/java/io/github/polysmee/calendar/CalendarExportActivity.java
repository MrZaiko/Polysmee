package io.github.polysmee.calendar;


import androidx.appcompat.app.AppCompatActivity;

//Commented to avoid coverage problem
public class CalendarExportActivity extends AppCompatActivity {

    /*private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    private static final int RC_SIGN_IN = 100;

    private GoogleSignInClient mGoogleSignInClient;
    private GoogleSignInAccount account;
    private Calendar service;
    private String apiKey;

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

        final NetHttpTransport HTTP_TRANSPORT;
        HTTP_TRANSPORT = new NetHttpTransport();

        GoogleCredential credential = new GoogleCredential.Builder()
                    .setTransport(HTTP_TRANSPORT)
                    .setJsonFactory(JSON_FACTORY)
                    .setServiceAccountScopes(Collections.singleton(CalendarScopes.CALENDAR))
                    .build();

        service = new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(String.valueOf(R.string.app_name))
                .build();

        try {
            ApplicationInfo ai = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = ai.metaData;
            apiKey = bundle.getString("com.google.android.calendar.v2.API_KEY");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }


        findViewById(R.id.calendarExportActivityExportButton).setOnClickListener(l -> {
            new Thread( () -> {
                try {
                    CalendarList list = service.calendarList().list().execute();
                    for (CalendarListEntry entry : list.getItems()) {
                        Log.d("CALENDAR", entry.toPrettyString());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                /*DateTime now = new DateTime(System.currentTimeMillis());
                Events events = null;
                try {
                    events = service.events().list("primary")
                            .setMaxResults(10)
                            .setKey(apiKey)
                            .setTimeMin(now)
                            .setOrderBy("startTime")
                            .setSingleEvents(true)
                            .execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                List<Event> items = events.getItems();
                if (items.isEmpty()) {
                    System.out.println("No upcoming events found.");
                } else {
                    System.out.println("Upcoming events");
                    for (Event event : items) {
                        DateTime start = event.getStart().getDateTime();
                        if (start == null) {
                            start = event.getStart().getDate();
                        }
                        System.out.printf("%s (%s)\n", event.getSummary(), start);
                    }
                }
            }).start();
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
    }*/



}
