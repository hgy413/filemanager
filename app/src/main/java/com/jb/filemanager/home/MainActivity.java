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
import android.widget.RadioGroup;
import android.widget.TextView;

import com.jb.filemanager.BaseActivity;
import com.jb.filemanager.R;
import com.jb.filemanager.TheApplication;
import com.jb.filemanager.function.feedback.FeedbackActivity;
import com.jb.filemanager.function.privacy.PrivacyGuardActivity;
import com.jb.filemanager.function.rate.dialog.RateDialog;
import com.jb.filemanager.function.rate.dialog.RateFeedbackDialog;
import com.jb.filemanager.function.rate.dialog.RateGuideDialog;
import com.jb.filemanager.function.rate.dialog.RateToGpDialog;
import com.jb.filemanager.function.rate.presenter.RateContract;
import com.jb.filemanager.function.rate.presenter.RatePresenter;
import com.jb.filemanager.function.rate.presenter.RateSupport;
import com.jb.filemanager.function.scanframe.clean.event.CleanScanDoneEvent;
import com.jb.filemanager.function.scanframe.clean.event.CleanScanFileSizeEvent;
import com.jb.filemanager.function.splash.SplashActivity;
import com.jb.filemanager.function.update.AppUpdatePresenter;
import com.jb.filemanager.home.event.SortByChangeEvent;
import com.jb.filemanager.manager.file.FileManager;
import com.jb.filemanager.ui.dialog.MultiFileDetailDialog;
import com.jb.filemanager.ui.dialog.ScreenWidthDialog;
import com.jb.filemanager.ui.dialog.SingleFileDetailDialog;
import com.jb.filemanager.util.APIUtil;
import com.jb.filemanager.util.AppUtils;
import com.jb.filemanager.util.FileUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends PrivacyGuardActivity implements MainContract.View, View.OnClickListener, RateContract.View {

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
    private LinearLayout mLlBottomOperateFirstContainer;
    private LinearLayout mLlBottomOperateSecondContainer;
    ///评分引导
    private RatePresenter mRatePresenter;
    //应用更新提醒
    private AppUpdatePresenter mAppUpdatePresenter;

    private View mViewSearchMask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //暂时不要删除
        try {
            TheApplication.getGlobalEventBus().register(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mPresenter != null) {
            mPresenter.onResume();
        }
//        if (mAppUpdatePresenter != null) {
//            mAppUpdatePresenter.onResume();
//        }
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

        try {//暂时不要删除
            TheApplication.getGlobalEventBus().unregister(this);
        } catch (Exception e) {
            e.printStackTrace();
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

        mRatePresenter = new RatePresenter(this, new RateSupport());
        mAppUpdatePresenter = new AppUpdatePresenter(this);


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

        mLlBottomOperateFirstContainer = (LinearLayout) findViewById(R.id.ll_common_operate_bar_container);
        if (mLlBottomOperateFirstContainer != null) {
            TextView cut = (TextView) mLlBottomOperateFirstContainer.findViewById(R.id.tv_common_operate_bar_cut);
            if (cut != null) {
                cut.getPaint().setAntiAlias(true);
                cut.setOnClickListener(this);
            }

            TextView copy = (TextView) mLlBottomOperateFirstContainer.findViewById(R.id.tv_common_operate_bar_copy);
            if (copy != null) {
                copy.getPaint().setAntiAlias(true);
                copy.setOnClickListener(this);
            }

            TextView paste = (TextView) mLlBottomOperateFirstContainer.findViewById(R.id.tv_common_operate_bar_delete);
            if (paste != null) {
                paste.getPaint().setAntiAlias(true);
                paste.setOnClickListener(this);
            }

            TextView more = (TextView) mLlBottomOperateFirstContainer.findViewById(R.id.tv_common_operate_bar_more);
            if (more != null) {
                more.setOnClickListener(this);
            }
        }

        mLlBottomOperateSecondContainer = (LinearLayout) findViewById(R.id.ll_main_bottom_operate_second_container);
        if (mLlBottomOperateSecondContainer != null) {
            TextView cancel = (TextView) mLlBottomOperateSecondContainer.findViewById(R.id.tv_main_bottom_operate_second_container_cancel);
            if (cancel != null) {
                cancel.getPaint().setAntiAlias(true);
                cancel.setOnClickListener(this);
            }

            TextView ok = (TextView) mLlBottomOperateSecondContainer.findViewById(R.id.tv_main_bottom_operate_second_container_paste);
            if (ok != null) {
                ok.getPaint().setAntiAlias(true);
                ok.setOnClickListener(this);
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
        popupWindow.showAsDropDown(mLlBottomOperateFirstContainer,
                mLlBottomOperateFirstContainer.getWidth() - contentView.getWidth(),
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

            SingleFileDetailDialog singleFileDetailDialog = new SingleFileDetailDialog(this, file, new SingleFileDetailDialog.Listener() {
                @Override
                public void onConfirm(SingleFileDetailDialog dialog) {
                    dialog.dismiss();
                }
            });
            singleFileDetailDialog.show();
        }
    }

    @Override
    public void showDetailMultiFile(ArrayList<File> files) {
        if (files != null && files.size() > 0) {
            MultiFileDetailDialog multiFileDetailDialog = new MultiFileDetailDialog(this, files, new MultiFileDetailDialog.Listener() {
                @Override
                public void onConfirm(MultiFileDetailDialog dialog) {
                    dialog.dismiss();
                }
            });
            multiFileDetailDialog.show();
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
        View dialogView = View.inflate(this, R.layout.dialog_main_sort, null);
        TextView descendButton = (TextView) dialogView.findViewById(R.id.tv_main_sort_descending);
        TextView ascendButton = (TextView) dialogView.findViewById(R.id.tv_main_sort_ascending);
        final RadioGroup itemGroup = (RadioGroup) dialogView.findViewById(R.id.rg_main_sort_by);

        final ScreenWidthDialog dialog = new ScreenWidthDialog(this, dialogView, true);

        descendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int checkId = itemGroup.getCheckedRadioButtonId();
                switch (checkId) {
                    case R.id.rb_main_sort_by_name:
                        FileManager.getInstance().setFileSort(FileUtil.sNameDescendComparator);
                        break;
                    case R.id.rb_main_sort_by_date:
                        FileManager.getInstance().setFileSort(FileUtil.sDateDescendComparator);
                        break;
                    case R.id.rb_main_sort_by_type:
                        FileManager.getInstance().setFileSort(FileUtil.sTypeDescendComparator);
                        break;
                    case R.id.rb_main_sort_by_size:
                        FileManager.getInstance().setFileSort(FileUtil.sSizeDescendComparator);
                        break;
                    default:
                        break;
                }
                EventBus.getDefault().post(new SortByChangeEvent());
                dialog.dismiss();
            }
        }); // 降序按钮点击事件

        ascendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int checkId = itemGroup.getCheckedRadioButtonId();
                switch (checkId) {
                    case R.id.rb_main_sort_by_name:
                        FileManager.getInstance().setFileSort(FileUtil.sNameAscendComparator);
                        break;
                    case R.id.rb_main_sort_by_date:
                        FileManager.getInstance().setFileSort(FileUtil.sDateAscendComparator);
                        break;
                    case R.id.rb_main_sort_by_type:
                        FileManager.getInstance().setFileSort(FileUtil.sTypeAscendComparator);
                        break;
                    case R.id.rb_main_sort_by_size:
                        FileManager.getInstance().setFileSort(FileUtil.sSizeAscendComparator);
                        break;
                    default:
                        break;
                }
                EventBus.getDefault().post(new SortByChangeEvent());
                dialog.dismiss();
            }
        }); // 升序按钮点击事件

        dialog.show();
    }

    @Override
    public void updateView() {
        if (mPresenter != null) {
            ArrayList<File> copyList = FileManager.getInstance().getCopyFiles();
            ArrayList<File> cutList = FileManager.getInstance().getCutFiles();
            if ((copyList != null && copyList.size() > 0) || (cutList != null && cutList.size() > 0)) {
                mLlBottomOperateFirstContainer.setVisibility(View.GONE);
                mLlBottomOperateSecondContainer.setVisibility(View.VISIBLE);
            } else {
                int status = mPresenter.getStatus();
                switch (status) {
                    case MainPresenter.MAIN_STATUS_NORMAL:
                        mLlBottomOperateFirstContainer.setVisibility(View.GONE);
                        mLlBottomOperateSecondContainer.setVisibility(View.GONE);
                        break;
                    case MainPresenter.MAIN_STATUS_SELECT:
                        mLlBottomOperateFirstContainer.setVisibility(View.VISIBLE);
                        mLlBottomOperateSecondContainer.setVisibility(View.GONE);
                        break;
                    default:
                        break;
                }
            }
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
            case R.id.tv_common_operate_bar_cut:
                if (mPresenter != null) {
                    mPresenter.onClickOperateCutButton();
                }
                break;
            case R.id.tv_common_operate_bar_copy:
                if (mPresenter != null) {
                    mPresenter.onClickOperateCopyButton();
                }
                break;
            case R.id.tv_common_operate_bar_delete:
                if (mPresenter != null) {
                    mPresenter.onClickOperateDeleteButton();
                }
                break;
            case R.id.tv_common_operate_bar_more:
                if (mPresenter != null) {
                    mPresenter.onClickOperateMoreButton();
                }
                break;
            case R.id.tv_main_bottom_operate_second_container_cancel:
                if (mPresenter != null) {
                    mPresenter.onClickOperateCancelButton();
                }
                break;
            case R.id.tv_main_bottom_operate_second_container_paste:
                if (mPresenter != null) {
                    mPresenter.onClickOperatePasteButton();
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
                    StorageFragment storageFragment = new StorageFragment();
                    storageFragment.setPresenter(mPresenter);
                    fragment = storageFragment;
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


    /******
     * 评分引导
     * */
    private RateDialog mRateCheerDialog;
    private RateDialog mRateFeedBackDialog;
    private RateDialog mRateLoveDialog;

    @Override
    public void showCheerDialog(RateDialog.OnPressListener onPressListener) {
        if (mRateCheerDialog == null) {
            mRateCheerDialog = new RateGuideDialog(this);
            mRateCheerDialog.setOnPressListener(onPressListener);
        }
        mRateCheerDialog.show();
    }

    @Override
    public void showFeedBackDialog(RateDialog.OnPressListener onPressListener) {
        if (mRateFeedBackDialog == null) {
            mRateFeedBackDialog = new RateFeedbackDialog(this);
            mRateFeedBackDialog.setOnPressListener(onPressListener);
        }
        mRateFeedBackDialog.show();
    }

    @Override
    public void showLoveDialog(RateDialog.OnPressListener onPressListener) {
        if (mRateLoveDialog == null) {
            mRateLoveDialog = new RateToGpDialog(this);
            mRateLoveDialog.setOnPressListener(onPressListener);
        }
        mRateLoveDialog.show();
    }

    @Override
    public void dismissCheerDialog() {
        if (mRateCheerDialog != null) {
            mRateCheerDialog.dismiss();
            mRateCheerDialog = null;
        }
    }

    @Override
    public void dismissFeedBackDialog() {
        if (mRateFeedBackDialog != null) {
            mRateFeedBackDialog.dismiss();
            mRateFeedBackDialog = null;
        }
    }

    @Override
    public void dismissLoveDialog() {
        if (mRateLoveDialog != null) {
            mRateLoveDialog.dismiss();
            mRateLoveDialog = null;
        }
    }

    @Override
    public void gotoFeedBack() {
        startActivity(new Intent(this, FeedbackActivity.class));
    }

    @Override
    public boolean gotoGp() {
        return AppUtils.openGP(this);
    }


    /**
     * 扫描到的文件的大小
     *
     * @param event e
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(CleanScanFileSizeEvent event) {
    }

    @Subscribe
    public void onEventMainThread(CleanScanDoneEvent event) {

    }
}
