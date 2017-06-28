package com.jb.filemanager.function.applock.fragment;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jb.filemanager.R;
import com.jb.filemanager.function.applock.activity.AppLockSettingActivity;
import com.jb.filemanager.function.applock.adapter.FloatBarGalleryAdapter;
import com.jb.filemanager.function.applock.model.bean.IntruderDisplayBean;
import com.jb.filemanager.function.applock.presenter.IntruderGalleryContract;
import com.jb.filemanager.function.applock.presenter.IntruderGalleryPresenter;
import com.jb.filemanager.ui.widget.FloatingGroupExpandableListView;
import com.jb.filemanager.ui.widget.ProgressWheel;
import com.jb.filemanager.ui.widget.WrapperExpandableListAdapter;
import com.jb.filemanager.util.APIUtil;
import com.jb.filemanager.util.QuickClickGuard;
import com.jb.filemanager.util.device.Machine;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nieyh on 2017/1/7.
 */

public class IntruderVertGalleryFragment extends Fragment implements IntruderGalleryContract.View {

    private FloatingGroupExpandableListView mGalleryList;

    private QuickClickGuard mQuickClickGuard;

    private FloatBarGalleryAdapter mFloatBarGalleryAdapter;

    private View mBack;

    private View mTitle;

    private View mSetting;

    private View mNoDataView;

    private IntruderGalleryContract.Presenter mPresenter;

    private ProgressWheel mLoading;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_intruder_vert_gallery, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mQuickClickGuard = new QuickClickGuard();
        initView(view);
        initListener();
    }

    /**
     * 初始化渐变
     */
    private void initGradient(View parent) {
        View root = parent.findViewById(R.id.fragment_intruder_vert_gallery_root_view);
        int startColor = 0xff3bd6f2;
        int endColor = 0xff0084ff;
        GradientDrawable gradientDrawableLR = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[]{startColor, endColor});
        gradientDrawableLR.setGradientType(GradientDrawable.LINEAR_GRADIENT);
        gradientDrawableLR.setShape(GradientDrawable.RECTANGLE);
        APIUtil.setBackground(root, gradientDrawableLR);

        //fitSystemWindows 只能用于非嵌入式Activity 所以此处fragment使用fitSystemwindows将无效 只能代码手动设置paddingtop
        if (Machine.HAS_SDK_KITKAT) {
            float paddingTop = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 25, getResources().getDisplayMetrics());
            root.setPadding(0, (int) paddingTop, 0, 0);
        }

        Drawable tempGradientDrawable = parent.findViewById(R.id.layout_intruder_gallery_no_data_num).getBackground();
        if (tempGradientDrawable instanceof GradientDrawable) {
            float width = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0.5f, getResources().getDisplayMetrics());
            ((GradientDrawable)tempGradientDrawable).setStroke(0xfface6ff, (int) width);
        }
    }

    /**
     * 初始化视图
     */
    private void initView(View parent) {
        mGalleryList = (FloatingGroupExpandableListView) parent.findViewById(R.id.fragment_intruder_vert_gallery_list);
        mBack = parent.findViewById(R.id.fragment_intruder_vert_gallery_icon);
        mTitle = parent.findViewById(R.id.fragment_intruder_vert_gallery_word);
        mSetting = parent.findViewById(R.id.fragment_intruder_vert_gallery_setting);
        mNoDataView = parent.findViewById(R.id.fragment_intruder_vert_gallery_no_data);

        mLoading = (ProgressWheel) parent.findViewById(R.id.fragment_intruder_vert_gallery_progress_wheel);
        initGradient(parent);
        mPresenter = new IntruderGalleryPresenter(this);
        mPresenter.start();
    }

    /**
     * 初始化监听器
     */
    private void initListener() {
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mQuickClickGuard.isQuickClick(v.getId())) {
                    onFinish();
                }
            }
        });

        mTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mQuickClickGuard.isQuickClick(v.getId())) {
                    onFinish();
                }
            }
        });
        mSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mQuickClickGuard.isQuickClick(v.getId())) {
                    Intent i = new Intent(getActivity(), AppLockSettingActivity.class);
                    i.putExtra(AppLockSettingActivity.SETTING_ARG, AppLockSettingActivity.SETTING_INTRUDER);
                    startActivity(i);
                }
            }
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mPresenter != null) {
            mPresenter.release();
        }
    }

    private void onFinish() {
        if (isAdded() && isResumed() && !getActivity().getSupportFragmentManager().popBackStackImmediate()) {
            getActivity().finish();
        }
    }

    @Override
    public void showDataLoading() {
        mGalleryList.setVisibility(View.GONE);
        mLoading.setVisibility(View.VISIBLE);
        if (!mLoading.isSpinning()) {
            mLoading.spin();
        }
    }

    @Override
    public void showDataLoaded() {
        mGalleryList.setVisibility(View.VISIBLE);
        mLoading.setVisibility(View.GONE);
        if (mLoading.isSpinning()) {
            mLoading.stopSpinning();
        }
    }

    @Override
    public void showGalleryData(List<IntruderDisplayBean> intruderDisplayBeanList) {
        if (mGalleryList != null) {
            mNoDataView.setVisibility(View.GONE);
            mGalleryList.setVisibility(View.VISIBLE);
            if (intruderDisplayBeanList != null) {
                if (mFloatBarGalleryAdapter == null) {
                    mFloatBarGalleryAdapter = new FloatBarGalleryAdapter(intruderDisplayBeanList);
                    mFloatBarGalleryAdapter.setOnPhotoClickListener(new FloatBarGalleryAdapter.onPhotoClickListener() {
                        @Override
                        public void onPhotoClick(ArrayList<String> pathList, int index) {
                            IntruderDitalPhotoFragment.startFragment(pathList, index, R.id.activity_intruder_main_content, getActivity().getSupportFragmentManager());
                        }
                    });
                    mGalleryList.setAdapter(new WrapperExpandableListAdapter(mFloatBarGalleryAdapter));
                    mGalleryList.setGroupIndicator(null);
                } else {
                    mFloatBarGalleryAdapter.bindData(intruderDisplayBeanList);
                }
            }
        }
    }

    @Override
    public void showNoDataLayout() {
        if (mNoDataView != null) {
            mNoDataView.setVisibility(View.VISIBLE);
            mGalleryList.setVisibility(View.GONE);
        }
    }

}
