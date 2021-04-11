package io.github.polysmee.room.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import io.github.polysmee.database.DatabaseUser;
import io.github.polysmee.database.User;
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
    private LayoutInflater inflater;
    private DatabaseReference databaseReference;
    private final Map<String, View> messagesDisplayed = new HashMap<>();

    private ActionMode actionMode;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.rootView = (ViewGroup)inflater.inflate(R.layout.fragment_activity_room_messages, container, false);

        String appointmentId = requireArguments().getString(MESSAGES_KEY);

        ImageView send = rootView.findViewById(R.id.roomActivitySendMessageButton);
        send.setOnClickListener(this::sendMessage);

        this.inflater = getLayoutInflater();
        initializeAndDisplayDatabase(appointmentId);

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

    /*
     * Edits the content of the message whose key is messageKey to newContent in the database
     */
    private void editMessage(String messageKey, String newContent) {
        databaseReference.child(messageKey).child("content").setValue(newContent);
    }

    /*
     * Deletes the message whose key is messageKey from the database
     */
    private void deleteMessage(String messageKey) {
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

    private View generateMessageTextView(String message, boolean isSent, String senderId, long date, String messageKey) {
        User sender = new DatabaseUser(senderId);

        Date currentDate = new Date(date);
        String TIMESTAMP_PATTERN = "HH:mm";
        SimpleDateFormat formatter = new SimpleDateFormat(TIMESTAMP_PATTERN, Locale.ENGLISH);

        ConstraintLayout messageLayout = (ConstraintLayout) inflater.inflate(R.layout.element_room_activity_message, null);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
        params.gravity = isSent ? Gravity.END : Gravity.START;
        messageLayout.setLayoutParams(params);

        TextView messageView = (TextView) messageLayout.getViewById(R.id.roomActivityMessageElementMessageContent);
        messageView.setText(message);

        if (isSent) {
            messageLayout.findViewById(R.id.roomActivityMessageElementSenderText).setVisibility(View.GONE);
            ((TextView) messageLayout.findViewById(R.id.roomActivityMessageElementDateSent)).setText(formatter.format(currentDate));
            messageLayout.setBackgroundResource(R.drawable.background_sent_message);
            messageLayout.setOnLongClickListener(v -> {
                if (actionMode != null)
                    return false;
                actionMode = getActivity().startActionMode(generateCallback(messageKey));
                return true;
            });
        }
        else {
            messageLayout.setBackgroundResource(R.drawable.background_received_message);
            ((TextView) messageLayout.findViewById(R.id.roomActivityMessageElementDateReceived)).setText(formatter.format(currentDate));
            sender.getNameAndThen(((TextView) messageLayout.findViewById(R.id.roomActivityMessageElementSenderText))::setText);
        }

        return messageLayout;
    }

    private ActionMode.Callback generateCallback(String messageKey) {
        return new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                MenuInflater inflater = mode.getMenuInflater();
                inflater.inflate(R.menu.room_edit_message_menu, menu);
                mode.setTitle("Choose an option");
                messagesDisplayed.get(messageKey).setBackgroundResource(R.drawable.background_selected_message);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.roomEditMessageMenuDelete:
                        deleteMessage(messageKey);
                        mode.finish();
                        return true;
                    case R.id.roomEditMessageMenuEdit:
                        generateEditMessageDialog(messageKey).show();
                        mode.finish();
                        return true;
                    default:
                        return false;
                }
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                actionMode = null;
                messagesDisplayed.get(messageKey).setBackgroundResource(R.drawable.background_sent_message);
            }
        };
    }


    private AlertDialog generateEditMessageDialog(String messageKey) {
        TextView messageView = messagesDisplayed.get(messageKey).findViewById(R.id.roomActivityMessageElementMessageContent);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Edit message");

        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_room_activity_edit_and_delete, null);

        EditText editMessage = dialogView.findViewById(R.id.roomActivityEditDialogText);
        editMessage.setHint(messageView.getText());

        builder.setPositiveButton("Edit", (dialog, id) -> {
            editMessage(messageKey, editMessage.getText().toString());
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
    private void initializeAndDisplayDatabase(String appointmentId) {

        //Initialize the database reference to the right path
        databaseReference = DatabaseFactory.getAdaptedInstance().getReference("appointments/" + appointmentId + "/messages");


        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Message message = snapshot.getValue(Message.class);

                String key = snapshot.getKey();
                String currentID = MainUserSingleton.getInstance().getId();
                View messageToAddLayout = generateMessageTextView(message.getContent(), currentID.equals(message.getSender()), message.getSender(), message.getMessageTime(), key);
                messagesDisplayed.put(key, messageToAddLayout);

                LinearLayout messages = rootView.findViewById(R.id.rommActivityScrollViewLayout);
                messages.addView(messageToAddLayout);

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
                ((TextView) messagesDisplayed.get(key).findViewById(R.id.roomActivityMessageElementMessageContent)).setText(message.getContent());
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                String key = snapshot.getKey();
                LinearLayout messages = rootView.findViewById(R.id.rommActivityScrollViewLayout);
                TextView viewToRemove = messagesDisplayed.get(key).findViewById(R.id.roomActivityMessageElementMessageContent);
                int indexOfMessage = messages.indexOfChild(viewToRemove);
                //remove the white space under the message and the message itself from the LinearLayout
                messages.removeViewAt(indexOfMessage + 1);
                messages.removeView(messagesDisplayed.get(key));
                messagesDisplayed.remove(key);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }


}
