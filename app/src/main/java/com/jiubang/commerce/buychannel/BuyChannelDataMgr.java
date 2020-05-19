package com.jiubang.commerce.buychannel;

import android.content.Context;
import android.content.SharedPreferences;
import com.jb.ga0.commerce.util.DevHelper;
import com.jb.ga0.commerce.util.LogUtils;
import com.jiubang.commerce.buychannel.BuyChannelSetting;
import com.jiubang.commerce.buychannel.buyChannel.Interface.SetBuyChannelListener;
import com.jiubang.commerce.buychannel.buyChannel.bean.BuyChannelBean;
import com.jiubang.commerce.buychannel.buyChannel.bean.UserTypeInfo;
import com.jiubang.commerce.buychannel.buyChannel.utils.BuyChannelUtils;
import com.jiubang.commerce.buychannel.buyChannel.utils.TextUtils;
import java.util.ArrayList;
import java.util.Iterator;

public class BuyChannelDataMgr {
    private static final String FILE_NAME = "commerce_buychannel";
    private static final String KEY_BUYCHANNEL = "buychannel";
    private static final String KEY_FIRST_CHECKTIME = "first_checktime";
    private static final String KEY_LAST_CHECKTIME = "last_checktime";
    private static final String USERTAG_FIRST_CHECKTIME = "usertag_first_checktime";
    private static BuyChannelDataMgr sInstance;
    private final Context mContext;
    private ArrayList<IBuyChannelUpdateListener> mListeners = new ArrayList<>(2);
    private byte[] mLocker = new byte[0];
    private SharedPreferences mSp;

