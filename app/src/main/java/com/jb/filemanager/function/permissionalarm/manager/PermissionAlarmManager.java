package com.jb.filemanager.function.permissionalarm.manager;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.support.annotation.IntDef;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.Button;

import com.jb.filemanager.Const;
import com.jb.filemanager.TheApplication;
import com.jb.filemanager.database.params.InsertParams;
import com.jb.filemanager.database.params.UpdateParams;
import com.jb.filemanager.database.provider.AppPermissionsProvider;
import com.jb.filemanager.function.permissionalarm.utils.PermissionHelper;
import com.jb.filemanager.function.permissionalarm.view.PermissionAlarmPopActivity;
import com.jb.filemanager.manager.spm.IPreferencesIds;
import com.jb.filemanager.manager.spm.SharedPreferencesManager;
import com.jb.filemanager.util.AppUtils;
import com.jb.filemanager.util.Logger;
import com.jiubang.commerce.ad.sdk.MoPubAdConfig;
import com.jiubang.commerce.ad.sdk.MoPubNativeConfig;
import com.mopub.nativeads.MoPubStaticNativeAdRenderer;
import com.mopub.nativeads.ViewBinder;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by nieyh on 2017/2/7. <br/> 权限警报管理器
 */

public class PermissionAlarmManager {

    private static PermissionAlarmManager sIntance;

    private PermissionAlarmManager() {
        mAppPermissionsProvider = new AppPermissionsProvider(TheApplication.getAppContext());
        mBlackList = new ArrayList<>();
        mCacheMap = new HashMap<>();
        mPermissionHelper = new PermissionHelper();
        mBlackList.add("com.fingerprints.serviceext");
        mBlackList.add("com.svox.pico");
        mBlackList.add("com.google.android.gms");
        mBlackList.add("com.google.android.gsf.login");
        mBlackList.add("com.google.android.gsf");
        mBlackList.add("android");
    }

    public static PermissionAlarmManager getInstance() {
        if (sIntance == null) {
            sIntance = new PermissionAlarmManager();
        }
        return sIntance;
    }

    private static final int INVALID = -2;
    private static final int DENIED = -1;
    private static final int STATUS_BAR_SHOW = 1;
    private static final int STATUS_BAR_DISMISS = 2;

    /**
     * <ol> <li>INVALID : 无效标示</li> <li>DENIED : 悬浮窗没有添加成功</li> <li>STATUS_BAR_SHOW : 状态栏展示中</li>
     * <li>STATUS_BAR_DISMISS : 状态栏消失中</li> </ol>
     */
    @IntDef({DENIED, STATUS_BAR_SHOW, STATUS_BAR_DISMISS})
    @Retention(value = RetentionPolicy.SOURCE)
    private @interface StatusBarState {
    }

    //监听顶部应用信息的间隔时间
    private final int MONITOR_TOPAPP_INTERVAL = 2000;
    //是否已经注册
    private boolean isRegister = false;
    //上次的顶部的包名
    private ComponentName mLastPkgName;
    //上一次屏幕的方向
    private int mLastScreenOrientation;
    //检测应用信息的线程
    private Thread mCheckAppInfoThread;
    //黑名单
    private List<String> mBlackList;
    //缓存需要弹出的弹框
    private Map<String, List<String>> mCacheMap = new HashMap<>();
    //权限获取辅助类
    private PermissionHelper mPermissionHelper;
    //应用权限数据库
    private AppPermissionsProvider mAppPermissionsProvider;
    //用于检测状态栏是否展示的View
    private Button mStatusBarIsVisiableView;
    //桌面程序列表
    private List<String> mLauncherAppList;
    //屏幕状态
    private
    @StatusBarState
    int mLastStatusBarState = INVALID;

