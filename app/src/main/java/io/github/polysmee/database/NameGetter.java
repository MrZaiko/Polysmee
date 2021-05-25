package io.github.polysmee.database;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import io.github.polysmee.database.databaselisteners.MapStringStringChildListener;
import io.github.polysmee.database.databaselisteners.StringValueListener;

//TODO get the lock inside a try catch
//TODO documentation
//TODO documentation for the database online null value etc, maybe ask the others
//TODO not thread safe if change occur while in clause of isUserHaveNickName for ligne 33 to 37
//TODO execute onDone of listener on a new thread
//TODO make a comment to explain why thread a running in order in the pool
//TODO can not guarantee that a thread will be stop when we remove it, or can we. Wrap the common callable and store it's value in a table, when executing we can check that the executor is this wrap class
//as the wrap class in the table is supposed to be the last one wanted to call if it is not do not execute if it is execute it and at the end mark it as executed before leaving
final class NameGetter {
    private static ReentrantLock lockForNoKeyValuePair = new ReentrantLock();
    private static HashMap<String, String> idToNickname = new HashMap();
    //make the table that save the value thread safe maybe
    //TODO check documentation
    //if return value is the StringValueListener from idToNameListeners then lastCallToExecute has been executed
    private static HashMap<StringValueListener, StringValueListener> stringValueListenerToLastCallToExecute = new HashMap<>();
    //TODO need concurenthasMap for idToNameListener
    private static ConcurrentHashMap<String, ArrayList<StringValueListener>> idToNameListeners = new ConcurrentHashMap();
    //access to idToNameOnlineListeners is always surrounded by synchronized (idToNameListeners)
    private static ConcurrentHashMap<String, ArrayList<StringValueListener>> idToNameOnlineListeners = new ConcurrentHashMap<>();
    private static boolean isNicknameSyncWithDatabaseEnable = true;

    private static boolean isUserHaveNickname(String userId) {
        return idToNickname.get(userId) != null;
    }
    //TODO write that we assume that when we remove a listener from firebase when it return the listener will no longer be executed and will never

    //The parameter passed should only be the StringValueListener from idToNameListeners
    private static StringValueListener getRunnable(StringValueListener stringValueListener, boolean isLocal) {
        StringValueListener stringValueListenerWrapper = new StringValueListener() {
            @Override
            public void onDone(@NonNull String o) {
                //TODO be thread safe
                //TODO should not redefine the hasable and equal function i.e. 2 class are equal if they have the same reference
                //the synchronized is needed so we can be sure the stringValueListener are executed one after the other (i.e. not at the same time it could cause data race)
                synchronized (stringValueListener) {
                    //So we are sure the last executed callable is indeed the last one that we wanted
                    if (stringValueListenerToLastCallToExecute.get(stringValueListener) == this) {
                        stringValueListener.onDone(o);
                    }
                    ;
                }
                if (isLocal) {
                    //TODO could make it faster by taking lower granularity and maybe use concurent map
                    synchronized (stringValueListenerToLastCallToExecute) {
                        if (stringValueListenerToLastCallToExecute.get(stringValueListener) == this) {
                            //indicate that the last wanted run has finished
                            stringValueListenerToLastCallToExecute.put(stringValueListener, stringValueListener);
                        }
                    }
                }
            }
        };
        synchronized (stringValueListenerToLastCallToExecute) {
            stringValueListenerToLastCallToExecute.put(stringValueListener, stringValueListenerWrapper);
        }
        return stringValueListenerWrapper;
    }

    public static void addListener(String userId, StringValueListener nameListener) {
        //to protect idToNameListener write
        boolean isLockForNoKeyValuePairTaken = false;
        if (!idToNameListeners.containsKey(userId)) {
            lockForNoKeyValuePair.lock();
            isLockForNoKeyValuePairTaken = true;
            idToNameListeners.put(userId, new ArrayList<StringValueListener>());
        }
        ArrayList<StringValueListener> nameListeners = idToNameListeners.get(userId);
        synchronized (nameListeners) {
            nameListeners.add(nameListener);
            if (isUserHaveNickname(userId)) {
                getRunnable(nameListener, true).onDone(idToNickname.get(userId));
            } else {
                //2 possibilities either i'm the first one so I would need array list or
                ArrayList<StringValueListener> nameOnlineListeners = idToNameOnlineListeners.getOrDefault(userId, new ArrayList<>());
                assert nameOnlineListeners != null;
                StringValueListener nameOnlineListener = getRunnable(nameListener, false);
                nameOnlineListeners.add(nameOnlineListener);
                new DatabaseUser(userId).getRealNameAndThen(nameListener);
            }
        }
        if (isLockForNoKeyValuePairTaken) {
            lockForNoKeyValuePair.unlock();
            //If other lock (in child listener) tooked before good nothing to do, if other lock tooked after need to rerun
            //if other lock tooked before he will not get any value for the key, if after he will. Assuming he try to get the value inside the lock
        }

    }

    public static void removeListener() {
        //TODO implement : local or non local is dealt in different way
        //TODO online : get the onlineListener array list in syncronised before removing the key and get normal listener in synchronyse also
        //TODO: remove the element from array before removing the key so that the array size ==0
    }

    ;

    public static void getOnce(String userId, StringValueListener nameValueListener) {
        //no need to store or do extra steps as it is only run once
        if (isUserHaveNickname(userId)) {
            nameValueListener.onDone(idToNickname.get(userId));
        } else {
            new DatabaseUser(userId).getRealName_Once_AndThen(nameValueListener);
        }
    }


