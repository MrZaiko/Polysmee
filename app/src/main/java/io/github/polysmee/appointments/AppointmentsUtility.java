package io.github.polysmee.appointments;

import android.content.Context;
import android.widget.AutoCompleteTextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import io.github.polysmee.database.DatabaseUser;
import io.github.polysmee.database.User;
import io.github.polysmee.profile.AutoCompleteUserAdapter;
import io.github.polysmee.profile.UserItemAutocomplete;

public class AppointmentsUtility {
    public static void usersNamesGetter(Set<String> allIds, ArrayList<String> users) {
        //This function is called at the creation of the fragment
        //So here we get the names at the beginning of the fragment's life cycle and the listeners should updated them, but not remove the old name
        //While this may cause small problems if a user changes their name during this time,
        //the life cycle is expected to be pretty short and users shouldn't often change their name so it should only very rarely occur.
        for (String userId : allIds) {
            User user = new DatabaseUser(userId);
            user.getName_Once_AndThen(users::add);
        }
    }

    public static void fillUserList (Set<String> ids, List<String> allUsersNames, List<UserItemAutocomplete> allUsers, AutoCompleteTextView autoCompleteTextView, Context context) {
        for (String id : ids) {
            User user = new DatabaseUser(id);
            UserItemAutocomplete userItemAutocomplete = new UserItemAutocomplete();
            user.getName_Once_AndThen((name) -> {
                allUsersNames.add(name);

                userItemAutocomplete.setUsername(name);
                user.getProfilePicture_Once_And_Then((profilePictureId) ->{
                    userItemAutocomplete.setPictureId(profilePictureId);
                    allUsers.add(userItemAutocomplete);
                    if(allUsers.size() == ids.size()){
                        autoCompleteTextView.setAdapter(new AutoCompleteUserAdapter(context,allUsers));
                    }
                });
            });

        }
    }
}
