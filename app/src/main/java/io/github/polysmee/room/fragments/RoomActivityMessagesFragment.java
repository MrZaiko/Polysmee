package io.github.polysmee.room.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


import io.github.polysmee.database.DatabaseAppointment;
import io.github.polysmee.database.DatabaseUser;
import io.github.polysmee.database.UploadServiceFactory;
import io.github.polysmee.database.User;
import io.github.polysmee.database.databaselisteners.MessageChildListener;
import io.github.polysmee.database.Message;
import io.github.polysmee.R;
import io.github.polysmee.photo.editing.FileHelper;
import io.github.polysmee.login.MainUser;
import io.github.polysmee.photo.editing.PictureEditActivity;

import static android.app.Activity.RESULT_OK;

/**
 * Fragment that handles messaging (Send, receive, display)
 */
public class RoomActivityMessagesFragment extends Fragment {
    public static String MESSAGES_KEY = "io.github.polysme.room.fragments.roomActivityMessagesFragment.MESSAGES_KEY";
    private static final int PICK_IMAGE = 100;
    private static final int TAKE_PICTURE = 200;
    private static final int SEND_PICTURE = 300;

    private String appointmentId;

    private ViewGroup rootView;
    private LayoutInflater inflater;

    private final Map<String, View> messagesDisplayed = new HashMap<>();
    private DatabaseAppointment databaseAppointment;
    private ActionMode actionMode;
    private MessageChildListener listener;

    private Uri currentPhotoUri;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.rootView = (ViewGroup)inflater.inflate(R.layout.fragment_activity_room_messages, container, false);

        appointmentId = requireArguments().getString(MESSAGES_KEY);
        databaseAppointment = new DatabaseAppointment(appointmentId);

        ImageView send = rootView.findViewById(R.id.roomActivitySendMessageButton);
        send.setOnClickListener(this::sendMessage);

        ImageView pickGallery = rootView.findViewById(R.id.roomActivitySendPictureButton);
        pickGallery.setOnClickListener(this::openGallery);

        ImageView takePicture = rootView.findViewById(R.id.roomActivityTakePictureButton);
        takePicture.setOnClickListener(this::takePicture);

        String appointmentId = requireArguments().getString(MESSAGES_KEY);

        this.inflater = getLayoutInflater();
        initializeAndDisplayDatabase();

