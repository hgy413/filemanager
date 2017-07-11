package com.jb.filemanager.function.trash;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jb.filemanager.BaseActivity;
import com.jb.filemanager.R;
import com.jb.filemanager.TheApplication;
import com.jb.filemanager.function.scanframe.clean.CleanManager;
import com.jb.filemanager.function.scanframe.clean.event.CleanCheckedFileSizeEvent;
import com.jb.filemanager.function.scanframe.clean.event.CleanNoneCheckedEvent;
import com.jb.filemanager.function.scanframe.clean.event.CleanProgressDoneEvent;
import com.jb.filemanager.function.scanframe.clean.event.CleanScanDoneEvent;
import com.jb.filemanager.function.scanframe.clean.event.CleanScanFileSizeEvent;
import com.jb.filemanager.function.scanframe.clean.event.CleanScanPathEvent;
import com.jb.filemanager.function.scanframe.clean.event.CleanStateEvent;
import com.jb.filemanager.function.trash.adapter.NewCleanListAdapter;
import com.jb.filemanager.function.trash.presenter.CleanTrashPresenter;
import com.jb.filemanager.function.trash.presenter.Contract;
import com.jb.filemanager.function.trashignore.activity.TrashIgnoreActivity;
import com.jb.filemanager.ui.widget.FloatingGroupExpandableListView;
import com.jb.filemanager.ui.widget.WrapperExpandableListAdapter;
import com.jb.filemanager.util.ConvertUtils;
import com.jb.filemanager.util.Logger;
import com.jb.filemanager.util.WindowUtil;
import com.jb.filemanager.util.imageloader.IconLoader;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by xiaoyu on 2016/11/9 13:46.<br>
 * 清理完成之后的动画进入在line<br>
 * {$date}
 */

public class CleanTrashActivity extends BaseActivity implements Contract.ICleanMainView, View.OnClickListener {

