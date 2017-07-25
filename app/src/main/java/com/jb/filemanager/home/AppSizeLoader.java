package com.jb.filemanager.home;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.support.v4.content.AsyncTaskLoader;

import com.jb.filemanager.Const;
import com.jb.filemanager.manager.PackageManagerLocker;
import com.jb.filemanager.util.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * A custom Loader that loads all of the installed applications.
 */
class AppSizeLoader extends AsyncTaskLoader<List<Long>> {

    private List<Long> mApps;

    AppSizeLoader(Context context) {
        super(context);

        // Retrieve the package manager for later use; note we don't
        // use 'context' directly but instead the save global application
        // context returned by getContext().
    }

    /**
     * This is where the bulk of our work is done. This function is called in a
     * background thread and should generate a new set of data to be published
     * by the loader.
     */
    @Override
    public List<Long> loadInBackground() {
        // Retrieve all known applications.
        List<ApplicationInfo> apps = PackageManagerLocker.getInstance().getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);
        if (apps == null) {
            apps = new ArrayList<>();
        }

        // Create corresponding array of entries and load their labels.
        int size = apps.size();
        List<Long> result = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            ApplicationInfo applicationInfo = apps.get(i);
            if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0)
                continue;
            if ((applicationInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0)
                continue;
            // 为了和点进去显示的一致，不过滤没有启动项的
//            if (PackageManagerLocker.getInstance().getLaunchIntentForPackage(applicationInfo.packageName) == null)
//                continue;

            // 自己不算
            if (applicationInfo.packageName.equals(Const.PACKAGE_NAME))
                continue;
            
            File apk = new File(applicationInfo.sourceDir);
            result.add(apk.length());
        }

        // Done!
        return result;
    }

    /**
     * Called when there is new data to deliver to the client. The super class
     * will take care of delivering it; the implementation here just adds a
     * little more logic.
     */
    @Override
    public void deliverResult(List<Long> apps) {
        if (isReset()) {
            // An async query came in while the loader is stopped. We
            // don't need the result.
            if (apps != null) {
                onReleaseResources(apps);
            }
        }
        List<Long> oldApps = mApps;
        mApps = apps;

        if (isStarted()) {
            // If the Loader is currently started, we can immediately
            // deliver its results.
            super.deliverResult(apps);
        }

        // At this point we can release the resources associated with
        // 'oldApps' if needed; now that the new result is delivered we
        // know that it is no longer in use.
        if (oldApps != null) {
            onReleaseResources(oldApps);
        }
    }

    /**
     * Handles a request to start the Loader.
     */
    @Override
    protected void onStartLoading() {
        if (mApps != null) {
            // If we currently have a result available, deliver it
            // immediately.
            deliverResult(mApps);
        }

        // Has something interesting in the configuration changed since we
        // last built the app list?
        if (takeContentChanged() || mApps == null) {
            // If the data has changed since the last time it was loaded
            // or is not currently available, start a load.
            forceLoad();
        }
    }

    /**
     * Handles a request to stop the Loader.
     */
    @Override
    protected void onStopLoading() {
        // Attempt to cancel the current load task if possible.
        cancelLoad();
    }

    /**
     * Handles a request to cancel a load.
     */
    @Override
    public void onCanceled(List<Long> apps) {
        super.onCanceled(apps);

        // At this point we can release the resources associated with 'apps'
        // if needed.
        onReleaseResources(apps);
    }

    /**
     * Handles a request to completely reset the Loader.
     */
    @Override
    protected void onReset() {
        super.onReset();

        // Ensure the loader is stopped
        onStopLoading();

        // At this point we can release the resources associated with 'apps'
        // if needed.
        if (mApps != null) {
            onReleaseResources(mApps);
            mApps = null;
        }
    }

    /**
     * Helper function to take care of releasing resources associated with an
     * actively loaded data set.
     */
    private void onReleaseResources(List<Long> apps) {
        // For a simple List<> there is nothing to do. For something
        // like a Cursor, we would close it here.
    }

}