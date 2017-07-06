package com.jb.filemanager.function.zipfile;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.jb.filemanager.R;
import com.jb.filemanager.TheApplication;
import com.jb.filemanager.function.zipfile.bean.ZipPreviewFileBean;
import com.jb.filemanager.function.zipfile.listener.ExtractingFilesListener;
import com.jb.filemanager.function.zipfile.receiver.NotificationClickReceiver;
import com.jb.filemanager.function.zipfile.task.ExtractFilesTask;
import com.jb.filemanager.function.zipfile.task.ExtractPackFileTask;

import java.util.List;

/**
 * Created by xiaoyu on 2017/7/6 10:38.<p>
 * 解压缩管理
 * <p>1. 待解压文件List</p>
 * <p>2. 单个压缩包文件(zip 或 rar)</p>
 */

public final class ExtractManager implements ExtractingFilesListener, View.OnClickListener, View.OnAttachStateChangeListener {

    private static final int EXTRACT_NOTIFICATION_ID = 0x12138;
    private static ExtractManager sInstance;
    private Notification mNotification;

    private ExtractManager() {
        mWindowManager = (WindowManager) TheApplication.getAppContext().getSystemService(Context.WINDOW_SERVICE);
    }

    public static ExtractManager getInstance() {
        if (sInstance == null) {
            synchronized (ExtractManager.class) {
                if (sInstance == null) {
                    sInstance = new ExtractManager();
                }
            }
        }
        return sInstance;
    }

    private boolean mIsExtracting = false;
    private boolean mIsProgressDialogAttached = false;
    private String mCurrentExtractingFile = "";
    private View mProgressDialogView = null;
    private TextView mProgressViewPath;
    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mParams;
    private ExtractFilesTask mExtractFilesTask = null;
    private ExtractPackFileTask mExtractPackFileTask = null;

    private RemoteViews mRemoteViews;
    private long lastUpdateUiTime;

    public void extractFiles(String zipFilePath, String password, List<ZipPreviewFileBean> data) {
        if (mIsExtracting && !mIsProgressDialogAttached) {
            addProgressDialogToWindow();
            updateDialogContent();
        } else {
            mExtractFilesTask = new ExtractFilesTask();
            mExtractFilesTask.setListener(this);
            mExtractFilesTask.execute(zipFilePath, password, data);
        }
    }

    public void extractPackFile(String zipFilePath, String password) {
        if (mIsExtracting && !mIsProgressDialogAttached) {
            addProgressDialogToWindow();
            updateDialogContent();
        } else {
            mExtractPackFileTask = new ExtractPackFileTask();
            mExtractPackFileTask.setListener(this);
            mExtractPackFileTask.execute(zipFilePath, password);
        }
    }

    /**
     * 添加至窗口并更新进度弹窗的显示内容
     */
    private void updateDialogContent() {
        if (!mIsProgressDialogAttached) return;// 未显示时不更新Dialog的TextView
        mProgressViewPath.setText(mCurrentExtractingFile);
    }

