package com.jiubang.commerce.ad.url;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.widget.Toast;
import com.jb.ga0.commerce.util.LogUtils;
import com.jiubang.commerce.ad.ResourcesProvider;
import com.jiubang.commerce.ad.http.bean.ParamsBean;
import com.jiubang.commerce.ad.url.AdRedirectJumpTask;
import com.jiubang.commerce.utils.GoogleMarketUtils;

public class AdUrlPreParseLoadingActivity extends Activity {
    public static final String INTENT_KEY_AD_ID = "aId";
    public static final String INTENT_KEY_DOWNLOAD_URL = "downloadUrl";
    public static final String INTENT_KEY_IS_OPEN_BROWSER = "isOpenBrowser";
    public static final String INTENT_KEY_IS_PARSE_URL = "isParseUrl";
    public static final String INTENT_KEY_IS_SHOW_FLOAT_WINDOW = "isShowFloatWindow";
    public static final String INTENT_KEY_MAP_ID = "mapId";
    public static final String INTENT_KEY_MODULE_ID = "moduleId";
    public static final String INTENT_KEY_PARAMSBEAN = "paramsBean";
    public static final String INTENT_KEY_PKG = "pkgName";
    public static final String INTENT_KEY_REDIRECT_URL = "redirectUrl";
    public static final String INTENT_KEY_TIME_OUT_DURATION = "timeOutDuration";
    private static AdUrlPreParseLoadingActivity sInstance;
    private String mAId;
    /* access modifiers changed from: private */
    public Runnable mDismissTipsDialogRunnable = new Runnable() {
        public void run() {
            if (AdUrlPreParseLoadingActivity.this.mHander == null || AdUrlPreParseLoadingActivity.this.isFinishing()) {
                AdUrlPreParseLoadingActivity.this.finish();
                return;
            }
            AdUrlPreParseLoadingActivity.this.executeTaskFailure();
            AdUrlPreParseLoadingActivity.this.finish();
        }
    };
    private String mDownloadUrl;
    /* access modifiers changed from: private */
    public Handler mHander = null;
    /* access modifiers changed from: private */
    public boolean mIsOpenBrowser;
    private boolean mIsParseUrl;
    private boolean mIsShowFloatWindow;
    private String mMapId;
    private String mModuleId;
    private ParamsBean mParamsBean;
    /* access modifiers changed from: private */
    public String mPkgName;
    /* access modifiers changed from: private */
    public String mRedirectUrl;
    private AdRedirectJumpTask.ExecuteTaskStateListener mTaskStateListener = new AdRedirectJumpTask.ExecuteTaskStateListener() {
        public void onExecuteTaskComplete(Context context, int stateCode, String pkgName, String adUrl, String redirectUrl, String googlePalyUrl, long startTime, boolean isShowFloatWindow) {
            if (LogUtils.isShowLog()) {
                LogUtils.d("Ad_SDK", "AdRedirectLoadingActivity.onExecuteTaskComplete(" + stateCode + ", " + adUrl + ", " + googlePalyUrl + ", " + startTime + ", " + isShowFloatWindow + ")");
            }
            AdvanceParseRedirectUrl.getInstance(context).saveFinalUrl(AdUrlPreParseLoadingActivity.this.mPkgName, AdUrlPreParseLoadingActivity.this.mRedirectUrl, adUrl);
            if (AdUrlPreParseLoadingActivity.this.mHander == null || AdUrlPreParseLoadingActivity.this.isFinishing()) {
                AdUrlPreParseLoadingActivity.this.finish();
                return;
            }
            AdUrlPreParseLoadingActivity.this.mHander.removeCallbacks(AdUrlPreParseLoadingActivity.this.mDismissTipsDialogRunnable);
            if (stateCode == 18) {
                Toast.makeText(context, ResourcesProvider.getInstance(context).getString("desksetting_net_error"), 1).show();
            } else if (stateCode != 16 || TextUtils.isEmpty(adUrl)) {
                AdUrlPreParseLoadingActivity.this.executeTaskFailure();
            } else {
                GoogleMarketUtils.gotoGoogleMarket(AdUrlPreParseLoadingActivity.this, adUrl, AdUrlPreParseLoadingActivity.this.mIsOpenBrowser, isShowFloatWindow);
            }
            AdUrlPreParseLoadingActivity.this.finish();
        }

        public void onRequestTimeOut(Context context, String googlePalyUrl, String redirectUrl, boolean isShowFloatWindow) {
            if (LogUtils.isShowLog()) {
                LogUtils.d("Ad_SDK", "AdRedirectLoadingActivity.onRequestTimeOut(" + context + ", " + googlePalyUrl + ", " + isShowFloatWindow + ")");
            }
            AdUrlPreParseLoadingActivity.this.executeTaskFailure();
        }
    };

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(ResourcesProvider.getInstance(this).getLayoutId("ad_jump_tips_layout"));
        Intent intent = getIntent();
        this.mIsParseUrl = intent != null ? intent.getBooleanExtra(INTENT_KEY_IS_PARSE_URL, true) : true;
        if (!this.mIsParseUrl) {
            sInstance = this;
            return;
        }
        this.mParamsBean = intent != null ? (ParamsBean) intent.getSerializableExtra(INTENT_KEY_PARAMSBEAN) : null;
        this.mPkgName = intent != null ? intent.getStringExtra(INTENT_KEY_PKG) : "-1";
        this.mModuleId = intent != null ? intent.getStringExtra("moduleId") : "-1";
        this.mMapId = intent != null ? intent.getStringExtra(INTENT_KEY_MAP_ID) : "-1";
        this.mAId = intent != null ? intent.getStringExtra(INTENT_KEY_AD_ID) : "-1";
        this.mDownloadUrl = intent != null ? intent.getStringExtra(INTENT_KEY_DOWNLOAD_URL) : null;
        this.mRedirectUrl = intent != null ? intent.getStringExtra("redirectUrl") : null;
        long requestTimeOutDuration = intent != null ? intent.getLongExtra(INTENT_KEY_TIME_OUT_DURATION, AppDetailsJumpUtil.sREQUEST_TIME_OUT_DURATION) : AppDetailsJumpUtil.sREQUEST_TIME_OUT_DURATION;
        if (requestTimeOutDuration <= 0) {
            requestTimeOutDuration = AppDetailsJumpUtil.sREQUEST_TIME_OUT_DURATION;
        }
        this.mIsShowFloatWindow = intent != null ? intent.getBooleanExtra(INTENT_KEY_IS_SHOW_FLOAT_WINDOW, true) : true;
        this.mIsOpenBrowser = intent != null ? intent.getBooleanExtra(INTENT_KEY_IS_OPEN_BROWSER, true) : true;
        if (TextUtils.isEmpty(this.mRedirectUrl)) {
            finish();
            return;
        }
        this.mHander = new Handler();
        if (AdRedirectJumpTask.startExecuteTask(getApplicationContext(), this.mParamsBean, this.mPkgName, this.mModuleId, this.mMapId, this.mAId, this.mRedirectUrl, this.mDownloadUrl, requestTimeOutDuration, this.mIsShowFloatWindow, false, "", this.mTaskStateListener)) {
            this.mHander.removeCallbacks(this.mDismissTipsDialogRunnable);
            this.mHander.postDelayed(this.mDismissTipsDialogRunnable, requestTimeOutDuration);
            return;
        }
        finish();
    }

    /* access modifiers changed from: private */
    public void executeTaskFailure() {
        if (TextUtils.isEmpty(this.mDownloadUrl)) {
            Toast.makeText(getApplicationContext(), ResourcesProvider.getInstance(getApplicationContext()).getString("desksetting_net_error"), 1).show();
        } else {
            GoogleMarketUtils.gotoGoogleMarket(this, TextUtils.isEmpty(this.mRedirectUrl) ? this.mDownloadUrl : this.mRedirectUrl, this.mIsOpenBrowser, this.mIsShowFloatWindow);
        }
    }

    public void onBackPressed() {
        super.onBackPressed();
        if (this.mHander != null && this.mDismissTipsDialogRunnable != null) {
            this.mHander.removeCallbacks(this.mDismissTipsDialogRunnable);
        }
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        super.onDestroy();
        this.mHander = null;
    }

    public static void finishActivity() {
        if (sInstance != null) {
            sInstance.finish();
            sInstance = null;
        }
    }
}
