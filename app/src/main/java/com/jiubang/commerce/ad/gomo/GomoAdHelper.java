package com.jiubang.commerce.ad.gomo;

import android.content.Context;
import android.text.TextUtils;
import com.jb.ga0.commerce.util.LogUtils;
import com.jiubang.commerce.ad.bean.AdModuleInfoBean;
import com.jiubang.commerce.ad.gomo.GomoAdRequestHandler;
import com.jiubang.commerce.ad.http.bean.BaseModuleDataItemBean;
import com.jiubang.commerce.utils.FileCacheUtils;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONObject;

public class GomoAdHelper {
    public static AdModuleInfoBean loadGomoAds(Context context, BaseModuleDataItemBean moduleDataItemBean, int virtualModuleId, int adCount, int requestAdCount, int onlineAdPosId, boolean isRequestData, boolean needShownFilter, List<String> installFilterException) {
        AdModuleInfoBean adInfoBean;
        int i;
        if (!isRequestData) {
            String adCacheData = FileCacheUtils.readCacheDataToString(GomoAdModuleInfo.getCacheFileName(onlineAdPosId), true);
            if (!TextUtils.isEmpty(adCacheData)) {
                try {
                    AdModuleInfoBean adInfoBean2 = GomoAdModuleInfo.getAdModuleInfoBean(context, moduleDataItemBean, virtualModuleId, onlineAdPosId, adCount, needShownFilter, installFilterException, new JSONObject(adCacheData));
                    List<GomoAd> onlineAdInfoList = adInfoBean2 != null ? adInfoBean2.getGomoAdInfoList() : null;
                    GomoAdModuleInfo onlineModuleInfoBean = adInfoBean2 != null ? adInfoBean2.getGomoModuleInfoBean() : null;
                    long loadAdDataTime = onlineModuleInfoBean != null ? onlineModuleInfoBean.getSaveDataTime() : -1;
                    boolean timeValid = GomoAdModuleInfo.checkOnlineAdInfoValid(loadAdDataTime);
                    if (onlineAdInfoList == null || onlineAdInfoList.isEmpty()) {
                        if (timeValid) {
                            if (LogUtils.isShowLog()) {
                                LogUtils.d("Ad_SDK", "loadGomoAds(cacheData----all ad be shown filtered, onlineAdPosId:" + onlineAdPosId + ")");
                            }
                            return null;
                        }
                    } else if (timeValid) {
                        if (!LogUtils.isShowLog()) {
                            return adInfoBean2;
                        }
                        StringBuilder append = new StringBuilder().append("loadGomoAds(end--cacheData, onlineAdPosId:").append(onlineAdPosId).append(", adCount:").append(adCount).append(", requestAdCount:").append(requestAdCount).append(", adSize:");
                        if (adInfoBean2.getOfflineAdInfoList() != null) {
                            i = adInfoBean2.getOfflineAdInfoList().size();
                        } else {
                            i = -1;
                        }
                        LogUtils.d("Ad_SDK", append.append(i).append(")").toString());
                        return adInfoBean2;
                    } else if (LogUtils.isShowLog()) {
                        LogUtils.d("Ad_SDK", "loadGomoAds(cacheData----cache data expired, loadOnlineAdTime:" + loadAdDataTime + ", onlineAdPosId:" + onlineAdPosId + ", adCount:" + adCount + ", requestAdCount:" + requestAdCount + ")");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (LogUtils.isShowLog()) {
                        LogUtils.e("Ad_SDK", "loadGomoAds(cacheData---error, Exception:" + (e != null ? e.getMessage() : "") + ", onlineAdPosId:" + onlineAdPosId + ", adCount:" + adCount + ", requestAdCount:" + requestAdCount + ")");
                    }
                }
            }
        }
        final ArrayList arrayList = new ArrayList();
        final Context context2 = context;
        final BaseModuleDataItemBean baseModuleDataItemBean = moduleDataItemBean;
        final int i2 = virtualModuleId;
        final int i3 = onlineAdPosId;
        final int i4 = adCount;
        final boolean z = needShownFilter;
        final List<String> list = installFilterException;
        new GomoAdRequestHandler(context, onlineAdPosId, moduleDataItemBean != null ? moduleDataItemBean.getAdvDataSource() : 0, new GomoAdRequestHandler.IGomoAdListener() {
            public void onRetrived(JSONObject datasJson) {
                AdModuleInfoBean adModuleInfoBean = GomoAdModuleInfo.getAdModuleInfoBean(context2, baseModuleDataItemBean, i2, i3, i4, z, list, datasJson);
                List<GomoAd> onlineAdInfoList = adModuleInfoBean != null ? adModuleInfoBean.getGomoAdInfoList() : null;
                if (onlineAdInfoList != null && !onlineAdInfoList.isEmpty()) {
                    GomoAdModuleInfo.saveAdDataToSdcard(i3, datasJson);
                    arrayList.add(adModuleInfoBean);
                }
            }
        }).startRequest(false);
        if (arrayList.size() > 0) {
            adInfoBean = (AdModuleInfoBean) arrayList.get(0);
        } else {
            adInfoBean = null;
        }
        return adInfoBean;
    }
}
