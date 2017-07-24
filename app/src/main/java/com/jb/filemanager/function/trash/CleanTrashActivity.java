package com.jb.filemanager.function.trash;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
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
import com.jb.filemanager.function.trash.adapter.CleanListAdapter;
import com.jb.filemanager.function.trash.adapter.TrashGroupAdapter;
import com.jb.filemanager.function.trash.adapter.view.SlideInRightAnimator;
import com.jb.filemanager.function.trash.adapter.view.WrapContentLinearLayoutManager;
import com.jb.filemanager.function.trash.presenter.CleanTrashPresenter;
import com.jb.filemanager.function.trash.presenter.Contract;
import com.jb.filemanager.function.trashignore.activity.TrashIgnoreActivity;
import com.jb.filemanager.ui.widget.FloatingGroupExpandableListView;
import com.jb.filemanager.ui.widget.WrapperExpandableListAdapter;
import com.jb.filemanager.util.ConvertUtils;
import com.jb.filemanager.util.Logger;
import com.jb.filemanager.util.ScreenUtils;
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
    private TextView mTvCommonActionBarTitle;
    private TextView mTvTrashSizeNumber;
    private TextView mTvTrashSizeUnit;
    private ProgressBar mPbScanProgress;
    private TextView mTvTrashPath;
    private FloatingGroupExpandableListView mCleanTrashExpandableListView;
    private RecyclerView mRvTrashGroupList;
    private ImageView mIvCleanButton;
    private ImageView mIvTopRed;
    private ImageView mIvTopGradient;
    private ImageView mIvTopGreen;

    private CleanListAdapter mAdapter;
    private String[] mSelectedStorageSize;
    private ImageView mIvCommonActionBarMore;
    private CleanManager mCleanManager;
    private boolean mIsDeleting;

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
        View VTitleShadow = (View) findViewById(R.id.v_title_shadow);
        mIvCommonActionBarMore = (ImageView) findViewById(R.id.iv_common_action_bar_more);
        mTvCommonActionBarTitle = (TextView) findViewById(R.id.tv_common_action_bar_title);
        mTvTrashSizeNumber = (TextView) findViewById(R.id.tv_trash_size_number);
        mTvTrashSizeUnit = (TextView) findViewById(R.id.tv_trash_size_unit);
        mPbScanProgress = (ProgressBar) findViewById(R.id.pb_scan_progress);
        mTvTrashPath = (TextView) findViewById(R.id.tv_trash_path);
        mCleanTrashExpandableListView = (FloatingGroupExpandableListView) findViewById(R.id.clean_trash_expandable_list_view);
        mRvTrashGroupList = (RecyclerView) findViewById(R.id.rv_trash_group_list);
        mIvCleanButton = (ImageView) findViewById(R.id.iv_clean_button_red);
        mIvCleanButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        view.setScaleX(0.9f);
                        view.setScaleY(0.9f);
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        view.setScaleX(1);
                        view.setScaleY(1);
                        break;
                    default:
                        break;
                }
                return false;
            }
        });
        VTitleShadow.setVisibility(View.INVISIBLE);
        mAdapter = new CleanListAdapter(mPresenter.getDataGroup(), this);
        mCleanTrashExpandableListView.setAdapter(new WrapperExpandableListAdapter(mAdapter));

        mIvCommonActionBarMore.setVisibility(View.VISIBLE);
        mIvCommonActionBarMore.setImageResource(R.drawable.trash_ignore_icon);
        mIvCommonActionBarMore.setEnabled(false);

        mIvTopRed = (ImageView) findViewById(R.id.iv_top_red);
        mIvTopGradient = (ImageView) findViewById(R.id.iv_top_gradient);
        mIvTopGreen = (ImageView) findViewById(R.id.iv_top_green);

    }

    private void initClick() {
        mTvCommonActionBarTitle.setOnClickListener(this);
        mIvCommonActionBarMore.setOnClickListener(this);
        mIvCleanButton.setOnClickListener(this);
    }

    private void gotoResultPage() {
        Intent intent = new Intent(CleanTrashActivity.this, CleanResultActivity.class);
        intent.putExtra(CleanResultActivity.CLEAN_SIZE, mSelectedStorageSize);
        startActivity(intent);
        overridePendingTransition(R.anim.in, R.anim.out);
        finish();
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
    public void onBackPressed() {
        if (!mIsDeleting) {//删除的时候禁止退出页面
            super.onBackPressed();
        }
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
    public void finish() {
        if (!mIsDeleting) {//删除过程中禁止退出
            super.finish();
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
    public void collapsedGroup(int index) {
        mCleanTrashExpandableListView.collapseGroup(index);
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

    @Override
    public void startDeleteAnimation() {
        SlideInRightAnimator animator = new SlideInRightAnimator();
        animator.setRemoveDuration(300);
        animator.setAddDuration(300);
        mRvTrashGroupList.setLayoutManager(new WrapContentLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mRvTrashGroupList.setItemAnimator(animator);
        TrashGroupAdapter adapter = new TrashGroupAdapter(mPresenter.getDataGroup());
        mRvTrashGroupList.setAdapter(adapter);
        mRvTrashGroupList.setVisibility(View.VISIBLE);
        mCleanTrashExpandableListView.setVisibility(View.GONE);
        adapter.setOnItemRemoveListener(new TrashGroupAdapter.OnItemRemoveListener() {
            @Override
            public void onLastItemRemoved() {
                mIsDeleting = false;
                /*gotoResultPage();*/
            }
        });
        initTopGradientAnimation();

        adapter.removeAllItem();
    }

    private void initTopGradientAnimation() {
        final int screenWidth = (int) (ScreenUtils.getScreenWidth());
        ValueAnimator valueAnimator = ValueAnimator.ofInt(-2 * screenWidth, 0);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int animatedValue = (int) valueAnimator.getAnimatedValue();
                mIvTopGradient.setTranslationX(animatedValue + screenWidth);
                mIvTopGreen.setTranslationX(animatedValue);
            }
        });

        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                mIvTopGradient.setVisibility(View.VISIBLE);
                mIvTopGreen.setVisibility(View.VISIBLE);
                Logger.d(TAG, "start translationX");
            }
        });
        valueAnimator.setDuration(10000);
        valueAnimator.start();
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
            mPbScanProgress.setProgress(0);
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
//                gotoResultPage();
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
            if (event.equals(CleanNoneCheckedEvent.NONE)) {
                //一个都没有选中
                mIvCleanButton.setVisibility(View.GONE);
            } else {
                mIvCleanButton.setVisibility(View.VISIBLE);
            }
        }
    };

    private void doCleanTrash() {
        // TODO: 2016/12/23 清理垃圾
        mAdapter.notifyDataSetChanged();
        mPresenter.collapsedAllGroup();

        mCleanTrashExpandableListView.setClickable(false);
        mIsDeleting = true;
        mCleanManager.startDelete();
        mPresenter.startDelete();
        mCleanManager.setLastTrashCleanTime(System.currentTimeMillis());

//        Toast.makeText(CleanTrashActivity.this, "clean start", Toast.LENGTH_SHORT).show();
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