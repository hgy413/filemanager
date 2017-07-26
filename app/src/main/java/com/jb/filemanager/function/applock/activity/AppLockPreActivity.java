package com.jb.filemanager.function.applock.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.view.ViewStub;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.jb.filemanager.R;
import com.jb.filemanager.TheApplication;
import com.jb.filemanager.function.applock.adapter.ApplockPreFloatAdapter;
import com.jb.filemanager.function.applock.model.bean.AppLockGroupData;
import com.jb.filemanager.function.applock.presenter.AppLockPreContract;
import com.jb.filemanager.function.applock.presenter.AppLockPrePresenter;
import com.jb.filemanager.function.applock.presenter.AppLockPreSupport;
import com.jb.filemanager.function.applock.view.SearchBarLayout;
import com.jb.filemanager.statistics.StatisticsConstants;
import com.jb.filemanager.statistics.StatisticsTools;
import com.jb.filemanager.ui.widget.FloatingGroupExpandableListView;
import com.jb.filemanager.ui.widget.WrapperExpandableListAdapter;
import com.jb.filemanager.util.AppUtils;
import com.jb.filemanager.util.imageloader.IconLoader;

import java.util.List;


public class AppLockPreActivity extends BaseProgressActivity implements AppLockPreContract.View {

    public final int REQUEST_CODE = 1;

    private final String TAG = "AppLockPreActivity";
    //搜索框
    private SearchBarLayout mSearchBarLayout;
    private View mBack;
    private TextView mTitle;
    //应用列表
    private FloatingGroupExpandableListView mLockAppsList;
    //适配器
    private ApplockPreFloatAdapter mAppLockPreAdapter;
    //显示默认推荐的应用数目
    private TextView mHeadDefaultSelectedNum;

    private AppLockPreContract.Presenter mPresenter;
    //操作按钮
    private ImageView mOperaterBtu;
    //搜索
    private ImageView mSearch;
    //设置
    private ImageView mSetting;

