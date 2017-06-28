package com.jb.filemanager.function.applock.model.dao;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ResolveInfo;

import com.jb.filemanager.R;
import com.jb.filemanager.TheApplication;
import com.jb.filemanager.function.applock.database.LockerDatabaseHelper;
import com.jb.filemanager.function.applock.model.bean.LockerGroup;
import com.jb.filemanager.function.applock.model.bean.LockerItem;
import com.jb.filemanager.function.applock.model.bean.RecommendLockApp;
import com.jb.filemanager.util.AppUtils;
import com.jb.filemanager.util.FileUtil;
import com.jb.filemanager.util.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * 关于锁信息的数据管理
 *
 * @author zhanghuijun
 */
public class LockerDao {

    public static final String TAG = "zhanghuijun LockerModel";

    private List<ResolveInfo> mResolveInfos;

    private List<ComponentName> mComponentNames;

    private List<String> mLauncherList;
    /**
     * Locker表的数据库管理
     */
    private LockerDatabaseHelper mLockerDatabaseHelper = null;
    /**
     * Context
     */
    private Context mContext = null;

    public LockerDao(Context context) {
        mContext = context;
        mLockerDatabaseHelper = LockerDatabaseHelper.getInstance();
    }

    public LockerGroup getLockerInfos() {
        mLauncherList = AppUtils.getLauncherPackageNames(mContext);
        if (null == mLauncherList) {
            mLauncherList = new ArrayList<String>();
        }
        mLauncherList.add(mContext.getPackageName());

        mComponentNames = mLockerDatabaseHelper.queryLockerInfo();
        mResolveInfos = AppUtils.getLauncherApps(mContext);
        LockerGroup lockerGroup = new LockerGroup();
        parseApp(lockerGroup);
        return lockerGroup;
    }

    public List<ComponentName> queryLockerInfo() {
        return mLockerDatabaseHelper.queryLockerInfo();
    }

    private void parseApp(LockerGroup mLockerGroup) {
        if (null != mResolveInfos && !mResolveInfos.isEmpty()) {
            //此处判断是否为桌面程序 和 设置程序
            for (ResolveInfo resolveInfo : mResolveInfos) {
                LockerItem lockerItem = new LockerItem();
                lockerItem.setComponentName(resolveInfo);
                lockerItem.setResolveInfo(resolveInfo);
                boolean isFind = false;
                if (null != mLauncherList && !mLauncherList.isEmpty()) {
                    for (String pkg : mLauncherList) {
                        if (pkg.equals(lockerItem.componentName.getPackageName())) {
                            isFind = true;
                            break;
                        }
                    }
                }
                //排除设置页
                if (lockerItem.componentName.getPackageName().equals("com.android.settings")) {
                    isFind = true;
                }

                if (!isFind) {
                    //修改 check 标示  （数据库中保存上次加锁的应用）
                    checkStatus(lockerItem);
                    mLockerGroup.addLockerItem(lockerItem);
                }
            }
            //此处对于所有程序进行排序
            sort(mLockerGroup);
        } else {
            Logger.e(TAG, "查询不到手机安装应用");
        }
    }

    private void checkStatus(LockerItem lockerItem) {
        if (mComponentNames != null) {
            for (int i = 0; i < mComponentNames.size(); i++) {
                ComponentName componentName = mComponentNames.get(i);
                if (componentName.equals(lockerItem.componentName)) {
                    lockerItem.isChecked = true;
                }
            }
        }
    }


    private void sort(LockerGroup mLockerGroup) {
        /**
         * 排序
         * 1、将已经选择的应用排在前面
         * 2、将默认顶部的列表 排在前面
         * 3、其他的在后面
         * */
        List<LockerItem> result = new ArrayList<LockerItem>();
        List<LockerItem> lockerItems = mLockerGroup.getLockerItems();

        //筛选选中状态
        Iterator<LockerItem> iterator = lockerItems.iterator();
        while (iterator.hasNext()) {
            LockerItem itemInfo = iterator.next();
            if (itemInfo.isChecked) {
                result.add(itemInfo);
                iterator.remove();
            }
        }
        sortByTitle(result);

        if (!lockerItems.isEmpty()) {
            //筛选top排名
            String[] appTop1000ByRaw = openRawListFile(mContext, R.raw.apptoplist);
            if (null != appTop1000ByRaw) {
                for (String string : appTop1000ByRaw) {
                    iterator = lockerItems.iterator();
                    while (iterator.hasNext()) {
                        LockerItem itemInfo = iterator.next();
                        if (string.equals(itemInfo.componentName.getPackageName())) {
                            result.add(itemInfo);
                            iterator.remove();
                        }
                    }
                }
            }
        }

        if (!lockerItems.isEmpty()) {
            sortByTitle(lockerItems);
            result.addAll(lockerItems);
        }

        mLockerGroup.setLockerItems(result);
    }

