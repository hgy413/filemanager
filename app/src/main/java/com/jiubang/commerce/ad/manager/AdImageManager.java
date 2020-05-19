package com.jiubang.commerce.ad.manager;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import com.jb.ga0.commerce.util.LogUtils;
import com.jiubang.commerce.ad.AdSdkContants;
import com.jiubang.commerce.ad.bean.AdInfoBean;
import com.jiubang.commerce.utils.ImageUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AdImageManager {
    private static AdImageManager sInstance;
    /* access modifiers changed from: private */
    public Context mContext;
    private ExecutorService mExecutorService = Executors.newSingleThreadExecutor();

    public interface ILoadSingleAdImageListener {
        void onLoadFail(String str);

        void onLoadFinish(String str, Bitmap bitmap);
    }

    private AdImageManager(Context context) {
        this.mContext = context;
    }

    public static AdImageManager getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new AdImageManager(context);
        }
        return sInstance;
    }

    public void asynLoadAdImage(final String imageUrl, final ILoadSingleAdImageListener listener) {
        if (!TextUtils.isEmpty(imageUrl)) {
            this.mExecutorService.execute(new Runnable() {
                public void run() {
                    Bitmap bitmap = ImageUtils.loadImage(AdImageManager.this.mContext, imageUrl, AdImageManager.getAdImagePath(imageUrl));
                    if (LogUtils.isShowLog()) {
                        LogUtils.d("Ad_SDK", "asynLoadAdImage(" + imageUrl + ", " + bitmap + ")");
                    }
                    if (listener != null) {
                        listener.onLoadFinish(imageUrl, bitmap);
                    }
                }
            });
        } else if (listener != null) {
            listener.onLoadFail(imageUrl);
        }
    }

    public boolean syncLoadAdImage(List<AdInfoBean> adInfoList, boolean isNeedDownloadIcon, boolean isNeedDownloadBanner) {
        if (adInfoList == null || adInfoList.isEmpty()) {
            return false;
        }
        List<AdInfoBean> adInfoListCopy = new ArrayList<>();
        adInfoListCopy.addAll(adInfoList);
        for (AdInfoBean adInfoBean : adInfoListCopy) {
            if (adInfoBean != null) {
                if (isNeedDownloadIcon && !TextUtils.isEmpty(adInfoBean.getIcon())) {
                    ImageUtils.loadImage(this.mContext, adInfoBean.getIcon(), getAdImagePath(adInfoBean.getIcon()));
                }
                if (isNeedDownloadBanner && !TextUtils.isEmpty(adInfoBean.getBanner())) {
                    ImageUtils.loadImage(this.mContext, adInfoBean.getBanner(), getAdImagePath(adInfoBean.getBanner()));
                }
                if (LogUtils.isShowLog()) {
                    LogUtils.d("Ad_SDK", "syncLoadAdImage(ad count:" + adInfoList.size() + ", isNeedDownloadBanner:" + isNeedDownloadBanner + ")");
                }
            }
        }
        return true;
    }

    public static Bitmap getBitmapForAd(String imageUrl) {
        if (TextUtils.isEmpty(imageUrl)) {
            return null;
        }
        return ImageUtils.getBitmapFromSDCard(getAdImagePath(imageUrl));
    }

    public static String getAdImagePath(String imageUrl) {
        return AdSdkContants.getADVERT_DATA_CACHE_IMAGE_PATH() + String.valueOf(imageUrl.hashCode());
    }
}
