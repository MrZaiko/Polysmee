package io.github.polysmee.utils;

import androidx.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.HashSet;

//TODO documentation
//not thread safe
public final class HashMapPutObservable<K,V> extends HashMap<K,V> {
    //in respect to equal (and so hash)
    HashMap<Object,HashSet<HashMapPutListener>> keyTohashMapPutListeners = new HashMap<>();
    @Nullable
    @Override
    public V put(@NotNull K key, V value) {
        HashSet<HashMapPutListener> hashMapPutListeners = keyTohashMapPutListeners.get(key);
        if (hashMapPutListeners!=null){
            for (HashMapPutListener hashMapPutListener : hashMapPutListeners){
                //TODO launch in a new thead;
                hashMapPutListener.onPut();
            }
        }
        return super.put(key, value);
    }


    public void addListener(HashMapPutListener hashMapPutListener, @NotNull K key){
        HashSet<HashMapPutListener> hashMapPutListeners = keyTohashMapPutListeners.get(key);
        if (hashMapPutListeners==null){
            hashMapPutListeners=new HashSet<>();
            keyTohashMapPutListeners.put(key, hashMapPutListeners);
        }
        hashMapPutListeners.add(hashMapPutListener);
    }

    public void removeListener(HashMapPutListener hashMapPutListener, @NotNull K key){
        HashSet<HashMapPutListener> hashMapPutListeners = keyTohashMapPutListeners.get(key);
        if (hashMapPutListeners!=null){
            hashMapPutListeners.remove(hashMapPutListener);
            if (hashMapPutListeners.isEmpty()){
                keyTohashMapPutListeners.remove(key);
            }
        }
    }
}
