package com.jb.filemanager.function.setting;

import android.content.Context;

import com.jb.filemanager.TheApplication;

/**
 * Created by miwo on 2016/9/2.
 *
 */
class SettingSupport implements SettingContract.Support {

    @Override
    public Context getContext() {
        return TheApplication.getInstance();
    }
}
