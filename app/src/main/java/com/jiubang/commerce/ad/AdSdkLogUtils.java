package com.jiubang.commerce.ad;

import android.annotation.SuppressLint;
import android.util.SparseArray;
import com.jb.ga0.commerce.util.LogUtils;
import com.jiubang.commerce.ad.bean.AdInfoBean;
import com.jiubang.commerce.ad.bean.AdModuleInfoBean;
import com.jiubang.commerce.ad.http.bean.BaseAdvDataSourceExtInfoBean;
import com.jiubang.commerce.ad.http.bean.BaseModuleDataItemBean;
import com.jiubang.commerce.ad.sdk.bean.SdkAdSourceAdInfoBean;
import com.jiubang.commerce.ad.sdk.bean.SdkAdSourceAdWrapper;
import java.util.List;

@SuppressLint({"DefaultLocale"})
public class AdSdkLogUtils {
    public static final String[] AD_DATA_SOURCE_DES_ARRAY = {"广告数据源::->0:大数据(本地优先)", "广告数据源::->1:本地配置(区分固定随机)", "广告数据源::->2:FacebookNativeAD", "广告数据源::->3:GO桌面清理(大数据)", "广告数据源::->4:GO桌面清理(Quettra)", "广告数据源::->5:大数据(本地递补)", "广告数据源::->6:帕尔加特", "广告数据源::->7:mobivista", "广告数据源::->8:Admob", "广告数据源::->9:MobileCore全屏", "广告数据源::->10:MobileCoreBanner", "广告数据源::->11:Facebook全屏", "广告数据源::->12:通用在线数据源", "广告数据源::->13:智能预加载数据源", "广告数据源::->14:本地配置(国内)", "广告数据源::->15:智能预加载数据源(分类推荐)", "广告数据源::->16:LoopMe"};
    public static final String[] AD_OBJECT_TYPE_DES_ARRAY = {"广告对象类型-->0：离线或在线API， 对应AdInfoBean", "广告对象类型-->1：facebook banner", "广告对象类型-->2：facebook全屏", "广告对象类型-->3：facebook native", "广告对象类型-->4：admob banner", "广告对象类型-->5：admob全屏", "广告对象类型-->6：admob native, 包括CententAd和InstallAppAd", "广告对象类型-->7：loopme banner", "广告对象类型-->8：loopme全屏", "广告对象类型-->9：mobile core广告", "广告对象类型-->10：vungle广告", "广告对象类型-->11：Applovin广告", "广告对象类型-->12：Cheetah广告", "广告对象类型-->13：gomo广告", "广告对象类型-->14：IronScr广告"};
    private static SparseArray<String> sFailStatusMap = new SparseArray<>();

    public static void log(String tag, AdModuleInfoBean module) {
        if (LogUtils.isShowLog()) {
            if (module == null) {
                LogUtils.w(tag, "adInfoBean is null", new Throwable());
                return;
            }
            BaseModuleDataItemBean moduleData = module.getModuleDataItemBean();
            if (moduleData == null) {
                LogUtils.w(tag, String.format("BaseModuleDataItemBean is null<>mAdType:%d(%s)", new Object[]{Integer.valueOf(module.getAdType()), getTypeString(module)}), new Throwable());
                return;
            }
            LogUtils.i(tag, String.format("[vmId:%d][AdModuleInfoBean] moduleId:%d, mAdType:%d(%s), %s, %s, %s", new Object[]{Integer.valueOf(moduleData.getVirtualModuleId()), Integer.valueOf(moduleData.getModuleId()), Integer.valueOf(module.getAdType()), getAdDataSourceString(moduleData), getTypeString(module), getShowStyleString(moduleData), getLogString(moduleData)}));
            if (module.getAdType() == 2) {
                SdkAdSourceAdInfoBean sdkAdInfoBean = module.getSdkAdSourceAdInfoBean();
                if (sdkAdInfoBean == null) {
                    LogUtils.w(tag, String.format("[vmId:%d][AdModuleInfoBean] moduleId:%d, getSdkAdSourceAdInfoBean() is null!", new Object[]{Integer.valueOf(moduleData.getVirtualModuleId()), Integer.valueOf(moduleData.getModuleId())}), new Throwable());
                    return;
                }
                List<SdkAdSourceAdWrapper> adViewList = sdkAdInfoBean.getAdViewList();
                if (adViewList == null || adViewList.size() == 0) {
                    LogUtils.w(tag, String.format("[vmId:%d][AdModuleInfoBean] moduleId:%d, getAdViewList() is null or empty!", new Object[]{Integer.valueOf(moduleData.getVirtualModuleId()), Integer.valueOf(moduleData.getModuleId())}), new Throwable());
                    return;
                }
                LogUtils.i(tag, String.format("[vmId:%d][AdModuleInfoBean] moduleId:%d, 广告对象数量：%d", new Object[]{Integer.valueOf(moduleData.getVirtualModuleId()), Integer.valueOf(moduleData.getModuleId()), Integer.valueOf(adViewList.size())}));
                return;
            }
            List<AdInfoBean> adInfoList = module.getAdInfoList();
            if (adInfoList == null || adInfoList.isEmpty()) {
                LogUtils.w(tag, String.format("[vmId:%d][AdModuleInfoBean] moduleId:%d, getAdInfoList() is null or empty!", new Object[]{Integer.valueOf(moduleData.getVirtualModuleId()), Integer.valueOf(moduleData.getModuleId())}), new Throwable());
                return;
            }
            for (AdInfoBean adInfo : adInfoList) {
                if (adInfo == null) {
                    LogUtils.w(tag, "adInfo is null!", new Throwable());
                } else if (LogUtils.isShowLog()) {
                    LogUtils.d(tag, String.format("[vmId:%d] %s", new Object[]{Integer.valueOf(moduleData.getVirtualModuleId()), getLogString(adInfo)}));
                }
            }
        }
    }

