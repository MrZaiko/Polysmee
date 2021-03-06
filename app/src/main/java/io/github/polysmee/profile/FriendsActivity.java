package io.github.polysmee.profile;

import android.content.Context;
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

import io.github.polysmee.R;
import io.github.polysmee.database.DatabaseUser;
import io.github.polysmee.database.User;
import io.github.polysmee.database.databaselisteners.valuelisteners.StringSetValueListener;
import io.github.polysmee.login.MainUser;

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
        friendAddButton = findViewById(R.id.friendActivityInviteButton);
        friendAddButton.setOnClickListener((v) -> inviteFriendButtonBehavior());
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
    protected void inviteFriendButtonBehavior() {
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
                    user.sendFriendInvitation(new DatabaseUser(namesToIds.get(s)));
                    builder.setMessage("Invitation sent")
                            .setCancelable(false)
                            .setPositiveButton("OK", null);
                    AlertDialog alert = builder.create();
                    alert.setTitle("Success");
                    alert.show();
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
        FriendMethodsHelpers.downloadFriendProfilePicture(userId,friendEntryLayout,this);

        FriendMethodsHelpers.visitProfileFriendEntry(nameFriend,userId,this,this);

        friendEntryLayout.findViewById(R.id.friendEntryRemoveFriendButton).setOnClickListener((v) -> {
            user.removeFriend(new DatabaseUser(userId));
            (new DatabaseUser(userId)).removeFriend(user);
        });
        List<View> friendViews = new ArrayList<>();
        FriendMethodsHelpers.addFriendEntryToLayout(scrollLayout,friendViews,this,friendEntryLayout);
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
}