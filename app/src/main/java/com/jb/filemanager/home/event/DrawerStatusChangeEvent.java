package com.jb.filemanager.home.event;

/**
 * Created by bill wang on 2017/7/27.
 *
 */

public class DrawerStatusChangeEvent {
    private boolean mDrawerIsOpen;

    public DrawerStatusChangeEvent(boolean isOpen) {
        mDrawerIsOpen = isOpen;
    }

    public boolean getDrawerIsOpen() {
        return mDrawerIsOpen;
    }
}
