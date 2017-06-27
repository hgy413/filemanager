package com.jb.filemanager.manager;

import android.content.Context;
import android.text.TextUtils;

import com.jb.filemanager.TheApplication;
import com.jb.filemanager.database.provider.DataProvider;
import com.jb.filemanager.function.scanframe.bean.appBean.AppItemInfo;
import com.jb.filemanager.function.scanframe.clean.AppManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;

/**
 * Created by bill wang on 17/3/3.
 *
 */

public class IgnoreListManager {

    @SuppressWarnings("unused")

    private static IgnoreListManager sInstance;
    private Context mContext;
    private DataProvider mDataProvider;

    private LinkedHashSet<String> mIgnoreList = null;

    public static IgnoreListManager getInstance() {
        synchronized (IgnoreListManager.class) {
            if (sInstance == null) {
                sInstance = new IgnoreListManager(TheApplication.getInstance());
            }
            return sInstance;
        }
    }

    public IgnoreListManager(Context context) {
        mDataProvider = new DataProvider(context);
        mContext = context;
        mIgnoreList = mDataProvider.getIgnoreList();
    }

    /**
     * 通过包名判断是否在白名单中
     * @param packageName
     * @return
     */
    public boolean isInIgnoreList(String packageName) {
        return mIgnoreList.contains(packageName);
    }

    /**
     * 将包名从白名单中移除
     * @param packageName
     */
    public void removeFromIgnoreList(String packageName) {
        if (!TextUtils.isEmpty(packageName)) {
            mIgnoreList.remove(packageName);
            mDataProvider.deleteFromIgnoreList(packageName);
        }
    }

    /**
     * 将包名加入白名单中
     * @param packageName
     */
    public void addToIgnoreList(String packageName) {
        if (!TextUtils.isEmpty(packageName)) {
            mIgnoreList.add(packageName);
            mDataProvider.insertIntoIgnoreList(packageName);
        }
    }

    /**
     * 获取已安装的白名单列表
     * <注意> 会过滤掉一些系统进程和重要进程
     * @return
     */
    public ArrayList<AppItemInfo> getInstalledIgnoreList() {
        ArrayList<AppItemInfo> apps = new ArrayList<AppItemInfo>();
        ArrayList<AppItemInfo> allApps = AppManager.getIntance().getAllApps();
        for (AppItemInfo app : allApps) {
            final String pkgName = app.getAppPackageName();
            if (EssentialProcessFilter.isEssentialProcess(pkgName)
                    || EssentialProcessFilter.isEssentialProcessMock(pkgName, app.getIsSysApp())) {
                continue;
            }
            if (isInIgnoreList(pkgName)) {
                apps.add(app);
            }
        }
        // 按照数据库中获取的数据顺序排序，后面插入的数据在最前面显示
        Collections.sort(apps, new Comparator<AppItemInfo>() {
            final ArrayList<String> mList = new ArrayList<String>(mIgnoreList);
            @Override
            public int compare(AppItemInfo l, AppItemInfo r) {
                String l_pkg = l.getAppPackageName();
                String r_pkg = r.getAppPackageName();
                int l_idx = mList.indexOf(l_pkg);
                int r_idx = mList.indexOf(r_pkg);
                return l_idx < r_idx ? 1 : (l_idx == r_idx ? 0 : -1);
            }
        });
        return apps;
    }

}
