package com.jb.filemanager;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jb.filemanager.function.image.app.BaseFragmentActivity;
import com.jb.filemanager.function.image.app.BaseFragmentWithImmersiveStatusBar;
import com.jb.filemanager.util.QuickClickGuard;

/**
 * Created by bill wang on 2017/7/18.
 *
 */

public class BaseFragment extends android.support.v4.app.Fragment {
    protected Activity mActivity;

    protected QuickClickGuard mQuickClickGuard;
    //这个主要是为防止快速点击不同的控件
    protected QuickClickGuard mQuickClickGuardForTwoView;
    //显示的时间 (用于区分当前显示的Fragment 时间排序可以判断上一个显示的Fragment)
    private long mVisibleTime;
    //是否参与返回统计
    private boolean isTitleBack;

    private BaseFragmentActivity mBaseFragmentActivity;

    public boolean isTitleBack() {
        return isTitleBack;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof BaseFragmentActivity) {
            mBaseFragmentActivity = (BaseFragmentActivity) context;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mQuickClickGuard = new QuickClickGuard();
        mQuickClickGuardForTwoView = new QuickClickGuard(300);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        TextView textView = new TextView(getContext());
        textView.setText(getClass().getSimpleName());
        return textView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //刷新成当前时间
        mVisibleTime = System.currentTimeMillis();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mQuickClickGuard != null) {
            mQuickClickGuard = null;
        }
    }

    /**
     * 处理页面返回到首界面
     * */
    public void onHandlePop() {
        //刷新当前显示时间
        mVisibleTime = System.currentTimeMillis();
    }

    /**
     * home键
     */
    public void onPressedHomeKey() {

    }

    public boolean onBackPressed() {
        return true;
    }


    /**
     * 用于标题返回统计
     * */
    public void onTitleBackStatistic() {

    }

    /**
     * 用于系统返回统计
     * */
    public void onBackPressStatistic() {

    }

    /**
     * 返回上一个页面
     * {@link BaseFragmentActivity#onBackPressed()}
     */
    protected void onToBack() {
        //当点击按钮返回时 是否需要参与统计
        isTitleBack = true;
        //调用BaseFragmentActivity 的 onBackPressed
        getActivity().onBackPressed();
        //重新置位
        isTitleBack = false;
    }

    /**
     * 压入Fragment到新栈中
     * */
    public void addFragment(BaseFragment aimFragment) {
        if (mBaseFragmentActivity != null && aimFragment != null) {
            mBaseFragmentActivity.addFragment(aimFragment);
        }
    }

    public void replaceFragment(BaseFragment aimFragment) {
        if (mBaseFragmentActivity != null && aimFragment != null) {
            mBaseFragmentActivity.replaceFragment(aimFragment);
        }
    }

    /**
     * 获取当前Fragment显示的时间
     * */
    public long getVisibleTime() {
        return mVisibleTime;
    }
}
