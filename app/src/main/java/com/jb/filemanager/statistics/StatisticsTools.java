package com.jb.filemanager.statistics;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;

import com.gau.go.gostaticsdk.StatisticsManager;
import com.gau.go.gostaticsdk.beans.OptionBean;
import com.jb.filemanager.BuildConfig;
import com.jb.filemanager.Const;
import com.jb.filemanager.TheApplication;
import com.jb.filemanager.abtest.ABTest;
import com.jb.filemanager.eventbus.AgreePrivacyEvent;
import com.jb.filemanager.eventbus.IOnEventMainThreadSubscriber;
import com.jb.filemanager.function.privacy.PrivacyHelper;
import com.jb.filemanager.manager.spm.IPreferencesIds;
import com.jb.filemanager.manager.spm.SharedPreferencesManager;
import com.jb.filemanager.statistics.bean.Statistics101Bean;
import com.jb.filemanager.statistics.bean.Statistics102Bean;
import com.jb.filemanager.statistics.bean.Statistics103Bean;
import com.jb.filemanager.util.AppUtils;
import com.jb.filemanager.util.TimeUtil;
import com.jb.filemanager.util.device.Machine;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bill wang on 2017/6/20.
 *
 */

public class StatisticsTools {

    /**
     * StatisticsTask
     */
    private static class StatisticsTask {
        private int mLogId;
        private int mFunId;
        private String mStatisticsData;
        private boolean mIgnoreAgreePrivacy;
        private boolean mIsForceUpload;

        public StatisticsTask(int logId, int funId, String statisticsData, boolean ignoreAgreePrivacy, boolean isForceUpload) {
            super();
            mLogId = logId;
            mFunId = funId;
            mStatisticsData = statisticsData;
            mIgnoreAgreePrivacy = ignoreAgreePrivacy;
            mIsForceUpload = isForceUpload;
        }

        public void run() {
            if (mIsForceUpload) {
                uploadStaticDataForce(mLogId, mFunId, mStatisticsData, mIgnoreAgreePrivacy);
            } else {
                uploadStaticData(mLogId, mFunId, mStatisticsData, mIgnoreAgreePrivacy);
            }
        }
    }

    /**
     * 101统计协议
     */
    private static final int PROTOCOL_101 = 101;

    /**
     * 102统计数据
     */
    private static final int PROTOCOL_102 = 102;

    /**
     * 103统计数据
     * */
    private static final int PROTOCOL_103 = 103;

    /**
     * 104统计协议
     */
    private static final int PROTOCOL_104 = 104;

    /**
     * 上传控制开关
     */
    private final static boolean STATISTICS_UPLOAD = true;
    public static final String PROTOCOL_DIVIDER = "||";

    private final static StatisticsTools INSTANCES = new StatisticsTools();

    private final List<StatisticsTask> mPendingTasks = new ArrayList<>();

    private StatisticsTools() {
        //当用户同意用户协议 会收到Event 并对于统计信息进行上传
        EventBus.getDefault().register(new IOnEventMainThreadSubscriber<AgreePrivacyEvent>() {
            @Subscribe(threadMode = ThreadMode.MAIN)
            @Override
            public void onEventMainThread(AgreePrivacyEvent event) {
                EventBus.getDefault().unregister(this);
                runPendingTask();
            }
        });
    }

    private void runPendingTask() {
        final List<StatisticsTask> pendingTasks = new ArrayList<>(
                mPendingTasks);
        mPendingTasks.clear();
        for (StatisticsTask statisticsTask : pendingTasks) {
            statisticsTask.run();
        }
    }

    // ==========================全新统计接口=============================

    /**
     * 新接口　以后统一使用本接口上传 101统计协议上传接口 参数传Statistics101Bean
     *
     * @param statisticsBaseBean 参数必须有操作码，对无操作码做了不上传处理
     */
    public static void upload101InfoNew(Statistics101Bean statisticsBaseBean) {
        upload101InfoNew(statisticsBaseBean, false);
    }

