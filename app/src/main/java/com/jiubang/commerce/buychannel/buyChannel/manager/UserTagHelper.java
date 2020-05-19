package com.jiubang.commerce.buychannel.buyChannel.manager;

import android.content.Context;
import com.jb.ga0.commerce.util.LogUtils;
import com.jiubang.commerce.ad.AdSdkApi;
import com.jiubang.commerce.ad.bean.AdUserTagInfoBean;
import com.jiubang.commerce.ad.manager.AdSdkManager;
import com.jiubang.commerce.ad.manager.AdSdkSetting;
import com.jiubang.commerce.ad.params.UserTagParams;
import com.jiubang.commerce.buychannel.BuyChannelDataMgr;
import com.jiubang.commerce.buychannel.BuySdkConstants;
import com.jiubang.commerce.buychannel.buyChannel.Interface.OldUserTagListenner;
import com.jiubang.commerce.buychannel.buyChannel.bean.UserTagParam;
import com.jiubang.commerce.buychannel.buyChannel.bean.UserTypeInfo;

public class UserTagHelper {
    private static UserTagHelper sInstance;
    private AdUserTagInfoBean mAdUserTagInfoBean;
    private Context mContext;

    public static UserTagHelper getInstance(Context context) {
        if (sInstance == null) {
            synchronized (UserTagHelper.class) {
                if (sInstance == null) {
                    sInstance = new UserTagHelper(context);
                }
            }
        }
        return sInstance;
    }

    private UserTagHelper(Context context) {
        this.mContext = context != null ? context.getApplicationContext() : null;
    }

    public UserTypeInfo.SecondUserType requestUserTag(final OldUserTagListenner listenner, boolean isNew) {
        UserTagParam params = UserTagParam.jsonStr2Bean(BuyChannelDataMgr.getInstance(this.mContext).getSharedPreferences(this.mContext).getString(BuySdkConstants.USERTAG_PARAMS, (String) null));
        if (params == null) {
            return null;
        }
        AdSdkApi.requestUserTags(this.mContext, new AdSdkManager.IAdvertUserTagResultListener() {
            public void onAdRequestSuccess(AdUserTagInfoBean adUserTagInfoBean) {
                LogUtils.i("buychannelsdk", "[UserTagHelper::requestUserTag]广告SDK获取用户标签成功");
                LogUtils.i("AutoTestAFib", "广告SDK获取用户标签成功");
                if (listenner != null) {
                    listenner.requestUserTagSuccess();
                }
            }

            public void onAdRequestFail(int i) {
                LogUtils.i("buychannelsdk", "[UserTagHelper::requestUserTag]广告SDK获取用户标签失败,错误代码：" + i);
                LogUtils.i("AutoTestAFib", "广告SDK获取用户标签失败,错误代码：" + i);
            }
        }, new UserTagParams(params.mGoId, params.mGoogleId, params.mChannel, params.mProductKey, params.mAccessKey), isNew);
        if (isNew) {
            this.mAdUserTagInfoBean = AdSdkSetting.getInstance(this.mContext).getUserTagInfoBean();
        } else {
            this.mAdUserTagInfoBean = AdSdkSetting.getInstance(this.mContext).getOldUserTagInfoBean();
        }
        if (this.mAdUserTagInfoBean != null) {
            LogUtils.i("buychannelsdk", "[UserTagHelper::requestUserTag]标签列表内容为," + this.mAdUserTagInfoBean.getUserTags().toString());
            LogUtils.i("AutoTestAFib", "标签列表内容为," + this.mAdUserTagInfoBean.getUserTags().toString());
        }
        if (this.mAdUserTagInfoBean.isTag("C2_APK")) {
            if (this.mAdUserTagInfoBean != null) {
                LogUtils.i("buychannelsdk", "[UserTagHelper::requestUserTag]通过标签识别为APK买量，标签列表内容为," + this.mAdUserTagInfoBean.getUserTags().toString());
            }
            return UserTypeInfo.SecondUserType.APK_USERBUY;
        } else if (this.mAdUserTagInfoBean.isTag("C2_GMFB")) {
            if (this.mAdUserTagInfoBean != null) {
                LogUtils.i("buychannelsdk", "[UserTagHelper::requestUserTag]通过标签识别为FB自投，标签列表内容为," + this.mAdUserTagInfoBean.getUserTags().toString());
            }
            return UserTypeInfo.SecondUserType.FB_AUTO;
        } else if (this.mAdUserTagInfoBean.isTag("C2_FB")) {
            if (this.mAdUserTagInfoBean != null) {
                LogUtils.i("buychannelsdk", "[UserTagHelper::requestUserTag]通过标签识别为FB非自投，标签列表内容为," + this.mAdUserTagInfoBean.getUserTags().toString());
            }
            return UserTypeInfo.SecondUserType.FB_NOTAUTO;
        } else if (this.mAdUserTagInfoBean.isTag("C2_GMADW")) {
            if (this.mAdUserTagInfoBean != null) {
                LogUtils.i("buychannelsdk", "[UserTagHelper::requestUserTag]通过标签识别为Adwords自投，标签列表内容为," + this.mAdUserTagInfoBean.getUserTags().toString());
            }
            return UserTypeInfo.SecondUserType.ADWORDS_AUTO;
        } else if (this.mAdUserTagInfoBean.isTag("C2_ADW")) {
            if (this.mAdUserTagInfoBean != null) {
                LogUtils.i("buychannelsdk", "[UserTagHelper::requestUserTag]通过标签识别为Adwords非自投，标签列表内容为," + this.mAdUserTagInfoBean.getUserTags().toString());
            }
            return UserTypeInfo.SecondUserType.ADWORDS_NOTAUTO;
        } else if (this.mAdUserTagInfoBean.isTag("C2_GA")) {
            if (this.mAdUserTagInfoBean != null) {
                LogUtils.i("buychannelsdk", "[UserTagHelper::requestUserTag]通过标签识别为GA买量，标签列表内容为," + this.mAdUserTagInfoBean.getUserTags().toString());
            }
            return UserTypeInfo.SecondUserType.GA_USERBUY;
        } else if (this.mAdUserTagInfoBean.isTag("C2_3G")) {
            if (this.mAdUserTagInfoBean != null) {
                LogUtils.i("buychannelsdk", "[UserTagHelper::requestUserTag]通过标签识别为自然带量，标签列表内容为," + this.mAdUserTagInfoBean.getUserTags().toString());
            }
            return UserTypeInfo.SecondUserType.WITHCOUNT_ORGNIC;
        } else if (this.mAdUserTagInfoBean.isTag("C2")) {
            if (this.mAdUserTagInfoBean != null) {
                LogUtils.i("buychannelsdk", "[UserTagHelper::requestUserTag]通过标签识别为买量用户,识别不出具体二级类型，标签列表内容为," + this.mAdUserTagInfoBean.getUserTags().toString());
            }
            return UserTypeInfo.SecondUserType.UNKNOWN_USERBUY;
        } else {
            LogUtils.i("buychannelsdk", "[UserTagHelper::requestUserTag]通过标签识为非买量用户");
            return null;
        }
    }
}