        return rootView;
    }

    private void openGallery(View view) {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }

    private void takePicture(View view) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getContext().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = FileHelper.createImageFile(getContext());
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                this.currentPhotoUri = FileProvider.getUriForFile(getContext(),
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, currentPhotoUri);
                startActivityForResult(takePictureIntent, TAKE_PICTURE);
            }
        }
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK ) {
            switch (requestCode) {
                case SEND_PICTURE:
                    currentPhotoUri = (Uri) data.getExtras().get("data");

                    byte[] picturesToByte = new byte[0];
                    try {
                        picturesToByte = getBytes(getContext().getContentResolver().openInputStream(currentPhotoUri));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    UploadServiceFactory.getAdaptedInstance().uploadImage(picturesToByte,
                            appointmentId, id -> databaseAppointment.addMessage(
                                    new Message(MainUser.getMainUser().getId(), id, System.currentTimeMillis(), true)
                            ), s -> showToast(getString(R.string.genericErrorText)));


                    return;

                case PICK_IMAGE:
                    currentPhotoUri = data.getData();

                case TAKE_PICTURE:
                    Intent intent = new Intent(getContext(), PictureEditActivity.class);
                    intent.putExtra(PictureEditActivity.PICTURE_URI, currentPhotoUri);
                    startActivityForResult(intent, SEND_PICTURE);
                    break;

                default:
            }
        }
    }

    private void showToast(String message) {
        Context context = getContext();

        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, message, duration);
        toast.show();
    }

    private byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    @Override
    public void onDestroy() {
        databaseAppointment.removeMessageListener(listener);
        super.onDestroy();
    }

    /**
     * @param view
     */
    private void sendMessage(View view) {
        closeKeyboard();

        EditText messageEditText = rootView.findViewById(R.id.roomActivityMessageText);
        String messageToAdd = messageEditText.getText().toString();
        String userId = MainUser.getMainUser().getId();

        databaseAppointment.addMessage(new Message(userId, messageToAdd, System.currentTimeMillis(), false));
        messageEditText.setText("");
    }



    private void closeKeyboard() {
        try {
            InputMethodManager inputManager = (InputMethodManager)
                    rootView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception ignored) {}
    }

    private View generateMessageTextView(String message, boolean isSent, String senderId, long date, boolean isAPicture, String messageKey) {
        User sender = new DatabaseUser(senderId);

        Date currentDate = new Date(date);
        String TIMESTAMP_PATTERN = "HH:mm";
        SimpleDateFormat formatter = new SimpleDateFormat(TIMESTAMP_PATTERN, Locale.ENGLISH);

        ConstraintLayout messageLayout;
        if (isAPicture)
            messageLayout = (ConstraintLayout) inflater.inflate(R.layout.element_room_activity_picture, null);
        else
            messageLayout = (ConstraintLayout) inflater.inflate(R.layout.element_room_activity_message, null);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
        params.gravity = isSent ? Gravity.END : Gravity.START;
        messageLayout.setLayoutParams(params);

        if (isAPicture) {
            downloadPicture(message, messageLayout);
            if (!isSent) {
                TextView sendText = messageLayout.findViewById(R.id.roomActivityMessageElementPictureSenderText);
                sendText.setVisibility(View.VISIBLE);
                sender.getName_Once_AndThen(sendText::setText);
                sendText.setBackgroundResource(R.drawable.background_received_picture);
            }
        } else {
            TextView messageView = (TextView) messageLayout.getViewById(R.id.roomActivityMessageElementMessageContent);
            messageView.setText(message);

            if (isSent) {
                messageLayout.findViewById(R.id.roomActivityMessageElementSenderText).setVisibility(View.GONE);
                messageLayout.setBackgroundResource(R.drawable.background_sent_message);
            } else {
                messageLayout.setBackgroundResource(R.drawable.background_received_message);
                sender.getName_Once_AndThen(((TextView) messageLayout.findViewById(R.id.roomActivityMessageElementSenderText))::setText);
            }
        }

        if (isSent) {
            messageLayout.setOnLongClickListener(v -> {
                if (actionMode != null)
                    return false;
                actionMode = getActivity().startActionMode(generateCallback(messageKey, isAPicture, message));
                return true;
            });
            ((TextView) messageLayout.findViewById(R.id.roomActivityMessageElementDateSent)).setText(formatter.format(currentDate));

            if (isAPicture)
                messageLayout.findViewById(R.id.roomActivityMessageElementDateSent).setBackgroundColor(Color.BLACK);
        } else {
            ((TextView) messageLayout.findViewById(R.id.roomActivityMessageElementDateReceived)).setText(formatter.format(currentDate));

            if (isAPicture)
                messageLayout.findViewById(R.id.roomActivityMessageElementDateReceived).setBackgroundColor(Color.BLACK);
        }

        return messageLayout;
    }

    private void downloadPicture(String id, View messageLayout) {
        UploadServiceFactory.getAdaptedInstance().downloadImage(id, imageBytes -> {
            Bitmap bmp = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            ImageView image = messageLayout.findViewById(R.id.roomActivityMessageElementPictureContent);
            image.setImageBitmap(Bitmap.createBitmap(bmp));
        }, s -> messageLayout.findViewById(R.id.roomActivityMessageElementPictureErrorText).setVisibility(View.VISIBLE));
    }

    private ActionMode.Callback generateCallback(String messageKey, boolean isAPicture, String pictureId) {
        return new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                MenuInflater inflater = mode.getMenuInflater();
                inflater.inflate(R.menu.room_edit_message_menu, menu);
                mode.setTitle(getString(R.string.roomMessageOptionText));

                if (isAPicture)
                    menu.findItem(R.id.roomEditMessageMenuEdit).setVisible(false);

                if (!isAPicture)
                    messagesDisplayed.get(messageKey).setBackgroundResource(R.drawable.background_selected_message);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.roomEditMessageMenuDelete:
                        if (isAPicture)
                            UploadServiceFactory.getAdaptedInstance().deleteImage(pictureId, l -> showToast("Picture successfully removed") , l -> showToast("An error occurred"));
                        databaseAppointment.removeMessage(messageKey);
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
                if (!isAPicture)
                    messagesDisplayed.get(messageKey).setBackgroundResource(R.drawable.background_sent_message);
            }
        };
    }


    private AlertDialog generateEditMessageDialog(String messageKey) {
        TextView messageView = messagesDisplayed.get(messageKey).findViewById(R.id.roomActivityMessageElementMessageContent);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getString(R.string.roomEditMessageText));

        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_room_activity_edit_and_delete, null);

        EditText editMessage = dialogView.findViewById(R.id.roomActivityEditDialogText);
        editMessage.setHint(messageView.getText());

        builder.setPositiveButton(getString(R.string.genericEditText), (dialog, id) -> {
            databaseAppointment.editMessage(messageKey, editMessage.getText().toString());
        });

        builder.setNeutralButton(getString(R.string.genericCancelText), (dialog, id) -> {
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


        listener = new MessageChildListener() {

            @Override
            public void childAdded(String key, Message value) {

                String userId = MainUser.getMainUser().getId();
                View messageToAddLayout = generateMessageTextView(value.getContent(), userId.equals(value.getSender()), value.getSender(), value.getMessageTime(), value.getIsAPicture(), key);
                messagesDisplayed.put(key, messageToAddLayout);
                LinearLayout messages = rootView.findViewById(R.id.roomActivityScrollViewLayout);
                messages.addView(messageToAddLayout);

                //Blank text view to add a space between messages
                messages.addView(new TextView(rootView.getContext()));

                //Scroll down the view to see the latest messages
                ScrollView scrollView = rootView.findViewById(R.id.roomActivityMessagesScrollView);
                scrollView.post(() -> scrollView.fullScroll(View.FOCUS_DOWN));
            }

            @Override
            public void childChanged(String key, Message value) {
                ((TextView) messagesDisplayed.get(key).findViewById(R.id.roomActivityMessageElementMessageContent)).setText(value.getContent());
            }

            @Override
            public void childRemoved(String key, Message value) {
                LinearLayout messages = rootView.findViewById(R.id.roomActivityScrollViewLayout);
                View viewToRemove = messagesDisplayed.get(key);
                int indexOfMessage = messages.indexOfChild(viewToRemove);
                //remove the white space under the message and the message itself from the LinearLayout
                messages.removeViewAt(indexOfMessage + 1);
                messages.removeView(messagesDisplayed.get(key));
                messagesDisplayed.remove(key);
            }

        };
        databaseAppointment.addMessageListener(listener);
    }

}
