package io.github.polysmee.database.databaselisteners;

import androidx.annotation.NonNull;


import java.util.ArrayList;
import java.util.HashMap;

import io.github.polysmee.database.DatabaseUser;


final class StringNameValueListener implements StringValueListener {
    //TODO it can store null, if store null then no nickname
    private static volatile HashMap<String, String> idToNickname = new HashMap();
    //TODO check if don't need thread safe
    private static HashMap<String, ArrayList<StringValueListener>> idToStingValueListener = new HashMap<>();
    private static boolean isNicknameSyncWithDatabaseEnable = true;


    public static void addListener(String userId, StringValueListener stringValueListener){
        if(!idToStingValueListener.containsKey(userId)){
            idToStingValueListener.put(userId, new ArrayList<StringValueListener>());
            //TODO when removing stringValue listener check if array empty if it is remove the map
        }
        idToStingValueListener.get(userId).add(stringValueListener);
        if(idToNickname.getOrDefault(userId, null)==null){
            // the user with userId do not have a nickname
            new DatabaseUser(userId).getRealNameAndThen(stringValueListener);
        }else{
            stringValueListener.onDone(idToNickname.get(userId));
        }

    }

    public void onDone(String o) {
        //fstringValueListener.onDone(idToNicknames.getOrDefault(o, o));
    }

    public static void setNickNameSyncWithDatabase(){
        //MainUser.getMainUser().getFriendsAndThen();
    }


    /**
     * If the last called before calling setNickNameSyncWithDatabase to setIsNickNameSyncWithDatabaseEnable is called with false
     * then the nicknames listeners will not be set when callingsetNickNameSyncWithDatabase.
     *
     * @param value see the function description to know what value to pass
     */
    public static void setIsNickNameSyncWithDatabaseEnable(boolean value) {
        //isNickNameSyncWithDatabaseEnable = value;
    }
}
