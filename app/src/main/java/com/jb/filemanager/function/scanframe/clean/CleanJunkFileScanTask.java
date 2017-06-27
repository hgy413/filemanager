package com.jb.filemanager.function.scanframe.clean;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.widget.ProgressBar;

import com.jb.filemanager.Const;
import com.jb.filemanager.R;
import com.jb.filemanager.TheApplication;
import com.jb.filemanager.commomview.GroupSelectBox;
import com.jb.filemanager.database.provider.DataProvider;
import com.jb.filemanager.eventbus.IOnEventMainThreadSubscriber;
import com.jb.filemanager.function.scanframe.bean.adbean.AdBean;
import com.jb.filemanager.function.scanframe.bean.appBean.AppItemInfo;
import com.jb.filemanager.function.scanframe.bean.bigfolder.BigFolderBean;
import com.jb.filemanager.function.scanframe.bean.cachebean.AppCacheBean;
import com.jb.filemanager.function.scanframe.bean.cachebean.CacheBean;
import com.jb.filemanager.function.scanframe.bean.cachebean.SysCacheBean;
import com.jb.filemanager.function.scanframe.bean.cachebean.subitem.SubAppCacheBean;
import com.jb.filemanager.function.scanframe.bean.cachebean.subitem.SubSysCacheBean;
import com.jb.filemanager.function.scanframe.bean.common.FileType;
import com.jb.filemanager.function.scanframe.bean.common.itemcommon.GroupType;
import com.jb.filemanager.function.scanframe.bean.common.itemcommon.ItemBean;
import com.jb.filemanager.function.scanframe.bean.common.subitemcommon.SubItemBean;
import com.jb.filemanager.function.scanframe.bean.filebean.FileBean;
import com.jb.filemanager.function.scanframe.bean.filebean.FileFlag;
import com.jb.filemanager.function.scanframe.bean.memorytrashbean.MemoryBean;
import com.jb.filemanager.function.scanframe.bean.residuebean.ResidueBean;
import com.jb.filemanager.function.scanframe.clean.event.CacheDataUpdateDoneEvent;
import com.jb.filemanager.function.scanframe.clean.event.CacheUpdateLangDataDoneEvent;
import com.jb.filemanager.function.scanframe.clean.event.CleanCheckedFileSizeEvent;
import com.jb.filemanager.function.scanframe.clean.event.CleanDBDataInitDoneEvent;
import com.jb.filemanager.function.scanframe.clean.event.CleanScanDoneEvent;
import com.jb.filemanager.function.scanframe.clean.event.CleanScanFileSizeEvent;
import com.jb.filemanager.function.scanframe.clean.event.CleanScanPathEvent;
import com.jb.filemanager.function.scanframe.clean.ignore.CleanIgnoreAdBean;
import com.jb.filemanager.function.scanframe.clean.ignore.CleanIgnoreBean;
import com.jb.filemanager.function.scanframe.clean.ignore.CleanIgnoreCacheAppBean;
import com.jb.filemanager.function.scanframe.clean.ignore.CleanIgnoreCachePathBean;
import com.jb.filemanager.function.scanframe.clean.ignore.CleanIgnoreResidueBean;
import com.jb.filemanager.function.scanframe.clean.util.ApkComparator;
import com.jb.filemanager.function.scanframe.clean.util.CleanListUtils;
import com.jb.filemanager.function.scanframe.clean.util.FileListComparator;
import com.jb.filemanager.function.scanframe.manager.MemoryTrashManager;
import com.jb.filemanager.function.scanframe.manager.SysCacheManager;
import com.jb.filemanager.function.scanframe.manager.ad.AdManager;
import com.jb.filemanager.function.scanframe.manager.residue.ResidualFileManager;
import com.jb.filemanager.manager.spm.IPreferencesIds;
import com.jb.filemanager.manager.spm.SharedPreferencesManager;
import com.jb.filemanager.os.ZAsyncTask;
import com.jb.filemanager.util.AppUtils;
import com.jb.filemanager.util.FileTypeUtil;
import com.jb.filemanager.util.Logger;
import com.jb.filemanager.util.StorageUtil;
import com.jb.filemanager.util.file.FileUtil;
import com.jb.filemanager.util.log.TimeRecord;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * 普通垃圾扫描任务
 *
 * @author chenbenbin
 */
public class CleanJunkFileScanTask extends ScanTask implements ITask {
    // 常量
    private static final String TAG = "CleanManager_Scan";
    private static final String SCAN_TAG = "Scan_Any";
    /**
     * 系统缓存扫描保护时间——删除后的保护
     */
    private static final int SYS_CACHE_DELETE_SCAN_WAIT_TIME = 3 * 60 * 1000;
    /**
     * 系统缓存扫描保护时间——扫描后的保护
     */
    private static final int SYS_CACHE_RESCAN_WAIT_TIME = 5 * 1000;
    /**
     * 10MB
     */
    private static final int TEN_MB = 1024 * 1024 * 10;
    // 管理器
    private Context mContext;
    private DataProvider mDataProvider;
    private AppManager mAppManager;
    private SharedPreferencesManager mPreferenceManager;
    //private CleanIgnorePathManager mIgnorePathManager;
    private CleanEventManager mEventManager;
    //private FilePathDataCollectManager mCollectManager;
    private MemoryTrashManager mMemoryManager;
    private EventBus mEventBus;
    //private CleanTimer mCleanTimer;
    private CleanScanTaskListener mListener;
    // 数据
    /**
     * 应用缓存
     */
    private ArrayList<CacheBean> mCacheList = new ArrayList<CacheBean>();
    /**
     * 残留文件
     */
    private ArrayList<ResidueBean> mResidueList = new ArrayList<ResidueBean>();
    /**
     * 广告
     */
    private ArrayList<AdBean> mAdList = new ArrayList<AdBean>();
    /**
     * 临时文件
     */
    private ArrayList<FileBean> mTempFiles = new ArrayList<FileBean>();
    /**
     * apk文件安装包
     */
    private ArrayList<FileBean> mApkFiles = new ArrayList<FileBean>();
    /**
     * 大文件
     */
    private ArrayList<FileBean> mBigSizeFiles = new ArrayList<FileBean>();
    /**
     * 内存
     */
    private ArrayList<MemoryBean> mMemoryList = new ArrayList<MemoryBean>();
    /**
     * 大文件夹
     */
    private ArrayList<BigFolderBean> mBigFolderList = new ArrayList<BigFolderBean>();
    /**
     * 大文件夹临时列表：用于存储当前扫描的文件夹下扫出的文件列表
     */
    private ArrayList<BigFolderBean> mTempBigFolderList = new ArrayList<BigFolderBean>();
    /**
     * 当前扫描的文件夹
     */
    private BigFolderBean mCurFolder = new BigFolderBean();
    /**
     * 扫描过滤路径
     */
    private HashSet<String> mIgnorePaths = new HashSet<String>();
    /**
     * 上次扫描的一级路径
     */
    private String mLastScanPath;
    /**
     * 上次一级路径的扫描时间点
     */
    private long mLastPathScanTime;
    /**
     * 上次系统缓存扫描完毕的时间
     */
    private long mSysCacheScanTime;
    /**
     * 开始扫描时间
     */
    private long mStartScanTime;
    // 标志位
    private boolean mIsRestart = false;
    /**
     * 语言进行切换
     */
    private boolean mIsAppCacheLanguageUpdate = false;
    /**
     * 是否需要上传SD卡路径数据
     */
    //private boolean mIsNeedUploadData = false;
    /**
     * 应用缓存是否被删除，用于扫描保护机制(因为系统数据列表没法清除)
     */
    private boolean mIsSysCacheDeleted = false;
    // 工具
    /**
     * 对APK进行排序，按照勾选与否排，再按大小排
     */
    private ApkComparator mApkComparator = new ApkComparator();
    private TimeRecord mTimeRecord;
    /**
     * 文件排靠前，文件夹靠后
     */
    private FileListComparator mFileListComparator = new FileListComparator();

    public CleanJunkFileScanTask(Context context) {
        init(context);
    }

    private void init(Context context) {
        mContext = context.getApplicationContext();
//        mDataProvider = LauncherModel.getInstance().getDataProvider();
        mDataProvider = new DataProvider(mContext);
        mEventManager = CleanEventManager.getInstance();
        mPreferenceManager = SharedPreferencesManager.getInstance(mContext);
        //mIgnorePathManager = CleanIgnorePathManager.getInstance(mContext);
        //mCollectManager = FilePathDataCollectManager.getInstance(mContext);
        mAppManager = AppManager.getIntance();
        mEventBus = TheApplication.getGlobalEventBus();
        mMemoryManager = MemoryTrashManager.getInstance();
        //mCleanTimer = CleanTimer.getInstance(mContext);
        mTimeRecord = new TimeRecord(TAG);
    }

