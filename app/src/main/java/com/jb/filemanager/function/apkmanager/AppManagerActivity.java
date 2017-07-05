package com.jb.filemanager.function.apkmanager;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jb.filemanager.BaseActivity;
import com.jb.filemanager.R;
import com.jb.filemanager.function.apkmanager.searchresult.AppManagerSearchResultActivity;
import com.jb.filemanager.function.apkmanager.searchresult.SearchResultBean;
import com.jb.filemanager.function.scanframe.bean.appBean.AppItemInfo;
import com.jb.filemanager.ui.widget.WrapperExpandableListAdapter;
import com.jb.filemanager.util.Logger;
import com.jb.filemanager.util.imageloader.IconLoader;

import java.util.ArrayList;
import java.util.List;

public class AppManagerActivity extends BaseActivity implements AppManagerContract.View, View.OnClickListener {

    public static final int UNINSTALL_APP_REQUEST_CODE = 101;
    public static final String TAG = "AppManagerActivity";
    public static final String SEARCH_RESULT = "search_result";
    public static final int SEARCH_RESULT_REQUEST_CODE = 102;
    private AppManagerPresenter mPresenter;
    private LinearLayout mLlTitle;
    private TextView mTvCommonActionBarWithSearchTitle;
    private EditText mEtCommonActionBarWithSearchSearch;
    private ImageView mIvCommonActionBarWithSearchSearch;
    private ExpandableListView mElvApk;
    private AppManagerAdapter mAdapter;
    private TextView mTvBottomDelete;
    private int mChosenCount;
    private List<AppGroupBean> mAppInfo;
    private BroadcastReceiver mReceiver;
    private boolean mIsSearchInput;
    private boolean mIsSearchProgress;
    private FrameLayout mFlProgressContainer;
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apk_manager);
        IconLoader.ensureInitSingleton(this);
        IconLoader.getInstance().bindServicer(this);
        mPresenter = new AppManagerPresenter(this, new AppManagerSupport());
        mPresenter.onCreate(getIntent());

        initView();
        initData();
        initClick();
        initBroadcastReceiver();
    }

    @Override
    public void initView() {
        mLlTitle = (LinearLayout) findViewById(R.id.ll_title);
        mTvCommonActionBarWithSearchTitle = (TextView) findViewById(R.id.tv_common_action_bar_with_search_title);
        mEtCommonActionBarWithSearchSearch = (EditText) findViewById(R.id.et_common_action_bar_with_search_search);
        mIvCommonActionBarWithSearchSearch = (ImageView) findViewById(R.id.iv_common_action_bar_with_search_search);
        mElvApk = (ExpandableListView) findViewById(R.id.elv_apk);
        mTvBottomDelete = (TextView) findViewById(R.id.tv_bottom_delete);
        mFlProgressContainer = (FrameLayout) findViewById(R.id.fl_progress_container);

        //监听搜索
        mEtCommonActionBarWithSearchSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_UNSPECIFIED || actionId == EditorInfo.IME_ACTION_DONE) {
                    String keyTag = mEtCommonActionBarWithSearchSearch.getText().toString().trim();
                    Logger.d(TAG, keyTag);
                    if (TextUtils.isEmpty(keyTag)) {
                        Toast.makeText(AppManagerActivity.this, "请输入搜索关键字", Toast.LENGTH_SHORT).show();
                        return false;
                    }

                    // 搜索功能主体
                    goToSearchResult(keyTag);
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public void initData() {
        mAppInfo = mPresenter.getAppInfo();
        mAdapter = new AppManagerAdapter(mAppInfo);
        mAdapter.setOnItemChosenListener(new AppManagerAdapter.OnItemChosenListener() {
            @Override
            public void onItemChosen(int chosenCount) {
                handleBottomDeleteShow(chosenCount);
            }
        });
        mElvApk.setAdapter(new WrapperExpandableListAdapter(mAdapter));
        mAdapter.handleCheckedCount();
    }

    @Override
    public void initClick() {
        mTvBottomDelete.setOnClickListener(this);
        mTvCommonActionBarWithSearchTitle.setOnClickListener(this);
        mIvCommonActionBarWithSearchSearch.setOnClickListener(this);
    }

    @Override
    public void initBroadcastReceiver() {
        //监听应用的安装卸载广播   刷系列表
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (mPresenter != null) {
                    mPresenter.refreshData();
                }
            }
        };
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
        intentFilter.addDataScheme("package");
        registerReceiver(mReceiver, intentFilter);
    }

    @Override
    public void releaseBroadcastReceiver() {
        unregisterReceiver(mReceiver);
    }

    @Override
    public void refreshList() {
        mAdapter.setListData(mPresenter.getAppInfo());
    }

    private void handleBottomDeleteShow(int chosenCount) {
        mChosenCount = chosenCount;
        if (chosenCount == 0) {
            mTvBottomDelete.setVisibility(View.GONE);
        } else {
            mTvBottomDelete.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void finishActivity() {
        finish();
    }

    @Override
    public void refreshTitle() {
        mEtCommonActionBarWithSearchSearch.setVisibility(View.INVISIBLE);
        mTvCommonActionBarWithSearchTitle.setVisibility(View.VISIBLE);
        mIsSearchInput = false;
    }

    @Override
    public void hideProgress() {
        mFlProgressContainer.setVisibility(View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mPresenter != null) {
            mPresenter.onResume();
        }
    }

    @Override
    protected void onPause() {
        if (mPresenter != null) {
            mPresenter.onPause();
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (mPresenter != null) {
            mPresenter.onDestroy();
        }
        releaseBroadcastReceiver();
        super.onDestroy();
        IconLoader.getInstance().unbindServicer(this);
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
    }

    @Override
    protected void onPressedHomeKey() {
        if (mPresenter != null) {
            mPresenter.onPressHomeKey();
        }
    }

    @Override
    public void onBackPressed() {
        if (mPresenter != null) {
            mPresenter.onClickBackButton(mIsSearchInput);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mPresenter != null) {
            mPresenter.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onClick(View view) {
        if (mQuickClickGuard.isQuickClick(view.getId())) {
            return;
        }
        switch (view.getId()) {
            case R.id.tv_common_action_bar_with_search_title:
                finishActivity();
                break;
            case R.id.tv_bottom_delete:
                Toast.makeText(AppManagerActivity.this, mChosenCount + "个app被选中了呢   欧尼酱", Toast.LENGTH_SHORT).show();
                List<AppItemInfo> children = mAppInfo.get(0).getChildren();
                for (AppItemInfo childBean : children) {
                    if (childBean.mIsChecked) {
                        uninstallApp(childBean.mAppPackageName);
                    }
                }
                break;
            case R.id.iv_common_action_bar_with_search_search:
                handleSearchButtonClick(mIsSearchInput);
                break;
        }
    }

    private void handleSearchButtonClick(boolean isSearchMode) {
        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        //说明现在是搜索模式  那么点击搜索要进行搜索了(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(WidgetSearchActivity.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);  (WidgetSearchActivity是当前的Activity)

        if (isSearchMode) {
//            inputManager.hideSoftInputFromWindow(mEtCommonActionBarWithSearchSearch.getWindowToken(), InputMethodManager.RESULT_HIDDEN);//手动隐藏输入法  貌似无效?? 至少米3上是有问题的
            /*mEtCommonActionBarWithSearchSearch.clearFocus();
            inputManager.toggleSoftInput(0,InputMethodManager.HIDE_NOT_ALWAYS);*/
//            hideInputMethod(this);
//            onBackPressed();
            goToSearchResult(mEtCommonActionBarWithSearchSearch.getEditableText().toString());
            return;
        }

        //非搜索模式下点击搜索  出现搜索框
        mTvCommonActionBarWithSearchTitle.setVisibility(View.GONE);
        mEtCommonActionBarWithSearchSearch.setVisibility(View.VISIBLE);
        mEtCommonActionBarWithSearchSearch.requestFocus();//请求焦点
        mEtCommonActionBarWithSearchSearch.setText(mAppInfo.get(0).getChild(0).mAppName);//默认内容是第一个的APP的名字
        mEtCommonActionBarWithSearchSearch.selectAll();//全选
        inputManager.showSoftInput(mEtCommonActionBarWithSearchSearch, InputMethodManager.SHOW_IMPLICIT);//手动调起输入法
        mIsSearchInput = true;
    }


    /**
     * 隐藏输入法面板
     *
     * @param activity
     */
    public static void hideInputMethod(Activity activity) {
        if (null == activity) {
            return;
        }
        if (null != activity.getCurrentFocus() && null != activity.getCurrentFocus().getWindowToken()) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }


    //去往搜索界面
    private void goToSearchResult(String keyTag) {
        mFlProgressContainer.setVisibility(View.VISIBLE);
        mFlProgressContainer.requestFocus();
        mFlProgressContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(AppManagerActivity.this, "please wait", Toast.LENGTH_SHORT).show();
            }
        });
        mHandler = new Handler();
        /*mEtCommonActionBarWithSearchSearch.clearFocus();
        mIvCommonActionBarWithSearchSearch.requestFocus();
        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(mEtCommonActionBarWithSearchSearch.getWindowToken(), InputMethodManager.RESULT_HIDDEN);//手动隐藏输入法  貌似无效?? 至少米3上是有问题的*/
        hideInputMethod(this);
        final ArrayList<SearchResultBean> mResultPackage = new ArrayList<>();
        Toast.makeText(AppManagerActivity.this, "我要搜索" + keyTag, Toast.LENGTH_SHORT).show();
        //在本界面处理搜索结果
        keyTag = keyTag.toLowerCase();
        for (AppGroupBean groupBean : mAppInfo) {
            List<AppItemInfo> children = groupBean.getChildren();
            if (children == null || children.isEmpty()) {
                continue;
            }
            for (AppItemInfo childBean : children) {
                if (childBean.mAppName.toLowerCase().contains(keyTag) || childBean.mAppPackageName.toLowerCase().contains(keyTag)) {
                    SearchResultBean resultBean = new SearchResultBean();
                    resultBean.mAppName = childBean.mAppName;
                    resultBean.mPackageName = childBean.mAppPackageName;
                    mResultPackage.add(resultBean);
                }
            }
        }
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(AppManagerActivity.this, AppManagerSearchResultActivity.class);
                intent.putExtra(SEARCH_RESULT, mResultPackage);
                startActivityForResult(intent, SEARCH_RESULT_REQUEST_CODE);
            }
        }, 2500);
    }

    //卸载应用程序
    public void uninstallApp(String pkgName) {
        Intent uninstall_intent = new Intent();
        uninstall_intent.setAction(Intent.ACTION_DELETE);
        uninstall_intent.setData(Uri.parse("package:" + pkgName));
        startActivityForResult(uninstall_intent, UNINSTALL_APP_REQUEST_CODE);
    }
}
