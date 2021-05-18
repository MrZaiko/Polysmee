package io.github.polysmee.profile;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;
import io.github.polysmee.R;
import io.github.polysmee.database.DatabaseUser;
import io.github.polysmee.database.UploadServiceFactory;
import io.github.polysmee.database.User;
import io.github.polysmee.database.databaselisteners.StringSetValueListener;
import io.github.polysmee.login.MainUser;
import io.github.polysmee.room.fragments.HelperImages;

public class FriendsActivity extends AppCompatActivity {


    private AutoCompleteTextView searchFriend;
    private Map<String, String> namesToIds;
    private List<UserItemAutocomplete> allUsers;
    private List<String> allUsersNames;
    private Button friendAddButton;
    private AlertDialog.Builder builder;
    private LayoutInflater inflater;

    private Map<String, List<View>> idsToFriendEntries;
    private final Set<String> friendsIds = new HashSet<>();
    private LinearLayout scrollLayout;
    private final User user = MainUser.getMainUser();
    private StringSetValueListener friendsValuesListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
        attributeSet();
        friendsValuesListener = friendListener();
        showFriendList();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        user.removeFriendsListener(friendsValuesListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        user.removeFriendsListener(friendsValuesListener);
    }

    protected void attributeSet() {
        allUsers = new ArrayList<>();
        allUsersNames = new ArrayList<>();
        namesToIds = new HashMap<>();
        idsToFriendEntries = new HashMap<>();
        scrollLayout = findViewById(R.id.friendsActivityScrollLayout);
        searchFriend = findViewById(R.id.friendAddTextView);
        User.getAllUsersIds_Once_AndThen(this::fillUserList);
        friendAddButton = findViewById(R.id.friendActivityAddButton);
        friendAddButton.setOnClickListener((v) -> addFriendBehavior());
        builder = new AlertDialog.Builder(this);
        inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    /**
     * @param ids the ids of the users we want to get the names of
     */
    protected void fillUserList(Set<String> ids) {
        for (String id : ids) {
            User user = new DatabaseUser(id);
            UserItemAutocomplete userItemAutocomplete = new UserItemAutocomplete();
            user.getName_Once_AndThen((name) -> {
                allUsersNames.add(name);
                namesToIds.put(name,id);
                userItemAutocomplete.setUsername(name);
                user.getProfilePicture_Once_And_Then((profilePictureId) ->{
                    userItemAutocomplete.setPictureId(profilePictureId);
                    allUsers.add(userItemAutocomplete);
                    if(allUsers.size() == ids.size()){
                        searchFriend.setAdapter(new AutoCompleteUserAdapter(this,allUsers));
                    }
                });
            });

        }
    }

    /**
     * Determines the behavior of the "add" button after typing the name
     * of a user we want to add as friend
     */
    protected void addFriendBehavior() {
        String s = searchFriend.getText().toString();
        if (!allUsersNames.contains(s)) {
            builder.setMessage(getString(R.string.genericUserNotFoundText))
                    .setCancelable(false)
                    .setPositiveButton(getString(R.string.genericOkText), null);

            AlertDialog alert = builder.create();
            alert.setTitle(getString(R.string.genericErrorText));
            alert.show();
        } else {
            user.getName_Once_AndThen((name) -> {
                if (s.equals(name)) {
                    builder.setMessage("You can't add yourself as friend.")
                            .setCancelable(false)
                            .setPositiveButton(getString(R.string.genericOkText), null);

                    AlertDialog alert = builder.create();
                    alert.setTitle("Oops");
                    alert.show();
                } else {
                    user.addFriend(new DatabaseUser(namesToIds.get(s)));
                }
            });
        }
        searchFriend.setText("");
    }

    /**
     * Sets the listener to the friends list
     */
    protected void showFriendList() {
        user.getFriendsAndThen(friendsValuesListener);
    }

    /**
     * @param userId the user which we will create a new friend entry for
     * @param name   the user's name
     */
    protected void createFriendEntry(String userId, String name) {
        ConstraintLayout friendEntryLayout = (ConstraintLayout) inflater.inflate(R.layout.element_friends_activity_entry, null);
        TextView nameFriend = friendEntryLayout.findViewById(R.id.friendEntryName);
        nameFriend.setText(name);
        downloadFriendProfilePicture(userId,friendEntryLayout);
        nameFriend.setOnClickListener((view) -> {
            Intent profileIntent = new Intent(this, ProfileActivity.class);
            profileIntent.putExtra(ProfileActivity.PROFILE_VISIT_CODE, ProfileActivity.PROFILE_VISITING_MODE);
            profileIntent.putExtra(ProfileActivity.PROFILE_ID_USER, userId);
            startActivityForResult(profileIntent, ProfileActivity.VISIT_MODE_REQUEST_CODE);
        });
        friendEntryLayout.findViewById(R.id.friendEntryRemoveFriendButton).setOnClickListener((v) -> {
            user.removeFriend(new DatabaseUser(userId));
        });
        TextView padding = new TextView(this);
        List<View> friendViews = new ArrayList<>();
        friendViews.add(friendEntryLayout);
        friendViews.add(padding);
        scrollLayout.addView(friendEntryLayout);
        scrollLayout.addView(padding);
        idsToFriendEntries.put(userId, friendViews);
    }

    /**
     * @return the friend listener we want to show the friend list, and reacts as we delete/add
     * new friends
     */
    protected StringSetValueListener friendListener() {
        return idsOfFriends -> {
            Set<String> deletedFriends = new HashSet<>(friendsIds);
            Set<String> newFriends = new HashSet<>(idsOfFriends);
            deletedFriends.removeAll(newFriends);
            newFriends.removeAll(friendsIds);
            for (String oldFriendId : deletedFriends) {
                scrollLayout.removeView(idsToFriendEntries.get(oldFriendId).get(0));
                scrollLayout.removeView(idsToFriendEntries.get(oldFriendId).get(1));
                idsToFriendEntries.remove(oldFriendId);
                friendsIds.remove(oldFriendId);
            }
            friendsIds.addAll(newFriends);
            if (newFriends.isEmpty()) {
                return;
            }
            for (String newFriendId : newFriends) {
                User newFriend = new DatabaseUser(newFriendId);
                newFriend.getName_Once_AndThen((name) -> {
                    createFriendEntry(newFriendId, name);
                });
            }
        };
    }

    protected void downloadFriendProfilePicture(String id, ConstraintLayout friendEntry){
        (new DatabaseUser(id)).getProfilePicture_Once_And_Then((profilePictureId) ->{
            if(!profilePictureId.equals("")){
                UploadServiceFactory.getAdaptedInstance().downloadImage(profilePictureId, imageBytes -> {
                            Bitmap bmp = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                            ((CircleImageView)friendEntry.findViewById(R.id.friendActivityElementProfilePicture)).setImageBitmap(Bitmap.createBitmap(bmp));
                        },ss -> HelperImages.showToast(getString(R.string.genericErrorText), this),this);
            }
        });
    }
}