package com.jb.filemanager.ad;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.NativeAd;
import com.jb.filemanager.BuildConfig;
import com.jb.filemanager.TheApplication;
import com.jb.filemanager.util.Logger;
import com.jiubang.commerce.ad.bean.AdModuleInfoBean;
import com.jiubang.commerce.ad.http.bean.BaseModuleDataItemBean;

import java.util.ArrayList;

/**
 * Created by bill wang on 17/3/8.
 *
 */

public class AdFrequencyManager {

    private static final String TAG = "AdFrequencyManager";
    private static AdFrequencyManager sInstance;

    public static AdFrequencyManager getInstance() {
        synchronized (AdFrequencyManager.class) {
            if (sInstance == null) {
                sInstance = new AdFrequencyManager();
            }
            return sInstance;
        }
    }

    private AdFrequencyManager() {

    }

    public void handleAdFrequency(AdModuleInfoBean adModuleInfoBean) {
        if (adModuleInfoBean != null) {
            BaseModuleDataItemBean baseModuleDataItemBean = adModuleInfoBean.getModuleDataItemBean();
            if (baseModuleDataItemBean != null) {
                int frequency = baseModuleDataItemBean.getAdFrequency();
                String[] fbIds = baseModuleDataItemBean.getFbIds();
                if (frequency > 0 && fbIds.length > 0) {
                    for (int i = 0; i < frequency; i++) {
                        requestFbNativeAndShow(fbIds[0]);
                        Logger.d(TAG, "稀释请求:" + String.valueOf(i + 1) + "/" + String.valueOf(frequency) + " facebook id: " + fbIds[0]);
                    }
                }
            }
        }
    }

    private void requestFbNativeAndShow(String placementId) {
        final com.facebook.ads.NativeAd fbNativeAd = new com.facebook.ads.NativeAd(TheApplication.getInstance(), placementId);
        fbNativeAd.setAdListener(new AdListener() {
            @Override
            public void onError(Ad ad, AdError adError) {

            }

            @Override
            public void onAdLoaded(Ad ad) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        exposeBySuspensionWindow(fbNativeAd);
                    }
                }).start();
            }

            @Override
            public void onAdClicked(Ad ad) {

            }

            @Override
            public void onLoggingImpression(Ad ad) {

            }
        });
        fbNativeAd.loadAd();
    }

    /**
     * 用悬浮窗来进行曝光处理
     *
     * @param fbAd FB广告实例
     */
    private void exposeBySuspensionWindow(final NativeAd fbAd) {
        final TextView exposeView = new TextView(TheApplication.getInstance());
        exposeView.setText(fbAd.getAdTitle());

        final RelativeLayout containerView = new RelativeLayout(TheApplication.getInstance());
        containerView.addView(exposeView);

        final WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.format = PixelFormat.RGBA_8888; // 设置透明背景
        lp.type = WindowManager.LayoutParams.TYPE_TOAST; //Toast类型的悬浮窗无需申请权限
        lp.width = BuildConfig.DEBUG ? WindowManager.LayoutParams.WRAP_CONTENT : 1;
        lp.height = BuildConfig.DEBUG ? WindowManager.LayoutParams.WRAP_CONTENT : 1;
        lp.gravity = Gravity.START | Gravity.TOP; //悬浮窗的位置在屏幕的左上方
        lp.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE; //让点击事件能够传递到下面的Activity

        final Handler handler = new Handler(TheApplication.getInstance().getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                fbAd.unregisterView();
                ArrayList<View> list = new ArrayList<>();
                list.add(exposeView);
                fbAd.registerViewForInteraction(containerView, list);
                // 显示悬浮窗
                try {
                    ((WindowManager) TheApplication.getInstance().getSystemService(Context.WINDOW_SERVICE)).addView(containerView, lp);
                    /* 过一段时间再隐藏悬浮窗，因为太快隐藏的话，FB不算曝光 */
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            ((WindowManager) TheApplication.getInstance().getSystemService(Context.WINDOW_SERVICE)).removeView(containerView);
                            fbAd.unregisterView();
                            fbAd.destroy();
                        }
                    }, 5000);
                } catch (Exception e) {
                    Logger.e(TAG, "Show expose facebook advertisement suspension window fail: " + e.toString());
                    e.printStackTrace();
                }
            }
        });
    }

}
