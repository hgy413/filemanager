package com.jiubang.commerce.ad.manager;

import android.content.Context;
import com.jb.ga0.commerce.util.LogUtils;
import com.jiubang.commerce.ad.http.bean.BaseIntellAdInfoBean;
import com.jiubang.commerce.ad.http.bean.BaseIntellModuleBean;
import com.jiubang.commerce.ad.manager.AdControlManager;
import com.jiubang.commerce.utils.LruCache;
import com.jiubang.commerce.utils.NetworkUtils;
import com.jiubang.commerce.utils.StringUtils;
import java.util.ArrayList;
import java.util.List;

public class AdSearchManager {
    private static AdSearchManager sInstance;
    private Context mContext;
    private LruCache<String, List<BaseIntellAdInfoBean>> mResultCahce = new LruCache<>(20);

    public interface IAdSearchListener {
        void onAdSearchDone(List<BaseIntellAdInfoBean> list);
    }

    private AdSearchManager(Context context) {
        this.mContext = context.getApplicationContext();
    }

    public static AdSearchManager getInstance(Context context) {
        if (sInstance == null) {
            synchronized (AdSearchManager.class) {
                if (sInstance == null) {
                    sInstance = new AdSearchManager(context);
                }
            }
        }
        return sInstance;
    }

    public void searchAdByTitle(String title, int adPos, IAdSearchListener listener) {
        if (LogUtils.isShowLog()) {
            LogUtils.d("Ad_SDK", "searchAdByTitle title=" + title + " adPos=" + adPos);
        }
        if (!StringUtils.isEmpty(title) && listener != null) {
            List<BaseIntellAdInfoBean> ads = getFromCache(title);
            if (ads != null) {
                LogUtils.d("Ad_SDK", "searchAdByTitle return cached");
                listener.onAdSearchDone(new ArrayList(ads));
            } else if (NetworkUtils.isNetworkOK(this.mContext)) {
                searchFromNet(adPos, title, listener);
            } else {
                listener.onAdSearchDone((List<BaseIntellAdInfoBean>) null);
            }
        }
    }

    private void searchFromNet(int adPos, final String key, final IAdSearchListener listener) {
        AdControlManager.getInstance(this.mContext).loadSearchPresolveAdInfo(this.mContext, adPos, key, new AdControlManager.AdIntellRequestListener() {
            public void onFinish(BaseIntellModuleBean moduleBean) {
                if (moduleBean == null || !moduleBean.isSuccess()) {
                    listener.onAdSearchDone((List<BaseIntellAdInfoBean>) null);
                    return;
                }
                List<BaseIntellAdInfoBean> ads = moduleBean.getmAdvs();
                if (ads == null) {
                    ads = new ArrayList<>();
                }
                AdSearchManager.this.add2Cache(key, ads);
                listener.onAdSearchDone(new ArrayList(ads));
            }
        });
    }

    private synchronized List<BaseIntellAdInfoBean> getFromCache(String title) {
        return this.mResultCahce.get(title);
    }

    /* access modifiers changed from: private */
    public synchronized void add2Cache(String title, List<BaseIntellAdInfoBean> ads) {
        if (!StringUtils.isEmpty(title)) {
            if (ads == null) {
                ads = new ArrayList<>();
            }
            this.mResultCahce.put(title, ads);
        }
    }
}
