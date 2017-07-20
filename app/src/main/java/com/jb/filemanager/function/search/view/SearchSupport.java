package com.jb.filemanager.function.search.view;

import android.app.Application;
import android.content.Context;

import com.jb.filemanager.TheApplication;

/**
 * Created by bill wang on 2017/7/20.
 *
 */

public class SearchSupport implements SearchContract.Support {
    @Override
    public Context getContext() {
        return TheApplication.getInstance();
    }

    @Override
    public Application getApplication() {
        return TheApplication.getInstance();
    }
}
