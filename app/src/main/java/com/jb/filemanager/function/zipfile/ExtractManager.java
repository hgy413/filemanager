package com.jb.filemanager.function.zipfile;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.jb.filemanager.R;
import com.jb.filemanager.TheApplication;
import com.jb.filemanager.function.filebrowser.FileBrowserActivity;
import com.jb.filemanager.function.zipfile.bean.ZipPreviewFileBean;
import com.jb.filemanager.function.zipfile.dialog.ExtractErrorDialog;
import com.jb.filemanager.function.zipfile.dialog.ExtractSnackBar;
import com.jb.filemanager.function.zipfile.listener.ExtractingFilesListener;
import com.jb.filemanager.function.zipfile.receiver.NotificationClickReceiver;
import com.jb.filemanager.function.zipfile.task.ExtractFilesTask;
import com.jb.filemanager.function.zipfile.task.ExtractPackFileTask;
import com.jb.filemanager.function.zipfile.util.FileUtils;
import com.jb.filemanager.util.APIUtil;

import java.util.List;

import static com.jb.filemanager.home.fragment.storage.StorageFragment.PARAM_PATH;

/**
 * Created by xiaoyu on 2017/7/6 10:38.<p>
 * 解压缩管理
 * <p>1. 待解压文件List</p>
 * <p>2. 单个压缩包文件(zip 或 rar)</p>
 */

public final class ExtractManager implements ExtractingFilesListener, View.OnClickListener, View.OnAttachStateChangeListener {

    private static final int EXTRACT_NOTIFICATION_ID = 0x12138;
    private static final int EXTRACT_COMPLETE_NOTIFICATION_ID = 0x12158;
    // 正在解压的路径刷新间隔, 需要严格控制, 频率过快会引发系统崩溃, 手机自动锁屏等问题
    private static final long FLUSH_UI_INTERVAL_TIME = 380L;
    private static ExtractManager sInstance;
    private Notification mNotification;
    private ProgressBar mDialogProgressBar;

