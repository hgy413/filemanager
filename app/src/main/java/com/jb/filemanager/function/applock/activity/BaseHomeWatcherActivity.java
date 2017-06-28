package com.jb.filemanager.function.applock.activity;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.jb.filemanager.BaseActivity;
import com.jb.filemanager.receiver.HomeWatcherReceiver;

/**
 * Created by nieyh on 2017/1/17.
 */

public class BaseHomeWatcherActivity extends BaseActivity {

    private HomeWatcherReceiver mHomeKeyEvtReceiver;

    @Override
    protected void onResume() {
        super.onResume();
        mHomeKeyEvtReceiver = new HomeWatcherReceiver();
        mHomeKeyEvtReceiver.setTouchSystemKeyListener(new HomeWatcherReceiver.TouchSystemKeyListener() {
            @Override
            public void onTouchHome() {
                onHomePressed();
            }
        });
        if (!mHomeKeyEvtReceiver.isRegistered()) {
            mHomeKeyEvtReceiver.register(this, new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mHomeKeyEvtReceiver != null && mHomeKeyEvtReceiver.isRegistered()) {
            mHomeKeyEvtReceiver.unregister(this);
            mHomeKeyEvtReceiver = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mHomeKeyEvtReceiver != null && mHomeKeyEvtReceiver.isRegistered()) {
            mHomeKeyEvtReceiver.unregister(this);
            mHomeKeyEvtReceiver = null;
        }
    }

    /**
     * 点击home键
     * */
    protected void onHomePressed() {
    }

}
