package com.jb.filemanager.ad.event;

/**
 * Created by bill wang on 17/1/19.
 *
 */

public class OnAdCloseEvent {
    int mAdType;

    int mEntrance;

    int mHashCode;


    public OnAdCloseEvent(int adType, int entrance, int hashCode) {
        mAdType = adType;
        mEntrance = entrance;
        mHashCode = hashCode;
    }

    public int getAdType() {
        return mAdType;
    }

    public void setAdType(int adType) {
        mAdType = adType;
    }

    public int getEntrance() {
        return mEntrance;
    }

    public void setEntrance(int entrance) {
        mEntrance = entrance;
    }

    public int getHashCode() {
        return mHashCode;
    }

    public void setHashCode(int hashCode) {
        mHashCode = hashCode;
    }
}
