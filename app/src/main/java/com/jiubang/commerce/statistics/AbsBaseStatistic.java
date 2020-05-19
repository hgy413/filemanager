package com.jiubang.commerce.statistics;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import com.gau.go.gostaticsdk.OnInsertDBListener;
import com.gau.go.gostaticsdk.StatisticsManager;
import com.gau.go.gostaticsdk.beans.OptionBean;
import com.jb.ga0.commerce.util.LogUtils;
import com.jb.ga0.commerce.util.io.MultiprocessSharedPreferences;
import com.jiubang.commerce.ad.http.bean.BaseModuleDataItemBean;
import com.jiubang.commerce.utils.StringUtils;
import java.util.HashMap;
import java.util.Map;

public abstract class AbsBaseStatistic {
    private static final String ACTIVATE_PREFIX_KEY = "ACTIVATE#";
    public static final int ADVERT_ID = 8;
    public static final int ASSOCIATED_OBJ = 7;
    public static final int CALL_URL = 11;
    public static final int DOWNLOAD_TIME = 10;
    protected static final long DOWNLOAD_TO_INSTALL_MAX_INTERVAL_TIME = 1800000;
    public static final int ENTRANCE = 4;
    public static final int FUN_ID = 0;
    private static final String INSTALL_PREFIX_KEY = "INSTALL#";
    protected static final String LAYOUT_STATISTICS_DATA_SEPARATE = "#";
    protected static final String LOG_TAG = "CommerceSDKStatistic";
    public static final int OPERATE_FAIL = 0;
    public static final int OPERATE_SUCCESS = 1;
    public static final int OPTION_CODE = 3;
    public static final int PACKAGENAME = 2;
    public static final int POSITION = 6;
    public static final int REMARK = 9;
    public static final int SENDER = 1;
    protected static final String STATISTICS_DATA_SEPARATE_ITEM = "#";
    protected static final String STATISTICS_DATA_SEPARATE_STRING = "||";
    public static final int TAB_CATEGORY = 5;
    protected static Map<Integer, String> sMPreActiveMap = null;
    protected static Map<Integer, String> sMPreInstallMap = null;
    private static SharedPreferences sPrivatePreference;

    protected static void uploadStatisticData(Context context, int logSequence, int funId, StringBuffer data, Object... immediately) {
        if (LogUtils.isShowLog()) {
            StatisticsManager.getInstance(context).enableLog(true);
        }
        LogUtils.d(LOG_TAG, "uploadStatisticData(" + StringUtils.toString(data) + ")");
        if (isAnywayImmediatelyUpload(funId)) {
            StatisticsManager.getInstance(context).uploadStaticDataForOptions(logSequence, funId, StringUtils.toString(data), (OnInsertDBListener) null, new OptionBean[]{new OptionBean(3, true)});
        } else if (immediately.length > 0) {
            StatisticsManager.getInstance(context).uploadStaticDataForOptions(logSequence, funId, StringUtils.toString(data), (OnInsertDBListener) null, new OptionBean[]{new OptionBean(0, immediately[0])});
        } else {
            StatisticsManager.getInstance(context).uploadStaticData(logSequence, funId, StringUtils.toString(data), (OnInsertDBListener) null);
        }
    }

    public static void saveReadyInstallList(Context context, int funId, String sender, String packageName, String optionCode, String entrance, String tabCategory, String position, String associatedObj, String aId, String remark, String seqId, String callUrl) {
        if (!TextUtils.isEmpty(optionCode) && !TextUtils.isEmpty(packageName)) {
            StringBuffer sb = new StringBuffer();
            sb.append(funId).append("#");
            sb.append(sender).append("#");
            sb.append(packageName).append("#");
            sb.append(optionCode).append("#");
            sb.append(entrance).append("#");
            sb.append(tabCategory).append("#");
            sb.append(position).append("#");
            sb.append(associatedObj).append("#");
            sb.append(aId).append("#");
            sb.append(remark).append("#");
            sb.append(System.currentTimeMillis()).append("#");
            sb.append(callUrl);
            getPrivatePreference(context);
            if (sPrivatePreference != null) {
                SharedPreferences.Editor editor = sPrivatePreference.edit();
                editor.putString(INSTALL_PREFIX_KEY + seqId + packageName, sb.toString());
                editor.commit();
            }
        }
    }

