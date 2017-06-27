package com.jb.filemanager.function.scanframe.manager;

import android.content.Context;
import android.content.pm.ApplicationInfo;

import com.jb.filemanager.TheApplication;
import com.jb.filemanager.commomview.GroupSelectBox;
import com.jb.filemanager.function.scanframe.bean.appBean.RunningAppBean;
import com.jb.filemanager.function.scanframe.bean.common.itemcommon.ItemBean;
import com.jb.filemanager.function.scanframe.bean.memorytrashbean.MemoryBean;
import com.jb.filemanager.function.scanframe.clean.CleanEventManager;
import com.jb.filemanager.manager.EssentialProcessFilter;
import com.jb.filemanager.manager.PackageManagerLocker;
import com.jb.filemanager.manager.ProcessManager;
import com.jb.filemanager.os.ZAsyncTask;
import com.jb.filemanager.util.AppUtils;
import com.jb.filemanager.util.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by xiaoyu on 2016/10/21.<br>
 * 内存垃圾管理扫描<br>
 */

public class MemoryTrashManager {

    private volatile boolean mIsScaning = false;
    private volatile boolean mIsScanDone = false;
    /**
     * Context
     */
    private Context mContext = null;
    /**
     * 总内存
     */
    private long mRunningMemory = 0L;
    /**
     * 选中的内存
     */
    private long mSelectedMemory = 0L;
    /**
     * 扫描结果集合
     */
    private ArrayList<MemoryBean> mMemoryList = null;


    private static MemoryTrashManager sMemoryTrashManager = null;

    public static MemoryTrashManager getInstance() {
        if (sMemoryTrashManager == null) {
            sMemoryTrashManager = new MemoryTrashManager();
        }
        return sMemoryTrashManager;
    }

    private MemoryTrashManager() {
        // TODO: 2016/10/21 do something required
        mContext = TheApplication.getAppContext();
        mMemoryList = new ArrayList<>();
    }

    /**
     * 查询正在运行的app的入口
     *
     * @return
     */
    public void getRunningAppInfo(Context context) {
        mMemoryList.clear();
        mIsScaning = true;
        getRunningAppPssSize(context);
    }

    private void getRunningAppPssSize(Context context) {
        ArrayList<MemoryBean> memoryList = new ArrayList<>();
        List<RunningAppBean> runningApps = AppUtils.getRunningApps(context);
        for (RunningAppBean process : runningApps) {
            if (!mIsScaning) {
                return;
            }
            RunningAppBean runningAppBean = new RunningAppBean();
            String packageName = process.mPackageName;
            if (EssentialProcessFilter.isEssentialProcess(packageName)
                    || EssentialProcessFilter.isEssentialProcessMock(packageName, false)) {
                continue;
            }
            runningAppBean.mPackageName = packageName;
            ApplicationInfo applicationInfo = PackageManagerLocker.getInstance().getApplicationInfo(packageName, 0);
            if (applicationInfo != null) {
                CharSequence charSequence = PackageManagerLocker.getInstance().getApplicationLabel(applicationInfo);
                if (charSequence != null) {
                    runningAppBean.mAppName = charSequence.toString();
                }
            }
            runningAppBean.mMemory = process.mMemory;
            MemoryBean bean = new MemoryBean(runningAppBean);
            memoryList.add(bean);
        }
        for (MemoryBean bean : memoryList) {
            if (!mIsScaning) {
                return;
            }
            String packageName = bean.getRunningAppBean().mPackageName;
            if (packageName != null) {
                boolean isExist = false;
                for (MemoryBean lastBean : mMemoryList) {
                    String lastPackageName = lastBean.getRunningAppBean().mPackageName;
                    if (lastPackageName != null && lastPackageName.equals(packageName)) {
                        isExist = true;
                        lastBean.getRunningAppBean().mMemory += bean.getRunningAppBean().mMemory;
                    }
                }
                if (!isExist) {
                    mMemoryList.add(bean);
                }
            }
        }
        Collections.sort(mMemoryList, new Comparator<MemoryBean>() {
            @Override
            public int compare(MemoryBean l, MemoryBean r) {
                return (int) (r.getRunningAppBean().mMemory - l.getRunningAppBean().mMemory);
            }
        });
    }

    /*--***********************************new **********************************************/

