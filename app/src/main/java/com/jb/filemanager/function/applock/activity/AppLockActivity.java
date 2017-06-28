package com.jb.filemanager.function.applock.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.jb.filemanager.R;
import com.jb.filemanager.TheApplication;
import com.jb.filemanager.eventbus.IOnEventMainThreadSubscriber;
import com.jb.filemanager.function.applock.adapter.AppLockAdapter;
import com.jb.filemanager.function.applock.event.IntruderSwitcherStateEvent;
import com.jb.filemanager.function.applock.model.bean.AppLockGroupData;
import com.jb.filemanager.function.applock.presenter.AppLockContract;
import com.jb.filemanager.function.applock.presenter.AppLockPresenter;
import com.jb.filemanager.function.applock.presenter.AppLockSupport;
import com.jb.filemanager.function.applock.view.SearchBarLayout;
import com.jb.filemanager.ui.widget.FloatingGroupExpandableListView;
import com.jb.filemanager.ui.widget.WrapperExpandableListAdapter;
import com.jb.filemanager.util.APIUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;


public class AppLockActivity extends BaseProgressActivity implements AppLockContract.View {

    private View mBack;

    private View mTitle;

    private View mIntruderEntanceLayout;

    private TextView mIntruderNumTxt;

    private TextView mCheckTxt;

    private FloatingGroupExpandableListView mLockerList;

//    private HalfCircleButton mOperate;

    private View mRootGradientBg;

    private ImageView mEntranceArrow;

    //搜索框
    private SearchBarLayout mSearchBarLayout;

    private View mSetting;
    //适配器
    private AppLockAdapter mAppLockAdapter;

