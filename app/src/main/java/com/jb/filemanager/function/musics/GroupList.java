package com.jb.filemanager.function.musics;


import android.support.v4.util.ArrayMap;

import java.util.ArrayList;

/**
 * Created by bool on 17-7-3.
 */

public class GroupList<K, V> extends ArrayMap<K, ArrayList<V>> {

    public int itemSize(){
        int count = 0;
        for (int i = 0; i < size(); i++) {
            count += valueAt(i).size();
        }
        return count;
    }

    public K getGroupKey(int index) {
        int groupSize = this.size();
        int groupIndex = 0;
        int i = 0;
        for(i = 0; i < groupSize ; i++){
            if (index > this.valueAt(i).size()) {
                index -= this.valueAt(i).size();
            } else {
                return this.keyAt(i);
            }
        }
        return null;
    }

    public V getItem(int index) {
        int groupSize = this.size();
        int groupIndex = 0;
        int i = 0;
        for(i = 0; i < groupSize ; i++){
            if (index > this.valueAt(i).size()) {
                index -= this.valueAt(i).size();
            } else {
                return valueAt(i).get(index);
            }
        }
        return null;
    }

    public ArrayList<V> getGroup(int index){
        return this.get(getGroupKey(index));
    }

    public void addToGroup(int groupIndex, V value) {
        this.get(groupIndex).add(value);
    }
}
