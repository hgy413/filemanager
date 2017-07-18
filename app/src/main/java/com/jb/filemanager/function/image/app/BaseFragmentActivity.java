package com.jb.filemanager.function.image.app;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.jb.filemanager.BaseFragment;
import com.jb.filemanager.receiver.HomeWatcherReceiver;
import com.jb.filemanager.util.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by nieyh on 2017/4/25.
 *
 */

public class BaseFragmentActivity extends FragmentActivity {

    private HomeWatcherReceiver mHomeKeyEventReceiver;

    /**
     * 使用时间来排序 Fragment （不同手机直接获取fragment的列表的顺序不一样, 此处使用时间来进行排序）
     */
    private Comparator<BaseFragment> mComparator = new Comparator<BaseFragment>() {
        @Override
        public int compare(BaseFragment o1, BaseFragment o2) {
            if (o2.getVisibleTime() > o1.getVisibleTime()) {
                return -1;
            } else if (o2.getVisibleTime() < o1.getVisibleTime()) {
                return 1;
            } else {
                return 0;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHomeKeyEventReceiver = new HomeWatcherReceiver();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mHomeKeyEventReceiver.setTouchSystemKeyListener(new HomeWatcherReceiver.TouchSystemKeyListener() {
            @Override
            public void onTouchHome() {
                BaseFragment nowFragment = getFragment(true);
                //执行当前视图的home点击
                nowFragment.onPressedHomeKey();
            }
        });
        if (!mHomeKeyEventReceiver.isRegistered()) {
            mHomeKeyEventReceiver.register(this, new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mHomeKeyEventReceiver.isRegistered()) {
            mHomeKeyEventReceiver.unregister(this);
        }
    }

    @Override
    public void onBackPressed() {
        BaseFragment nowFragment, popFragment;
        nowFragment = getFragment(true);
        popFragment = getFragment(false);
        if (nowFragment != null) {
            if (nowFragment.onBackPressed()) {
                if (nowFragment.isTitleBack()) {
                    //如果是标题的返回 则执行标题的统计
                    nowFragment.onTitleBackStatistic();
                } else {
                    //如果是系统返回 则执行系统返回的统计
                    nowFragment.onBackPressStatistic();
                }
                if (popFragment != null) {
                    //当已经返回的时候将调用上一界面的返回处理
                    popFragment.onHandlePop();
                }
                super.onBackPressed();
            } else {
                Logger.w("nieyh", "don back!!");
            }
        } else {
            super.onBackPressed();
        }
    }

    /**
     * 获取当前显示的Fragment
     */
    private BaseFragment getFragment(boolean isCurrent) {
        List<Fragment> fragmentList = getSupportFragmentManager().getFragments();
        List<BaseFragment> baseFragmentList = new ArrayList<>(fragmentList.size());
        for (Fragment fragment : fragmentList) {
            if (fragment instanceof BaseFragment) {
                baseFragmentList.add((BaseFragment) fragment);
            }
        }

        Collections.sort(baseFragmentList, mComparator);
        if (baseFragmentList.size() == 0) {
            return null;
        }
        if (isCurrent) {
            return baseFragmentList.get(baseFragmentList.size() - 1);
        } else {
            if (baseFragmentList.size() > 1) {
                return baseFragmentList.get(baseFragmentList.size() - 2);
            } else {
                return null;
            }
        }
    }

    public void setDefaultFragment(BaseFragment aimFragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(android.R.id.content, aimFragment, aimFragment.getClass().getSimpleName());
        //不要调用 addToBackStack
        transaction.commit();
    }

    /**
     * 将目标Fragment添加到指定视图中
     */
    public void addFragment(BaseFragment aimFragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(android.R.id.content, aimFragment, aimFragment.getClass().getSimpleName());
        transaction.addToBackStack(aimFragment.getClass().getName());
        transaction.commitAllowingStateLoss();
    }

    /**
     * 替换
     * */
    public void replaceFragment(BaseFragment aimFragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(android.R.id.content, aimFragment);
        transaction.commit();
    }
}
