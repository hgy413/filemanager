package com.jb.filemanager;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.StrictMode;
import android.os.UserManager;
import android.support.multidex.MultiDex;
import android.text.TextUtils;
import android.widget.Toast;

import com.flashlight.brightestflashlightpro.app.ToolLockerSdk;
import com.gau.go.gostaticsdk.StatisticsManager;
import com.jb.filemanager.abtest.ABTest;
import com.jb.filemanager.ad.AdManager;
import com.jb.filemanager.alarmtask.ScheduleTaskHandler;
import com.jb.filemanager.buyuser.BuyUserManager;
import com.jb.filemanager.function.applock.manager.AppLockerCenter;
import com.jb.filemanager.function.daemon.AssistantReceiver;
import com.jb.filemanager.function.daemon.AssistantService;
import com.jb.filemanager.function.daemon.DaemonReceiver;
import com.jb.filemanager.function.daemon.DaemonService;
import com.jb.filemanager.function.permissionalarm.manager.PermissionAlarmManager;
import com.jb.filemanager.function.recent.RecentFileManager;
import com.jb.filemanager.function.scanframe.clean.CacheManager;
import com.jb.filemanager.function.scanframe.clean.CleanManager;
import com.jb.filemanager.function.scanframe.clean.event.GlobalDataLoadingDoneEvent;
import com.jb.filemanager.function.scanframe.manager.ad.AdTrashManager;
import com.jb.filemanager.function.scanframe.manager.residue.ResidualFileManager;
import com.jb.filemanager.function.search.SearchManager;
import com.jb.filemanager.function.tip.manager.StorageTipManager;
import com.jb.filemanager.function.tip.manager.UsbStateManager;
import com.jb.filemanager.function.zipfile.ExtractManager;
import com.jb.filemanager.global.TheUncaughtExceptionHandler;
import com.jb.filemanager.manager.GlobalFileManager;
import com.jb.filemanager.manager.spm.IPreferencesIds;
import com.jb.filemanager.manager.spm.SharedPreferencesManager;
import com.jb.filemanager.receiver.ScreenStateReceiver;
import com.jb.filemanager.statistics.AlarmEight;
import com.jb.filemanager.util.AppUtils;
import com.jb.filemanager.util.CrashHandler;
import com.jb.filemanager.util.Logger;
import com.jb.filemanager.util.device.Machine;
import com.jiubang.commerce.ad.AdSdkApi;
import com.jiubang.commerce.ad.params.ClientParams;
import com.jiubang.commerce.buychannel.BuyChannelApi;
import com.jiubang.commerce.buychannel.buyChannel.bean.BuyChannelBean;
import com.jiubang.commerce.buychannel.buyChannel.utils.AppInfoUtils;
import com.jiubang.commerce.daemon.DaemonClient;
import com.jiubang.commerce.daemon.DaemonConfigurations;
import com.squareup.leakcanary.LeakCanary;

import org.greenrobot.eventbus.EventBus;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;

/**
 * Created by bill wang on 2017/6/20.
 */

public class TheApplication extends Application {

    private final String LOG_TAG = "TheApplication";

    private static TheApplication sInstance;

    private ScheduleTaskHandler mTaskHandler;
    private AlarmEight mAlarmEight;

    public static Activity sCurrentActivity;

    private final static EventBus GLOBAL_EVENT_BUS = EventBus.getDefault();
    /**
     * 异步线程，用于处理一般比较短暂的耗时操作，如数据库读写操作等<br>
     */
    private static final HandlerThread SHORT_TASK_WORKER_THREAD = new HandlerThread(
            "Short-Task-Worker-Thread");

    static {
        SHORT_TASK_WORKER_THREAD.start();
    }

    private final static Handler MAIN_LOOPER_HANDLER = new Handler(
            Looper.getMainLooper());

    private final static Handler SHORT_TASK_HANDLER = new Handler(
            SHORT_TASK_WORKER_THREAD.getLooper());
    private ActivityLifecycleCallbacks mActivityLifecycleCallbacks;

    public static TheApplication getInstance() {
        return sInstance;
    }

    public TheApplication() {
        sInstance = this;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            if (LeakCanary.isInAnalyzerProcess(this)) {
                // This process is dedicated to LeakCanary for heap analysis.
                // You should not init your app in this process.
                return;
            }
            LeakCanary.install(this);
        }

