package com.jb.filemanager.function.music;

import android.app.Application;
import android.content.Context;

import com.jb.filemanager.TheApplication;
import com.jb.filemanager.function.image.ImageContract;

/**
 * Created by bill wang on 2017/6/28.
 */

public class MusicSupport implements MusicContract.Support {
    @Override
    public Context getContext() {
        return TheApplication.getInstance();
    }

    @Override
    public Application getApplication() {
        return TheApplication.getInstance();
    }
}