    private static String getTypeString(AdModuleInfoBean module) {
        if (module == null || module.getAdType() < 0 || module.getAdType() > 2) {
            return null;
        }
        return new String[]{"离线广告", "在线API广告", "在线SDK广告"}[module.getAdType()];
    }

    private static String getShowStyleString(AdModuleInfoBean module) {
        if (module == null) {
            return "";
        }
        return getShowStyleString(module.getModuleDataItemBean());
    }

    private static String getShowStyleString(BaseModuleDataItemBean moduleDataItemBean) {
        if (BaseModuleDataItemBean.isBannerAd(moduleDataItemBean)) {
            return "banner样式";
        }
        if (BaseModuleDataItemBean.isInterstitialAd(moduleDataItemBean)) {
            return "全屏样式";
        }
        if (BaseModuleDataItemBean.isBannerAd300_250(moduleDataItemBean)) {
            return "banner300*250样式";
        }
        return "native样式";
    }

    public static String getLogString(AdInfoBean adInfo) {
        if (adInfo == null) {
            return "{[AdInfoBean] null}";
        }
        return String.format("{[AdInfoBean] mModuleId:%d, mMapId:%d, mPkgName:%s, mName:%s, mAdPreload:%b, mIconUrl:%s, mBannerUrl:%s, mRemdMsg:%s, mAdUrl:%s}", new Object[]{Integer.valueOf(adInfo.getModuleId()), Integer.valueOf(adInfo.getMapId()), adInfo.getPackageName(), adInfo.getName(), Integer.valueOf(adInfo.getAdPreload()), adInfo.getIcon(), adInfo.getBanner(), adInfo.getRemdMsg(), adInfo.getAdUrl()});
    }

    public static String getSimpleLogString(AdInfoBean adInfo) {
        if (adInfo == null) {
            return "{[AdInfoBean] null}";
        }
        return String.format("{[AdInfoBean] mModuleId:%d, mMapId:%d, mPkgName:%s, mName:%s, mAdPreload:%b}", new Object[]{Integer.valueOf(adInfo.getModuleId()), Integer.valueOf(adInfo.getMapId()), adInfo.getPackageName(), adInfo.getName(), Integer.valueOf(adInfo.getAdPreload())});
    }

