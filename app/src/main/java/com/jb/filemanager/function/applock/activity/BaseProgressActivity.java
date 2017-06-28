package com.jb.filemanager.function.applock.activity;

import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.support.annotation.UiThread;
import android.support.v7.widget.ContentFrameLayout;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

import com.jb.filemanager.ui.widget.ProgressWheel;
import com.jb.filemanager.util.APIUtil;
import com.jb.filemanager.util.Logger;

/**
 * Created by nieyh on 2016/12/30.<br>
 * 描述：<br/>
 * 当activity需要进度条来表示数据加载中时，可以继承此类 {@link BaseProgressActivity} <br/>
 * 方法说明: <br/>
 * {@link #resetProgressWheelColor(int)} 用于重新设置进度条的颜色 参数: 颜色值<br>
 * {@link #resetProgressWheelBarWidth(int)} 用于重新设置进度条的条的宽度 参数: 资源id<br>
 * {@link #resetProgressWheelResourceColor(int)} 用于重新设置进度条的颜色 参数: 资源id<br>
 * {@link #startSpin()} 开始动画<br>
 * {@link #isSpining()} 是否正在动画<br>
 * {@link #stopSpin()} 停止动画<br>
 * 注意：<br>
 * <b>操作进度条, 请在setContentView 方法执行之后<b/>
 */

public abstract class BaseProgressActivity extends BaseHomeWatcherActivity {

    private ProgressWheel mLoading;
    private final String TAG = "BaseProgressActivity";

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        addDefaultProgressWheelToRoot();
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        super.setContentView(view, params);
        addDefaultProgressWheelToRoot();
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        addDefaultProgressWheelToRoot();
    }



    /**
     * 添加进度条到根目录下
     * 默认情况下：<br/>
     * 1、进度条为蓝色
     * 2、进度条宽度为3dp
     * 3、进度条开始动画
     */
    private void addDefaultProgressWheelToRoot() {
        ContentFrameLayout frameLayout = (ContentFrameLayout) findViewById(android.R.id.content);
        if (frameLayout != null) {
            mLoading = new ProgressWheel(this);
            int size = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60, getResources().getDisplayMetrics());
            ContentFrameLayout.LayoutParams layoutParams = new ContentFrameLayout.LayoutParams(size, size);
            layoutParams.gravity = Gravity.CENTER;
            mLoading.setLayoutParams(layoutParams);
            mLoading.setBarColor(0xFF0d96fc);
            mLoading.setBarWidth((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3, getResources().getDisplayMetrics()));
            Logger.w(TAG, String.valueOf(size));
            frameLayout.addView(mLoading);
        }
    }

    /**
     * @param resourceId 资源文件id
     */
    @UiThread
    protected void resetProgressWheelResourceColor(@ColorRes int resourceId) {
        int color = APIUtil.getColor(this, resourceId);
        mLoading.setBarColor(color);
    }


    /**
     * @param color 颜色值
     */
    @UiThread
    protected void resetProgressWheelColor(@ColorInt int color) {
        mLoading.setBarColor(color);
    }

    /**
     * @param resourceId 资源文件id
     */
    @UiThread
    protected void resetProgressWheelBarWidth(@DimenRes int resourceId) {
        int dimen = getResources().getDimensionPixelSize(resourceId);
        mLoading.setBarWidth(dimen);
    }

    /**
     * @param dip dp值
     */
    @UiThread
    protected void resetProgressWheelBarWidthDP(int dip) {
        int dimen = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, getResources().getDisplayMetrics());
        mLoading.setBarWidth(dimen);
    }

    /**
     * 开始加载动画
     * */
    @UiThread
    protected void startSpin() {
        if (mLoading != null && !isSpining()) {
            mLoading.spin();
        }
    }

    /**
     * 是否正在执行动画
     * */
    protected boolean isSpining() {
        if (mLoading != null) {
            return mLoading.isSpinning();
        }
        return false;
    }

    /**
     * 停止执行动画
     * */
    @UiThread
    protected void stopSpin() {
        if (mLoading != null) {
            mLoading.stopSpinning();
        }
    }

    @Override
    protected void onDestroy() {
        if (isSpining()) {
            stopSpin();
        }
        super.onDestroy();
    }
}
