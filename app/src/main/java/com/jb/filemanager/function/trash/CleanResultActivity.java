package com.jb.filemanager.function.trash;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jb.filemanager.BaseActivity;
import com.jb.filemanager.R;
import com.jb.filemanager.util.AppUtils;

public class CleanResultActivity extends BaseActivity implements View.OnClickListener {
    public static final String CLEAN_SIZE = "clean_size";
    public static final String TAG = CleanResultActivity.class.getSimpleName();
    private LinearLayout mLlTitle;
    private TextView mTvCommonActionBarTitle;
    private RelativeLayout mRlBlueRect;
    private TextView mTvTrashSizeNumber;
    private TextView mTvTrashSizeUnit;
    private ImageView mIvCleanResultButtonBlue;
    private TextView mTvCleanResultSize;
    private TextView mTvCleanResultSizeUnit;
    private TextView mTvCleanResultCleaned;
    private ValueAnimator mTopHideAnimation;
    private ValueAnimator mResultContentShowAnimation;
    private boolean mIsFirstComeIn = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clean_result);

        initView();
        initData();
        initClick();
        releaseAnimation();
    }

    private void initView() {
        mLlTitle = (LinearLayout) findViewById(R.id.ll_title);
        mTvCommonActionBarTitle = (TextView) findViewById(R.id.tv_common_action_bar_title);
        mRlBlueRect = (RelativeLayout) findViewById(R.id.rl_blue_rect);
        mTvTrashSizeNumber = (TextView) findViewById(R.id.tv_trash_size_number);
        mTvTrashSizeUnit = (TextView) findViewById(R.id.tv_trash_size_unit);
        mIvCleanResultButtonBlue = (ImageView) findViewById(R.id.iv_clean_result_button_blue);
        mTvCleanResultSize = (TextView) findViewById(R.id.tv_clean_result_size);
        mTvCleanResultSizeUnit = (TextView) findViewById(R.id.tv_clean_result_size_unit);
        mTvCleanResultCleaned = (TextView) findViewById(R.id.tv_clean_result_cleaned);

        mTvCommonActionBarTitle.setTextColor(AppUtils.getColor(R.color.black_70));
    }

    private void initData() {
        Intent intent = getIntent();
        String[] cleanSizes = intent.getStringArrayExtra(CLEAN_SIZE);
        mTvTrashSizeNumber.setText(cleanSizes[0]);
        mTvTrashSizeUnit.setText(cleanSizes[1]);
        mRlBlueRect.setVisibility(View.VISIBLE);

        mTvCleanResultSize.setText(cleanSizes[0]);
        mTvCleanResultSizeUnit.setText(cleanSizes[1]);
        mTvCleanResultCleaned.setVisibility(View.GONE);
        mTvCleanResultSize.setVisibility(View.GONE);
        mTvCleanResultSizeUnit.setVisibility(View.GONE);
        mIvCleanResultButtonBlue.setVisibility(View.GONE);
    }

    private void initAnimation() {
        mTopHideAnimation = ValueAnimator.ofFloat(1f, 0f);
        mTopHideAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float animatedValue = (float) valueAnimator.getAnimatedValue();
                mRlBlueRect.setScaleY(animatedValue);
                mRlBlueRect.setScaleX(animatedValue);
            }
        });
        mTopHideAnimation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationCancel(Animator animation) {
                super.onAnimationCancel(animation);
                mRlBlueRect.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mRlBlueRect.setVisibility(View.GONE);
            }
        });


        mTopHideAnimation.setDuration(1500);

        mResultContentShowAnimation = ValueAnimator.ofFloat(0, 1);
        mResultContentShowAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float animatedValue = (float) valueAnimator.getAnimatedValue();
                mTvCleanResultSize.setScaleX(animatedValue);
                mTvCleanResultSize.setScaleY(animatedValue);
                mTvCleanResultSizeUnit.setScaleX(animatedValue);
                mTvCleanResultSizeUnit.setScaleY(animatedValue);
                mTvCleanResultCleaned.setScaleX(animatedValue);
                mTvCleanResultCleaned.setScaleY(animatedValue);
                mIvCleanResultButtonBlue.setScaleX(animatedValue);
                mIvCleanResultButtonBlue.setScaleY(animatedValue);
            }
        });

        mResultContentShowAnimation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                mTvCleanResultCleaned.setVisibility(View.VISIBLE);
                mTvCleanResultSize.setVisibility(View.VISIBLE);
                mTvCleanResultSizeUnit.setVisibility(View.VISIBLE);
                mIvCleanResultButtonBlue.setVisibility(View.VISIBLE);
            }
        });
        mResultContentShowAnimation.setStartDelay(1200);
        mResultContentShowAnimation.setDuration(1500);
    }

    private void initClick() {
        mTvCommonActionBarTitle.setOnClickListener(this);
    }

    private void releaseAnimation() {
        if (mTopHideAnimation != null) {
            mTopHideAnimation.cancel();
            mTopHideAnimation.removeAllListeners();
            mTopHideAnimation.removeAllUpdateListeners();
            mTopHideAnimation = null;
        }

        if (mResultContentShowAnimation != null) {
            mResultContentShowAnimation.cancel();
            mResultContentShowAnimation.removeAllListeners();
            mResultContentShowAnimation.removeAllUpdateListeners();
            mResultContentShowAnimation = null;
        }
    }

    private void startAnimation() {
        if (mTopHideAnimation == null || mResultContentShowAnimation == null) {
            initAnimation();
        }

        mTopHideAnimation.start();
        mResultContentShowAnimation.start();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (mIsFirstComeIn) {
            mIsFirstComeIn = false;
            startAnimation();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseAnimation();
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
            default:
                break;
        }
    }
}
