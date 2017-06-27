package com.jb.filemanager.function.scanframe.manager.residue;

import android.content.Context;
import android.text.TextUtils;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.jb.filemanager.database.provider.CacheDataProvider;
import com.jb.filemanager.database.provider.ResidueDataProvider;
import com.jb.filemanager.function.scanframe.bean.residuebean.ResidueBean;
import com.jb.filemanager.manager.LanguageManager;
import com.jb.filemanager.util.Logger;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
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
 * 残余垃圾网络数据管理器
 *
 * @author chenbenbin & kvan
 */
public class ResidueDataManager {
    // 常量
    final static byte[] KEY_BYTES = new byte[]{0x66, 0x65, 0x35, 0x43, 0x61,
            0x26, 0x41, 0x3d, 0x75, 0x41, 0x14, 0x43, 0x48, 0x24, 0x00, 0x32};
    final static byte[] IV_BYTES = new byte[]{0x29, 0x58, 0x24, 0x72, 0x54,
            0x21, 0x42, 0x35, 0x17, 0x4b, 0x46, 0x57, 0x18, 0x21, 0x35, 0x16};

    private static final String RESIDUE_DATA_SERVER_URL5 = "/zspeed_service/api/v6/i3?code=";
    private static final int ALLOW_MAX_ERROR_REQUEST = 2;

    // 管理器
    private Context mContext;
    private static ResidueDataManager sInstance;
    private ResidueDataProvider mDataProvider;

    private RequestQueue mRequestQueue;

    private boolean mIsUpdated = false;
    private static int sGetBatchsErrorCounts = 0;

    private static boolean sRequestUsUrl = false;
    private static boolean sRequestNorUrl = false;

    public static synchronized ResidueDataManager getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new ResidueDataManager(context);
        }
        sGetBatchsErrorCounts = 0;

        sRequestUsUrl = false;
        sRequestNorUrl = false;
        return sInstance;
    }

    private ResidueDataManager(Context context) {
        mContext = context.getApplicationContext();
        initData();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        CacheDataProvider.change(sKeyBytesV6, sIvBytesV6);
        mDataProvider = ResidueDataProvider.getInstance(mContext);
    }

    private RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(mContext);
        }
        return mRequestQueue;
    }

    /**
     * 返回两个map，一个以包名为key，一个以路径为key 返回 true表示两个map都已填充，false表示填充失败
     *
     * @return boolean
     */
    public boolean getResidueMap(HashMap<String, ResidueBean> residueMap,
                                 HashMap<String, HashSet<ResidueBean>> residuePathMap) {
        if (mDataProvider == null) {
            Logger.e("", "must call the initData method first.");
            return false;
        }
        ArrayList<ResidueBean> residueBeanList = mDataProvider
                .queryAllResiduePath();
        Map<String, String> langMap = mDataProvider.queryAllResidueLang();
        if (null == residueBeanList || residueBeanList.isEmpty()
                || null == langMap || langMap.isEmpty()) {
            return false;
        }

        Iterator<ResidueBean> it = residueBeanList.iterator();
        while (it.hasNext()) {
            String key = null;
            ResidueBean bean = it.next();
            key = bean.getPackageName()
                    + "#"
                    + LanguageManager.getInstance(mContext).getCurrentLanguageWithLocale();
            if (langMap.containsKey(key)) {
                bean.setAppName(langMap.get(key));
            } else {
                key = bean.getPackageName() + "#zz_ZZ";
                if (langMap.containsKey(key)) {
                    bean.setAppName(langMap.get(key));
                } else {
                    Logger.e("kvan", "something wrong with langMap");
                    continue;
                }
            }
            if (residueMap.containsKey(bean.getPackageName())) {
                // 空指针保护
                ResidueBean tempBean = residueMap.get(bean.getPackageName());
                if (tempBean != null) {
                    tempBean.addPath(bean.getPath());
                }
            } else {
                residueMap.put(bean.getPackageName(), bean);
            }

            String path = bean.getPath();
            if (!TextUtils.isEmpty(path)) {
                if (residuePathMap.containsKey(path)) {
                    HashSet<ResidueBean> set = residuePathMap.get(path);
                    if (null != set) {
                        set.add(bean);
                    }
                } else {
                    HashSet<ResidueBean> set = new HashSet<ResidueBean>();
                    set.add(bean);
                    residuePathMap.put(path, set);
                }
            }
        }

        return true;
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
        gzipOutputStream.write(data);
        gzipOutputStream.close();
        final byte[] re = baos.toByteArray();
        return re;
    }

    public boolean checkIsUpdated() {
        return mIsUpdated;
    }

    public void setIsUpdated(boolean isUpdated) {
        mIsUpdated = isUpdated;
    }
}
