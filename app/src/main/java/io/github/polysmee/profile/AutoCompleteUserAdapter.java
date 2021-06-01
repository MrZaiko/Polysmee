package io.github.polysmee.profile;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.github.polysmee.R;
import io.github.polysmee.database.UploadServiceFactory;
import io.github.polysmee.room.fragments.HelperImages;

/**
 * Custom array adapter to show pictures of users when searching for them
 */
public class AutoCompleteUserAdapter extends ArrayAdapter<UserItemAutocomplete> {

    private final List<UserItemAutocomplete> userListFull;


    public AutoCompleteUserAdapter(@NonNull Context context, @NonNull List userList) {
        super(context,0,userList);
        this.userListFull = new ArrayList(userList);
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return userFilter;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.element_autocomplete_user, parent,false);
        TextView textView = view.findViewById(R.id.autoCompleteEntryName);
        CircleImageView imageView = view.findViewById(R.id.autoCompleteProfilePicture);
        UserItemAutocomplete userItemAutocomplete = (UserItemAutocomplete) getItem(position);
        if(userItemAutocomplete != null){
            textView.setText(userItemAutocomplete.getUsername());
            if(!userItemAutocomplete.getPictureId().equals("")){
                downloadUserProfilePicture(userItemAutocomplete.getPictureId(),imageView);
            }
        };
        return view;
    }

    private final Filter userFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            FilterResults results = new FilterResults();
            List<UserItemAutocomplete> suggestions = new ArrayList<>();
            if(charSequence != null && charSequence.length() >= 2){
                String filterPattern = charSequence.toString().toLowerCase().trim();
                for(UserItemAutocomplete item: userListFull){
                    if(item.getUsername().toLowerCase().trim().startsWith(filterPattern)){
                        suggestions.add(item);
                    }
                }
            }
            results.values = suggestions;
            results.count = suggestions.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            clear();
            addAll((List)filterResults.values);
            notifyDataSetChanged();
        }

        @Override
        public CharSequence convertResultToString(Object resultValue) {
            return((UserItemAutocomplete)resultValue).getUsername();
        }
    };

    private void downloadUserProfilePicture(String pictureId, CircleImageView picture){
            UploadServiceFactory.getAdaptedInstance().downloadImage(pictureId, imageBytes -> {
                picture.setImageBitmap(Bitmap.createBitmap(BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length)));
            },ss -> HelperImages.showToast(getContext().getString(R.string.genericErrorText), getContext()),getContext());
    }
}
