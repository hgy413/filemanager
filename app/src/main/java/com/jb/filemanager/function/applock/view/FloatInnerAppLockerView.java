package com.jb.filemanager.function.applock.view;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.IntDef;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jb.filemanager.R;
import com.jb.filemanager.TheApplication;
import com.jb.filemanager.receiver.HomeWatcherReceiver;
import com.jb.filemanager.util.AppUtils;
import com.jb.filemanager.util.DrawUtils;
import com.jb.filemanager.util.Logger;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

/**
 * Created by nieyh on 2017/1/5.
 * 应用锁展示的view
 */

public class FloatInnerAppLockerView extends RelativeLayout {

    //返回键
    private ImageView mBack;
    //标题
    private TextView mTitle;
    //忘记
    private TextView mForget;
    //设置按钮
    private ImageView mSetting;
    //对话框
    private View mDialog;
    //图案锁
    private PatternView mPatternView;
    private IFloatAppLockerViewEvtListener mIFloatAppLockerViewEvtListener;

    private HomeWatcherReceiver mHomeKeyEventReceiver;

    private Handler mHandler;

    private View mContentRoot;

    public FloatInnerAppLockerView(Context context) {
        super(context);
        mContentRoot = LayoutInflater.from(context).inflate(R.layout.view_applock_inner_float_layout, this);
        mBack = (ImageView) mContentRoot.findViewById(R.id.common_applock_bar_layout_back);
        mTitle = (TextView) mContentRoot.findViewById(R.id.common_applock_bar_layout_title);
        mSetting = (ImageView) mContentRoot.findViewById(R.id.common_applock_bar_layout_setting);
        ((ImageView)findViewById(R.id.view_applock_inner_float_layout_icon)).setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        mTitle.setText(R.string.app_name);
        mSetting.setVisibility(VISIBLE);
        mSetting.setImageResource(R.drawable.ic_applock_setting2);
        mPatternView = (PatternView) mContentRoot.findViewById(R.id.view_applock_inner_float_layout_patternview);
        mForget = (TextView) mContentRoot.findViewById(R.id.view_applock_inner_float_layout_forget_psd);
        mDialog = mContentRoot.findViewById(R.id.view_applock_inner_float_layout_dialog);
        setBackgroundColor(0xFF44D6C3);
        initListener();
        mHomeKeyEventReceiver = new HomeWatcherReceiver();
    }

    /**
     * 初始化监听器
     */
    private void initListener() {
        mBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIFloatAppLockerViewEvtListener != null) {
                    mIFloatAppLockerViewEvtListener.onBackPress();
                }
            }
        });
        mTitle.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIFloatAppLockerViewEvtListener != null) {
                    mIFloatAppLockerViewEvtListener.onBackPress();
                }
            }
        });
        mForget.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIFloatAppLockerViewEvtListener != null) {
                    mIFloatAppLockerViewEvtListener.onForgetClick(v);
                }
            }
        });
        mSetting.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.setVisibility(VISIBLE);
            }
        });
        mDialog.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.setVisibility(GONE);
            }
        });

        mPatternView.setOnPatternListener(new PatternView.OnPatternListener() {
            @Override
            public void onPatternStart() {
                removeClearErrorWork();
            }

            @Override
            public void onPatternCleared() {

            }

            @Override
            public void onPatternCellAdded(List<PatternView.Cell> pattern) {

            }

            @Override
            public void onPatternDetected(List<PatternView.Cell> pattern) {
                if (mIFloatAppLockerViewEvtListener != null) {
                    mIFloatAppLockerViewEvtListener.onInputCompleted(pattern, null);
                }
            }
        });
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mHandler = new Handler();
        mHomeKeyEventReceiver.setTouchSystemKeyListener(new HomeWatcherReceiver.TouchSystemKeyListener() {
            @Override
            public void onTouchHome() {
                if (mIFloatAppLockerViewEvtListener != null) {
                    mIFloatAppLockerViewEvtListener.onHomeClick();
                }
            }
        });
        if (!mHomeKeyEventReceiver.isRegistered()) {
            mHomeKeyEventReceiver.register(getContext(), new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
        if (mHomeKeyEventReceiver.isRegistered()) {
            mHomeKeyEventReceiver.unregister(getContext());
        }
    }

    public void setIFloatAppLockerViewEvtListener(IFloatAppLockerViewEvtListener iFloatAppLockerViewEvtListener) {
        this.mIFloatAppLockerViewEvtListener = iFloatAppLockerViewEvtListener;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_UP && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (mIFloatAppLockerViewEvtListener != null) {
                mIFloatAppLockerViewEvtListener.onBackPress();
            }
            return false;
        }
        return super.dispatchKeyEvent(event);
    }

    /**
     * 重置图案
     */
    public void resetPatternView() {
        if (mPatternView != null) {
            mPatternView.clearPattern();
            mPatternView.setDisplayMode(PatternView.DisplayMode.Correct);
        }
    }

    private final long DELAY_CLEAR_ERROR_TIME_LONG = 1000;

    private Runnable mDelayClearErrorWork = new Runnable() {
        @Override
        public void run() {
            if (mPatternView != null) {
                mPatternView.clearPattern();
            }
        }
    };

    /**
     * 延时清除掉错误图案
     */
    public void delayClearErrorPattern() {
        if (mPatternView != null) {
            mPatternView.setDisplayMode(PatternView.DisplayMode.Wrong);
        }
        if (mHandler != null) {
            mHandler.postDelayed(mDelayClearErrorWork, DELAY_CLEAR_ERROR_TIME_LONG);
        }
    }

    private void removeClearErrorWork() {
        if (mHandler != null) {
            mHandler.removeCallbacks(mDelayClearErrorWork);
        }
    }

}
