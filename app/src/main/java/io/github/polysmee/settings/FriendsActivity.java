package io.github.polysmee.settings;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.github.polysmee.R;
import io.github.polysmee.database.DatabaseUser;
import io.github.polysmee.database.User;
import io.github.polysmee.login.MainUserSingleton;

import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

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
    private final User user = MainUserSingleton.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
        attributeSet();
        showFriendList();
    }


    protected void attributeSet(){
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

    protected void nameGetters(Set<String> ids){
        for(String id: ids){
            User user = new DatabaseUser(id);
            user.getName_Once_AndThen((name) ->{
                allUsers.add(name);
                namesToIds.put(name,id);
            });
        }
    }

    protected void addFriendBehavior(){
        String s = searchFriend.getText().toString();
        if(!allUsers.contains(s)){
            builder.setMessage("User not found")
                    .setCancelable(false)
                    .setPositiveButton("Ok", null);

            AlertDialog alert = builder.create();
            alert.setTitle("Error");
            alert.show();
        }
        else{
            user.getName_Once_AndThen((name)->{
                if(s.equals(name)){
                    builder.setMessage("You can't add yourself as friend.")
                            .setCancelable(false)
                            .setPositiveButton("Ok", null);

                    AlertDialog alert = builder.create();
                    alert.setTitle("Oops");
                    alert.show();
                }
                else{
                    user.addFriend(new DatabaseUser(namesToIds.get(s)) );
                }
            });
        }
        searchFriend.setText("");
    }

    protected void showFriendList(){
        user.getFriendsAndThen((idsOfFriends)->{
            Set<String> deletedFriends = new HashSet<>(friendsIds);
            Set<String> newFriends = new HashSet<>(idsOfFriends);
            deletedFriends.removeAll(newFriends);
            newFriends.removeAll(friendsIds);
            for(String oldFriendId : deletedFriends){
                scrollLayout.removeView(idsToFriendEntries.get(oldFriendId).get(0));
                scrollLayout.removeView(idsToFriendEntries.get(oldFriendId).get(1));
                idsToFriendEntries.remove(oldFriendId);
                friendsIds.remove(oldFriendId);
            }
            friendsIds.addAll(newFriends);
            if(newFriends.isEmpty()){
                return;
            }
            for(String newFriendId : newFriends){
                User newFriend = new DatabaseUser(newFriendId);
                newFriend.getName_Once_AndThen((name)->{
                    createFriendEntry(newFriendId,name);
                });
            }
        });
    }

    protected void createFriendEntry(String userId, String name){
        ConstraintLayout friendEntryLayout = (ConstraintLayout)inflater.inflate(R.layout.element_friends_activity_entry,null);
        ((TextView)friendEntryLayout.findViewById(R.id.friendEntryName)).setText(name);
        ((FloatingActionButton)friendEntryLayout.findViewById(R.id.removeFriendButton)).setOnClickListener((v)->{
            user.removeFriend(new DatabaseUser(userId));
        });
        TextView padding = new TextView(this);
        List<View> friendViews = new ArrayList<>();
        friendViews.add(friendEntryLayout);
        friendViews.add(padding);
        scrollLayout.addView(friendEntryLayout);
        scrollLayout.addView(padding);
        idsToFriendEntries.put(userId,friendViews);
    }
}