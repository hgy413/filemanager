package com.jb.filemanager.home;

import android.content.Intent;
import android.content.res.Configuration;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.jb.filemanager.BaseActivity;
import com.jb.filemanager.R;
import com.jb.filemanager.TheApplication;
import com.jb.filemanager.function.about.AboutActivity;
import com.jb.filemanager.function.feedback.FeedbackActivity;
import com.jb.filemanager.function.setting.SettingActivity;
import com.jb.filemanager.function.trash.CleanTrashActivity;
import com.jb.filemanager.statistics.StatisticsConstants;
import com.jb.filemanager.statistics.StatisticsTools;
import com.jb.filemanager.statistics.bean.Statistics101Bean;
import com.jb.filemanager.util.Logger;
import com.jb.filemanager.util.QuickClickGuard;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bill wang on 16/8/19.
 *
 */
public class MainDrawer implements View.OnClickListener {

    public static final String FEED_BACK = "feed_back";
    private static final String ABOUT = "about";
    private static final String CLEAN_TRASH = "clean_trash";
    private ActionBarDrawerToggle mDrawerToggle;
    private QuickClickGuard mQuickClickGuard;
    private DrawerLayout mDrawerLayout;
    private BaseActivity mActivity;
    public static final int CLI_OPEN = 1;
    private static final int FLING_OPEN = 2;
    private int mOpenType = FLING_OPEN;

    private RelativeLayout mRlDrawerBottom;
    private MainDrawerAdapter mMainDrawerAdapter;
    private RecyclerView mRvForDrawerItem;

    public MainDrawer(BaseActivity activity) {
        mActivity = activity;
    }

    public void initDrawer() {
        mDrawerLayout = (DrawerLayout) mActivity.findViewById(R.id.dl_drawer);

        // enableOrDisable ActionBar app icon to behave as action to toggle nav drawer
        ActionBar actionBar = mActivity.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
                mActivity,                  /* host Activity */
                mDrawerLayout,          /* DrawerLayout object */
                null,
                0,                     /* "open drawer" description for accessibility */
                0                      /* "close drawer" description for accessibility */
        ) {
            @Override
            public void onDrawerClosed(View view) {
                mOpenType = FLING_OPEN;
                //add by nieyh 抽屉栏关闭 入口值都是1
                statisticsCloseDrawer(1);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                if (mOpenType == FLING_OPEN) {
                    statisticsStartDrawer(2);
                } else {
                    statisticsStartDrawer(1);
                }
            }

            @Override
            public void onDrawerStateChanged(int newState) {
                // drawer状态改变的回调
            }

            // an unknown crash: No drawer view found with gravity LEFT
            @Override
            public boolean onOptionsItemSelected(MenuItem item) {
                if (item != null && item.getItemId() == android.R.id.home) {
                    toggleMenu();
                    return true;
                }
                return super.onOptionsItemSelected(item);
            }
        };

        mDrawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleMenu();
            }
        });
        mDrawerToggle.setDrawerIndicatorEnabled(false);
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        initDrawerItems();

        mQuickClickGuard = new QuickClickGuard();
    }

    private void toggleMenu() {
        if (mDrawerLayout == null) {
            return;
        }
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            mDrawerLayout.openDrawer(GravityCompat.START);
        }
    }

    private void initDrawerItems() {
        mRvForDrawerItem = (RecyclerView) mActivity.findViewById(R.id.rv_for_drawer_item);
        mRlDrawerBottom = (RelativeLayout) mActivity.findViewById(R.id.rl_drawer_bottom);
        ImageView ivDrawerSetting = (ImageView) mActivity.findViewById(R.id.iv_drawer_setting);
        ivDrawerSetting.setOnClickListener(this);

        mRvForDrawerItem.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false));
        List<DrawerItemBean> drawerList = getDrawerList();
        mMainDrawerAdapter = new MainDrawerAdapter(drawerList, mActivity);
        mRvForDrawerItem.setAdapter(mMainDrawerAdapter);
        mMainDrawerAdapter.setOnItemClickListener(new MainDrawerAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(String tag, int position) {
                switch (tag) {
                    case FEED_BACK:
                        jumpToFeedBack();
                        break;
                    case ABOUT:
                        jumpToAbout();
                        break;
                    case CLEAN_TRASH:
                        jumpToCleanCrash();
                        break;
                    default:
                        break;
                }
            }
        });
    }

    private List<DrawerItemBean> getDrawerList() {
        List<DrawerItemBean> itemBeanArrayList = new ArrayList<>();

        DrawerItemBean feedback = new DrawerItemBean(mActivity.getString(R.string.drawer_feedback), R.drawable.ic_drawer_feedback, FEED_BACK);
        DrawerItemBean about = new DrawerItemBean(mActivity.getString(R.string.drawer_about), R.drawable.ic_drawer_about, ABOUT);
        DrawerItemBean trash = new DrawerItemBean(mActivity.getString(R.string.trash), R.drawable.ic_drawer_about, CLEAN_TRASH);

        itemBeanArrayList.add(feedback);
        itemBeanArrayList.add(about);
        itemBeanArrayList.add(trash);
        return itemBeanArrayList;
    }

    void openDrawerWithDelay(int delayMills) {
        if (mDrawerLayout == null) {
            mDrawerLayout = (DrawerLayout) mActivity.findViewById(R.id.left_drawer);
        }
        mDrawerLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                mDrawerLayout.openDrawer(GravityCompat.START);
                mRvForDrawerItem.smoothScrollToPosition(0);
            }
        }, delayMills);
    }

    private void closeDrawerWithDelay(int delayMills) {
        if (mDrawerLayout == null) {
            mDrawerLayout = (DrawerLayout) mActivity.findViewById(R.id.left_drawer);
        }
        mDrawerLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                mDrawerLayout.closeDrawers();
                mRvForDrawerItem.smoothScrollToPosition(0);
            }
        }, delayMills);
    }

    public boolean isDrawerOpened() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(GravityCompat.START);
    }

    public void onPostCreate() {
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    public void onConfigurationChanged(Configuration newConfig) {
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    public void setDrawerEnable(boolean enable) {
        if (enable) {
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        } else {
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        }
    }

    private void statisticsStartDrawer(int entrance) {
        Statistics101Bean bean = Statistics101Bean.builder();
        bean.mOperateId = StatisticsConstants.HOME_SIDE_SHOW;
        bean.mEntrance = String.valueOf(entrance);
        StatisticsTools.upload101InfoNew(bean);
        Logger.d("MainActivity:statistics", "[]:" + "点击开启侧边栏 OperateId" + bean.mOperateId + " entrance" + bean.mEntrance);
    }

    private void statisticsCloseDrawer(int entrance) {
        Statistics101Bean bean = Statistics101Bean.builder();
        bean.mOperateId = StatisticsConstants.HOME_SIDE_CLOSE;
        bean.mEntrance = String.valueOf(entrance);
        StatisticsTools.upload101InfoNew(bean);
        Logger.d("MainActivity:statistics", "[]:" + "关闭侧边栏 OperateId" + bean.mOperateId + " entrance" + bean.mEntrance);
    }

    void setOpenType(int openType) {
        this.mOpenType = openType;
    }

    @Override
    public void onClick(View v) {
        if (mQuickClickGuard.isQuickClick(v.getId())) {
            return;
        }
        switch (v.getId()){
            case R.id.iv_drawer_setting:
                jumpToSetting();
                break;
            default:
                break;
        }
    }

    private void jumpToCleanCrash() {
        closeDrawerWithDelay(0);
        delayStartActivity(new Intent(mActivity, CleanTrashActivity.class));
    }

    private void jumpToAbout() {
        closeDrawerWithDelay(0);
        delayStartActivity(new Intent(mActivity, AboutActivity.class));
    }

    private void jumpToFeedBack() {
        closeDrawerWithDelay(0);
        delayStartActivity(new Intent(mActivity, FeedbackActivity.class));
    }

    private void jumpToSetting() {
        closeDrawerWithDelay(0);
        delayStartActivity(new Intent(mActivity, SettingActivity.class));
    }

    /**
     * 延时跳转
     * */
    private void delayStartActivity(final Intent intent) {
        TheApplication.postRunOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (intent != null && mActivity != null) {
                    mActivity.startActivity(intent);
                }
            }
        }, 260);
    }


}