        long s = System.currentTimeMillis();
        initCrashReport();
        TheUncaughtExceptionHandler.getInstance().init();
        /*
         * fix android leak fix which is caused by UserManager holding on to a activity ctx
         * 反射处理userManager的泄露
         * 原理：userManager在初次调用的时候会持有一个context的引用
         * 详情：https://code.google.com/p/android/issues/detail?id=173789
         *       https://github.com/square/leakcanary/issues/62
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            try {
                final Method m = UserManager.class.getMethod("get", Context.class);
                m.setAccessible(true);
                m.invoke(null, this);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }

        if (BuildConfig.DEBUG) {
            Toast.makeText(this, "onCreate:" + AppUtils.getCurrentProcessName(this), Toast.LENGTH_SHORT).show();
        }

        registerActivityLifecycleListener();
        if (isRunningOnMainProcess()) {
            onCreateForMainProcess();
        } else if (isRunningOnIntelligentPreloadServiceProcess()) {
            onCreateForIntelligentPreloadServiceProcess();
        }

        Logger.e("onCreat执行时间", "时间 : " + (System.currentTimeMillis() - s));
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
        //重要：以下接入代码，建议在业务代码之前执行，守护效果更好
        //初始化DaemonClient
        DaemonClient.getInstance().init(createDaemonConfigurations());
        DaemonClient.getInstance().onAttachBaseContext(base);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        ExtractManager.getInstance().onAppDestroy();
        GlobalFileManager.getInstance().onApplicationTerminate();
        unregisterActivityLifecycleListener();
    }

    public static Context getAppContext() {
        return sInstance.getApplicationContext();
    }

    private boolean isRunningOnMainProcess() {
        return Const.PROCESS_NAME_MAIN.equals(AppUtils.getCurrentProcessName(this));
    }

    /**
     * 是否在广告智能预加载子进程运行
     *
     * @return result
     */
    private boolean isRunningOnIntelligentPreloadServiceProcess() {
        return Const.PROCESS_NAME_INTELLIGENT_PRELOAD_SERVICE
                .equals(AppUtils.getCurrentProcessName(this));
    }

