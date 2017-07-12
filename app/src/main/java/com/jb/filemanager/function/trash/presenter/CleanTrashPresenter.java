package com.jb.filemanager.function.trash.presenter;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.SystemClock;

import com.jb.filemanager.TheApplication;
import com.jb.filemanager.database.provider.CacheTrashRecordProvider;
import com.jb.filemanager.function.scanframe.bean.CleanGroupsBean;
import com.jb.filemanager.function.scanframe.bean.appBean.AppItemInfo;
import com.jb.filemanager.function.scanframe.bean.cachebean.CacheBean;
import com.jb.filemanager.function.scanframe.bean.cachebean.subitem.SubSysCacheBean;
import com.jb.filemanager.function.scanframe.bean.common.god.BaseChildBean;
import com.jb.filemanager.function.scanframe.bean.common.itemcommon.GroupType;
import com.jb.filemanager.function.scanframe.bean.common.itemcommon.ItemBean;
import com.jb.filemanager.function.scanframe.bean.common.subitemcommon.SubItemBean;
import com.jb.filemanager.function.scanframe.clean.CleanEventManager;
import com.jb.filemanager.function.scanframe.clean.CleanManager;
import com.jb.filemanager.function.scanframe.clean.event.CleanCheckedFileSizeEvent;
import com.jb.filemanager.function.scanframe.clean.event.CleanScanFileSizeEvent;
import com.jb.filemanager.function.scanframe.clean.event.CleanSingleSysCacheScanDoneEvent;
import com.jb.filemanager.function.scanframe.clean.event.CleanStateEvent;
import com.jb.filemanager.util.Logger;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.jb.filemanager.commomview.GroupSelectBox.SelectState.ALL_SELECTED;
import static com.jb.filemanager.commomview.GroupSelectBox.SelectState.NONE_SELECTED;
import static com.jb.filemanager.function.scanframe.bean.common.itemcommon.GroupType.AD;
import static com.jb.filemanager.function.scanframe.bean.common.itemcommon.GroupType.APK;
import static com.jb.filemanager.function.scanframe.bean.common.itemcommon.GroupType.APP_CACHE;
import static com.jb.filemanager.function.scanframe.bean.common.itemcommon.GroupType.BIG_FILE;
import static com.jb.filemanager.function.scanframe.bean.common.itemcommon.GroupType.RESIDUE;
import static com.jb.filemanager.function.scanframe.bean.common.itemcommon.GroupType.TEMP;
import static com.jb.filemanager.util.file.FileUtil.deleteCategory;


/**
 * Created by xiaoyu on 2016/12/23 18:22.
 */

public class CleanTrashPresenter {
    // 常量
    private static final long RESET_TIME = 60 * 1000;
    private static final String DELETE_PATH = "DeletePath";
    private static final String TAG = "CleanManager";

    // 管理器
    private Context mContext;
    private CleanManager mCleanManager;
    private CleanEventManager mEventManager;
    private Contract.ICleanMainView mView;
    // 数据
    private List<CleanGroupsBean> mDataGroup;
    private CleanGroupsBean mAppCacheGroup;
    private CleanGroupsBean mResidueGroup;
    private CleanGroupsBean mTempGroup;
    private CleanGroupsBean mBigFileGroup;
    private CleanGroupsBean mApkGroup;
    private CleanGroupsBean mAdGroup;
    //private CleanGroupsBean mMemoryGroup;
    private final ArrayList<BaseChildBean> mEmptyList = new ArrayList<>();
    // 已删除的文件列表
    private ArrayList<ItemBean> mAppCacheDelList = new ArrayList<>();
    private ArrayList<ItemBean> mResidueDelList = new ArrayList<>();
    private ArrayList<ItemBean> mTempDelList = new ArrayList<>();
    private ArrayList<ItemBean> mApkDelList = new ArrayList<>();
    private ArrayList<ItemBean> mBigFileDelList = new ArrayList<>();
    private ArrayList<ItemBean> mAdDelList = new ArrayList<>();
    //private ArrayList<ItemBean> mMemoryDelList = new ArrayList<>();
    // 待删除的文件列表
    private ArrayList<ItemBean> mAppCacheChildren = new ArrayList<>();
    private ArrayList<ItemBean> mResidueChildren = new ArrayList<>();
    private ArrayList<ItemBean> mTempChildren = new ArrayList<>();
    private ArrayList<ItemBean> mApkChildren = new ArrayList<>();
    private ArrayList<ItemBean> mBigFileChildren = new ArrayList<>();
    private ArrayList<ItemBean> mAdChildren = new ArrayList<>();
    //private ArrayList<ItemBean> mMemoryChildren = new ArrayList<>();
    // 标志位
    private boolean mIsStopDelete;
    /**
     * 控制每次只删除一个Item的标志位
     */
    private boolean mIsOneItemDeleted = false;
    /**
     * 正在启动结果页
     */
    private boolean mStartingDonePage = false;
    /**
     * 完成扫描后是否处于加载动画：防止加载动画过程中进行点击操作
     */
    private boolean mIsInProgress = true;
    private HandlerThread mThread;
    private Handler mAsyHandler;
    private boolean mNoneGroupChecked;

