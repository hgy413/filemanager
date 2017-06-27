package com.jb.filemanager.function.scanframe.clean;


import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.android.volley.RequestQueue;
import com.jb.filemanager.R;
import com.jb.filemanager.TheApplication;
import com.jb.filemanager.database.provider.CacheDataProvider;
import com.jb.filemanager.eventbus.AgreePrivacyEvent;
import com.jb.filemanager.eventbus.IOnEventAsyncSubscriber;
import com.jb.filemanager.eventbus.IOnEventMainThreadSubscriber;
import com.jb.filemanager.function.scanframe.bean.cachebean.AppCacheBean;
import com.jb.filemanager.function.scanframe.bean.cachebean.subitem.SubAppCacheBean;
import com.jb.filemanager.function.scanframe.bean.common.subitemcommon.SubItemBean;
import com.jb.filemanager.function.scanframe.clean.event.CleanDBDataInitDoneEvent;
import com.jb.filemanager.function.scanframe.clean.event.GlobalDataLoadingDoneEvent;
import com.jb.filemanager.function.scanframe.clean.event.PackageAddedEvent;
import com.jb.filemanager.function.scanframe.clean.ignore.CleanIgnoreBean;
import com.jb.filemanager.function.scanframe.clean.ignore.CleanIgnoreCacheAppBean;
import com.jb.filemanager.function.scanframe.clean.ignore.CleanIgnoreCachePathBean;
import com.jb.filemanager.manager.LanguageManager;
import com.jb.filemanager.util.AppUtils;
import com.jb.filemanager.util.Logger;

import org.greenrobot.eventbus.Subscribe;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * 应用缓存管理器 <br>
 * 提供一个接口获取一个APP级别缓存的List，调用前需初始化，第一次调 用此接口返回数据后会触发更新数据库操作，更新动作完成后发送广播
 *
 * @author chenbenbin & kvan
 */
public class CacheManager {
    // 常量
    final static byte[] KEY_BYTES = new byte[]{0x70, 0x70, 0x51, 0x5a, 0x4b,
            0x26, 0x24, 0x4f, 0x67, 0x51, 0x72, 0x71, 0x4b, 0x48, 0x6a, 0x78};
    final static byte[] IV_BYTES = new byte[]{0x07, 0x1f, 0x12, 0x50, 0x48,
            0x7a, 0x3c, 0x77, 0x26, 0x3c, 0x70, 0x54, 0x51, 0x51, 0x67, 0x1a};

    private static final String CACHE_LANG_SERVER_URL5 = "/zspeed_service/api/v6/i2?code=";
    private static final String CACHE_TRASH_DATA_SERVER_URL5 = "/zspeed_service/api/v6/i1?code=";
    private static final int NO_FIRST_UPDATE_DELAY = 60 * 1000;
    private static final int ALLOW_MAX_ERROR_REQUEST = 2;
    private boolean mIsFirstInstall = true;
    private boolean mIsStopInstall = true;
    // 管理器
    private Context mContext;
    private static CacheManager sInstance;
    private CacheDataProvider mDataProvider;
    private long mUpdateCurrentTime;
    private HashSet<String> mInstalledApp = new HashSet<String>();

    private RequestQueue mRequestQueue;

    private boolean mIsOnAgreePrivacyEvent = false;
    private boolean mInitDbData = false;

    private static int sGetBatchsErrorCounts = 0;

    private static boolean sRequestUsUrl = false;
    private static boolean sRequestNorUrl = false;