    private ViewStub mPermissionRequestLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_applock_pre);
        IconLoader.ensureInitSingleton(getApplicationContext());
        IconLoader.getInstance().bindServicer(this);
        initView();
        initListener();
        mPresenter.loadData();
    }

    /**
     * 初始化所有视图
     */
    private void initView() {
        mSearchBarLayout = (SearchBarLayout) findViewById(R.id.activity_applock_pre_search_bar);
        findViewById(android.R.id.content).setBackgroundColor(0xFFFF7D7D);
        mOperaterBtu = (ImageView) findViewById(R.id.activity_applock_pre_lock);
        mBack = findViewById(R.id.common_applock_bar_layout_back);
        mTitle = (TextView) findViewById(R.id.common_applock_bar_layout_title);
        mTitle.setText(R.string.activity_applock_title);
        mSetting = (ImageView) findViewById(R.id.common_applock_bar_layout_setting);
        mSetting.setVisibility(View.VISIBLE);
        mSearch = (ImageView) findViewById(R.id.common_applock_bar_layout_search);
        mSearch.setVisibility(View.VISIBLE);
        mLockAppsList = (FloatingGroupExpandableListView) findViewById(R.id.activity_applock_pre_float_view);
        mHeadDefaultSelectedNum = (TextView) findViewById(R.id.activity_applock_pre_tip1);
        mPermissionRequestLayout = (ViewStub) findViewById(R.id.activity_applock_pre_permisstion);
        showDefaultRecommendAppsNum(0);
        mPresenter = new AppLockPrePresenter(this, new AppLockPreSupport());
    }

    /**
     * 设置监听器
     */
    private void initListener() {
        //搜索数据
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
        //返回
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSearchBarLayout != null && !mSearchBarLayout.safeToSlideClose()) {
                    if (mPresenter != null) {
                        StatisticsTools.upload(StatisticsConstants.APPLOCK_EXIT, String.valueOf(2));
                        AppLockPreActivity.super.onBackPressed();
                        mSearchBarLayout.release(AppLockPreActivity.this);
                        mPresenter.release();
                    }
                } else {
                    if (mPresenter != null) {
                        mPresenter.search(null);
                    }
                }
            }
        });
        mTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        mLockAppsList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                if (mAppLockPreAdapter != null) {
                    if (groupPosition == 0) {
                        StatisticsTools.upload(StatisticsConstants.APPLOCK_PRE_RE_CHECK_CLI);
                    } else {
                        StatisticsTools.upload(StatisticsConstants.APPLOCK_PRE_OTHER_CHECK_CLI);
                    }
                    mAppLockPreAdapter.performItemClick(groupPosition, childPosition);
                }
                return true;
            }
        });
        mLockAppsList.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                if (groupPosition == 0) {
                    StatisticsTools.upload(StatisticsConstants.APPLOCK_RECOMMOND_FLOD_CLI);
                } else {
                    StatisticsTools.upload(StatisticsConstants.APPLOCK_OTHER_FLOD_CLI);
                }
            }
        });
        mOperaterBtu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPresenter != null) {
                    mPresenter.handleOperateClick();
                }
                StatisticsTools.upload(StatisticsConstants.APPLOCK_START_LOCK);
            }
        });
        mSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StatisticsTools.upload(StatisticsConstants.APPLOCK_SEARCH_CLI);
                mSearchBarLayout.safeToSlideOpen();
            }
        });

        mSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击设置统计
                StatisticsTools.upload(StatisticsConstants.APPLOCK_SETTING_CLI);
                Intent intent = new Intent(AppLockPreActivity.this, AppLockSettingActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mPresenter != null) {
            mPresenter.dealResume();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            Bundle bundle = data.getExtras();
            if (bundle != null) {
                String answer = bundle.getString(PsdSettingActivity.PSD_ANSWER_WORD);
                String question = bundle.getString(PsdSettingActivity.PSD_QUESTION_WORD);
                String passcode = bundle.getString(PsdSettingActivity.PSD_GRAPHICAL_PASSCODE);
                boolean isLockForLeave = bundle.getBoolean(PsdSettingActivity.PSD_LOCK_OPTIONS);
                if (mPresenter != null) {
                    mPresenter.cacheInitInfo(passcode, answer, question, isLockForLeave);
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (mSearchBarLayout != null && !mSearchBarLayout.safeToSlideClose()) {
            if (mPresenter != null) {
                StatisticsTools.upload(StatisticsConstants.APPLOCK_EXIT, String.valueOf(1));
                super.onBackPressed();
                mSearchBarLayout.release(this);
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
        IconLoader.getInstance().unbindServicer(this);
        if (mPresenter != null) {
            mPresenter.release();
        }
        super.onDestroy();
    }

    @Override
    public void gotoAppLockerView() {
        Intent intent = new Intent(this, AppLockActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void gotoSetPsdView() {
        Intent i = new Intent(AppLockPreActivity.this, PsdSettingActivity.class);
        i.putExtra(PsdSettingActivity.PSD_SETTING_MODE, PsdSettingActivity.PSD_INIT);
        startActivityForResult(i, REQUEST_CODE);
    }

    @Override
    public void showPermisstionGuideView() {
        mSearchBarLayout.safeToSlideClose();
        findViewById(android.R.id.content).setBackgroundColor(0xFF44D6C3);
        mSearch.setVisibility(View.GONE);
        mSetting.setVisibility(View.GONE);
        mPermissionRequestLayout.inflate();
        findViewById(R.id.view_usage_permisstion_layout_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppUtils.openUsageAccess(TheApplication.getAppContext(), 0);
                if (mPresenter != null) {
                    mPresenter.dealPermissionCheck();
                }
            }
        });
    }

    @Override
    public void showDataLoading() {
        startSpin();
    }

    @Override
    public void showDefaultRecommendAppsNum(int num) {
        if (mHeadDefaultSelectedNum != null) {
            mHeadDefaultSelectedNum.setText(getString(R.string.applock_pre_header_text1_new, num));
        }
    }

    @Override
    public void showAppDatas(List<AppLockGroupData> appLockGroupDataList) {
        if (mAppLockPreAdapter == null) {
            mAppLockPreAdapter = new ApplockPreFloatAdapter(appLockGroupDataList);
            mLockAppsList.setAdapter(new WrapperExpandableListAdapter(mAppLockPreAdapter));
        } else {
            mAppLockPreAdapter.bindData(appLockGroupDataList);
        }
        if (appLockGroupDataList != null) {
            //默认打开页面
            for (int i = 0; i < appLockGroupDataList.size(); i++) {
                mLockAppsList.expandGroup(i);
            }
        }
    }

    @Override
    public void showDataLoadFinish() {
        if (isSpining()) {
            stopSpin();
        }
    }

}
