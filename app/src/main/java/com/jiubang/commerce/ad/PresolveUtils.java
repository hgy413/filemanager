package com.jiubang.commerce.ad;

import android.content.Context;
import com.jb.ga0.commerce.util.LogUtils;
import com.jiubang.commerce.ad.bean.AdInfoBean;
import com.jiubang.commerce.ad.http.bean.BaseModuleDataItemBean;
import com.jiubang.commerce.ad.http.bean.ParamsBean;
import com.jiubang.commerce.ad.manager.AdSdkManager;
import com.jiubang.commerce.ad.params.PresolveParams;
import com.jiubang.commerce.ad.url.AdRedirectUrlUtils;
import com.jiubang.commerce.ad.url.AdUrlPreParseTask;
import java.util.List;

public class PresolveUtils {

    public interface IResolveListener {
        void onResolved(String str);
    }

    public static void preResolveRealClickAndUploadGA(Context context, List<AdInfoBean> adInfoList, AdUrlPreParseTask.ExecuteTaskStateListener listener) {
        preResolveAdvertUrl(context, adInfoList, getRealClickPresolveBuilder().build(), listener);
    }

    public static PresolveParams.Builder getRealClickPresolveBuilder() {
        return new PresolveParams.Builder().repeatClickEnable(false).isControlled(false).useCache(false).uaType(2).uploadGA(true);
    }

    public static void preResolveAdvertUrl(Context context, List<AdInfoBean> adInfoList, PresolveParams preParams, AdUrlPreParseTask.ExecuteTaskStateListener listener) {
        preResolveAdvertUrl(context, adInfoList, preParams != null ? preParams.mUseCache : true, preParams, listener);
    }

    public static void preResolveAdvertUrl(Context context, List<AdInfoBean> adInfoList, boolean useCache, PresolveParams preParams, AdUrlPreParseTask.ExecuteTaskStateListener listener) {
        if (adInfoList == null || adInfoList.isEmpty() || preParams == null) {
            if (LogUtils.isShowLog()) {
                LogUtils.w("Ad_SDK", "preResolveAdvertUrl(params null error)", new Throwable());
            }
            if (listener != null) {
                listener.onExecuteTaskComplete(context);
                return;
            }
            return;
        }
        AdSdkManager.preResolveAdvertUrl(context, adInfoList.get(0) != null ? adInfoList.get(0).getModuleId() : -1, adInfoList, useCache, preParams, listener);
    }

    public static void realClickAdInfoBean(Context context, AdInfoBean adInfoBean, IResolveListener listener) {
        preResolveAdInfoBean(context, new PresolveParams.Builder().repeatClickEnable(false).isControlled(false).useCache(false).uaType(2).uploadGA(true).build(), adInfoBean, listener);
    }

    public static void preResolveAdInfoBean(final Context context, final PresolveParams presolveParams, final AdInfoBean adInfoBean, final IResolveListener listener) {
        new Thread(new Runnable() {
            public void run() {
                int uaType = presolveParams.mUAType != -1 ? presolveParams.mUAType : adInfoBean.getUAType();
                ParamsBean paramsBean = new ParamsBean();
                paramsBean.setUASwitcher(adInfoBean.getUASwitcher());
                paramsBean.setFinalGpJump(adInfoBean.getmFinalGpJump());
                paramsBean.setUAType(uaType);
                String adUrl = presolveParams.mRepeatClickEnable ? AdRedirectUrlUtils.handlePreloadAdUrl(adInfoBean.getAdUrl()) : adInfoBean.getAdUrl();
                if (LogUtils.isShowLog()) {
                    LogUtils.d("Ad_SDK", "start preResolve url:" + adUrl);
                }
                listener.onResolved(AdRedirectUrlUtils.getHttpRedirectUrlFromLocation(context, paramsBean, BaseModuleDataItemBean.AD_DATA_SOURCE_TYPE_OFFLINE, String.valueOf(adInfoBean.getMapId()), String.valueOf(adInfoBean.getAdId()), adUrl));
            }
        }, "preResolveAdInfoBean").start();
    }
}