    private void sortByTitle(List<LockerItem> lockerItems) {
        final Collator collator = Collator.getInstance();
        Collections.sort(lockerItems, new Comparator<LockerItem>() {
            @Override
            public int compare(LockerItem a, LockerItem b) {
                int result = collator.compare(a.getTitle() == null ? "" : a.getTitle().trim(), b.getTitle() == null ? "" : b.getTitle().trim());
                if (result == 0) {
                    try {
                        result = a.componentName.compareTo(b.componentName);
                    } catch (Exception e) {
                    }
                }
                return result;
            }
        });
    }

    private String[] openRawListFile(Context mContext, int resId) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(mContext.getResources().openRawResource(resId)));
            String str;
            StringBuffer buffer = new StringBuffer();
            while ((str = reader.readLine()) != null) {
                buffer.append(str);
            }
            return buffer.toString().split("\\|\\|");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != reader) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * 加锁一组选项
     */
    public void lockItem(LockerItem... lockerItems) {
        for (LockerItem lockerItem : lockerItems) {
            lockerItem.isChecked = true;
        }
        mLockerDatabaseHelper.lockItem(lockerItems);
    }


    /**
     * 解锁一组选项
     */
    public void unlockItem(LockerItem... lockerItems) {
        for (LockerItem lockerItem : lockerItems) {
            lockerItem.isChecked = false;
        }
        mLockerDatabaseHelper.unlockItem(lockerItems);
    }

    /**
     * 解锁一组选项
     */
    public void unlockItem(String packageName) {
        mLockerDatabaseHelper.unlockItem(packageName);
    }


    /**
     * 获取推荐需要加锁的信息列表（用于新手推荐）
     */
    public List<LockerItem> getRecommendLockerData() {
        LockerGroup databaseLockerGroup = getLockerInfos();
        List<String> list = new ArrayList<String>();
        list.addAll(decodeFile());

        List<LockerItem> result = new ArrayList<LockerItem>();
        Iterator<LockerItem> iterator = databaseLockerGroup.getLockerItems().iterator();
        while (iterator.hasNext()) {
            LockerItem lockerItem = iterator.next();
            if (list.contains(lockerItem.componentName.getPackageName())) {
                iterator.remove();
                lockerItem.isChecked = false;
                result.add(lockerItem);
                if (result.size() >= 5) {
                    break;
                }
            }
        }
        return result;
    }

    /**
     * 是否存在锁定的APP
     * */
    public boolean isHaveLockerApp() {
        if (mLockerDatabaseHelper != null) {
            return mLockerDatabaseHelper.isHaveLockerApp();
        }
        return false;
    }

    /**
     * 获取推荐需要加锁的信息列表（用于新手推荐）
     */
    private List<String> decodeFile() {
        ArrayList<String> result = new ArrayList<String>();
        String raw = FileUtil.getAllStrDataFromRaw(TheApplication.getAppContext(), R.raw.recommend_applock_list);
        try {
            JSONArray array = new JSONArray(raw);
            ArrayList<RecommendLockApp> list = new ArrayList<RecommendLockApp>();
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                RecommendLockApp app = new RecommendLockApp();
                app.setPackageName(object.optString(RecommendLockApp.PACKAGE_NAME));
                app.setLevel(object.optInt(RecommendLockApp.LEVEL));
                list.add(app);
            }
            RecommendLockAppComparator comparator = new RecommendLockAppComparator();
            Collections.sort(list, comparator);
            for (RecommendLockApp app : list) {
                result.add(app.getPackageName());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 推荐加锁应用排序：按级别
     */
    private static class RecommendLockAppComparator implements Comparator<RecommendLockApp> {
        @Override
        public int compare(RecommendLockApp lhs, RecommendLockApp rhs) {
            if (lhs.getLevel() < rhs.getLevel()) {
                return -1;
            } else if (lhs.getLevel() > rhs.getLevel()) {
                return 1;
            } else {
                return 0;
            }
        }
    }
}
