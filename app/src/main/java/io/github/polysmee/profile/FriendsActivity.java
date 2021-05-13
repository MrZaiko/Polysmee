package io.github.polysmee.profile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.github.polysmee.R;
import io.github.polysmee.database.DatabaseUser;
import io.github.polysmee.database.User;
import io.github.polysmee.database.databaselisteners.StringSetValueListener;
import io.github.polysmee.login.MainUser;

public class FriendsActivity extends AppCompatActivity {


    private AutoCompleteTextView searchFriend;
    private Map<String, String> namesToIds;
    private List<String> allUsers;
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
        namesToIds = new HashMap<>();
        idsToFriendEntries = new HashMap<>();
        scrollLayout = findViewById(R.id.friendsActivityScrollLayout);
        User.getAllUsersIds_Once_AndThen(this::nameGetters);
        searchFriend = findViewById(R.id.friendAddTextView);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, allUsers);
        searchFriend.setAdapter(adapter);
        friendAddButton = findViewById(R.id.friendActivityAddButton);
        friendAddButton.setOnClickListener((v) -> addFriendBehavior());
        builder = new AlertDialog.Builder(this);
        inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    /**
     * @param ids the ids of the users we want to get the names of
     */
    protected void nameGetters(Set<String> ids) {
        for (String id : ids) {
            User user = new DatabaseUser(id);
            user.getName_Once_AndThen((name) -> {
                allUsers.add(name);
                namesToIds.put(name, id);
            });
        }
    }

    /**
     * Determines the behavior of the "add" button after typing the name
     * of a user we want to add as friend
     */
    protected void addFriendBehavior() {
        String s = searchFriend.getText().toString();
        if (!allUsers.contains(s)) {
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
        TextView nameFriend = ((TextView) friendEntryLayout.findViewById(R.id.friendEntryName));
        nameFriend.setText(name);
        nameFriend.setOnClickListener((view) -> {
            Intent profileIntent = new Intent(this, ProfileActivity.class);
            profileIntent.putExtra(ProfileActivity.PROFILE_VISIT_CODE, ProfileActivity.PROFILE_VISITING_MODE);
            profileIntent.putExtra(ProfileActivity.PROFILE_ID_USER, userId);
            startActivityForResult(profileIntent, ProfileActivity.VISIT_MODE_REQUEST_CODE);
        });
        ((FloatingActionButton) friendEntryLayout.findViewById(R.id.friendEntryRemoveFriendButton)).setOnClickListener((v) -> {
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
}