    private static final String TAG = CleanTrashActivity.class.getSimpleName();
    private CleanTrashPresenter mPresenter = new CleanTrashPresenter(this);
    private RelativeLayout mRlRoot;
    private View mVTitleShadow;
    private TextView mTvCommonActionBarTitle;
    private TextView mTvTrashSizeNumber;
    private TextView mTvTrashSizeUnit;
    private ProgressBar mPbScanProgress;
    private LinearLayout mLlContent;
    private TextView mTvTrashPath;
    private FloatingGroupExpandableListView mCleanTrashExpandableListView;
    private ImageView mIvCleanButton;
    private NewCleanListAdapter mAdapter;
    private ValueAnimator mAnimator;
    private String[] mSelectedStorageSize;
    private RelativeLayout mRlTitle;
    private ImageView mIvCommonActionBarMore;
    private RelativeLayout mRlTopContainer;
    private CleanManager mCleanManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clean_trash);
        IconLoader.ensureInitSingleton(TheApplication.getAppContext());
        IconLoader.getInstance().bindServicer(this);
        com.jb.filemanager.util.imageloader.ImageLoader.getInstance(TheApplication.getAppContext());
        mCleanManager = CleanManager.getInstance(this.getApplicationContext());
        initView();
        initData();
        initClick();
    }

    private void initView() {
        mRlRoot = (RelativeLayout) findViewById(R.id.rl_root);
        mVTitleShadow = (View) findViewById(R.id.v_title_shadow);
        mLlContent = (LinearLayout) findViewById(R.id.ll_content);
        mRlTitle = (RelativeLayout) findViewById(R.id.rl_title);
        mRlTopContainer = (RelativeLayout) findViewById(R.id.rl_top_container);
        mIvCommonActionBarMore = (ImageView) findViewById(R.id.iv_common_action_bar_more);
        mTvCommonActionBarTitle = (TextView) findViewById(R.id.tv_common_action_bar_title);
        mTvTrashSizeNumber = (TextView) findViewById(R.id.tv_trash_size_number);
        mTvTrashSizeUnit = (TextView) findViewById(R.id.tv_trash_size_unit);
        mPbScanProgress = (ProgressBar) findViewById(R.id.pb_scan_progress);
        /*mBtnIgnore = (Button) findViewById(R.id.btn_ignore);
        mBtnIgnore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CleanTrashActivity.this, TrashIgnoreActivity.class));
            }
        });*/
        mTvTrashPath = (TextView) findViewById(R.id.tv_trash_path);
        mCleanTrashExpandableListView = (FloatingGroupExpandableListView) findViewById(R.id.clean_trash_expandable_list_view);
        mIvCleanButton = (ImageView) findViewById(R.id.iv_clean_button_red);
        mIvCleanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doCleanTrash();
                mPbScanProgress.setVisibility(View.GONE);
                mRlRoot.setBackgroundResource(R.color.clean_trash_bg_blue);
                mIvCleanButton.setVisibility(View.GONE);
//                handleDisappearAnimation();
                /*KShareViewActivityManager.getInstance(CleanTrashActivity.this).
                        startActivity(CleanTrashActivity.this, CleanResultActivity.class,
                                R.layout.activity_clean_trash, R.layout.activity_clean_result, mIvCleanButton);*/
            }
        });
        mVTitleShadow.setVisibility(View.INVISIBLE);
        mAdapter = new NewCleanListAdapter(mPresenter.getDataGroup(), this);
        mCleanTrashExpandableListView.setAdapter(new WrapperExpandableListAdapter(mAdapter));

        mIvCommonActionBarMore.setVisibility(View.VISIBLE);
        mIvCommonActionBarMore.setImageResource(R.drawable.trash_ignore_icon);
        mIvCommonActionBarMore.setEnabled(false);
    }

    private void initClick() {
        mTvCommonActionBarTitle.setOnClickListener(this);
        mIvCommonActionBarMore.setOnClickListener(this);
        mIvCleanButton.setOnClickListener(this);
    }

    private void handleDisappearAnimation() {
        mAnimator = ValueAnimator.ofFloat(1, 0);
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float animatedValue = (float) valueAnimator.getAnimatedValue();
                mCleanTrashExpandableListView.setAlpha(animatedValue);
            }
        });

        mAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                Intent intent = new Intent(CleanTrashActivity.this, CleanResultActivity.class);
                intent.putExtra(CleanResultActivity.CLEAN_SIZE, mSelectedStorageSize);
                startActivity(intent);
                overridePendingTransition(R.anim.in, R.anim.out);
                finish();
            }
        });
        mAnimator.start();
    }

    private void initData() {
        if (!TheApplication.getGlobalEventBus().isRegistered(mSubscriber)) {
            TheApplication.getGlobalEventBus().register(mSubscriber);
        }
        mPresenter.enterCleanMainFragment();
    }

    private void setTotalCheckedSizeText() {
        long checkedSize = CleanCheckedFileSizeEvent.getJunkFileAllSize(true);
        mSelectedStorageSize = ConvertUtils.getFormatterTraffic(checkedSize);
        mTvTrashSizeNumber.setText(mSelectedStorageSize[0]);
        mTvTrashSizeUnit.setText(mSelectedStorageSize[1]);
    }

    private void keepScreenOn(boolean keep) {
        WindowUtil.keepScreenOn(getWindow(), keep);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        IconLoader.getInstance().unbindServicer(this);
        if (TheApplication.getGlobalEventBus().isRegistered(mSubscriber)) {
            TheApplication.getGlobalEventBus().unregister(mSubscriber);
        }
    }

    @Override
    public void onFileScanning() {
        setTotalCheckedSizeText();
    }

    @Override
    public void onFileScanFinish() {
        setTotalCheckedSizeText();
    }

    @Override
    public void notifyDataSetChanged() {
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void expandGroup(int index) {
        mCleanTrashExpandableListView.expandGroup(index);
    }

    @Override
    public void onDeleteStart() {
        mCleanTrashExpandableListView.setEnabled(false);
    }

    @Override
    public void updateProgress(float process) {
        mPbScanProgress.setProgress((int) (process * 100));
    }

    @Override
    public void onDeleteFinish() {
        mAdapter.notifyDataSetChanged();
    }

    private Object mSubscriber = new Object() {

        /**
         * 扫描到选中的文件的大小
         *
         * @param event e
         */
        @Subscribe(threadMode = ThreadMode.MAIN)
        public void onEventMainThread(CleanCheckedFileSizeEvent event) {
//            Logger.i(TAG, "CleanCheckedFileSizeEvent 扫描到选中的文件的大小");
            setTotalCheckedSizeText();
        }

        /**
         * 扫描到的文件的大小
         *
         * @param event e
         */
        @Subscribe(threadMode = ThreadMode.MAIN)
        public void onEventMainThread(CleanScanFileSizeEvent event) {
            //Logger.i(TAG, "CleanScanFileSizeEvent 扫描到的文件的大小");
            mAdapter.notifyDataSetChanged();
        }

        /**
         * 扫描完成事件
         *
         * @param event event
         */
        @Subscribe(threadMode = ThreadMode.MAIN)
        public void onEventMainThread(CleanScanDoneEvent event) {
            //Logger.i(TAG, "CleanScanDoneEvent 扫描完成事件" + event.name());
            mPresenter.updateProgressState();
            if (CleanScanDoneEvent.isAllDoneWithoutMemory()) {
                keepScreenOn(false);
            }
        }

        /**
         * 一旦扫显示了SD卡的扫描路径，就不再显示系统缓存扫描的路径
         */
        private boolean mIsSDCardPathShow = false;

        /**
         * 监听SD卡文件扫描路径事件
         */
        @Subscribe(threadMode = ThreadMode.MAIN)
        public void onEventMainThread(final CleanScanPathEvent event) {
            //Logger.i(TAG, "CleanScanPathEvent 监听SD卡文件扫描路径事件");
            if (event.equals(CleanScanPathEvent.SDCard)) {
                mIsSDCardPathShow = true;
            } else if (event.equals(CleanScanPathEvent.SysCache)
                    && mIsSDCardPathShow) {
                return;
            }
            TheApplication.postRunOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mTvTrashPath.setText(event.getPath());
                }
            });
        }

        /**
         * 监听扫描完成动画的结束事件
         */
        @Subscribe(threadMode = ThreadMode.MAIN)
        public void onEventMainThread(CleanProgressDoneEvent event) {
            if (CleanProgressDoneEvent.isAllDoneWithoutMemory()) {
                mPresenter.onScanFinish();
                boolean isAllEmpty = mPresenter.removeEmptyGroup();
                //mAdapter.notifyDataSetChanged();
                if (isAllEmpty) {
                    // 没有可以删除的文件，直接跳转到结果页
                    mIvCleanButton.setVisibility(View.GONE);
                    startActivity(new Intent(CleanTrashActivity.this, NoNeedCleanActivity.class));
                    finish();
                    Toast.makeText(CleanTrashActivity.this, "Had nothing to show and no where to go", Toast.LENGTH_SHORT).show();
                } else {
                    showScanResult();
                }
                CleanProgressDoneEvent.cleanAllDone();
            }
        }

        private void showScanResult() {
            Logger.d(TAG, "扫描完成   ");
//            Logger.i(TAG, "showScanResult");
            mTvTrashPath.setText("");
            mPresenter.updateDefaultCheckedState();
            mPresenter.setAllProgressFinish();
            // 展开指定的组
            mPresenter.expandAssignGroup();
            updateCleanBtnEnable();
            mAdapter.notifyDataSetChanged();
            setTotalCheckedSizeText();
            mIvCleanButton.setVisibility(View.VISIBLE);
            mIvCommonActionBarMore.setEnabled(true);
        }

        /**
         * 更新清理按钮的可用状态
         */
        private void updateCleanBtnEnable() {
            Logger.d(TAG, "监听清理按钮显示   " + mPresenter.isNoneGroupChecked());
            mIvCleanButton.setVisibility(mPresenter.isNoneGroupChecked() ? View.GONE : View.VISIBLE);
        }

        /**
         * 监听状态改变的事件
         */
        @Subscribe(threadMode = ThreadMode.MAIN)
        public void onEventMainThread(CleanStateEvent event) {
            Logger.d(TAG, "监听状态改变" + event.toString());

            if ((event.equals(CleanStateEvent.DELETE_FINISH) || event
                    .equals(CleanStateEvent.DELETE_SUSPEND))) {
                // 完成删除或者停止删除都跳转到结果页
                handleDisappearAnimation();
            }
        }

        /**
         * 监听文件选中状态改变
         */
        @Subscribe(threadMode = ThreadMode.MAIN)
        public void onEventMainThread(CleanNoneCheckedEvent event) {
//            Logger.e("SCROLL", "监听到文件选中状态的改变");
//            setTotalCheckedSizeText();
            Logger.d(TAG, "监听文件选中状态改变" + event.toString());
        }
    };

    private void doCleanTrash() {
        // TODO: 2016/12/23 清理垃圾
        mAdapter.notifyDataSetChanged();
        mCleanManager.startDelete();
        mPresenter.startDelete();
        mCleanManager.setLastTrashCleanTime(System.currentTimeMillis());
        mAdapter.notifyDataSetChanged();

       /* mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                handleDisappearAnimation();
            }
        }, 2000);*/

        Toast.makeText(CleanTrashActivity.this, "clean start", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View view) {
        if (mQuickClickGuard.isQuickClick(view.getId())) {
            return;
        }
        switch (view.getId()) {
            case R.id.tv_common_action_bar_title:
                finish();
                break;
            case R.id.iv_common_action_bar_more:
                startActivity(new Intent(this, TrashIgnoreActivity.class));
                break;
            case R.id.iv_clean_button_red:
                doCleanTrash();
                mPbScanProgress.setVisibility(View.GONE);
                mRlRoot.setBackgroundResource(R.color.clean_trash_bg_blue);
                mIvCleanButton.setVisibility(View.GONE);
                break;
            default:
                break;
        }
    }
}