    private BuyChannelDataMgr(Context context) {
        this.mContext = context;
        this.mSp = MPSharedPreferences.getSharedPreferences(context, "commerce_buychannel", 0);
        this.mSp.registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                if (BuyChannelDataMgr.KEY_BUYCHANNEL.equals(key)) {
                    BuyChannelDataMgr.this.notifyBuyChannelUpdate();
                }
            }
        });
    }

    public static BuyChannelDataMgr getInstance(Context context) {
        if (sInstance == null) {
            synchronized (BuyChannelDataMgr.class) {
                if (sInstance == null) {
                    sInstance = new BuyChannelDataMgr(context.getApplicationContext());
                }
            }
        }
        return sInstance;
    }

    public void registerBuyChannelListener(IBuyChannelUpdateListener listener) {
        if (listener != null) {
            synchronized (this.mLocker) {
                if (!this.mListeners.contains(listener)) {
                    this.mListeners.add(listener);
                }
            }
        }
    }

    public void unregisterBuyChannelListener(IBuyChannelUpdateListener listener) {
        if (listener != null) {
            synchronized (this.mLocker) {
                this.mListeners.remove(listener);
            }
        }
    }

    public String getBuyChannel() {
        return this.mSp.getString(KEY_BUYCHANNEL, (String) null);
    }

    public BuyChannelBean getBuyChannelBean() {
        BuyChannelBean testBuyChannelBean = getTestBuyChannelBean();
        if (testBuyChannelBean != null) {
            return testBuyChannelBean;
        }
        if (this.mSp == null) {
            this.mSp = MPSharedPreferences.getSharedPreferences(this.mContext, "commerce_buychannel", 0);
        }
        return BuyChannelUtils.jsonStr2BuyChannelBean(this.mSp.getString(KEY_BUYCHANNEL, (String) null));
    }

    private void setBuyChannel(BuyChannelBean newBuyChannelBean) {
        if (newBuyChannelBean != null) {
            this.mSp.edit().putString(KEY_BUYCHANNEL, newBuyChannelBean.toJsonStr()).commit();
        }
    }

    private BuyChannelBean getTestBuyChannelBean() {
        BuyChannelBean buyChannelBean = null;
        try {
            String testbuyChannel = new DevHelper().getValueByKey("testBuyChannel");
            if (!TextUtils.isEmpty(testbuyChannel)) {
                String[] testString = testbuyChannel.split(",");
                if (testString.length == 4) {
                    BuyChannelBean buyChannelBean2 = new BuyChannelBean();
                    try {
                        buyChannelBean2.setChannelFrom(testString[0]);
                        buyChannelBean2.setBuyChannel(testString[1]);
                        buyChannelBean2.setFirstUserType(testString[2]);
                        buyChannelBean2.setSecondUserType(Integer.parseInt(testString[3]));
                        BuyChannelBean buyChannelBean3 = buyChannelBean2;
                        return buyChannelBean2;
                    } catch (Exception e) {
                        e = e;
                        buyChannelBean = buyChannelBean2;
                        e.printStackTrace();
                        BuyChannelBean buyChannelBean4 = buyChannelBean;
                        return buyChannelBean;
                    }
                }
            }
        } catch (Exception e2) {
            e = e2;
            e.printStackTrace();
            BuyChannelBean buyChannelBean42 = buyChannelBean;
            return buyChannelBean;
        }
        BuyChannelBean buyChannelBean422 = buyChannelBean;
        return buyChannelBean;
    }

    public void setConversionStr(String conversionDataJsonStr) {
        if (!TextUtils.isEmpty(conversionDataJsonStr)) {
            this.mSp.edit().putString(BuySdkConstants.CONVERSIONDATA, conversionDataJsonStr).commit();
        }
    }

    public void setBuyChannelBean(String buyChannel, BuyChannelSetting.ChannelFrom channel, UserTypeInfo.FirstUserType userType, UserTypeInfo.SecondUserType juserType, String conversionDataJsonStr, SetBuyChannelListener listener, String campaign, String campaignId) {
        if (userType != null && channel != null && juserType != null) {
            if (listener != null) {
                listener.setBuyChannelSuccess();
            }
            BuyChannelBean newBuyChannelBean = new BuyChannelBean();
            newBuyChannelBean.setBuyChannel(buyChannel);
            newBuyChannelBean.setFirstUserType(userType.toString());
            newBuyChannelBean.setChannelFrom(channel.toString());
            newBuyChannelBean.setSecondUserType(juserType.getValue());
            newBuyChannelBean.setSuccessCheck(true);
            newBuyChannelBean.setCampaign(campaign);
            newBuyChannelBean.setCampaignId(campaignId);
            setBuyChannel(newBuyChannelBean);
            setConversionStr(conversionDataJsonStr);
            LogUtils.i("buychannelsdk", "setBuyChannel完毕,[BuyChannelDataMgr::setBuyChannelBean] :buyChannel=" + buyChannel + ",一级用户类型=" + userType.toString() + ",二级用户类型=" + juserType.getValue() + ",识别来源=" + channel.toString() + "买量SDK是否已经成功确认用户身份 " + true);
        }
    }

    public long getLastCheckTime() {
        return this.mSp.getLong(KEY_LAST_CHECKTIME, 0);
    }

    public void setLastCheckTime(long lastCheckTime) {
        this.mSp.edit().putLong(KEY_LAST_CHECKTIME, lastCheckTime).commit();
    }

    public long getFirstCheckTime() {
        return this.mSp.getLong(KEY_FIRST_CHECKTIME, 0);
    }

    public long getFirstCheckUserTagTime() {
        return this.mSp.getLong(USERTAG_FIRST_CHECKTIME, 0);
    }

    public void saveFirstCheckUserTagTime(long firstCheckTime) {
        this.mSp.edit().putLong(USERTAG_FIRST_CHECKTIME, firstCheckTime).commit();
    }

    public void setFirstCheckTime(long firstCheckTime) {
        this.mSp.edit().putLong(KEY_FIRST_CHECKTIME, firstCheckTime).commit();
    }

    /* access modifiers changed from: private */
    public void notifyBuyChannelUpdate() {
        ArrayList arrayList;
        synchronized (this.mLocker) {
            arrayList = (ArrayList) this.mListeners.clone();
        }
        String buyChannel = getBuyChannelBean().getBuyChannel();
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            IBuyChannelUpdateListener listener = (IBuyChannelUpdateListener) it.next();
            if (listener != null) {
                listener.onBuyChannelUpdate(buyChannel);
            }
        }
    }

    public SharedPreferences getSharedPreferences(Context context) {
        if (this.mSp != null) {
            return this.mSp;
        }
        this.mSp = MPSharedPreferences.getSharedPreferences(context, "commerce_buychannel", 0);
        return this.mSp;
    }
}
