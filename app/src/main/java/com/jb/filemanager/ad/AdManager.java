package com.jb.filemanager.ad;

import android.content.Context;

import com.jb.filemanager.buyuser.BuyUserManager;
import com.jb.filemanager.manager.spm.SharedPreferencesManager;
import com.jb.filemanager.manager.spm.IPreferencesIds;
import com.jb.filemanager.util.AppUtils;
import com.gau.go.gostaticsdk.utiltool.UtilTool;
import com.jb.filemanager.BuildConfig;
import com.jb.filemanager.Const;
import com.jb.filemanager.TheApplication;
import com.jb.filemanager.ad.cache.AdDataManager;
import com.jb.filemanager.ad.event.AdManagerInitedEvent;
import com.jb.filemanager.util.Logger;
import com.jiubang.commerce.ad.AdSdkApi;
import com.jiubang.commerce.ad.params.ClientParams;
import com.jiubang.commerce.ad.sdk.MoPubAdConfig;
import com.jiubang.commerce.buychannel.buyChannel.utils.AppInfoUtils;

import org.greenrobot.eventbus.EventBus;

/**
 * 广告信息管理类<br>
 *
 * @author wangying <br/>
 *         create at 2015-5-22 上午11:19:14
 */
public class AdManager {

    private static final String LOGGER_TAG = AdManager.class.getSimpleName();

    private AdDataManager mAdDataManager;

    private static AdManager sAdManager;
    private volatile boolean mIsInit = false;
    private String mGoogleId;

    public static void initSingleton(Context context) {
        if (sAdManager == null) {
            sAdManager = new AdManager(context);
        }
    }

    /**
     * 注意判空,AdManager的初始化是在onGlobalDataLoadingDone后，所以在程序刚起来就去获取时会报错<br>
     * 现在已经知道需要判空的地方有主界面的试试手气，游戏加速的桌面游戏文件夹
     *
     * @return result
     */
    public static AdManager getInstance() {
        return sAdManager;
    }

    private AdManager(Context context) {
        mAdDataManager = new AdDataManager();
        initAppAdSdk(context);
        AdFrequencyManager.getInstance();
    }

    /**
     * 初始化应用分发广告SDK
     */
    private void initAppAdSdk(final Context context) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                if (BuildConfig.DEBUG) {
                    AdSdkApi.setEnableLog(Logger.DEBUG); // 测试
                }
                final String processName = Const.PROCESS_NAME_MAIN;
                final String goId = UtilTool.getGOId(context);
                final String googleId = AppInfoUtils.getAdvertisingId(context);
                mGoogleId = googleId;
                final String channel = AppUtils.getChannel(context);

                SharedPreferencesManager spManager = SharedPreferencesManager.getInstance(context);
                long installTime = 0;
                if (spManager != null) {
                    installTime = spManager.getLong(IPreferencesIds.KEY_FIRST_INSTALL_TIME, 0);
                }
                String buyChannel = BuyUserManager.getInstance().getBuyUserChannel();
                boolean isUpgrade = AppUtils.isInstallFromUpdate();
                AdSdkApi.initSDK(context,
                        processName,
                        goId,
                        googleId,
                        channel,
                        new ClientParams(buyChannel, installTime, isUpgrade));

                mIsInit = true;
                EventBus.getDefault().post(new AdManagerInitedEvent());
            }
        }).start();
    }

    /**
     * 获取GoogleId
     *
     * @return 正确的Google Id或则UNABLE-TO-RETRIEVE
     */
    public String getGoogleId() {
        return mGoogleId;
    }

    /**
     * 判断是否已经初始化
     *
     * @return is init
     */
    public static boolean isInit() {
        return sAdManager != null && sAdManager.mIsInit;
    }

    // 获取广告，如果使用缓存为true，则优先缓存，false直接去SDK中取广告
    public void loadAd(int entrance, int adNum, MoPubAdConfig moPubAdConfig, boolean cache) {
        Logger.i(LOGGER_TAG, "load ad entrance is:" + String.valueOf(entrance) + ", number:" + String.valueOf(adNum) + ", use cache:" + String.valueOf(cache));
        mAdDataManager.load(TheApplication.getInstance(), entrance, adNum, moPubAdConfig, cache);
    }

    // 检查缓存中是否有广告
    public boolean hasAdInCache(int entrance) {
        Logger.i(LOGGER_TAG, "has ad in cache entrance is:" + String.valueOf(entrance));
        return mAdDataManager.hasAd(entrance);
    }

    // 从缓存中获取广告
    public void getAdFromCache(int entrance) {
        Logger.i(LOGGER_TAG, "get ad from cache entrance is:" + String.valueOf(entrance));
        mAdDataManager.getAdFromCache(entrance);
    }

    // 移除缓存中的广告
    public void removeAdFromCache(int entrance) {
        Logger.i(LOGGER_TAG, "remove ad from cache entrance is:" + String.valueOf(entrance));
        mAdDataManager.removeAd(entrance);
    }
}