    public static String[] getInstallData(Context context, String packageName, String seqId) {
        getPrivatePreference(context);
        if (sPrivatePreference == null) {
            return null;
        }
        String strData = sPrivatePreference.getString(INSTALL_PREFIX_KEY + seqId + packageName, "");
        if (TextUtils.isEmpty(strData)) {
            return null;
        }
        String[] datas = strData.split("#");
        SharedPreferences.Editor editor = sPrivatePreference.edit();
        editor.putString(INSTALL_PREFIX_KEY + seqId + packageName, "");
        editor.commit();
        return datas;
    }

    public static synchronized boolean isPreInstallState(Context context, String pkgName, String seqId) {
        boolean flag;
        synchronized (AbsBaseStatistic.class) {
            flag = false;
            String[] data = getInstallData(context, pkgName, seqId);
            if (data != null && data.length > 1) {
                flag = true;
                if (sMPreInstallMap == null) {
                    sMPreInstallMap = new HashMap();
                }
                sMPreInstallMap.clear();
                sMPreInstallMap.put(0, StringUtils.toString(data[0]));
                sMPreInstallMap.put(1, data[1]);
                sMPreInstallMap.put(2, data.length > 2 ? data[2] : "");
                sMPreInstallMap.put(3, data.length > 3 ? data[3] : "");
                sMPreInstallMap.put(4, data.length > 4 ? data[4] : "");
                sMPreInstallMap.put(5, data.length > 5 ? data[5] : "");
                sMPreInstallMap.put(6, data.length > 6 ? data[6] : "");
                sMPreInstallMap.put(7, data.length > 7 ? data[7] : "");
                sMPreInstallMap.put(8, data.length > 8 ? data[8] : "");
                sMPreInstallMap.put(9, data.length > 9 ? data[9] : "");
                sMPreInstallMap.put(10, data.length > 10 ? data[10] : BaseModuleDataItemBean.AD_DATA_SOURCE_TYPE_OFFLINE);
                sMPreInstallMap.put(11, data.length > 11 ? data[11] : "");
            }
        }
        return flag;
    }

    public static void saveReadyActivateList(Context context, int funId, String sender, String packageName, String optionCode, String entrance, String tabCategory, String position, String associatedObj, String aId, String remark, String seqId, String callUrl) {
        if (!TextUtils.isEmpty(optionCode) && !TextUtils.isEmpty(packageName)) {
            StringBuffer sb = new StringBuffer();
            sb.append(funId).append("#");
            sb.append(sender).append("#");
            sb.append(packageName).append("#");
            sb.append(optionCode).append("#");
            sb.append(entrance).append("#");
            sb.append(tabCategory).append("#");
            sb.append(position).append("#");
            sb.append(associatedObj).append("#");
            sb.append(aId).append("#");
            sb.append(remark).append("#");
            sb.append(System.currentTimeMillis()).append("#");
            sb.append(callUrl);
            getPrivatePreference(context);
            if (sPrivatePreference != null) {
                SharedPreferences.Editor editor = sPrivatePreference.edit();
                editor.putString(ACTIVATE_PREFIX_KEY + seqId + packageName, sb.toString());
                editor.commit();
            }
        }
    }

