package com.jb.filemanager.function.trash;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jb.filemanager.BaseActivity;
import com.jb.filemanager.R;
import com.jb.filemanager.function.rate.RateManager;

public class CleanResultActivity extends BaseActivity implements View.OnClickListener {
    public static final String CLEAN_SIZE = "clean_size";
    public static final String TAG = CleanResultActivity.class.getSimpleName();
    private TextView mTvCommonActionBarTitle;
    private ImageView mIvCleanResultButtonBlue;
    private TextView mTvCleanResultSize;
    private TextView mTvCleanResultSizeUnit;
    private TextView mTvCleanResultCleaned;

    private ValueAnimator mResultContentShowAnimation;
    private boolean mIsFirstComeIn = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clean_result);
        //add by nieyh 评分引导收集触发因素
        RateManager.getsInstance().collectTriggeringFactor(RateManager.CLEAN_FINISH);
        initView();
        initData();
        initClick();
    }

    private void initView() {
        mTvCommonActionBarTitle = (TextView) findViewById(R.id.tv_common_action_bar_title);
        mIvCleanResultButtonBlue = (ImageView) findViewById(R.id.iv_clean_result_button_blue);
        mTvCleanResultSize = (TextView) findViewById(R.id.tv_clean_result_size);
        mTvCleanResultSizeUnit = (TextView) findViewById(R.id.tv_clean_result_size_unit);
        mTvCleanResultCleaned = (TextView) findViewById(R.id.tv_clean_result_cleaned);
        mTvCommonActionBarTitle.setVisibility(View.INVISIBLE);
    }

    private void initData() {
        Intent intent = getIntent();
        String[] cleanSizes = intent.getStringArrayExtra(CLEAN_SIZE);

        mTvCleanResultSize.setText(cleanSizes[0]);
        mTvCleanResultSizeUnit.setText(cleanSizes[1]);
        mTvCleanResultCleaned.setVisibility(View.GONE);
        mTvCleanResultSize.setVisibility(View.GONE);
        mTvCleanResultSizeUnit.setVisibility(View.GONE);
        mIvCleanResultButtonBlue.setVisibility(View.GONE);
    }

    private void initAnimation() {
        mResultContentShowAnimation = ValueAnimator.ofFloat(0, 1);
        mResultContentShowAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float animatedValue = (float) valueAnimator.getAnimatedValue();
                mTvCommonActionBarTitle.setAlpha(animatedValue);
                mTvCommonActionBarTitle.setScaleX(animatedValue);
                mTvCommonActionBarTitle.setScaleY(animatedValue);

                mTvCleanResultSize.setAlpha(animatedValue);
                mTvCleanResultSize.setScaleX(animatedValue);
                mTvCleanResultSize.setScaleY(animatedValue);

                mTvCleanResultSizeUnit.setAlpha(animatedValue);
                mTvCleanResultSizeUnit.setScaleX(animatedValue);
                mTvCleanResultSizeUnit.setScaleY(animatedValue);

                mTvCleanResultCleaned.setAlpha(animatedValue);
                mTvCleanResultCleaned.setScaleX(animatedValue);
                mTvCleanResultCleaned.setScaleY(animatedValue);

                mIvCleanResultButtonBlue.setAlpha(animatedValue);
                mIvCleanResultButtonBlue.setScaleX(animatedValue);
                mIvCleanResultButtonBlue.setScaleY(animatedValue);
            }
        });

        mResultContentShowAnimation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                mTvCommonActionBarTitle.setVisibility(View.VISIBLE);
                mTvCleanResultCleaned.setVisibility(View.VISIBLE);
                mTvCleanResultSize.setVisibility(View.VISIBLE);
                mTvCleanResultSizeUnit.setVisibility(View.VISIBLE);
                mIvCleanResultButtonBlue.setVisibility(View.VISIBLE);
            }
        });
        mResultContentShowAnimation.setDuration(750);
    }

    private void initClick() {
        mTvCommonActionBarTitle.setOnClickListener(this);
    }

    private void releaseAnimation() {

        if (mResultContentShowAnimation != null) {
            mResultContentShowAnimation.cancel();
            mResultContentShowAnimation.removeAllListeners();
            mResultContentShowAnimation.removeAllUpdateListeners();
            mResultContentShowAnimation = null;
        }
    }

    private void startAnimation() {
        if (mResultContentShowAnimation == null) {
            initAnimation();
        }
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
