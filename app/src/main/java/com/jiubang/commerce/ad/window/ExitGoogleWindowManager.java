package com.jiubang.commerce.ad.window;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import com.jb.ga0.commerce.util.LogUtils;
import com.jiubang.commerce.ad.ResourcesProvider;
import com.jiubang.commerce.service.AdService;
import com.jiubang.commerce.utils.AppUtils;
import com.jiubang.commerce.utils.GoogleMarketUtils;
import com.jiubang.commerce.utils.SystemUtils;

public class ExitGoogleWindowManager {
    public static final int ENTRANCE_ID_FLOATINGWINDOW = 16;
    public static final String EXTRA_ENTRANCE_ID = "entrance_id";
    private static ExitGoogleWindowManager sInstance;
    private Activity mEntranceActivity;
    public boolean mFloatWindowShowing;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    if (ExitGoogleWindowManager.this.mStart && (msg.obj instanceof Context)) {
                        ExitGoogleWindowManager.this.startCheckGooglePlay((Context) msg.obj);
                        if (ExitGoogleWindowManager.this.mIsGooglePlayOnTop && !ExitGoogleWindowManager.this.mFloatWindowShowing) {
                            ExitGoogleWindowManager.this.createSmallWindow((Context) msg.obj);
                            return;
                        } else if (!ExitGoogleWindowManager.this.mIsGooglePlayOnTop && ExitGoogleWindowManager.this.mFloatWindowShowing) {
                            ExitGoogleWindowManager.this.removeSmallWindow((Context) msg.obj);
                            return;
                        } else if (ExitGoogleWindowManager.this.mIsGooglePlayOnTop && ExitGoogleWindowManager.this.mFloatWindowShowing) {
                            ExitGoogleWindowManager.this.moveToEdge();
                            return;
                        } else {
                            return;
                        }
                    } else {
                        return;
                    }
                default:
                    return;
            }
        }
    };
    public boolean mIsGooglePlayOnTop;
    /* access modifiers changed from: private */
    public FloatWindowSmallView mSmallWindow;
    /* access modifiers changed from: private */
    public WindowManager.LayoutParams mSmallWindowParams;
    /* access modifiers changed from: private */
    public boolean mStart;
    /* access modifiers changed from: private */
    public WindowManager mWindowManager;

    private ExitGoogleWindowManager() {
    }

    public static synchronized ExitGoogleWindowManager getInstance() {
        ExitGoogleWindowManager exitGoogleWindowManager;
        synchronized (ExitGoogleWindowManager.class) {
            if (sInstance == null) {
                sInstance = new ExitGoogleWindowManager();
            }
            exitGoogleWindowManager = sInstance;
        }
        return exitGoogleWindowManager;
    }

    public void createSmallWindow(final Context context) {
        if (context != null) {
            WindowManager windowManager = getWindowManager(context);
            int screenWidth = windowManager.getDefaultDisplay().getWidth();
            int screenHeight = windowManager.getDefaultDisplay().getHeight();
            if (this.mSmallWindow == null) {
                this.mSmallWindow = new FloatWindowSmallView(context);
                if (this.mSmallWindowParams == null) {
                    this.mSmallWindowParams = new WindowManager.LayoutParams();
                    this.mSmallWindowParams.type = 2003;
                    this.mSmallWindowParams.format = 1;
                    this.mSmallWindowParams.flags = 40;
                    this.mSmallWindowParams.gravity = 51;
                    this.mSmallWindowParams.width = this.mSmallWindow.mWindowViewWidth;
                    this.mSmallWindowParams.height = this.mSmallWindow.mWindowViewHeight;
                    this.mSmallWindowParams.x = screenWidth;
                    this.mSmallWindowParams.y = (screenHeight / 2) - (this.mSmallWindowParams.height / 2);
                }
                windowManager.addView(this.mSmallWindow, this.mSmallWindowParams);
                this.mFloatWindowShowing = true;
                this.mSmallWindow.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View arg0) {
                        ExitGoogleWindowManager.this.hideFloatWindow(context);
                    }
                });
            }
        }
    }

    public void removeSmallWindow(Context context) {
        this.mFloatWindowShowing = false;
        if (this.mSmallWindow != null) {
            getWindowManager(context).removeView(this.mSmallWindow);
            this.mSmallWindow = null;
            this.mSmallWindowParams = null;
            setEntranceActivity((Context) null);
        }
        if (!SystemUtils.IS_SDK_ABOVE_L) {
            Intent intent = new Intent(context, AdService.class);
            Bundle bundle = new Bundle();
            bundle.putInt(AdService.AD_SERVICES_REQUEST, 17);
            intent.putExtras(bundle);
            context.startService(intent);
        }
    }

    private WindowManager getWindowManager(Context context) {
        if (this.mWindowManager == null) {
            this.mWindowManager = (WindowManager) context.getSystemService("window");
        }
        return this.mWindowManager;
    }

    /* access modifiers changed from: private */
    public void hideFloatWindow(Context context) {
        this.mStart = false;
        Class<?> cls = getEntranceActivity() != null ? getEntranceActivity().getClass() : null;
        if (LogUtils.isShowLog()) {
            LogUtils.d("Ad_SDK", "hideFloatWindow to Activity::->" + cls);
        }
        removeSmallWindow(context);
        if (cls != null && context != null) {
            Intent returnIntent = new Intent(context, cls);
            returnIntent.putExtra("entrance_id", 16);
            returnIntent.setFlags(270532608);
            context.startActivity(returnIntent);
        }
    }

    public void setEntranceActivity(Context activityContext) {
        if (activityContext == null || !(activityContext instanceof Activity)) {
            this.mEntranceActivity = null;
        } else {
            this.mEntranceActivity = (Activity) activityContext;
        }
    }

    public void startCheckGooglePlay(Context context) {
        new CheckGooglePlayThread(context, this.mHandler).start();
    }

    public Activity getEntranceActivity() {
        return this.mEntranceActivity;
    }

    public void moveToEdge() {
        if (this.mSmallWindowParams != null && this.mSmallWindowParams.x < this.mWindowManager.getDefaultDisplay().getWidth()) {
            new LaunchTask().execute(new Void[0]);
        }
    }

    class LaunchTask extends AsyncTask<Void, Void, Void> {
        int mWth = ExitGoogleWindowManager.this.mWindowManager.getDefaultDisplay().getWidth();

        LaunchTask() {
        }

        /* access modifiers changed from: protected */
        public Void doInBackground(Void... params) {
            while (ExitGoogleWindowManager.this.mSmallWindowParams != null && ExitGoogleWindowManager.this.mSmallWindowParams.x < this.mWth) {
                ExitGoogleWindowManager.this.mSmallWindowParams.x += 10;
                publishProgress(new Void[0]);
                try {
                    Thread.sleep(8);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        /* access modifiers changed from: protected */
        public void onProgressUpdate(Void... values) {
            if (ExitGoogleWindowManager.this.mSmallWindow != null && ExitGoogleWindowManager.this.mSmallWindowParams != null) {
                ExitGoogleWindowManager.this.mWindowManager.updateViewLayout(ExitGoogleWindowManager.this.mSmallWindow, ExitGoogleWindowManager.this.mSmallWindowParams);
            }
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(Void result) {
            if (ExitGoogleWindowManager.this.mSmallWindow != null && ExitGoogleWindowManager.this.mSmallWindowParams != null) {
                ExitGoogleWindowManager.this.mWindowManager.updateViewLayout(ExitGoogleWindowManager.this.mSmallWindow, ExitGoogleWindowManager.this.mSmallWindowParams);
            }
        }
    }

    class FloatWindowSmallView extends LinearLayout {
        private RelativeLayout mWindowLayout;
        public int mWindowViewHeight = this.mWindowLayout.getLayoutParams().height;
        public int mWindowViewWidth = this.mWindowLayout.getLayoutParams().width;

        public FloatWindowSmallView(Context context) {
            super(context);
            LayoutInflater.from(context).inflate(ResourcesProvider.getInstance(context).getLayoutId("ad_exit_google_float_window_small_layout"), this);
            this.mWindowLayout = (RelativeLayout) findViewById(ResourcesProvider.getInstance(context).getId("small_window_layout"));
        }
    }

    class CheckGooglePlayThread extends Thread {
        private Context mContext;
        private Handler mHandler;

        public CheckGooglePlayThread(Context context, Handler handler) {
            this.mContext = context;
            this.mHandler = handler;
        }

        public void run() {
            setName("CheckGooglePlayThread");
            ExitGoogleWindowManager.this.mIsGooglePlayOnTop = ExitGoogleWindowManager.this.isGooglePlayOnTop(this.mContext);
            if (ExitGoogleWindowManager.this.mStart) {
                Message message = new Message();
                message.what = 1;
                message.obj = this.mContext;
                this.mHandler.sendMessageDelayed(message, 1000);
            }
        }
    }

    public boolean isGooglePlayOnTop(Context context) {
        if (context == null) {
            return false;
        }
        boolean isTop = AppUtils.isAppRunningInForground(context, GoogleMarketUtils.MARKET_PACKAGE);
        if (!SystemUtils.IS_SDK_ABOVE_L) {
            return isTop;
        }
        String topName = AppUtils.getTopAppPackageName(context);
        if (!isTop || !GoogleMarketUtils.MARKET_PACKAGE.equals(topName)) {
            return false;
        }
        return true;
    }

    public void start(Context context) {
        this.mStart = true;
        Message message = new Message();
        message.what = 1;
        message.obj = context;
        this.mHandler.sendMessageDelayed(message, 1000);
    }

    public void stop() {
        this.mStart = false;
    }
}