    public static String[] getActivateData(Context context, String packageName, String seqId) {
        getPrivatePreference(context);
        if (sPrivatePreference == null) {
            return null;
        }
        String strData = sPrivatePreference.getString(ACTIVATE_PREFIX_KEY + seqId + packageName, "");
        if (TextUtils.isEmpty(strData)) {
            return null;
        }
        String[] datas = strData.split("#");
        SharedPreferences.Editor editor = sPrivatePreference.edit();
        editor.putString(ACTIVATE_PREFIX_KEY + seqId + packageName, "");
        editor.commit();
        return datas;
    }

    public static synchronized boolean isPreActivateState(Context context, String pkgName, String seqId) {
        boolean flag;
        synchronized (AbsBaseStatistic.class) {
            flag = false;
            String[] data = getActivateData(context, pkgName, seqId);
            if (data != null && data.length > 1) {
                flag = true;
                if (sMPreActiveMap == null) {
                    sMPreActiveMap = new HashMap();
                }
                sMPreActiveMap.clear();
                setPreActivateDataToMap(sMPreActiveMap, data);
            }
        }
        return flag;
    }

    private static void setPreActivateDataToMap(Map<Integer, String> preActiveMap, String[] activateDataArray) {
        if (preActiveMap == null) {
            preActiveMap = new HashMap<>();
        }
        preActiveMap.put(0, StringUtils.toString(activateDataArray[0]));
        preActiveMap.put(1, activateDataArray[1]);
        preActiveMap.put(2, activateDataArray.length > 2 ? activateDataArray[2] : "");
        preActiveMap.put(3, activateDataArray.length > 3 ? activateDataArray[3] : "");
        preActiveMap.put(4, activateDataArray.length > 4 ? activateDataArray[4] : "");
        preActiveMap.put(5, activateDataArray.length > 5 ? activateDataArray[5] : "");
        preActiveMap.put(6, activateDataArray.length > 6 ? activateDataArray[6] : "");
        preActiveMap.put(7, activateDataArray.length > 7 ? activateDataArray[7] : "");
        preActiveMap.put(8, activateDataArray.length > 8 ? activateDataArray[8] : "");
        preActiveMap.put(9, activateDataArray.length > 9 ? activateDataArray[9] : "");
        preActiveMap.put(10, activateDataArray.length > 10 ? activateDataArray[10] : BaseModuleDataItemBean.AD_DATA_SOURCE_TYPE_OFFLINE);
        preActiveMap.put(11, activateDataArray.length > 11 ? activateDataArray[11] : "");
    }

    public static Map<Integer, String> getPreActivateData(Context context, String pkgName) {
        String[] datas = null;
        getPrivatePreference(context);
        if (sPrivatePreference != null) {
            String strData = sPrivatePreference.getString("ACTIVATE#105" + pkgName, "");
            if (!TextUtils.isEmpty(strData)) {
                datas = strData.split("#");
            }
        }
        if (datas == null || datas.length <= 0) {
            return null;
        }
        Map<Integer, String> preActiveDataMap = new HashMap<>();
        setPreActivateDataToMap(preActiveDataMap, datas);
        return preActiveDataMap;
    }

    protected static void uploadStatisticDataAndLocation(Context context, int logSequence, int funId, StringBuffer data, String position) {
        StatisticsManager.getInstance(context).uploadStaticDataForOptions(logSequence, funId, StringUtils.toString(data), (OnInsertDBListener) null, new OptionBean[]{new OptionBean(1, position)});
    }

    private static boolean isAnywayImmediatelyUpload(int funId) {
        if (funId == BaseSeq105OperationStatistic.sPRODUCT_ID) {
            return true;
        }
        return false;
    }

    public static synchronized SharedPreferences getPrivatePreference(Context context) {
        SharedPreferences sharedPreferences;
        synchronized (AbsBaseStatistic.class) {
            if (sPrivatePreference == null) {
                sPrivatePreference = MultiprocessSharedPreferences.getSharedPreferences(context, "ad_sdk_statistic", 0);
            }
            sharedPreferences = sPrivatePreference;
        }
        return sharedPreferences;
    }
}
