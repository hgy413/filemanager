package com.jb.filemanager.buyuser;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;

import com.flashlight.brightestflashlightpro.app.ToolLockerSdk;
import com.jb.filemanager.BuildConfig;
import com.jb.filemanager.Const;
import com.jb.filemanager.TheApplication;
import com.jb.filemanager.buyuser.event.BuyUserChannelEvent;
import com.jb.filemanager.manager.spm.IPreferencesIds;
import com.jb.filemanager.manager.spm.SharedPreferencesManager;
import com.jb.filemanager.statistics.StatisticsTools;
import com.jiubang.commerce.buychannel.BuyChannelApi;
import com.jiubang.commerce.buychannel.BuySdkInitParams;
import com.jiubang.commerce.buychannel.IBuyChannelUpdateListener;
import com.jiubang.commerce.buychannel.buyChannel.bean.BuyChannelBean;

import org.greenrobot.eventbus.EventBus;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * 负责是否为买量用户的数据管理<br>
 * @author laojiale
 */
public class BuyUserManager {

    @SuppressLint("StaticFieldLeak")
    private static BuyUserManager sInstance;

    private Context mContext;

    private BuyUserManager(Context context) {
        mContext = context;

        //45协议相关参数
        //45协议的功能点id，每个产品都不一样,查询链接：http://wiki.3g.net.cn/pages/viewpage.action?pageId=18781210
        int p45FunId = 760;
        //产品key,长度24的字符串，在广告portal产品管理中生成,查询链接：http://gouser.3g.net.cn/userManage/login_iframe.jsp?systemid=68&hd=0(如果没有权限，请找黄炯查询)
        String productKey = "7EPR2BWJNI2K7ZV1V04CT4MB";
        //接入密钥，长度32的字符串，在广告portal产品管理中生成，查询链接：http://gouser.3g.net.cn/userManage/login_iframe.jsp?systemid=68&hd=0(如果没有权限，请找黄炯查询)
        String accessKey = "Q0I73XWNKHH9L2GI8TVDOCWP2VNUXQVY";
        // userTypeProtocolCId "用户来源判断接口协议"里的产品cid 定义详见 http://wiki.3g.net.cn/pages/viewpage.action?pageId=14255114
        String userTypeProtocolCId = "89";

        // BuySdkInitParams buySdkInitParams = new BuySdkInitParams(CHANNEL, false, p45FunId);
        //2.初始化买量SDK需要的相关参数
        BuySdkInitParams.Builder builder = new BuySdkInitParams.Builder(Const.APP_CHANNEL, p45FunId, userTypeProtocolCId, new BuySdkInitParams.IProtocal19Handler() {
            @Override
            public void uploadProtocal19() {
                StatisticsTools.upload19Info();
            }
        }, false, productKey, accessKey);

        //测试用，设置AdwordsGdnId
       /* String adwordsGdnId = mSp.getString("AdwordsGdnId", null);
        List<String> adwordsGdnIds = new ArrayList<>();
        adwordsGdnIds.add(adwordsGdnId);
        buySdkInitParams.setAdwordsGdnCampaignids(adwordsGdnIds);*/

        //开启LOG，上线包要注释掉这行
        if (BuildConfig.DEBUG) {
            BuyChannelApi.setDebugMode();
        }

        //3.初始化买量SDK
        BuyChannelApi.init(TheApplication.getInstance(), builder.build());

        //4.监听买量渠道名称
        BuyChannelApi.registerBuyChannelListener(mContext,
                new IBuyChannelUpdateListener() {
                    @Override
                    public void onBuyChannelUpdate(String buyChannel) {
                        // 检查是不是fb审核员
                        checkIsFbChecker(BuyChannelApi.getReferrer(mContext));
                        // 发送通知
                        EventBus.getDefault().post(new BuyUserChannelEvent());

                        // TODO @wangzq 工具锁初始化各种id配置之后打开这里
//                        // 更新工具锁的买量设置
//                        BuyChannelBean buyChannelBean =BuyChannelApi.getBuyChannelBean(mContext);
//                        ToolLockerSdk.getInstance().setBuyChannel(buyChannel, buyChannelBean.isUserBuy());
                    }
                }
        );
    }

    @Override
    protected void finalize() throws Throwable {
        mContext = null;
        super.finalize();
    }

    /**
     * 初始化单例,在程序启动时调用<br>
     * @param context context
     */
    public static void initSingleton(Context context) {
        sInstance = new BuyUserManager(context);
    }

    /**
     * 获取单例.<br>
     * @return BuyUserManager object
     */
    public static BuyUserManager getInstance() {
        return sInstance;
    }

    /**
     * 是否为买量用户
     * @return is buy user
     */
    public boolean isBuyUser() {
        boolean isBuyUser = false;
        BuyChannelBean buyChannelBean = BuyChannelApi.getBuyChannelBean(mContext);
        if (buyChannelBean != null) {
            isBuyUser = buyChannelBean.isUserBuy();
        }
        return isBuyUser;
    }

    /**
     * 获取用户的买量来源<br>
     * @return result
     */
    public String getBuyUserChannel() {
        String result = "";
        BuyChannelBean buyChannelBean = BuyChannelApi.getBuyChannelBean(mContext);
        if (buyChannelBean != null) {
            result = buyChannelBean.getBuyChannel();
        }
        return result;
    }

    private void checkIsFbChecker(String referrer) {
        SharedPreferencesManager spm = SharedPreferencesManager.getInstance(TheApplication.getInstance());
        int firstLaunchVersionCode = spm.getInt(IPreferencesIds.KEY_FIRST_LAUNCH_VERSION_CODE, BuildConfig.VERSION_CODE);

        if (firstLaunchVersionCode == BuildConfig.VERSION_CODE) {
            if (!TextUtils.isEmpty(referrer)) {
                try {
                    final String refer = URLDecoder.decode(referrer, "utf-8");
                    String utmSend = getUtmSend(refer);
                    if (!TextUtils.isEmpty(utmSend) && utmSend.equals("wbqchchjlxh")) {
                        spm.commitBoolean(IPreferencesIds.KEY_IS_FACEBOOK_CHECKER, true);
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private String getUtmSend(String referrer) {
        if (!TextUtils.isEmpty(referrer)) {
            String[] splits = referrer.split("&");
            for (String string : splits) {
                if (string != null && string.contains("utm_send")) {
                    String[] utmSources = string.split("=");
                    if (utmSources.length > 1) {
                        return utmSources[1];
                    }
                }
            }
        }
        return null;
    }

}
