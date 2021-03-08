package io.github.polysmee.room;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import io.github.polysmee.R;
import io.github.polysmee.interfaces.Appointment;
import io.github.polysmee.room.fragments.ActivityRoomMessagesFragment;
import io.github.polysmee.room.fragments.ActivityRoomParticipantsFragment;

public class RoomActivity extends AppCompatActivity {

    private Appointment appointment;
    public static String APPOINTMENT_KEY = "io.github.polysmee.room.RoomActivity.APPOINTMENT_KEY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);

        this.appointment = (Appointment) getIntent().getSerializableExtra(APPOINTMENT_KEY);
        setTitle(appointment.getTitle());


        //Fragment Creation
        List<Fragment> list = new ArrayList<>();
        list.add(new ActivityRoomMessagesFragment());
        list.add(new ActivityRoomParticipantsFragment(appointment.getParticipants()));


        ViewPager2 pager = findViewById(R.id.roomActivityPager);
        FragmentStateAdapter pagerAdapter = new RoomPagerAdapter(this, list);

        pager.setAdapter(pagerAdapter);

        TabLayout tabs = findViewById(R.id.roomActivityTabs);
        new TabLayoutMediator(tabs, pager,
                (tab, position) -> tab.setText(list.get(position).toString())).attach();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.room_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.roomMenuInfo:
                Intent intent = new Intent(this, RoomInfoActivity.class);
                intent.putExtra(RoomInfoActivity.APPOINTMENT_KEY, (Serializable) appointment);
                startActivityForResult(intent, RESULT_OK);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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