    /**
     * 获取总内存
     */
    public long getRunningMemory() {
        return mRunningMemory;
    }

    /**
     * 获取选中的内存
     */
    public long getSelectedMemory() {
        return mSelectedMemory;
    }

    /**
     * 是否有选中
     */
    public boolean isSelected() {
       /* if (mInitSelectState == SelectState.NONE_SELECTED) {
            return false;
        }*/
        return true;
    }

    /**
     * 获取选中状态
     */
    public GroupSelectBox.SelectState getSelectState() {
        //return mInitSelectState;
        return GroupSelectBox.SelectState.ALL_SELECTED;
    }

    /**
     * 获取内存列表
     */
    public ArrayList<MemoryBean> getMemoryList() {
        return mMemoryList;
    }

    /**
     * 扫描内存
     */
    public void scanMemory(final OnScanMemoryListener listener) {
        Logger.i("CleanManager", "MemoryManager is scanning = " + mIsScanDone);
        if (mIsScaning) {
            // 不需要扫描
            return;
        }
        final ProcessManager processManager = ProcessManager.getInstance(mContext);
        new ZAsyncTask<Context, Void, List<RunningAppBean>>() {

            @Override
            protected void onPreExecute() {
                Logger.i("CleanManager", "id=" + this.toString());
                mMemoryList.clear();
                //mInitMemoryBeans.clear();
                mRunningMemory = 0L;
                mSelectedMemory = 0L;
                mIsScanDone = false;
                mIsScaning = true;
            }

            @Override
            protected List<RunningAppBean> doInBackground(Context... params) {
                Logger.i("CleanManager", "scanMemory onPostExecute");
                final Context context = params[0];
                return ProcessManager.checkHasRunningServices(context,
                        processManager.getRunningAppList());
            }

            @Override
            protected void onPostExecute(List<RunningAppBean> result) {
                Logger.i("CleanManager", "scanMemory onPostExecute");
                if (result != null) {
                    //int selectCount = 0;
                    for (int i = 0; i < result.size(); i++) {
                        RunningAppBean modle = result.get(i);
                        MemoryBean cleanMemoryBean = new MemoryBean(modle);
                        if (!modle.mIsIgnore) {
                            cleanMemoryBean.setCheck(true);
                            cleanMemoryBean.setDefaultCheck(true);
                            //selectCount++;
                            mSelectedMemory += modle.mMemory * 1024;
                            //mInitMemoryBeans.add(cleanMemoryBean);
                        } else {
                            cleanMemoryBean.setDefaultCheck(false);
                        }
                        mMemoryList.add(cleanMemoryBean);
                        mRunningMemory += modle.mMemory * 1024;
                    }
                    /*if (selectCount == 0) {
                        mInitSelectState = SelectState.NONE_SELECTED;
                    } else if (selectCount == result.size()) {
                        mInitSelectState = SelectState.ALL_SELECTED;
                    } else {
                        mInitSelectState = SelectState.MULT_SELECTED;
                    }*/
                    // 排序，未勾选的放后面
                    if (!mMemoryList.isEmpty()) {
                        Collections.sort(mMemoryList, mMemoryComparator);
                    }
                }
                mIsScanDone = true;
                CleanEventManager.getInstance().sendAppMemoryScanDoneEvent();
                if (listener != null) {
                    listener.onScanMemoryDone();
                }
                mIsScaning = false;
            }
        }.execute(mContext);
    }

    /**
     * 扫描监听
     */
    public interface OnScanMemoryListener {
        public void onScanMemoryDone();
    }

    private Comparator<MemoryBean> mMemoryComparator = new Comparator<MemoryBean>() {
        @Override
        public int compare(MemoryBean l, MemoryBean r) {
            if (l.getRunningAppBean().mIsIgnore && !r.getRunningAppBean().mIsIgnore) {
                return 1;
            } else if (!l.getRunningAppBean().mIsIgnore && r.getRunningAppBean().mIsIgnore) {
                return -1;
            } else {
                // 按照占用内存大->小排序
                return (int) (r.getRunningAppBean().mMemory - l.getRunningAppBean().mMemory);
            }
        }
    };

    // --------------------------------清理-------------------------

