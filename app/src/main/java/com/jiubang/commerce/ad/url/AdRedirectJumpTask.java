package com.jiubang.commerce.ad.url;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.text.TextUtils;
import android.widget.Toast;
import com.jiubang.commerce.ad.ResourcesProvider;
import com.jiubang.commerce.ad.http.bean.ParamsBean;
import com.jiubang.commerce.utils.NetworkUtils;

public class AdRedirectJumpTask extends AsyncTask<Integer, String, String> {
    protected static final int URL_TYPE_NORMAL = 0;
    protected static final int URL_TYPE_REDIRECT = 1;
    private String mAId;
    /* access modifiers changed from: private */
    public Context mContext;
    /* access modifiers changed from: private */
    public String mGooglePlayUrl;
    private Handler mHander;
    /* access modifiers changed from: private */
    public boolean mIsShowFloatWindow;
    private boolean mIsShowToast;
    private String mMapId;
    private String mModuleId;
    private ParamsBean mParamsBean;
    private String mPkgName;
    /* access modifiers changed from: private */
    public String mRedirectUrl;
    private Runnable mRequestTimeOutRunnable = new Runnable() {
        public void run() {
            if (AdRedirectJumpTask.this.mTaskStateListener != null) {
                AdRedirectJumpTask.this.mTaskStateListener.onRequestTimeOut(AdRedirectJumpTask.this.mContext, AdRedirectJumpTask.this.mGooglePlayUrl, AdRedirectJumpTask.this.mRedirectUrl, AdRedirectJumpTask.this.mIsShowFloatWindow);
            }
            AdRedirectJumpTask.this.recycle();
        }
    };
    private long mStartTime;
    /* access modifiers changed from: private */
    public ExecuteTaskStateListener mTaskStateListener;
    private String mToastMessageStr;

    public interface ExecuteTaskStateListener {
        public static final int STATE_FAILURE = 17;
        public static final int STATE_NO_NETWORK = 18;
        public static final int STATE_SUCCESS = 16;

        void onExecuteTaskComplete(Context context, int i, String str, String str2, String str3, String str4, long j, boolean z);

        void onRequestTimeOut(Context context, String str, String str2, boolean z);
    }

    public AdRedirectJumpTask(Context context, ParamsBean paramsBean, String pkgName, String moduleId, String mapId, String aId, String redirectUrl, String googlePalyUrl, long timeOutDuration, boolean isShowFloatWindow, boolean isShowToast, String toastMessageStr, ExecuteTaskStateListener listener) {
        this.mContext = context;
        this.mParamsBean = paramsBean;
        this.mPkgName = pkgName;
        this.mModuleId = moduleId;
        this.mMapId = mapId;
        this.mAId = aId;
        this.mRedirectUrl = redirectUrl;
        this.mGooglePlayUrl = googlePalyUrl;
        this.mStartTime = System.currentTimeMillis();
        this.mIsShowFloatWindow = isShowFloatWindow;
        this.mIsShowToast = isShowToast;
        this.mToastMessageStr = toastMessageStr;
        this.mTaskStateListener = listener;
        if (timeOutDuration > 0) {
            this.mHander = new Handler();
            this.mHander.postDelayed(this.mRequestTimeOutRunnable, timeOutDuration);
        }
    }

    /* access modifiers changed from: protected */
    public void onPreExecute() {
        super.onPreExecute();
        if (!this.mIsShowToast) {
            return;
        }
        if (!TextUtils.isEmpty(this.mToastMessageStr)) {
            Toast.makeText(this.mContext, this.mToastMessageStr, 1).show();
        } else {
            Toast.makeText(this.mContext, ResourcesProvider.getInstance(this.mContext).getString("recommended_click_tip"), 1).show();
        }
    }

    /* access modifiers changed from: protected */
    public String doInBackground(Integer... params) {
        return AdRedirectUrlUtils.getHttpRedirectUrlFromLocation(this.mContext, this.mParamsBean, this.mModuleId, this.mMapId, this.mAId, this.mRedirectUrl);
    }

    /* access modifiers changed from: protected */
    public void onPostExecute(String result) {
        super.onPostExecute(result);
        if (this.mHander != null) {
            this.mHander.removeCallbacks(this.mRequestTimeOutRunnable);
        }
        if (this.mTaskStateListener != null) {
            try {
                if (TextUtils.isEmpty(result)) {
                    this.mTaskStateListener.onExecuteTaskComplete(this.mContext, 17, this.mPkgName, result, this.mRedirectUrl, this.mGooglePlayUrl, this.mStartTime, this.mIsShowFloatWindow);
                } else {
                    this.mTaskStateListener.onExecuteTaskComplete(this.mContext, 16, this.mPkgName, result, this.mRedirectUrl, this.mGooglePlayUrl, this.mStartTime, this.mIsShowFloatWindow);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        recycle();
    }

    /* access modifiers changed from: private */
    public void recycle() {
        if (this.mTaskStateListener != null) {
            this.mTaskStateListener = null;
        }
        if (this.mHander != null) {
            this.mHander = null;
        }
    }

    public static boolean isRedirectUrl(String url, int isRedirectType) {
        if (TextUtils.isEmpty(url) || isRedirectType != 1) {
            return false;
        }
        return true;
    }

    public static boolean startExecuteTask(Context context, ParamsBean paramsBean, String pkgName, String moduleId, String mapId, String aId, String redirectUrl, String googlePlayUrl, long timeOutDuration, boolean isShowFloatWindow, boolean isShowToast, String toastMessageStr, ExecuteTaskStateListener listener) {
        if (NetworkUtils.isNetworkOK(context)) {
            new AdRedirectJumpTask(context, paramsBean, pkgName, moduleId, mapId, aId, redirectUrl, googlePlayUrl, timeOutDuration, isShowFloatWindow, isShowToast, toastMessageStr, listener).execute(new Integer[]{0});
            return true;
        }
        if (listener != null) {
            listener.onExecuteTaskComplete(context, 18, pkgName, "", redirectUrl, googlePlayUrl, System.currentTimeMillis(), isShowFloatWindow);
        }
        new ParseAdUrlResponseBean(1, 2, redirectUrl, "network is not ok", 0).uploadParseUrlStatusStatistic(context, moduleId, mapId, aId);
        return false;
    }
}
