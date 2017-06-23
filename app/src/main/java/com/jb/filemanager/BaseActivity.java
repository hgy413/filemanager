package com.jb.filemanager;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jb.filemanager.receiver.HomeWatcherReceiver;
import com.jb.filemanager.util.QuickClickGuard;

/**
 * Activity基类，所有Activity继承该基类实现，方便以后对Activity的方法修改.<br>
 */
public class BaseActivity extends AppCompatActivity {

    protected boolean mDestroyed;
    protected Activity mActivity;

    protected QuickClickGuard mQuickClickGuard;
    protected BroadcastReceiver mBaseReceiver;
    private HomeWatcherReceiver mHomeKeyEventReceiver;

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
    }

    @Override
    public void setContentView(int layoutResID) {
        View v = LayoutInflater.from(this).inflate(layoutResID, null);
        setContentView(v);
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        super.setContentView(view, params);
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = this;
        mQuickClickGuard = new QuickClickGuard();
        initBaseBroadcastReceiver();

        mHomeKeyEventReceiver = new HomeWatcherReceiver();
        mHomeKeyEventReceiver.setTouchSystemKeyListener(new HomeWatcherReceiver.TouchSystemKeyListener() {
            @Override
            public void onTouchHome() {
                onPressedHomeKey();
            }
        });

        if (!mHomeKeyEventReceiver.isRegistered()) {
            mHomeKeyEventReceiver.register(this.getApplicationContext(), new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
        }
    }

    //home键被点击的时候会触发  由子类去实现内容
    protected void onPressedHomeKey() {}

    //初始化广播
    private void initBaseBroadcastReceiver() {
        mBaseReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                finish();
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_LOCALE_CHANGED);
        registerReceiver(mBaseReceiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        try {
            super.onBackPressed();
        } catch (Exception e) {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        if (mBaseReceiver != null) {
            unregisterReceiver(mBaseReceiver);
        }
        mDestroyed = true;
        if (mQuickClickGuard != null) {
            mQuickClickGuard = null;
        }

        if (mHomeKeyEventReceiver != null && mHomeKeyEventReceiver.isRegistered()) {
            mHomeKeyEventReceiver.unregister(this.getApplicationContext());
            mHomeKeyEventReceiver = null;
        }
        super.onDestroy();
    }

    @Override
    public boolean isDestroyed() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return super.isDestroyed();
        } else {
            return mDestroyed;
        }
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        //强制改变activity的切换动画
        overridePendingTransition(R.anim.in, R.anim.out);
    }
}