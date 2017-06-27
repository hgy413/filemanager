package com.jb.filemanager.function.scanframe.clean;

import android.content.Context;

import com.jb.filemanager.commomview.GroupSelectBox;
import com.jb.filemanager.function.scanframe.bean.adbean.AdBean;
import com.jb.filemanager.function.scanframe.bean.appBean.AppItemInfo;
import com.jb.filemanager.function.scanframe.bean.bigfolder.BigFolderBean;
import com.jb.filemanager.function.scanframe.bean.cachebean.AppCacheBean;
import com.jb.filemanager.function.scanframe.bean.cachebean.CacheBean;
import com.jb.filemanager.function.scanframe.bean.cachebean.subitem.SubAppCacheBean;
import com.jb.filemanager.function.scanframe.bean.common.itemcommon.ItemBean;
import com.jb.filemanager.function.scanframe.bean.filebean.FileBean;
import com.jb.filemanager.function.scanframe.bean.memorytrashbean.MemoryBean;
import com.jb.filemanager.function.scanframe.bean.residuebean.ResidueBean;
import com.jb.filemanager.function.scanframe.clean.ignore.CleanIgnoreAdBean;
import com.jb.filemanager.function.scanframe.clean.ignore.CleanIgnoreBean;
import com.jb.filemanager.function.scanframe.clean.ignore.CleanIgnoreCacheAppBean;
import com.jb.filemanager.function.scanframe.clean.ignore.CleanIgnoreCachePathBean;
import com.jb.filemanager.function.scanframe.clean.ignore.CleanIgnoreResidueBean;

import java.util.ArrayList;
import java.util.List;

/**
 * 文件扫描管理器,用于统计垃圾文件
 *
 * @author chenbenbin
 */

public class CleanManager {
    private static CleanManager sInstance;
    private CleanScanTaskManager mCleanScanTaskManager;
    private CleanJunkFileScanTask mJunkFileScanTask;
    //private CleanDeepCacheScanTask mDeepCacheScanTask;
    private long mLastTrashCleanTime;
    private long mLastCoolCpuTime;
    private long mLastBatteryBoost;

    public void setLastBatteryBoost(long time) {
        mLastBatteryBoost = time;
    }

    public long getLastBatteryBoost() {
        return mLastBatteryBoost;
    }

    public long getLastTrashCleanTime() {
        return mLastTrashCleanTime;
    }

    public void setLastTrashCleanTime(long lastTrashCleanTime) {
        mLastTrashCleanTime = lastTrashCleanTime;
    }

    public long getLastCoolCpuTime() {
        return mLastCoolCpuTime;
    }

    public void setLastCoolCpuTime(long lastCoolCpuTime) {
        mLastCoolCpuTime = lastCoolCpuTime;
    }
    public synchronized static CleanManager getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new CleanManager(context);
        }
        return sInstance;
    }

    private CleanManager(Context context) {
        Context applicationContext = context.getApplicationContext();
        //AppPathDetector.getInstance(applicationContext);
        mCleanScanTaskManager = CleanScanTaskManager.getInstance(applicationContext);
        mJunkFileScanTask = mCleanScanTaskManager.getJunkFileScanTask();
        //mDeepCacheScanTask = mCleanScanTaskManager.getDeepCacheScanTask();
    }

    //*******************************************************  获取数据队列接 **************************************************************//

//    public List<CleanDeepBean> getDeepCacheList() {
//        return new ArrayList<CleanDeepBean>();
//    }

    public List<CacheBean> getCacheList() {
        return mJunkFileScanTask.getCacheList();
    }

    public List<ResidueBean> getResidueList() {
        return mJunkFileScanTask.getResidueList();
    }

    public List<AdBean> getAdList() {
        return mJunkFileScanTask.getAdList();
    }

    public List<FileBean> getTempArrayList() {
        return mJunkFileScanTask.getTempFilesList();
    }

    public List<FileBean> getApkArrayList() {
        return mJunkFileScanTask.getApkFilesList();
    }

    public List<BigFolderBean> getBigFolderList() {
        return mJunkFileScanTask.getBigFolderList();
    }

    public List<FileBean> getBigSizeArrayList() {
        return mJunkFileScanTask.getBigSizeFilesList();
    }

    public List<MemoryBean> getMemoryArrayList() {
        return mJunkFileScanTask.getMemoryList();
    }

//    public WhatsAppScanTask.WhatsAppData getWhatsAppData() {
//        return mDeepCacheScanTask.getWhatsAppScanTask().getData();
//    }