    //*******************************************************  数据操作 **************************************************************//

    @SuppressWarnings("unchecked")
    public List<CacheBean> getCacheList() {
        appCacheInsertSysCache();
        return (List<CacheBean>) mCacheList.clone();
    }

    /**
     * 缓存列表首位添加系统缓存数据
     */
    private void appCacheInsertSysCache() {
        boolean isSysCacheInserted = !mCacheList.isEmpty()
                && mCacheList.get(0) instanceof SysCacheBean;
        // 没有添加过 并且 没有被删除时, 添加系统缓存
        if (!isSysCacheInserted && !mIsSysCacheDeleted) {
            mCacheList.add(0, getSysCache());
        }
    }

    /**
     * 构建系统缓存的列表二级对象
     */
    private SysCacheBean getSysCache() {
        ArrayList<SubItemBean> list = SubSysCacheBean
                .createFromAppItemInfo(mAppManager.getCacheApps());
        SysCacheBean bean = new SysCacheBean();
        for (SubItemBean subItem : list) {
            bean.setSize(bean.getSize() + subItem.getSize());
        }
        bean.setDefaultCheck(true);
        bean.setTitle(mContext.getString(R.string.clean_item_sys_cache));
        bean.setSubItemList(list);
        return bean;
    }

    @SuppressWarnings("unchecked")
    public List<ResidueBean> getResidueList() {
        return (List<ResidueBean>) mResidueList.clone();
    }

    @SuppressWarnings("unchecked")
    public List<AdBean> getAdList() {
        return (List<AdBean>) mAdList.clone();
    }

    @SuppressWarnings("unchecked")
    public List<FileBean> getTempFilesList() {
        return (List<FileBean>) mTempFiles.clone();
    }

    @SuppressWarnings("unchecked")
    public List<FileBean> getApkFilesList() {
        return (List<FileBean>) mApkFiles.clone();
    }

    @SuppressWarnings("unchecked")
    public List<FileBean> getBigSizeFilesList() {
        return (List<FileBean>) mBigSizeFiles.clone();
    }

    @SuppressWarnings("unchecked")
    public List<MemoryBean> getMemoryList() {
        return (List<MemoryBean>) mMemoryList.clone();
    }

    @SuppressWarnings("unchecked")
    public List<BigFolderBean> getBigFolderList() {
        return (List<BigFolderBean>) mBigFolderList.clone();
    }

    /**
     * 清空扫描数据
     */
    private void cleanData() {
        mCacheList.clear();
        mTempFiles.clear();
        mApkFiles.clear();
        mBigFolderList.clear();
        mBigSizeFiles.clear();
        mResidueList.clear();
        mAdList.clear();
        mMemoryList.clear();
    }

    //******************************************************** 删除文件后更新内存数据 ***********************************************************//

    public void updateAppCacheList(List<ItemBean> delList) {
        CleanListUtils.updateDataList(mCacheList, delList);
    }

    public void updateResidueList(List<ItemBean> delList) {
        CleanListUtils.updateDataList(mResidueList, delList);
    }

    public void updateAdList(List<ItemBean> delList) {
        CleanListUtils.updateDataList(mAdList, delList);
    }

    public void updateTempList(List<ItemBean> delList) {
        CleanListUtils.updateDataList(mTempFiles, delList);
    }

    public void updateApkList(List<ItemBean> delList) {
        CleanListUtils.updateDataList(mApkFiles, delList);
    }

    public void updateBigFolderList(List<ItemBean> delList) {
        CleanListUtils.updateDataList(mBigFolderList, delList);
    }

    public void updateBigFileList(List<ItemBean> delList) {
        CleanListUtils.updateDataList(mBigSizeFiles, delList);
    }

