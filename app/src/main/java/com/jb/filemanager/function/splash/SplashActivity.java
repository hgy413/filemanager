package com.jb.filemanager.function.splash;

import android.os.Bundle;
import android.os.Handler;

import com.jb.filemanager.BaseActivity;
import com.jb.filemanager.R;

/**
 * Created by xiaoyu on 2016/10/12.
 *
 */

public class SplashActivity extends BaseActivity {

    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mHandler == null) {
            mHandler = new Handler();
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            }, 2500);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
        mHandler = null;
    }
}