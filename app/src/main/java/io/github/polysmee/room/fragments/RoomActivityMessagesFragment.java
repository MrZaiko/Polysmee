package io.github.polysmee.room.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
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

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.constraintlayout.widget.Guideline;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


import de.hdodenhof.circleimageview.CircleImageView;
import io.github.polysmee.R;
import io.github.polysmee.database.DatabaseAppointment;
import io.github.polysmee.database.DatabaseUser;
import io.github.polysmee.database.Message;
import io.github.polysmee.database.UploadServiceFactory;
import io.github.polysmee.database.User;
import io.github.polysmee.database.databaselisteners.MessageChildListener;
import io.github.polysmee.internet.connection.InternetConnection;
import io.github.polysmee.photo.editing.FileHelper;
import io.github.polysmee.login.MainUser;
import io.github.polysmee.photo.editing.PictureEditActivity;
import io.github.polysmee.profile.ProfileActivity;
import io.github.polysmee.room.MessageReaction;
import io.github.polysmee.room.RoomActivity;

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

    private ActivityResultLauncher<String> requestPermissionLauncher;
    private final Map<String, View> messagesDisplayed = new HashMap<>();
    private DatabaseAppointment databaseAppointment;
    private ActionMode actionMode;
    private MessageChildListener listener;
    private ImageView takePictureBtn;

    private Uri currentPhotoUri;

    private String selectedMessage;
    private MessageReaction selectedReaction;
    private Context context;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.rootView = (ViewGroup) inflater.inflate(R.layout.fragment_activity_room_messages, container, false);

        appointmentId = requireArguments().getString(MESSAGES_KEY);
        databaseAppointment = new DatabaseAppointment(appointmentId);

        ImageView send = rootView.findViewById(R.id.roomActivitySendMessageButton);
        send.setOnClickListener(this::sendMessage);

        ImageView pickGallery = rootView.findViewById(R.id.roomActivitySendPictureButton);
        pickGallery.setOnClickListener(this::openGallery);

        takePictureBtn = rootView.findViewById(R.id.roomActivityTakePictureButton);
        takePictureBtn.setOnClickListener(this::takePicture);

        initializePermissionRequester();

        this.inflater = getLayoutInflater();
        initializeAndDisplayDatabase();

        //Used to avoid crash in tests
        try {
            ((RoomActivity) getActivity()).setContext(getContext());
        } catch (ClassCastException e) {
            //tests might launch exceptions
        }

        return rootView;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    /**
     * Open the phone gallery allowing the user to select a picture
     * @param view button
     */
    private void openGallery(View view) {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }

    /**
     * Generate a temporary file to store a picture and launch the phone camera
     * @param view button
     */
    private void takePicture(View view) {
        if(!(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)) {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA);
            return;
        }

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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case SEND_PICTURE:
                    uploadAndSendPictureMessage((Uri) data.getExtras().get("data"));
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

    /**
     * Upload the picture with uri pictureUri and send a message displaying this picture
     * @param pictureUri picture to upload and send
     */
    private void uploadAndSendPictureMessage(Uri pictureUri) {
        currentPhotoUri = pictureUri;

        byte[] picturesToByte = new byte[0];
        try {
            picturesToByte = HelperImages.getBytes(getContext().getContentResolver().openInputStream(currentPhotoUri));
        } catch (IOException e) {
            e.printStackTrace();
        }

        UploadServiceFactory.getAdaptedInstance().uploadImage(picturesToByte,
                appointmentId, id -> databaseAppointment.addMessage(
                        new Message(MainUser.getMainUser().getId(), id, System.currentTimeMillis(), true, 0)
                ), s -> HelperImages.showToast(getActivity().getString(R.string.genericErrorText), getContext()), getContext());

        if(!InternetConnection.isOn()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setMessage(R.string.offline_picture);

            //add ok button
            builder.setPositiveButton(R.string.offline_ok, (dialog, which) -> {});
            builder.show();
        }
    }

    @Override
    public void onDestroy() {
        databaseAppointment.removeMessageListener(listener);
        super.onDestroy();
    }

    /**
     * Send a message with the text written in roomActivityMessageText
     * @param view button
     */
    private void sendMessage(View view) {
        closeKeyboard();

        EditText messageEditText = rootView.findViewById(R.id.roomActivityMessageText);
        String messageToAdd = messageEditText.getText().toString();
        String userId = MainUser.getMainUser().getId();

        databaseAppointment.addMessage(new Message(userId, messageToAdd, System.currentTimeMillis(), false, 0));
        messageEditText.setText("");

        if(!InternetConnection.isOn()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setMessage(R.string.offline_send_message);

            //add ok button
            builder.setPositiveButton(R.string.offline_ok, (dialog, which) -> {});
            builder.show();
        }
    }


    /**
     * Close the keyboard if displayed
     */
    private void closeKeyboard() {
        try {
            InputMethodManager inputManager = (InputMethodManager)
                    rootView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception ignored) {}
    }

    private View generateMessageTextView(Message message, boolean isSent, String messageKey) {
        User sender = new DatabaseUser(message.getSender());

        Date currentDate = new Date(message.getMessageTime());
        String TIMESTAMP_PATTERN = "HH:mm";
        SimpleDateFormat formatter = new SimpleDateFormat(TIMESTAMP_PATTERN, Locale.ENGLISH);

        /* ================
             LAYOUT SETUP
           ================ */

        ConstraintLayout messageLayout;
        if (message.getIsAPicture())
            messageLayout = (ConstraintLayout) inflater.inflate(R.layout.element_room_activity_picture, null);
        else
            messageLayout = (ConstraintLayout) inflater.inflate(R.layout.element_room_activity_message, null);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
        params.gravity = isSent ? Gravity.END : Gravity.START;
        messageLayout.setLayoutParams(params);

        /* ================
                CONTENT
           ================ */

        if (message.getIsAPicture()) {
            downloadMessagePicture(message.getContent(), messageLayout);
            if (!isSent) {
                TextView sendText = messageLayout.findViewById(R.id.roomActivityMessageElementPictureSenderText);
                sendText.setVisibility(View.VISIBLE);
                sender.getName_Once_AndThen(sendText::setText);
                sendText.setBackgroundResource(R.drawable.background_received_picture);
            }
        } else {
            TextView messageView = messageLayout.findViewById(R.id.roomActivityMessageElementMessageContent);
            messageView.setText(message.getContent());

            if (isSent) {
                messageLayout.findViewById(R.id.roomActivityMessageElementSenderText).setVisibility(View.GONE);
                messageLayout.findViewById(R.id.roomActivityMessageElementMainLayout).setBackgroundResource(R.drawable.background_sent_message);
            } else {
                messageLayout.findViewById(R.id.roomActivityMessageElementMainLayout).setBackgroundResource(R.drawable.background_received_message);
                sender.getName_Once_AndThen(((TextView) messageLayout.findViewById(R.id.roomActivityMessageElementSenderText))::setText);

                CircleImageView profilePicture = messageLayout.findViewById(R.id.roomActivityMessageElementProfilePicture);
                profilePicture.setVisibility(View.VISIBLE);
                sender.getProfilePicture_Once_And_Then(pictureId -> downloadProfilePicture(pictureId, profilePicture));
                profilePicture.setOnClickListener(v -> visitProfile(message.getSender()));
            }
        }

        /* ================
               DATE UI
           ================ */

        if (isSent) {
            ((TextView) messageLayout.findViewById(R.id.roomActivityMessageElementDateSent)).setText(formatter.format(currentDate));

            if (message.getIsAPicture())
                messageLayout.findViewById(R.id.roomActivityMessageElementDateSent).setBackgroundColor(Color.BLACK);
        } else {
            ((TextView) messageLayout.findViewById(R.id.roomActivityMessageElementDateReceived)).setText(formatter.format(currentDate));

            if (message.getIsAPicture())
                messageLayout.findViewById(R.id.roomActivityMessageElementDateReceived).setBackgroundColor(Color.BLACK);
        }

        /* ================
             EDIT MESSAGE
           ================ */

        messageLayout.setOnLongClickListener(v -> {
            if (actionMode != null)
                return false;
            actionMode = getActivity().startActionMode(generateCallback(messageKey, message.getIsAPicture() ? message.getContent() : null, isSent));
            return true;
        });

        /* ================
           MESSAGE REACTION
           ================ */

        if (!isSent)
            adaptMessageReactionConstraint(messageLayout, message);

        setupMessageReaction(messageLayout, message);

        return messageLayout;
    }

    /**
     * Adapt messageReaction constraint for a sent message
     * @param messageLayout message layout to adapt
     * @param message message to display
     */
    private void adaptMessageReactionConstraint(View messageLayout, Message message) {
        ConstraintLayout mainLayout = messageLayout.findViewById(R.id.roomActivityMessageElementMainLayout);
        ConstraintLayout.LayoutParams mainConstraints = (ConstraintLayout.LayoutParams) mainLayout.getLayoutParams();
        mainConstraints.horizontalBias = 0;
        mainLayout.setLayoutParams(mainConstraints);

        Guideline guideLineVert = messageLayout.findViewById(R.id.roomActivityMessageElementChooseReactionGuideline);
        ConstraintLayout.LayoutParams vertGuidelineParams = (ConstraintLayout.LayoutParams) guideLineVert.getLayoutParams();
        vertGuidelineParams.guidePercent = 0.35f;
        guideLineVert.setLayoutParams(vertGuidelineParams);

        ConstraintLayout reactionLayout = messageLayout.findViewById(R.id.roomActivityMessageElementReactionLayout);
        ConstraintLayout.LayoutParams reactionConstraints = (ConstraintLayout.LayoutParams) reactionLayout.getLayoutParams();
        reactionConstraints.horizontalBias = 1;
        reactionLayout.setLayoutParams(reactionConstraints);

        Guideline guideLine = messageLayout.findViewById(R.id.roomActivityMessageElementReactionGuideline);
        ConstraintLayout.LayoutParams horGuidelineParams = (ConstraintLayout.LayoutParams) guideLine.getLayoutParams();
        horGuidelineParams.guidePercent = 0.75f;
        guideLine.setLayoutParams(horGuidelineParams);

        ConstraintLayout chooseReactionMessageLayout = messageLayout.findViewById(
                message.getIsAPicture() ? R.id.roomActivityMessageElementPictureMainLayout : R.id.roomActivityMessageElementReactionMessageLayout);
        ConstraintSet chooseReactionConstraints = new ConstraintSet();
        chooseReactionConstraints.clone(chooseReactionMessageLayout);
        chooseReactionConstraints.connect(R.id.roomActivityMessageElementChooseReactionLayout, ConstraintSet.START,
                R.id.roomActivityMessageElementChooseReactionGuideline, ConstraintSet.START);
        chooseReactionConstraints.clear(R.id.roomActivityMessageElementChooseReactionLayout, ConstraintSet.END);
        chooseReactionConstraints.applyTo(chooseReactionMessageLayout);
    }

    /**
     * Setup the messageReaction UI
     * @param messageLayout message layout to adapt
     * @param message message to display
     */
    private void setupMessageReaction(View messageLayout, Message message) {
        MessageReaction reaction = MessageReaction.getReaction(message.getReaction());
        messageLayout.findViewById(R.id.roomActivityMessageElementJoyReaction).setOnClickListener(this::chooseReaction);
        messageLayout.findViewById(R.id.roomActivityMessageElementSadReaction).setOnClickListener(this::chooseReaction);
        messageLayout.findViewById(R.id.roomActivityMessageElementExpressionLessReaction).setOnClickListener(this::chooseReaction);
        messageLayout.findViewById(R.id.roomActivityMessageElementHeartEyesReaction).setOnClickListener(this::chooseReaction);
        messageLayout.findViewById(R.id.roomActivityMessageElementSunglassesReaction).setOnClickListener(this::chooseReaction);

        updateReaction(reaction, messageLayout);
    }

    /**
     * Download the specified picture and display it in profilePicture
     * @param pictureId the picture to download
     * @param profilePicture the view where the picture should be displayed
     */
    private void downloadProfilePicture(String pictureId, CircleImageView profilePicture) {
        if (pictureId != null && !pictureId.equals("")) {
            UploadServiceFactory.getAdaptedInstance().downloadImage(pictureId, imageBytes -> {
                Bitmap bmp = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                profilePicture.setImageBitmap(Bitmap.createBitmap(bmp));
            }, ss -> HelperImages.showToast(getActivity().getString(R.string.genericErrorText), getContext()), getContext());
        }
    }

    /**
     * Download the specified picture and display it in profilePicture
     * @param id the picture to download
     * @param messageLayout the message where the picture should be displayed
     */
    private void downloadMessagePicture(String id, View messageLayout) {
        if (id != null && !id.equals("")) {
            UploadServiceFactory.getAdaptedInstance().downloadImage(id, imageBytes -> {
                Bitmap bmp = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                ImageView image = messageLayout.findViewById(R.id.roomActivityMessageElementPicture);
                image.setImageBitmap(Bitmap.createBitmap(bmp));
            }, s -> messageLayout.findViewById(R.id.roomActivityMessageElementPictureErrorText).setVisibility(View.VISIBLE), getContext());
        }
    }

    /**
     * Start the ProfileActivity to display information of the specified user
     * @param userId user to display
     */
    private void visitProfile(String userId) {
        Intent profileIntent = new Intent(getContext(), ProfileActivity.class);
        profileIntent.putExtra(ProfileActivity.PROFILE_VISIT_CODE, ProfileActivity.PROFILE_VISITING_MODE);
        profileIntent.putExtra(ProfileActivity.PROFILE_ID_USER, userId);
        startActivityForResult(profileIntent, ProfileActivity.VISIT_MODE_REQUEST_CODE);
    }

    /**
     * Generate an action bar to edit the specified message
     *
     * @param messageKey message to interact with
     * @param pictureId id of the picture, null if the message is not a picture
     * @param isSent true if the message is sent
     * @return ActionMode.Callback for the action bar corresponding to the messageKey message
     */
    private ActionMode.Callback generateCallback(String messageKey, String pictureId, boolean isSent) {
        return new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                MenuInflater inflater = mode.getMenuInflater();
                selectedMessage = messageKey;
                selectedReaction = MessageReaction.DEFAULT;
                inflater.inflate(R.menu.room_edit_message_menu, menu);
                mode.setTitle(getActivity().getString(R.string.roomMessageOptionText));

                if (!isSent) {
                    menu.findItem(R.id.roomEditMessageMenuEdit).setVisible(false);
                    menu.findItem(R.id.roomEditMessageMenuDelete).setVisible(false);
                }

                View messageLayout = messagesDisplayed.get(messageKey);
                messageLayout.findViewById(R.id.roomActivityMessageElementChooseReactionLayout).setVisibility(View.VISIBLE);
                messageLayout.findViewById(R.id.roomActivityMessageElementReactionLayout).setVisibility(View.GONE);

                if (pictureId != null)
                    menu.findItem(R.id.roomEditMessageMenuEdit).setVisible(false);
                else
                    messageLayout
                            .findViewById(R.id.roomActivityMessageElementMainLayout)
                            .setBackgroundResource(R.drawable.background_selected_message);
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
                        if (pictureId != null)
                            UploadServiceFactory.getAdaptedInstance().deleteImage(pictureId, l -> HelperImages.showToast("Picture successfully removed", getContext()), l -> HelperImages.showToast("An error occurred", getContext()), getContext());
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

                View messageLayout = messagesDisplayed.get(messageKey);
                messageLayout.findViewById(R.id.roomActivityMessageElementChooseReactionLayout).setVisibility(View.GONE);

                updateReaction(selectedReaction, messageLayout);
                selectedReaction = null;
                selectedMessage = null;

                if (pictureId == null)
                    messagesDisplayed.get(messageKey)
                            .findViewById(R.id.roomActivityMessageElementMainLayout)
                            .setBackgroundResource(isSent ? R.drawable.background_sent_message : R.drawable.background_received_message);
            }
        };
    }

    /**
     * Select the reaction corresponding to the string stored in view
     * Only called when action mode != null
     * @param view textView
     */
    private void chooseReaction(View view) {
        assert actionMode != null;

        TextView selectedReactionView = (TextView) view;
        selectedReaction = MessageReaction.getReaction(getContext(), (String) selectedReactionView.getText());

        databaseAppointment.getMessageReaction_Once_AndThen(selectedMessage, reactId -> {
            if (reactId == selectedReaction.getReactionId())
                selectedReaction = MessageReaction.DEFAULT;

            databaseAppointment.editMessageReaction(selectedMessage, selectedReaction.getReactionId());
            actionMode.finish();
        });

    }

    /**
     * Update the messageView to display the given reaction
     * @param reaction reaction to display
     * @param messageView message to adapt
     */
    private void updateReaction(MessageReaction reaction, View messageView) {
        View reactionLayout = messageView.findViewById(R.id.roomActivityMessageElementReactionLayout);
        TextView reactionView = messageView.findViewById(R.id.roomActivityMessageElementReaction);
        if (reaction != MessageReaction.DEFAULT) {
            reactionLayout.setVisibility(View.VISIBLE);
            if (reaction != null)
                reactionView.setText(context.getText(reaction.getEmoji()));
        } else {
            reactionLayout.setVisibility(View.GONE);
        }
    }


    /**
     * generate a dialog to edit the specified message
     * @param messageKey the message to edit
     * @return a Dialog to edit the specified message
     */
    private AlertDialog generateEditMessageDialog(String messageKey) {
        TextView messageView = messagesDisplayed.get(messageKey).findViewById(R.id.roomActivityMessageElementMessageContent);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getActivity().getString(R.string.roomEditMessageText));

        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_room_activity_edit_and_delete, null);

        EditText editMessage = dialogView.findViewById(R.id.roomActivityEditDialogText);
        editMessage.setHint(messageView.getText());

        builder.setPositiveButton(getActivity().getString(R.string.genericEditText), (dialog, id) -> {
            databaseAppointment.editMessage(messageKey, editMessage.getText().toString());
            if(!InternetConnection.isOn()) {
                AlertDialog.Builder offlineMsg = new AlertDialog.Builder(getContext());
                offlineMsg.setMessage(R.string.offline_edit_message);

                //add ok button
                offlineMsg.setPositiveButton(R.string.offline_ok, (dialog1, which) -> {});
                offlineMsg.show();
            }
        });

        builder.setNeutralButton(getActivity().getString(R.string.genericCancelText), (dialog, id) -> {});

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
                View messageToAddLayout = generateMessageTextView(value, userId.equals(value.getSender()), key);
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
                if (!value.getIsAPicture())
                    ((TextView) messagesDisplayed.get(key).findViewById(R.id.roomActivityMessageElementMessageContent)).setText(value.getContent());
                updateReaction(MessageReaction.getReaction(value.getReaction()), messagesDisplayed.get(key));
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

    /**
     * Initializes the request permission requester
     */
    private void initializePermissionRequester() {
        requestPermissionLauncher =
                this.registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                    //joins the channel if granted and do nothing otherwise
                    if (isGranted) {
                        takePicture(takePictureBtn);

                    } else {
                        System.out.println("not granted");
                    }
                });
    }

}
