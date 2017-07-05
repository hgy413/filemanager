package com.jb.filemanager.function.apkmanager;

import com.jb.filemanager.function.scanframe.bean.appBean.AppItemInfo;
import com.jb.filemanager.function.scanframe.clean.AppManager;

import java.util.List;

/**
 * Desc:
 * Author lqf
 * Email: liqf@m15.cn
 * Date: 2017/6/29 13:37
 */

class AppManagerSupport implements AppManagerContract.Support {
    /*@Override
    public List<AppChildBean> getInstallAppInfo() {
        PackageManager packageManager = TheApplication.getAppContext().getPackageManager();
        List<ApplicationInfo> apps = packageManager.getInstalledApplications(0);
        List<AppChildBean> baseAppBeenList = new ArrayList<>();
        for (ApplicationInfo info : apps) {
            AppChildBean baseAppBean = new AppChildBean();
            baseAppBean.mAppName = info.loadLabel(packageManager).toString();
            baseAppBean.mPackageName = info.packageName;
            baseAppBean.mIsSysApp = AppUtils.isSystemApp(info);
            baseAppBeenList.add(baseAppBean);
        }
        return baseAppBeenList;
    }*/

    @Override
    public List<AppItemInfo> getInstallAppInfo() {
        return AppManager.getIntance().getAllApps();
    }
}
