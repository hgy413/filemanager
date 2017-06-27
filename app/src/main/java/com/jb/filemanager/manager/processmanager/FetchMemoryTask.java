package com.jb.filemanager.manager.processmanager;


import android.app.ActivityManager;
import android.content.Context;
import android.os.Debug;

import com.jb.filemanager.function.scanframe.bean.appBean.RunningAppBean;
import com.jb.filemanager.util.ConvertUtils;
import com.jb.filemanager.util.Logger;
import com.jb.filemanager.util.MemoryUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * 用于获取某一进程内存占用的任务
 *
 * @author lishen
 */
public class FetchMemoryTask implements Runnable {

    private Context mContext;
    private CountDownLatch mLatch;
    private RunningAppBean mApp;

    public FetchMemoryTask(Context context, CountDownLatch latch, RunningAppBean app) {
        mContext = context;
        mLatch = latch;
        mApp = app;
    }

    @Override
    public void run() {
        final int[] pids = ConvertUtils.toIntArray(mApp.mPids);
//        Logger.d(ProcessManager.TAG, "获取内存值：" + mApp.mPids.size());
        mApp.mMemory = getProcessMemoryTotalPss(pids);
        mLatch.countDown();
    }

    /**
     * 获得该进程占用的内存
     *
     * @param pid
     * @return
     */
    public int getProcessMemoryTotalPss(int[] pid) {
        ActivityManager actMgr = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        if (pid.length == 0) {
            List<ActivityManager.RunningServiceInfo> runningServices = actMgr.getRunningServices(100);
            ArrayList<Integer> pidArr = new ArrayList<>();
            for (ActivityManager.RunningServiceInfo service : runningServices) {
                if (service.service.getPackageName().equals(mApp.mPackageName)) {
                    boolean isExist = false;
                    for (Integer integer : pidArr) {
                        if (integer == service.pid) {
                            isExist = true;
                        }
                    }
                    if (!isExist) {
                        pidArr.add(service.pid);
                    }
                }
            }
            mApp.mPids = pidArr;
            int[] pid1 = ConvertUtils.toIntArray(pidArr);
            Logger.e("MemoryTast", "isexist = " + pid1);
            return MemoryUtils.getProcessMemoryTotalPss(mContext, pid1);
        }
        Logger.e("MemoryTast", "enter getProcessMemoryTotalPss 传入长度" + pid.length);
        // 此MemoryInfo位于android.os.Debug.MemoryInfo包中，用来统计进程的内存信息
        Debug.MemoryInfo[] memoryInfo = actMgr.getProcessMemoryInfo(pid);
        Logger.e("MemoryTast", "memoryInfo " + (memoryInfo == null));
        int memory = 0;
        if (memoryInfo != null) {
            Logger.e("MemoryTast", "memoryInfo " + memoryInfo.length);
            for (Debug.MemoryInfo m : memoryInfo) {
                memory += m.getTotalPss();
                Logger.e("MemoryTast", "size=" + m.getTotalPss());
            }
        }
//        if (memory == 0) {
//            Runtime runtime = Runtime.getRuntime();
//            try {
//                Process exec = runtime.exec("dumpsys meminfo " + mApp.mPackageName);
//                Logger.e("FetMemory", "执行 exec" + mApp.mPackageName);
//                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(exec.getInputStream()));
//                String line = "";
//                while ((line = bufferedReader.readLine()) != null) {
//                    Logger.e("FetMemory", line);
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
        // 读取进程Pss内存值，包含均分的程序内存
        return memory;
    }
}
