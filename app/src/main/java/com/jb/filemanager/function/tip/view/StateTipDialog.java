package com.jb.filemanager.function.tip.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.jb.filemanager.BaseActivity;
import com.jb.filemanager.TheApplication;
import com.jb.filemanager.eventbus.IOnEventMainThreadSubscriber;
import com.jb.filemanager.function.tip.event.LayerCloseEvent;
import com.jb.filemanager.function.tip.manager.StorageTipManager;
import com.jb.filemanager.function.tip.manager.UsbStateManager;
import com.jb.filemanager.util.Logger;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by nieyh on 17-7-17.
 */

public class StateTipDialog extends BaseActivity {
    private final String TAG = "StateTipDialog";
    //提示框的类型
    private static String STATE_TIP_TYPE_ARG = "state_tip_type_arg";
    //根布局
    private FrameLayout mRoot;
    //关闭事件监听
    private IOnEventMainThreadSubscriber<LayerCloseEvent> mLayerCloseEventSubscriber = new IOnEventMainThreadSubscriber<LayerCloseEvent>() {
        @Override
        @Subscribe(threadMode = ThreadMode.MAIN)
        public void onEventMainThread(LayerCloseEvent event) {
            int count = mRoot.getChildCount();
            boolean isWindowsClosed = false;
            if (count == 1) {
                if (!isFinishing()) {
                    isWindowsClosed = true;
                    finish();
                }
            } else {
                for (int i = 0; i < mRoot.getChildCount(); i++) {
                    TipLayer tipLayer = (TipLayer) mRoot.getChildAt(i);
                    if (tipLayer.getLayerType() == event.mLayerType) {
                        isWindowsClosed = true;
                        tipLayer.removeView2Root(mRoot);
                        break;
                    }
                }
            }
            if (isWindowsClosed && event.isEffect) {
                switch (event.mLayerType) {
                    case TipLayer.USB_STATE_TIP_LAYER:
                        UsbStateManager.getInstance().changerSwitch();
                        break;
                    case TipLayer.FREE_SPACE_TIP_LAYER:
                        StorageTipManager.getInstance().changerSwitch();
                        break;
                }
            }
        }
    };

    // 展示弹窗
    public static void show(Context context, @TipLayer.LAYER_TYPE int layerType) {
        if (context == null) {
            return;
        }
        Intent intent = new Intent(context, StateTipDialog.class);
        intent.putExtra(STATE_TIP_TYPE_ARG, layerType);
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRoot = (FrameLayout) findViewById(android.R.id.content);
        if (!TheApplication.getGlobalEventBus().isRegistered(mLayerCloseEventSubscriber)) {
            TheApplication.getGlobalEventBus().register(mLayerCloseEventSubscriber);
        }
        //点击空白位置 直接关闭最上层视图
        mRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        handleIntent();
        mRoot.setBackgroundColor(0x7f000000);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent();
    }

    //处理数据
    private void handleIntent() {
        Bundle bundle = getIntent().getExtras();
        int tipType = TipLayer.USB_STATE_TIP_LAYER;
        if (bundle != null) {
            tipType = bundle.getInt(STATE_TIP_TYPE_ARG, TipLayer.USB_STATE_TIP_LAYER);
        }

        TipLayer tipLayer = null;
        switch (tipType) {
            case TipLayer.USB_STATE_TIP_LAYER:
                tipLayer = new UsbStateTipLayer(this);
                break;
            case TipLayer.FREE_SPACE_TIP_LAYER:
                tipLayer = new FreeSpaceTipLayer(this);
                break;
        }
        if (tipLayer._isShow()) {
            Logger.w(TAG, "页面已经存在!!");
            if (mRoot.getChildCount() == 0) {
                finish();
            }
            return;
        }
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER;
        if (tipLayer != null) {
            Logger.w(TAG, "addView2Root >> " + tipLayer.getLayerType());
            tipLayer.addView2Root(mRoot, layoutParams);
        } else {
            if (mRoot.getChildCount() == 0) {
                finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        Logger.w(TAG, "onBackPressed");
        int count = mRoot.getChildCount();
        TipLayer tipLayer = (TipLayer) mRoot.getChildAt(count - 1);
        if (tipLayer.onTouchOutSide()) {
            //如果被消耗了事件 则退出
            return;
        }
        if (count == 1) {
            super.onBackPressed();
            return;
        }
        mRoot.removeViewAt(count - 1);
    }

    @Override
    protected void onDestroy() {
        if (TheApplication.getGlobalEventBus().isRegistered(mLayerCloseEventSubscriber)) {
            TheApplication.getGlobalEventBus().unregister(mLayerCloseEventSubscriber);
        }
        //移除所有的视图
        mRoot.removeAllViews();
        super.onDestroy();
    }
}
