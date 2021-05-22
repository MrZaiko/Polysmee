package io.github.polysmee.profile;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.github.polysmee.R;
import io.github.polysmee.database.DatabaseUser;
import io.github.polysmee.database.UploadServiceFactory;
import io.github.polysmee.room.fragments.HelperImages;

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

    public static void downloadFriendProfilePicture(String id, ConstraintLayout friendEntry, Context context){
        (new DatabaseUser(id)).getProfilePicture_Once_And_Then((profilePictureId) ->{
            if(!profilePictureId.equals("")){
                UploadServiceFactory.getAdaptedInstance().downloadImage(profilePictureId, imageBytes -> {
                    ((CircleImageView)friendEntry.findViewById(R.id.friendActivityElementProfilePicture)).setImageBitmap(Bitmap.createBitmap(BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length)));
                },ss -> HelperImages.showToast(context.getString(R.string.genericErrorText), context),context);
            }
        });
    }
}
