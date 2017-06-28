package com.jb.filemanager.function.applock.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jb.filemanager.R;
import com.jb.filemanager.TheApplication;
import com.jb.filemanager.function.applock.activity.AppLockSettingActivity;
import com.jb.filemanager.function.applock.adapter.HoriGalleryAdapter;
import com.jb.filemanager.function.applock.model.bean.IntruderDisplaySubBean;
import com.jb.filemanager.function.applock.presenter.IntruderHoriGalleryContract;
import com.jb.filemanager.function.applock.presenter.IntruderHoriGalleryPersenter;
import com.jb.filemanager.function.applock.presenter.IntruderHoriGallerySupport;
import com.jb.filemanager.function.applock.view.HorizontalListView;
import com.jb.filemanager.ui.widget.ProgressWheel;
import com.jb.filemanager.util.DrawUtils;
import com.jb.filemanager.util.device.Machine;

import java.util.List;

/**
 * Created by nieyh on 2017/1/6.
 * 展示所有图片 背景色透明 并可以左右滑动
 */

public class IntruderHoriGalleryFragment extends Fragment implements IntruderHoriGalleryContract.View {

    //设置
    private View mSetting;
    //水平列表（此处使用列表为了同时看到两个图片 而ViewPager实现不了）
    private HorizontalListView mHoriGallery;
    //确定按钮
    private TextView mSure;

    private TextView mNotice;

    private ProgressWheel mLoading;

    private IntruderHoriGalleryContract.Persenter mPersenter;

    private HoriGalleryAdapter mHoriGalleryAdapter;
    //每一张图片展示的宽度
    private int mImgSizeInPix;
    //横向列表的左右边距
    private int mGalleryMargin;
    //下一张图片露出来的宽度
    private int mNextVisibleWidth;
    //最小滑动距离
    private int mMiniSlideDistance;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_intruder_hori_gallery, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        layoutView(view);
        ViewConfiguration viewConfiguration = ViewConfiguration.get(TheApplication.getAppContext());
        mMiniSlideDistance = viewConfiguration.getScaledTouchSlop();
        mPersenter = new IntruderHoriGalleryPersenter(this, new IntruderHoriGallerySupport());
        mPersenter.loadData();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPersenter != null) {
            mPersenter.dealViewDestory();
        }
    }

    /**
     * 加载布局
     */
    private View layoutView(View view) {
        View root = view.findViewById(R.id.fragment_intruder_hori_gallery_root);
        mHoriGallery = (HorizontalListView) view.findViewById(R.id.fragment_intruder_hori_gallery_horilist);
        mSetting = view.findViewById(R.id.fragment_intruder_hori_gallery_set_btn);
        mSure = (TextView) view.findViewById(R.id.fragment_intruder_hori_gallery_ok);
        mNotice = (TextView) view.findViewById(R.id.fragment_intruder_hori_gallery_indicator);
        mLoading = (ProgressWheel) view.findViewById(R.id.fragment_intruder_hori_gallery_progress_wheel);
        mImgSizeInPix = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 235, getResources().getDisplayMetrics());
        mGalleryMargin = (getResources().getDisplayMetrics().widthPixels - mImgSizeInPix) / 2;

        //fitSystemWindows 只能用于非嵌入式Activity 所以此处fragment使用fitSystemwindows将无效 只能代码手动设置paddingtop
        if (Machine.HAS_SDK_KITKAT) {
            float paddingTop = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 25, getResources().getDisplayMetrics());
            root.setPadding(0, (int) paddingTop, 0, 0);
        }

        mNextVisibleWidth = mGalleryMargin - (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 18, getResources().getDisplayMetrics());
        if (mNextVisibleWidth < 0) {
            mNextVisibleWidth = 0;
        }
        mHoriGallery.setPadding(mGalleryMargin, 0, mGalleryMargin, 0);
        initListener();
        return view;
    }

    /**
     * 初始化监听器
     */
    private void initListener() {
        mSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), AppLockSettingActivity.class);
                i.putExtra(AppLockSettingActivity.SETTING_ARG, AppLockSettingActivity.SETTING_INTRUDER);
                startActivity(i);
                getActivity().finish();
            }
        });

        mSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onFinish();
            }
        });

        mHoriGallery.setOnTouchListener(new View.OnTouchListener() {
            float mStartX;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mPersenter != null) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            mStartX = event.getX();
                            break;
                        case MotionEvent.ACTION_UP:
                            if (event.getX() - mStartX > mMiniSlideDistance) {
                                mPersenter.dealSlideToLastPhoto();
                                break;
                            }
                            if (event.getX() - mStartX < -mMiniSlideDistance) {
                                mPersenter.dealSlideToNextPhoto();
                                break;
                            }
                            // TODO: 2017/1/6 跳转详情页面
                            IntruderDitalPhotoFragment.startFragment(mPersenter.getPhotoPath(), R.id.activity_intruder_main_content, getActivity().getSupportFragmentManager());
                    }
                    return true;
                }
                return false;
            }
        });
    }

    /**
     * 根据子Item的位置算出列表需要偏移以让其居中显示的距离
     */
    private int positionToOffset(int position) {
        if (position == 0) {
            return 0;
        } else if (position == 1) {
            return mGalleryMargin + mImgSizeInPix - mNextVisibleWidth;
        } else {
            return (mGalleryMargin + mImgSizeInPix) + (mImgSizeInPix + DrawUtils.dip2px(18)) * (position - 1) - mNextVisibleWidth;
        }
    }

    @Override
    public void showDataLoading() {
        mLoading.setVisibility(View.VISIBLE);
        mHoriGallery.setVisibility(View.GONE);
        if (!mLoading.isSpinning()) {
            mLoading.spin();
        }
    }

    @Override
    public void showDataLoaded() {
        mLoading.setVisibility(View.GONE);
        mHoriGallery.setVisibility(View.VISIBLE);
        if (mLoading.isSpinning()) {
            mLoading.stopSpinning();
        }
    }

    @Override
    public void showPhotoData(List<IntruderDisplaySubBean> subBeans) {
        if (mHoriGalleryAdapter == null) {
            mHoriGalleryAdapter = new HoriGalleryAdapter(subBeans);
            mHoriGallery.setAdapter(mHoriGalleryAdapter);
        } else {
            mHoriGalleryAdapter.bindData(subBeans);
        }
    }

    @Override
    public void onFinish() {
        if (isAdded() && isResumed()) {
            getActivity().finish();
        }
    }

    @Override
    public void refreshNotice(String notice) {
        if (mNotice != null) {
            mNotice.setText(notice);
        }
    }

    @Override
    public void scrollToPosition(int currentPosition, int time) {
        if (mHoriGallery != null) {
            mHoriGallery.scrollTo(positionToOffset(currentPosition), time);
        }
    }
}
