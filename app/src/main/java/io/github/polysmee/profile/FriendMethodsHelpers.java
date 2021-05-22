package io.github.polysmee.profile;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.ArrayList;
import java.util.List;

public class FriendMethodsHelpers {

    public static void visitProfileFriendEntry(TextView nameFriend, String userId, Activity activity, Context context){
        nameFriend.setOnClickListener((view) -> {
            Intent profileIntent = new Intent(context, ProfileActivity.class);
            profileIntent.putExtra(ProfileActivity.PROFILE_VISIT_CODE, ProfileActivity.PROFILE_VISITING_MODE);
            profileIntent.putExtra(ProfileActivity.PROFILE_ID_USER, userId);
            activity.startActivityForResult(profileIntent, ProfileActivity.VISIT_MODE_REQUEST_CODE);
        });
    }

    public static void addFriendEntryToLayout(LinearLayout linearLayout,List<View> friendViews, Context context, ConstraintLayout friendEntryLayout){
        TextView padding = new TextView(context);
        friendViews.add(friendEntryLayout);
        friendViews.add(padding);
        linearLayout.addView(friendEntryLayout);
        linearLayout.addView(padding);

    }
}
