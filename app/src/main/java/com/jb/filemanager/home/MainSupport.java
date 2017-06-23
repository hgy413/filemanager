package com.jb.filemanager.home;

import android.app.Application;
import android.content.Context;

import com.jb.filemanager.TheApplication;

/**
 * Created by bill wang on 2017/6/21.
 *
 */

class MainSupport implements MainContract.Support {

    @Override
    public Context getContext() {
        return TheApplication.getInstance();
    }

    @Override
    public Application getApplication() {
        return TheApplication.getInstance();
    }
}