    private final String TAG = "PermissionAlarmManager";
    //监听应用安装
    BroadcastReceiver mAppAddedOrUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // 获取包名
            String packageName = intent.getData().getSchemeSpecificPart();
            //当不是当前应用
            if (!TextUtils.isEmpty(packageName)
                    && !packageName.contains(Const.PACKAGE_NAME)) {
                //处理
                checkPermissionNow(packageName, true);
            }
        }
    };

    /**
     * 查看开关是否开启
     *
     * @return 开启状态
     */
    public boolean isSwitchEnable() {
        //服务器配置 返回是否打开
        // TODO: 17-6-29 @nieyh 需要看是否依靠服务器控制
//        boolean isEnable = RemoteConfigManager.getInstance().getPermissionCheckConfig().funcEnable();
        //保持与服务器控制的一致
//        SharedPreferencesManager.getInstance(TheApplication.getAppContext()).commitBoolean(IPreferencesIds.KEY_PERMISSION_ALARM_ENABLE, isEnable);
        return SharedPreferencesManager.getInstance(TheApplication.getAppContext()).getBoolean(IPreferencesIds.KEY_PERMISSION_ALARM_ENABLE, true);
    }

    /**
     * 改变功能开关 详情：如果之前功能是关闭的，则执行则将开启功能.
     *
     * @return 切换后的状态
     */
    public boolean changerSwitch() {
        SharedPreferencesManager sharedPreferencesManager = SharedPreferencesManager.getInstance(TheApplication.getAppContext());
        boolean isEnable = sharedPreferencesManager.getBoolean(IPreferencesIds.KEY_PERMISSION_ALARM_ENABLE, true);
        isEnable = !isEnable;
        if (isEnable) {
            Logger.w(TAG, "开启权限检测开关");
            //扫描所有的权限到表中
//            scanAllPermissionToTableInsertFir();
            //初始化Windows
            initWindows();
            initLaunchApp();
            // 开启功能
            monitorAppInfoAndScreenState();
            monitorAppAddedOrUpdate();
        } else {
            Logger.w(TAG, "关闭权限检测开关");
            // 关闭功能
            stopMonitor();
        }
        sharedPreferencesManager.commitBoolean(IPreferencesIds.KEY_PERMISSION_ALARM_USER_HAS_CHANGE, true);
        sharedPreferencesManager.commitBoolean(IPreferencesIds.KEY_PERMISSION_ALARM_ENABLE, isEnable);
        return isEnable;
    }

    /**
     * 准备
     */
    public void toReady() {
        if (isSwitchEnable()) {
//            scanAllPermissionToTableInsertFir();
            initWindows();
            initLaunchApp();
            // 开启功能
            monitorAppInfoAndScreenState();
            monitorAppAddedOrUpdate();
        }
    }

    /**
     * 初始化悬浮窗界面
     */
    public void initWindows() {
        mStatusBarIsVisiableView = new Button(TheApplication.getAppContext());
        mStatusBarIsVisiableView.setBackgroundColor(Color.TRANSPARENT);
        WindowManager windowManager = (WindowManager) TheApplication.getAppContext().getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams wmParams = new WindowManager.LayoutParams();
        wmParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY;
        wmParams.format = PixelFormat.TRANSPARENT;
        wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
        wmParams.gravity = Gravity.TOP | Gravity.LEFT;
        wmParams.x = 100;
        wmParams.y = 0;
        wmParams.width = 200;
        wmParams.height = 200;
        try {
            windowManager.addView(mStatusBarIsVisiableView, wmParams);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化桌面程序
     */
    public void initLaunchApp() {
        mLauncherAppList = AppUtils.getLauncherPackageNames(TheApplication.getAppContext());
    }

    /**
     * 监听当前顶部应用 监听屏幕状态（是否横屏以及是否全屏）
     */
    private void monitorAppInfoAndScreenState() {
        mCheckAppInfoThread = new Thread() {
            @Override
            public void run() {
                while (true) {
                    try {
                        if (Thread.currentThread().isInterrupted()) {
                            return;
                        }
//                        checkTopAppState();
                        checkScreenState();
                        //等待间隔时间
                        try {
                            Thread.sleep(MONITOR_TOPAPP_INTERVAL);
                        } catch (InterruptedException e) {
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        mCheckAppInfoThread.start();
    }

    /**
     * 查看顶部信息变化
     */
    private void checkTopAppState() {
        try {
            //当有权限的时候
            if (AppUtils.isHaveUsageDataPermission()) {
                ComponentName pkgName = AppUtils.getTopPackageName(TheApplication.getAppContext());
                if (pkgName != null
                        && !pkgName.getPackageName().equalsIgnoreCase(Const.PACKAGE_NAME)
                        && !pkgName.getPackageName().equalsIgnoreCase("invalid_package_name")) {
                    //当当前应用与上一次检测的应用的包名不一致时
                    if (mLastPkgName != null && !mLastPkgName.getPackageName().equalsIgnoreCase(pkgName.getPackageName())) {
                        if (!TextUtils.isEmpty(mLastPkgName.getPackageName())
                                && mLauncherAppList != null
                                && mLauncherAppList.contains(mLastPkgName.getPackageName())) {
                            //如果上一个包是 桌面程序时 则保存当前包名 并退出 进入下一个检测
                            mLastPkgName = pkgName;
                            return;
                        }
                        if (!mAppPermissionsProvider.hasExitShowBefore(mLastPkgName.getPackageName())
                                && !isAtBlackList(mLastPkgName.getPackageName())) {
                            checkPermissionNow(mLastPkgName.getPackageName(), false);
                            mAppPermissionsProvider.update(mLastPkgName.getPackageName(), true);
                        }

                    }
                    mLastPkgName = pkgName;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 检查应用权限变化情况
     *
     * @param isNeedConsiderUpdate 是否需要考虑是否第一次安装等条件 <br/> note：isNeedConsiderUpdate 只有在应用退出时弹出的对话框
     *                             不考虑 其他情况都需要考虑
     */
    private void checkPermissionNow(String pkgName, boolean isNeedConsiderUpdate) {
        if (TextUtils.isEmpty(pkgName)) {
            return;
        }
        //展示弹框 并且保存数据
        String[] permissions = mPermissionHelper.getAppPermission(pkgName);
        if (permissions != null && permissions.length > 0) {
            List<String> sensitivePermissionsList = mPermissionHelper.getSensitivePermissions(permissions);
            if (sensitivePermissionsList != null && sensitivePermissionsList.size() > 0) {
                // 1.获取当前屏幕状态是否符合条件
                if (isScreenStateMeet()) {
                    //获取数据库中的权限
                    String[] oldPermissions = mAppPermissionsProvider.getPermissions(pkgName);
                    boolean isNeedShowAd = false;
                    if (mPermissionHelper.hasNewPermissionAdd(oldPermissions, permissions)) {
                        isNeedShowAd = true;
                    } else {
                        isNeedShowAd = false;
                    }
                    List<String> newPermission;
                    if (isNeedConsiderUpdate) {
                        //当需要考虑的时候
                        if (mAppPermissionsProvider.hasUpdateBefore(pkgName)) {
                            if (oldPermissions != null) {
                                newPermission = mPermissionHelper.getAddPermissions(permissions, oldPermissions);
                            } else {
                                newPermission = sensitivePermissionsList;
                            }
                        } else {
                            //如果是初次更新或安装，则显示全部敏感权限
                            newPermission = sensitivePermissionsList;
                        }
                    } else {
                        newPermission = sensitivePermissionsList;
                    }
                    String[] perArr = null;
                    if (sensitivePermissionsList.size() > 0) {
                        perArr = new String[sensitivePermissionsList.size()];
                        sensitivePermissionsList.toArray(perArr);
                    }
                    mAppPermissionsProvider.update(pkgName, perArr);
//                    newPermission =  mPermissionHelper.getSensitivePermissions(newPermission);
                    if (newPermission != null && newPermission.size() > 0) {
                        gotoPermissionAlarmPopActivity(pkgName, newPermission, isNeedShowAd, PermissionAlarmPopActivity.DLG_COMMON);
                    } else {
                        gotoPermissionMergePopActivity(pkgName, newPermission);
                    }
                } else {
                    //缓存弹框，等待屏幕状态改变后 再显示
//                    Logger.w(TAG, "缓存弹窗！");
                    mCacheMap.put(pkgName, sensitivePermissionsList);
                }
            }
        }
    }

    /**
     * 跳转到权限警告弹框
     */
    private void gotoPermissionAlarmPopActivity(final String pkgName, final List<String> permissions, final boolean isNeedShowAd, @PermissionAlarmPopActivity.PermissionAlarmDlgType final int type) {
        if (isNeedShowAd) {
//            ViewBinder.Builder mopubNativeViewBuilder = new ViewBinder.Builder(R.layout.layout_permission_alarm_ad)
//                    .iconImageId(R.id.ad_icon)
//                    .mainImageId(R.id.ad_head)
//                    .titleId(R.id.ad_info_title)
//                    .textId(R.id.ad_info_detail)
//                    .callToActionId(R.id.ad_action)
//                    .privacyInformationIconImageId(R.id.ad_mopub_logo);
//            MoPubStaticNativeAdRenderer renderer = new MoPubStaticNativeAdRenderer(mopubNativeViewBuilder.build());
//            MoPubAdConfig moPubAdConfig = new MoPubAdConfig().moPubNativeConfig(new MoPubNativeConfig(renderer, null));
            // TODO: 17-6-29 @nieyh mopub广告 与 请求广告
//            AdManager.getInstance().loadAd(BasePermissionView.mAdEntrance, 1, moPubAdConfig, true);
            TheApplication.postRunOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //加一个过滤 当开关已经关上时 不再弹框
                    if (PermissionAlarmManager.this.isSwitchEnable()) {
                        PermissionAlarmPopActivity.show(pkgName, permissions, isNeedShowAd, type);
                    }
                }
            }, 5000);
        } else {
            PermissionAlarmPopActivity.show(pkgName, permissions, isNeedShowAd, type);
        }
    }

    /**
     * 展示所有的缓存对话框
     */
    private void showPermissionCacheDlg() {
        for (Map.Entry<String, List<String>> permissionsEntry : mCacheMap.entrySet()) {
            String pkgName = permissionsEntry.getKey();
            List<String> permissionList = permissionsEntry.getValue();
            String[] permissions = new String[permissionList.size()];
            permissionList.toArray(permissions);
            //获取数据库中的权限
            String[] oldPermissions = mAppPermissionsProvider.getPermissions(pkgName);
            boolean isNeedShowAd = false;
            if (mPermissionHelper.hasNewPermissionAdd(oldPermissions, permissions)) {
                isNeedShowAd = true;
            } else {
                isNeedShowAd = false;
            }
            List<String> newPermission;
            //当需要考虑的时候
            if (mAppPermissionsProvider.hasUpdateBefore(pkgName)) {
                if (oldPermissions != null) {
                    newPermission = mPermissionHelper.getAddPermissions(permissions, oldPermissions);
                } else {
                    newPermission = permissionList;
                }
            } else {
                //如果是初次更新或安装，则显示全部敏感权限
                newPermission = permissionList;
            }
            mAppPermissionsProvider.update(pkgName, permissions);
            if (newPermission != null && newPermission.size() > 0) {
                gotoPermissionAlarmPopActivity(pkgName, newPermission, isNeedShowAd, PermissionAlarmPopActivity.DLG_COMMON);
            } else {
                gotoPermissionMergePopActivity(pkgName, newPermission);
            }
        }
        mCacheMap.clear();
    }

    /**
     * 跳转到合并对话框 <br> 逻辑：<br> 1、查看服务器配置是否可以展示广告 <br> 2、广告只请求一次. <br> 3、如果activity没有存活 则请求广告并等待五秒再展示。
     * <br> 4、如果广告已经请求过（上一个5秒等待还没有过去）此时新应用安装的将直接展示activity <br>
     */
    private void gotoPermissionMergePopActivity(final String pkgName, final List<String> newPermission) {
        //服务器配置返回功能可以展示广告 并且这个没有新增权限的也可以展示广告
        // TODO: 17-6-29 @nieyh 是否远程控制展示广告
        //RemoteConfigManager.getInstance().getPermissionCheckConfig().showAd()
        if (false) {
            //如果activiity已经存在 则直接展示对话框
            if (PermissionAlarmPopActivity.isLive) {
                PermissionAlarmPopActivity.show(pkgName, newPermission, true, PermissionAlarmPopActivity.DLG_MERGE);
            } else {
                if (!PermissionAlarmPopActivity.isAlreadyRequestAd) {
                    //当没有请求过广告 则请求一次
                    // TODO: 17-6-29 @nieyh 是否展示mopub广告
//                    PermissionAlarmPopActivity.isAlreadyRequestAd = true;
//                    ViewBinder.Builder mopubNativeViewBuilder = new ViewBinder.Builder(R.layout.layout_permission_alarm_ad)
//                            .iconImageId(R.id.ad_icon)
//                            .mainImageId(R.id.ad_head)
//                            .titleId(R.id.ad_info_title)
//                            .textId(R.id.ad_info_detail)
//                            .callToActionId(R.id.ad_action)
//                            .privacyInformationIconImageId(R.id.ad_mopub_logo);
//                    MoPubStaticNativeAdRenderer renderer = new MoPubStaticNativeAdRenderer(mopubNativeViewBuilder.build());
//                    MoPubAdConfig moPubAdConfig = new MoPubAdConfig().moPubNativeConfig(new MoPubNativeConfig(renderer, null));
//
//                    AdManager.getInstance().loadAd(BasePermissionView.mAdEntrance, 1, moPubAdConfig, true);
//                    //并且等待五秒再展示对话框
//                    TheApplication.postRunOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            //加一个过滤 当开关已经关上时 不再弹框
//                            if (PermissionAlarmManager.this.isSwitchEnable()) {
//                                PermissionAlarmPopActivity.show(pkgName, newPermission, true, PermissionAlarmPopActivity.DLG_MERGE);
//                            }
//                        }
//                    }, 5000);
                } else {
                    //如果在这个五秒等待中 又有对话框需要展示 则直接展示
                    PermissionAlarmPopActivity.show(pkgName, newPermission, true, PermissionAlarmPopActivity.DLG_MERGE);
                }
            }
        } else {
            PermissionAlarmPopActivity.show(pkgName, newPermission, false, PermissionAlarmPopActivity.DLG_MERGE);
        }
    }

    /**
     * 查看屏幕状态以及是否全屏显示 是否变化
     */
    private void checkScreenState() {
        //此处两处逻辑判断
        // 1.是否非全屏
        // 2.是否竖屏
        //当竖屏并且非全屏时 展示缓存对话框
        try {
            int statusBarState = getStatusBarState();
            Configuration configuration = TheApplication.getAppContext().getResources().getConfiguration();
            int ori = configuration.orientation; //获取屏幕方向
            if (ori == Configuration.ORIENTATION_PORTRAIT) {
                //当时竖屏并且非全屏
                if (statusBarState == STATUS_BAR_SHOW && mLastStatusBarState != statusBarState) {
                    //当时非全屏 弹出之前缓存的弹框
                    showPermissionCacheDlg();
                }
            }
            mLastScreenOrientation = ori;
            mLastStatusBarState = statusBarState;
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 是否屏幕状态满足条件 <br/> <ol> <li>竖屏</li> <li>非全屏</li> </ol>
     */
    private boolean isScreenStateMeet() {
        int statusBarState = getStatusBarState();
        Configuration configuration = TheApplication.getAppContext().getResources().getConfiguration();
        int ori = configuration.orientation; //获取屏幕方向
        if (ori == Configuration.ORIENTATION_PORTRAIT && statusBarState == STATUS_BAR_SHOW) {
            return true;
        }
        return false;
    }

    /**
     * 是否状态栏可以看到
     */
    private
    @StatusBarState
    synchronized int getStatusBarState() {
        int statusBarState = DENIED;

        int[] location = new int[]{0, 1};
        mStatusBarIsVisiableView.getLocationOnScreen(location);
        if (location[0] == 0) {
            //设置时是 wmParams.x = 100，说明addView没有生效。
            statusBarState = DENIED;
        } else if (location[1] == 0) {
            //与初始化值 不一致 说明生效 并且此时全屏
            statusBarState = STATUS_BAR_DISMISS;
        } else {
            statusBarState = STATUS_BAR_SHOW;
        }
        return statusBarState;
    }

    /**
     * 监听应用添加与更新
     */
    private void monitorAppAddedOrUpdate() {
        if (!isRegister) {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
            intentFilter.addDataScheme("package");
            TheApplication.getAppContext().registerReceiver(mAppAddedOrUpdateReceiver, intentFilter);
            isRegister = true;
        }
    }

    /**
     * 停止所有监听
     */
    private void stopMonitor() {
        if (isRegister) {
            TheApplication.getAppContext().unregisterReceiver(mAppAddedOrUpdateReceiver);
            isRegister = false;
        }
        if (mCheckAppInfoThread != null) {
            mCheckAppInfoThread.interrupt();
        }

        //移除界面
        if (mStatusBarIsVisiableView != null && mStatusBarIsVisiableView.getParent() != null) {
            WindowManager windowManager = (WindowManager) TheApplication.getAppContext().getSystemService(Context.WINDOW_SERVICE);
            windowManager.removeView(mStatusBarIsVisiableView);
        }
    }

    /**
     * 是否在黑名单中 <br/> 在{@link #PermissionAlarmManager()} 中初始化
     */
    private boolean isAtBlackList(String pkgName) {
        for (String black : mBlackList) {
            if (pkgName.contains(black)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取所有的应用权限保存到数据库中
     */
    private void scanAllPermissionToTableInsertFir() {
        if (mAppPermissionsProvider == null) {
            return;
        }
        PackageManager pm = TheApplication.getAppContext().getPackageManager();
        List<PackageInfo> packageInfos = pm.getInstalledPackages(PackageManager.GET_PERMISSIONS);
        List<InsertParams> insertParamsList = new ArrayList<>();
        for (PackageInfo info : packageInfos) {
            String[] permissions = info.requestedPermissions;
            if (permissions != null && permissions.length > 0) {
                InsertParams insertParams = mAppPermissionsProvider.buildInsertParams(info.packageName, permissions);
                if (insertParams != null) {
                    insertParamsList.add(insertParams);
                }
            }
        }
        //清空所有的数据
        mAppPermissionsProvider.deleteAllData();
        //提交数据
        mAppPermissionsProvider.commitBatch(insertParamsList);
    }


    /**
     * 获取所有的应用权限保存到数据库中 1、先通过更新来修改数据 2、失败 则直接插入
     */
    private void scanAllPermissionToTableUpdateFirst() {
        if (mAppPermissionsProvider == null) {
            return;
        }
        PackageManager pm = TheApplication.getAppContext().getPackageManager();
        List<PackageInfo> packageInfos = pm.getInstalledPackages(PackageManager.GET_PERMISSIONS);
        for (PackageInfo info : packageInfos) {
            String[] permissions = info.requestedPermissions;
            if (permissions != null && permissions.length > 0) {
                UpdateParams updateParams = mAppPermissionsProvider.buildUpdateParams(info.packageName, permissions);
                if (!mAppPermissionsProvider.updatePermission(updateParams)) {
                    //UpdateParams 继承于 insert
                    mAppPermissionsProvider.insert(updateParams);
                    Logger.w(TAG, "insert >> " + info.packageName);
                } else {
                    Logger.w(TAG, "update >> " + info.packageName);
                }
            }
        }
    }
}
