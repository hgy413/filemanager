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
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.jb.filemanager.BaseActivity;
import com.jb.filemanager.Const;
import com.jb.filemanager.R;
import com.jb.filemanager.function.feedback.FeedbackActivity;
import com.jb.filemanager.function.privacy.PrivacyGuardActivity;
import com.jb.filemanager.function.rate.dialog.AbsRateDialog;
import com.jb.filemanager.function.rate.dialog.RateFeedbackDialog;
import com.jb.filemanager.function.rate.dialog.RateGuideDialog;
import com.jb.filemanager.function.rate.dialog.RateToGpDialog;
import com.jb.filemanager.function.rate.presenter.RateContract;
import com.jb.filemanager.function.rate.presenter.RatePresenter;
import com.jb.filemanager.function.rate.presenter.RateSupport;
import com.jb.filemanager.function.search.view.SearchActivity;
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

public class MainActivity extends PrivacyGuardActivity implements MainContract.View, View.OnClickListener, RateContract.View {

    public static final String ACTION_AGREE_PRIVACY = "action_agree_privacy";

    private MainContract.Presenter mPresenter;

    private MainDrawer mDrawer;

    private ImageView mIvActionBarMenu;
    private ImageView mTvActionBarTitle;
    private ImageView mIvActionBarSearch;
    private ImageView mIvActionBarMore;

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
        if (mAppUpdatePresenter != null) {
            mAppUpdatePresenter.onResume();
        }

        mRatePresenter.rateShow();
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

        mTvActionBarTitle = (ImageView) findViewById(R.id.tv_main_action_bar_title);

        mIvActionBarSearch = (ImageView) findViewById(R.id.iv_main_action_bar_search);
        if (mIvActionBarSearch != null) {
            mIvActionBarSearch.setOnClickListener(this);
        }

        mIvActionBarMore = (ImageView) findViewById(R.id.iv_main_action_bar_more);
        if (mIvActionBarMore != null) {
            mIvActionBarMore.setOnClickListener(this);
        }

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

        if (mIvActionBarMenu != null) {
            mIvActionBarMenu.setVisibility(View.VISIBLE);
        }

        if (mTvActionBarTitle != null) {
            mTvActionBarTitle.setVisibility(View.VISIBLE);
        }

        if (mIvActionBarMore != null) {
            mIvActionBarMore.setVisibility(moreButtonStatus);
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
        CreateNewFolderDialog dialog = new CreateNewFolderDialog(this, mPresenter.getCurrentPath(), new CreateNewFolderDialog.Listener() {
            @Override
            public void onResult(CreateNewFolderDialog dialog, boolean success) {
                if (success) {
                    dialog.dismiss();
                } else {
                    AppUtils.showToast(MainActivity.this, R.string.toast_create_folder_failed);
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

    @Override
    public void goToSearchActivity() {
        SearchActivity.showSearchResult(this, Const.CategoryType.CATEGORY_TYPE_ALL);
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


    /******
     * 评分引导
     * */
    private AbsRateDialog mRateCheerDialog;
    private AbsRateDialog mRateFeedBackDialog;
    private AbsRateDialog mRateLoveDialog;

    @Override
    public void showCheerDialog(AbsRateDialog.OnPressListener onPressListener) {
        if (mRateCheerDialog == null) {
            mRateCheerDialog = new RateGuideDialog(this);
            mRateCheerDialog.setOnPressListener(onPressListener);
        }
        mRateCheerDialog.show();
    }

    @Override
    public void showFeedBackDialog(AbsRateDialog.OnPressListener onPressListener) {
        if (mRateFeedBackDialog == null) {
            mRateFeedBackDialog = new RateFeedbackDialog(this);
            mRateFeedBackDialog.setOnPressListener(onPressListener);
        }
        mRateFeedBackDialog.show();
    }

    @Override
    public void showLoveDialog(AbsRateDialog.OnPressListener onPressListener) {
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