    public void updateMemoryList(List<ItemBean> delList) {
        CleanListUtils.updateDataList(mMemoryList, delList);
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    //*******************************************************  扫描逻辑 - 外部接口 **************************************************************//
    @Override
    public void startTask() {
        mIsScanning = true;
        mIsStop = false;
        mIsSwitch = false;
        mStartScanTime = System.currentTimeMillis();
        mEventManager.sendScanStartEvent();
        executeScanTask();
    }

    @Override
    public void stopTask() {
        mIsStop = true;
        if (!CleanDBDataInitDoneEvent.isAllDone()) {
            if (mEventBus.isRegistered(mDBInitDoneEvent)) {
                mEventBus.unregister(mDBInitDoneEvent);
            }
            onStopTaskDone();
        }
    }

    @Override
    public void switchTask() {
        mIsSwitch = true;
        if (!CleanDBDataInitDoneEvent.isAllDone()) {
            if (mEventBus.isRegistered(mDBInitDoneEvent)) {
                mEventBus.unregister(mDBInitDoneEvent);
            }
            onSwitchDone();
        }
    }

    public void setTaskListener(CleanScanTaskListener listener) {
        mListener = listener;
    }

    /**
     * 开始删除
     */
    public void startDelete() {
        mEventManager.sendDeleteStartEvent();
        //mCleanTimer.recordDeleteDate();
    }

    /**
     * 查询单个应用的系统缓存数据
     */
    public void querySysCache(AppItemInfo info) {
        SysCacheManager appCacheManager = new SysCacheManager(mContext);
        try {
            appCacheManager.queryPackageSize(info, 1, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 清理所有的系统缓存数据
     */
    public void cleanSysCache() {
        mIsSysCacheDeleted = true;
        mSysCacheScanTime = System.currentTimeMillis();
        SysCacheManager.clearAllCache(mContext);
    }

    /**
     * 获取扫描进度
     */
    public float getScanProgress() {
        if (mIsScanning) {
            long scanPredictTime = mPreferenceManager.getLong(
                    IPreferencesIds.KEY_CLEAN_SCAN_TIME, 1000 * 30);
            float progress = 1.0f
                    * (System.currentTimeMillis() - mStartScanTime)
                    / scanPredictTime;
            return Math.min(0.9f, progress);
        } else if (CleanScanDoneEvent.isAllDone()) {
            return 1.0f;
        } else {
            return 0f;
        }
    }

    //*******************************************************  扫描逻辑 - 内部实现 - 整体流程 **************************************************************//

    /**
     * 执行扫描任务(数据初始化 + 异步扫描)
     */
    private void executeScanTask() {
        // 预启动，初始化数据&相关标志位
        Logger.w(TAG, "真正开始JunkFile扫描");
        //mIsNeedUploadData = mCollectManager.isNeedCollect();
        cleanData();
        mEventManager.cleanEventData();

        // 监听数据初始化状态，开支执行扫描任务
        if (CleanDBDataInitDoneEvent.isAllDone()) {
            Logger.i(TAG, "所有数据库都加载完毕!");
           // listenLanguageChange();
            executeScanAsyTask();
        } else {
            listenCleanDBDataInitDone();
        }
        // 扫描系统缓存
        scanSysCache();
    }

    /**
     * 监听数据库初始化
     */
    private void listenCleanDBDataInitDone() {
        if (!mEventBus.isRegistered(mDBInitDoneEvent)) {
            mEventBus.register(mDBInitDoneEvent);
        }
    }

    private IOnEventMainThreadSubscriber<CleanDBDataInitDoneEvent> mDBInitDoneEvent = new IOnEventMainThreadSubscriber<CleanDBDataInitDoneEvent>() {
        @Subscribe(threadMode = ThreadMode.MAIN)
        @Override
        public void onEventMainThread(CleanDBDataInitDoneEvent event) {
            Logger.i(TAG, "监听数据库加载完毕：" + event.toString());
            if (CleanDBDataInitDoneEvent.isAllDone()) {
                mEventBus.unregister(this);
                //listenLanguageChange();
                executeScanAsyTask();
            }
        }
    };

    /**
     * 任务切换:完成当前任务的中断
     */
    private void onSwitchDone() {
        Logger.e(TAG, "JunkFile中断！！！执行DeepCache");
        mIsScanning = false;
        mListener.onSwitchDone(CleanJunkFileScanTask.this);
        mEventManager.sendScanSuspendEvent();
    }

    /**
     * 完成任务的停止
     */
    private void onStopTaskDone() {
        mIsScanning = false;
        mEventManager.sendScanSuspendEvent();
    }

    /**
     * 监听语言切换，用于更新应用缓存的语言描述
     */
//    private void listenLanguageChange() {
//        if (!mEventBus.isRegistered(mLanguageChangeEvent)) {
//            mEventBus.register(mLanguageChangeEvent);
//        }
//    }

//    IOnEventMainThreadSubscriber<OnLanguageChangeFinish> mLanguageChangeEvent = new IOnEventMainThreadSubscriber<OnLanguageChangeFinish>() {
//        @Override
//        public void onEventMainThread(OnLanguageChangeFinish event) {
//            // 扫描过程中切换语言,则交给异步扫描内部去更新数据
//            if (mIsScanning) {
//                mIsAppCacheLanguageUpdate = true;
//            } else {
//                // 若扫描结束,立即更新语言，并请求网络更新(数据用于下次切换)
//                Logger.w(TAG, "扫描完成,监听到语言切换,更新应用缓存的语言内容");
//                onAppCacheLanguageUpdate();
//                CacheManager.getInstance(mContext).updateLangDataV5();
//            }
//        }
//    };

    /**
     * 应用缓存的语言内容更新
     */
//    private void onAppCacheLanguageUpdate() {
//        // TODO 语言更新可能会有冲突,一旦执行到一半,突然有数据来会有问题
//        ArrayList<AppCacheBean> appCacheList = new ArrayList<AppCacheBean>();
//        for (CacheBean cacheBean : mCacheList) {
//            if (cacheBean instanceof AppCacheBean) {
//                appCacheList.add((AppCacheBean) cacheBean);
//            }
//        }
//        CacheManager.getInstance(mContext).getCacheDescription(appCacheList);
//    }

    /**
     * 执行异步任务
     */
    private void executeScanAsyTask() {
        new LoopScanAsyncTask().executeOnExecutor(ZAsyncTask.THREAD_POOL_EXECUTOR);
    }

    /**
     * 扫描系统缓存
     */
    private void scanSysCache() {
        if (System.currentTimeMillis() - mSysCacheScanTime > SYS_CACHE_DELETE_SCAN_WAIT_TIME) {
            // 删除保护期外
            mIsSysCacheDeleted = false;
            if (System.currentTimeMillis() - mAppManager.getCompleteTime() > SYS_CACHE_RESCAN_WAIT_TIME && mAppManager.isComplete()) {
                // 扫描保护期外，并且已经扫描完毕了，则重新扫描系统缓存
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        mAppManager.scanAllAppItems();
                    }
                }).start();
            } else if (mAppManager.isComplete()) {
                // TODO 目前不会调用到，即使不停地切换任务
                mEventManager.sendCheckedFileSizeEvent(
                        CleanCheckedFileSizeEvent.CacheSize,
                        mAppManager.getAllAppCacheSize());
                mEventManager.sendScanFileSizeEvent(
                        CleanScanFileSizeEvent.CacheSize,
                        mAppManager.getAllAppCacheSize());
                mEventManager.sendSysCacheScanDoneEvent();
            }
        } else {
            mEventManager.sendSysCacheScanDoneEvent();
        }
    }

    /**
     * 递归循环扫描SD卡的异步线程
     *
     * @author chenbenbin
     */
    private class LoopScanAsyncTask extends ZAsyncTask<Void, Void, Void> {
        private long mRestartScanTime;
        private IOnEventMainThreadSubscriber<CacheDataUpdateDoneEvent> mAppCacheUpdateDoneEvent;
        private IOnEventMainThreadSubscriber<CacheUpdateLangDataDoneEvent> mAppCacheLangUpdateEvent;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mRestartScanTime = System.currentTimeMillis();
            registerAppCacheUpdateEvent();
        }

        /**
         * 监听应用缓存更新事件
         */
        private void registerAppCacheUpdateEvent() {
            mIsRestart = false;
            mIsAppCacheLanguageUpdate = false;
            mAppCacheUpdateDoneEvent = new IOnEventMainThreadSubscriber<CacheDataUpdateDoneEvent>() {
                @Subscribe(threadMode = ThreadMode.MAIN)
                @Override
                public void onEventMainThread(CacheDataUpdateDoneEvent event) {
                    // 扫描过程中一旦发生联网更新应用缓存数据，则重新执行SD卡扫描
                    mIsRestart = true;
                }
            };
            mAppCacheLangUpdateEvent = new IOnEventMainThreadSubscriber<CacheUpdateLangDataDoneEvent>() {
                @Subscribe(threadMode = ThreadMode.MAIN)
                @Override
                public void onEventMainThread(CacheUpdateLangDataDoneEvent event) {
                    mIsAppCacheLanguageUpdate = true;
                }
            };
            mEventBus.register(mAppCacheUpdateDoneEvent);
            mEventBus.register(mAppCacheLangUpdateEvent);
        }

        @Override
        protected Void doInBackground(Void... params) {
            mTimeRecord.begin();
            scanFilesFunction();
            while (mIsRestart) {
                // 重新扫描SD卡
                mIsRestart = false;
                restartScan();
            }
            if (mIsSwitch || mIsStop) {
                return null;
            }
            if (mIsAppCacheLanguageUpdate) {
                mIsAppCacheLanguageUpdate = false;
                Logger.w(TAG, "扫描未完成,收到应用缓存语言更新!");
                //onAppCacheLanguageUpdate();
            }
           // if (mIsNeedUploadData) {
            //    mCollectManager.uploadData();
            //}
            mTimeRecord.end();
            return null;
        }

        /**
         * 接收到应用缓存网络更新完毕标志，重新全部扫描(除了系统缓存，因为停止不了，需要特殊处理)
         */
        private void restartScan() {
            mRestartScanTime = System.currentTimeMillis();
            Logger.d(TAG, "首次更新数据，重新扫描!");
            cleanData();
            CleanCheckedFileSizeEvent.updateSuspendSize();
            CleanScanFileSizeEvent.updateSuspendSize();
            if (mAppManager.isComplete()) {
                Logger.i(TAG, "应用缓存数据更新成功,系统缓存数据已经加载完成");
                mEventManager.sendCheckedFileSizeEvent(
                        CleanCheckedFileSizeEvent.CacheSize,
                        mAppManager.getAllAppCacheSize());
                mEventManager.sendScanFileSizeEvent(
                        CleanScanFileSizeEvent.CacheSize,
                        mAppManager.getAllAppCacheSize());
            } else {
                Logger.i(TAG, "应用缓存数据更新成功,系统缓存数据尚未加载完成");
            }
            scanMemory();
            scanFilesFunction();
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (mIsSwitch) {
                onSwitchDone();
            } else if (mIsStop) {
                onStopTaskDone();
            } else {
                onTaskDone();
            }
        }

        /**
         * 任务执行完毕
         */
        private void onTaskDone() {
            recordScanTime();
            unRegisterAppCacheUpdateEvent();
            mEventManager.sendSDCardScanDoneEvent();
            ResidualFileManager.getInstance(mContext).updateDataIfNeed();
            /*AdManager.getInstance(mContext).updateDataIfNeed();*/
            //uploadStatistics();
            //mCleanTimer.setDefaultNotifySize(CleanCheckedFileSizeEvent.getJunkFileAllSize(true));
            mEventManager.sendJunkScanDoneEvent();
            mListener.onTaskDone(CleanJunkFileScanTask.this);
            mIsScanning = false;
        }

        /**
         * 记录当前的扫描时间
         */
        private void recordScanTime() {
            if (mRestartScanTime > mStartScanTime) {
                // 重新扫描:区分处理，避免因为网络更新数据导致扫描时间过长
                mPreferenceManager.commitLong(IPreferencesIds.KEY_CLEAN_SCAN_TIME,
                        System.currentTimeMillis() - mRestartScanTime);
            } else {
                // 没有重新扫描
                mPreferenceManager.commitLong(IPreferencesIds.KEY_CLEAN_SCAN_TIME,
                        System.currentTimeMillis() - mStartScanTime);
            }
        }

        private void unRegisterAppCacheUpdateEvent() {
            if (mAppCacheUpdateDoneEvent != null
                    && mEventBus.isRegistered(mAppCacheUpdateDoneEvent)) {
                mEventBus.unregister(mAppCacheUpdateDoneEvent);
            }
            if (mAppCacheLangUpdateEvent != null
                    && mEventBus.isRegistered(mAppCacheLangUpdateEvent)) {
                mEventBus.unregister(mAppCacheLangUpdateEvent);
            }
        }

        /**
         * 扫描完成,上次统计
         */
//        private void uploadStatistics() {
//            uploadScanTime();
//            uploadBigFolder();
//            uploadAppData();
//            uploadAnalysis();
//        }

        /**
         * 上传扫描耗时
         */
        @SuppressWarnings("deprecation")
        private void uploadScanTime() {
            int scanTime = (int) ((System.currentTimeMillis() - mStartScanTime) / 1000);
            Logger.d("Junk", "扫描耗时 : " + scanTime);
            //StatisticsTools.uploadRemark(StatisticsConstants.JUNK_SCAN,scanTime);
        }

        /**
         * 上传缓存垃圾数据
         */
        @SuppressWarnings("deprecation")
        private void uploadAppData() {
            StringBuilder uploadString = new StringBuilder();
            int appCount = mCacheList.size();
            int cleanSize = 0;
            for (int i = 0; i < appCount; i++) {
                CacheBean bean = mCacheList.get(i);
                if (bean instanceof AppCacheBean) {
                    AppCacheBean appCacheBean = (AppCacheBean) bean;
                    uploadString
                            .append(appCacheBean
                                    .getPackageName()).append(",")
                            .append(appCacheBean.getSize()).append("#");
                    cleanSize += appCacheBean.getSize();
                }
            }
            //StatisticsTools.uploadRemark(StatisticsConstants.JUNK_SCAN_APP,uploadString.toString());
            //StatisticsTools.uploadLocationAndRemark(StatisticsConstants.JUNK_SCAN_GS, appCount + "", cleanSize+ "");
        }

        /**
         * 上传扫描结果占比统计
         */
//        private void uploadAnalysis() {
//            Statistics101Bean info = Statistics101Bean.builder();
//            info.mOpeateId = StatisticsConstants.JUNK_RAB_SCAN;
//            info.mEntrance = CleanCheckedFileSizeEvent.CacheSize.getSize() + "";
//            info.mLocation = CleanCheckedFileSizeEvent.TempFileSize.getSize()
//                    + "";
//            info.mRelativeObject = CleanCheckedFileSizeEvent.AdSize.getSize()
//                    + "";
//            info.mRemark = CleanCheckedFileSizeEvent.APKFileSize.getSize() + "";
//            StatisticsTools.upload101InfoNew(info);
//        }

        /**
         * 上传大文件夹统计
         */
//        private void uploadBigFolder() {
//            if (mPreferenceManager.getBoolean(IPreferencesIds.KEY_BIG_FOLDER_SCAN_UPLOAD, false)) {
//                return;
//            }
//            int scanTime = (int) ((System.currentTimeMillis() - mStartScanTime) / 1000);
//            long totalSize = 0;
//            StringBuilder builder = new StringBuilder();
//            for (BigFolderBean bean : mBigFolderList) {
//                totalSize += bean.getSize();
//                if (builder.length() != 0) {
//                    builder.append("#");
//                }
//                builder.append(bean.getPath()).append(",").append(bean.getSize());
//            }
//            Statistics101Bean bean = new Statistics101Bean(StatisticsConstants.STOR_FIL_SCAN);
//            bean.mEntrance = scanTime + "";
//            bean.mTab = totalSize + "";
//            bean.mRemark = builder.toString();
//            StatisticsTools.upload101InfoNew(bean);
//            mPreferenceManager.commitBoolean(IPreferencesIds.KEY_BIG_FOLDER_SCAN_UPLOAD, true);
//        }
    }

