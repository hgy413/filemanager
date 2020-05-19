package com.jiubang.commerce.ad.url;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import com.jb.ga0.commerce.util.LogUtils;
import com.jiubang.commerce.ad.bean.AdInfoBean;
import com.jiubang.commerce.ad.http.bean.ParamsBean;
import com.jiubang.commerce.ad.params.PresolveParams;
import com.jiubang.commerce.utils.NetworkUtils;
import java.util.ArrayList;
import java.util.List;

public class AdUrlPreParseTask extends AsyncTask<Integer, String, List<String>> {
    protected static final int URL_TYPE_NORMAL = 0;
    protected static final int URL_TYPE_REDIRECT = 1;
    private List<AdInfoBean> mAdPublicInfoList;
    private AdvanceParseRedirectUrl mAdvanceParseRedirectUrl;
    private Context mContext;
    private String mModuleId;
    private PresolveParams mPresolveParams;
    private ExecuteTaskStateListener mTaskStateListener;
    private boolean mUseCache;

    public interface ExecuteTaskStateListener {
        void onExecuteTaskComplete(Context context);
    }

    public AdUrlPreParseTask(Context context, String moduleId, List<AdInfoBean> adPublicInfoList, boolean useCache, PresolveParams preParams, ExecuteTaskStateListener listener) {
        initData(context, moduleId, adPublicInfoList, useCache, preParams, listener);
    }

    private void initData(Context context, String moduleId, List<AdInfoBean> adPublicInfoList, boolean useCache, PresolveParams preParams, ExecuteTaskStateListener listener) {
        this.mContext = context;
        this.mModuleId = moduleId;
        this.mAdPublicInfoList = adPublicInfoList;
        this.mUseCache = useCache;
        this.mTaskStateListener = listener;
        this.mPresolveParams = preParams;
        this.mAdvanceParseRedirectUrl = AdvanceParseRedirectUrl.getInstance(this.mContext);
    }

    /* access modifiers changed from: protected */
    public void onPreExecute() {
        super.onPreExecute();
    }

    /* access modifiers changed from: protected */
    public List<String> doInBackground(Integer... params) {
        String str;
        List<String> resultList = new ArrayList<>();
        String parsedAdUrlString = null;
        while (this.mAdvanceParseRedirectUrl != null && this.mAdPublicInfoList != null && this.mAdPublicInfoList.size() > 0) {
            try {
                AdInfoBean adPublicInfoBean = this.mAdPublicInfoList.remove(0);
                boolean isAgainPreLoad = false;
                boolean condition = true;
                if (this.mUseCache) {
                    condition = TextUtils.isEmpty(this.mAdvanceParseRedirectUrl.getFinalUrl(adPublicInfoBean.getAdUrl(), this.mPresolveParams.mCustomCacheDuration));
                }
                if (adPublicInfoBean != null && !TextUtils.isEmpty(adPublicInfoBean.getAdUrl()) && condition && !this.mAdvanceParseRedirectUrl.isParsing(adPublicInfoBean.getAdUrl())) {
                    this.mAdvanceParseRedirectUrl.removeRedirectUrl(adPublicInfoBean.getAdUrl());
                    int uaType = this.mPresolveParams.mUAType != -1 ? this.mPresolveParams.mUAType : adPublicInfoBean.getUAType();
                    ParamsBean paramsBean = new ParamsBean();
                    paramsBean.setUASwitcher(adPublicInfoBean.getUASwitcher());
                    paramsBean.setFinalGpJump(adPublicInfoBean.getmFinalGpJump());
                    paramsBean.setUAType(uaType);
                    parsedAdUrlString = AdRedirectUrlUtils.getHttpRedirectUrlFromLocation(this.mContext, paramsBean, this.mModuleId, String.valueOf(adPublicInfoBean.getMapId()), String.valueOf(adPublicInfoBean.getAdId()), this.mPresolveParams.mRepeatClickEnable ? AdRedirectUrlUtils.handlePreloadAdUrl(adPublicInfoBean.getAdUrl()) : adPublicInfoBean.getAdUrl());
                    if (this.mUseCache) {
                        this.mAdvanceParseRedirectUrl.saveFinalUrl(adPublicInfoBean.getPackageName(), adPublicInfoBean.getAdUrl(), parsedAdUrlString);
                    }
                    switch (this.mPresolveParams.mSendReferBroadcast) {
                        case DEPENDS:
                            if (adPublicInfoBean.shouldSendReferBroadcast()) {
                                ReferrerUtil.sendGPUrlBroadcast(this.mContext, parsedAdUrlString);
                                break;
                            }
                            break;
                        case ALWAYS:
                            ReferrerUtil.sendGPUrlBroadcast(this.mContext, parsedAdUrlString);
                            break;
                    }
                    resultList.add(parsedAdUrlString);
                    isAgainPreLoad = true;
                }
                if (LogUtils.isShowLog()) {
                    LogUtils.w("Ad_SDK", "AdUrlPreParseTask.doInBackground(剩余要解析数量：" + this.mAdPublicInfoList.size() + ", 解析广告名：" + (adPublicInfoBean != null ? adPublicInfoBean.getName() : "") + ", 是否刚进行预加载:" + isAgainPreLoad + ", 解析前地址：" + (adPublicInfoBean != null ? adPublicInfoBean.getAdUrl() : "") + ",解析后地址：" + ((!TextUtils.isEmpty(parsedAdUrlString) || adPublicInfoBean == null) ? parsedAdUrlString : this.mAdvanceParseRedirectUrl.getFinalUrl(adPublicInfoBean.getAdUrl(), new long[0])) + ")");
                }
            } catch (Exception e) {
                e.printStackTrace();
                StringBuilder append = new StringBuilder().append("AdUrlPreParseTask.doInBackground(fail, ");
                if (e != null) {
                    str = e.getMessage();
                } else {
                    str = "";
                }
                LogUtils.e("Ad_SDK", append.append(str).append(")").toString());
            }
        }
        return resultList;
    }