    public CleanTrashPresenter(Contract.ICleanMainView view) {
        mView = view;
        mContext = TheApplication.getAppContext();
        mCleanManager = CleanManager.getInstance(TheApplication.getAppContext());
        mEventManager = CleanEventManager.getInstance();
        mProgressBarHandler = new MyHandler(mCleanManager, mView);
        initData();
    }

    private void initData() {
        mDataGroup = new ArrayList<>();
        mEmptyList.clear();
        mAppCacheGroup = new CleanGroupsBean(mContext, mEmptyList,
                APP_CACHE);
        mResidueGroup = new CleanGroupsBean(mContext, mEmptyList,
                RESIDUE);
        mTempGroup = new CleanGroupsBean(mContext, mEmptyList,
                TEMP);
        mApkGroup = new CleanGroupsBean(mContext, mEmptyList,
                APK);
        mBigFileGroup = new CleanGroupsBean(mContext, mEmptyList,
                BIG_FILE);
        mAdGroup = new CleanGroupsBean(mContext, mEmptyList, AD);
        //mMemoryGroup = new CleanGroupsBean(mContext, mEmptyList,
        //        GroupType.MEMORY);

        mDataGroup.add(mAppCacheGroup);
        mDataGroup.add(mResidueGroup);
        mDataGroup.add(mAdGroup);
        mDataGroup.add(mTempGroup);
        mDataGroup.add(mApkGroup);
        // 此页面不需要扫描内存信息
        //if (CleanMemoryManager.getInstance().isScanMemoryJunk()) {
        //    mDataGroup.add(mMemoryGroup);
        //}
        mDataGroup.add(mBigFileGroup);
    }

    //***************************************** 通用 *******************************************//

    /**
     * 点击清理按钮
     */
    @SuppressWarnings("deprecation")
    public void onCleanBtnClick() {
        switch (mEventManager.getCleanState()) {
            case SCAN_ING:
                stopScan();
                break;
            case SCAN_FINISH:
            case DELETE_FINISH:
            case DELETE_SUSPEND:
                if (mIsInProgress | mStartingDonePage) {
                    return;
                }
                mCleanManager.startDelete();
                mView.onDeleteStart();
                break;
            case DELETE_ING:
                stopDelete();
                break;
            default:
                break;
        }
        //if (DailyLeadTipManager.sIsUserOperationUnderJunkFileTips) {
        //    StatisticsTools.uploadClickData(StatisticsConstants.SCR_RAB_ENTER);
        //}

        //prepareAdMildly();
    }

    /**
     * 根据类型获取相应的数据组
     */
    public CleanGroupsBean getGroup(GroupType type) {
        return CleanGroupsBean.getGroup(type, mDataGroup);
    }

    /**
     * 获取各组数据列表
     */
    public List<CleanGroupsBean> getDataGroup() {
        return mDataGroup;
    }

//************************************* 进入清理主界面流程 *****************************************//

