package com.jb.filemanager.function.scanframe.manager;

import android.content.Context;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.os.Build;
import android.os.Environment;
import android.os.RemoteException;
import android.os.StatFs;

import com.jb.filemanager.function.scanframe.bean.appBean.AppItemInfo;
import com.jb.filemanager.function.scanframe.clean.AppManager;
import com.jb.filemanager.function.scanframe.clean.CleanEventManager;
import com.jb.filemanager.function.scanframe.clean.event.CleanScanPathEvent;
import com.jb.filemanager.util.device.Machine;
import com.jb.filemanager.util.file.FileUtil;

import java.io.File;
import java.io.FileFilter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 系统缓存 - 应用程序数据大小获取类 <br/>
 * com.gto.zero.zboost.util.trash.AppCaCheManager
 *
 * @author wangying <br/>
 *         create at 2015-1-20 下午6:37:25
 */
public class SysCacheManager {

    private Context mContext;

    /**
     * 标志是不是安装和更新操作
     */
    private boolean mIsAppInstall = false;

    /**
     * 存放扫出的App缓存的大小
     */
    private long mCacheSize = 0;

    /**
     * 循环次数,线程安全
     */
    private AtomicInteger mSizeAtomicInteger = new AtomicInteger(0);

    /**
     * 数据查询完毕回调接口
     */
    private AppSizeCompleteListener mListener;

    public SysCacheManager(Context context) {
        this.mContext = context;
    }

    public SysCacheManager(Context context, boolean isAppInstall) {
        this.mContext = context;
        this.mIsAppInstall = true;
    }

    /**
     * 查询入口函数
     *
     * @param appItemInfos
     */
    public void querySizes(ArrayList<AppItemInfo> appItemInfos) {
        int size = appItemInfos.size();
        for (int i = 0; i < appItemInfos.size(); i++) {
            try {
                queryPackageSize((AppItemInfo) appItemInfos.get(i), size, true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 查询某个应用程序的大小（包括缓存数据，应用数据，程序大小）
     */
    public void queryPackageSize(AppItemInfo appItemInfo, int size,
                                 boolean isQueryAll) throws Exception {

        if (appItemInfo.getAppPackageName() != null) {
            // 使用反射机制得到PackageManager类的隐藏函数getPackageSizeInfo
            PackageManager pm = mContext.getPackageManager();
            try {

                Method getPackageSizeInfo = null;

                if (Machine.HAS_SDK_JELLY_BEAN_MR1) {
                    Class[] arrayOfClass = new Class[2];
                    arrayOfClass[0] = String.class;
                    arrayOfClass[1] = IPackageStatsObserver.class;
                    getPackageSizeInfo = pm.getClass().getMethod(
                            "getPackageSizeInfo", arrayOfClass);
                } else {
                    getPackageSizeInfo = pm.getClass().getDeclaredMethod(
                            "getPackageSizeInfo", String.class,
                            IPackageStatsObserver.class);
                }
                // 调用该函数，并且给其分配参数 ，待调用流程完成后会回调PkgSizeObserver类的函数
                getPackageSizeInfo.invoke(pm, appItemInfo.getAppPackageName(),
                        new PkgSizeObserver(appItemInfo, size, isQueryAll));
            } catch (Exception ex) {
                ex.printStackTrace();
                throw ex;
            }
        }
    }

    /**
     * <br>
     * 类描述:PkgSizeObserver <br>
     * 功能详细描述:
     *
     * @author zoupingyuan
     * @date [2012-9-29]
     */
    public class PkgSizeObserver extends IPackageStatsObserver.Stub {

        private AppItemInfo mTempAppItemInfo = new AppItemInfo();
        private int mNumber;
        private long mOneAppCacheSize;
        private boolean mIsQueryAll;

        public PkgSizeObserver(AppItemInfo appItemInfo, int size,
                               boolean isQueryAll) {
            mTempAppItemInfo = appItemInfo;
            mNumber = size;
            mIsQueryAll = isQueryAll;
        }

        /***
         * 回调函数，
         *
         * @param pStats    ,返回数据封装在PackageStats对象中
         * @param succeeded 代表回调成功
         */
        @Override
        public void onGetStatsCompleted(PackageStats pStats, boolean succeeded)
                throws RemoteException {
            mOneAppCacheSize = 0;
            mSizeAtomicInteger.incrementAndGet();
            mTempAppItemInfo.setAppLabel(mTempAppItemInfo.getAppLabel());
            if (Machine.HAS_SDK_JELLY_BEAN_MR1) {
                mOneAppCacheSize = pStats.cacheSize + pStats.externalCacheSize;
            } else {
                mOneAppCacheSize = pStats.cacheSize;
            }

            mTempAppItemInfo.setAppCacheSize(mOneAppCacheSize);
            mTempAppItemInfo.setAppDataSize(pStats.dataSize);
            mTempAppItemInfo.setAppCodeSize(pStats.codeSize);

            mCacheSize = mCacheSize + mOneAppCacheSize;

            if (mIsQueryAll) {
                // 若是扫描全部的应用，则通知正在扫描的应用包名
                CleanEventManager.getInstance().sendScanPathEvent(
                        CleanScanPathEvent.SysCache,
                        pStats.packageName);
            }

            if (mSizeAtomicInteger.get() == mNumber) {
                if (mIsQueryAll) {
                    // 扫描全部，则通知清理界面扫描结果
                    CleanEventManager.getInstance()
                            .sendSysCacheSize(mCacheSize);
                    CleanEventManager.getInstance().sendSysCacheScanDoneEvent();
                    AppManager.getIntance().setAllAppCacheSize(mCacheSize);
                } else if (mIsAppInstall) {
                    // 监听应用安装，则将扫描单个的应用添加到应用管理器中的当前缓存总值
                    AppManager.getIntance().addOneAppCache(mCacheSize);
                } else {
                    // 查询单个应用，通知单个的扫描结果
                    CleanEventManager.getInstance().sendSingleSysCacheSize(
                            mTempAppItemInfo.getAppPackageName(), mCacheSize);
                }
                if (mListener != null) {
                    mListener.onGetCacheListComplete();
                }
            }
        }
    }

    /**
     * 不用root一键清除所有程序缓存
     */
    public static void clearAllCache(final Context context) {
        if (Build.VERSION.SDK_INT >= 23) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    cleanCacheForM(context.getApplicationContext());
                }
            }).start();
        } else {
            cleanCacheBeforeM(context);
        }
    }

