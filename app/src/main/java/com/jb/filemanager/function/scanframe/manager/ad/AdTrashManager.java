package com.jb.filemanager.function.scanframe.manager.ad;

import android.content.Context;

import com.jb.filemanager.TheApplication;
import com.jb.filemanager.function.scanframe.bean.adbean.AdBean;
import com.jb.filemanager.function.scanframe.clean.CleanManager;
import com.jb.filemanager.function.scanframe.clean.event.CleanAdUpdateDoneEvent;
import com.jb.filemanager.function.scanframe.clean.event.CleanDBDataInitDoneEvent;
import com.jb.filemanager.function.scanframe.clean.event.GlobalDataLoadingDoneEvent;
import com.jb.filemanager.util.file.FileUtil;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by xiaoyu on 2016/10/21.<br>
 * 广告文件扫描管理<br>
 */

public class AdTrashManager {
    private static AdTrashManager sInstance;
    private ArrayList<AdBean> mAdBeanList = new ArrayList<>();
    private AdDataManager mDataManager;
    private Context mContext;

    private AdTrashManager(Context context) {
        mContext = context.getApplicationContext();
        mDataManager = AdDataManager.getInstance(mContext);
        mAdBeanList.clear();
        mAdBeanList.addAll(mDataManager.getAdBeanList());
        TheApplication.getGlobalEventBus().register(mEventReceiver);
    }

    public static AdTrashManager getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new AdTrashManager(context);
        }
        return sInstance;
    }

    public ArrayList<AdBean> getAdList(String sdPath, HashSet<String> adIgnoreList) {
        ArrayList<AdBean> resultList = new ArrayList<>();
        for (AdBean bean : mAdBeanList) {
            if (adIgnoreList != null) {
                if (adIgnoreList.contains(bean.getPath())) {
                    continue;
                }
            }
            String filePath = sdPath + bean.getPath();
            if (FileUtil.isFileExist(filePath)) {
                AdBean clone = bean.clone();
                clone.setPath(filePath);
                resultList.add(clone);
            }
        }
        return resultList;
    }

    /**
     * 事件接收器
     */
    private Object mEventReceiver = new Object() {
        /**
         * 初始化数据
         */
        @SuppressWarnings("unused")
        @Subscribe
        public void onEventAsync(GlobalDataLoadingDoneEvent event) {
            mAdBeanList.clear();
            mAdBeanList.addAll(mDataManager.getAdBeanList());
            CleanDBDataInitDoneEvent.AD.setIsDone(true);
            TheApplication.postEvent(CleanDBDataInitDoneEvent.AD);
        }

        /**
         * 广告更新结束
         */
        @SuppressWarnings("unused")
        @Subscribe
        public void onEventMainThread(CleanAdUpdateDoneEvent event) {
            if (!CleanManager.getInstance(mContext).isScanning()) {
                updateDataIfNeed();
            }
        }
    };

    public void updateDataIfNeed() {
        if (!mDataManager.checkIsUpdated()) {
            return;
        }
         CleanDBDataInitDoneEvent.AD.setIsDone(false);
        TheApplication.postRunOnShortTaskThread(new Runnable() {
            @Override
            public void run() {
                mAdBeanList.clear();
               mAdBeanList.addAll(mDataManager.getAdBeanList());
               CleanDBDataInitDoneEvent.AD.setIsDone(true);
               TheApplication.postEvent(CleanDBDataInitDoneEvent.AD);
           }
         });
    }

}