    /**
     * 进入清理主界面
     */
    public void enterCleanMainFragment() {
        // 都重新扫描内存
        //mCleanManager.scanMemory();
        if (canRestartScan()) {
            onStateRestartScan();
        } else if (mEventManager.getCleanState().equals(
                CleanStateEvent.SCAN_ING)) {
            // 处于扫描文件中
            onStateScanning();
        } else if (mEventManager.getCleanState().equals(
                CleanStateEvent.SCAN_FINISH)) {
            // 处于扫描结束状态
            onStateScanFinish();
        } else if (mEventManager.getCleanState().equals(
                CleanStateEvent.DELETE_SUSPEND)) {
            // 处于扫描中断状态
            onStateDeleteSuspend();
        } else if (mEventManager.getCleanState().equals(
                CleanStateEvent.DELETE_FINISH)) {
            // 删除完毕
            onStateDeleteFinish();
        }
    }

    /**
     * 是否满足重新扫描的条件
     */
    private boolean canRestartScan() {
        return mEventManager.getCleanState().equals(CleanStateEvent.SCAN_SUSPEND)
                || isStateUpdateOut(CleanStateEvent.SCAN_FINISH)
                || isStateUpdateOut(CleanStateEvent.DELETE_SUSPEND)
                || isStateUpdateOut(CleanStateEvent.DELETE_FINISH);
    }

    /**
     * 是否处于该状态，并且状态更新时间超过规定时长，已过时
     */
    private boolean isStateUpdateOut(CleanStateEvent event) {
        return mEventManager.getCleanState().equals(event)
                && System.currentTimeMillis() - event.getLastTime() > RESET_TIME;
    }

    /**
     * 执行类型1：重新扫描文件
     */
    private void onStateRestartScan() {
        Logger.i(TAG, "执行类型1：重新扫描文件");
        if (!mCleanManager.isScanning()) {
            mCleanManager.startJunkFileScanTask();
        }
        updateProgressBar();
    }

    /**
     * 执行类型2：继续扫描
     */
    private void onStateScanning() {
        Logger.i(TAG, "执行类型2：继续扫描");
        updateProgressBar();
        updateProgressState();
        mView.onFileScanning();
    }

    public static final int PROGRESS_MSG_NORMAL = 0x31;
    public static final int PROGRESS_MSG_WAIT = 0x32;

    /**
     * 更新进度条
     */
    private Handler mProgressBarHandler;

    private static class MyHandler extends Handler {
        float mScanProgress;
        float mScanIncrease = 0.001f;
        float mScanIncSum = 0f;
        float mScanLastInc = 0f;

        private WeakReference<CleanManager> mCleanManagerR;
        private WeakReference<Contract.ICleanMainView> mViewR;

        public MyHandler(CleanManager cleanManager, Contract.ICleanMainView view) {
            mCleanManagerR = new WeakReference<CleanManager>(cleanManager);
            mViewR = new WeakReference<Contract.ICleanMainView>(view);
        }

        public void handleMessage(Message msg) {
            if (mCleanManagerR == null
                    || mViewR == null
                    || mCleanManagerR.get() == null
                    || mViewR.get() == null) {
                return;
            }
            if (msg.what == PROGRESS_MSG_NORMAL) {
                float progress = mCleanManagerR.get().getScanProgress();
                //Logger.i("CleanPresenter", "progress = " + progress);
                mViewR.get().updateProgress(mScanProgress);
                if (progress >= 0.9f) {
                    if (progress == 1.0f) {
                        mScanLastInc = (1 - mScanProgress) / 10.0f;
                    }
                    sendEmptyMessageDelayed(PROGRESS_MSG_WAIT, 200);
                } else {
                    if (mScanProgress < 0.5f) {
                        mScanIncSum += mScanIncrease;
                        mScanProgress = progress + mScanIncSum;
                    } else {
                        mScanProgress = Math.max(mScanProgress + 0.0005f,
                                progress);
                    }
                    sendEmptyMessageDelayed(PROGRESS_MSG_NORMAL, 16);
                }
            } else if (msg.what == PROGRESS_MSG_WAIT) {
                if (mCleanManagerR.get().getScanProgress() == 1) {
                    if (mScanLastInc == 0) {
                        mScanLastInc = (1 - mScanProgress) / 10.0f;
                    }
                    mScanProgress += mScanLastInc;
                    mScanProgress = Math.min(mScanProgress, 1.0f);
                    mViewR.get().updateProgress(mScanProgress);
                    if (mScanProgress < 1.0f) {
                        sendEmptyMessageDelayed(PROGRESS_MSG_WAIT, 10);
                    }
                } else {
                    mScanProgress += 0.002f;
                    mScanProgress = Math.min(mScanProgress, 0.98f);
                    mViewR.get().updateProgress(mScanProgress);
                    sendEmptyMessageDelayed(PROGRESS_MSG_WAIT, 200);
                }
            }
        }
    }

