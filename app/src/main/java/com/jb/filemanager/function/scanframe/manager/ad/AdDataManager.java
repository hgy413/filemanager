package com.jb.filemanager.function.scanframe.manager.ad;

import android.content.Context;

import com.jb.filemanager.database.provider.AdDataProvider;
import com.jb.filemanager.database.provider.CacheDataProvider;
import com.jb.filemanager.database.tablebean.AdPathBean;
import com.jb.filemanager.function.scanframe.bean.adbean.AdBean;
import com.jb.filemanager.manager.LanguageManager;
import com.jb.filemanager.util.Logger;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
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
 * Created by xiaoyu on 2016/10/25.
 */

public class AdDataManager {
    // 常量
    final static byte[] KEY_BYTES = new byte[]{0x66, 0x65, 0x35, 0x43, 0x61,
            0x26, 0x41, 0x3d, 0x75, 0x41, 0x14, 0x43, 0x48, 0x34, 0x00, 0x32};
    final static byte[] IV_BYTES = new byte[]{0x29, 0x58, 0x24, 0x72, 0x54,
            0x21, 0x42, 0x35, 0x17, 0x4b, 0x46, 0x57, 0x19, 0x21, 0x35, 0x16};

    private static final String AD_DATA_SERVER_URL5 = "/zspeed_service/api/v6/i4?code=";
    private static final int ALLOW_MAX_ERROR_REQUEST = 2;
    // 管理器
    private Context mContext;
    private static AdDataManager sInstance;
    private AdDataProvider mDataProvider;

    //private RequestQueue mRequestQueue;

    static private boolean sIsUpdated = false;
    //private static int sGetBatchsErrorCounts = 0;

    //private static boolean sRequestUsUrl = false;
    //private static boolean sRequestNorUrl = false;

    public static synchronized AdDataManager getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new AdDataManager(context);
        }
        //sGetBatchsErrorCounts = 0;

        //sRequestUsUrl = false;
        //sRequestNorUrl = false;
        return sInstance;
    }

    private AdDataManager(Context context) {
        mContext = context.getApplicationContext();
        initData();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        CacheDataProvider.change(sKeyBytesV6, sIvBytesV6);
        mDataProvider = AdDataProvider.getInstance(mContext);
    }

   /* private RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(mContext);
        }
        return mRequestQueue;
    }*/

    /**
     * 返回一个广告垃圾AdBean列表，数据获取失败时返回空队列
     */
    public List<AdBean> getAdBeanList() {
        List<AdBean> adBeanList = new ArrayList<AdBean>();
        if (mDataProvider == null) {
            Logger.e("", "must call the initData method first.");
            return adBeanList;
        }
        ArrayList<AdPathBean> adPathBeanList = mDataProvider
                .queryAllAdPath();
        Map<String, String> langMap = mDataProvider.queryAllAdLang();
        if (null == langMap || langMap.isEmpty()) {
            return adBeanList;
        }

        Iterator<AdPathBean> it = adPathBeanList.iterator();

        while (it.hasNext()) {
            String key = null;
            AdPathBean bean = it.next();
            AdBean adBean = new AdBean();
            key = bean.getAdId()
                    + "#"
                    + LanguageManager.getInstance(mContext).getCurrentLanguageWithLocale();
            if (langMap.containsKey(key)) {
                adBean.setDBKey(bean.getPath());
                adBean.setPath(bean.getPath());
                adBean.setTitle(langMap.get(key));
            } else {
//                key = bean.getAdId() + "#zh_CN";
                key = bean.getAdId() + "#en_US";
                if (langMap.containsKey(key)) {
                    adBean.setDBKey(bean.getPath());
                    adBean.setPath(bean.getPath());
                    adBean.setTitle(langMap.get(key));
                } else {
                    continue;
                }
            }
            adBeanList.add(adBean);
        }
        return adBeanList;
    }

    // *****************************************************************请求服务器更新缓存文件数据库**********************************************************************//

    static byte[] sKeyBytesV6 = new byte[]{0x07, 0x77, 0x74, 0x55, 0x37, 0x03, 0x35, 0x04, 0x70, 0x35, 0x62, 0x55, 0x05, 0x64, 0x21, 0x44};
    static byte[] sIvBytesV6 = new byte[]{0x04, 0x07, 0x54, 0x32, 0x57, 0x24, 0x36, 0x65, 0x03, 0x06, 0x76, 0x07, 0x03, 0x24, 0x65, 0x73};

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
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    private byte[] encGzipAes(final byte[] data) throws Exception {
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

    public boolean checkIsUpdated() {
        return sIsUpdated;
    }

    public void setIsUpdated(boolean isUpdated) {
        sIsUpdated = isUpdated;
    }
}