    private AppLockContract.Presenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_applock);
        initView();
        initListener();
    }

    /**
     * 初始化颜色值
     */
    private void initView() {
        mBack = findViewById(R.id.activity_applock_title_icon);
        mTitle = findViewById(R.id.activity_applock_title_word);
        mRootGradientBg = findViewById(R.id.activity_applock_root_bg);
        mIntruderEntanceLayout = findViewById(R.id.activity_applock_header);
        mIntruderNumTxt = (TextView) findViewById(R.id.activity_applock_header_count_view);
        mSetting = findViewById(R.id.activity_applock_title_setting);
        mLockerList = (FloatingGroupExpandableListView) findViewById(R.id.activity_applock_list);
        mSearchBarLayout = (SearchBarLayout) findViewById(R.id.activity_applock_searchbar);
        mCheckTxt = (TextView) findViewById(R.id.activity_applock_header_check);
        initGradient();
        mPresenter = new AppLockPresenter(this, new AppLockSupport());
        if (!TheApplication.getGlobalEventBus().isRegistered(mIOnEventMainThreadSubscriber)) {
            TheApplication.getGlobalEventBus().register(mIOnEventMainThreadSubscriber);
        }
        mPresenter.loadData();
    }

    /**
     * 初始化监听器
     */
    private void initListener() {
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dealback(false);
            }
        });
        mTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dealback(false);
            }
        });
        mIntruderEntanceLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转到 图片页面
                if (mPresenter != null) {
                    mPresenter.dealiIntruderEntranceOnclick();
                }
            }
        });

        mLockerList.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent,
                                        View v, int groupPosition, long id) {
                if (mAppLockAdapter != null) {
                    mAppLockAdapter.performGroupClick(groupPosition);
                    mAppLockAdapter.notifyDataSetChanged();
                }
                if (mPresenter != null) {
                    mPresenter.refreshOperateButState();
                }
                dealLockerInfoChg();
                return true;
            }
        });

        mLockerList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                if (mAppLockAdapter != null) {
                    mAppLockAdapter.performItemClick(groupPosition, childPosition);
                    mAppLockAdapter.notifyDataSetChanged();
                }
                if (mPresenter != null) {
                    mPresenter.refreshOperateButState();
                }
                dealLockerInfoChg();
                return true;
            }
        });

        mSearchBarLayout.setOnSearchTxtChgLisenter(new SearchBarLayout.OnSearchEvtLisenter() {
            @Override
            public void searchTxtChange(Editable editable) {
                if (mPresenter != null) {
                    mPresenter.search(editable.toString().trim());
                }
            }

            @Override
            public void searchOnclick() {
            }
        });
        mSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击设置统计
                Intent intent = new Intent(AppLockActivity.this, AppLockSettingActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * 处理应用信息改变
     * */
    private void dealLockerInfoChg() {
        if (mPresenter != null) {
            mPresenter.dealUpdateLockerInfo();
        }
    }

    @Override
    public void onBackPressed() {
        dealback(true);
    }

    /**
     * 处理
     */
    private void dealback(boolean isSystemBack) {
        if (mSearchBarLayout != null && !mSearchBarLayout.safeToSlideClose()) {
            if (TheApplication.getGlobalEventBus().isRegistered(mIOnEventMainThreadSubscriber)) {
                TheApplication.getGlobalEventBus().unregister(mIOnEventMainThreadSubscriber);
            }
            if (isSystemBack) {
//                StatisticsTools.logBothEvent(StatisticsConst.APP_LOCK_LIST_ACT_BACK2);
            } else {
//                StatisticsTools.logBothEvent(StatisticsConst.APP_LOCK_LIST_ACT_BACK1);
            }
            finish();
            if (mSearchBarLayout != null) {
                mSearchBarLayout.release(this);
            }
            if (mPresenter != null) {
                mPresenter.release();
            }
        } else {
            if (mPresenter != null) {
                mPresenter.search(null);
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (TheApplication.getGlobalEventBus().isRegistered(mIOnEventMainThreadSubscriber)) {
            TheApplication.getGlobalEventBus().unregister(mIOnEventMainThreadSubscriber);
        }
        if (mSearchBarLayout != null) {
            mSearchBarLayout.release(this);
        }
        if (mPresenter != null) {
            mPresenter.release();
        }
        super.onDestroy();
    }

    /**
     * 初始化所有渐变色
     */
    private void initGradient() {
        int startColor = 0xff0084ff;
        int endColor = 0xff3bd6f2;
        GradientDrawable gradientDrawableLR = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[]{startColor, endColor});
        gradientDrawableLR.setGradientType(GradientDrawable.LINEAR_GRADIENT);
        gradientDrawableLR.setShape(GradientDrawable.RECTANGLE);
        APIUtil.setBackground(mRootGradientBg, gradientDrawableLR);

        ImageView entranceIcon = (ImageView) findViewById(R.id.activity_applock_header_icon);
        mEntranceArrow = (ImageView) findViewById(R.id.activity_applock_header_arrow);
        entranceIcon.setColorFilter(0xffff8314, PorterDuff.Mode.SRC_ATOP);
        mEntranceArrow.setColorFilter(0xffff8314, PorterDuff.Mode.SRC_ATOP);
    }

    public static final void gotoAppLock(Context context) {
        if (context == null) {
            return;
        }
        Intent intent = new Intent(context, AppLockActivity.class);
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }

    @Override
    public void showAppLockGroupData(List<AppLockGroupData> appLockGroupDataList) {
        if (appLockGroupDataList != null && appLockGroupDataList.get(0).getChildren() != null) {
            if (mAppLockAdapter == null) {
                mAppLockAdapter = new AppLockAdapter(appLockGroupDataList);
                final WrapperExpandableListAdapter wrapperAdapter = new WrapperExpandableListAdapter(
                        mAppLockAdapter);
                mLockerList.setGroupIndicator(null);
                mLockerList.setFloatingGroupEnabled(true);
                mLockerList.setAdapter(wrapperAdapter);
            } else {
                mAppLockAdapter.bindData(appLockGroupDataList);
            }
        }
    }


    @Override
    protected void onHomePressed() {
        //点击home键
//        StatisticsTools.logBothEvent(StatisticsConst.APP_LOCK_LIST_ACT_HOME);
    }

    @Override
    public void showDataLoading() {
//        if (mIntruderEntanceLayout != null) {
//            mIntruderEntanceLayout.setVisibility(View.GONE);
//        }
//        if (mLockerList != null) {
//            mLockerList.setVisibility(View.GONE);
//        }
        if (!isSpining()) {
            startSpin();
        }
    }

    @Override
    public void showDataLoaded() {
        if (mIntruderEntanceLayout != null) {
            mIntruderEntanceLayout.setVisibility(View.VISIBLE);
        }
        if (mLockerList != null) {
            mLockerList.setVisibility(View.VISIBLE);
        }
        if (isSpining()) {
            stopSpin();
        }
        //统计列表页展示
//        StatisticsTools.logBothEvent(StatisticsConst.APP_LOCK_LIST_ACT_SHOW);
    }

    @Override
    public void showIntruderTipDialog() {
        IntruderOpenGuideActivity.pop();
        //点击检测按钮统计
//        StatisticsTools.logBothEvent(StatisticsConst.APP_LOCK_LIST_ACT_CLI_CHECK);
    }

    @Override
    public void showIntruderTipOpened() {
        mCheckTxt.setVisibility(View.GONE);
        mEntranceArrow.setVisibility(View.VISIBLE);
    }

    @Override
    public void showIntruderTipClosed() {
        mCheckTxt.setVisibility(View.VISIBLE);
        mEntranceArrow.setVisibility(View.GONE);
    }

    private IOnEventMainThreadSubscriber<IntruderSwitcherStateEvent> mIOnEventMainThreadSubscriber = new IOnEventMainThreadSubscriber<IntruderSwitcherStateEvent>() {
        @Override
        @Subscribe (threadMode = ThreadMode.MAIN)
        public void onEventMainThread(IntruderSwitcherStateEvent event) {
            if (event.isOpen) {
                mCheckTxt.setVisibility(View.GONE);
                mEntranceArrow.setVisibility(View.VISIBLE);
            } else {
                mCheckTxt.setVisibility(View.VISIBLE);
                mEntranceArrow.setVisibility(View.GONE);
            }
        }
    };

    @Override
    public void showIntruderPhotoCounts(int counts) {
        if (mIntruderNumTxt != null) {
            if (counts > 0) {
                mIntruderNumTxt.setVisibility(View.VISIBLE);
                mIntruderNumTxt.setText(String.valueOf(counts));
            } else {
                mIntruderNumTxt.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void gotoIntruderVertGallery() {
        IntruderVertGalleryActivity.gotoIntruderVertGallery(this);
    }

}
