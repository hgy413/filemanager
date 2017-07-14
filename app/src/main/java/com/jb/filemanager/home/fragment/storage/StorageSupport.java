package com.jb.filemanager.home.fragment.storage;

import android.app.Application;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;

import com.jb.filemanager.TheApplication;

import java.lang.ref.WeakReference;

/**
 * Created by bill wang on 2017/7/14.
 *
 */

public class StorageSupport implements StorageContract.Support {

    WeakReference<Fragment> mFragmentRef;

    StorageSupport(Fragment fragment) {
        mFragmentRef = new WeakReference<Fragment>(fragment);
    }

    @Override
    public Context getContext() {
        return TheApplication.getInstance();
    }

    @Override
    public Application getApplication() {
        return TheApplication.getInstance();
    }

    @Override
    public LoaderManager getLoaderManager() {
        LoaderManager loaderManager = null;
        if (mFragmentRef != null && mFragmentRef.get() != null) {
            loaderManager = mFragmentRef.get().getLoaderManager();
        }
        return loaderManager;
    }
}
