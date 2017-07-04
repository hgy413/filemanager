package com.jb.filemanager.function.image.presenter;

import android.app.Application;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import com.jb.filemanager.TheApplication;
import com.jb.filemanager.manager.file.FileManager;

/**
 * Created by bill wang on 2017/6/27.
 *
 */

public class ImageSupport implements ImageContract.Support {
    @Override
    public Context getContext() {
        return TheApplication.getInstance();
    }

    @Override
    public Application getApplication() {
        return TheApplication.getInstance();
    }
}
