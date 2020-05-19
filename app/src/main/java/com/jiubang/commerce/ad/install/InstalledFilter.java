package com.jiubang.commerce.ad.install;

import android.content.Context;
import android.content.Intent;
import com.jb.ga0.commerce.util.LogUtils;
import com.jiubang.commerce.ad.bean.AdInfoBean;
import com.jiubang.commerce.ad.install.InstallBroadcaster;
import com.jiubang.commerce.database.model.InstalledPkgBean;
import com.jiubang.commerce.database.table.InstalledPkgTable;
import com.jiubang.commerce.utils.AppUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class InstalledFilter implements InstallBroadcaster.IInstallListener {
    private static final String TAG = "InstalledFilter";
    public static boolean sHasStarted = false;
    private Context mContext;
    private ExecutorService mExecutorService = Executors.newSingleThreadExecutor();
    /* access modifiers changed from: private */
    public InstalledPkgTable mInstalledPkgTable;

    public InstalledFilter(Context context, String cid) {
        this.mContext = context;
        this.mInstalledPkgTable = InstalledPkgTable.getInstance(context);
    }

    public void start() {
        if (!sHasStarted) {
            sHasStarted = true;
            this.mInstalledPkgTable.deleteExpiredData();
            registerInstallReceiver();
        }
    }

    public void onPackageInstalled(final String packageName, Intent intent) {
        this.mExecutorService.execute(new Runnable() {
            public void run() {
                List<InstalledPkgBean> beanList = new ArrayList<>();
                InstalledPkgBean bean = new InstalledPkgBean();
                bean.setPackageName(packageName);
                bean.setUpdateTime(System.currentTimeMillis());
                beanList.add(bean);
                InstalledFilter.this.mInstalledPkgTable.insertData(beanList);
            }
        });
    }

    private void registerInstallReceiver() {
        InstallBroadcaster.getInstance(this.mContext).registerListener(this);
    }

    private void unregisterInstallReceiver() {
        InstallBroadcaster.getInstance(this.mContext).unregisterListener(this);
    }

    public void cleanUp() {
        this.mExecutorService.shutdownNow();
        this.mExecutorService = null;
        unregisterInstallReceiver();
        sHasStarted = false;
    }

    public static List<AdInfoBean> filter(Context context, List<AdInfoBean> adInfoList) {
        if (LogUtils.isShowLog()) {
            LogUtils.d(TAG, "sHasStarted=" + sHasStarted);
        }
        return filter(context, adInfoList, (List<String>) null);
    }

    public static List<AdInfoBean> filter(Context context, List<AdInfoBean> adInfoList, List<String> installFilterException) {
        if (adInfoList == null || adInfoList.isEmpty()) {
            return adInfoList;
        }
        List<InstalledPkgBean> installedPkgList = InstalledPkgTable.getInstance(context).getValidData();
        if (installedPkgList.isEmpty()) {
            return adInfoList;
        }
        List<AdInfoBean> filteredList = new ArrayList<>();
        for (AdInfoBean adBean : adInfoList) {
            if (!needFiltered(adBean.getPackageName(), installedPkgList, installFilterException)) {
                filteredList.add(adBean);
            }
        }
        return filteredList;
    }

    public static List<AdInfoBean> simpleFilter(Context context, List<AdInfoBean> adInfoList, List<String> installFilterException) {
        if (adInfoList == null || adInfoList.isEmpty()) {
            return adInfoList;
        }
        List<AdInfoBean> filteredList = new ArrayList<>();
        for (AdInfoBean adBean : adInfoList) {
            String pkgName = adBean.getPackageName();
            if ((installFilterException != null && installFilterException.contains(pkgName)) || !AppUtils.isAppExist(context, pkgName)) {
                filteredList.add(adBean);
            }
        }
        return filteredList;
    }

    private static boolean needFiltered(String packageName, List<InstalledPkgBean> installedPkgList, List<String> installFilterException) {
        for (InstalledPkgBean bean : installedPkgList) {
            if ((installFilterException == null || !installFilterException.contains(packageName)) && bean.getPackageName().equals(packageName)) {
                return true;
            }
        }
        return false;
    }
}