    //*******************************************************  扫描逻辑 - 内部实现 - 具体类型扫描 **************************************************************//

    /**
     * 扫描全部垃圾的入口
     */
    private void scanFilesFunction() {
        mIgnorePaths.clear();
        mTempBigFolderList.clear();
        mCacheList.clear();
        mTempFiles.clear();
        mApkFiles.clear();
        mBigFolderList.clear();
        mBigSizeFiles.clear();
        mResidueList.clear();
        mAdList.clear();

        if (!StorageUtil.isSDCardAvailable()) {
            return;
        }
        Set<String> sdPaths = StorageUtil.getAllExternalPaths(mContext);
        for (String sdPath : sdPaths) {
            File file = new File(sdPath);
            ProgressBar progressBar;
            if (file.exists()) {
                Logger.i(TAG, "SD卡路径：" + file.getPath() + "。开始应用缓存路径扫描!");
                // 应用缓存
                scanAppCache(sdPath);
                // 应用残留
                scanResidue(sdPath, file);
                // 广告
                scanAd(sdPath);
                // 遍历SD卡
                //updateIgnorePath(sdPath);
                // 是否扫遍历完SD卡，没有被中断
                boolean isRunAll = scanFile(sdPath, file, 0, false,
                        mCommonScanRunnable, null);
                checkBigFolder(null);
                //logAndUploadScanTime();
                if (mIsSwitch || mIsStop) {
                    return;
                }
                if (mIsRestart) {
                    // 重新扫描SD卡
                    return;
                }
                if (!isRunAll) {
                    break;
                }
                mTimeRecord.mark("完成SD卡遍历");
            }
        }
        if (mIsRestart) {
            return;
        }
        CleanListUtils.sortItemList(mCacheList);
        CleanListUtils.sortItemList(mResidueList);
        CleanListUtils.sortItemList(mAdList);
        CleanListUtils.sortItemList(mTempFiles);
        CleanListUtils.sortItemList(mBigSizeFiles);
        CleanListUtils.sortItemList(mBigFolderList);
        Collections.sort(mApkFiles, mApkComparator);
        mTimeRecord.mark("完成排序");
    }

    /**
     * 扫描应用缓存
     */
    private void scanAppCache(String sdPath) {
        if (mIsSwitch || mIsStop) {
            return;
        }
        long cacheTime = System.currentTimeMillis();
        ArrayList<AppCacheBean> cacheList = CacheManagerWrapper
                .getInstance(mContext).getAppCacheList(sdPath);// 不存在的缓存路径已经筛选掉
        Logger.i(TAG, "分析SD卡中应用缓存路径,共" + cacheList.size() + "个应用。");
        for (CacheBean cacheBean : cacheList) {
            for (SubItemBean subItemBean : cacheBean.getSubItemList()) {
                if (mIsSwitch || mIsStop) {
                    return;
                }
                SubAppCacheBean appSubAppCacheBean = (SubAppCacheBean) subItemBean;
                mIgnorePaths.add(appSubAppCacheBean
                        .getPathWithoutEndSeparator().toLowerCase());
                HashSet<String> childList = appSubAppCacheBean.getChildList();
                if (childList.isEmpty()) {
                    scanFile(sdPath, new File(appSubAppCacheBean.getPath()), 0,
                            true, mAppCacheScanRunnable, appSubAppCacheBean);
                } else {
                    // 由于子路径列表会在扫描过程中判断是否符合过期条件，会再次添加元素到队列中，引起遍历错误
                    // 所以先取出到临时队列，在扫描过程中符合条件的子路径添加到队列中
                    HashSet<String> tempList = new HashSet<String>();
                    tempList.addAll(childList);
                    if (appSubAppCacheBean.getDayBefore() != 0) {
                        // 为了避免包含过期判断时，队列包含上层文件夹，导致整个文件删除，从而子文件过期判断无意义
                        childList.clear();
                    }
                    for (String path : tempList) {
                        scanFile(sdPath, new File(path), 0, true,
                                mAppCacheScanRunnable, appSubAppCacheBean);
                    }
                }
                cacheBean.setSize(cacheBean.getSize()
                        + appSubAppCacheBean.getSize());
            }
        }
        mCacheList.addAll(cacheList);
        mEventManager.sendAppCacheScanDoneEvent();
        Logger.i(TAG, "获取SD卡中应用缓存路径 - 耗时 ："
                + (System.currentTimeMillis() - cacheTime));
    }

    /**
     * 扫描残留
     */
    private void scanResidue(String sdPath, File file) {
        if (mIsSwitch || mIsStop) {
            return;
        }
        long residueTime = System.currentTimeMillis();
        ArrayList<ResidueBean> residueList = ResidualFileManager.getInstance(
                mContext).scanResidueData(file, getResidueIgnoreList());
        filterResidueByAppCache();
        Logger.i(TAG, "获取SD卡中残留路径 - 耗时 ："
                + (System.currentTimeMillis() - residueTime));
        residueTime = System.currentTimeMillis();
        for (ResidueBean bean : residueList) {
            for (String path : bean.getPaths()) {
                if (mIsSwitch || mIsStop) {
                    return;
                }
                mIgnorePaths.add(path.toLowerCase());
                scanFile(sdPath, new File(path), 0, true, mResidueScanRunnable,
                        bean);
            }
        }
        mResidueList.addAll(residueList);
        mEventManager.sendResidueScanDoneEvent();
        Logger.i(TAG, "获取SD卡中残留大小 - 耗时 ："
                + (System.currentTimeMillis() - residueTime));
    }

    /**
     * 获取残留白名单应用列表
     */
    private HashSet<String> getResidueIgnoreList() {
        HashSet<String> result = new HashSet<String>();
        List<CleanIgnoreBean> ignoreBeans = queryResidueIgnore();
        for (CleanIgnoreBean ignoreBean : ignoreBeans) {
            CleanIgnoreResidueBean ignoreResidueBean = (CleanIgnoreResidueBean) ignoreBean;
            result.addAll(ignoreResidueBean.getPkgNameSet());
        }
        return result;
    }

