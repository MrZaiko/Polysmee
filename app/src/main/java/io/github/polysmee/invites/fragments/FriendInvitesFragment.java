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
import io.github.polysmee.login.MainUser;
import io.github.polysmee.profile.FriendMethodsHelpers;
import io.github.polysmee.profile.ProfileActivity;
import io.github.polysmee.room.fragments.HelperImages;

public class FriendInvitesFragment extends Fragment {
    private final Map<String, List<View>> friendInvitationIdsToView = new HashMap<>();
    private User user;
    private ViewGroup rootView;
    private List<Command> commandsToRemoveListeners = new ArrayList<Command>();
    private LinearLayout scrollLayout;
    private LayoutInflater inflater;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = (ViewGroup)inflater.inflate(R.layout.fragment_friend_invites, container, false);
        this.inflater = inflater;
        scrollLayout = rootView.findViewById(R.id.InvitesManagementFriendScrollLayout);
        user = MainUser.getMainUser();
        setFriendInvitesListener();
        return rootView;
    }

    @Override
    public void onDestroy() {

        Object dummyArgument = null;

        for(Command command: commandsToRemoveListeners) {
            command.execute(dummyArgument,dummyArgument);
        }

        super.onDestroy();
    }

    /**
     * Sets the listener of the friend invitations
     */
    protected void setFriendInvitesListener(){
        StringSetValueListener friendInvitesListener = currentFriendInvitesListener();
        user.getFriendsInvitationsAndThen((friendInvitesListener));
        commandsToRemoveListeners.add((x,y) -> user.removeFriendsInvitationsListener(friendInvitesListener));
    }

    private StringSetValueListener currentFriendInvitesListener(){
        return (friendsInvites) ->{
            scrollLayout.removeAllViewsInLayout();
            friendInvitationIdsToView.clear();
            for (String newFriendId : friendsInvites) {
                User newFriend = new DatabaseUser(newFriendId);
                newFriend.getName_Once_AndThen((name) -> {
                    createFriendInvitationEntry(newFriendId, name);
                });
            }
        };
    }

    /**
     * Add a new friend invitation entry to the scroll layout in the fragment
     * @param userId the user who sent the invitation's id
     * @param name the user who sent the invitation's friend
     */
    protected void createFriendInvitationEntry(String userId, String name) {
        User invitee = new DatabaseUser(userId);
        ConstraintLayout friendEntryLayout = (ConstraintLayout) inflater.inflate(R.layout.element_friends_activity_entry, null);
        TextView nameFriend = friendEntryLayout.findViewById(R.id.friendEntryName);
        nameFriend.setText(name);
        FriendMethodsHelpers.downloadFriendProfilePicture(userId,friendEntryLayout,getContext());
        FriendMethodsHelpers.visitProfileFriendEntry(nameFriend,userId,getActivity(),getContext());

        friendEntryLayout.findViewById(R.id.friendEntryAcceptFriendButton).setVisibility(View.VISIBLE);
        friendEntryLayout.findViewById(R.id.friendEntryRemoveFriendButton).setOnClickListener((v) -> {
            user.removeFriendInvitation(invitee);
        });
        friendEntryLayout.findViewById(R.id.friendEntryAcceptFriendButton).setOnClickListener((v) -> {
            user.removeFriendInvitation(invitee);
            user.addFriend(invitee);
            invitee.addFriend(user);
        });

        List<View> friendViews = new ArrayList<>();
        FriendMethodsHelpers.addFriendEntryToLayout(scrollLayout,friendViews,getContext(),friendEntryLayout);
        friendInvitationIdsToView.put(userId, friendViews);
    }

}