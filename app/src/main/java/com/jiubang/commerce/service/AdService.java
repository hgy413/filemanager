package com.jiubang.commerce.service;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import com.jb.ga0.commerce.util.LogUtils;
import com.jiubang.commerce.ad.window.ExitGoogleWindowManager;
import com.jiubang.commerce.ad.window.GuideDownloadWindowManager;

public class AdService extends Service {
    public static final String AD_SERVICES_REQUEST = "AD_SERVICES_REQUEST";
    public static final int REQUEST_FLOAT_WINDOW_HIDE = 17;
    public static final int REQUEST_FLOAT_WINDOW_SHOW = 16;
    private ExitGoogleWindowManager mFloatWindowManager;

    public void onCreate() {
        super.onCreate();
        LogUtils.d("Ad_SDK", "Ad Service onCreate");
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtils.d("Ad_SDK", "Ad Service onStartCommand");
        if (intent == null) {
            return super.onStartCommand(intent, flags, startId);
        }
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            switch (bundle.getInt(AD_SERVICES_REQUEST, -1)) {
                case 16:
                    GuideDownloadWindowManager.getInstance(this).createGuideDownloadWindow();
                    this.mFloatWindowManager = ExitGoogleWindowManager.getInstance();
                    this.mFloatWindowManager.start(this);
                    break;
                case 17:
                    if (this.mFloatWindowManager != null) {
                        GuideDownloadWindowManager.getInstance(this).hideGuideDownloadWindow();
                        this.mFloatWindowManager.stop();
                        break;
                    }
                    break;
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    public void onDestroy() {
        super.onDestroy();
    }

    public IBinder onBind(Intent arg0) {
        return null;
    }
}
