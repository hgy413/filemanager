package com.jb.filemanager.home;

import android.app.Activity;
import android.content.Context;
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
import android.widget.PopupWindow;
import android.widget.TextView;

import com.jb.filemanager.BaseActivity;
import com.jb.filemanager.R;
import com.jb.filemanager.function.feedback.FeedbackActivity;
import com.jb.filemanager.function.privacy.PrivacyGuardActivity;
import com.jb.filemanager.function.rate.dialog.RateDialog;
import com.jb.filemanager.function.rate.dialog.RateFeedbackDialog;
import com.jb.filemanager.function.rate.dialog.RateGuideDialog;
import com.jb.filemanager.function.rate.dialog.RateToGpDialog;
import com.jb.filemanager.function.rate.presenter.RateContract;
import com.jb.filemanager.function.rate.presenter.RatePresenter;
import com.jb.filemanager.function.rate.presenter.RateSupport;
import com.jb.filemanager.function.splash.SplashActivity;
import com.jb.filemanager.function.update.AppUpdatePresenter;
import com.jb.filemanager.home.event.SortByChangeEvent;
import com.jb.filemanager.home.fragment.storage.StorageFragment;
import com.jb.filemanager.manager.file.FileManager;
import com.jb.filemanager.ui.dialog.CreateNewFolderDialog;
import com.jb.filemanager.ui.dialog.SortByDialog;
import com.jb.filemanager.util.APIUtil;
import com.jb.filemanager.util.AppUtils;
import com.jb.filemanager.util.FileUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.Comparator;

import static com.jb.filemanager.home.MainPresenter.EXTRA_DIRS;
import static com.jb.filemanager.home.MainPresenter.EXTRA_FOCUS_FILE;
import static com.jb.filemanager.home.MainPresenter.EXTRA_TITLE;

public class MainActivity extends PrivacyGuardActivity implements MainContract.View, View.OnClickListener, RateContract.View {

    public static final String ACTION_AGREE_PRIVACY = "action_agree_privacy";

    private MainContract.Presenter mPresenter;

    private MainDrawer mDrawer;

    private ImageView mIvActionBarMenu;
    private TextView mTvActionBarTitle;
    private EditText mEtActionBarSearch;
    private ImageView mIvActionBarBack;
    private ImageView mIvActionBarSearch;
    private ImageView mIvActionBarMore;
    private View mViewSearchMask;

    private PhoneStoragePagerAdapter mPagerAdapter;
    private ViewPager mVpPhoneStorage;
    private ViewPager.SimpleOnPageChangeListener mViewPageChangeListener;
    private TabLayout mTlViewPageTab;

    ///评分引导
    private RatePresenter mRatePresenter;
    //应用更新提醒
    private AppUpdatePresenter mAppUpdatePresenter;


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

