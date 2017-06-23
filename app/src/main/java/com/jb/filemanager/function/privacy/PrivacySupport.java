package com.jb.filemanager.function.privacy;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.jb.filemanager.TheApplication;
import com.jb.filemanager.manager.PackageManagerLocker;

/**
 * Created by nieyh on 2016/10/12.
 *
 */
class PrivacySupport implements PrivacyContract.Support {

    @Override
    public String gainVersion() {
        try {
            PackageManager manager = PackageManagerLocker.getInstance().getPackageManager();
            PackageInfo info = manager.getPackageInfo(TheApplication.getInstance().getPackageName(), 0);
            return info.versionName;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}