    /**
     * 过滤掉残留应用中的应用缓存路径
     */
    private void filterResidueByAppCache() {
        Iterator<ResidueBean> residueIterator = mResidueList.iterator();
        // 残留Bean列表
        while (residueIterator.hasNext()) {
            ResidueBean residueBean = residueIterator.next();
            Iterator<String> residuePathIterator = residueBean.getPaths()
                    .iterator();
            // 残留Bean的路径列表
            while (residuePathIterator.hasNext()) {
                String residuePath = residuePathIterator.next();
                int cacheSize = mCacheList.size();
                // 应用缓存列表
                for (int i = 0; i < cacheSize; i++) {
                    CacheBean cacheBean = mCacheList.get(i);
                    if (!(cacheBean instanceof AppCacheBean)) {
                        continue;
                    }
                    AppCacheBean appCacheBean = (AppCacheBean) cacheBean;
                    Iterator<SubItemBean> cacheIterator = appCacheBean
                            .getSubItemList().iterator();
                    boolean mRemoved = false;
                    // 应用缓存对应的路径列表
                    while (cacheIterator.hasNext()) {
                        SubItemBean subItem = cacheIterator.next();
                        String cachePath = FileUtil.removeEndSeparator(subItem
                                .getPath());
                        if (cachePath.startsWith(residuePath)
                                || residuePath.startsWith(cachePath)) {
                            residuePathIterator.remove();
                            mRemoved = true;
                            break;
                        }
                    }
                    if (mRemoved) {
                        break;
                    }
                }
            }
            if (residueBean.getPaths().isEmpty()) {
                residueIterator.remove();
            }
        }
    }

    /**
     * 扫描广告
     */
    private void scanAd(String sdPath) {
        if (mIsSwitch || mIsStop) {
            return;
        }
        long adTime = System.currentTimeMillis();
        ArrayList<AdBean> adList = AdManager.getInstance(mContext).getAdList(sdPath, getAdIgnoreList());
        for (AdBean bean : adList) {
            if (mIsSwitch || mIsStop) {
                return;
            }
            String path = bean.getPath();
            bean.setCheck(true);
            bean.setDefaultCheck(true);
            mIgnorePaths.add(path.toLowerCase());
            scanFile(sdPath, new File(path), 0, true, mAdScanRunnable, bean);
        }
        mAdList.addAll(adList);
        mEventManager.sendAdScanDoneEvent();
        Logger.i(TAG, "获取广告 - 耗时 ：" + (System.currentTimeMillis() - adTime));
    }

    /**
     * 获取广告白名单路径列表
     */
    private HashSet<String> getAdIgnoreList() {
        HashSet<String> result = new HashSet<>();
        List<CleanIgnoreBean> ignoreBeans = queryAdIgnore();
        for (CleanIgnoreBean ignoreBean : ignoreBeans) {
            CleanIgnoreAdBean ignoreAdBean = (CleanIgnoreAdBean) ignoreBean;
            result.add(ignoreAdBean.getPath());
        }
        return result;
    }

    /**
     * 更新扫描黑名单路径
     */
//    private void updateIgnorePath(String sdPath) {
//        for (String path : mIgnorePathManager.getIgnorePaths()) {
//            String ignorePath = sdPath + path;
//            mIgnorePaths.add(ignorePath.toLowerCase());
//        }
//    }

    /**
     * 扫描逻辑接口
     *
     * @author chenbenbin
     */
    private interface ScanRunnable {
        void run(String sdPath, int paramInt, File childFile,
                 boolean isDeepScanTemp, Object object);
    }

    /**
     * 扫描应用缓存的具体逻辑
     */
    private ScanRunnable mAppCacheScanRunnable = new ScanRunnable() {
        @Override
        public void run(String sdPath, int paramInt, File childFile,
                        boolean isDeepScanTemp, Object object) {
            if (object == null) {
                return;
            }
            onScanAppCache(sdPath, paramInt, childFile, isDeepScanTemp, this,
                    (SubAppCacheBean) object);
        }

        /**
         * 扫描到应用缓存
         */
        private void onScanAppCache(String sdPath, int paramInt, File file,
                                    boolean isDeepScanTemp, ScanRunnable runnable,
                                    SubAppCacheBean subBean) {
            if (mCurFolder != null) {
                if (TextUtils.isEmpty(mCurFolder.getPackageName())) {
                    mCurFolder.setPackageName(subBean.getPackageName());
                }
                mCurFolder.setTitle(subBean.getTitle());
            }
            if (file.isFile()) {
                // 判断时间限制
                if (subBean.getDayBefore() != 0) {
                    boolean matchDayBefore = subBean.isMatchDayBefore(file);
                    if (matchDayBefore) {
                        // 如果符合X天前，则加入到子文件队列中
                        subBean.addChildFile(file.getPath());
                    } else {
                        return;
                    }
                }
                if (file.length() > TEN_MB) {
                    BigFolderBean bigFolderBean = new BigFolderBean();
                    bigFolderBean.setTitle(file.getName());
                    bigFolderBean.setPath(file.getPath());
                    bigFolderBean.setIsFolder(false);
                    bigFolderBean.setSize(file.length());
                    bigFolderBean.setCheck(false);
                    bigFolderBean.setPackageName(subBean.getPackageName());
                    mTempBigFolderList.add(bigFolderBean);
                }
                // 文件个数
                subBean.setFileCount(subBean.getFileCount() + 1);
                // 文件大小
                long length = file.length();
                subBean.setSize(subBean.getSize() + length);
                mEventManager.sendScanFileSizeEvent(
                        CleanScanFileSizeEvent.CacheSize, length);
                mEventManager.sendCheckedFileSizeEvent(
                        CleanCheckedFileSizeEvent.CacheSize,
                        subBean.isChecked() ? length : 0);
                onScanFile(file);
            } else if (file.isDirectory()) {
                // 文件夹个数
                subBean.setFolderCount(subBean.getFolderCount() + 1);
                onScanFolder(sdPath, paramInt, file, isDeepScanTemp, runnable,
                        subBean);
            }
        }
    };

    /**
     * 扫描应用残留的具体逻辑
     */
    private ScanRunnable mResidueScanRunnable = new ScanRunnable() {

        @Override
        public void run(String sdPath, int paramInt, File childFile,
                        boolean isDeepScanTemp, Object object) {
            if (object == null) {
                return;
            }
            onScanResidue(sdPath, paramInt, childFile, isDeepScanTemp, this,
                    (ResidueBean) object);
        }

        private void onScanResidue(String sdPath, int paramInt, File file,
                                   boolean isDeepScanTemp, ScanRunnable runnable,
                                   ResidueBean bean) {
            if (mCurFolder != null) {
                mCurFolder.setTitle(bean.getTitle());
            }
            if (file.isFile()) {
                if (file.length() > TEN_MB) {
                    BigFolderBean bigFolderBean = new BigFolderBean();
                    bigFolderBean.setTitle(file.getName());
                    bigFolderBean.setPath(file.getPath());
                    bigFolderBean.setIsFolder(false);
                    bigFolderBean.setSize(file.length());
                    bigFolderBean.setCheck(false);
                    mTempBigFolderList.add(bigFolderBean);
                }
                // 文件个数
                bean.setFileCount(bean.getFileCount() + 1);
                // 文件大小
                bean.setSize(bean.getSize() + file.length());
                // 文件类型
                FileType type = FileTypeUtil.getFileTypeSensitive(file
                        .getName());
                if (!type.equals(FileType.OTHER)) {
                    bean.addFileType(type);
                    if (type.equals(FileType.IMAGE)) {
                        bean.addImage(file.getPath());
                    } else if (type.equals(FileType.VIDEO)) {
                        bean.addVideo(file.getPath());
                    }
                }
                mEventManager.sendScanFileSizeEvent(
                        CleanScanFileSizeEvent.ResidueFileSize, file.length());
                onScanFile(file);
            } else if (file.isDirectory()) {
                // 文件夹个数，但残留文件夹根路径不统计
                if (paramInt != 0) {
                    bean.setFolderCount(bean.getFolderCount() + 1);
                }
                onScanFolder(sdPath, paramInt, file, isDeepScanTemp, runnable,
                        bean);
            }
        }
    };

