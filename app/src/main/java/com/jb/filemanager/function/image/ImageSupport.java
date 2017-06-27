package com.jb.filemanager.function.image;

import android.app.Application;
import android.content.Context;

import com.jb.filemanager.TheApplication;

/**
 * Created by bill wang on 2017/6/27.
 *
 */

class ImageSupport implements ImageContract.Support {
    @Override
    public Context getContext() {
        return TheApplication.getInstance();
    }

    @Override
    public Application getApplication() {
        return TheApplication.getInstance();
    }
}