    private void updateProgressBar() {
        mProgressBarHandler.sendEmptyMessageDelayed(PROGRESS_MSG_NORMAL, 20);
    }

    /**
     * 执行类型3：显示扫描结果
     */
    private void onStateScanFinish() {
        Logger.i(TAG, "执行类型3：显示扫描结果");
        resetCheckState();
        updateProgressState();
        //updateProgressBar();
        mView.onFileScanFinish();
    }

    /**
     * 执行类型4：显示选中但未删除的数据
     */
    private void onStateDeleteSuspend() {
        Logger.i(TAG, "执行类型4：删除中断 - 显示选中但未删除的数据");
        CleanCheckedFileSizeEvent.initData();
        resetCheckState();
        updateProgressState();
        //updateProgressBar();
        mView.onFileScanFinish();
    }

    /**
     * 执行类型5：显示未选中的数据
     */
    private void onStateDeleteFinish() {
        Logger.i(TAG, "执行类型5：删除完成 - 显示未选中的数据");
        CleanCheckedFileSizeEvent.initData();
        resetCheckState();
        updateProgressState();
        //updateProgressBar();
        mView.onFileScanFinish();
    }

    private void resetCheckState() {
        mCleanManager.resetStateToDefault();
    }

    //**************************************** 扫描完毕 *******************************************//

    /**
     * 更新进度圈动画状态
     */
    public void updateProgressState() {
        for (CleanGroupsBean group : mDataGroup) {
            group.updateScanFinish();
        }
        mView.notifyDataSetChanged();
    }

    /**
     * 更新默认选中状态
     */
    public void updateDefaultCheckedState() {
        updateSysCacheToDefault();
        mAppCacheGroup.updateStateBySubItem();
        mTempGroup.setState(ALL_SELECTED);
        mApkGroup.updateStateBySubItem();
        mAdGroup.setState(ALL_SELECTED);
        //mMemoryGroup.setState(mCleanManager.getMemoryGroupSelectState());
    }

    /**
     * 更新系统缓存为默认状态(选中)
     */
    private void updateSysCacheToDefault() {
        postSysCacheTask(new SimpleSysCacheTask() {
            @Override
            public void run(int index, CacheBean cacheItem,
                            SubSysCacheBean sysSubItem) {
                sysSubItem.setChecked(true);
            }
        });
    }

    /**
     * 执行系统缓存任务
     */
    @SuppressWarnings("unchecked")
    private void postSysCacheTask(SysCacheTask task) {
        List<BaseChildBean> children = mAppCacheGroup.getChildren();
        //List<CacheBean> children = mCleanManager.getCacheList();
        if (!children.isEmpty()) {
            // 获取队列首位，通过判断三级目录是否为系统缓存类型
            BaseChildBean chiBean = children.get(0);
            if (chiBean.isTypeItem()) {
                CacheBean itemBean = (CacheBean) chiBean;
                ArrayList<SubItemBean> subItemList = itemBean
                        .getSubItemList();
                if (!subItemList.isEmpty()) {
                    SubItemBean subItemBean = subItemList.get(0);
                    if (subItemBean.isSysCache()) {
                        // 突破层层判断，确定为系统缓存
                        task.run(children, itemBean, subItemList);
                    }
                }
            }
        }
    }

    /**
     * 系统缓存任务
     *
     * @author chenbenbin
     */
    abstract class SimpleSysCacheTask implements SysCacheTask {

        /**
         * 循环回调列表中的系统缓存数据
         *
         * @param index      三级列表的下标
         * @param itemBean   系统缓存二级对象
         * @param sysSubItem 系统缓存三级对象
         */
        public abstract void run(int index, CacheBean itemBean,
                                 SubSysCacheBean sysSubItem);

