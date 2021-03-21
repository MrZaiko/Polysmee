package io.github.polysmee.room.fragments;

import android.content.Context;
import android.os.Bundle;
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
import java.util.Map;

import io.github.polysmee.Messages.Message;
import io.github.polysmee.R;
import io.github.polysmee.database.DatabaseFactory;
import io.github.polysmee.login.AuthenticationFactory;

/**
 * Fragment that handles messaging (Send, receive, display)
 */
public class RoomActivityMessagesFragment extends Fragment {
    private ViewGroup rootView;
    private DatabaseReference databaseReference;
    private Map<String, TextView> messagesDisplayed = new HashMap<String, TextView>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.rootView = (ViewGroup)inflater.inflate(R.layout.activity_room_messages_fragment, container, false);
        Button send = rootView.findViewById(R.id.roomActivitySendMessageButton);
        send.setOnClickListener(this::sendMessage);
        Button receive = rootView.findViewById(R.id.roomActivityReceiveMessageButton);
        receive.setOnClickListener(this::receiveMessage);

        //Initialize the database reference to the right path (default path for now)
        databaseReference = DatabaseFactory.getAdaptedInstance().getReference("messages");

        //add a value listener on the value of the database in order to display the messages and update them
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                //iterate over messages to display them
                for(DataSnapshot ds: snapshot.getChildren()) {

                    String key = ds.getKey();
                    String user = ds.child("sender").getValue(String.class);
                    String content = ds.child("content").getValue(String.class);
                    Long time = ds.child("messageTime").getValue(Long.class);
                    Message message = new Message(user, content, time);

                    /**
                     * Avoid displaying the same message multiple times by storing them in a hashMap
                     */
                    if (!messagesDisplayed.containsKey(key)) {

                        //check for each message whether the sender is the current user or not in order to adapt the background of the message (grey or blue) in the room
                        String userId = AuthenticationFactory.getAdaptedInstance().getCurrentUser().getUid();
                        TextView messageToAddTextView = generateMessageTextView(content, user.equals(userId));
                        messagesDisplayed.put(key, messageToAddTextView);

                        LinearLayout messages = rootView.findViewById(R.id.rommActivityScrollViewLayout);
                        messages.addView(messageToAddTextView);

                        //Blank text view to add a space between messages
                        messages.addView(new TextView(rootView.getContext()));

                        //Scroll down the view to see the latest messages
                        ScrollView scrollView = rootView.findViewById(R.id.roomActivityMessagesScrollView);
                        scrollView.post(() -> scrollView.fullScroll(View.FOCUS_DOWN));

                    }
                    //check whether the content of the message was updated
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

    /**
     * Display the message written in the PlainText RoomActivityMessageTest in the ScrollView
     * RoomActivityMessagesScrollView with a sent_message_background style
     * @param view
     */
    public void sendMessage(View view) {
        closeKeyboard();

        EditText messageEditText = rootView.findViewById(R.id.roomActivityMessageText);
        String messageToAdd = messageEditText.getText().toString();
        String userId = AuthenticationFactory.getAdaptedInstance().getCurrentUser().getUid();

        //sends the message using the uid of the current user and the text from the EditText of the room
        Message.sendMessage(messageToAdd, databaseReference, userId);
        messageEditText.setText("");

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
