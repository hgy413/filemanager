package com.jiubang.commerce.ad.window;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.text.Html;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.jb.ga0.commerce.util.LogUtils;
import com.jiubang.commerce.ad.AdSdkApi;
import com.jiubang.commerce.ad.AdSdkContants;
import com.jiubang.commerce.ad.ResourcesProvider;
import com.jiubang.commerce.ad.bean.AdInfoBean;
import com.jiubang.commerce.ad.bean.AdModuleInfoBean;
import com.jiubang.commerce.ad.manager.AdSdkManager;
import com.jiubang.commerce.ad.params.AdSdkParamsBuilder;
import com.jiubang.commerce.ad.window.activation.ActivationRecommendAdapter;
import com.jiubang.commerce.statistics.AdSdkOperationStatistic;
import com.jiubang.commerce.thread.AdSdkThreadExecutorProxy;
import com.jiubang.commerce.utils.AppUtils;
import com.jiubang.commerce.utils.DrawUtils;
import com.jiubang.commerce.utils.FileUtils;
import com.jiubang.commerce.utils.NetworkUtils;
import com.jiubang.commerce.utils.StringUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ActivationGuideWindowManager {
    public static final int ENTRANCE_ID_FLOATINGWINDOW = 16;
    public static final String EXTRA_ENTRANCE_ID = "entrance_id";
    private static ActivationGuideWindowManager sInstance;
    private View mActivationGuideMainView;
    /* access modifiers changed from: private */
    public List<AdInfoBean> mAdInfoList;
    /* access modifiers changed from: private */
    public View mCancelBtn;
    /* access modifiers changed from: private */
    public Context mContext;
    /* access modifiers changed from: private */
    public String mCurPackageName;
    public boolean mFloatWindowShowing;
    private Handler mHandler;
    /* access modifiers changed from: private */
    public TextView mInstalledAppNameTextView;
    /* access modifiers changed from: private */
    public volatile boolean mIsRefreshing;
    private AdSdkManager.ILoadAdvertDataListener mLoadAdvertDataListener = new AdSdkManager.ILoadAdvertDataListener() {
        public void onAdInfoFinish(boolean isCache, final AdModuleInfoBean adModuleInfoBean) {
            AdSdkThreadExecutorProxy.runOnMainThread(new Runnable() {
                public void run() {
                    ActivationGuideWindowManager.this.setIsRefreshing(false);
                    ActivationGuideWindowManager.this.processAdData(adModuleInfoBean);
                }
            });
        }

        public void onAdImageFinish(AdModuleInfoBean adModuleInfoBean) {
        }

        public void onAdFail(int statusCode) {
            AdSdkThreadExecutorProxy.runOnMainThread(new Runnable() {
                public void run() {
                    ActivationGuideWindowManager.this.setIsRefreshing(false);
                }
            });
        }

        public void onAdShowed(Object adViewObj) {
        }

        public void onAdClicked(Object adViewObj) {
        }

        public void onAdClosed(Object adViewObj) {
        }
    };
    /* access modifiers changed from: private */
    public View mOpenBtn;
    /* access modifiers changed from: private */
    public Map<Integer, String> mPreActivateDataMap;
    /* access modifiers changed from: private */
    public ActivationRecommendAdapter mRecommendAdapter;
    /* access modifiers changed from: private */
    public GridView mRecommendGrid;
    /* access modifiers changed from: private */
    public View mRefreshBtn;
    /* access modifiers changed from: private */
    public ProgressBar mRefreshProgressBar;
    private WindowManager.LayoutParams mSmallWindowParams;
    /* access modifiers changed from: private */
    public LinearLayout mTopLayout;
    private WindowManager mWindowManager;

    private ActivationGuideWindowManager(Context context) {
        this.mContext = context != null ? context.getApplicationContext() : null;
        this.mHandler = new Handler();
    }

    public static synchronized ActivationGuideWindowManager getInstance(Context context) {
        ActivationGuideWindowManager activationGuideWindowManager;
        synchronized (ActivationGuideWindowManager.class) {
            if (sInstance == null) {
                sInstance = new ActivationGuideWindowManager(context);
            }
            activationGuideWindowManager = sInstance;
        }
        return activationGuideWindowManager;
    }

    public static String getSAVE_SHOW_ACTIVATION_GUIDE_TIME_FILE_PATH() {
        return AdSdkContants.getADVERT_CONFIG_PATH() + "show_activation_time".hashCode();
    }

    private void initView() {
        this.mActivationGuideMainView = new ActivitionGuideMainView(this.mContext);
    }

    /* access modifiers changed from: private */
    public void setIsRefreshing(boolean isRefreshing) {
        this.mIsRefreshing = isRefreshing;
        if (this.mRefreshProgressBar != null && this.mRefreshBtn != null && this.mHandler != null) {
            try {
                this.mHandler.post(new Runnable() {
                    public void run() {
                        int i;
                        int i2 = 8;
                        ProgressBar access$100 = ActivationGuideWindowManager.this.mRefreshProgressBar;
                        if (ActivationGuideWindowManager.this.mIsRefreshing) {
                            i = 0;
                        } else {
                            i = 8;
                        }
                        access$100.setVisibility(i);
                        View access$200 = ActivationGuideWindowManager.this.mRefreshBtn;
                        if (!ActivationGuideWindowManager.this.mIsRefreshing) {
                            i2 = 0;
                        }
                        access$200.setVisibility(i2);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void loadNextData() {
        loadRecommendData(this.mContext, this.mCurPackageName);
    }

    public void showActivationGuideWindow(Context context, String installPackageName) {
        if (context != null && !TextUtils.isEmpty(installPackageName)) {
            try {
                String showActivitionTimeStr = FileUtils.readFileToString(getSAVE_SHOW_ACTIVATION_GUIDE_TIME_FILE_PATH());
                if (TextUtils.isEmpty(showActivitionTimeStr) || System.currentTimeMillis() - StringUtils.toLong(showActivitionTimeStr, 0L).longValue() >= 300000) {
                    this.mCurPackageName = installPackageName;
                    WindowManager windowManager = getWindowManager(context);
                    if (this.mActivationGuideMainView == null) {
                        initView();
                        if (this.mSmallWindowParams == null) {
                            this.mSmallWindowParams = new WindowManager.LayoutParams();
                            this.mSmallWindowParams.type = 2003;
                            this.mSmallWindowParams.format = 1;
                            this.mSmallWindowParams.gravity = 83;
                            this.mSmallWindowParams.height = -2;
                        }
                    }
                    setInstallAppName(installPackageName);
                    setIsRefreshing(false);
                    try {
                        windowManager.addView(this.mActivationGuideMainView, this.mSmallWindowParams);
                    } catch (Exception e) {
                        LogUtils.e("Ad_SDK", "show ActivationGuide Window error::->" + e.getMessage());
                        windowManager.removeView(this.mActivationGuideMainView);
                        windowManager.addView(this.mActivationGuideMainView, this.mSmallWindowParams);
                    }
                    this.mFloatWindowShowing = true;
                    if (!loadRecommendData(context, installPackageName)) {
                        if (this.mTopLayout != null) {
                            this.mTopLayout.setVisibility(8);
                        }
                    } else if (!(this.mTopLayout == null || this.mTopLayout.getVisibility() == 0)) {
                        this.mTopLayout.setVisibility(0);
                    }
                    FileUtils.saveStringToSDFile(String.valueOf(System.currentTimeMillis()), getSAVE_SHOW_ACTIVATION_GUIDE_TIME_FILE_PATH());
                    this.mPreActivateDataMap = AdSdkOperationStatistic.getPreActivateData(context, installPackageName);
                    AdSdkOperationStatistic.uploadAdShowActivationGuideStaticstic(context, AdSdkOperationStatistic.ACTIVATION_GUIDE_WINDOW_AV_F000, this.mPreActivateDataMap != null ? this.mPreActivateDataMap.get(1) : "", this.mPreActivateDataMap != null ? this.mPreActivateDataMap.get(6) : "", this.mPreActivateDataMap != null ? this.mPreActivateDataMap.get(8) : "", installPackageName);
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }

    public void hideActivationGuideWindow() {
        this.mFloatWindowShowing = false;
        if (this.mActivationGuideMainView != null) {
            getWindowManager(this.mContext).removeView(this.mActivationGuideMainView);
            this.mActivationGuideMainView = null;
            this.mSmallWindowParams = null;
        }
        FileUtils.deleteFile(getSAVE_SHOW_ACTIVATION_GUIDE_TIME_FILE_PATH());
    }

    private WindowManager getWindowManager(Context context) {
        if (this.mWindowManager == null) {
            this.mWindowManager = (WindowManager) context.getSystemService("window");
        }
        return this.mWindowManager;
    }

    private void setInstallAppName(String packageName) {
        if (this.mInstalledAppNameTextView != null && !TextUtils.isEmpty(packageName)) {
            String label = AppUtils.getAppLabel(this.mContext, packageName);
            if (!TextUtils.isEmpty(label)) {
                this.mInstalledAppNameTextView.setText(Html.fromHtml("<B>" + label + "</B>" + ResourcesProvider.getInstance(this.mContext).getString("ad_activation_guide_dialog_installed")));
            }
        }
    }

    public boolean loadRecommendData(Context context, String curPackageName) {
        if (!NetworkUtils.isNetworkOK(context)) {
            return false;
        }
        setIsRefreshing(true);
        AdSdkApi.loadAdBean(new AdSdkParamsBuilder.Builder(context, 146, AdSdkOperationStatistic.INTERNAL_TABCATEGORY, this.mLoadAdvertDataListener).build());
        return true;
    }

    /* access modifiers changed from: private */
    public void processAdData(AdModuleInfoBean adModuleInfoBean) {
        List<AdInfoBean> adInfoList = adModuleInfoBean != null ? adModuleInfoBean.getAdInfoList() : null;
        if (adInfoList != null && !adInfoList.isEmpty()) {
            if (this.mAdInfoList == null) {
                this.mAdInfoList = new ArrayList();
            } else {
                this.mAdInfoList.clear();
            }
            this.mAdInfoList.addAll(adInfoList);
            this.mRecommendAdapter.updateData(this.mAdInfoList);
        }
    }

    class ActivitionGuideMainView extends LinearLayout implements View.OnClickListener {
        public ActivitionGuideMainView(Context context) {
            super(context);
            initView();
        }

        private void initView() {
            LayoutInflater.from(ActivationGuideWindowManager.this.mContext).inflate(ResourcesProvider.getInstance(ActivationGuideWindowManager.this.mContext).getLayoutId("ad_activation_guide_dialog_layout"), this);
            LinearLayout unused = ActivationGuideWindowManager.this.mTopLayout = (LinearLayout) findViewById(ResourcesProvider.getInstance(ActivationGuideWindowManager.this.mContext).getId("ad_activation_top_layout"));
            GridView unused2 = ActivationGuideWindowManager.this.mRecommendGrid = (GridView) findViewById(ResourcesProvider.getInstance(ActivationGuideWindowManager.this.mContext).getId("dialog_recommends"));
            if ((DrawUtils.dip2px(56.0f) * 4) + (DrawUtils.dip2px(10.0f) * 3) > DrawUtils.getScreenWidth(ActivationGuideWindowManager.this.mContext)) {
                ActivationGuideWindowManager.this.mRecommendGrid.setNumColumns(3);
            } else {
                ActivationGuideWindowManager.this.mRecommendGrid.setNumColumns(4);
            }
            ActivationRecommendAdapter unused3 = ActivationGuideWindowManager.this.mRecommendAdapter = new ActivationRecommendAdapter(ActivationGuideWindowManager.this.mContext, ActivationGuideWindowManager.this, ActivationGuideWindowManager.this.mAdInfoList);
            ActivationGuideWindowManager.this.mRecommendGrid.setAdapter(ActivationGuideWindowManager.this.mRecommendAdapter);
            ActivationGuideWindowManager.this.mRecommendGrid.setSelector(new ColorDrawable(0));
            TextView unused4 = ActivationGuideWindowManager.this.mInstalledAppNameTextView = (TextView) findViewById(ResourcesProvider.getInstance(ActivationGuideWindowManager.this.mContext).getId("dialog_installed_app_name_textview"));
            View unused5 = ActivationGuideWindowManager.this.mRefreshBtn = findViewById(ResourcesProvider.getInstance(ActivationGuideWindowManager.this.mContext).getId("dialog_refresh"));
            ActivationGuideWindowManager.this.mRefreshBtn.setOnClickListener(this);
            ProgressBar unused6 = ActivationGuideWindowManager.this.mRefreshProgressBar = (ProgressBar) findViewById(ResourcesProvider.getInstance(ActivationGuideWindowManager.this.mContext).getId("ad_refresh_progressbar"));
            View unused7 = ActivationGuideWindowManager.this.mCancelBtn = findViewById(ResourcesProvider.getInstance(ActivationGuideWindowManager.this.mContext).getId("dialog_cancel"));
            ActivationGuideWindowManager.this.mCancelBtn.setOnClickListener(this);
            View unused8 = ActivationGuideWindowManager.this.mOpenBtn = findViewById(ResourcesProvider.getInstance(ActivationGuideWindowManager.this.mContext).getId("dialog_open"));
            ActivationGuideWindowManager.this.mOpenBtn.setOnClickListener(this);
        }

        public void onClick(View view) {
            String str;
            String str2;
            if (view != null) {
                int viewId = view.getId();
                if (viewId == ResourcesProvider.getInstance(ActivationGuideWindowManager.this.mContext).getId("dialog_cancel")) {
                    ActivationGuideWindowManager.this.hideActivationGuideWindow();
                } else if (viewId == ResourcesProvider.getInstance(ActivationGuideWindowManager.this.mContext).getId("dialog_open")) {
                    AppUtils.safeStartActivity(ActivationGuideWindowManager.this.mContext, ActivationGuideWindowManager.this.mCurPackageName);
                    Context access$500 = ActivationGuideWindowManager.this.mContext;
                    String str3 = ActivationGuideWindowManager.this.mPreActivateDataMap != null ? (String) ActivationGuideWindowManager.this.mPreActivateDataMap.get(1) : "";
                    if (ActivationGuideWindowManager.this.mPreActivateDataMap != null) {
                        str = (String) ActivationGuideWindowManager.this.mPreActivateDataMap.get(6);
                    } else {
                        str = "";
                    }
                    if (ActivationGuideWindowManager.this.mPreActivateDataMap != null) {
                        str2 = (String) ActivationGuideWindowManager.this.mPreActivateDataMap.get(8);
                    } else {
                        str2 = "";
                    }
                    AdSdkOperationStatistic.uploadAdActivationGuideBtnClickStaticstic(access$500, AdSdkOperationStatistic.ACTIVATION_GUIDE_WINDOW_AV_A000, str3, str, str2, ActivationGuideWindowManager.this.mCurPackageName);
                    if (ActivationGuideWindowManager.this.mPreActivateDataMap != null) {
                        ActivationGuideWindowManager.this.mPreActivateDataMap.clear();
                        Map unused = ActivationGuideWindowManager.this.mPreActivateDataMap = null;
                    }
                    ActivationGuideWindowManager.this.hideActivationGuideWindow();
                } else if (viewId == ResourcesProvider.getInstance(ActivationGuideWindowManager.this.mContext).getId("dialog_refresh") && !ActivationGuideWindowManager.this.mIsRefreshing) {
                    ActivationGuideWindowManager.this.setIsRefreshing(true);
                    ActivationGuideWindowManager.this.loadNextData();
                }
            }
        }

        public boolean dispatchKeyEvent(KeyEvent event) {
            switch (event.getKeyCode()) {
                case 4:
                case 82:
                    ActivationGuideWindowManager.this.hideActivationGuideWindow();
                    break;
            }
            return super.dispatchKeyEvent(event);
        }

        public boolean onInterceptTouchEvent(MotionEvent ev) {
            if (isTouchDialog(ev)) {
                return super.onInterceptTouchEvent(ev);
            }
            ActivationGuideWindowManager.this.hideActivationGuideWindow();
            return true;
        }

        private boolean isTouchDialog(MotionEvent ev) {
            return ev.getY() >= ((float) getTop());
        }
    }
}
