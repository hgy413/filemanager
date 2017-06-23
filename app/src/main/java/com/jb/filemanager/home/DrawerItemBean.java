package com.jb.filemanager.home;

/**
 * Desc:
 * Author lqf
 * Email: liqf@m15.cn
 * Date: 2017/6/7 20:15
 */

class DrawerItemBean {
    public String mItemName;
    public int mItemIconResId;
    public String mTag;

    public DrawerItemBean() {
    }

    public DrawerItemBean(String itemName, int itemIconResId, String tag) {
        mItemName = itemName;
        mItemIconResId = itemIconResId;
        mTag = tag;
    }
}