    /**
     * 扫描应用广告的具体逻辑
     */
    private ScanRunnable mAdScanRunnable = new ScanRunnable() {
        @Override
        public void run(String sdPath, int paramInt, File childFile,
                        boolean isDeepScanTemp, Object object) {
            if (object == null) {
                return;
            }
            onScanAd(sdPath, paramInt, childFile, isDeepScanTemp, this,
                    (AdBean) object);
        }

        private void onScanAd(String sdPath, int paramInt, File file,
                              boolean isDeepScanTemp, ScanRunnable runnable, AdBean bean) {
            if (mCurFolder != null) {
                mCurFolder.setTitle(bean.getTitle());
            }
            if (file.isFile()) {
                if (file.length() > TEN_MB) {
                    BigFolderBean bigFolderBean = new BigFolderBean();
                    bigFolderBean.setTitle(file.getName());
                    bigFolderBean.setPath(file.getPath());
                    bigFolderBean.setIsFolder(false);
                    bigFolderBean.setSize(file.length());
                    bigFolderBean.setCheck(false);
                    mTempBigFolderList.add(bigFolderBean);
                }
                // 文件个数
                bean.setFileCount(bean.getFileCount() + 1);
                // 文件大小
                bean.setSize(bean.getSize() + file.length());
                mEventManager.sendCheckedFileSizeEvent(
                        CleanCheckedFileSizeEvent.AdSize, file.length());
                mEventManager.sendScanFileSizeEvent(
                        CleanScanFileSizeEvent.AdSize, file.length());
                onScanFile(file);
            } else if (file.isDirectory()) {
                // 文件夹个数，但广告文件夹根路径不统计
                if (paramInt != 0) {
                    bean.setFolderCount(bean.getFolderCount() + 1);
                }
                onScanFolder(sdPath, paramInt, file, isDeepScanTemp, runnable,
                        bean);
            }
        }
    };

    /**
     * 扫描SD卡其余路径
     */
    private ScanRunnable mCommonScanRunnable = new ScanRunnable() {
        @Override
        public void run(String sdPath, int paramInt, File childFile,
                        boolean isDeepScanTemp, Object object) {
            onScanCommonFile(sdPath, paramInt, childFile, isDeepScanTemp);
        }

        private void onScanCommonFile(String sdPath, int paramInt, File file,
                                      boolean isDeepScanTemp) {
            if (file.isFile()) {
                if (file.length() > TEN_MB) {
                    BigFolderBean bigFolderBean = new BigFolderBean();
                    bigFolderBean.setTitle(file.getName());
                    bigFolderBean.setPath(file.getPath());
                    bigFolderBean.setIsFolder(false);
                    bigFolderBean.setSize(file.length());
                    bigFolderBean.setCheck(false);
                    mTempBigFolderList.add(bigFolderBean);
                }
                onScanFile(file);
            } else if (file.isDirectory()) {
                onScanFolder(sdPath, paramInt, file, isDeepScanTemp, this, null);
            }
        }
    };

