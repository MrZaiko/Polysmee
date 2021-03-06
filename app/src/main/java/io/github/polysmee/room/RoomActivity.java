package io.github.polysmee.room;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;

import io.github.polysmee.R;
import io.github.polysmee.interfaces.Appointment;

public class RoomActivity extends AppCompatActivity {

    private Appointment appointment;
    public static String APPOINTMENT_KEY = "io.github.polysmee.room.RoomActivity.APPOINTMENT_KEY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);
    }

    public void sendMessage(View view) {
        //Close the keyboard
        try {
            InputMethodManager inputManager = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception ignored) {}


        EditText messageEditText = findViewById(R.id.roomActivityMessageText);
        String messageToAdd = messageEditText.getText().toString();
        messageEditText.setText("");

        TextView messageToAddTextView = generateMessageTextView(messageToAdd, true);

        LinearLayout messages = findViewById(R.id.rommActivityScrollViewLayout);
        messages.addView(messageToAddTextView);
        //Blank text view to add a space between messages
        messages.addView(new TextView(this));

        //Scroll down the view to see the latest messages
        ScrollView scrollView = findViewById(R.id.roomActivityMessagesScrollView);
        scrollView.post(() -> scrollView.fullScroll(View.FOCUS_DOWN));
    }

    public void receiveMessage(View view) {
        //Close the keyboard
        try {
            InputMethodManager inputManager = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception ignored) {}


        EditText messageEditText = findViewById(R.id.roomActivityMessageText);
        String messageToAdd = messageEditText.getText().toString();
        messageEditText.setText("");
        System.out.println(messageToAdd);
        TextView messageToAddTextView = generateMessageTextView(messageToAdd, false);

        LinearLayout messages = findViewById(R.id.rommActivityScrollViewLayout);
        messages.addView(messageToAddTextView);
        //Blank text view to add a space between messages
        messages.addView(new TextView(this));

        //Scroll down the view to see the latest messages
        ScrollView scrollView = findViewById(R.id.roomActivityMessagesScrollView);
        scrollView.post(() -> scrollView.fullScroll(View.FOCUS_DOWN));
    }

    private TextView generateMessageTextView(String message, boolean isSent) {
        TextView messageView = new TextView(this);
        messageView.setText(message);

        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        params.gravity = isSent ? Gravity.END : Gravity.START;
        messageView.setLayoutParams(params);

        if (isSent)
            messageView.setBackgroundResource(R.drawable.sent_message_background);
        else
            messageView.setBackgroundResource(R.drawable.received_message_background);

        return messageView;
    }


}