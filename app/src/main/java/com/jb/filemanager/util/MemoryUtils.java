package com.jb.filemanager.util;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Debug;
import android.os.Environment;
import android.os.StatFs;

import com.jb.filemanager.TheApplication;
import com.jb.filemanager.manager.ProcessManager;

import java.io.File;

/**
 * Created by xiaoyu on 2016/10/17.
 */

public class MemoryUtils {

    /**
     * 获得一个或者多个进程占用的内存<br>
     * <b>单位B</b>
     *
     * @param pid
     * @param context
     * @return
     */
    public static int getProcessMemoryTotalPss(Context context, int[] pid) {
        ActivityManager actMgr = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        // 此MemoryInfo位于android.os.Debug.MemoryInfo包中，用来统计进程的内存信息
        Debug.MemoryInfo[] memoryInfo = actMgr.getProcessMemoryInfo(pid);
        int memory = 0;
        if (memoryInfo != null) {
            for (Debug.MemoryInfo m : memoryInfo) {
                memory += m.getTotalPss();
            }
        }
        // 读取进程Pss内存值，包含均分的程序内存
        return memory;
    }

    /**
     * 获取设备总内存 存储空间
     *
     * @return
     */
    public static long getTotalMemory() {
        return getSDTotalSize();
//        return getMemoryInfo()[0];
    }

    /**
     * 获取设备可用内存 存储空间
     *
     * @return
     */
    public static long getAvailableMemory() {
        return getSDAvailableSize();
//        return getMemoryInfo()[1];
    }

    /**
     * 根据路径获取内存状态
     *
     * @param
     * @return 总空间, 可用空间
     */
    private static long[] getMemoryInfo() {
        // 得一个磁盘状态对象
        StatFs stat = new StatFs(Environment.getExternalStorageDirectory()
                .getAbsolutePath());
        long blockSize = stat.getBlockSize();   // 获得一个扇区的大小
        long totalBlocks = stat.getBlockCount();    // 获得扇区的总数
        long availableBlocks = stat.getAvailableBlocks();   // 获得可用的扇区数量
        // 总空间
        long totalMemory = totalBlocks * blockSize;
        // 可用空间
        long availableMemory = availableBlocks * blockSize;

        return new long[]{totalMemory, availableMemory};
    }

    /**
     * 获取可用RAM运存大小
     */
    public static long getAvailableRam(Context context) {
        // 获取Android当前可用内存大小
//        ActivityManager am = (ActivityManager) context.getSystemService(
//                Context.ACTIVITY_SERVICE);
//        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
//        am.getMemoryInfo(mi);
        // modify date : 2017年2月7日14:17:22
        // By xiaoyu.
        ProcessManager instance = ProcessManager.getInstance(context);
        return instance.getAvaliableMemory() * 1024;
    }

    /**
     * 获取总RAM运存大小
     */
    public static long getTotalRam(Context context) {
        // modify date : 2017年2月7日14:15:18
        // By xiaoyu.
        ProcessManager instance = ProcessManager.getInstance(context);
        return instance.getTotalMemory() * 1024;
        /*ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(mi);
        Log.e("mem","mi.totalMem" + ConvertUtils.formatFileSize(mi.totalMem));
        Log.e("mem","totalMem" + ConvertUtils.formatFileSize(Runtime.getRuntime().maxMemory()));
        return mi.totalMem;*/
    }

    /**
     * 获得SD卡总大小
     *
     * @return
     * @date 2017年1月16日19:02:29
     */
    public static long getSDTotalSize() {
        long result = 0;
        try {
            StorageUtil.SDCardInfo sdCardInfo = StorageUtil.getSDCardInfo(TheApplication.getAppContext());
            result = sdCardInfo.mTotal;
            //File path = Environment.getExternalStorageDirectory();
            //StatFs stat = new StatFs(path.getPath());
            //long blockSize = stat.getBlockSize();
            //long totalBlocks = stat.getBlockCount();
            //result = blockSize * totalBlocks;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 获得sd卡剩余容量，即可用大小
     *
     * @return
     * @date 2017年1月16日19:02:29
     */
    public static long getSDAvailableSize() {
        long result = 0;
        try {
            StorageUtil.SDCardInfo sdCardInfo = StorageUtil.getSDCardInfo(TheApplication.getAppContext());
            result = sdCardInfo.mFree;
            //File path = Environment.getExternalStorageDirectory();
            //StatFs stat = new StatFs(path.getPath());
            //long blockSize = stat.getBlockSize();
            //long availableBlocks = stat.getAvailableBlocks();
            //result = blockSize * availableBlocks;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 获得机身内存总大小
     *
     * @return
     * @date 2017年1月16日19:02:29
     */
    public static long getRomTotalSize() {
        long result = 0;
        try {
            File path = Environment.getDataDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long totalBlocks = stat.getBlockCount();
            result = blockSize * totalBlocks;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 获得机身可用内存
     *
     * @return
     * @date 2017年1月16日19:02:29
     */
    public static long getRomAvailableSize() {
        long result = 0;
        try {
            File path = Environment.getDataDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long availableBlocks = stat.getAvailableBlocks();
            result = blockSize * availableBlocks;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
