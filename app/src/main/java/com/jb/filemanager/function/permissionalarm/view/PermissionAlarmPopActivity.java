package com.jb.filemanager.function.permissionalarm.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.jb.filemanager.BaseActivity;
import com.jb.filemanager.R;
import com.jb.filemanager.TheApplication;
import com.jb.filemanager.eventbus.IOnEventMainThreadSubscriber;
import com.jb.filemanager.function.permissionalarm.event.PermissionViewDismissEvent;
import com.jb.filemanager.manager.spm.IPreferencesIds;
import com.jb.filemanager.manager.spm.SharedPreferencesManager;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by nieyh on 2017/2/8.
 * 权限警报弹框Activity
 */

public class PermissionAlarmPopActivity extends BaseActivity {

    private static final String ARG_PKG_NAME = "1";
    private static final String ARG_PERMISSIONS = "2";
    private static final String ARG_IS_NEEDSHOWAD = "3";
    private static final String ARG_DLG_TYPE = "4";
    public static final int DLG_COMMON = 1;
    public static final int DLG_MERGE = 2;
    //标示当前Activity存活
    public static boolean isLive = false;
    //是否已经请求过广告
    public static boolean isAlreadyRequestAd = false;

    /**
     * DLG_COMMON : 普通权限警报对话框 <br/>
     * DLG_MERGE : 合并权限警报对话框
     */
    @IntDef({DLG_COMMON, DLG_MERGE})
    @Retention(value = RetentionPolicy.SOURCE)
    public @interface PermissionAlarmDlgType {
    }

    /**
     * 展示对话框
     *
     * @param pkgName      包名
     * @param permissions  权限列表
     * @param type         对话框类型
     * @param isNeedShowAd 是否需要展示广告
     * @see PermissionAlarmDlgType
     */
    public static void show(String pkgName, List<String> permissions, boolean isNeedShowAd, @PermissionAlarmDlgType int type) {
        Intent intent = new Intent(TheApplication.getAppContext(), PermissionAlarmPopActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(ARG_PKG_NAME, pkgName);
        if (permissions instanceof ArrayList) {
            intent.putStringArrayListExtra(ARG_PERMISSIONS, (ArrayList<String>) permissions);
        }
        intent.putExtra(ARG_IS_NEEDSHOWAD, isNeedShowAd);
        intent.putExtra(ARG_DLG_TYPE, type);
        TheApplication.getAppContext().startActivity(intent);
    }

    /**
     * 查看顶部视图的root
     */
    private FrameLayout mRoot;

    private String mPkgName;

    private List<String> mPermissionsList;

    private boolean isShowAd;

    private int mCurShowTimes;

    private BasePermissionView mPermissionMergeAlertDialog;

    /**
     * 权限页 隐藏事件
     */
    private IOnEventMainThreadSubscriber<PermissionViewDismissEvent> mPermissionViewDismissEvtSubscriber = new IOnEventMainThreadSubscriber<PermissionViewDismissEvent>() {
        @Override
        @Subscribe(threadMode = ThreadMode.MAIN)
        public void onEventMainThread(PermissionViewDismissEvent event) {
            //页面隐藏
            if (event.isSignificance) {
                //代表需要直接关闭页面
                finish();
            } else {
                if (mRoot != null && mRoot.getChildCount() > 0) {
                    View child = mRoot.getChildAt(mRoot.getChildCount() - 1);
                    if (child != null) {
                        //移除View
                        mRoot.removeView(child);
                    }

                    if (mRoot.getChildCount() == 0) {
                        PermissionAlarmPopActivity.super.onBackPressed();
                    }
                } else {
                    PermissionAlarmPopActivity.super.onBackPressed();
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRoot = (FrameLayout) findViewById(android.R.id.content);
        //半透明
        mRoot.setBackgroundColor(0x7f000000);
        dealIntent(getIntent());
        TheApplication.getGlobalEventBus().register(mPermissionViewDismissEvtSubscriber);

        mRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mRoot.getChildCount() > 0) {
                    View child = mRoot.getChildAt(mRoot.getChildCount() - 1);
                    if (child != null) {
                        if (child instanceof BasePermissionView) {
                            //点击外部点击
                            ((BasePermissionView) child).onOutSideTouch();
                        }
                    }
                }
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        dealIntent(intent);
    }

    @Override
    protected void onPressedHomeKey() {
        super.onPressedHomeKey();
        //点击home键销毁当前Activity 防止多次安装出现同一个应用
        finish();
    }

    /**
     * 处理Intent, 获取传递的数据。
     */
    private void dealIntent(Intent intent) {
        isLive = true;
        if (intent != null && intent.getExtras() != null) {
            Bundle bundle = intent.getExtras();
            mPkgName = bundle.getString(ARG_PKG_NAME);
            mPermissionsList = bundle.getStringArrayList(ARG_PERMISSIONS);
            isShowAd = bundle.getBoolean(ARG_IS_NEEDSHOWAD);
            int dlgType = bundle.getInt(ARG_DLG_TYPE);
            switch (dlgType) {
                case DLG_COMMON:
                    showPermissionDialog(new PermissionAlertView(this));
                    break;
                case DLG_MERGE:
                    showPermissionMergeAlertDialog();
                    break;
            }
        }
    }

    /**
     * 展示警报对话框
     */
    private BasePermissionView showPermissionDialog(BasePermissionView basePermissionView) {
        if (basePermissionView == null) {
            return null;
        }
        BasePermissionView dialog = basePermissionView;
        if (mRoot != null) {
            if (!TextUtils.isEmpty(mPkgName)) {
                dialog.buildView(mPkgName, mPermissionsList);
                if (isShowAd) {
                    //加载广告
                    dialog.loadAd();
                }
                FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.gravity = Gravity.CENTER;
                dialog.setLayoutParams(layoutParams);
                mRoot.addView(dialog, 0);
            }
        }
        return dialog;
    }

    /**
     * 展示合并警报对话框
     */
    public void showPermissionMergeAlertDialog() {
        if (mPermissionMergeAlertDialog == null) {
            mPermissionMergeAlertDialog = showPermissionDialog(new PermissionMergeAlertView(this));
        } else if (!TextUtils.isEmpty(mPkgName)) {
            mPermissionMergeAlertDialog.buildView(mPkgName, mPermissionsList);
        }
    }

    @Override
    public void onBackPressed() {
        if (mRoot != null && mRoot.getChildCount() > 0) {
            View child = mRoot.getChildAt(mRoot.getChildCount() - 1);
            if (child != null) {
                if (child instanceof BasePermissionView) {
                    if (((BasePermissionView) child).onBackPress()) {
                        //移除View
                        mRoot.removeView(child);
                    }
                }
            }

            if (mRoot.getChildCount() == 0) {
                onBackPressed();
            }
        } else {
            super.onBackPressed();
        }
    }

    /**
     * 展示权限获取对话框
     */
    private void showAppPermissionsRequestDialog() {
        RequestPermissionView dialog = new RequestPermissionView(this);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER;
        dialog.setLayoutParams(layoutParams);
        mRoot.addView(dialog);
    }

    @Override
    protected void onDestroy() {
        if (mRoot != null) {
            mRoot.removeAllViews();
        }
        isLive = false;
        //刷新已经请求过广告标示 将可以再一次请求广告
        isAlreadyRequestAd = false;
        TheApplication.getGlobalEventBus().unregister(mPermissionViewDismissEvtSubscriber);
        super.onDestroy();
    }
}
