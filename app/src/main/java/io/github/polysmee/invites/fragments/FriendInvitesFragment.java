package io.github.polysmee.invites.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import io.github.polysmee.R;
import io.github.polysmee.agora.Command;
import io.github.polysmee.database.DatabaseUser;
import io.github.polysmee.database.UploadServiceFactory;
import io.github.polysmee.database.User;
import io.github.polysmee.database.databaselisteners.StringSetValueListener;
import io.github.polysmee.profile.ProfileActivity;
import io.github.polysmee.room.fragments.HelperImages;

public class FriendInvitesFragment extends Fragment {
    private final Map<String, List<View>> friendInvitationIdsToView = new HashMap<>();
    private User user;
    private List<Command> commandsToRemoveListeners = new ArrayList<Command>();
    private LinearLayout scrollLayout;
    private LayoutInflater inflater;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        this.inflater = inflater;
        return inflater.inflate(R.layout.fragment_friend_invites, container, false);
    }

    @Override
    public void onDestroy() {

        Object dummyArgument = null;

        for(Command command: commandsToRemoveListeners) {
            command.execute(dummyArgument,dummyArgument);
        }

        super.onDestroy();
    }

    protected void setFriendInvitesListener(){
        StringSetValueListener friendInvitesListener = currentFriendInvitesListener();
        user.getFriendsInvitations_Once_And_Then((friendInvitesListener));
        commandsToRemoveListeners.add((x,y) -> user.removeFriendsInvitationsListener(friendInvitesListener));
    }
    private StringSetValueListener currentFriendInvitesListener(){
        return (friendsInvites) ->{
            friendInvitationIdsToView.clear();
            for (String newFriendId : friendsInvites) {
                User newFriend = new DatabaseUser(newFriendId);
                newFriend.getName_Once_AndThen((name) -> {
                    createFriendInvitationEntry(newFriendId, name);
                });
            }
        };
    }

    protected void createFriendInvitationEntry(String userId, String name) {
        User invitee = new DatabaseUser(userId);
        ConstraintLayout friendEntryLayout = (ConstraintLayout) inflater.inflate(R.layout.element_friends_activity_entry, null);
        TextView nameFriend = friendEntryLayout.findViewById(R.id.friendEntryName);
        nameFriend.setText(name);
        downloadFriendProfilePicture(userId,friendEntryLayout);
        nameFriend.setOnClickListener((view) -> {
            Intent profileIntent = new Intent(getContext(), ProfileActivity.class);
            profileIntent.putExtra(ProfileActivity.PROFILE_VISIT_CODE, ProfileActivity.PROFILE_VISITING_MODE);
            profileIntent.putExtra(ProfileActivity.PROFILE_ID_USER, userId);
            startActivityForResult(profileIntent, ProfileActivity.VISIT_MODE_REQUEST_CODE);
        });
        friendEntryLayout.findViewById(R.id.friendEntryAcceptFriendButton).setVisibility(View.VISIBLE);
        friendEntryLayout.findViewById(R.id.friendEntryRemoveFriendButton).setOnClickListener((v) -> {
            user.removeFriendInvitation(invitee);
        });
        friendEntryLayout.findViewById(R.id.friendEntryAcceptFriendButton).setOnClickListener((v) -> {
            user.removeFriendInvitation(invitee);
            user.addFriend(invitee);
            invitee.addFriend(user);
        });
        TextView padding = new TextView(getContext());
        List<View> friendViews = new ArrayList<>();
        friendViews.add(friendEntryLayout);
        friendViews.add(padding);
        scrollLayout.addView(friendEntryLayout);
        scrollLayout.addView(padding);
        friendInvitationIdsToView.put(userId, friendViews);
    }

    protected void downloadFriendProfilePicture(String id, ConstraintLayout friendEntry){
        (new DatabaseUser(id)).getProfilePicture_Once_And_Then((profilePictureId) ->{
            if(!profilePictureId.equals("")){
                UploadServiceFactory.getAdaptedInstance().downloadImage(profilePictureId, imageBytes -> {
                    Bitmap bmp = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                    ((CircleImageView)friendEntry.findViewById(R.id.friendActivityElementProfilePicture)).setImageBitmap(Bitmap.createBitmap(bmp));
                },ss -> HelperImages.showToast(getString(R.string.genericErrorText), getContext()),getContext());
            }
        });
    }
}