package com.jb.filemanager.function.apkmanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.jb.filemanager.TheApplication;
import com.jb.filemanager.function.scanframe.clean.event.PackageAddedEvent;
import com.jb.filemanager.function.scanframe.clean.event.PackageRemovedEvent;
import com.jb.filemanager.util.Logger;

/**
 * Desc:
 * Author lqf
 * Email: liqf@m15.cn
 * Date: 2017/7/18 18:32
 */

public class AppChangeReceiver extends BroadcastReceiver {
    private final static String LOG_TAG = "PackageEventReceiver";

    private PackageEventListener mPackageEventListener;
    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        if (Intent.ACTION_PACKAGE_ADDED.equals(action)) {
            String pkgName = intent.getDataString();
            pkgName = pkgName.replace("package:", "");
            boolean replacing = intent.getBooleanExtra(
                    Intent.EXTRA_REPLACING, false);
            if (!TextUtils.isEmpty(pkgName) && !replacing) {
                Logger.d(LOG_TAG, "ACTION_PACKAGE_ADDED: " + pkgName);
                if (mPackageEventListener != null) {
                    mPackageEventListener.onPackageAdded(pkgName);
                }
                TheApplication.postEvent(new PackageAddedEvent(pkgName));
            }

        } else if (Intent.ACTION_PACKAGE_REMOVED.equals(action)) {
            String pkgName = intent.getDataString();
            pkgName = pkgName.replace("package:", "");
            boolean replacing = intent.getBooleanExtra(
                    Intent.EXTRA_REPLACING, false);
            if (!TextUtils.isEmpty(pkgName) && !replacing) {
                Logger.d(LOG_TAG, "ACTION_PACKAGE_REMOVED: " + pkgName);
                if (mPackageEventListener != null) {
                    mPackageEventListener.onPackageRemoved(pkgName);
                }
                TheApplication.postEvent(new PackageRemovedEvent(pkgName));
            }
        }
    }
    /**
     * 设置包事件监听器<br>
     *
     * @param l
     */
    public void setPackageEventListener(PackageEventListener l) {
        mPackageEventListener = l;
    }

    /**
     * Package Event Listener<br>
     *
     * @author laojiale
     */
    public interface PackageEventListener {

        void onPackageAdded(String packageName);

        void onPackageRemoved(String packageName);
    }

}
