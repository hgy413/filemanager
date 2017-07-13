package com.jb.filemanager.function.applock.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.jb.filemanager.R;
import com.jb.filemanager.TheApplication;
import com.jb.filemanager.function.applock.adapter.ApplockFloatAdapter;
import com.jb.filemanager.function.applock.model.bean.AppLockGroupData;
import com.jb.filemanager.function.applock.presenter.AppLockContract;
import com.jb.filemanager.function.applock.presenter.AppLockPresenter;
import com.jb.filemanager.function.applock.presenter.AppLockSupport;
import com.jb.filemanager.function.applock.view.SearchBarLayout;
import com.jb.filemanager.ui.widget.FloatingGroupExpandableListView;
import com.jb.filemanager.ui.widget.WrapperExpandableListAdapter;
import com.jb.filemanager.util.imageloader.IconLoader;

import java.util.List;


public class AppLockActivity extends BaseProgressActivity implements AppLockContract.View {

    private View mBack;

    private TextView mTitle;
    //应用锁提示
    private TextView mApplockTip2;

    private FloatingGroupExpandableListView mLockerList;

    //搜索框
    private SearchBarLayout mSearchBarLayout;

    private View mSetting;
    //适配器
    private ApplockFloatAdapter mApplockFloatAdapter;

    private AppLockContract.Presenter mPresenter;
    private ImageView mSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_applock);
        IconLoader.ensureInitSingleton(TheApplication.getAppContext());
        IconLoader.getInstance().bindServicer(this);
        initView();
        initListener();
    }

    /**
     * 初始化颜色值
     */
    private void initView() {
        mBack = findViewById(R.id.common_applock_bar_layout_back);
        mTitle = (TextView)findViewById(R.id.common_applock_bar_layout_title);
        mSetting = findViewById(R.id.common_applock_bar_layout_setting);
        mSearch = (ImageView) findViewById(R.id.common_applock_bar_layout_search);
        mApplockTip2 = (TextView) findViewById(R.id.activity_applock_tip2);
        mLockerList = (FloatingGroupExpandableListView) findViewById(R.id.activity_applock_float_view);
        mSearchBarLayout = (SearchBarLayout) findViewById(R.id.activity_applock_search_bar);
        mTitle.setText(R.string.activity_applock_title);
        mSearch.setVisibility(View.VISIBLE);
        mSetting.setVisibility(View.VISIBLE);
        mPresenter = new AppLockPresenter(this, new AppLockSupport());
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

        mLockerList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                if (mApplockFloatAdapter != null) {
                    mApplockFloatAdapter.performItemClick(groupPosition, childPosition);
                }
                dealLockerInfoChg();
                return true;
            }
        });

        mSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSearchBarLayout.safeToSlideOpen();
            }
        });

        mSearchBarLayout.setOnSearchActionLisenter(new SearchBarLayout.OnSearchEvtLisenter() {
            @Override
            public void searchTxtChange(Editable editable) {
                if (mPresenter != null) {
                    mPresenter.search(editable.toString().trim());
                }
            }

            @Override
            public void dismiss() {
                mTitle.setVisibility(View.VISIBLE);
                mSetting.setVisibility(View.VISIBLE);
                mBack.setVisibility(View.VISIBLE);
                mSearch.setVisibility(View.VISIBLE);
            }

            @Override
            public void onShow() {
                mTitle.setVisibility(View.INVISIBLE);
                mSetting.setVisibility(View.INVISIBLE);
                mBack.setVisibility(View.INVISIBLE);
                mSearch.setVisibility(View.INVISIBLE);
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
        if (mSearchBarLayout != null) {
            mSearchBarLayout.release(this);
        }
        if (mPresenter != null) {
            mPresenter.release();
        }
        IconLoader.getInstance().unbindServicer(this);
        super.onDestroy();
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
        if (appLockGroupDataList != null) {
            if (mApplockFloatAdapter == null) {
                mApplockFloatAdapter = new ApplockFloatAdapter(appLockGroupDataList);
                final WrapperExpandableListAdapter wrapperAdapter = new WrapperExpandableListAdapter(
                        mApplockFloatAdapter);
                mLockerList.setAdapter(wrapperAdapter);
            } else {
                mApplockFloatAdapter.bindData(appLockGroupDataList);
            }
            //更新就扩展列表
            for (int i = 0; i < appLockGroupDataList.size(); i++) {
                mLockerList.expandGroup(i);
            }
        }
    }

    @Override
    public void showLockAppsNum(int nums) {
        if (nums > 0) {
            findViewById(android.R.id.content).setBackgroundColor(0xFF44D6C3);
        } else {
            findViewById(android.R.id.content).setBackgroundColor(0xFFFF7D7D);
        }
        mApplockTip2.setText(getString(R.string.applock_header_text2_new, nums));
    }

    @Override
    protected void onHomePressed() {
    }

    @Override
    public void showDataLoading() {
        if (!isSpining()) {
            startSpin();
        }
    }

    @Override
    public void showDataLoaded() {
        if (mLockerList != null) {
            mLockerList.setVisibility(View.VISIBLE);
        }
        if (isSpining()) {
            stopSpin();
        }
    }

}
