package com.jiubang.commerce.ad.manager;

import android.content.Context;
import android.util.SparseArray;
import com.jiubang.commerce.database.model.AdShowClickBean;
import com.jiubang.commerce.database.table.AdShowClickTable;
import com.jiubang.commerce.utils.AdTimer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AdModuleShowCountManager {
    private static AdModuleShowCountManager sInstance;
    /* access modifiers changed from: private */
    public Context mContext;
    private ExecutorService mExecutorService = Executors.newSingleThreadExecutor();
    private byte[] mLock = new byte[0];
    private SparseArray<ShowCountItem> mShowCountDatas;

    private AdModuleShowCountManager(Context context) {
        this.mContext = context.getApplicationContext();
        init();
    }

    public static synchronized AdModuleShowCountManager getInstance(Context context) {
        AdModuleShowCountManager adModuleShowCountManager;
        synchronized (AdModuleShowCountManager.class) {
            if (sInstance == null && context != null) {
                sInstance = new AdModuleShowCountManager(context);
            }
            adModuleShowCountManager = sInstance;
        }
        return adModuleShowCountManager;
    }

    private void init() {
        this.mShowCountDatas = new SparseArray<>();
    }

    private int calculateShowCount(int vmid) {
        int showCount = 0;
        AdShowClickTable.getInstance(this.mContext).deleteExpiredData();
        for (AdShowClickBean bean : AdShowClickTable.getInstance(this.mContext).getValidData(vmid)) {
            String opt = bean.getOpt();
            if (AdShowClickBean.OPT_CLICK.equals(opt)) {
                break;
            } else if (AdShowClickBean.OPT_SHOW.equals(opt)) {
                showCount++;
            }
        }
        return showCount;
    }

    public int getShowCount(int vmid) {
        int showCount;
        synchronized (this.mLock) {
            ShowCountItem item = this.mShowCountDatas.get(vmid);
            if (item == null || Math.abs(System.currentTimeMillis() - item.mUpdateTimeZero) >= 86400000) {
                if (item == null) {
                    item = new ShowCountItem();
                }
                showCount = calculateShowCount(vmid);
                item.mShowCount = showCount;
                item.mUpdateTimeZero = AdTimer.getTodayZeroMills();
                this.mShowCountDatas.put(vmid, item);
            } else {
                showCount = item.mShowCount;
            }
        }
        return showCount;
    }

    public void recordShow(int vmid) {
        recordOpt(vmid, AdShowClickBean.OPT_SHOW);
    }

    public void recordClick(int vmid) {
        recordOpt(vmid, AdShowClickBean.OPT_CLICK);
    }

    private void recordOpt(final int vmid, final String opt) {
        this.mExecutorService.execute(new Runnable() {
            public void run() {
                AdShowClickBean bean = new AdShowClickBean();
                bean.setVMID(vmid);
                bean.setOpt(opt);
                bean.setUpdateTime(System.currentTimeMillis());
                AdShowClickTable.getInstance(AdModuleShowCountManager.this.mContext).insertData(bean);
            }
        });
    }

    private static class ShowCountItem {
        public int mShowCount;
        public long mUpdateTimeZero;

        private ShowCountItem() {
        }
    }
}
