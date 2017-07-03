package com.jb.filemanager.function.usbstatemanager;

import android.content.Intent;
import android.os.Bundle;

import com.jb.filemanager.BaseActivity;

/**
 * Created by nieyh on 17-6-30.
 */

public class UsbStateTipDialog extends BaseActivity {

    //是否弹窗一直展示出来了
    public static boolean isWindowIsTip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * 展示弹窗
     * */
    public static void show() {
        isWindowIsTip = true;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isWindowIsTip = false;
    }
}