        mIvActionBarBack = (ImageView) findViewById(R.id.iv_main_action_bar_back);
        if (mIvActionBarBack != null) {
            mIvActionBarBack.setOnClickListener(this);
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
                if (mPresenter != null) {
                    mPresenter.onClickSearchMask();
                }
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

        if (mIvActionBarBack != null) {
            mIvActionBarBack.setVisibility(View.GONE);
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
        if (mIvActionBarBack != null) {
            mIvActionBarBack.setVisibility(View.VISIBLE);
        }

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

        int width = (int)getResources().getDimension(R.dimen.popup_window_width);
        final PopupWindow popupWindow = new PopupWindow(contentView,
                width, ViewGroup.LayoutParams.WRAP_CONTENT, true);

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

        int marginRight = (int)getResources().getDimension(R.dimen.popup_window_margin_right);
        int marginTarget = (int)getResources().getDimension(R.dimen.popup_window_margin_target);
        // 设置好参数之后再show
        popupWindow.showAsDropDown(mIvActionBarMore,
                mIvActionBarMore.getWidth() - width - marginRight,
                marginTarget);

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
    public void showNewFolderDialog() {
        CreateNewFolderDialog dialog = new CreateNewFolderDialog(this, new CreateNewFolderDialog.Listener() {
            @Override
            public void onConfirm(CreateNewFolderDialog dialog, String folderName) {
                if (mPresenter != null) {
                    boolean success = mPresenter.onClickConfirmCreateFolderButton(folderName);
                    if (success) {
                        dialog.dismiss();
                    } else {
                        AppUtils.showToast(MainActivity.this, R.string.toast_create_folder_failed);
                    }
                }
            }

            @Override
            public void onCancel(CreateNewFolderDialog dialog) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    public void showSortByDialog() {
        int currentSort;
        Comparator<File> sort = FileManager.getInstance().getFileSort();
        if (sort == FileUtil.sNameDescendComparator || sort == FileUtil.sNameAscendComparator) {
            currentSort = SortByDialog.SORT_BY_NAME;
        } else if (sort == FileUtil.sSizeDescendComparator || sort == FileUtil.sSizeAscendComparator) {
            currentSort = SortByDialog.SORT_BY_SIZE;
        } else if (sort == FileUtil.sTypeDescendComparator || sort == FileUtil.sTypeAscendComparator) {
            currentSort = SortByDialog.SORT_BY_TYPE;
        } else if (sort == FileUtil.sDateDescendComparator || sort == FileUtil.sDateAscendComparator) {
            currentSort = SortByDialog.SORT_BY_DATE;
        } else {
            currentSort = SortByDialog.SORT_BY_NAME;
        }


        SortByDialog dialog = new SortByDialog(this, currentSort, new SortByDialog.Listener() {
            @Override
            public void onDescend(SortByDialog dialog, int sortBy) {
                switch (sortBy) {
                    case SortByDialog.SORT_BY_NAME:
                        FileManager.getInstance().setFileSort(FileUtil.sNameDescendComparator);
                        break;
                    case SortByDialog.SORT_BY_DATE:
                        FileManager.getInstance().setFileSort(FileUtil.sDateDescendComparator);
                        break;
                    case SortByDialog.SORT_BY_TYPE:
                        FileManager.getInstance().setFileSort(FileUtil.sTypeDescendComparator);
                        break;
                    case SortByDialog.SORT_BY_SIZE:
                        FileManager.getInstance().setFileSort(FileUtil.sSizeDescendComparator);
                        break;
                    default:
                        break;
                }
                EventBus.getDefault().post(new SortByChangeEvent());
                dialog.dismiss();
            }

            @Override
            public void onAscend(SortByDialog dialog, int sortBy) {
                switch (sortBy) {
                    case SortByDialog.SORT_BY_NAME:
                        FileManager.getInstance().setFileSort(FileUtil.sNameAscendComparator);
                        break;
                    case SortByDialog.SORT_BY_DATE:
                        FileManager.getInstance().setFileSort(FileUtil.sDateAscendComparator);
                        break;
                    case SortByDialog.SORT_BY_TYPE:
                        FileManager.getInstance().setFileSort(FileUtil.sTypeAscendComparator);
                        break;
                    case SortByDialog.SORT_BY_SIZE:
                        FileManager.getInstance().setFileSort(FileUtil.sSizeAscendComparator);
                        break;
                    default:
                        break;
                }
                EventBus.getDefault().post(new SortByChangeEvent());
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    public void showStoragePage() {
        mVpPhoneStorage.setCurrentItem(1);
        mTlViewPageTab.getTabAt(1).select();
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
            case R.id.iv_main_action_bar_back:
                if (mPresenter != null) {
                    mPresenter.onClickActionBackButton();
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
            default:
                break;
        }
    }

    /**
     * 预览文件
     *
     * @param context 上下文
     * @param title   文件的类型
     * @param path    文件路径
     */
    public static void browserFile(Context context, String title, String path) {
        Intent intent = new Intent(context, MainActivity.class);
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        intent.putExtra(EXTRA_TITLE, title);
        intent.putExtra(EXTRA_FOCUS_FILE, path);
        intent.putExtra(MainPresenter.FILE_EXPLORER, true);
        try {
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 预览文件夹
     *
     * @param context 上下文
     * @param title   文件类型
     * @param dirs    文件夹的路径集合
     */
    public static void browserDirs(Context context, String title, String... dirs) {
        Intent intent = new Intent(context, MainActivity.class);
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        intent.putExtra(EXTRA_TITLE, title);
        intent.putExtra(EXTRA_DIRS, dirs);
        intent.putExtra(MainPresenter.FILE_EXPLORER, true);
        try {
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
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

}