    /**
     * SD卡遍历扫描
     *
     * @param sdPath     SD卡路径
     * @param paramFile  扫描的文件
     * @param paramInt   当前扫描的深度
     * @param isDeepScan 是否采取深度扫描模式(该路径为卸载残留路径 or 应用缓存 or 广告)
     * @param runnable   具体业务
     * @param objectBean 业务参数对象
     * @return 是否全部扫描完成
     */
    private boolean scanFile(String sdPath, File paramFile, int paramInt,
                             boolean isDeepScan, ScanRunnable runnable, Object objectBean) {
        try {
            if (mIsSwitch || mIsStop) {
                return false;
            }
            if (paramInt >= 5 && !(isDeepScan/* || mIsNeedUploadData*/)) {
                // 不须上传数据（需要上传数据时全局扫描）
                // 默认扫5层(SD卡根路径为第1层)，但如果是深度扫描模式(该路径为卸载残留路径 or 应用缓存)，则破除限制
                return true;
            }
            File[] childFiles = paramFile.listFiles();
            if (childFiles == null) {
                return true;
            }

            if ((paramInt == 1 && !isDeepScan)
                    && !paramFile.getPath().equals(mLastScanPath)) {
                // 打印并上传超时的扫描时间和路径
                //logAndUploadScanTime();
                mLastScanPath = paramFile.getPath();
            }

            if (mCurFolder != null && !TextUtils.isEmpty(mCurFolder.getPath())
                    && !paramFile.getPath().startsWith(mCurFolder.getPath())) {
                // 若当前扫描的路径非上次扫描的子路径
                checkBigFolder(paramFile);
            }

            mCurFolder = new BigFolderBean();
            mCurFolder.setPath(paramFile.getPath());
            mCurFolder.setTitle(FileUtil.getName(paramFile.getPath()));
            mCurFolder.setSize(0);
            mCurFolder.setIsFolder(true);
            List<File> fileList = Arrays.asList(childFiles);
            Collections.sort(fileList, mFileListComparator);
            for (File childFile : fileList) {
                if (mIsStop || mIsRestart || mIsSwitch) {
                    return false;
                }
                if (paramInt == 0&& childFile.getPath().equals(Const.BOOST_DIR)) {
                    // 跳出项目存储的路径：Sdcard根目录扫描 && 扫描的目录为项目存储根路径
                    continue;
                }
                if (!isDeepScan
                        && mIgnorePaths.contains(paramFile.getPath()
                        .toLowerCase())) {
                    // 非深度模式下，即扫描其余路径时跳出已经扫描的路径
                    continue;
                }
                // 通知扫描路径
                mEventManager.sendScanPathEvent(CleanScanPathEvent.SDCard,getFilePathWithoutSDPath(sdPath, childFile.getPath()));
//                uploadPath(sdPath, childFile);

                if (onScanDCIM(childFile)) {
                    continue;
                }
                if (onScanLostFile(childFile)) {
                    continue;
                }

                runnable.run(sdPath, paramInt, childFile, isDeepScan,
                        objectBean);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * 扫描超时时间
     */
    private static final long SCAN_OVERTIME_L = 5000;
    private static final long SCAN_OVERTIME_H = 30000;

    /**
     * 打印并上传超时的扫描时间和路径
     */
//    @SuppressWarnings("deprecation")
//    private void logAndUploadScanTime() {
//        if (!PrivacyHelper.isJoinUepPlan()) {
//            return;
//        }
//        long cur = System.currentTimeMillis();
//        long spend = cur - mLastPathScanTime;
//        Logger.d(SCAN_TAG, "Path : " + mLastScanPath + " - " + spend);
//        if (!TextUtils.isEmpty(mLastScanPath) && spend > SCAN_OVERTIME_L && spend < SCAN_OVERTIME_H) {
//            boolean exist = mDataProvider.isCleanScanOvertimePathExist(mLastScanPath);
//            if (!exist) {
//                // 超时设置范围，避免特殊错误数据情况引起异常超时
//                StatisticsTools.uploadLocationAndRemark(StatisticsConstants.APP_SD_SCA, mLastScanPath, spend+ "");
//                mDataProvider.insertCleanScanOvertimePath(mLastScanPath);
//            }
//        }
//        mLastPathScanTime = cur;
//    }

    /**
     * 上传SD卡路径
     */
//    private void uploadPath(String sdPath, File childFile) {
//        if (mIsNeedUploadData) {
//            String uploadPath = childFile.getPath().replace(sdPath, "");
//            if (childFile.isDirectory()) {
//                uploadPath = uploadPath + File.separator;
//            }
//            mCollectManager.appendData(uploadPath.trim());
//        }
//    }

    /**
     * 扫描到相册缓存
     */
    private boolean onScanDCIM(File childFile) {
        if (!childFile.getPath().endsWith("/DCIM/.thumbnails")) {
            return false;
        }
        long fileSize = FileUtil.getFileSize(childFile);
        if (fileSize > TEN_MB) {
            // 需求:缩略图只有大于10M才在大文件类显示,不再其他类别显示
            FileBean info = new FileBean(GroupType.BIG_FILE);
            info.setPath(childFile.getPath());
            info.setSize(fileSize);
            info.setCheck(false);
            info.setDefaultCheck(info.isCheck());
            info.setFileName(mContext.getResources().getString(R.string.gallery_thumbnails));
            info.setFileFlag(FileFlag.GALLERY_THUMBNAILS);
            mBigSizeFiles.add(info);
            mEventManager.sendScanFileSizeEvent(CleanScanFileSizeEvent.BigFileSize, fileSize);
        }
        return true;
    }

    /**
     * 扫描到损坏文件
     */
    private boolean onScanLostFile(File childFile) {
        // LOST.DIR是专门收集Android系统运行时意外丢失的文件而设置的文件夹，
        // 其收集的内容包括系统因为意外而没能保存的各类内存 、交换、暂存等数据,软件的缓存数据、以及其他各类文件。
        if (!childFile.getPath().contains("LOST.DIR")) {
            return false;
        }
        File[] arrayFileLost = childFile.listFiles();
        if (arrayFileLost != null && arrayFileLost.length > 0) {
            for (File file : arrayFileLost) {
                if (file.exists()) {
                    FileBean info = new FileBean(GroupType.TEMP);
                    info.setPath(file.getPath());
                    info.setSize(file.length());
                    info.setFileName(file.getName());
                    info.setCheck(true);
                    info.setDefaultCheck(info.isCheck());
                    mTempFiles.add(info);
                    mEventManager
                            .sendScanFileSizeEvent(
                                    CleanScanFileSizeEvent.TempFileSize,
                                    info.getSize());
                    mEventManager.sendCheckedFileSizeEvent(
                            CleanCheckedFileSizeEvent.TempFileSize,
                            info.getSize());
                }
            }
        }
        return true;
    }

    private void onScanFile(File childFile) {
        if (childFile.getName().toLowerCase().endsWith(".log")
                || childFile.getName().toLowerCase().endsWith(".tmp")) {
            onScanLogOrTempFile(childFile);
        } else if (childFile.getName().toLowerCase().endsWith(".apk")) {
            onScanApkFile(childFile);
        } else if (childFile.length() >= TEN_MB) {
            onScanBigFile(childFile);
        }
        if (childFile.getPath().startsWith(mCurFolder.getPath())) {
            mCurFolder.setSize(mCurFolder.getSize() + childFile.length());
        }
    }

    /**
     * 扫描到Log文件或者临时文件
     */
    private void onScanLogOrTempFile(File childFile) {
        FileBean info = new FileBean(GroupType.TEMP);
        info.setPath(childFile.getPath());
        info.setSize(childFile.length());
        info.setFileName(childFile.getName());
        info.setCheck(true);
        info.setDefaultCheck(info.isCheck());
        mTempFiles.add(info);
        mEventManager.sendScanFileSizeEvent(
                CleanScanFileSizeEvent.TempFileSize, childFile.length());
        mEventManager.sendCheckedFileSizeEvent(
                CleanCheckedFileSizeEvent.TempFileSize, childFile.length());
    }

    /**
     * 扫描到APK文件
     */
    private void onScanApkFile(File childFile) {
        FileBean bean = new FileBean(GroupType.APK);
        bean.setPath(childFile.getPath());
        bean.setSize(childFile.length());
        bean.setFileName(childFile.getName());
        ApkFile apkFile = getPackageByApkFile(childFile.getPath());
        if (apkFile != null && !TextUtils.isEmpty(apkFile.getPackage())) {
            boolean backUp = isBackUpApk(apkFile.getPackage());
            bean.setIsBackup(backUp);
            bean.setVersionName(apkFile.getVersionName());
            bean.setVersionCode(apkFile.getVersionCode());
            if (!backUp) {
                // 备份的APK不添加到数据列表
                bean.setCheck(compareToInstall(bean.getVersionCode(),
                        apkFile.getPackage()));
                bean.setDefaultCheck(bean.isCheck());
                mApkFiles.add(bean);
                mEventManager.sendScanFileSizeEvent(
                        CleanScanFileSizeEvent.APKFileSize, childFile.length());
                mEventManager.sendCheckedFileSizeEvent(
                        CleanCheckedFileSizeEvent.APKFileSize,
                        bean.isCheck() ? childFile.length() : 0);
            }
        }
    }

    /**
     * 封装获取apk文件的包名和版本名
     *
     * @author wangying <br/>
     *         create at 2015-1-28 上午10:16:50
     */
    class ApkFile {
        private String mPackageString;
        private String mVersionNameString;
        private int mVersionCode;

        public int getVersionCode() {
            return mVersionCode;
        }

        public void setVersionCode(int versionCode) {
            mVersionCode = versionCode;
        }

        public String getPackage() {
            return mPackageString;
        }

        public void setPackage(String packageString) {
            mPackageString = packageString;
        }

        public String getVersionName() {
            return mVersionNameString;
        }

        public void setVersionName(String versionNameString) {
            mVersionNameString = versionNameString;
        }
    }

    /**
     * 由Apk文件路径获得本apk包的信息
     */
    private ApkFile getPackageByApkFile(String path) {
        ApkFile apkFile = new ApkFile();
        PackageManager pm = mContext.getPackageManager();
        PackageInfo packageInfo = pm.getPackageArchiveInfo(path,
                PackageManager.GET_ACTIVITIES);

        if (packageInfo != null) {
            ApplicationInfo applicationInfo = packageInfo.applicationInfo;
            apkFile.setPackage(applicationInfo.packageName);
            apkFile.setVersionName(packageInfo.versionName);
            apkFile.setVersionCode(packageInfo.versionCode);
            return apkFile;
        }
        return null;
    }

    /**
     * 与备份记录比较是否已经备份,已备份返回true，否则返回false
     */
    private boolean isBackUpApk(String packageName) {
        if (!new File(Const.BACK_UP_PATH).exists()) {
            return false;
        }
        ArrayList<String> arrayListPackageName = (ArrayList<String>) FileUtil
                .listFile(Const.BACK_UP_PATH, ".apk", true);
        return arrayListPackageName.contains(packageName);
    }

    /**
     * 比较是否安装<br>
     * 未安装：不勾选<br>
     * 安装：若版本号比安装的应用版本号高，则不勾选
     */
    private boolean compareToInstall(int versionCode, String packageName) {
        PackageInfo info;
        try {
            info = mContext.getPackageManager().getPackageInfo(packageName, 0);
            return versionCode <= info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    /**
     * 扫描到大文件
     */
    private void onScanBigFile(File childFile) {
        FileBean info = new FileBean(GroupType.BIG_FILE);
        info.setPath(childFile.getPath());
        info.setSize(childFile.length());
        info.setCheck(false);
        info.setDefaultCheck(info.isCheck());
        info.setFileName(childFile.getName());
        mBigSizeFiles.add(info);
        mEventManager.sendScanFileSizeEvent(CleanScanFileSizeEvent.BigFileSize,
                childFile.length());
    }

    /**
     * 判断当前文件夹是否符合大文件夹的逻辑
     */
    private void checkBigFolder(File folder) {
        long size = 0;
        if (folder != null && mCurFolder.getSize() > TEN_MB
                && !folder.getPath().startsWith(mCurFolder.getPath())
                && !mCurFolder.getPath().toLowerCase().contains("dcim")) {
            // 上次扫描文件夹大于10 MB && 当前扫描的文件夹不是上次扫描文件夹的子文件夹 && 路径不包含DCIM的目录
            mBigFolderList.add(mCurFolder);
            size = mCurFolder.getSize();
        } else {
            mBigFolderList.addAll(mTempBigFolderList);
            for (BigFolderBean bigFolderBean : mTempBigFolderList) {
                size += bigFolderBean.getSize();
            }
        }
        mEventManager.sendScanFileSizeEvent(
                CleanScanFileSizeEvent.BigFolderSize, size);
        mTempBigFolderList.clear();
    }

    /**
     * 扫描到文件夹
     *
     * @param sdPath     SD卡路径
     * @param paramInt   当前扫描的深度
     * @param runnable   具体业务
     * @param objectBean 业务参数对象
     */
    private void onScanFolder(String sdPath, int paramInt, File childFile,
                              boolean isDeepScanTemp, ScanRunnable runnable, Object objectBean) {
        checkBigFolder(childFile);
        try {
            File[] arrayFileNull = childFile.listFiles();
            mCurFolder = new BigFolderBean();
            mCurFolder.setPath(childFile.getPath());
            mCurFolder.setTitle(FileUtil.getName(childFile.getPath()));
            mCurFolder.setSize(0);
            mCurFolder.setIsFolder(true);
            if (arrayFileNull != null && arrayFileNull.length == 0) {
                onScanEmptyFolder(childFile);
            } else {
                int count = paramInt + 1;
                scanFile(sdPath, childFile, count, isDeepScanTemp, runnable,
                        objectBean);
            }
        } catch (StackOverflowError e) {
            Logger.e(TAG, "StackOverflowError : " + childFile.getPath());
        }
    }

    /**
     * 扫描到空文件夹
     */
    private void onScanEmptyFolder(File childFile) {
        FileBean info = new FileBean(GroupType.TEMP);
        info.setPath(childFile.getPath());
        info.setSize(0);
        info.setFileName(childFile.getName());
        info.setCheck(true);
        info.setDefaultCheck(info.isCheck());
        mTempFiles.add(info);
        mEventManager.sendScanFileSizeEvent(
                CleanScanFileSizeEvent.TempFileSize, 0);
        mEventManager.sendCheckedFileSizeEvent(
                CleanCheckedFileSizeEvent.TempFileSize, 0);
    }

    private String getFilePathWithoutSDPath(String sdPath, String filePath) {
        return filePath.replace(sdPath + File.separator, "");
    }


    //***************************************************************** 内存相关 ***********************************************************//

    /**
     * 扫描内存
     */
    public void scanMemory() {
        // 扫描内存
        mMemoryList.clear();
        CleanScanFileSizeEvent.MemoryFileSize.setSize(0);
        CleanCheckedFileSizeEvent.MemoryFileSize.setSize(0);
        mMemoryManager.scanMemory(new MemoryTrashManager.OnScanMemoryListener() {
            @Override
            public void onScanMemoryDone() {
                // 扫描完毕
                mMemoryList = mMemoryManager.getMemoryList();
                Logger.d(TAG, "运行内存:" + mMemoryManager.getRunningMemory() + " -  选中内存: "
                        + mMemoryManager.getSelectedMemory());
                mEventManager.sendScanFileSizeEvent(
                        CleanScanFileSizeEvent.MemoryFileSize,
                        mMemoryManager.getRunningMemory());
                mEventManager.sendCheckedFileSizeEvent(
                        CleanCheckedFileSizeEvent.MemoryFileSize,
                        mMemoryManager.isSelected() ? mMemoryManager
                                .getSelectedMemory() : 0);
            }
        });
    }

    /**
     * 获取内存选中状态
     */
    public GroupSelectBox.SelectState getMemoryGroupSelectState() {
        return mMemoryManager.getSelectState();
    }

    /**
     * 清理内存
     */
    public void cleanAllMemoryItems(ArrayList<MemoryBean> memoryList) {
        mMemoryManager.cleanAllMemoryItems(memoryList);
    }

    /*public void cleanAllBatteryBoostItems(List<BatteryBoostAppBean> list) {
        mMemoryManager.cleanAllBatteryBoostItems(list);
    }*/

    //************************************************* 白名单相关 ***********************************************************//
    // 添加
    public void addCacheAppIgnore(AppCacheBean bean) {
        mDataProvider.removeCachePathByPackageName(bean.getPackageName());
        CleanIgnoreCacheAppBean ignoreCacheAppBean = new CleanIgnoreCacheAppBean(bean);
        mDataProvider.addCacheApp(ignoreCacheAppBean);
        //uploadIgnoreData(ignoreCacheAppBean.getType(), 1, bean.getPackageName());
        if (!bean.isNoneSelected()) {
            long checkedSize = 0;
            for (SubItemBean subBean : bean.getSubItemList()) {
                checkedSize += subBean.isChecked() ? subBean.getSize() : 0;
            }
            mEventManager.sendCheckedFileSizeEvent(
                    CleanCheckedFileSizeEvent.get(GroupType.APP_CACHE), -checkedSize);
        }
        mCacheList.remove(bean);
        mEventManager.sendScanFileSizeEvent(
                CleanScanFileSizeEvent.get(GroupType.APP_CACHE), -bean.getSize());
    }

    public void addCachePathIgnore(AppCacheBean bean, SubAppCacheBean subBean) {
        CleanIgnoreCachePathBean ignoreCachePathBean = new CleanIgnoreCachePathBean(subBean);
        ignoreCachePathBean.setSubTitle(AppUtils.getAppName(mContext, subBean.getPackageName()));
        mDataProvider.addCachePath(ignoreCachePathBean);
        //uploadIgnoreData(ignoreCachePathBean.getType(), 1, bean.getPackageName() + "#" + bean.getPath());
        bean.removeSubItem(subBean);
        bean.setSize(bean.getSize() - subBean.getSize());
        if (bean.getSubItemList().isEmpty()) {
            mCacheList.remove(bean);
        }
        if (subBean.isChecked()) {
            mEventManager.sendCheckedFileSizeEvent(
                    CleanCheckedFileSizeEvent.get(GroupType.APP_CACHE), -subBean.getSize());
        }
        mEventManager.sendScanFileSizeEvent(
                CleanScanFileSizeEvent.get(GroupType.APP_CACHE), -subBean.getSize());
    }

    public void addResidueIgnore(ResidueBean bean) {
        CleanIgnoreResidueBean ignoreResidueBean = new CleanIgnoreResidueBean(bean);
        mDataProvider.addResidue(ignoreResidueBean);
       // uploadIgnoreData(ignoreResidueBean.getType(), 1, bean.getPackageName());
        mResidueList.remove(bean);
        if (bean.isAllSelected()) {
            mEventManager.sendCheckedFileSizeEvent(
                    CleanCheckedFileSizeEvent.get(GroupType.RESIDUE), -bean.getSize());
        }
        mEventManager.sendScanFileSizeEvent(
                CleanScanFileSizeEvent.get(GroupType.RESIDUE), -bean.getSize());
    }

    public void addAdIgnore(AdBean bean) {
        CleanIgnoreAdBean ignoreAdBean = new CleanIgnoreAdBean(bean);
        mDataProvider.addAd(ignoreAdBean);
        //uploadIgnoreData(ignoreAdBean.getType(), 1, bean.getPath());
        mAdList.remove(bean);
        if (bean.isAllSelected()) {
            mEventManager.sendCheckedFileSizeEvent(
                    CleanCheckedFileSizeEvent.get(GroupType.AD), -bean.getSize());
        }
        mEventManager.sendScanFileSizeEvent(
                CleanScanFileSizeEvent.get(GroupType.AD), -bean.getSize());
    }

    //查询
    public List<CleanIgnoreBean> queryCacheAppIgnore() {
        return mDataProvider.queryCacheApp();
    }

    public List<CleanIgnoreBean> queryCachePathIgnore() {
        return mDataProvider.queryCachePath();
    }

    public List<CleanIgnoreBean> queryResidueIgnore() {
        return mDataProvider.queryResidue();
    }

    public List<CleanIgnoreBean> queryAdIgnore() {
        return mDataProvider.queryAd();
    }

    // 删除
    public void removeCacheAppIgnore(CleanIgnoreCacheAppBean bean) {
        mDataProvider.removeCacheApp(bean);
       // uploadIgnoreData(bean.getType(), 2, bean.getPackageName());
    }

    public void removeCachePathIgnore(CleanIgnoreCachePathBean bean) {
        mDataProvider.removeCachePath(bean);
        //uploadIgnoreData(bean.getType(), 2, bean.getPackageName() + "#" + bean.getPath());
    }

    public void removeResidueIgnore(CleanIgnoreResidueBean bean) {
        mDataProvider.removeResidue(bean);
        //uploadIgnoreData(bean.getType(), 2, bean.getPackageName());
    }

    public void removeAdIgnore(CleanIgnoreAdBean bean) {
        mDataProvider.removeAd(bean);
        ///uploadIgnoreData(bean.getType(), 2, bean.getPath());
    }

//    private void uploadIgnoreData(int type, int operate, String detail) {
//        if (!PrivacyHelper.isJoinUepPlan()) {
//            return;
//        }
//        Statistics101Bean bean = Statistics101Bean.builder();
//        bean.mOpeateId = StatisticsConstants.CLEAN_IG_ADD_REM;
//        bean.mEntrance = operate + "";
//        bean.mRemark = type + "";
//        // 残留只对应1个包名；缓存路径：应用#路径
//        bean.mTab = detail;
//        StatisticsTools.upload101InfoNew(bean);
//    }

    /**
     * 重置选中状态(根据选中规则) & 展开状态
     */
    public void resetStateToDefault() {
        appCacheInsertSysCache();
        CleanCheckedFileSizeEvent.cleanAllSizeData();
        resetState(mCacheList, CleanCheckedFileSizeEvent.CacheSize);
        resetState(mResidueList, CleanCheckedFileSizeEvent.ResidueFileSize);
        resetState(mTempFiles, CleanCheckedFileSizeEvent.TempFileSize);
        resetState(mApkFiles, CleanCheckedFileSizeEvent.APKFileSize);
        resetState(mBigFolderList, CleanCheckedFileSizeEvent.BigFolderSize);
        resetState(mBigSizeFiles, CleanCheckedFileSizeEvent.BigFileSize);
        resetState(mAdList, CleanCheckedFileSizeEvent.AdSize);
        resetState(mMemoryList, CleanCheckedFileSizeEvent.MemoryFileSize);
    }


    /**
     * 重置状态：选中 & 展开状态
     */
    private void resetState(List<? extends ItemBean> list,
                            CleanCheckedFileSizeEvent event) {
        for (ItemBean item : list) {
            // 全部都收缩
            item.setIsExpand(false);
            ArrayList<SubItemBean> subItemList = item.getSubItemList();
            if (subItemList.isEmpty()) {
                boolean defaultCheck = item.isDefaultCheck();
                item.setCheck(defaultCheck);
                // 更新选中文件的大小
                if (defaultCheck) {
                    event.addSize(item.getSize());
                }
            } else {
                // 针对3级列表进行重置处理
                int size = 0;
                for (SubItemBean subItem : subItemList) {
                    boolean check = subItem.isDefaultCheck();
                    subItem.setChecked(check);
                    size += check ? subItem.getSize() : 0;
                }
                item.updateState();
                event.addSize(size);
            }
        }
    }
}