    /**
     * 清缓存：6.0版本之前有效
     */
    private static void cleanCacheBeforeM(Context context) {
        try {
            PackageManager pm = context.getPackageManager();
            Class[] arrayOfClass = new Class[2];
            Class localClass2 = Long.TYPE;
            arrayOfClass[0] = localClass2;
            arrayOfClass[1] = IPackageDataObserver.class;
            Method localMethod = pm.getClass().getMethod(
                    "freeStorageAndNotify", arrayOfClass);
            Long localLong = Long.valueOf(getEnvironmentSize() - 1L);
            Object[] arrayOfObject = new Object[2];
            arrayOfObject[0] = localLong;
            localMethod.invoke(pm, localLong, new IPackageDataObserver.Stub() {
                public void onRemoveCompleted(String packageName,
                                              boolean succeeded) throws RemoteException {
                    // 回调函数，当succeeded为true时，表示清除成功，重新刷新缓存列表
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static long getEnvironmentSize() {
        File localFile = Environment.getDataDirectory();
        long l1;
        if (localFile == null) {
            l1 = 0L;
        }
        while (true) {
            String str = localFile.getPath();
            StatFs localStatFs = new StatFs(str);
            long l2 = localStatFs.getBlockSize();
            l1 = localStatFs.getBlockCount() * l2;
            return l1;
        }
    }

    /**
     * 清缓存：6.0版本之后有效
     */
    public static void cleanCacheForM(Context context) {
        if (!isExternalStorageWritable()) {
            return;
        }
        CacheFileFilter fileFilter = new CacheFileFilter();
        File externalFilesDir = context.getExternalCacheDir();
        File externalData;
        try {
            externalData = externalFilesDir.getParentFile().getParentFile();
        } catch (Exception e) {
            e.printStackTrace();
            externalData = new File(Environment.getExternalStorageDirectory().getPath()
                    + File.separator + "Android" + File.separator + "data");
        }
        File[] files = externalData.listFiles();
        for (File file : files) {
            File[] cacheFolder = file.listFiles(fileFilter);
            if (cacheFolder == null) {
                continue;
            }
            for (File cache : cacheFolder) {
                FileUtil.deleteCategory(cache.getPath());
            }
        }
    }

    /**
     * 缓存文件夹过滤器
     *
     * @author chenbenbin
     */
    private static class CacheFileFilter implements FileFilter {
        @Override
        public boolean accept(File pathname) {
            return pathname.getName().toLowerCase().equals("cache");
        }
    }

    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    /**
     * 设置监听数据写入完毕接口
     *
     * @param listener
     */
    public void setOnAppSizeCompleteListener(AppSizeCompleteListener listener) {
        mListener = listener;
    }

    /**
     * 数据写入完毕监听接口
     * com.gto.zero.zboost.function.clean.manager.AppSizeCompleteListener
     *
     * @author wangying <br/>
     *         create at 2015-1-22 下午9:52:27
     */
    public interface AppSizeCompleteListener {
        void onGetCacheListComplete();
    }

}
