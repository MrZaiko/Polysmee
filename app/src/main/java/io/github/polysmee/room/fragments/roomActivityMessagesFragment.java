package io.github.polysmee.room.fragments;

import android.content.Context;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import io.github.polysmee.Messages.Message;
import io.github.polysmee.R;

/**
 * Fragment that handles messaging (Send, receive, display)
 */
public class roomActivityMessagesFragment extends Fragment {
    private ViewGroup rootView;
    DatabaseReference databaseReference;
    Map<String, TextView> messagesDisplayed = new HashMap<String, TextView>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.rootView = (ViewGroup)inflater.inflate(R.layout.activity_room_messages_fragment, container, false);
        Button send = rootView.findViewById(R.id.roomActivitySendMessageButton);
        send.setOnClickListener(this::sendMessage);
        Button receive = rootView.findViewById(R.id.roomActivityReceiveMessageButton);
        receive.setOnClickListener(this::receiveMessage);

        databaseReference = FirebaseDatabase.getInstance().getReference("messages");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                rootView.findViewById(R.id.rommActivityScrollViewLayout);
                for(DataSnapshot ds: snapshot.getChildren()) {
                    String key = ds.getKey();
                    String user = ds.child("sender").getValue(String.class);
                    String content = ds.child("content").getValue(String.class);
                    Long time = ds.child("messageTime").getValue(Long.class);
                    Message message = new Message(user, content, time);//ds.getValue(Message.class);

                    if (!messagesDisplayed.containsKey(key)) {
                        //System.out.println(messagesDisplayed);

                        TextView messageToAddTextView = generateMessageTextView(content, user.equals(FirebaseAuth.getInstance().getCurrentUser().getUid()));
                        messagesDisplayed.put(key, messageToAddTextView);

                        LinearLayout messages = rootView.findViewById(R.id.rommActivityScrollViewLayout);
                        messages.addView(messageToAddTextView);
                        //Blank text view to add a space between messages
                        messages.addView(new TextView(rootView.getContext()));

                        //Scroll down the view to see the latest messages
                        ScrollView scrollView = rootView.findViewById(R.id.roomActivityMessagesScrollView);
                        scrollView.post(() -> scrollView.fullScroll(View.FOCUS_DOWN));

                    }

                    else if(!messagesDisplayed.get(key).getText().toString().equals(content)) {
                        messagesDisplayed.get(key).setText(content);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        return rootView;
    }

    @Override
    public String toString() {
        return "Messages";
    }

    /**
     * Display the message written in the PlainText RoomActivityMessageTest in the ScrollView
     * RoomActivityMessagesScrollView with a sent_message_background style
     * @param view
     */
    public void sendMessage(View view) {
        closeKeyboard();

        EditText messageEditText = rootView.findViewById(R.id.roomActivityMessageText);
        String messageToAdd = messageEditText.getText().toString();
        //Message.sendMessage(messageToAdd, "currentRoomId", FirebaseAuth.getInstance().getCurrentUser().getUid());
        databaseReference.push().setValue(new Message(FirebaseAuth.getInstance().getCurrentUser().getUid(), messageToAdd, System.currentTimeMillis()));
        messageEditText.setText("");

        TextView messageToAddTextView = generateMessageTextView(messageToAdd, true);

        LinearLayout messages = rootView.findViewById(R.id.rommActivityScrollViewLayout);
        //messages.addView(messageToAddTextView);
        //Blank text view to add a space between messages
        //messages.addView(new TextView(rootView.getContext()));

        //Scroll down the view to see the latest messages
        //ScrollView scrollView = rootView.findViewById(R.id.roomActivityMessagesScrollView);
        //scrollView.post(() -> scrollView.fullScroll(View.FOCUS_DOWN));
    }

    /**
     * Display the message written in the PlainText RoomActivityMessageTest in the ScrollView
     * RoomActivityMessagesScrollView with a received_message_background style
     * @param view
     */
    public void receiveMessage(View view) {
        closeKeyboard();

        EditText messageEditText = rootView.findViewById(R.id.roomActivityMessageText);
        String messageToAdd = messageEditText.getText().toString();
        messageEditText.setText("");
        System.out.println(messageToAdd);
        TextView messageToAddTextView = generateMessageTextView(messageToAdd, false);

        LinearLayout messages = rootView.findViewById(R.id.rommActivityScrollViewLayout);
        messages.addView(messageToAddTextView);
        //Blank text view to add a space between messages
        messages.addView(new TextView(rootView.getContext()));

        //Scroll down the view to see the latest messages
        ScrollView scrollView = rootView.findViewById(R.id.roomActivityMessagesScrollView);
        scrollView.post(() -> scrollView.fullScroll(View.FOCUS_DOWN));
    }

    private void closeKeyboard() {
        //Close the keyboard
        try {
            InputMethodManager inputManager = (InputMethodManager)
                    rootView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception ignored) {}
    }

    private TextView generateMessageTextView(String message, boolean isSent) {
        TextView messageView = new TextView(rootView.getContext());
        messageView.setText(message);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
        params.gravity = isSent ? Gravity.END : Gravity.START;
        messageView.setLayoutParams(params);

        if (isSent)
            messageView.setBackgroundResource(R.drawable.sent_message_background);
        else
            messageView.setBackgroundResource(R.drawable.received_message_background);

        return messageView;
    }

}