    public static String getLogString(BaseModuleDataItemBean moduleData) {
        if (moduleData == null) {
            return "{[BaseModuleDataItemBean] null}";
        }
        String fbIds = moduleData.getFbIds() != null ? moduleData.getFbIds().toString() : "null";
        BaseAdvDataSourceExtInfoBean advDataSourceExt = moduleData.getAdvDataSourceExtInfoBean();
        return String.format("{[BaseModuleDataItemBean] mModuleId:%d, mAdvPositionId:%d, OnlineAdvPositionId:%d, mAdvDataSource:%d, mFbAdvCount:%d, mAdvDataSourceType:%d, mOnlineAdvType:%d, mFbIds:%s, mDataType:%d, mClearflag:%d, {[BaseAdvDataSourceExtInfoBean] mPreloadPerDay:%s}}", new Object[]{Integer.valueOf(moduleData.getModuleId()), Integer.valueOf(moduleData.getAdvPositionId()), Integer.valueOf(moduleData.getOnlineAdvPositionId()), Integer.valueOf(moduleData.getAdvDataSource()), Integer.valueOf(moduleData.getFbAdvCount()), Integer.valueOf(moduleData.getAdvDataSourceType()), Integer.valueOf(moduleData.getOnlineAdvType()), fbIds, Integer.valueOf(moduleData.getDataType()), Integer.valueOf(moduleData.getClearFlag()), advDataSourceExt != null ? "" + advDataSourceExt.getPreloadPerDay() : "getAdvDataSourceExtInfoBean() is null"});
    }

    public static String getSimpleLogString(BaseModuleDataItemBean moduleData) {
        if (moduleData == null) {
            return "{[BaseModuleDataItemBean] null}";
        }
        if (moduleData.isSdkOnlineAdType()) {
            return String.format("{[BaseModuleDataItemBean] 模块id:%d, %s, %s, %s, mAdvPositionId:%d, OnlineAdvPositionId:%d, 广告id:%s, AdCacheTag:%d, AdvDataSource:%d}", new Object[]{Integer.valueOf(moduleData.getModuleId()), getAdDataSourceTypeString(moduleData), getAdDataSourceString(moduleData), getShowStyleString(moduleData), Integer.valueOf(moduleData.getAdvPositionId()), Integer.valueOf(moduleData.getOnlineAdvPositionId()), fbIds2String(moduleData.getFbIds()), Integer.valueOf(moduleData.getAdCacheFlag()), Integer.valueOf(moduleData.getAdvDataSource())});
        }
        return String.format("{[BaseModuleDataItemBean] 模块id:%d, %s, %s, %s, mAdvPositionId:%d, OnlineAdvPositionId:%d, 广告id:%s, AdCacheTag:%d, AdvDataSource:%d}", new Object[]{Integer.valueOf(moduleData.getModuleId()), getAdDataSourceTypeString(moduleData), getAdDataSourceString(moduleData), getShowStyleString(moduleData), Integer.valueOf(moduleData.getAdvPositionId()), Integer.valueOf(moduleData.getOnlineAdvPositionId()), fbIds2String(moduleData.getFbIds()), Integer.valueOf(moduleData.getAdCacheFlag()), Integer.valueOf(moduleData.getAdvDataSource())});
    }

    private static String fbIds2String(String[] fbIdArray) {
        String fbIds = "";
        if (fbIdArray != null && fbIdArray.length > 0) {
            for (String fbId : fbIdArray) {
                if (fbId != null) {
                    if (fbIds.length() > 0) {
                        fbIds = fbIds + "#";
                    }
                    fbIds = fbIds + fbId;
                }
            }
        }
        if (fbIds.length() == 0) {
            return "null";
        }
        return fbIds;
    }

    private static String getAdDataSourceTypeString(BaseModuleDataItemBean moduleData) {
        if (moduleData == null) {
            return null;
        }
        if (moduleData.isSdkOnlineAdType()) {
            return "SDK广告";
        }
        if (moduleData.isOfflineAdType()) {
            return "离线广告";
        }
        if (AdModuleInfoBean.isGomoAd(moduleData)) {
            return "Gomo广告";
        }
        if (AdModuleInfoBean.isS2SAd(moduleData)) {
            return "在线API(GomoS2S)广告";
        }
        return "未知广告源";
    }

    private static String getAdDataSourceString(BaseModuleDataItemBean moduleData) {
        int adDataSource;
        if (moduleData != null && (adDataSource = moduleData.getAdvDataSource()) >= 0 && adDataSource < AD_DATA_SOURCE_DES_ARRAY.length) {
            return AD_DATA_SOURCE_DES_ARRAY[adDataSource];
        }
        return null;
    }

    static {
        sFailStatusMap.put(17, "17->网络错误");
        sFailStatusMap.put(18, "18->请求错误");
        sFailStatusMap.put(19, "19->模块下线");
        sFailStatusMap.put(20, "20->获取广告控制信息列表为空");
        sFailStatusMap.put(21, "21->获取广告信息列表为空");
    }

    public static String getFailStatusDescription(int statusCode) {
        String des = sFailStatusMap.get(statusCode);
        if (des == null) {
            return statusCode + "";
        }
        return des;
    }
}