    /**
     * 将主进程的主线程设置成严苛模式 包括线程耗时以及内存泄露部分
     */
    private void initStrictMode() {
        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectCustomSlowCalls() //API等级11，使用StrictMode.noteSlowCode
                    .detectAll()
                    .penaltyLog() //在Logcat 中打印违规异常信息
                    .build());

            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectLeakedSqlLiteObjects()
                    .detectLeakedClosableObjects() //API等级11
                    .detectActivityLeaks()
                    .penaltyLog() //在Logcat 中打印违规异常信息
                    .build());
        }
    }

    private void onCreateForMainProcess() {
        // 初始化统计
        initStatistics();

        // 初始化8小时定期上传统计
        initEightUploadStatistic();

        // 初始化SPM
        initSharedPreferenceManager();

        // 初始化ABTest
        initABTest();

        // 初始化买量SDK
        BuyUserManager.initSingleton(getAppContext());

        //初始化广告SDK
        AdManager.initSingleton(this);

        initToolLocker();
        GlobalFileManager.getInstance().onApplicationCreate();

        //异步调用方法
        ResidualFileManager.getInstance(getApplicationContext());
        CacheManager.getInstance(getApplicationContext());
        // 初始化广告垃圾扫描
        AdTrashManager.getInstance(getAppContext());
        CleanManager.getInstance(getApplicationContext()).startJunkFileScanTask();
        //应用锁
        ScreenStateReceiver.getInstance().registerReceiver();
        AppLockerCenter.getInstance();
        //权限警报管理器准备工作
        PermissionAlarmManager.getInstance().toReady();
        UsbStateManager.getInstance().toReady();
        StorageTipManager.getInstance().toReady();
        //搜索管家启动
        SearchManager.getInstance().toReady();
        // 最近文件 应用启动时进行全盘扫描
        RecentFileManager.getInstance().scanAllFile();
        // 主进程启动完毕,更新上一次启动的版本号和时间 **这句永远在最后
        appLaunchFinished();

        // 将主进程的主线程设置成严苛模式 包括线程耗时以及内存泄露部分
        initStrictMode();
        TheApplication.getGlobalEventBus().post(new GlobalDataLoadingDoneEvent());
    }

    private void onCreateForIntelligentPreloadServiceProcess() {
        // 这个进程里也要先初始化完统计sdk再初始化广告sdk
        initStatistics();
        // 初始化买量SDK
        BuyUserManager.initSingleton(getAppContext());
        // 用到广告sdk的进程都需要先初始化统计sdk
        initAdSdk();
    }

    /**
     * 此处为统计上传初始化代码 请<i>不要</i>删除 !!!!!
     */
    private void initEightUploadStatistic() {
        mAlarmEight = new AlarmEight(this);
        mTaskHandler = new ScheduleTaskHandler(this);
    }

    private void initSharedPreferenceManager() {
        SharedPreferencesManager spm = SharedPreferencesManager.getInstance(this);
        if (!spm.contains(IPreferencesIds.KEY_FIRST_LAUNCH_VERSION_CODE)) {
            spm.commitInt(IPreferencesIds.KEY_FIRST_LAUNCH_VERSION_CODE, BuildConfig.VERSION_CODE);
        }
        if (!spm.contains(IPreferencesIds.KEY_FIRST_LAUNCH_TIME)) {
            spm.commitLong(IPreferencesIds.KEY_FIRST_LAUNCH_TIME, System.currentTimeMillis());
        }
        if (spm.getBoolean(IPreferencesIds.KEY_FIRST_INSTALL, true)) {
            spm.commitLong(IPreferencesIds.KEY_FIRST_INSTALL_TIME, System.currentTimeMillis());
            spm.commitBoolean(IPreferencesIds.KEY_FIRST_INSTALL, false);
        }
    }

    private void appLaunchFinished() {
        SharedPreferencesManager spm = SharedPreferencesManager.getInstance(this);
        spm.commitInt(IPreferencesIds.KEY_LAST_LAUNCH_VERSION_CODE, BuildConfig.VERSION_CODE);
        spm.commitLong(IPreferencesIds.KEY_LAST_LAUNCH_TIME, System.currentTimeMillis());
    }

    private void initABTest() {
        SharedPreferencesManager spm = SharedPreferencesManager.getInstance(this);
        String user = spm.getString(IPreferencesIds.KEY_AB_TEST_USER, "");
        int lastLaunchVersionCode = spm.getInt(IPreferencesIds.KEY_LAST_LAUNCH_VERSION_CODE, 0);
        boolean isUpgrade = lastLaunchVersionCode != 0 && lastLaunchVersionCode != BuildConfig.VERSION_CODE;
        ABTest.initSingleton(this);
        ABTest.getInstance().init(user, isUpgrade);
    }

    /**
     * <br>功能简述:初始化相关sdk
     * <br>功能详细描述:
     * <br>注意:
     */
    private void initAdSdk() {

        // 这里起线程加载google advertising id, 会导致有些统计会没有gaid，因为有些统计比这线程还早执行
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Context context = getApplicationContext();
                String gadId = AppInfoUtils.getAdvertisingId(TheApplication.getInstance());
                String channel = AppUtils.getChannel(context);

                // 调试
                if (BuildConfig.DEBUG) {
                    // 广告日志
                    AdSdkApi.setEnableLog(true);
                }

                SharedPreferencesManager spManager = SharedPreferencesManager.getInstance(context);
                long installTime = 0;
                if (spManager != null) {
                    installTime = spManager.getLong(IPreferencesIds.KEY_FIRST_INSTALL_TIME, 0);
                }

                String buyUserChannel = BuyUserManager.getInstance().getBuyUserChannel();
                if (TextUtils.isEmpty(buyUserChannel)) {
                    buyUserChannel = AppUtils.getChannel(context);
                }
                boolean isUpgrade = AppUtils.isInstallFromUpdate();

                AdSdkApi.initSDK(context,
                        Const.PROCESS_NAME_INTELLIGENT_PRELOAD_SERVICE,
                        StatisticsManager.getGOID(context),
                        gadId,
                        channel,
                        new ClientParams(buyUserChannel, installTime, isUpgrade));

            }
        }, "init");
        thread.start();
    }

    private void initToolLocker() {
        // TODO @wangzq 工具锁广告配置
//
//        SharedPreferencesManager spm = SharedPreferencesManager.getInstance(this);
//        long installDate = spm.getLong(IPreferencesIds.KEY_FIRST_INSTALL_TIME, System.currentTimeMillis());
//        boolean isUpgrade = AppUtils.isInstallFromUpdate();
//
//
//        ToolLockerSdk toolLockerSdk = ToolLockerSdk.getInstance();
//        toolLockerSdk.enableLog(BuildConfig.DEBUG); //开关log
//
//        //初始化sdk，参数依次为：Context，AbtestCenterService.cid, AbtestCenterService.cid2, 101统计协议功能点id，应用首次安装时间，是否是升级用户，买量渠道，是否是买量用户
//        BuyChannelBean buyChannelBean = BuyChannelApi.getBuyChannelBean(this);
//        toolLockerSdk.init(this,
//                getPackageName(),
//                Integer.valueOf(GoBatteryUtil.getProductId(this)),
//                FunctionIdConst.PRODUCT_ID_OF_19,
//                FunctionIdConst.FUNCTION_ID_OF_PROTOCOL_101,
//                installDate,
//                isUpgrade,
//                BuyUserManager.getInstance().getBuyUserChannel(),
//                BuyUserManager.getInstance().isBuyUser());
//
//        toolLockerSdk.setAbTestServiceSid(186); //设置ab后台的业务id，sdk中默认是148，如果不是默认，请重新设置
//
//        toolLockerSdk.setAppIcon(R.mipmap.ic_launcher); //设置你的应用图标，用于工具锁顶部标识
//        toolLockerSdk.setAppName(getResources().getString(R.string.app_name)); //设置你的应用名称，用于工具锁顶部标识
//        toolLockerSdk.setAdLockScreenId(VirtualIdConstant.AD_VITRUAL_ID_TOOL_LOCKER_MAIN); //设置锁屏主界面广告的默认id
//        toolLockerSdk.setAdCleanId(VirtualIdConstant.AD_VIRTUAL_ID_TOOL_LOCKER_CLEAN); //设置清理界面广告的默认id
//        toolLockerSdk.setAdNotifyId(VirtualIdConstant.AD_VIRTUAL_ID_TOOL_LOCKER_NOTIFY); //设置工具锁第二屏通知管理广告的默认id
    }

    private DaemonConfigurations createDaemonConfigurations() {
        DaemonConfigurations.DaemonConfiguration configuration1 = new DaemonConfigurations.DaemonConfiguration(
                Const.PACKAGE_NAME,
                DaemonService.class.getCanonicalName(),
                DaemonReceiver.class.getCanonicalName());
        DaemonConfigurations.DaemonConfiguration configuration2 = new DaemonConfigurations.DaemonConfiguration(
                Const.PACKAGE_NAME + ":assistant",
                AssistantService.class.getCanonicalName(),
                AssistantReceiver.class.getCanonicalName());
        DaemonConfigurations configs = new DaemonConfigurations(configuration1, configuration2);
        //设置保护配置，在一定时间重启超过一定次数，则不再重启。用来防止因客户端程序BUG导致的频繁重启
        //此功能实现还不完整
        configs.setProtectConfig(DaemonConfigurations.ProtectConfiguration.build(15, 5));
        return configs;
    }

    private void initCrashReport() {
        CrashHandler.getInstance().init(Const.LOG_DIR);
    }

    /**
     * 初始统计SDK
     */
    private void initStatistics() {
        // 1.主进程名（注意：并非主包名，如果手动改过进程名，此时的进程名就不是主包名！如果传错了，就会上传不成功！）
        // 2.渠道号，如果没有可传""，不要传null
        // 3.IMEI,如果没有可传""，不要传null
        // 4.ContentProvider对应的Authority，不要传null
        final Context applicationContext = getApplicationContext();
        // 初始化必要参数
        StatisticsManager.initBasicInfo(Const.PROCESS_NAME_MAIN,
                AppUtils.getChannel(applicationContext),
                Machine.getIMEI(applicationContext),
                Const.STATISTICS_SDK_PROVIDER_AUTHORITIES);
        StatisticsManager statisticsManager = StatisticsManager.getInstance(applicationContext);
        // 调试
        if (BuildConfig.DEBUG) {
            //设置是否显示日志
            statisticsManager.enableLog(true);
        }
    }

    /**
     * 提交一个Runnable到短时任务线程执行<br>
     * <p>
     * <strong>NOTE:</strong>
     * 只充许提交比较短暂的耗时操作，如数据库读写操作等，像网络请求这类可能耗时较长的<i>不能</i>提交，<br>
     * 以免占用线程影响其他的重要数据库操作。
     * </p>
     *
     * @param r runnable
     * @see #postRunOnShortTaskThread(Runnable, long)
     * @see #removeFromShortTaskThread(Runnable)
     */
    public static void postRunOnShortTaskThread(Runnable r) {
        postRunnableByHandler(SHORT_TASK_HANDLER, r);
    }

    /**
     * 提交一个Runnable到短时任务线程执行<br>
     * <p>
     * <strong>NOTE:</strong>
     * 只充许提交比较短暂的耗时操作，如数据库读写操作等，像网络请求这类可能耗时较长的<i>不能</i>提交，<br>
     * 以免占用线程影响其他的重要数据库操作。
     * </p>
     *
     * @param r           runnable
     * @param delayMillis 延迟指定的毫秒数执行.
     * @see #postRunOnShortTaskThread(Runnable)
     * @see #removeFromShortTaskThread(Runnable)
     */
    public static void postRunOnShortTaskThread(Runnable r, long delayMillis) {
        postRunnableByHandler(SHORT_TASK_HANDLER, r, delayMillis);
    }

    /**
     * 从短时任务线程移除一个先前post进去的Runnable<b>
     *
     * @param r runnable
     * @see #postRunOnShortTaskThread(Runnable)
     * @see #postRunOnShortTaskThread(Runnable, long)
     */
    public static void removeFromShortTaskThread(Runnable r) {
        removeRunnableFromHandler(SHORT_TASK_HANDLER, r);
    }

    /**
     * 提交一个Runnable到UI线程执行<br>
     *
     * @param r runnable
     * @see #removeFromUiThread(Runnable)
     */
    public static void postRunOnUiThread(Runnable r) {
        postRunnableByHandler(MAIN_LOOPER_HANDLER, r);
    }

    /**
     * 提交一个Runnable到UI线程执行<br>
     *
     * @param r           runnable
     * @param delayMillis 延迟指定的毫秒数执行.
     * @see #postRunOnUiThread(Runnable)
     * @see #removeFromUiThread(Runnable)
     */
    public static void postRunOnUiThread(Runnable r, long delayMillis) {
        postRunnableByHandler(MAIN_LOOPER_HANDLER, r, delayMillis);
    }

    /**
     * 从UI线程移除一个先前post进去的Runnable<b>
     */
    public static void removeAllFromHandler() {
        cleanHandler(MAIN_LOOPER_HANDLER);
    }

    /**
     * 从UI线程移除一个先前post进去的Runnable<b>
     *
     * @param r runnable
     * @see #postRunOnUiThread(Runnable)
     */
    public static void removeFromUiThread(Runnable r) {
        removeRunnableFromHandler(MAIN_LOOPER_HANDLER, r);
    }

    /**
     * 是否运行在UI线程<br>
     */
    public static boolean isRunOnUiThread() {
        return Thread.currentThread() == Looper.getMainLooper().getThread();
    }

    private static void postRunnableByHandler(Handler handler, Runnable r) {
        handler.post(r);
    }

    private static void postRunnableByHandler(Handler handler, Runnable r, long delayMillis) {
        if (delayMillis <= 0) {
            postRunnableByHandler(handler, r);
        } else {
            handler.postDelayed(r, delayMillis);
        }
    }

    private static void removeRunnableFromHandler(Handler handler, Runnable r) {
        handler.removeCallbacks(r);
    }

    private static void cleanHandler(Handler handler) {
        handler.removeCallbacksAndMessages(null);
    }

    /**
     * 获取一个全局的EventBus实例<br>
     *
     * @return
     */
    public static EventBus getGlobalEventBus() {
        return GLOBAL_EVENT_BUS;
    }

    /**
     * 使用全局EventBus post一个事件<br>
     *
     * @param event
     */
    public static void postEvent(Object event) {
        GLOBAL_EVENT_BUS.post(event);
    }

    /**
     * 使用全局EventBus post一个Sticky事件<br>
     *
     * @param event
     */
    public static void postStickyEvent(Object event) {
        GLOBAL_EVENT_BUS.postSticky(event);
    }

    private void registerActivityLifecycleListener() {
        if (mActivityLifecycleCallbacks != null) return;
        mActivityLifecycleCallbacks = new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

            }

            @Override
            public void onActivityStarted(Activity activity) {

            }

            @Override
            public void onActivityResumed(Activity activity) {
                WeakReference<Activity> reference = new WeakReference<Activity>(activity);
                if (reference != null) {
                    sCurrentActivity = reference.get();
                }
            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {

            }
        };
        registerActivityLifecycleCallbacks(mActivityLifecycleCallbacks);
    }

    private void unregisterActivityLifecycleListener() {
        if (mActivityLifecycleCallbacks != null) {
            unregisterActivityLifecycleCallbacks(mActivityLifecycleCallbacks);
        }
    }
}