    private ExtractManager() {
        mContext = TheApplication.getAppContext();
        mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
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
    private float mPercent;
    private View mProgressDialogView = null;
    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mParams;
    private ExtractFilesTask mExtractFilesTask = null;
    private ExtractPackFileTask mExtractPackFileTask = null;

    private RemoteViews mRemoteViews;
    private long lastUpdateUiTime;
    private String mFileName;

    public void extractFiles(String zipFilePath, String password, List<ZipPreviewFileBean> data) {
        if (mIsExtracting && !mIsProgressDialogAttached) {
            addProgressDialogToWindow();
            updateDialogContent();
        } else {
            mExtractFilesTask = new ExtractFilesTask();
            mExtractFilesTask.setListener(this);
            mFileName = FileUtils.getFileName(zipFilePath);
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
            mFileName = FileUtils.getFileName(zipFilePath);
            mExtractPackFileTask.execute(zipFilePath, password);
        }
    }

    /**
     * 添加至窗口并更新进度弹窗的显示内容
     */
    private void updateDialogContent() {
        if (!mIsProgressDialogAttached) return;// 未显示时不更新Dialog的TextView
        mDialogProgressBar.setProgress((int) (mPercent * 100));
    }

    /**
     * 添加进度弹窗到窗口
     */
    public void addProgressDialogToWindow() {
        if (!mIsProgressDialogAttached) {
            if (mParams == null) {
                mParams = new WindowManager.LayoutParams();
                mParams.width = WindowManager.LayoutParams.MATCH_PARENT;
                mParams.height = WindowManager.LayoutParams.MATCH_PARENT;
                mParams.format = PixelFormat.RGBA_8888;

                mParams.flags = WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN;
                mParams.gravity = Gravity.CENTER;

                mParams.type = WindowManager.LayoutParams.TYPE_TOAST;
            }

            if (mProgressDialogView == null) {
                mProgressDialogView = View.inflate(mContext, R.layout.view_extract_progress, null);
                mProgressDialogView.addOnAttachStateChangeListener(this);
                mDialogProgressBar = (ProgressBar) mProgressDialogView.findViewById(R.id.extract_dialog_progress);
                View progressCancel = mProgressDialogView.findViewById(R.id.view_extract_cancel);
                progressCancel.setOnClickListener(this);
                View progressBackground = mProgressDialogView.findViewById(R.id.view_extract_background_handle);
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
        mPercent = 0;
        updateNotificationContent();
        updateDialogContent();
    }

    @Override
    public void onExtractingFile(float percent) {
        mPercent = percent;
        if (System.currentTimeMillis() - lastUpdateUiTime > FLUSH_UI_INTERVAL_TIME) {
            lastUpdateUiTime = System.currentTimeMillis();
            updateDialogContent();
            updateNotificationContent();
        }
    }

    @Override
    public void onPostExtractFiles(String savePath) {
        showMessage("解压完成");
        sendExtractAccomplishNotification(savePath);
//        Toast.makeText(mContext, mContext.getString(R.string.extract_to, savePath), Toast.LENGTH_SHORT).show();
        onTaskOverState();
    }

    @Override
    public void onCancelExtractFiles() {
        showMessage("解压取消");
        Toast.makeText(mContext, mContext.getString(R.string.extract_cancel), Toast.LENGTH_SHORT).show();
        onTaskOverState();
    }

    @Override
    public void onExtractError() {
        showMessage("解压错误");
        showExtractErrorDialog();
        Toast.makeText(mContext, mContext.getString(R.string.extract_error), Toast.LENGTH_SHORT).show();
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
//        Log.e("extract", message);
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
            case R.id.view_extract_background_handle:
                hideProgressDialogFromWindow();
                ZipStatistics.upload(ZipStatistics.ZIP_SMALL);
                break;
        }
    }

    public boolean isProgressDialogAttached() {
        return mIsProgressDialogAttached;
    }

    /**
     * 显示解压失败的弹窗
     */
    private void showExtractErrorDialog() {
        if (TheApplication.sCurrentActivity == null) return;
        ExtractErrorDialog extractErrorDialog = new ExtractErrorDialog(TheApplication.sCurrentActivity, mFileName);
        extractErrorDialog.setSubTextVisibility(false);
        extractErrorDialog.show();
    }

    /////////////////////////////////
    //// 进度条窗口连接状态改变回调接口
    ///////////////////////////////
    @Override
    public void onViewAttachedToWindow(View v) {
//        Log.e("dialog", "onViewAttachedToWindow");
        mIsProgressDialogAttached = true;
        ZipStatistics.upload(ZipStatistics.ZIP_GO);
    }

    // 若点击物理返回键可隐藏弹窗, 默认不取消任务, 后台继续运行
    @Override
    public void onViewDetachedFromWindow(View v) {
//        Log.e("dialog", "onViewDetachedToWindow");
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

    private Context mContext;

    private void updateNotificationContent() {
        TheApplication.postRunOnShortTaskThread(new Runnable() {
            @Override
            public void run() {

                if (mContext == null) {
                    mContext = TheApplication.getAppContext();
                }

                if (mRemoteViews == null) {
                    mRemoteViews = new RemoteViews(mContext.getPackageName(), R.layout.notification_extract);

                }

                if (mNotification == null) {
                    Notification.Builder builder = new Notification.Builder(mContext);
                    builder.setAutoCancel(false);
                    builder.setSmallIcon(R.drawable.zip_icon);
                    builder.setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.zip_icon));
                    mNotification = APIUtil.build(builder);
                    mNotification.when = System.currentTimeMillis();
                    mNotification.flags = Notification.FLAG_NO_CLEAR;
                    mNotification.icon = R.drawable.zip_icon;
                    mNotification.contentView = mRemoteViews;
                }

                mRemoteViews.setTextViewText(R.id.extract_noti_title, mFileName);
                mRemoteViews.setInt(R.id.extract_noti_progress_tv, "setProgress", (int) (mPercent * 100));
                mNotification.contentIntent = PendingIntent.getBroadcast(mContext, 0, new Intent(mContext, NotificationClickReceiver.class),
                        PendingIntent.FLAG_UPDATE_CURRENT);

                NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(EXTRACT_NOTIFICATION_ID, mNotification);
            }
        });

    }

    /**
     * 发送解压完成的通知
     *
     * @param savePath 路径
     */
    private void sendExtractAccomplishNotification(final String savePath) {
        TheApplication.postRunOnShortTaskThread(new Runnable() {
            @Override
            public void run() {

                if (mContext == null) {
                    mContext = TheApplication.getAppContext();
                }

                ExtractSnackBar.showSnackBar(mContext, savePath);

                RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(), R.layout.notification_extract_finish);
                remoteViews.setTextViewText(R.id.extract_noti_finish_dest_path, mContext.getString(R.string.extract_to, savePath));

                Notification.Builder builder = new Notification.Builder(mContext);
                builder.setSmallIcon(R.drawable.zip_icon);
                builder.setAutoCancel(true);
                builder.setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.zip_icon));
                Notification notification = APIUtil.build(builder);
                notification.when = System.currentTimeMillis();
                notification.icon = R.drawable.zip_icon;
                notification.contentView = remoteViews;

                Intent intent = new Intent(mContext, FileBrowserActivity.class);
                if (!(mContext instanceof Activity)) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }
                intent.putExtra(PARAM_PATH, savePath);
                notification.contentIntent = PendingIntent.getActivity(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(EXTRACT_COMPLETE_NOTIFICATION_ID, notification);
            }
        });
    }

    private void cancelExtractNotification() {
        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(EXTRACT_NOTIFICATION_ID);
    }
}
