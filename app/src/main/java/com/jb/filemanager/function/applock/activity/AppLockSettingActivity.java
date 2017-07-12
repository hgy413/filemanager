package com.jb.filemanager.function.applock.activity;

import android.os.Bundle;
import android.provider.SyncStateContract;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.jb.filemanager.BaseActivity;
import com.jb.filemanager.Const;
import com.jb.filemanager.R;
import com.jb.filemanager.function.applock.dialog.ListDialog;
import com.jb.filemanager.function.applock.manager.LockerFloatLayerManager;
import com.jb.filemanager.function.applock.model.AppLockerDataManager;

/**
 * Created by nieyh on 2017/1/4. <br/>
 */

public class AppLockSettingActivity extends BaseActivity {

    //返回按钮
    private ImageView mBack;
    //标题
    private TextView mTitle;
    private View mArrow;
    private View mLockOptionsView;
    private TextView mLockOptionsTextView;
    private View mLockOptionsBottomLine;
    //选项列表对话框
    private ListDialog mOptionsListDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_applock_setting);
        initView();
        initListener();
    }

    /**
     * 初始化界面
     */
    private void initView() {
        mBack = (ImageView) findViewById(R.id.common_applock_bar_layout_back);
        mTitle = (TextView) findViewById(R.id.common_applock_bar_layout_title);
        mTitle.setText(R.string.app_lock_setting_title);
        mArrow = findViewById(R.id.fragment_app_lock_lock_options_arrow);
        mLockOptionsView = findViewById(R.id.fragment_app_lock_lock_options_layout);
        mLockOptionsTextView = (TextView) findViewById(R.id.fragment_app_lock_lock_options_show);
        mLockOptionsBottomLine = findViewById(R.id.fragment_app_lock_set_psd_bottom_line3);
        mOptionsListDialog = new ListDialog(mLockOptionsTextView, R.array.pupup_window_lock_options);
    }

    /**
     * 设置监听器
     */
    private void initListener() {
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mQuickClickGuard.isQuickClick(v.getId())) {
                    onBackPressed();
                }
            }
        });

        mTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (!mQuickClickGuard.isQuickClick(v.getId())) {
//                    onBackPressed();
//                }
                LockerFloatLayerManager.getInstance().showFloatViewInSide();
            }
        });

        mLockOptionsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mArrow.setRotation(180);
                mOptionsListDialog.showUnderView(mLockOptionsBottomLine);
            }
        });

        mOptionsListDialog.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                mArrow.setRotation(0);
                AppLockerDataManager.getInstance().setLockForLeave(mOptionsListDialog.getCurrentPos() == 0);
            }
        });
    }

    @Override
    protected void onDestroy() {
        mOptionsListDialog.setOnDismissListener(null);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