    public static void setNickNameSyncWithDatabase() {
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

    //TODO to test just make the above scenario happen and check the value of (volatile) value who's value is the value return as the name

    //call to that should be surrounded by synchronized (idToNameListeners) otherwise undefined behavior
    private static final class FriendsAndNicknameMapChildListener implements MapStringStringChildListener {

        private static boolean hasListeners(@NotNull ArrayList<StringValueListener> nameListeners){
            return nameListeners.size()>0;
        }
        private static void fromNoNicknameToNicknameClean(@NotNull DatabaseUser databaseUser) {
            ArrayList<StringValueListener> nameOnlineListeners = idToNameOnlineListeners.get(databaseUser.getId());
            assert nameOnlineListeners != null;
            for (StringValueListener nameOnlineListener : nameOnlineListeners) {
                databaseUser.removeRealNameListener(nameOnlineListener);
            }
            //TODO check if we can remove the key as we don't need anymore the values in the array, as it will be recreated next time

        }

        private static void toNewNickname(ArrayList<StringValueListener> nameListeners, @NotNull String value) {
            for (StringValueListener nameListener : nameListeners) {
                nameListener.onDone(value);
            }
        }

        private static void fromNicknameToNoNickname(@NotNull ArrayList<StringValueListener> nameListeners, @NotNull DatabaseUser databaseUser) {
            //TODO check that the deleter can not delete while here, normally shoudl take the lock as write
            ArrayList<StringValueListener> nameOnlineListeners = new ArrayList<>(nameListeners.size());
            idToNameOnlineListeners.put(databaseUser.getId(), nameOnlineListeners);
            for (StringValueListener nameListener : nameListeners) {
                StringValueListener nameOnlineListener = getRunnable(nameListener, false);
                nameOnlineListeners.add(nameOnlineListener);
                databaseUser.getRealNameAndThen(nameOnlineListener);
            }
        }

        @Nullable
        private static ArrayList<StringValueListener> getNameListeners(String key, @Nullable String value) {
            ArrayList<StringValueListener> nameListeners = idToNameListeners.get(key);
            if (nameListeners == null) {
                lockForNoKeyValuePair.lock();
                nameListeners = idToNameOnlineListeners.get(key);
                if (nameListeners == null) {
                    updateIdToNickname(key, value);
                    lockForNoKeyValuePair.unlock();
                    return null;
                }
                lockForNoKeyValuePair.unlock();
            }
            return nameListeners;
        }

        private static void updateIdToNickname(String key, @Nullable String value) {
            if(value==null){
                idToNickname.remove(key);
            }else{
                idToNickname.put(key, value);
            }
        }

        //Assume that access to idToNickname is done serially
        @Override
        //Called when we have a new friend or at the beginning of the listening
        public void childAdded(String key, @Nullable String value) {
            if (value != null) {
                //value == null would mean a new friend but with no nickname so he should be addressed like before, i.e. no action needed
                //I assume that idToNickname has no value for key since the child has just been added => if their are listener they are listening online
                ArrayList<StringValueListener> nameListeners = getNameListeners(key, value);
                if (nameListeners == null) {
                    return;
                }
                synchronized (nameListeners) {
                    //do not need write lock here as we are in synchronized statement and so there is a happen before relation sheep with the addListener function
                    updateIdToNickname(key, value);
                    //is possible when we delete the last listener for the userId == key
                    if(hasListeners(nameListeners)){
                        DatabaseUser databaseUser = new DatabaseUser(key);
                        fromNoNicknameToNicknameClean(databaseUser);
                        toNewNickname(nameListeners, value);
                    }
                }
            }
        }

        @Override
        //Called when the Nickname change or there is no more Nickname
        public void childChanged(String key, @Nullable String value) {
            ArrayList<StringValueListener> nameListeners = getNameListeners(key, value);
            if (nameListeners == null) {
                return;
            }
            synchronized (nameListeners) {
                if(hasListeners(nameListeners)){
                    DatabaseUser databaseUser = new DatabaseUser(key);
                    if (!isUserHaveNickname(key)) {
                        fromNoNicknameToNicknameClean(databaseUser);
                    }
                    //do not need write lock here as we are in synchronized statement and so there is a happen before relation sheep with the addListener function
                    updateIdToNickname(key,value);
                    if (value == null) {
                        fromNicknameToNoNickname(nameListeners, databaseUser);
                    } else {
                        toNewNickname(nameListeners, value);
                    }
                }else{
                    updateIdToNickname(key, value);
                }

            }
        }

        @Override
        public void childRemoved(String key, @Nullable String value) {
            //not friend anymore
            ArrayList<StringValueListener> nameListeners = getNameListeners(key, null);
            if (nameListeners == null) {
                return;
            }
            synchronized (nameListeners) {
                if(hasListeners(nameListeners)){
                    DatabaseUser databaseUser = new DatabaseUser(key);
                    if (isUserHaveNickname(key)) {
                        fromNicknameToNoNickname(nameListeners, databaseUser);
                    }
                }
                //do not need write lock here as we are in synchronized statement and so there is a happen before relation sheep with the addListener function
                updateIdToNickname(key, null);
            }
        }

        @Override
        public void childMoved(String key, @Nullable String value) {
            //don't care
        }
    }
}

