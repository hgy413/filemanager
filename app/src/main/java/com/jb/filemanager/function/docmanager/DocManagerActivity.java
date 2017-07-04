package com.jb.filemanager.function.docmanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
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
import com.jb.filemanager.util.Logger;

import java.util.ArrayList;
import java.util.List;

import static com.jb.filemanager.function.apkmanager.AppManagerActivity.hideInputMethod;

public class DocManagerActivity extends BaseActivity implements DocManagerContract.View, View.OnClickListener {
    //    public static final int UNINSTALL_APP_REQUEST_CODE = 101;
    public static final String TAG = "DocManagerActivity";
    public static final String SEARCH_RESULT = "search_result";
    public static final int SEARCH_RESULT_REQUEST_CODE = 102;
    private DocManagerPresenter mPresenter;
    private LinearLayout mLlTitle;
    private TextView mTvCommonActionBarWithSearchTitle;
    private EditText mEtCommonActionBarWithSearchSearch;
    private ImageView mIvCommonActionBarWithSearchSearch;
    private ExpandableListView mElvApk;
    private DocManagerAdapter mAdapter;
    private TextView mTvBottomDelete;
    private int mChosenCount;
    private List<DocGroupBean> mAppInfo;
    private BroadcastReceiver mReceiver;
    private boolean mIsSearchInput;
    private boolean mIsSearchProgress;
    private FrameLayout mFlProgressContainer;
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doc_manager);

        mPresenter = new DocManagerPresenter(this, new DocManagerSupport());
        mPresenter.onCreate(getIntent());
        initView();
        initData();
        initClick();
        initList();
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
                        Toast.makeText(DocManagerActivity.this, "请输入搜索关键字", Toast.LENGTH_SHORT).show();
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

    private void goToSearchResult(String keyTag) {
        mFlProgressContainer.setVisibility(View.VISIBLE);
        mFlProgressContainer.requestFocus();
        mFlProgressContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(DocManagerActivity.this, "please wait", Toast.LENGTH_SHORT).show();
            }
        });
        mHandler = new Handler();
        /*mEtCommonActionBarWithSearchSearch.clearFocus();
        mIvCommonActionBarWithSearchSearch.requestFocus();
        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(mEtCommonActionBarWithSearchSearch.getWindowToken(), InputMethodManager.RESULT_HIDDEN);//手动隐藏输入法  貌似无效?? 至少米3上是有问题的*/
        hideInputMethod(this);
        final ArrayList<DocChildBean> mResultPackage = new ArrayList<>();
        Toast.makeText(DocManagerActivity.this, "我要搜索" + keyTag, Toast.LENGTH_SHORT).show();
        //在本界面处理搜索结果
        keyTag = keyTag.toLowerCase();
        for (DocGroupBean groupBean : mAppInfo) {
            List<DocChildBean> children = groupBean.getChildren();
            if (children == null || children.isEmpty()) {
                continue;
            }
            for (DocChildBean childBean : children) {
                if (childBean.mDocName.toLowerCase().contains(keyTag)) {
                    // TODO: 2017/7/4 add by --miwo 封装搜索结果
                    /*SearchResultBean resultBean = new SearchResultBean();
                    resultBean.mAppName = childBean.mDocName;
                    resultBean.mPackageName = childBean.mPackageName;
                    mResultPackage.add(resultBean);*/
                    mResultPackage.add(childBean);
                }
            }
        }
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // TODO: 2017/7/4 add by --miwo 跳转结果页
                /*Intent intent = new Intent(DocManagerActivity.this, AppManagerSearchResultActivity.class);
                intent.putExtra(SEARCH_RESULT, mResultPackage);
                startActivityForResult(intent, SEARCH_RESULT_REQUEST_CODE);*/
                Toast.makeText(DocManagerActivity.this, "我找到了" + mResultPackage.size() + "个文件呢", Toast.LENGTH_SHORT).show();
            }
        }, 2500);
    }

    @Override
    public void initData() {
        mAppInfo = mPresenter.getDocInfo();
        mAdapter = new DocManagerAdapter(mAppInfo);
        mAdapter.setOnItemChosenListener(new DocManagerAdapter.OnItemChosenListener() {
            @Override
            public void onItemChosen(int chosenCount) {
                handleBottomDeleteShow(chosenCount);
            }
        });
        mElvApk.setAdapter(mAdapter);
        mAdapter.handleCheckedCount();
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
    public void initClick() {
        mTvBottomDelete.setOnClickListener(this);
        mTvCommonActionBarWithSearchTitle.setOnClickListener(this);
        mIvCommonActionBarWithSearchSearch.setOnClickListener(this);
        mElvApk.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int groupPosition,
                                        int childPosition, long id) {
                return false;
            }
        });
    }

    @Override
    public void initList() {

    }

    @Override
    public void refreshList() {
        mAdapter.setListData(mPresenter.getDocInfo());
    }

    @Override
    public void initBroadcastReceiver() {

    }

    @Override
    public void releaseBroadcastReceiver() {

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
            mPresenter.onClickBackButton(false);
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
        if (mQuickClickGuard.isQuickClick(view.getId())){
            return;
        }
        switch (view.getId()){
            case R.id.tv_bottom_delete:
                Toast.makeText(DocManagerActivity.this, "我是占位的bottom啦", Toast.LENGTH_SHORT).show();
                break;
            case R.id.tv_common_action_bar_with_search_title:
                finishActivity();
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
        mEtCommonActionBarWithSearchSearch.setText(mAppInfo.get(0).getChild(0).mDocName);//默认内容是第一个的APP的名字
        mEtCommonActionBarWithSearchSearch.selectAll();//全选
        inputManager.showSoftInput(mEtCommonActionBarWithSearchSearch, InputMethodManager.SHOW_IMPLICIT);//手动调起输入法
        mIsSearchInput = true;
    }
}