    /**
     * 批量删除内存
     */
    public void cleanAllMemoryItems(List<MemoryBean> memoryList) {
        //final RootManager rootManager = LauncherModel.getInstance().getRootManager();
        final ProcessManager processManager = ProcessManager.getInstance(mContext);
        //final BoostManager boostManager = BoostManager.getInstance();
        //final BoostProtectManger boostProtectManger = BoostProtectManger.getInstance();
        // 已root的情况,要么使用Root授权模式, 要么使用普通模式.
        /*if (rootManager.isRootAvailable() && rootManager.isGrantedRoot()) {
            // 加速后的数据保护
            boostProtectManger.onBeforeMemoryBoost(processManager.getAvaliableMemory(false));
            // 内存查杀杀掉的内存
            long boostedRamSize = 0l;
            if (memoryList != null && !memoryList.isEmpty()) {
                for (int i = 0; i < memoryList.size(); i++) {
                    ItemBean bean = memoryList.get(i);
                    if (bean != null && bean instanceof MemoryBean) {
                        MemoryBean cmBean = (MemoryBean) bean;
                        String packageName = cmBean.getRunningAppBean().mPackageName;
                        if (boostManager.isNeedRootBoostApp(cmBean.getRunningAppBean())) {
                            processManager.forceKillAppByPackageName(packageName);
                        } else {
                            processManager.killAppByPackageName(packageName);
                        }
                        EssentialProcessFilter.addKilledProcess(packageName);
                        boostedRamSize += cmBean.getRunningAppBean().mMemory;
                    }
                }
            }
            // 加速后的数据保护
            boostProtectManger.onMemoryBoost(boostedRamSize);
            boostProtectManger.onMemoryBoostForCard();
        } else {}*/
        if (memoryList != null && !memoryList.isEmpty()) {
            for (int i = 0; i < memoryList.size(); i++) {
                ItemBean bean = memoryList.get(i);
                if (bean != null && bean instanceof MemoryBean) {
                    String packageName = ((MemoryBean) bean).getRunningAppBean().mPackageName;
                    ProcessManager.getInstance(mContext).killAppByPackageName(packageName);
                    EssentialProcessFilter.addKilledProcess(packageName);
                }
            }
        }
    }

   /* *//**
     * 批量删除内存
     *//*
    public void cleanAllBatteryBoostItems(List<BatteryBoostAppBean> memoryList) {
        //final RootManager rootManager = LauncherModel.getInstance().getRootManager();
        final ProcessManager processManager = ProcessManager.getInstance(mContext);
        //final BoostManager boostManager = BoostManager.getInstance();
        //final BoostProtectManger boostProtectManger = BoostProtectManger.getInstance();
        // 已root的情况,要么使用Root授权模式, 要么使用普通模式.
        *//*if (rootManager.isRootAvailable() && rootManager.isGrantedRoot()) {
            // 加速后的数据保护
            boostProtectManger.onBeforeMemoryBoost(processManager.getAvaliableMemory(false));
            // 内存查杀杀掉的内存
            long boostedRamSize = 0l;
            if (memoryList != null && !memoryList.isEmpty()) {
                for (int i = 0; i < memoryList.size(); i++) {
                    ItemBean bean = memoryList.get(i);
                    if (bean != null && bean instanceof MemoryBean) {
                        MemoryBean cmBean = (MemoryBean) bean;
                        String packageName = cmBean.getRunningAppBean().mPackageName;
                        if (boostManager.isNeedRootBoostApp(cmBean.getRunningAppBean())) {
                            processManager.forceKillAppByPackageName(packageName);
                        } else {
                            processManager.killAppByPackageName(packageName);
                        }
                        EssentialProcessFilter.addKilledProcess(packageName);
                        boostedRamSize += cmBean.getRunningAppBean().mMemory;
                    }
                }
            }
            // 加速后的数据保护
            boostProtectManger.onMemoryBoost(boostedRamSize);
            boostProtectManger.onMemoryBoostForCard();
        } else {}*//*
        if (memoryList != null && !memoryList.isEmpty()) {
            for (int i = 0; i < memoryList.size(); i++) {
                BatteryBoostAppBean bean = memoryList.get(i);
                if (bean != null && bean.mIsChecked) {
                    String packageName = bean.mPkgName;
                    ProcessManager.getInstance(mContext).killAppByPackageName(packageName);
                    EssentialProcessFilter.addKilledProcess(packageName);
                }
            }
        }
    }*/

}
