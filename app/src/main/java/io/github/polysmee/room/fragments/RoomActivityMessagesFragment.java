package io.github.polysmee.room.fragments;

import android.app.AlertDialog;
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

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;
import java.util.Map;

import io.github.polysmee.messages.Message;
import io.github.polysmee.R;
import io.github.polysmee.database.DatabaseFactory;
import io.github.polysmee.login.MainUserSingleton;

/**
 * Fragment that handles messaging (Send, receive, display)
 */
public class RoomActivityMessagesFragment extends Fragment {
    public static String MESSAGES_KEY = "io.github.polysme.room.fragments.roomActivityMessagesFragment.MESSAGES_KEY";

    private ViewGroup rootView;
    private DatabaseReference databaseReference;
    private final Map<String, TextView> messagesDisplayed = new HashMap<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.rootView = (ViewGroup)inflater.inflate(R.layout.fragment_activity_room_messages, container, false);

        String appointmentId = requireArguments().getString(MESSAGES_KEY);

        Button send = rootView.findViewById(R.id.roomActivitySendMessageButton);
        send.setOnClickListener(this::sendMessage);

        initializeAndDisplayDatabase();

        return rootView;

    }

    /**
     * @param view
     */
    public void sendMessage(View view) {
        closeKeyboard();

        EditText messageEditText = rootView.findViewById(R.id.roomActivityMessageText);
        String messageToAdd = messageEditText.getText().toString();
        String userId = MainUserSingleton.getInstance().getId();

        //sends the message using the uid of the current user and the text from the EditText of the room
        Message.sendMessage(messageToAdd, databaseReference, userId);
        messageEditText.setText("");
    }

    /**
     * @param messageKey
     * @param newContent Edits the content of the message whose key is messageKey to newContent in the database
     */
    public void editMessage(String messageKey, String newContent) {
        databaseReference.child(messageKey).child("content").setValue(newContent);
    }

    /**
     * @param messageKey deletes the message whose key is messageKey from the database
     */
    public void deleteMessage(String messageKey) {
        databaseReference.child(messageKey).removeValue();
    }

    private void closeKeyboard() {
        try {
            InputMethodManager inputManager = (InputMethodManager)
                    rootView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception ignored) {}
    }

    private TextView generateMessageTextView(String message, boolean isSent, String messageKey) {
        TextView messageView = new TextView(rootView.getContext());
        messageView.setText(message);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
        params.gravity = isSent ? Gravity.END : Gravity.START;
        messageView.setLayoutParams(params);

        if (isSent) {
            messageView.setBackgroundResource(R.drawable.sent_message_background);
            messageView.setOnLongClickListener(v -> {
                generateEditMessageDialog(messageKey, messageView).show();
                return true;
            });
        }
        else
            messageView.setBackgroundResource(R.drawable.received_message_background);

        return messageView;
    }

    private AlertDialog generateEditMessageDialog(String messageKey, TextView messageView) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Edit message");

        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_room_activity_edit_and_delete, null);

        EditText editMessage = dialogView.findViewById(R.id.roomActivityEditDialogText);
        editMessage.setHint(messageView.getText());

        builder.setPositiveButton("Edit", (dialog, id) -> {
            editMessage(messageKey, editMessage.getText().toString());
        });

        builder.setNegativeButton("Delete", (dialog, id) -> {
            deleteMessage(messageKey);
        });

        builder.setNeutralButton("Cancel", (dialog, id) -> {
            //Nothing to do
        });


        builder.setView(dialogView);

        return builder.create();
    }

    /**
     * Initializes the path of the database, displays the messages from the database and adds an event listener on the value of the messages
     * in order to update them in case of changes
     */
    private void initializeAndDisplayDatabase() {

        //Initialize the database reference to the right path
        String appointmentId = requireArguments().getString(MESSAGES_KEY);
        databaseReference = DatabaseFactory.getAdaptedInstance().getReference("appointments/" + appointmentId + "/messages");


        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Message message = snapshot.getValue(Message.class);

                String key = snapshot.getKey();
                String currentID = MainUserSingleton.getInstance().getId();
                TextView messageToAddTextView = generateMessageTextView(message.getContent(), currentID.equals(message.getSender()), key);
                messagesDisplayed.put(key, messageToAddTextView);

                LinearLayout messages = rootView.findViewById(R.id.rommActivityScrollViewLayout);
                messages.addView(messageToAddTextView);

                //Blank text view to add a space between messages
                messages.addView(new TextView(rootView.getContext()));

                //Scroll down the view to see the latest messages
                ScrollView scrollView = rootView.findViewById(R.id.roomActivityMessagesScrollView);
                scrollView.post(() -> scrollView.fullScroll(View.FOCUS_DOWN));
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                //update the corresponding textView
                Message message = snapshot.getValue(Message.class);
                String key = snapshot.getKey();
                messagesDisplayed.get(key).setText(message.getContent());
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                String key = snapshot.getKey();
                LinearLayout messages = rootView.findViewById(R.id.rommActivityScrollViewLayout);
                TextView viewToRemove = messagesDisplayed.get(key);
                int indexOfMessage = messages.indexOfChild(viewToRemove);
                //remove the white space under the message and the message itself from the LinearLayout
                messages.removeViewAt(indexOfMessage + 1);
                messages.removeView(messagesDisplayed.get(key));
                messagesDisplayed.remove(key);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}
