package com.jb.filemanager.function.search;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Process;

import com.jb.filemanager.TheApplication;
import com.jb.filemanager.function.search.database.ExternalStorageProvider;
import com.jb.filemanager.function.search.event.SearchFinishEvent;
import com.jb.filemanager.function.search.modle.FileInfo;
import com.jb.filemanager.function.search.view.SearchActivity;
import com.jb.filemanager.util.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nieyh on 17-7-5.
 * 搜索管理器 <br/>
 * 为何选用数据库作为最终的搜索方式 <br/>
 * <ol>
 *     <li>经过我半天的研究发现在搜索方面还是B+Tree更加效率高, 而Sqlite使用的就是优化版的B+Tree,并且在内存以及硬盘读取方面都做了相应的优化.</li>
 *     <li>可以考虑使用索引, 但是索引不支持模糊搜索(不支持between, or, like), 并且数据量不大的数据库表没有必要使用索引.</li>
 * </ol>
 *
 * 功能描述：<br/>
 * <ol>
 *     <li>每隔一段时间{@link #INTERVAL_DEEP_SCAN_TIME} 全局扫描外部存储一遍</li>
 *     <li>将扫描的数据一次性插入到数据库中</li>
 *     <li>********************************************未完待续..***************************************</li>
 * </ol>
 */

public class SearchManager {
    //TAG
    public static final String TAG = "SearchManager";
    //搜索中
    private final int STATE_SCANING = 0x01;
    //插入数据中
    private final int STATE_UDATEDATA = 0x02;
    //获取数据中
    private final int STATE_GETDATA = 0x03;
    //空闲
    private final int STATE_IDLE = 0x04;

    private static SearchManager sInstance;
    //每20分钟深度扫描一次
    private static final long INTERVAL_DEEP_SCAN_TIME = 20 * 60 * 1000;
    //不正常情况下 需要等待的时间
    private static final long UNNORMAL_WAIT_TIME = 2 * 1000;
    //最长搜索等待时间 8秒 以后用到
    private static final long MAX_SEARCH_WAIT_TIME = 8 * 1000;
    //默认为已经搜索完成 设置同步标示 性能会有所下降 但是保证同步
    private volatile int mState = STATE_IDLE;
    //线程执行者 一般专门用于深度扫描
    private HandlerThread mTaskWorker = new HandlerThread("scan_thread", Process.THREAD_PRIORITY_BACKGROUND);
    //消息循环处理者
    private Handler mHandler;
    //遍历者
    private Traverser mTraverser = new Traverser();
    //请求计数器
    private volatile int mQuestCount = 0;
    //数据库操作
    private ExternalStorageProvider mExternalStorageProvider;

    private SearchManager() {
        if (!mTaskWorker.isAlive()) {
            mTaskWorker.start();
            mHandler = new Handler(mTaskWorker.getLooper());
            Logger.w(SearchManager.TAG, "contructor");
        }
        mExternalStorageProvider = new ExternalStorageProvider(TheApplication.getAppContext());
    }

    public static SearchManager getInstance() {
        if (sInstance == null) {
            sInstance = new SearchManager();
        }
        return sInstance;
    }

    /**
     * 开始准备自动深度扫描
     * */
    public void toReady() {
        if (mHandler != null) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (mState == STATE_GETDATA) {
                        // 当用户获取数据时 则等待一段时间 再来查看状态
                        mHandler.postDelayed(this, UNNORMAL_WAIT_TIME);
                        return;
                    }
                    //开始深度扫描
                    mState = STATE_SCANING;
                    //文件列表
                    ArrayList<FileInfo> fileInfoList = mTraverser.traverseExternalFileSystem();
                    //开始更新数据
                    mState = STATE_UDATEDATA;
                    //插入数据
                    mExternalStorageProvider.insertData(fileInfoList);
                    //当存在请求时
                    if (mQuestCount > 0) {
                        TheApplication.getGlobalEventBus().post(new SearchFinishEvent(fileInfoList));
                        mQuestCount = 0;
                    }
                    //扫描完成
                    mState = STATE_IDLE;
                    //每隔一段事件扫描一次
                    mHandler.postDelayed(this, INTERVAL_DEEP_SCAN_TIME);
                }
            });
        }
    }

    /**
     * 申请搜索
     * @param parameter 搜索字段
     * */
    public void requestSearch(final String parameter) {
        if (parameter == null) {
            return;
        }
        //当正在深度扫描
        if (mState != STATE_IDLE) {
            //则等待一段时间
            TheApplication.getInstance().postRunOnShortTaskThread(new Runnable() {
                @Override
                public void run() {
                    //当还在深度扫描状态中时
                    if (mState == STATE_SCANING) {
                        //直接从数据库中获取数据
                        if (mExternalStorageProvider.checkHasData()) {
                            ArrayList<FileInfo> fileInfoList = mExternalStorageProvider.searchData(parameter);
                            //如果存在数据则直接显示数据库数据
                            TheApplication.getGlobalEventBus().post(new SearchFinishEvent(fileInfoList));
                            return;
                        }
                        //数据库没有数据的话 就只能等待了
                        //增加请求数量
                        //必须非mHandler对应线程中设置请求数目
                        mQuestCount ++;
                    }
                }
            }, MAX_SEARCH_WAIT_TIME);
        } else {
            //在当前线程中直接设置成获取数据状态
            mState = STATE_GETDATA;
            //直接获取数据
            if (mHandler != null) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        ArrayList<FileInfo> fileInfoList = mExternalStorageProvider.searchData(parameter);
                        TheApplication.getGlobalEventBus().post(new SearchFinishEvent(fileInfoList));
                        mState = STATE_IDLE;
                    }
                });
            } else {
                mState = STATE_IDLE;
            }
        }
    }
//
//    /**
//     * 检测查询的结果 <br/>
//     * 用于当页面在跳转的过程中 数据已经加载完成的情况
//     * @param parameter 搜索参数
//     * */
//    public void checkSearchResult(final String parameter) {
//        //当处于空闲的
//        if (mState == STATE_IDLE) {
//            mState = STATE_GETDATA;
//            if (mHandler != null) {
//                mHandler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        List<FileInfo> fileInfoList = mExternalStorageProvider.searchData(parameter);
//                        TheApplication.getGlobalEventBus().post(new SearchFinishEvent(fileInfoList));
//                        mState = STATE_IDLE;
//                    }
//                });
//            }
//        }
//    }

    /**
     * 取消搜索
     * */
    public void cancelSearch() {
        mQuestCount--;
        if (mQuestCount < 0) {
            mQuestCount = 0;
        }
    }
}
