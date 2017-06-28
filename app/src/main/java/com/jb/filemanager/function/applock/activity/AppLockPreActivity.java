package com.jb.filemanager.function.applock.activity;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.jb.filemanager.R;
import com.jb.filemanager.TheApplication;
import com.jb.filemanager.function.applock.adapter.AppLockPreAdapter;
import com.jb.filemanager.function.applock.dialog.BackTipDialog;
import com.jb.filemanager.function.applock.model.bean.LockerItem;
import com.jb.filemanager.function.applock.presenter.AppLockPreContract;
import com.jb.filemanager.function.applock.presenter.AppLockPrePresenter;
import com.jb.filemanager.function.applock.presenter.AppLockPreSupport;
import com.jb.filemanager.function.applock.view.SearchBarLayout;
import com.jb.filemanager.util.APIUtil;
import com.jb.filemanager.util.AppUtils;

import java.util.List;


public class AppLockPreActivity extends BaseProgressActivity implements AppLockPreContract.View {

    public final int REQUEST_CODE = 1;

    private final String TAG = "AppLockPreActivity";

    //搜索框
    private SearchBarLayout mSearchBarLayout;

    private View mBack;
    private View mTitle;
    //顶部背景的渐变色
    private FrameLayout mRootGradientBg;
    //操作按钮的渐变色
//    private View mOperateGradientBg;
    //应用列表
    private ListView mLockAppsList;
    //适配器
    private AppLockPreAdapter mAppLockPreAdapter;

    //显示默认推荐的应用数目
    private TextView mHeadDefaultSelectedNum;

    private View mContainerRoot;

    private AppLockPreContract.Presenter mPresenter;

    private TextView mOperaterBtu;

    private BackTipDialog mBackTipDialog;

    private boolean isOperateEnable = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_applock_pre);
        initView();
        initListener();
        mPresenter.loadData();
    }

    /**
     * 初始化所有视图
     */
    private void initView() {
        mSearchBarLayout = (SearchBarLayout) findViewById(R.id.activity_applock_pre_searchbar);
        mRootGradientBg = (FrameLayout) findViewById(R.id.activity_applock_pre_root_bg);
//        mOperateGradientBg = findViewById(R.id.activity_applock_pre_operate_gradient);
        mOperaterBtu = (TextView) findViewById(R.id.activity_applock_pre_operate);
        mOperaterBtu.setTextSize(16);
        mOperaterBtu.setText(getString(R.string.applock_pre_operate_txt));
        mBack = findViewById(R.id.activity_applock_title_icon);
        mTitle = findViewById(R.id.activity_applock_title_word);
        mContainerRoot = findViewById(R.id.activity_applock_main_container);
        mLockAppsList = (ListView) findViewById(R.id.activity_applock_pre_list);
        mHeadDefaultSelectedNum = (TextView) findViewById(R.id.applock_pre_header_text);
        showDefaultRecommendAppsNum(0);
        initGradient();
        mBackTipDialog = new BackTipDialog(this);
        mPresenter = new AppLockPrePresenter(this, new AppLockPreSupport());
    }

    /**
     * 设置监听器
     */
    private void initListener() {
        //搜索数据
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
        //返回
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        mTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        mLockAppsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mAppLockPreAdapter != null) {
                    mAppLockPreAdapter.performItemClick(position);
                }
                if (mPresenter != null) {
                    mPresenter.refreshOperateButState();
                }
            }
        });
        mOperaterBtu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isOperateEnable) {
                    Intent i = new Intent(AppLockPreActivity.this, PsdSettingActivity.class);
                    i.putExtra(PsdSettingActivity.PSD_SETTING_MODE, PsdSettingActivity.PSD_INIT);
                    startActivityForResult(i, REQUEST_CODE);
                }
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
                boolean isPatternPsd = bundle.getBoolean(PsdSettingActivity.PSD_DEFAULT_TYPE_IS_PATTERN);
                if (mPresenter != null) {
                    mPresenter.cacheInitInfo(isPatternPsd, passcode, answer, question);
                }
            }
        }
    }

    /**
     * 初始化所有渐变色
     */
    private void initGradient() {
        int startColor = 0xff3bd6f2;
        int endColor = 0xff0084ff;
        GradientDrawable gradientDrawableLR = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[]{startColor, endColor});
        gradientDrawableLR.setGradientType(GradientDrawable.LINEAR_GRADIENT);
        gradientDrawableLR.setShape(GradientDrawable.RECTANGLE);
        APIUtil.setBackground(mRootGradientBg, gradientDrawableLR);
    }

    @Override
    public void onBackPressed() {
        if (mSearchBarLayout != null && !mSearchBarLayout.safeToSlideClose()) {
            if (mPresenter != null) {
                if (mPresenter.isShouldShowBackTipDialog()) {
                    mBackTipDialog.show();
                    mBackTipDialog.setOnBackTipClickListener(new BackTipDialog.OnBackTipClickListener() {
                        @Override
                        public void onExitClick(View v) {
                            if (!mQuickClickGuard.isQuickClick(v.getId())) {
                                AppLockPreActivity.super.onBackPressed();
                                mSearchBarLayout.release(AppLockPreActivity.this);
                                mPresenter.release();
                            }
                        }
                    });
                } else {
                    super.onBackPressed();
                    mSearchBarLayout.release(this);
                    mPresenter.release();
                }
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
        super.onDestroy();
    }

    @Override
    public void gotoAppLockerView() {
        Intent intent = new Intent(this, AppLockActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void showPermisstionGuideView() {
        mSearchBarLayout.setVisibility(View.GONE);
        findViewById(R.id.activity_applock_pre_permisstion).setVisibility(View.VISIBLE);
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
//        if (mContainerRoot != null) {
//            mContainerRoot.setVisibility(View.GONE);
//        }
        startSpin();
    }

    @Override
    public void showDefaultRecommendAppsNum(int num) {
        if (mHeadDefaultSelectedNum != null) {
            mHeadDefaultSelectedNum.setText(getString(R.string.applock_pre_header_text1_new, num));
        }
    }

    @Override
    public void showAppDatas(List<LockerItem> lockerItems) {
        if (lockerItems != null) {
            if (mAppLockPreAdapter == null) {
                mAppLockPreAdapter = new AppLockPreAdapter(lockerItems);
                mLockAppsList.setAdapter(mAppLockPreAdapter);
            } else {
                mAppLockPreAdapter.bindData(lockerItems);
            }
        }
    }

    @Override
    public void showButtonEnableToLock() {
        isOperateEnable = true;
    }

    @Override
    public void showButtonDisEnable() {
        isOperateEnable = false;
    }

    @Override
    public void showDataLoadFinish() {
        if (mContainerRoot != null) {
            mContainerRoot.setVisibility(View.VISIBLE);
        }
        if (isSpining()) {
            stopSpin();
        }
    }

}
