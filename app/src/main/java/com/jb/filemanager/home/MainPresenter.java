package com.jb.filemanager.home;

import android.content.Intent;
import android.text.TextUtils;
import android.widget.Toast;

import com.jb.filemanager.R;
import com.jb.filemanager.util.FileUtil;
import com.jb.filemanager.util.Logger;

import java.io.File;

/**
 * Created by bill wang on 2017/6/21.
 *
 */

@SuppressWarnings("StatementWithEmptyBody")
public class MainPresenter implements MainContract.Presenter {
    private static final String TAG = "MainPresenter.class";
    /**
     * 判断是否从垃圾清理跳转的标志
     */
    public static final String FILE_EXPLORER = "file_explore";
    /**
     * 附带的title参数 <li>类型：String
     */
    public static final String EXTRA_TITLE = "title";
    /**
     * 附带的文件夹地址参数 <li>类型：StringArray
     */
    public static final String EXTRA_DIRS = "extra_dirs";
    /**
     * 附带的高亮文件路径参数 <li>类型：String
     */
    public static final String EXTRA_FOCUS_FILE = "extra_focus_file";

    /**
     * 当传入多个路径时，默认的顶层目录
     */
    private static final String DEFAULT_ROOT_DIR = ".";

    private MainContract.View mView;
    private MainContract.Support mSupport;

    private int mCurrentTab = 0;

    private long mExitTime;

    private String mCurrentPath;
    private String mRootDir;//要到达的路径
    private boolean mIsFileExplorer;

    MainPresenter(MainContract.View view, MainContract.Support support) {
        mView = view;
        mSupport = support;
    }

    @Override
    public void onCreate(Intent intent) {

        handleTrashFiles(intent);
    }

    @Override
    public void onResume() {
        MainActivity activity = (MainActivity)mView;
        boolean haveDateToPase = activity.getIntent().getBooleanExtra("HAVE_PAST_DATE",false);
        activity.getIntent().removeExtra("HAVE_PAST_DATE");
        if (haveDateToPase) {
            mView.showStoragePage();
        }
    }

    @Override
    public void onPause() {

    }

    @Override
    public void onDestroy() {
        mView = null;
        mSupport = null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

    @Override
    public void onClickBackButton(boolean systemBack) {
        if (mView != null) {
            if (systemBack) {
                if (System.currentTimeMillis() - mExitTime > 2000) {
                    mExitTime = System.currentTimeMillis();
                    Toast.makeText(mSupport.getContext(), R.string.main_double_click_exit_app_tips, Toast.LENGTH_SHORT).show();
                } else {
                    mView.finishActivity();
                }
            } else {
                // 首页没有非系统返回
            }
        }
    }

    @Override
    public void onPressHomeKey() {
        // nothing to do
    }

    @Override
    public void onSwitchTab(int pos) {
        if (mView != null) {
            mCurrentTab = pos;
            mView.showNormalStatus(mCurrentTab);
        }
    }

    @Override
    public void onClickDrawerButton() {
        if (mView != null) {
            mView.openDrawer(MainDrawer.CLI_OPEN);
        }
    }

    @Override
    public void onClickActionSearchButton() {
        if (mView != null) {
            mView.goToSearchActivity();
        }
    }

    @Override
    public void onClickActionMoreButton() {
        if (mView != null) {
            mView.showActionMoreOperatePopWindow();
        }
    }

    @Override
    public void onClickActionNewFolderButton() {
        if (mView != null) {
            mView.showNewFolderDialog();
        }
    }

    @Override
    public void onClickActionSortByButton() {
        if (mView != null) {
            mView.showSortByDialog();
        }
    }

    @Override
    public boolean onClickConfirmCreateFolderButton(String name) {
        return FileUtil.createFolder(mCurrentPath + File.separator + name);
    }

    @Override
    public void updateCurrentPath(String path) {
        mCurrentPath = path;
    }

    @Override
    public String getCurrentPath() {
        return mCurrentPath;
    }

    @Override
    public boolean isFileExplorer() {
        return mIsFileExplorer;
    }

    @Override
    public String getTargetFilePath() {
        return mRootDir;//目标路径
    }

    private void handleTrashFiles(Intent intent) {
        // 获取路径参数
        String filePath = null;
        mIsFileExplorer = intent.getBooleanExtra(FILE_EXPLORER, false);
        if (!mIsFileExplorer) {//不是文件预览  那么直接返回
            return;
        }
        String[] baseDirs = intent.getStringArrayExtra(EXTRA_DIRS);
        if (baseDirs == null) {
            filePath = intent.getStringExtra(EXTRA_FOCUS_FILE);

        }

        if (baseDirs != null || filePath != null) {
            if (baseDirs != null) {
                if (baseDirs.length > 1) {
                    mRootDir = DEFAULT_ROOT_DIR;
                } else {
                    mRootDir = baseDirs[0];
                }
            } else {
                final int index;
                if (!TextUtils.isEmpty(filePath)) {
                    try {
                        index = filePath.lastIndexOf("/");
                        mRootDir = filePath.substring(0, index);
                    } catch (Exception e) {
                        mView.finishActivity();
                    }
                }
            }
        }

        Logger.d(TAG, "   目标路径为: " + mRootDir);
    }
}