        public final void run(List<BaseChildBean> groupChildren,
                              CacheBean itemBean, List<SubItemBean> subItemList) {
            int size = subItemList.size();
            for (int i = 0; i < size; i++) {
                run(i, itemBean, (SubSysCacheBean) subItemList.get(i));
            }
        }
    }

    /**
     * 设置所有进度图动画状态均为执行完成
     */
    public void setAllProgressFinish() {
        for (CleanGroupsBean bean : mDataGroup) {
            bean.setProgressFinish(true);
        }
    }

    /**
     * 扫描完毕，从数据管理器获取相关的数据
     */
    public void onScanFinish() {
        mIsInProgress = false;
        mEmptyList.clear();
        updateChildData(mAppCacheGroup, mCleanManager.getCacheList());
        updateChildData(mResidueGroup, mCleanManager.getResidueList());
        updateChildData(mTempGroup, mCleanManager.getTempArrayList());
        updateChildData(mApkGroup, mCleanManager.getApkArrayList());
        updateChildData(mBigFileGroup, mCleanManager.getBigSizeArrayList());
        updateChildData(mAdGroup, mCleanManager.getAdList());
        //updateChildData(mMemoryGroup, mCleanManager.getMemoryArrayList());
    }

    @SuppressWarnings("unchecked")
    private void updateChildData(CleanGroupsBean group,
                                 List<? extends BaseChildBean> children) {
        group.setChild(children);
    }

    /**
     * 隐藏扫描结果数据为空的组
     */
    public boolean removeEmptyGroup() {
        Iterator<CleanGroupsBean> iterator = mDataGroup.iterator();
        while (iterator.hasNext()) {
//            if (iterator.next().getchildrenSize() == 0) {
//                iterator.remove();
//            }
            CleanGroupsBean next = iterator.next();
            if (next.getSize() == 0) {
                iterator.remove();
                continue;
            }
            Iterator<BaseChildBean> itemIterator = next.getChildren().iterator();
            while (itemIterator.hasNext()) {
                BaseChildBean next1 = itemIterator.next();
                if (next1.getSize() == 0) {
                    itemIterator.remove();
                }
            }

        }
        return mDataGroup.size() == 0;
    }