    /**
     * 新接口　以后统一使用本接口上传 101统计协议上传接口 参数传Statistics101Bean
     *
     * @param statisticsBaseBean 参数必须有操作码，对无操作码做了不上传处理
     */
    public static void upload101InfoNew(Statistics101Bean statisticsBaseBean, boolean ignoreAgreePrivacy) {
        if (statisticsBaseBean.mOperateId.equals("")) {
            return;
        }
        String sb = String.valueOf(StatisticsConstants.LOG_ID_758) + PROTOCOL_DIVIDER +
                statisticsBaseBean.mStatisticsObject + PROTOCOL_DIVIDER +
                statisticsBaseBean.mOperateId + PROTOCOL_DIVIDER +
                "1" + PROTOCOL_DIVIDER +
                statisticsBaseBean.mEntrance + PROTOCOL_DIVIDER +
                statisticsBaseBean.mTab + PROTOCOL_DIVIDER +
                statisticsBaseBean.mLocation + PROTOCOL_DIVIDER +
                statisticsBaseBean.mRelativeObject + PROTOCOL_DIVIDER +
                statisticsBaseBean.mRemark;
        uploadStaticData(PROTOCOL_101, StatisticsConstants.LOG_ID_758, sb, ignoreAgreePrivacy);
    }

    /**
     * 新接口　上传102统计协议
     */
    public static void upload102InfoNew() {
        uploadIsFacebookUserNew();
        if (PrivacyHelper.isAgreePrivacy()) {
            uploadAppListInfo();
        }
    }

    /**
     * 新接口　102协议上传接口　内部接口　传入参数为Statistics102Bean 对无mSettingInfo为""的数据进行不上传处理
     *
     * @param statisticsBaseBean 102统计bean
     */
    private static void upload102InfoPrivateNew(Statistics102Bean statisticsBaseBean) {
        if (statisticsBaseBean.mSettingInfo.equals("")) {
            return;
        }
        StringBuffer sb = new StringBuffer();
        sb.append(StatisticsConstants.LOG_ID_759).append(PROTOCOL_DIVIDER);
        sb.append(statisticsBaseBean.mSettingInfo).append(PROTOCOL_DIVIDER);
        sb.append(statisticsBaseBean.mType).append(PROTOCOL_DIVIDER);
        sb.append("").append(PROTOCOL_DIVIDER);
        sb.append("");
        uploadStaticData(PROTOCOL_102, StatisticsConstants.LOG_ID_759, sb.toString(), false);
    }

    /**
     * 上传103 协议
     * */
    public static void upload103InfoPrivate() {
        Statistics103Bean statistics103Bean = Statistics103Bean.build();
        uploadStaticData(PROTOCOL_103,
                StatisticsConstants.LOG_ID_584,
                statistics103Bean.transformStatisticString(PROTOCOL_DIVIDER),
                false);
    }

    /**
     * 11:是否FB用户 1:是;0否;2:获取不到
     */
    private static void uploadIsFacebookUserNew() {
        Statistics102Bean statistics102Bean = Statistics102Bean.builder();
        statistics102Bean.mType = "2";
        if (AppUtils.isFacebookInstalled(TheApplication.getAppContext())) {
            statistics102Bean.mSettingInfo = "1";
        } else {
            statistics102Bean.mSettingInfo = "0";
        }
        upload102InfoPrivateNew(statistics102Bean);
    }

