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

import io.github.polysmee.R;

public class roomActivityMessagesFragment extends Fragment {
    private ViewGroup rootView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.rootView = (ViewGroup)inflater.inflate(R.layout.activity_room_messages_fragment, container, false);
        Button send = rootView.findViewById(R.id.roomActivitySendMessageButton);
        send.setOnClickListener(this::sendMessage);
        Button receive = rootView.findViewById(R.id.roomActivityReceiveMessageButton);
        receive.setOnClickListener(this::receiveMessage);

        return rootView;
    }

    @Override
    public String toString() {
        return "Messages";
    }

    public void sendMessage(View view) {
        closeKeyboard();

        EditText messageEditText = rootView.findViewById(R.id.roomActivityMessageText);
        String messageToAdd = messageEditText.getText().toString();
        messageEditText.setText("");

        TextView messageToAddTextView = generateMessageTextView(messageToAdd, true);

        LinearLayout messages = rootView.findViewById(R.id.rommActivityScrollViewLayout);
        messages.addView(messageToAddTextView);
        //Blank text view to add a space between messages
        messages.addView(new TextView(rootView.getContext()));

        //Scroll down the view to see the latest messages
        ScrollView scrollView = rootView.findViewById(R.id.roomActivityMessagesScrollView);
        scrollView.post(() -> scrollView.fullScroll(View.FOCUS_DOWN));
    }

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
