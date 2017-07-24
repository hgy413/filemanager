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
import android.widget.TextView;

import com.jb.filemanager.BaseActivity;
import com.jb.filemanager.R;
import com.jb.filemanager.TheApplication;
import com.jb.filemanager.function.about.AboutActivity;
import com.jb.filemanager.function.applock.activity.AppLockPreActivity;
import com.jb.filemanager.function.applock.manager.LockerFloatLayerManager;
import com.jb.filemanager.function.feedback.FeedbackActivity;
import com.jb.filemanager.function.setting.SettingActivity;
import com.jb.filemanager.function.trash.CleanTrashActivity;
import com.jb.filemanager.manager.spm.IPreferencesIds;
import com.jb.filemanager.manager.spm.SharedPreferencesManager;
import com.jb.filemanager.statistics.StatisticsConstants;
import com.jb.filemanager.statistics.StatisticsTools;
import com.jb.filemanager.statistics.bean.Statistics101Bean;
import com.jb.filemanager.util.AppUtils;
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
    private static final String APPLOCK = "app_lock";
    private ActionBarDrawerToggle mDrawerToggle;
    private QuickClickGuard mQuickClickGuard;
    private DrawerLayout mDrawerLayout;
    private BaseActivity mActivity;
    public static final int CLI_OPEN = 1;
    private static final int FLING_OPEN = 2;
    private int mOpenType = FLING_OPEN;


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
        TextView appLockerItem = (TextView) mActivity.findViewById(R.id.tv_drawer_app_locker);
        if (appLockerItem != null) {
            appLockerItem.getPaint().setAntiAlias(true);
            appLockerItem.setOnClickListener(this);
        }

        TextView smartChargeItem = (TextView) mActivity.findViewById(R.id.tv_drawer_smart_charge);
        if (smartChargeItem != null) {
            smartChargeItem.getPaint().setAntiAlias(true);
            smartChargeItem.setOnClickListener(this);
        }

        TextView usbPluginItem = (TextView) mActivity.findViewById(R.id.tv_drawer_usb_plugin);
        if (usbPluginItem != null) {
            usbPluginItem.getPaint().setAntiAlias(true);
            usbPluginItem.setOnClickListener(this);
        }

        TextView lowSpaceItem = (TextView) mActivity.findViewById(R.id.tv_drawer_low_space);
        if (lowSpaceItem != null) {
            lowSpaceItem.getPaint().setAntiAlias(true);
            lowSpaceItem.setOnClickListener(this);
        }

        TextView loggerNotificationItem = (TextView) mActivity.findViewById(R.id.tv_drawer_logger_notification);
        if (loggerNotificationItem != null) {
            loggerNotificationItem.getPaint().setAntiAlias(true);
            loggerNotificationItem.setOnClickListener(this);
        }

        TextView ratingItem = (TextView) mActivity.findViewById(R.id.tv_drawer_rating);
        if (ratingItem != null) {
            ratingItem.getPaint().setAntiAlias(true);
            ratingItem.setOnClickListener(this);
        }

        TextView updateItem = (TextView) mActivity.findViewById(R.id.tv_drawer_update);
        if (updateItem != null) {
            updateItem.getPaint().setAntiAlias(true);
            updateItem.setOnClickListener(this);
        }

        TextView helpItem = (TextView) mActivity.findViewById(R.id.tv_drawer_help);
        if (helpItem != null) {
            helpItem.getPaint().setAntiAlias(true);
            helpItem.setOnClickListener(this);
        }

        TextView aboutItem = (TextView) mActivity.findViewById(R.id.tv_drawer_about);
        if (aboutItem != null) {
            aboutItem.getPaint().setAntiAlias(true);
            aboutItem.setOnClickListener(this);
        }
    }

    void openDrawerWithDelay(int delayMills) {
        if (mDrawerLayout == null) {
            mDrawerLayout = (DrawerLayout) mActivity.findViewById(R.id.left_drawer);
        }
        mDrawerLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                mDrawerLayout.openDrawer(GravityCompat.START);
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
            case R.id.tv_drawer_app_locker:
                jumpToApplock();
                break;
            case R.id.tv_drawer_smart_charge:
                // TODO @wangzq
                break;
            case R.id.tv_drawer_usb_plugin:{
                boolean usbPluginEnable = !v.isSelected();
                v.setSelected(usbPluginEnable);
                SharedPreferencesManager spm = SharedPreferencesManager.getInstance(TheApplication.getInstance());
                spm.commitBoolean(IPreferencesIds.KEY_USB_CONNECTED_TIP_ENABLE, usbPluginEnable);

                AppUtils.showToast(TheApplication.getInstance(), usbPluginEnable ? R.string.toast_usb_plugin_switch_enable : R.string.toast_usb_plugin_switch_disable);
            }
                break;
            case R.id.tv_drawer_low_space: {
                boolean lowSpaceEnable = !v.isSelected();
                v.setSelected(lowSpaceEnable);
                SharedPreferencesManager spm = SharedPreferencesManager.getInstance(TheApplication.getInstance());
                spm.commitBoolean(IPreferencesIds.KEY_LOW_SPACE_WARNING_ENABLE, lowSpaceEnable);

                AppUtils.showToast(TheApplication.getInstance(), lowSpaceEnable ? R.string.toast_low_space_switch_enable : R.string.toast_low_space_switch_disable);
            }
                break;
            case R.id.tv_drawer_logger_notification: {
                boolean loggerNotificationEnable = !v.isSelected();
                v.setSelected(loggerNotificationEnable);
                SharedPreferencesManager spm = SharedPreferencesManager.getInstance(TheApplication.getInstance());
                spm.commitBoolean(IPreferencesIds.KEY_LOGGER_NOTIFICATION_ENABLE, loggerNotificationEnable);

                AppUtils.showToast(TheApplication.getInstance(), loggerNotificationEnable ? R.string.toast_logger_notification_switch_enable : R.string.toast_logger_notification_switch_disable);
            }
                break;
            case R.id.tv_drawer_rating:
                // TODO @wangzq
                break;
            case R.id.tv_drawer_update:
                // TODO @wangzq
                break;
            case R.id.tv_drawer_help:
                jumpToFeedBack();
                break;
            case R.id.tv_drawer_about:
                jumpToAbout();
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

    private void jumpToApplock() {
        SharedPreferencesManager sharedPreferencesManager = SharedPreferencesManager.getInstance(TheApplication.getAppContext());
        boolean isEnable = sharedPreferencesManager.getBoolean(IPreferencesIds.KEY_APP_LOCK_ENABLE, false);
        if (!isEnable) {
            Intent i = new Intent(mActivity, AppLockPreActivity.class);
            mActivity.startActivity(i);
        } else {
            LockerFloatLayerManager.getInstance().showFloatViewInSide();
        }
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