    /**
     * 用户安装列表：元素间用#号分割，例如：包名;是否内置;软件版本名;软件版本号#包名;是否内置;软件版本名;软件版本号（是否内置，1：是，0：否）
     */
    private static void uploadAppListInfo() {
        Statistics102Bean statistics102Bean = Statistics102Bean.builder();
        StringBuilder sb = new StringBuilder();

        List<PackageInfo> packages = AppUtils.getInstalledPackages(TheApplication.getAppContext());

        ArrayList<String> buildInApps = new ArrayList<>();
        try {
            List<ApplicationInfo> applicationInfoList = TheApplication.getAppContext().getPackageManager().getInstalledApplications(0);
            for (ApplicationInfo info : applicationInfoList) {
                if ((info.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                    buildInApps.add(info.packageName);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (null != packages && packages.size() > 0) {
            for (PackageInfo packageInfo : packages) {
                if (packageInfo != null) {
                    // 包名
                    if (packageInfo.packageName != null) {
                        sb.append(packageInfo.packageName);
                    }
                    sb.append(";");

                    // 是否内置
                    if (packageInfo.packageName != null) {
                        sb.append(buildInApps.contains(packageInfo.packageName) ? "1" : "0");
                    }
                    sb.append(";");

                    // 软件版本名
                    if (packageInfo.versionName != null) {
                        sb.append(packageInfo.versionName);
                    }
                    sb.append(";");

                    // 软件版本名
                    sb.append(packageInfo.versionCode);
                    sb.append("#");
                }
            }
            if (sb.length() > 0) {
                sb.deleteCharAt(sb.length() - 1);
            }
        }
        statistics102Bean.mSettingInfo = sb.toString();
        statistics102Bean.mType = "3";
        upload102InfoPrivateNew(statistics102Bean);
    }

    /**
     * 内部使用接口
     *
     * @param logId log id
     * @param funId fun id
     * @param buffer data
     */
    private static void uploadStaticData(final int logId,
                                         final int funId,
                                         final String buffer) {
        uploadStaticData(logId, funId, buffer, false);
    }

    /**
     * 内部使用接口
     *
     * @param logId log id
     * @param funId fun id
     * @param statisticsData data
     * @param ignoreAgreePrivacy 是否忽略判断是否同意隐私协议
     */
    private static void uploadStaticData(final int logId,
                                         final int funId,
                                         final String statisticsData,
                                         boolean ignoreAgreePrivacy) {
        if (!STATISTICS_UPLOAD) {
            return;
        }
       /* //当数据没有加载完成时 则保存任务 等待加载完成
        if (!LauncherModel.getInstance().isGlobalDataLoadingDone()) {
            INSTANCES.mPendingTasks.add(new StatisticsTask(logId, funid,
                    statisticsData, ignoreAgreePrivacy, false));
            return;
        }*/
        // 用户同意协议才上传信息
        if (!ignoreAgreePrivacy && !PrivacyHelper.isAgreePrivacy()) {
            INSTANCES.mPendingTasks.add(new StatisticsTask(logId, funId,
                    statisticsData, ignoreAgreePrivacy, false));
            return;
        }
        //1.13
        //新增:第一天设置为实时上传
        long currentTime = System.currentTimeMillis();
        long installTime = SharedPreferencesManager.getInstance(TheApplication.getAppContext()).getLong(IPreferencesIds.KEY_FIRST_INSTALL_TIME, currentTime);
        long diffTime = currentTime - installTime;
        int day = (int) (diffTime / TimeUtil.MILLIS_IN_DAY);
        if (logId == PROTOCOL_101) {
            //安装第一天
            if (day == 0) {
                StatisticsManager.getInstance(TheApplication.getAppContext())
                        .uploadStaticDataForOptions(
                                logId,
                                funId,
                                statisticsData,
                                null,
                                new OptionBean(
                                        OptionBean.OPTION_INDEX_IMMEDIATELY_ANYWAY,
                                        true));
            } else {
                // OptionBean为空表示受服务器控制上传
                StatisticsManager.getInstance(TheApplication.getAppContext())
                        .uploadStaticDataForOptions(
                                logId,
                                funId,
                                statisticsData,
                                null);
            }
        } else if (logId == PROTOCOL_104) {
            StatisticsManager.getInstance(TheApplication.getAppContext())
                    .upLoadStaticData(logId,
                            funId, statisticsData);
        } else if (logId == PROTOCOL_102) {
            StatisticsManager.getInstance(TheApplication.getAppContext())
                    .uploadStaticDataForOptions(
                            logId,
                            funId,
                            statisticsData,
                            null,
                            new OptionBean(
                                    OptionBean.OPTION_INDEX_IMMEDIATELY_ANYWAY,
                                    true));
        } else if (logId == PROTOCOL_103) {
            //add by nieyh 实时上传
            StatisticsManager.getInstance(TheApplication.getAppContext())
                    .uploadStaticDataForOptions(
                            logId,
                            funId,
                            statisticsData,
                            null,
                            new OptionBean(
                                    OptionBean.OPTION_INDEX_IMMEDIATELY_ANYWAY,
                                    true));
        }
    }

    /**
     * 内部使用接口
     *
     * @param logId log id
     * @param funId fun id
     * @param statisticsData data
     * @param ignoreAgreePrivacy 是否忽略判断是否同意隐私协议
     */
    private static void uploadStaticDataForce(final int logId,
                                              final int funId,
                                              final String statisticsData,
                                              boolean ignoreAgreePrivacy) {
        if (!STATISTICS_UPLOAD) {
            return;
        }
        // 用户同意协议才上传信息
        if (!ignoreAgreePrivacy && !PrivacyHelper.isAgreePrivacy()) {
            INSTANCES.mPendingTasks.add(new StatisticsTask(logId, funId,
                    statisticsData, ignoreAgreePrivacy, true));
            return;
        }
        if (logId == PROTOCOL_101) {
            // OptionBean为空表示受服务器控制上传
            StatisticsManager.getInstance(TheApplication.getAppContext())
                    .uploadStaticDataForOptions(
                            logId,
                            funId,
                            statisticsData,
                            null,
                            new OptionBean(
                                    OptionBean.OPTION_INDEX_IMMEDIATELY_ANYWAY,
                                    true));
        } else if (logId == PROTOCOL_104) {
            StatisticsManager.getInstance(TheApplication.getAppContext()).upLoadStaticData(logId,
                    funId,
                    statisticsData);

        } else if (logId == PROTOCOL_102) {
            StatisticsManager.getInstance(TheApplication.getAppContext()).uploadStaticDataForOptions(
                    logId,
                    funId,
                    statisticsData,
                    null,
                    new OptionBean(OptionBean.OPTION_INDEX_IMMEDIATELY_ANYWAY, true));
        }
    }

    public static void upload19Info() {
        SharedPreferencesManager sharedPreferencesManager = SharedPreferencesManager.getInstance(TheApplication.getInstance());
        boolean isNew = sharedPreferencesManager.getBoolean(IPreferencesIds.KEY_IS_NEW_USER, true);
        if (isNew) {
            sharedPreferencesManager.commitBoolean(IPreferencesIds.KEY_IS_NEW_USER, false);
        }

        int isBackground = AppUtils.isFrontActivity(TheApplication.getInstance(), Const.PACKAGE_NAME) ? 0 : 1;
        // google警告,只有用户授权,才能上传用户信息,这里是说传了用户的应用包名
        // 所以用户未授权时,不能上传包名
        boolean agreePrivacy = PrivacyHelper.isAgreePrivacy();
        String launcherPackageName = agreePrivacy ? Machine.getLauncherPackageName(TheApplication.getInstance()) : "";

        final String productID = StatisticsConstants.PRODUCT_ID;
        final String channel = AppUtils.getChannel(TheApplication.getInstance());
        final boolean isPay = false;
        final boolean needRootInfo = true;
        final String key = ABTest.getInstance().getUser();
        final int versionCode = BuildConfig.VERSION_CODE;
        final String versionName = BuildConfig.VERSION_NAME;
        final String appendData = "" +
                StatisticsTools.PROTOCOL_DIVIDER +
                com.gau.go.gostaticsdk.utiltool.Machine.getLanguage(TheApplication.getInstance()) +
                StatisticsTools.PROTOCOL_DIVIDER +
                launcherPackageName +
                StatisticsTools.PROTOCOL_DIVIDER +
                isBackground;

        StatisticsManager.getInstance(TheApplication.getInstance()).upLoadBasicInfoStaticData(
                productID,
                channel,
                isPay,
                needRootInfo,
                key,
                isNew,
                versionCode,
                versionName,
                appendData);
    }
}
