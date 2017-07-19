package com.jb.filemanager.function.apkmanager;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jb.filemanager.BaseActivity;
import com.jb.filemanager.Const;
import com.jb.filemanager.R;
import com.jb.filemanager.TheApplication;
import com.jb.filemanager.function.scanframe.bean.appBean.AppItemInfo;
import com.jb.filemanager.function.scanframe.clean.event.AppInstallEvent;
import com.jb.filemanager.function.scanframe.clean.event.AppUninstallEvent;
import com.jb.filemanager.function.search.view.SearchActivity;
import com.jb.filemanager.ui.widget.WrapperExpandableListAdapter;
import com.jb.filemanager.util.imageloader.IconLoader;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

public class AppManagerActivity extends BaseActivity implements AppManagerContract.View, View.OnClickListener {

    public static final int UNINSTALL_APP_REQUEST_CODE = 101;
    public static final String TAG = "AppManagerActivity";
    public static final String SEARCH_RESULT = "search_result";
    public static final int SEARCH_RESULT_REQUEST_CODE = 102;
    private AppManagerPresenter mPresenter;
    private TextView mTvCommonActionBarWithSearchTitle;
    private ImageView mIvCommonActionBarWithSearchSearch;
    private ExpandableListView mElvApk;
    private AppManagerAdapter mAdapter;
    private TextView mTvBottomDelete;
    private int mChosenCount;
    private List<AppGroupBean> mAppInfo;
    private BroadcastReceiver mReceiver;

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
        mTvCommonActionBarWithSearchTitle = (TextView) findViewById(R.id.tv_common_action_bar_title);
        mIvCommonActionBarWithSearchSearch = (ImageView) findViewById(R.id.iv_common_action_bar_search);
        mElvApk = (ExpandableListView) findViewById(R.id.elv_apk);
        mTvBottomDelete = (TextView) findViewById(R.id.tv_bottom_delete);
        mIvCommonActionBarWithSearchSearch.setVisibility(View.VISIBLE);
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
        mElvApk.expandGroup(0, true);//只展开 第一个
    }

    @Override
    public void initClick() {
        mTvBottomDelete.setOnClickListener(this);
        mTvCommonActionBarWithSearchTitle.setOnClickListener(this);
        mIvCommonActionBarWithSearchSearch.setOnClickListener(this);
    }

    @Override
    public void initBroadcastReceiver() {
        /*//监听应用的安装卸载广播   刷系列表
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
        registerReceiver(mReceiver, intentFilter);*/

        EventBus globalEventBus = TheApplication.getGlobalEventBus();
        if (!globalEventBus.isRegistered(this)) {
            globalEventBus.register(this);
        }
    }

    @Override
    public void releaseBroadcastReceiver() {
//        unregisterReceiver(mReceiver);
        EventBus globalEventBus = TheApplication.getGlobalEventBus();
        if (globalEventBus.isRegistered(this)) {
            globalEventBus.unregister(this);
        }
    }

    //监听APP卸载
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(AppUninstallEvent event) {
        mPresenter.refreshData();
    }

    //监听app安装
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(AppInstallEvent event) {
//        mPresenter.refreshData();
    }


    @Override
    public void refreshList() {
        mTvBottomDelete.setVisibility(View.GONE);
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
            mPresenter = null;
        }
        releaseBroadcastReceiver();
        super.onDestroy();
        IconLoader.getInstance().unbindServicer(this);
    }

    @Override
    protected void onPressedHomeKey() {
        if (mPresenter != null) {
            mPresenter.onPressHomeKey();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
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
            case R.id.tv_common_action_bar_title:
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
            case R.id.iv_common_action_bar_search:
                SearchActivity.showSearchResult(this, Const.CategoryType.CATEGORY_TYPE_APP);
                break;
            default:
                break;
        }
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

    //卸载应用程序
    public void uninstallApp(String pkgName) {
        Intent uninstall_intent = new Intent();
        uninstall_intent.setAction(Intent.ACTION_DELETE);
        uninstall_intent.setData(Uri.parse("package:" + pkgName));
        startActivityForResult(uninstall_intent, UNINSTALL_APP_REQUEST_CODE);
    }
}
