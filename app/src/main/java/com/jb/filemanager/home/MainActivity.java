package com.jb.filemanager.home;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.jb.filemanager.BaseActivity;
import com.jb.filemanager.R;
import com.jb.filemanager.function.privacy.PrivacyGuardActivity;
import com.jb.filemanager.function.splash.SplashActivity;
import com.jb.filemanager.manager.file.FileManager;
import com.jb.filemanager.ui.dialog.ScreenWidthDialog;
import com.jb.filemanager.util.APIUtil;
import com.jb.filemanager.util.ConvertUtil;
import com.jb.filemanager.util.TimeUtil;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends PrivacyGuardActivity implements MainContract.View, View.OnClickListener {

    public static final String ACTION_AGREE_PRIVACY = "action_agree_privacy";

    private MainContract.Presenter mPresenter;

    private MainDrawer mDrawer;

    private ImageView mIvActionBarMenu;
    private TextView mTvActionBarTitle;
    private EditText mEtActionBarSearch;
    private ImageView mIvActionBarSearch;
    private ImageView mIvActionBarMore;

    private PhoneStoragePagerAdapter mPagerAdapter;
    private ViewPager mVpPhoneStorage;
    private ViewPager.SimpleOnPageChangeListener mViewPageChangeListener;
    private TabLayout mTlViewPageTab;
    private LinearLayout mLlBottomOperateContainer;

    private View mViewSearchMask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        if (mVpPhoneStorage != null) {
            if (mViewPageChangeListener != null) {
                mVpPhoneStorage.removeOnPageChangeListener(mViewPageChangeListener);
                mViewPageChangeListener = null;
            }
        }
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
            mPresenter.onClickBackButton(true);
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
    protected void agreePrivacy() {
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        String action = intent.getAction();
        if (!TextUtils.isEmpty(action) && !action.equals(ACTION_AGREE_PRIVACY)) {
            gotoSplashActivity();
        }

        initView();

        mPresenter = new MainPresenter(this, new MainSupport());
        mPresenter.onCreate(getIntent());

        initDrawer(this);
    }

    // public start


    // public end


    // private start

    private void initView() {
        mIvActionBarMenu = (ImageView) findViewById(R.id.iv_main_action_bar_menu);
        if (mIvActionBarMenu != null) {
            mIvActionBarMenu.setOnClickListener(this);
        }

        mTvActionBarTitle = (TextView) findViewById(R.id.tv_main_action_bar_title);
        if (mTvActionBarTitle != null) {
            mTvActionBarTitle.getPaint().setAntiAlias(true);
        }

        mEtActionBarSearch = (EditText) findViewById(R.id.et_main_action_bar_search);
        if (mEtActionBarSearch != null) {
            mEtActionBarSearch.getPaint().setAntiAlias(true);
            mEtActionBarSearch.setOnClickListener(this);
        }

        mIvActionBarSearch = (ImageView) findViewById(R.id.iv_main_action_bar_search);
        if (mIvActionBarSearch != null) {
            mIvActionBarSearch.setOnClickListener(this);
        }

        mIvActionBarMore = (ImageView) findViewById(R.id.iv_main_action_bar_more);
        if (mIvActionBarMore != null) {
            mIvActionBarMore.setOnClickListener(this);
        }

        // Mask 显示后要屏蔽触摸事件
        mViewSearchMask = findViewById(R.id.view_home_search_mask);
        mViewSearchMask.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        mVpPhoneStorage = (ViewPager) findViewById(R.id.vp_main_phone_storage);
        if (mVpPhoneStorage != null) {
            mPagerAdapter = new PhoneStoragePagerAdapter(getSupportFragmentManager());
            mVpPhoneStorage.setAdapter(mPagerAdapter);

            mViewPageChangeListener = new ViewPager.SimpleOnPageChangeListener() {
                @Override
                public void onPageSelected(int position) {
                    if (mPresenter != null) {
                        mPresenter.onSwitchTab(position);
                    }
                }
            };
            mVpPhoneStorage.addOnPageChangeListener(mViewPageChangeListener);
        }

        mTlViewPageTab = (TabLayout) findViewById(R.id.tl_main_view_pager_tab);
        if (mTlViewPageTab != null && mVpPhoneStorage != null) {
            mTlViewPageTab.setupWithViewPager(mVpPhoneStorage);
        }

        mLlBottomOperateContainer = (LinearLayout) findViewById(R.id.ll_main_bottom_operate_container);
        if (mLlBottomOperateContainer != null) {
            TextView cut = (TextView)mLlBottomOperateContainer.findViewById(R.id.tv_main_bottom_cut);
            if (cut != null) {
                cut.getPaint().setAntiAlias(true);
                cut.setOnClickListener(this);
            }

            TextView copy = (TextView)mLlBottomOperateContainer.findViewById(R.id.tv_main_bottom_copy);
            if (copy != null) {
                copy.getPaint().setAntiAlias(true);
                copy.setOnClickListener(this);
            }

            TextView paste = (TextView)mLlBottomOperateContainer.findViewById(R.id.tv_main_bottom_delete);
            if (paste != null) {
                paste.getPaint().setAntiAlias(true);
                paste.setOnClickListener(this);
            }

            ImageView more = (ImageView)mLlBottomOperateContainer.findViewById(R.id.iv_main_bottom_more);
            if (more != null) {
                more.setOnClickListener(this);
            }
        }

    }

    private void gotoSplashActivity() {
        Intent intent = new Intent(this, SplashActivity.class);
        startActivity(intent);
    }

    private void initDrawer(BaseActivity activity) {
        if (mDrawer == null) {
            mDrawer = new MainDrawer(activity);
        }
        mDrawer.initDrawer();
    }


    // private end



    // implements MainContract.View


    @Override
    public void showNormalStatus(int tabPos) {
        int moreButtonStatus;
        switch (tabPos) {
            case 0:
                moreButtonStatus = View.GONE;
                break;
            case 1:
                moreButtonStatus = View.VISIBLE;
                break;
            default:
                moreButtonStatus = View.GONE;
                break;
        }

        if (mEtActionBarSearch != null) {
            mEtActionBarSearch.setVisibility(View.GONE);
        }

        if (mIvActionBarMenu != null) {
            mIvActionBarMenu.setVisibility(View.VISIBLE);
        }

        if (mTvActionBarTitle != null) {
            mTvActionBarTitle.setVisibility(View.VISIBLE);
        }

        if (mIvActionBarMore != null) {
            mIvActionBarMore.setVisibility(moreButtonStatus);
        }

        if (mViewSearchMask != null) {
            mViewSearchMask.setVisibility(View.GONE);
        }

    }

    @Override
    public void showSearchStatus() {
        if (mEtActionBarSearch != null) {
            mEtActionBarSearch.setVisibility(View.VISIBLE);
        }

        if (mIvActionBarMenu != null) {
            mIvActionBarMenu.setVisibility(View.GONE);
        }

        if (mTvActionBarTitle != null) {
            mTvActionBarTitle.setVisibility(View.GONE);
        }

        if (mIvActionBarMore != null) {
            mIvActionBarMore.setVisibility(View.GONE);
        }

        if (mViewSearchMask != null) {
            mViewSearchMask.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void showActionMoreOperatePopWindow() {
        int resId = R.layout.pop_main_action_more;

        // 一个自定义的布局，作为显示的内容
        View contentView = getLayoutInflater().inflate(resId, null);
        TextView newFolder = (TextView) contentView.findViewById(R.id.tv_main_action_more_new_folder);
        TextView sortBy = (TextView) contentView.findViewById(R.id.tv_main_action_more_sort_by);

        final PopupWindow popupWindow = new PopupWindow(contentView,
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);

        popupWindow.setTouchable(true);

        popupWindow.setTouchInterceptor(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
                // 这里如果返回true的话，touch事件将被拦截
                // 拦截后 PopupWindow的onTouchEvent不被调用，这样点击外部区域无法dismiss
            }
        });

        // 如果不设置PopupWindow的背景，无论是点击外部区域还是Back键都无法dismiss弹框
        // 我觉得这里是API的一个bug
        popupWindow.setBackgroundDrawable(APIUtil.getDrawable(this, R.color.white));

        // 设置好参数之后再show
        popupWindow.showAsDropDown(mIvActionBarMore,
                mIvActionBarMore.getWidth(),
                0);

        if (newFolder != null) {
            newFolder.getPaint().setAntiAlias(true);
            newFolder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupWindow.dismiss();
                    if (mPresenter != null) {
                        mPresenter.onClickActionNewFolderButton();
                    }
                }
            });
        }

        if (sortBy != null) {
            sortBy.getPaint().setAntiAlias(true);
            sortBy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupWindow.dismiss();
                    if (mPresenter != null) {
                        mPresenter.onClickActionSortByButton();
                    }
                }
            });
        }
    }

    @Override
    public void showBottomMoreOperatePopWindow(boolean multiSelected) {
        int resId;
        if (multiSelected) {
            resId = R.layout.pop_mutli_file_operate_more;
        } else {
            resId = R.layout.pop_single_file_operate_more;
        }

        // 一个自定义的布局，作为显示的内容
        View contentView = getLayoutInflater().inflate(resId, null);
        TextView details = (TextView) contentView.findViewById(R.id.tv_main_operate_more_detail);
        TextView rename = (TextView) contentView.findViewById(R.id.tv_main_operate_more_rename);

        final PopupWindow popupWindow = new PopupWindow(contentView,
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);

        popupWindow.setTouchable(true);

        popupWindow.setTouchInterceptor(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
                // 这里如果返回true的话，touch事件将被拦截
                // 拦截后 PopupWindow的onTouchEvent不被调用，这样点击外部区域无法dismiss
            }
        });

        // 如果不设置PopupWindow的背景，无论是点击外部区域还是Back键都无法dismiss弹框
        // 我觉得这里是API的一个bug
        popupWindow.setBackgroundDrawable(APIUtil.getDrawable(this, R.color.white));

        // 设置好参数之后再show
        popupWindow.showAsDropDown(mLlBottomOperateContainer,
                mLlBottomOperateContainer.getWidth() - contentView.getWidth(),
                5);

        if (details != null) {
            details.getPaint().setAntiAlias(true);
            details.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mPresenter != null) {
                        mPresenter.onClickOperateDetailButton();
                    }
                    popupWindow.dismiss();
                }
            });
        }

        if (rename != null) {
            rename.getPaint().setAntiAlias(true);
            rename.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mPresenter != null) {
                        mPresenter.onClickOperateRenameButton();
                    }
                    popupWindow.dismiss();
                }
            });
        }
    }

    @Override
    public void showDeleteConfirmDialog() {
        View dialogView = View.inflate(this, R.layout.dialog_main_delete_confirm, null);
        TextView okButton = (TextView) dialogView.findViewById(R.id.tv_main_delete_confirm_confirm);
        TextView cancelButton = (TextView) dialogView.findViewById(R.id.tv_main_delete_confirm_cancel);

        final ScreenWidthDialog dialog = new ScreenWidthDialog(this, dialogView, true);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        }); // 取消按钮点击事件

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (mPresenter != null) {
                    mPresenter.onClickConfirmDeleteButton();
                }
            }
        }); // 确定按钮点击事件

        dialog.show();
    }

    @Override
    public void showRenameDialog() {
        View dialogView = View.inflate(this, R.layout.dialog_main_rename, null);
        TextView okButton = (TextView) dialogView.findViewById(R.id.tv_main_rename_confirm);
        TextView cancelButton = (TextView) dialogView.findViewById(R.id.tv_main_rename_cancel);
        final EditText editText = (EditText) dialogView.findViewById(R.id.et_main_rename_input);

        final ScreenWidthDialog dialog = new ScreenWidthDialog(this, dialogView, true);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        }); // 取消按钮点击事件

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPresenter != null) {
                    boolean success = mPresenter.onClickConfirmRenameButton(editText.getText().toString());
                    if (success) {
                        dialog.dismiss();
                    } else {
                        // TODO 处理失败
                    }
                }
            }
        }); // 确定按钮点击事件

        dialog.show();
    }

    @Override
    public void showDetailSingleFile(File file) {
        if (file != null && file.exists()) {
            View dialogView = View.inflate(this, R.layout.dialog_main_single_file_detail, null);
            TextView okButton = (TextView) dialogView.findViewById(R.id.tv_main_single_file_detail_confirm);
            TextView name = (TextView) dialogView.findViewById(R.id.tv_main_single_file_detail_name_value);
            TextView location = (TextView) dialogView.findViewById(R.id.tv_main_single_file_detail_location_value);
            TextView modifyTime = (TextView) dialogView.findViewById(R.id.tv_main_single_file_detail_modify_time_value);
            TextView size = (TextView) dialogView.findViewById(R.id.tv_main_single_file_detail_size_value);
            TextView containTitle = (TextView) dialogView.findViewById(R.id.tv_main_single_file_detail_contain_title);
            TextView containValue = (TextView) dialogView.findViewById(R.id.tv_main_single_file_detail_contain_value);

            name.setText(file.getName());
            location.setText(file.getAbsolutePath());
            modifyTime.setText(TimeUtil.getTime(file.lastModified()));
            size.setText(ConvertUtil.getReadableSize(file.length()));

            if (file.isDirectory()) {
                int[] counts = FileManager.getInstance().countFolderAndFile(file);
                containValue.setText(getString(R.string.main_dialog_single_detail_contain, counts[0], counts[1]));
            } else {
                containTitle.setVisibility(View.GONE);
                containValue.setVisibility(View.GONE);
            }

            final ScreenWidthDialog dialog = new ScreenWidthDialog(this, dialogView, true);

            okButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            }); // 确定按钮点击事件

            dialog.show();
        }
    }

    @Override
    public void showDetailMultiFile(ArrayList<File> files) {
        if (files != null && files.size() > 0) {
            View dialogView = View.inflate(this, R.layout.dialog_main_multi_files_detail, null);
            TextView okButton = (TextView) dialogView.findViewById(R.id.tv_main_multi_files_detail_confirm);
            TextView size = (TextView) dialogView.findViewById(R.id.tv_main_multi_files_detail_size_value);
            TextView containValue = (TextView) dialogView.findViewById(R.id.tv_main_multi_files_detail_contain_value);

            long filesSize = 0L;
            Integer folderTotalCount = 0;
            Integer fileTotalCount = 0;
            for (File file : files) {
                int[] count = FileManager.getInstance().countFolderAndFile(file);
                filesSize += file.length();
                folderTotalCount += count[0];
                fileTotalCount += count[1];

                if (file.isDirectory()) {
                    folderTotalCount++;
                } else {
                    fileTotalCount++;
                }
            }
            size.setText(ConvertUtil.getReadableSize(filesSize));
            containValue.setText(getString(R.string.main_dialog_single_detail_contain, folderTotalCount, fileTotalCount));

            final ScreenWidthDialog dialog = new ScreenWidthDialog(this, dialogView, true);
            okButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            }); // 确定按钮点击事件

            dialog.show();
        }
    }

    @Override
    public void showNewFolderDialog() {
        View dialogView = View.inflate(this, R.layout.dialog_main_create_folder, null);
        TextView okButton = (TextView) dialogView.findViewById(R.id.tv_main_create_folder_confirm);
        TextView cancelButton = (TextView) dialogView.findViewById(R.id.tv_main_create_folder_cancel);
        final EditText editText = (EditText) dialogView.findViewById(R.id.et_main_create_folder_input);

        final ScreenWidthDialog dialog = new ScreenWidthDialog(this, dialogView, true);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        }); // 取消按钮点击事件

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mPresenter != null) {
                    boolean success = mPresenter.onClickConfirmCreateFolderButton(editText.getText().toString());
                    if (success) {
                        dialog.dismiss();
                    } else {
                        // TODO 处理失败
                    }
                }
            }
        }); // 确定按钮点击事件

        dialog.show();
    }

    @Override
    public void showSortByDialog() {
        // TODO
        Toast.makeText(this, "显示排序dialog", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void updateSelectedFileChange() {
        ArrayList<File> selectedFiles = FileManager.getInstance().getSelectedFiles();
        if (selectedFiles != null && selectedFiles.size() > 0) {
            mLlBottomOperateContainer.setVisibility(View.VISIBLE);
        } else {
            mLlBottomOperateContainer.setVisibility(View.GONE);
        }
    }

    @Override
    public void openDrawer(int openType) {
        if (mDrawer != null) {
            mDrawer.setOpenType(openType);
            mDrawer.openDrawerWithDelay(0);
        }
    }

    @Override
    public void finishActivity() {
        super.onBackPressed();
    }

    // implements View.OnClickListener
    @Override
    public void onClick(View view) {
        if (mQuickClickGuard.isQuickClick(view.getId())) {
            return;
        }
        switch (view.getId()){
            case R.id.iv_main_action_bar_menu:
                if (mPresenter != null) {
                    mPresenter.onClickDrawerButton();
                }
                break;
            case R.id.iv_main_action_bar_search:
                if (mPresenter != null) {
                    mPresenter.onClickActionSearchButton();
                }
                break;
            case R.id.iv_main_action_bar_more:
                if (mPresenter != null) {
                    mPresenter.onClickActionMoreButton();
                }
                break;
            case R.id.tv_main_bottom_cut:
                if (mPresenter != null) {
                    mPresenter.onClickOperateCutButton();
                }
                break;
            case R.id.tv_main_bottom_copy:
                if (mPresenter != null) {
                    mPresenter.onClickOperateCopyButton();
                }
                break;
            case R.id.tv_main_bottom_delete:
                if (mPresenter != null) {
                    mPresenter.onClickOperateDeleteButton();
                }
                break;
            case R.id.iv_main_bottom_more:
                if (mPresenter != null) {
                    mPresenter.onClickOperateMoreButton();
                }
                break;
            default:
                break;
        }
    }





    // private class

    // Since this is an object collection, use a FragmentStatePagerAdapter,
    // and NOT a FragmentPagerAdapter.
    private class PhoneStoragePagerAdapter extends FragmentPagerAdapter {
        PhoneStoragePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            Fragment fragment = null;
            switch (i){
                case 0: {
                    fragment = new CategoryFragment();
//                    Bundle args = new Bundle();
//                    fragment.setArguments(args);
                }
                    break;
                case 1: {
                    fragment = new StorageFragment();
//                    Bundle args = new Bundle();
//                    fragment.setArguments(args);
                }
                    break;
                default:
                    break;
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            String result;
            switch (position){
                case 0:
                    result = getString(R.string.main_category_title);
                    break;
                case 1:
                    result = getString(R.string.main_storage_title);
                    break;
                default:
                    result = "";
                    break;
            }
            return result;
        }
    }
}