    public static synchronized CacheManager getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new CacheManager(context);
        }
        sGetBatchsErrorCounts = 0;

        sRequestUsUrl = false;
        sRequestNorUrl = false;
        return sInstance;
    }

    private CacheManager(Context context) {
        mContext = context.getApplicationContext();
        initData();
    }

    private void initData() {
        TheApplication.getGlobalEventBus().register(
                new IOnEventAsyncSubscriber<GlobalDataLoadingDoneEvent>() {
                    @Override
                    @Subscribe
                    public void onEventAsync(GlobalDataLoadingDoneEvent event) {
                        TheApplication.getGlobalEventBus().unregister(this);
                        initDbData();
                        //checkHandleAgreePrivacyUpdateNetData();
                    }
                });
        CacheDataProvider.change(sKeyBytesV6, sIvBytesV6);

        // 监听用户同意协议
        TheApplication.getGlobalEventBus().register(
                new IOnEventMainThreadSubscriber<AgreePrivacyEvent>() {
                    @Override
                    @Subscribe
                    public void onEventMainThread(AgreePrivacyEvent event) {
                        TheApplication.getGlobalEventBus().unregister(this);
                        Logger.d("kvan", "用户同意了协议，发起第一次请求");
                        mIsOnAgreePrivacyEvent = true;
                        //checkHandleAgreePrivacyUpdateNetData();
                    }
                });

        // 监听安装
        TheApplication.getGlobalEventBus().register(
                new IOnEventMainThreadSubscriber<PackageAddedEvent>() {
                    @Override
                    @Subscribe
                    public void onEventMainThread(PackageAddedEvent event) {
                        String packageName = event.getPackageName();
                        Logger.d("kvan", packageName + " installed.");
                        if (mIsFirstInstall) {
                            Logger.d("kvan", "first install");
                            mInstallRequestHandler.sendEmptyMessageDelayed(0,
                                    NO_FIRST_UPDATE_DELAY);
                            //CacheManager.getInstance(mContext).updateLangDataV5();
                            mIsFirstInstall = false;
                        } else {
                            Logger.d("kvan", "no first install");
                            mIsStopInstall = false;
                        }
                    }
                });
    }

    /*private void checkHandleAgreePrivacyUpdateNetData() {
        if (!mIsOnAgreePrivacyEvent || !mInitDbData) {
            return;
        }
        TheApplication.postRunOnShortTaskThread(new Runnable() {

            @Override
            public void run() {
                RemoteSettingManager.getInstance(mContext).updateRemoteSettingDataV5();
                KeywordBean.updateHotWord();
                CacheManager.getInstance(mContext).updateLangDataV5();
                ResidueDataManager.getInstance(mContext).updateResidueDataV5();
                CleanAdDataManager.getInstance(mContext).updateAdDataV5();
            }
        });
    }*/

    /*private RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(mContext);
        }
        return mRequestQueue;
    }*/

    /**
     * 初始化
     */
    private void initDbData() {
        // 这里在Cache数据库不存在时会复制数据库
        mDataProvider = CacheDataProvider.getInstance(mContext);
        CleanDBDataInitDoneEvent.APP_CACHE.setIsDone(true);
        TheApplication.postEvent(CleanDBDataInitDoneEvent.APP_CACHE);
        mInstalledApp = getInstalledApp();
        //CleanDataUpdateTimer.getInstance(mContext);
        mInitDbData = true;
    }

    private Handler mInstallRequestHandler = new Handler() {
        public void handleMessage(Message msg) {
            Logger.d("kvan", "time up");
            if (mIsStopInstall) {
                Logger.d("kvan", "stop looper");
                mIsFirstInstall = true;
            } else {
                //CacheManager.getInstance(mContext).updateLangDataV5();
                mInstallRequestHandler.sendEmptyMessageDelayed(0,
                        NO_FIRST_UPDATE_DELAY);
                Logger.d("kvan", "loop again");
            }
            mIsStopInstall = true;
        }

        ;
    };

    /**
     * 获取缓存垃圾的描述，传入一个二级数据单元的列表 数据查询后会填充到传入的数据结构中
     */
    private void getCacheDescription(ArrayList<AppCacheBean> appList) {
        Map<String, String[]> allCacheDes = mDataProvider.queryAllCacheDes();
        Iterator<AppCacheBean> it2 = appList.iterator();
        while (it2.hasNext()) {
            AppCacheBean tempBean2 = it2.next();
            Iterator<SubItemBean> it3 = tempBean2.getSubItemList()
                    .iterator();
            while (it3.hasNext()) {
                SubAppCacheBean tempBean3 = (SubAppCacheBean) it3
                        .next();
                String key = tempBean3.getTextId()
                        + LanguageManager.getInstance(mContext)
                        .getCurrentLanguageWithLocale();
                String[] str = null;
                if (allCacheDes.containsKey(key)) {
                    str = allCacheDes.get(key);
                } else {
                    key = tempBean3.getTextId() + "en_US";
                    if (allCacheDes.containsKey(key)) {
                        str = allCacheDes.get(key);
                    } else {
                        Logger.e("kvan",
                                "can't find desc: "
                                        + tempBean3.getPackageName());
                        str = new String[]{"cache", ""};
                    }
                }
                int dayBefore = tempBean3.getDayBefore();
                if (dayBefore > 0) {
                    tempBean3
                            .setTitle(str[0]
                                    + "("
                                    + dayBefore
                                    + mContext
                                    .getString(R.string.clean_days_ago)
                                    + ")");
                } else {
                    tempBean3.setTitle(str[0]);
                }
                tempBean3.setDesc(str[1]);
            }
            // Logger.d("kvan", tempBean2.toString());
        }
    }

    /**
     * 返回一个二级清理单元的ArrayList
     *
     * @return ArrayList<AppCacheBean>
     */
    public ArrayList<AppCacheBean> getAppCacheList() {
        mInstalledApp = getInstalledApp();
        filterAppListByIgnoreApp();
        ArrayList<AppCacheBean> cacheList = new ArrayList<>();
        if (mInstalledApp.isEmpty() || mDataProvider == null) {
            Logger.e("", "must call the initData method first.");
            Logger.d("kvan", "must call the init data first");
            return cacheList;
        }
        Map<String, AppCacheBean> appCacheBeanMap = mDataProvider.queryAllAppCache(mContext);
        // 如果查询出错则直接返回空队列
        if (appCacheBeanMap.isEmpty()) {
            return cacheList;
        }
        // 获取路径白名单
        List<CleanIgnoreBean> ignoreList = CleanManager.getInstance(mContext).queryCachePathIgnore();
        Logger.e("Ignore", "size = " + ignoreList.size());
        for (String installApp : mInstalledApp) {
            AppCacheBean appCacheBean = appCacheBeanMap.get(installApp);
            if (null != appCacheBean) {
                filterCacheBeanByIgnorePath(ignoreList, appCacheBean);
                if (appCacheBean.getSubItemList().isEmpty()) {
                    // 过滤后若没有子路径，则跳过，该应用不加入列表
                    continue;
                }
                String appName = AppUtils.getAppName(mContext,
                        appCacheBean.getPackageName());
                appCacheBean.setTitle(TextUtils.isEmpty(appName) ? appCacheBean.getPackageName()
                        : appName);
                cacheList.add(appCacheBean);
            }
        }
        getCacheDescription(cacheList);
        return cacheList;
    }

    /**
     * 将安装列表中的白名单应用过滤掉
     */
    private void filterAppListByIgnoreApp() {
        List<CleanIgnoreBean> ignoreList = CleanManager.getInstance(mContext).queryCacheAppIgnore();
        for (CleanIgnoreBean ignoreBean : ignoreList) {
            CleanIgnoreCacheAppBean cacheAppBean = (CleanIgnoreCacheAppBean) ignoreBean;
            mInstalledApp.remove(cacheAppBean.getPackageName());
        }
    }

    /**
     * 过滤掉应用中的白名单路径
     */
    private void filterCacheBeanByIgnorePath(List<CleanIgnoreBean> ignoreList, AppCacheBean appCacheBean) {
        for (CleanIgnoreBean ignoreBean : ignoreList) {
            CleanIgnoreCachePathBean cachePathBean = (CleanIgnoreCachePathBean) ignoreBean;
            ArrayList<SubItemBean> subItemList = appCacheBean.getSubItemList();
            Iterator<SubItemBean> iterator = subItemList.iterator();
            while (iterator.hasNext()) {
                SubItemBean subItemBean = iterator.next();
                if (subItemBean.getPath().equals(cachePathBean.getPath())) {
                    iterator.remove();
                }
            }
        }
    }

    /**
     * 获取已安装的应用包名
     */
    public HashSet<String> getInstalledApp() {
        HashSet<String> set = new HashSet<>();

        List<String> installedPackages = AppUtils
                .getInstalledPackagesPackageNameOnly(mContext);
        set.addAll(installedPackages);
        return set;
    }

    // *****************************************************************请求服务器更新缓存文件数据库**********************************************************************//

    /**
     * 请求服务器更新缓存文件数据库 v3接口必须先请求多语言接口，完毕后再请求垃圾路径接口
     * volley在SDK>=9的情况下除了需要重写getHeaders之外还有重写getBodyContentType 默认情况下
     * getBodyContentType中返回的字段会以和getHeader中“Content-Type”重复出现的形式出现在最后的请求header中
     * 这里最后返回一个CleanUpdateCacheBean
     */

   /* public void updataCacheData() {
        String requestCode = mDataProvider.getCacheCode();
        if (requestCode.equals("-1")) {
            Logger.d("kvan", "服务器垃圾接口已废弃,不再更新。");
            return;
        }

        String url = null;
        String districtCode = com.gto.zero.zboost.function.gameboost.http.GoHttpHeadUtil
                .getLocal(mContext);
        if (null != districtCode
                && (districtCode.equals("us") || districtCode.equals("US"))) {
            url = CleanNetRequest.US_DOMAIN + CACHE_TRASH_DATA_SERVER_URL5
                    + requestCode;
        } else {
            url = CleanNetRequest.DOMAIN + CACHE_TRASH_DATA_SERVER_URL5
                    + requestCode;
        }
        final JSONObject requestJson = createCacheJsonRequest();
        if (requestJson == null) {
            return;
        }

        RequestQueue queue = getRequestQueue();

        CleanNetRequest cacheRequest = new CleanNetRequest("i1",
                Request.Method.POST, url, new Response.Listener<byte[]>() {
            @Override
            public void onResponse(byte[] response) {
                InputStream stream = new ByteArrayInputStream(response);
                final String responseStr = decGzipAes(stream);
                Logger.d("kvan", "cache path response");
                // Logger.d("kvan", "cache path responseStr: "
                // + responseStr);
                if (responseStr != null) {
                    Runnable dbUpdateRunnable = new Runnable() {
                        @Override
                        public void run() {
                            JSONObject responseObject = null;
                            try {
                                responseObject = new JSONObject(
                                        responseStr);
                            } catch (JSONException e1) {
                                e1.printStackTrace();
                            }
                            mDataProvider
                                    .updateCacheCode(responseObject
                                            .optString("code"));
                            // 获得一个CleanUpdateCacheBean
                            CleanUpdateCacheBean updateResultBean = CacheJsonUtil
                                    .decodeRequestResult(responseObject);

                            // 将更新的数据保存到数据库
                            mDataProvider
                                    .updateAppsCache(updateResultBean
                                            .getmNewAppCacheData());

                            mDataProvider.updateDelayApps(
                                    updateResultBean.getmDelayApps(),
                                    mUpdateCurrentTime);

                            Logger.d("kvan", "cache data update finish.");
                            TheApplication
                                    .postEvent(new CacheDataUpdateDoneEvent());
                        }
                    };
                    TheApplication
                            .postRunOnShortTaskThread(dbUpdateRunnable);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                if (networkResponse != null) {
                    Logger.d("kvan", "network error: "
                            + new String(networkResponse.data));
                }
                if (networkResponse == null) {
                    if (error.getClass().equals(TimeoutError.class)) {
                        // Show timeout error message
                        Logger.d("kvan", "request timeout!");
                    }
                }
            }
        }) {
            // 添加请求的头
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Charset", "UTF-8");
                headers.put("Content-Encoding", "gaip");
                headers.put("Accept-Encoding", "gaip");
                return headers;
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }

            @Override
            public byte[] getBody() {
                try {
                    return encGzipAes(requestJson.toString().getBytes("UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

        };
        Logger.d("kvan", "cache path request:");
        // Logger.d("kvan", "cache path request: " + requestJson.toString());
        cacheRequest.setRetryPolicy(new DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(cacheRequest);
    }*/

    /**
     * 向多语言服务器请求完整多语言列表
     */
   /* public void updateLangDataV5() {
        String requestCode = mDataProvider.getLangCode();
        if (requestCode.equals("-1")) {
            Logger.d("kvan", "服务器多语言接口已废弃,不再更新。");
            return;
        }
        if (sGetBatchsErrorCounts >= ALLOW_MAX_ERROR_REQUEST) {
            Logger.d("kvan", "getBatchs error to stop");
            return;
        }

        String url = null;

        if (!sRequestNorUrl && !sRequestUsUrl) {
            String districtCode = com.gto.zero.zboost.function.gameboost.http.GoHttpHeadUtil
                    .getLocal(mContext);
            if (null != districtCode
                    && (districtCode.equals("us") || districtCode.equals("US"))) {
                url = CleanNetRequest.US_DOMAIN + CACHE_LANG_SERVER_URL5
                        + requestCode;
                sRequestUsUrl = true;
            } else {
                url = CleanNetRequest.DOMAIN + CACHE_LANG_SERVER_URL5
                        + requestCode;
                sRequestNorUrl = true;
            }
        } else {
            if (!sRequestNorUrl) {
                url = CleanNetRequest.DOMAIN + CACHE_LANG_SERVER_URL5
                        + requestCode;
                sRequestNorUrl = true;
            } else {
                url = CleanNetRequest.US_DOMAIN + CACHE_LANG_SERVER_URL5
                        + requestCode;
                sRequestUsUrl = true;
            }
        }

        final JSONObject requestJson = new JSONObject();
        try {
            requestJson.put("pheadV5", CleanNetRequest.getPheadV5JSONObject());
            requestJson.put("clientVersion",
                    ZBoostUtil.getVersionCode(mContext));
            JSONObject jb = new JSONObject();
            List<CleanBatchMd5Bean> list = mDataProvider.getCacheBatchMd5();
            if (!list.isEmpty()) {
                Iterator<CleanBatchMd5Bean> it = list.iterator();
                while (it.hasNext()) {
                    CleanBatchMd5Bean temp = it.next();
                    jb.put(String.valueOf(temp.getBatchId()), temp.getMd5());
                }
            } else {
                Logger.d("kvan", "lang data is empty now."
                        + sGetBatchsErrorCounts);
                jb.put("0", "a46a1b59d3c4f8157de56d55bd0ed2f5");
                sGetBatchsErrorCounts++;
            }
            requestJson.put("md5s", jb);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        RequestQueue queue = getRequestQueue();

        final CleanNetRequest langRequest = new CleanNetRequest("i2",
                Request.Method.POST, url, new Response.Listener<byte[]>() {
            @Override
            public void onResponse(byte[] response) {
                InputStream stream = new ByteArrayInputStream(response);
                final String responseStr = decGzipAes(stream);
                Logger.d("kvan", "cache lang response");
                // Logger.d("kvan", "cache lang response: " +
                // responseStr);
                if (responseStr != null) {
                    Runnable dbUpdateRunnable = new Runnable() {
                        @Override
                        public void run() {
                            JSONObject responseObject = null;
                            try {
                                responseObject = new JSONObject(
                                        responseStr);
                            } catch (JSONException e1) {
                                e1.printStackTrace();
                            }
                            mDataProvider.updateLangCode(responseObject
                                    .optString("code"));
                            JSONArray langData = responseObject
                                    .optJSONArray("batchs");
                            if (0 == langData.length()) {
                                Logger.d("kvan",
                                        "local lang data is newest");
                            } else {
                                ArrayList<CleanBatchMd5Bean> batchMd5List = new ArrayList<CleanBatchMd5Bean>();
                                ArrayList<CleanCacheLangBean> langDataArrayList = new ArrayList<CleanCacheLangBean>();
                                CacheJsonUtil.decodeLangDataV3(
                                        langData, batchMd5List,
                                        langDataArrayList);
                                if (mDataProvider
                                        .updateLangData(langDataArrayList)) {
                                    mDataProvider
                                            .updateCacheBatchMd5(batchMd5List);
                                }
                            }
                            boolean isRequestAgain = responseObject
                                    .optBoolean("requestAgain");
                            if (isRequestAgain) {
                                sRequestNorUrl = false;
                                sRequestUsUrl = false;
                                updateLangDataV5();
                            } else {
                                TheApplication
                                        .postEvent(new CacheUpdateLangDataDoneEvent());
                                Logger.d("kvan",
                                        "lang data update finish. start updata cache data.");
                                updataCacheData();
                            }
                        }
                    };
                    TheApplication
                            .postRunOnShortTaskThread(dbUpdateRunnable);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                if (networkResponse != null) {
                    Logger.d("kvan", "network error: "
                            + new String(networkResponse.data));
                }
                if (networkResponse == null) {
                    if (error.getClass().equals(TimeoutError.class)) {
                        if (!sRequestNorUrl || !sRequestUsUrl) {
                            updateLangDataV5();
                        }
                        // Show timeout error message
                        Logger.d("kvan", "request timeout!");
                    }
                }
            }
        }) {
            // 添加请求的头
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Charset", "UTF-8");
                headers.put("Content-Encoding", "gaip");
                headers.put("Accept-Encoding", "gaip");
                return headers;
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }

            @Override
            public byte[] getBody() {
                try {
                    return encGzipAes(requestJson.toString().getBytes("UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        };
        Logger.d("kvan", "cache lang request" + url + " usR: " + sRequestUsUrl
                + " norR: " + sRequestNorUrl);
        // Logger.d("kvan", "cache lang request: " + requestJson.toString());
        langRequest.setRetryPolicy(new DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // System.setProperty("http.keepAlive", "false");
        queue.add(langRequest);
    }*/

    /**
     * 构造应用缓存更新的请求Json体
     */
    /*private JSONObject createCacheJsonRequest() {
        JSONObject requestJson = new JSONObject();
        JSONArray appArrayJson = new JSONArray();
        Log.d("kvan", "start request online cache data.");
        // 从这里注释
        // 创建app的json数组，不使用mInstalledApp避免升级后再提供的数据出现缺失
        HashSet<String> installedApps = getInstalledApp();

        // 去除需要延迟请求的应用，同时将当前时间记录于mUpdateCurrentTime
        removeDelayApps(installedApps);
        if (installedApps.isEmpty()) {
            Logger.d("kvan", "没有需要更新的应用，不发送请求");
            return null;
        }
        Iterator<String> it = installedApps.iterator();
        // 从这里结束注释就能获取所有
        // HashSet<String> installedApps = new HashSet<String>();
        // InputStream is =
        // mContext.getResources().openRawResource(R.raw.all_apk);
        // final BufferedReader br = new BufferedReader(new InputStreamReader(
        // is));
        // String str;
        // try {
        // while( (str = br.readLine() )!=null) {
        // installedApps.add(str);
        // }
        // } catch (IOException e) {
        // e.printStackTrace();
        // }
        // Iterator<String> it = installedApps.iterator();
        // it = installedApps.iterator();
        // 从这里结束注释
        // //////////////
        try {
            while (it.hasNext()) {
                String pkgName = it.next();
                JSONObject mAppItemJson = new JSONObject();
                mAppItemJson.put("pkgName", pkgName);
                mAppItemJson.put("curVersion",
                        mDataProvider.queryAppCacheVersion(pkgName));
                appArrayJson.put(mAppItemJson);
            }

            requestJson.put("clientVersion",
                    ZBoostUtil.getVersionCode(mContext));
            requestJson.put("pheadV5", CleanNetRequest.getPheadV5JSONObject());
            requestJson.put("apps", appArrayJson);
        } catch (JSONException e) {
            requestJson = null;
            e.printStackTrace();
        }

        return requestJson;
    }
*/
    /**
     * 过滤延期请求的应用，还未到请求时间的应用会从传入的列表中删去
     *
     * @param HashSet
     * <String>
     */

    static byte[] sKeyBytesV6 = new byte[]{0x07, 0x77, 0x74, 0x55, 0x37, 0x03, 0x35, 0x04, 0x70, 0x35, 0x62, 0x55, 0x05, 0x64, 0x21, 0x44};
    static byte[] sIvBytesV6 = new byte[]{0x04, 0x07, 0x54, 0x32, 0x57, 0x24, 0x36, 0x65, 0x03, 0x06, 0x76, 0x07, 0x03, 0x24, 0x65, 0x73};

   /* public void removeDelayApps(HashSet<String> appSet) {
        mUpdateCurrentTime = System.currentTimeMillis();
        // Logger.d("kvan ", "current time: " + mUpdateCurrentTime);
        HashSet<String> delayApps = mDataProvider
                .getDelayApps(mUpdateCurrentTime);
        appSet.removeAll(delayApps);
    }*/

    public String decGzipAes(InputStream is) {
        final SecretKey key = new SecretKeySpec(sKeyBytesV6, "AES");
        final IvParameterSpec iv = new IvParameterSpec(sIvBytesV6);
        BufferedReader br = null;
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, key, iv);
            final CipherInputStream cipherInputStream = new CipherInputStream(
                    is, cipher);
            GZIPInputStream gzipInputStream = null;
            gzipInputStream = new GZIPInputStream(cipherInputStream);
            br = new BufferedReader(new InputStreamReader(gzipInputStream));
            return br.readLine();
        } catch (final Exception e) {
            e.printStackTrace();
        } finally {
            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null != br) {
                try {
                    br.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    private static byte[] encGzipAes(final byte[] data) throws Exception {
        final SecretKey key = new SecretKeySpec(sKeyBytesV6, "AES");
        final IvParameterSpec iv = new IvParameterSpec(sIvBytesV6);
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key, iv);
        } catch (final Exception e) {
            e.printStackTrace();
        }
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final CipherOutputStream cipherOutputStream = new CipherOutputStream(
                baos, cipher);
        final GZIPOutputStream gzipOutputStream = new GZIPOutputStream(
                cipherOutputStream);
        // System.out.println("input data[], len is : " + data.length);
        gzipOutputStream.write(data);
        gzipOutputStream.close();
        final byte[] re = baos.toByteArray();
        // System.out.println("after gzip and aes enc, byte array len : "
        // + re.length);
        return re;
    }

}