//    public FacebookScanTask.FacebookData getFacebookData() {
//        return mDeepCacheScanTask.getFacebookScanTask().getData();
//    }

    //******************************************************** 删除文件后更新内存数据 ***********************************************************//

    public void updateDeepCacheList(List<ItemBean> delList) {

    }

    public void updateAppCacheList(List<ItemBean> delList) {
        mJunkFileScanTask.updateAppCacheList(delList);
    }

    public void updateResidueList(List<ItemBean> delList) {
        mJunkFileScanTask.updateResidueList(delList);
    }

    public void updateAdList(List<ItemBean> delList) {
        mJunkFileScanTask.updateAdList(delList);
    }

    public void updateTempList(List<ItemBean> delList) {
        mJunkFileScanTask.updateTempList(delList);
    }

    public void updateApkList(List<ItemBean> delList) {
        mJunkFileScanTask.updateApkList(delList);
    }

    public void updateBigFolderList(List<ItemBean> delList) {
        mJunkFileScanTask.updateBigFolderList(delList);
    }

    public void updateBigFileList(List<ItemBean> delList) {
        mJunkFileScanTask.updateBigFileList(delList);
    }

    public void updateMemoryList(List<ItemBean> delList) {
        mJunkFileScanTask.updateMemoryList(delList);
    }

    //******************************************************** 删除垃圾大小的接口 ***********************************************************//

    /**
     * 清理的垃圾大小(B)
     */
    private long mCleanSize = 0;

    /**
     * 获取清理的垃圾大小(B)
     */
    public long getCleanSize() {
        return mCleanSize;
    }

    /**
     * 设置清理的垃圾大小(B)
     */
    public void setCleanSize(long cleanSize) {
        mCleanSize = cleanSize;
    }

    //************************************************************ 扫描接口 ***********************************************************//

    /**
     * 停止扫描
     */
    public void stopScan() {
        mCleanScanTaskManager.stopAllTask();
    }

    /**
     * 退出应用
     */
    public void onAppExit() {
        mCleanScanTaskManager.onAppExit();
    }

    /**
     * 是否正在扫描
     */
    public boolean isScanning() {
        return mJunkFileScanTask.isRunning();
    }

    /**
     * 获取扫描进度
     */
    public float getScanProgress() {
        return mJunkFileScanTask.getScanProgress();
    }

    /**
     * 进入应用
     */
    public void onAppEnter() {
        //mDeepCacheScanTask.resetTaskData();
        mCleanScanTaskManager.initDefaultTaskList();
    }

    /**
     * 启动默认扫描任务
     */
    public void startDefaultTask() {
        mCleanScanTaskManager.startDefaultTaskList();
    }

    /**
     * 开始JunkFile扫描
     */
    public void startJunkFileScanTask() {
        mCleanScanTaskManager.startJunkFileScanTask();
    }

    /**
     * 开始深度缓存扫描
     */
//    public void startDeepCacheScanTask() {
//        mCleanScanTaskManager.startDeepCacheScanTask();
//    }

    /**
     * 开始扫描WhatsApp
     */
//    public void startWhatAppScanTask() {
//        mCleanScanTaskManager.startWhatAppScanTask();
//    }

    /**
     * 开始Facebook扫描
     */
//    public void startFacebookScanTask() {
//        mCleanScanTaskManager.startFacebookScanTask();
//    }

    /**
     * 重置选中状态(根据选中规则) & 展开状态
     */
    public void resetStateToDefault() {
        mJunkFileScanTask.resetStateToDefault();
    }

    /**
     * 查询单个应用的系统缓存数据
     */
    public void querySysCache(AppItemInfo info) {
        mJunkFileScanTask.querySysCache(info);
    }

    /**
     * 清理所有的系统缓存数据
     */
    public void cleanSysCache() {
        mJunkFileScanTask.cleanSysCache();
    }

    /**
     * 开始删除
     */
    public void startDelete() {
        mJunkFileScanTask.startDelete();
    }

    //***************************************************************** 内存相关 ***********************************************************//

    /**
     * 扫描内存
     */
    public void scanMemory() {
        mJunkFileScanTask.scanMemory();
    }

    /**
     * 获取内存选中状态
     */
    public GroupSelectBox.SelectState getMemoryGroupSelectState() {
        return mJunkFileScanTask.getMemoryGroupSelectState();
    }

    /**
     * 清理内存
     */
    public void cleanAllMemoryItems(ArrayList<MemoryBean> memoryList) {
        mJunkFileScanTask.cleanAllMemoryItems(memoryList);
    }

    /*public void cleanAllBatteryBoost(List<BatteryBoostAppBean> list) {
        mJunkFileScanTask.cleanAllBatteryBoostItems(list);
    }*/
    //***************************************************************** 白名单相关 ***********************************************************//
    // 添加
    public void addCacheAppIgnore(AppCacheBean bean) {
        mJunkFileScanTask.addCacheAppIgnore(bean);
    }

    public void addCachePathIgnore(AppCacheBean bean, SubAppCacheBean subBean) {
        mJunkFileScanTask.addCachePathIgnore(bean, subBean);
    }

    public void addResidueIgnore(ResidueBean bean) {
        mJunkFileScanTask.addResidueIgnore(bean);
    }

    public void addAdIgnore(AdBean bean) {
        mJunkFileScanTask.addAdIgnore(bean);
    }

    // 查询
    public List<CleanIgnoreBean> queryCacheAppIgnore() {
        return mJunkFileScanTask.queryCacheAppIgnore();
    }

    public List<CleanIgnoreBean> queryCachePathIgnore() {
        return mJunkFileScanTask.queryCachePathIgnore();
    }

    public List<CleanIgnoreBean> queryResidueIgnore() {
        return mJunkFileScanTask.queryResidueIgnore();
    }

    public List<CleanIgnoreBean> queryAdIgnore() {
        return mJunkFileScanTask.queryAdIgnore();
    }

    // 删除
    public void removeCacheAppIgnore(CleanIgnoreCacheAppBean bean) {
        mJunkFileScanTask.removeCacheAppIgnore(bean);
    }

    public void removeCachePathIgnore(CleanIgnoreCachePathBean bean) {
        mJunkFileScanTask.removeCachePathIgnore(bean);
    }

    public void removeResidueIgnore(CleanIgnoreResidueBean bean) {
        mJunkFileScanTask.removeResidueIgnore(bean);
    }

    public void removeAdIgnore(CleanIgnoreAdBean bean) {
        mJunkFileScanTask.removeAdIgnore(bean);
    }
}