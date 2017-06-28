package com.jb.filemanager.function.applock.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jb.filemanager.BaseActivity;
import com.jb.filemanager.R;
import com.jb.filemanager.TheApplication;
import com.jb.filemanager.function.applock.presenter.InruderOpenGuidePresenter;
import com.jb.filemanager.function.applock.presenter.IntruderOpenGuideContract;
import com.jb.filemanager.function.applock.presenter.IntruderOpenGuideSupport;
import com.jb.filemanager.function.applock.view.CameraPermissionCheckView;
import com.jb.filemanager.util.APIUtil;

/**
 * Created by nieyh on 2017/1/3.
 */

public class IntruderOpenGuideActivity extends BaseActivity implements IntruderOpenGuideContract.View {

    private TextView mSure;

    private View mDialog;

    private View mCoverBg;

    private TextView mIntruderTip;

    private CameraPermissionCheckView mCameraPermissionCheckView;

    private IntruderOpenGuideContract.Presenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intruder_open_guide);
        initView();
        initListener();
    }

    private void initView() {
        mSure = (TextView) findViewById(R.id.activity_intruder_open_guide_btn);
        mDialog = findViewById(R.id.activity_intruder_open_guide_dialog);
        mCoverBg = findViewById(R.id.activity_intruder_open_guide_cover_bg);
        mIntruderTip = (TextView) findViewById(R.id.activity_intruder_open_guide_intruder_tip);
        mCameraPermissionCheckView = new CameraPermissionCheckView(this);
        mPresenter = new InruderOpenGuidePresenter(this, new IntruderOpenGuideSupport());
        initGradientBg();
        mPresenter.start();
    }

    /**
     * 初始化监听器
     */
    private void initListener() {
        mSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //打开功能
                mPresenter.checkPremissionState();
            }
        });

        mCoverBg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //关闭
                finish();
            }
        });

        mDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //什么都不做
            }
        });
    }

    /**
     * 初始化渐变色背景
     */
    private void initGradientBg() {
        float radius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics());
//        float dialogRadius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics());
        View mTopImgBg = findViewById(R.id.activity_intruder_open_guide_dialog_top_bg);
        GradientDrawable gradientDrawableLR = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[]{0xffff981d, 0xffffd93a});
        gradientDrawableLR.setGradientType(GradientDrawable.LINEAR_GRADIENT);
        gradientDrawableLR.setShape(GradientDrawable.RECTANGLE);
        gradientDrawableLR.setCornerRadii(new float[]{radius, radius, radius, radius, 0, 0, 0, 0});
        APIUtil.setBackground(mTopImgBg, gradientDrawableLR);
        ImageView headImg = (ImageView) findViewById(R.id.activity_intruder_open_guide_dialog_head);
        ImageView bodyImg = (ImageView) findViewById(R.id.activity_intruder_open_guide_dialog_body);
        headImg.setColorFilter(0xff000000, PorterDuff.Mode.SRC_ATOP);
        bodyImg.setColorFilter(0xff000000, PorterDuff.Mode.SRC_ATOP);

        GradientDrawable buttonDrawable = new GradientDrawable();
        buttonDrawable.setColor(0xffff8314);
        buttonDrawable.setCornerRadii(new float[]{radius, radius, radius, radius, radius, radius, radius, radius});
        APIUtil.setBackground(mSure, buttonDrawable);
    }

    /**
     * 弹出对话框
     */
    public static void pop() {
        Context context = TheApplication.getAppContext();
        Intent intent = new Intent(context, IntruderOpenGuideActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    public void showPremisstionGetFail() {
        Toast.makeText(TheApplication.getAppContext(), R.string.intruder_shot_info_open_fail_tip, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showIntruderTipTimes(int times) {
        if (mIntruderTip != null) {
            mIntruderTip.setText(getResources().getString(R.string.intruder_shot_info_desc, times));
        }
    }

    @Override
    public boolean openCamera() {
        if (mCameraPermissionCheckView != null) {
            return mCameraPermissionCheckView.show();
        }
        return false;
    }

    @Override
    public void releaseCamera() {
        if (mCameraPermissionCheckView != null) {
            mCameraPermissionCheckView.close();
        }
    }

    @Override
    protected void onDestroy() {
        releaseCamera();
        super.onDestroy();
    }

    @Override
    public void close() {
        finish();
    }
}