    /**
     * 添加进度弹窗到窗口
     */
    public void addProgressDialogToWindow() {
        if (!mIsProgressDialogAttached) {
            if (mParams == null) {
                mParams = new WindowManager.LayoutParams();
                mParams.width = WindowManager.LayoutParams.MATCH_PARENT;
                mParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
                mParams.format = PixelFormat.RGBA_8888;

                mParams.flags = WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN;
                mParams.gravity = Gravity.CENTER;

                mParams.type = WindowManager.LayoutParams.TYPE_TOAST;
            }

            if (mProgressDialogView == null) {
                mProgressDialogView = View.inflate(TheApplication.getAppContext(), R.layout.view_extract_progress, null);
                mProgressDialogView.addOnAttachStateChangeListener(this);
                TextView progressViewTitle = (TextView) mProgressDialogView.findViewById(R.id.view_extract_title);
                progressViewTitle.setText("解压中....");
                mProgressViewPath = (TextView) mProgressDialogView.findViewById(R.id.view_extract_path);
                View progressCancel = mProgressDialogView.findViewById(R.id.view_extract_cancel);
                progressCancel.setOnClickListener(this);
                View progressBackground = mProgressDialogView.findViewById(R.id.view_extract_backgroud_handle);
                progressBackground.setOnClickListener(this);
            }
            try {
                mWindowManager.addView(mProgressDialogView, mParams);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 彻底移除
     */
    private void removeProgressDialogFromWindow() {
        if (mIsProgressDialogAttached && mProgressDialogView != null) {
            mWindowManager.removeViewImmediate(mProgressDialogView);
            mProgressDialogView.removeOnAttachStateChangeListener(this);
            mProgressDialogView = null;
            mParams = null;
        }
    }

    /**
     * 暂时隐藏
     */
    public void hideProgressDialogFromWindow() {
        if (mIsProgressDialogAttached && mProgressDialogView != null) {
            mWindowManager.removeViewImmediate(mProgressDialogView);
        }
    }

    @Override
    public void onPreExtractFiles() {
        showMessage("开始解压");
        mIsExtracting = true;
        addProgressDialogToWindow();
        mCurrentExtractingFile = "loading...";
        updateNotificationContent();
        updateDialogContent();
    }

    @Override
    public void onExtractingFile(String filePath) {
        mCurrentExtractingFile = filePath; // 备用
        if (System.currentTimeMillis() - lastUpdateUiTime > 400) {
            lastUpdateUiTime = System.currentTimeMillis();
            updateDialogContent();
            updateNotificationContent();
        }
    }

    @Override
    public void onPostExtractFiles() {
        showMessage("解压完成");
        onTaskOverState();
    }

    @Override
    public void onCancelExtractFiles() {
        showMessage("解压取消");
        onTaskOverState();
    }

    @Override
    public void onExtractError() {
        showMessage("解压错误");
        onTaskOverState();
    }

    /**
     * 三种状态: 结束, 取消, 错误
     */
    private void onTaskOverState() {
        mIsExtracting = false;
        if (mExtractFilesTask != null) {
            mExtractFilesTask.cancel(true);
        }
        mExtractFilesTask = null;
        if (mExtractPackFileTask != null) {
            mExtractPackFileTask.cancel(true);
        }
        mExtractPackFileTask = null;
        removeProgressDialogFromWindow();
        cancelExtractNotification();
    }

    private void showMessage(String message) {
        Log.e("extract", message);
//        Toast.makeText(TheApplication.getAppContext(), message, Toast.LENGTH_SHORT).show();
    }

    /**
     * 弹窗按钮点击事件
     *
     * @param v v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.view_extract_cancel:
                if (mExtractPackFileTask != null) {
                    mExtractPackFileTask.cancel(true);
                }
                if (mExtractFilesTask != null) {
                    mExtractFilesTask.cancel(true);
                }
                removeProgressDialogFromWindow();
                cancelExtractNotification();
                break;
            case R.id.view_extract_backgroud_handle:
                hideProgressDialogFromWindow();
                break;
        }
    }

    public boolean isProgressDialogAttached() {
        return mIsProgressDialogAttached;
    }

    /////////////////////////////////
    //// 进度条窗口连接状态改变回调接口
    ///////////////////////////////
    @Override
    public void onViewAttachedToWindow(View v) {
        Log.e("dialog", "onViewAttachedToWindow");
        mIsProgressDialogAttached = true;
    }

    // 若点击物理返回键可隐藏弹窗, 默认不取消任务, 后台继续运行
    @Override
    public void onViewDetachedFromWindow(View v) {
        Log.e("dialog", "onViewDetachedToWindow");
        mIsProgressDialogAttached = false;
    }

    /**
     * application被强杀, 彻底over
     */
    public void onAppDestroy() {
        if (mProgressDialogView != null) {
            mProgressDialogView.removeOnAttachStateChangeListener(this);
        }
        if (mExtractFilesTask != null) {
            mExtractFilesTask.cancel(true);
            mExtractFilesTask = null;
        }
        if (mExtractPackFileTask != null) {
            mExtractPackFileTask.cancel(true);
            mExtractPackFileTask = null;
        }
        cancelExtractNotification();
        removeProgressDialogFromWindow();
    }

    public boolean isExtracting() {
        return mIsExtracting;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private void updateNotificationContent() {
        if (mNotification == null) {
            mRemoteViews = new RemoteViews(TheApplication.getAppContext().getPackageName(), R.layout.notification_extract);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(TheApplication.getAppContext());
            builder.setOngoing(true).setContent(mRemoteViews).setSmallIcon(R.mipmap.ic_launcher);
            mNotification = builder.build();
        }
        mNotification.contentIntent = PendingIntent.getBroadcast(
                TheApplication.getAppContext(),
                0,
                new Intent(TheApplication.getAppContext(), NotificationClickReceiver.class),
                PendingIntent.FLAG_UPDATE_CURRENT);
        mRemoteViews.setTextViewText(R.id.extract_noti_path, mCurrentExtractingFile);
        NotificationManager notificationManager = (NotificationManager) TheApplication.getAppContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(EXTRACT_NOTIFICATION_ID, mNotification);
    }

    private void cancelExtractNotification() {
        NotificationManager notificationManager = (NotificationManager) TheApplication.getAppContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(EXTRACT_NOTIFICATION_ID);
    }
}
