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

public class AutoCompleteUserAdapter extends ArrayAdapter<UserItemAutocomplete> {

    private final List<UserItemAutocomplete> userListFull;
    private final Context context;
    public AutoCompleteUserAdapter(@NonNull Context context, @NonNull List userList) {
        super(context,0,userList);
        this.userListFull = new ArrayList(userList);
        this.context = context;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return userFilter;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(
                    R.layout.element_autocomplete_user, parent,false
            );
        }
        TextView textView = convertView.findViewById(R.id.autoCompleteEntryName);
        CircleImageView imageView = convertView.findViewById(R.id.autoCompleteProfilePicture);
        UserItemAutocomplete userItemAutocomplete = (UserItemAutocomplete) getItem(position);
        if(userItemAutocomplete != null){
            textView.setText(userItemAutocomplete.getUsername());
            if(!userItemAutocomplete.getPictureId().equals("")){
                downloadUserProfilePicture(userItemAutocomplete.getPictureId(),imageView);
            }
        };
        return convertView;
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

    protected void downloadUserProfilePicture(String pictureId, CircleImageView picture){
            UploadServiceFactory.getAdaptedInstance().downloadImage(pictureId, imageBytes -> {
                Bitmap bmp = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                picture.setImageBitmap(Bitmap.createBitmap(bmp));
            },ss -> HelperImages.showToast(context.getString(R.string.genericErrorText), context));
    }
}