    /* access modifiers changed from: protected */
    public void onPostExecute(List<String> resultList) {
        super.onPostExecute(resultList);
        if (this.mTaskStateListener != null) {
            try {
                this.mTaskStateListener.onExecuteTaskComplete(this.mContext);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (LogUtils.isShowLog()) {
            LogUtils.d("Ad_SDK", "AdUrlPreParseTask.onPostExecute(complete, " + (resultList != null ? resultList.size() : -1) + ")");
        }
        recycle();
    }

    private void recycle() {
        if (this.mTaskStateListener != null) {
            this.mTaskStateListener = null;
        }
        if (this.mAdPublicInfoList != null) {
            this.mAdPublicInfoList.clear();
            this.mAdPublicInfoList = null;
        }
        if (this.mAdvanceParseRedirectUrl != null) {
            this.mAdvanceParseRedirectUrl = null;
        }
    }

    public static boolean startExecuteTask(Context context, String moduleId, List<AdInfoBean> adInfoList, boolean useCache, PresolveParams preParams, ExecuteTaskStateListener listener) {
        if (NetworkUtils.isNetworkOK(context)) {
            new AdUrlPreParseTask(context, moduleId, adInfoList, useCache, preParams, listener).execute(new Integer[]{0});
            return true;
        }
        LogUtils.e("Ad_SDK", "startExecuteTaskNew(preloadUrl, no network)");
        if (listener != null) {
            listener.onExecuteTaskComplete(context);
        }
        if (adInfoList != null && adInfoList.size() > 0) {
            AdvanceParseRedirectUrl parseRedirectUrl = AdvanceParseRedirectUrl.getInstance(context);
            for (int index = 0; index < adInfoList.size(); index++) {
                AdInfoBean adInfoBean = adInfoList.get(index);
                if (adInfoBean != null && !TextUtils.isEmpty(adInfoBean.getAdUrl()) && TextUtils.isEmpty(parseRedirectUrl.getFinalUrl(adInfoBean.getAdUrl(), new long[0]))) {
                    new ParseAdUrlResponseBean(1, 2, adInfoBean.getAdUrl(), "network is not ok", 0).uploadParseUrlStatusStatistic(context, moduleId, String.valueOf(adInfoBean.getMapId()), String.valueOf(adInfoBean.getAdId()));
                }
            }
        }
        return false;
    }
}