    /**
     * 展开指定的组
     */
    public void expandAssignGroup() {
        // 延迟600之后再展开
        new Thread() {
            @Override
            public void run() {
                for (int i = 0; i < mDataGroup.size(); i++) {
                    final int index = i;
                    SystemClock.sleep(200);
                    TheApplication.postRunOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mView.expandGroup(index);
                        }
                    });
                }
            }
        }.start();
    }

    public void collapsedAllGroup() {
        // 关闭所有的组
        new Thread() {
            @Override
            public void run() {
                for (int i = 0; i < mDataGroup.size(); i++) {
                    final int index = i;
                    SystemClock.sleep(200);
                    TheApplication.postRunOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mView.collapsedGroup(index);
                            if (index == (mDataGroup.size() - 1)) {
                                //说明关闭的是最后一个
                                mView.startDeleteAnimation();
                            }
                        }
                    });
                }
            }
        }.start();
    }

    //**************************************** 停止扫描 ********************************************//

    /**
     * 停止扫描
     */
    @SuppressWarnings("deprecation")
    public void stopScan() {
        mCleanManager.stopScan();
        int stopTime = (int) ((System.currentTimeMillis() - CleanStateEvent.SCAN_ING
                .getLastTime()) / 1000.0f);
        //StatisticsTools.uploadEnterAndRemark(StatisticsConstants.CLEAN_INTERSCAN_CLICK, 1, stopTime);
    }

    //************************************** 删除前 ************************************************//

    /**
     * 是否没有任意一组数据被选中
     */
    public boolean isNoneGroupChecked() {
        boolean isEmpty = true;
        for (int i = 0; i < mDataGroup.size(); i++) {
            CleanGroupsBean groupBean = mDataGroup.get(i);
            isEmpty = groupBean.getState().equals(NONE_SELECTED);
            if (!isEmpty) {
                break;
            }
        }
        return isEmpty;
    }

    /**
     * 隐藏未选中的Item
     */
    public void hideUncheckedItem() {
        //uploadAppCacheCleanData();
        mView.notifyDataSetChanged();
        hideUncheckedItem(mAppCacheGroup);
        hideUncheckedItem(mResidueGroup);
        hideUncheckedItem(mTempGroup);
        hideUncheckedItem(mBigFileGroup);
        hideUncheckedItem(mApkGroup);
        hideUncheckedItem(mAdGroup);
        //hideUncheckedItem(mMemoryGroup);
    }

    /**
     * 隐藏未选中的Item,并隐藏三级列表
     */
    private void hideUncheckedItem(CleanGroupsBean group) {
        List<?> children = group.getChildren();
        Iterator<?> sListIterator = children.iterator();
        while (sListIterator.hasNext()) {
            BaseChildBean childBean = (BaseChildBean) sListIterator.next();
            if (childBean.isTypeSubItem()) {
                // 隐藏三级项
                sListIterator.remove();
                mView.notifyDataSetChanged();
                continue;
            }
            ItemBean itemBean = (ItemBean) childBean;
            if (itemBean.isNoneSelected()) {
                sListIterator.remove();
                mView.notifyDataSetChanged();
            }
        }
        if (group.getchildrenSize() == 0) {
            mDataGroup.remove(group);
            mView.notifyDataSetChanged();
        }
    }

    /**
     * 获取删除项的列表
     */
    public List<ItemBean> getDeleteItemList() {
        ArrayList<ItemBean> list = new ArrayList<ItemBean>();
        list.addAll(mAppCacheChildren);
        list.addAll(mResidueChildren);
        list.addAll(mAdChildren);
        list.addAll(mTempChildren);
        list.addAll(mApkChildren);
        list.addAll(mBigFileChildren);
        //list.addAll(mMemoryChildren);
        return list;
    }

    /**
     * 查询单个应用的系统缓存数据
     */
    public void querySysCache(AppItemInfo info) {
        mCleanManager.querySysCache(info);
    }

    //************************************* 删除过程 **********************************************//

    /**
     * 开始删除
     */
    public void startDelete() {
        mIsStopDelete = false;
        mAppCacheChildren.addAll(filterSubBean(mCleanManager.getCacheList()));
        //add by nieyh 将应用缓存垃圾记录添加到数据库中 ↓↓↓
        CacheTrashRecordProvider cacheTrashRecordProvider = new CacheTrashRecordProvider(TheApplication.getAppContext());
        cacheTrashRecordProvider.insertAllCacheTrashRecord(mAppCacheChildren);
        //add by nieyh 将应用缓存垃圾记录添加到数据库中 ↑↑↑
        mResidueChildren.addAll(filterSubBean(mCleanManager.getResidueList()));
        mAdChildren.addAll(filterSubBean(mCleanManager.getAdList()));
        mTempChildren.addAll(filterSubBean(mCleanManager.getTempArrayList()));
        mApkChildren.addAll(filterSubBean(mCleanManager.getApkArrayList()));
        mBigFileChildren.addAll(filterSubBean(mCleanManager.getBigSizeArrayList()));

        postSysCacheTask(new SysCacheTask() {
            @Override
            public void run(List<BaseChildBean> groupChildren, CacheBean itemBean, List<SubItemBean> subItemList) {
                if (itemBean.isAllSelected()) {
                    itemBean.getSubItemList().clear();
                    mCleanManager.cleanSysCache();
                }
            }
        });

        deleteAllItemsAsy();
    }

    /**
     * 过滤掉组的子列表中的三级数据Bean，留下二级列表项
     */
    private List<ItemBean> filterSubBean(List children) {
        List<ItemBean> result = new ArrayList<>();
        for (Object child : children) {
            if (child instanceof ItemBean) {
                ItemBean itemBean = (ItemBean) child;
                result.add(itemBean);
            }
        }
        return result;
    }

    /**
     * 异步删除全部Item
     */
    public void deleteAllItemsAsy() {
        new DeleteAsy().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    /**
     * 删除文件的异步任务
     * TODO
     *
     * @author chenbenbin
     */
    class DeleteAsy extends AsyncTask<Void, Object, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            // TODO: 2017/7/10 add by --miwo 进度回调的添加地点
            deleteItem(mAppCacheChildren, mAppCacheDelList);
            deleteItem(mResidueChildren, mResidueDelList);
            deleteItem(mAdChildren, mAdDelList);
            deleteItem(mTempChildren, mTempDelList);
            deleteItem(mApkChildren, mApkDelList);
            deleteItem(mBigFileChildren, mBigFileDelList);
            return null;
        }

        /**
         * 删除选中项
         */
        private void deleteItem(List<ItemBean> children, List<ItemBean> delList) {
            for (int size = children.size(); size > 0; size--) {
                if (mIsStopDelete || children.size() <= 0) {
                    return;
                }
                ItemBean delBean = children.remove(0);
                ArrayList<SubItemBean> subItemList = delBean.getSubItemList();
                // 删除文件
                if (subItemList.size() == 0) {
                    if (!delBean.isAllSelected()) {
                        continue;
                    }
                    // 没有子项，则删除本身带的路径(二级)
                    for (String path : delBean.getPaths()) {
                        deleteCategory(path);
                    }
                } else {
                    // 包含子项，则删除子项的路径(三级)
                    for (SubItemBean subItem : subItemList) {
                        // 由于界面数据只是克隆了内存数据的列表，并没有对列表进行深度克隆。
                        // 所以三级项不能直接移除(因为是同个SubItemBean)，所以只能通过判断是否勾选来决定删除文件
                        if (subItem.isChecked()) {
                            deleteCategory(subItem.getPath());
                            delBean.setSize(delBean.getSize() - subItem.getSize());
                        }
                    }
                }
                delList.add(delBean);
            }
        }

        @Override
        protected void onProgressUpdate(Object... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            updateSrcData();
            onDeleteFinish();
        }
    }

    /**
     * 单个应用的系统缓存清理结束
     */
    public void onSingleSysCacheScanDone(
            final CleanSingleSysCacheScanDoneEvent event) {
        postSysCacheTask(new SysCacheTask() {
            @Override
            public void run(List<BaseChildBean> groupChildren,
                            CacheBean itemBean, List<SubItemBean> subItemList) {
                // 二级的迭代器
                Iterator<BaseChildBean> groupIterator = groupChildren
                        .iterator();
                // 三级的迭代器
                Iterator<SubItemBean> subIterator = subItemList.iterator();
                while (subIterator.hasNext()) {
                    SubSysCacheBean sysSubItem = (SubSysCacheBean) subIterator
                            .next();
                    if (sysSubItem.getPackageName().equals(
                            event.getPackageName())) {
                        long srcSize = sysSubItem.getSize();
                        long dif = srcSize - event.getSize();
                        if (dif <= 0) {
                            break;
                        }
                        // 传递过去的值为原本显示在列表中的值，相比起传递减少的值，可以避免删除后固定的20K(12K等，不确定)文件空间大小问题
                        CleanEventManager.getInstance().sendSingleSysCacheDelete(
                                srcSize, itemBean.isAllSelected());
                        itemBean.setSize(itemBean.getSize() - srcSize);
                        // 移除界面数据
                        while (groupIterator.hasNext()) {
                            BaseChildBean cleanChildBean = groupIterator
                                    .next();
                            if (cleanChildBean.equals(sysSubItem)) {
                                groupIterator.remove();
                            }
                        }
                        // 移除内存数据
                        subIterator.remove();
                        // 若系统缓存列表为空，则将系统缓存项从缓存列表中移除
                        if (itemBean.getSubItemList().isEmpty()) {
                            Iterator<BaseChildBean> gIterator = groupChildren.iterator();
                            while (gIterator.hasNext()) {
                                BaseChildBean cleanChildBean = gIterator
                                        .next();
                                if (cleanChildBean.equals(itemBean)) {
                                    gIterator.remove();
                                    break;
                                }
                            }
                        }
                        mView.notifyDataSetChanged();
                        break;
                    }
                }
            }
        });
    }

    //*************************************** 删除中断 ********************************************//

    /**
     * 停止删除
     */
    public void stopDelete() {
        mIsStopDelete = true;
        // 直接删除所有内存项
        //deleteMemoryAllItem();
    }

    /**
     * 是否已经停止删除
     */
    public boolean isDeleteStop() {
        return mIsStopDelete;
    }

    /**
     * 是否正在删除
     */
    public boolean isDeleting() {
        return mEventManager.getCleanState().equals(CleanStateEvent.DELETE_ING);
    }

    /**
     * 删除内存数据
     */
    private void deleteMemoryAllItem() {
        //mMemoryDelList.addAll(mMemoryChildren);
        //mMemoryChildren.clear();
    }

    //***************************************删除完成***************************************/

    /**
     * 删除完成
     */
    @SuppressWarnings("deprecation")
    public void onDeleteFinish() {
        if (mThread != null) {
            try {
                mThread.quit();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        mStartingDonePage = true;
        int deleteTime = (int) ((System.currentTimeMillis() - CleanStateEvent.DELETE_ING
                .getLastTime()) / 1000);
        if (mIsStopDelete) {
            CleanEventManager.getInstance().sendDeleteSuspendEvent();
        } else {
            CleanEventManager.getInstance().sendDeleteFinishEvent();
        }
    }

    /**
     * 更新内存数据
     */
    private void updateSrcData() {
        // 删除文件的总大小
        long delSize = 0;
        // 更新文件大小
        delSize += updateSizeEvent(CleanScanFileSizeEvent.CacheSize,
                CleanCheckedFileSizeEvent.CacheSize, mAppCacheDelList);
        delSize += updateSizeEvent(CleanScanFileSizeEvent.ResidueFileSize,
                CleanCheckedFileSizeEvent.ResidueFileSize, mResidueDelList);
        delSize += updateSizeEvent(CleanScanFileSizeEvent.TempFileSize,
                CleanCheckedFileSizeEvent.TempFileSize, mTempDelList);
        delSize += updateSizeEvent(CleanScanFileSizeEvent.APKFileSize,
                CleanCheckedFileSizeEvent.APKFileSize, mApkDelList);
        delSize += updateSizeEvent(CleanScanFileSizeEvent.BigFileSize,
                CleanCheckedFileSizeEvent.BigFileSize, mBigFileDelList);
        delSize += updateSizeEvent(CleanScanFileSizeEvent.AdSize,
                CleanCheckedFileSizeEvent.AdSize, mAdDelList);
        mCleanManager.setCleanSize(delSize);

        // 更新内存数据
        mCleanManager.updateAppCacheList(mAppCacheDelList);
        mCleanManager.updateResidueList(mResidueDelList);
        mCleanManager.updateTempList(mTempDelList);
        mCleanManager.updateApkList(mApkDelList);
        mCleanManager.updateBigFileList(mBigFileDelList);
        mCleanManager.updateAdList(mAdDelList);
    }

    /**
     * 更新各类文件大小的事件数据
     *
     * @param totalEvent   文件总大小
     * @param checkedEvent 文件选中的大小
     * @param delList      删除的文件队列
     * @return 删除的大小
     */
    private long updateSizeEvent(CleanScanFileSizeEvent totalEvent,
                                 CleanCheckedFileSizeEvent checkedEvent,
                                 ArrayList<ItemBean> delList) {
        long delSize = getDelSize(delList);
        long totalSize = totalEvent.getSize() - delSize;
        totalEvent.setSize(totalSize >= 0 ? totalSize : 0);

        long checkedSize = checkedEvent.getSize() - delSize;
        checkedEvent.setSize(checkedSize >= 0 ? checkedSize : 0);
        return delSize;
    }

    private long getDelSize(ArrayList<ItemBean> delList) {
        long size = 0;
        for (ItemBean cleanItem : delList) {
            ArrayList<SubItemBean> subItemList = cleanItem
                    .getSubItemList();
            if (subItemList.isEmpty()) {
                size += cleanItem.getSize();
            } else {
                // 包含三级项的需要根据三级项的选中状态决定
                for (SubItemBean subItem : subItemList) {
                    if (subItem.isChecked()) {
                        size += subItem.getSize();
                    }
                }
            }
        }
        return size;
    }

    /**
     * 系统缓存任务
     *
     * @author chenbenbin
     */
    interface SysCacheTask {
        /**
         * 回调列表中的系统缓存数据
         *
         * @param groupChildren 缓存二级数据列表
         * @param itemBean      系统缓存二级对象
         * @param subItemList   系统缓存三级列表
         */
        void run(List<BaseChildBean> groupChildren,
                 CacheBean itemBean, List<SubItemBean> subItemList);
    }
}
