package com.jb.filemanager.function.applock.view;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.ViewManager;
import android.view.WindowManager;

import com.jb.filemanager.util.device.Machine;

/**
 * 相机检测View
 *
 * @author chenbenbin
 */
public class CameraPermissionCheckView extends ViewHolder {
    private ViewManager mWindowManager;
    private AntiPeepCameraHolder mCameraHolder;
    private Context mContext;
    private boolean mOpen = false;

    public CameraPermissionCheckView(Context context) {
        mContext = context.getApplicationContext();
        mWindowManager = (WindowManager) mContext
                .getSystemService(Context.WINDOW_SERVICE);
    }

    public boolean show() {
        if (mCameraHolder == null) {
            // 添加相机视图
            mCameraHolder = new AntiPeepCameraHolder(mContext);
            View contentView = mCameraHolder.getContentView();
            setContentView(contentView);
            WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                    2, 2, WindowManager.LayoutParams.TYPE_PHONE,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                            | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                    PixelFormat.TRANSLUCENT);
            if (Machine.HAS_SDK_ICS) {
                params.gravity = Gravity.BOTTOM | Gravity.END;
            } else {
                params.gravity = Gravity.BOTTOM | Gravity.RIGHT;
            }
            params.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
            if (Build.VERSION.SDK_INT >= 19) {
                params.type = WindowManager.LayoutParams.TYPE_TOAST;
            } else {
                //低版本使用Toast无法接受点击事件 只能兼容使用TYPE_SYSTEM_ALERT
                params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
            }
            mWindowManager.addView(contentView, params);
        }
        if (mCameraHolder != null) {
            // 打开相机
            mOpen = mCameraHolder.open();
        }
        return mOpen;
    }

    /**
     * 自动拍照
     */
    public void captureAuto() {
        mCameraHolder.captureAuto();
    }

    public void close() {
        if (mCameraHolder == null) {
            return;
        }
        try {
            mWindowManager.removeView(getContentView());
            mCameraHolder.close();
            mCameraHolder = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
