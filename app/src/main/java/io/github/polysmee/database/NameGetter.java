package io.github.polysmee.database;



import androidx.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import io.github.polysmee.database.databaselisteners.MapStringStringChildListener;
import io.github.polysmee.database.databaselisteners.StringValueListener;
//TODO documentation
//TODO documentation for the database online null value etc, maybe ask the others
//TODO not thread safe if change occur while in clause of isUserHaveNickName for ligne 33 to 37
final class NameGetter {
    private static  ConcurrentHashMap<String, String> idToNickname = new ConcurrentHashMap();
    private static ConcurrentHashMap<String, ArrayList<StringValueListener>> idToNameListeners = new ConcurrentHashMap();
    private static boolean isNicknameSyncWithDatabaseEnable = true;

    private static boolean isUserHaveNickName(String userId){
        return idToNickname.get(userId)!=null;
    }


    public static void addListener(String userId, StringValueListener nameValueListener){
        if(!idToNameListeners.containsKey(userId)){
            idToNameListeners.put(userId, new ArrayList<StringValueListener>());
            //TODO when removing stringValue listener check if array empty if it is remove the map
        }
        idToNameListeners.get(userId).add(nameValueListener);
        if(isUserHaveNickName(userId)){
            nameValueListener.onDone(idToNickname.get(userId));
        }else{
            new DatabaseUser(userId).getRealNameAndThen(nameValueListener);
        }

    }

    public static void getOnce(String userId, StringValueListener nameValueListener){
        if(isUserHaveNickName(userId)){
            nameValueListener.onDone(idToNickname.get(userId));
        }else{
            new DatabaseUser(userId).getRealName_Once_AndThen(nameValueListener);
        }
    }


    public static void setNickNameSyncWithDatabase(){
        ///MainUser.getMainUser().getFriendsAndNicknameAndThen();
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


    //TODO have a local id_to_nickName that is sync online, check if the id of the wanted user is in here if it is use this mapping. Then everytime the id_to_nickName change call the changer listener
    //TODO if it is remove put it to the normal listener. When map changer if a id map is added remove the database listener and call the function with the mapping
    //TODO if the id is not in local id_to_nickname call the normal listener and save the listener so it can potentially be added again
    //TODO to test just make the above scenario happen and check the value of (volatile) value who's value is the value return as the name

    private static final class FriendsAndNicknameMapChildListener implements MapStringStringChildListener{

        @Override
        //Called when we have a new friend or at the beginning of the listening
        public void childAdded(String key, @Nullable String value) {
            //I assume that idToNickname has no value for key since the child has just been added => if their are listener they are listening online
            idToNickname.put(key, value);
            ArrayList<StringValueListener> nameListeners = idToNameListeners.get(key);
            if(nameListeners==null || value == null){
                return;
            }
            DatabaseUser databaseUser = new DatabaseUser(key);
            fromNoNicknameToNicknameClean(nameListeners, databaseUser);
            toNewNickname(nameListeners, value);
        }

        @Override
        //Called when the Nickname change or there is no more Nickname
        public void childChanged(String key, @Nullable String value) {
            ArrayList<StringValueListener> nameListeners = idToNameListeners.get(key);
            if(nameListeners == null){
                idToNickname.put(key, value);
                return;
            }
            DatabaseUser databaseUser = new DatabaseUser(key);
            if(!isUserHaveNickName(key)){
                fromNoNicknameToNicknameClean(nameListeners, databaseUser);
            }
            idToNickname.put(key, value);
            if(value==null){
                fromNicknameToNoNickname(nameListeners, databaseUser);
            }else{
                toNewNickname(nameListeners, value);
            }
        }


        @Override
        public void childRemoved(String key, @Nullable String value) {
            //not friend anymore
            ArrayList<StringValueListener> nameListeners = idToNameListeners.get(key);
            if(nameListeners==null){
                idToNickname.put(key, null);
                return;
            }
            DatabaseUser databaseUser = new DatabaseUser(key);
            if(isUserHaveNickName(key)){
                fromNicknameToNoNickname(nameListeners, databaseUser);
            }
            idToNickname.put(key, null);
        }

        @Override
        public void childMoved(String key, @Nullable String value) {
            //don't care
        }

        private void toNewNickname(ArrayList<StringValueListener> nameListeners, @NotNull String value) {
            for(StringValueListener nameListener : nameListeners){
                nameListener.onDone(value);
            }
        }

        private void fromNicknameToNoNickname(ArrayList<StringValueListener> nameListeners, DatabaseUser databaseUser) {
            for(StringValueListener nameListener : nameListeners){
                databaseUser.getRealNameAndThen(nameListener);
            }
        }

        private void fromNoNicknameToNicknameClean(ArrayList<StringValueListener> nameListeners, DatabaseUser databaseUser) {
            for(StringValueListener nameListener : nameListeners){
                databaseUser.removeRealNameListener(nameListener);
            }
        }
    }
}
