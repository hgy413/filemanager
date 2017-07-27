package com.jb.filemanager.ui.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;

import com.jb.filemanager.R;

/**
 * Created by nieyh on 17-7-27.
 * 通用加载进度条
 */

public class CommonLoadingView extends LinearLayout {

    private ValueAnimator mAnim;
    private float mLastValue;
    // anim
    private View mViewAnim1;
    private View mViewAnim2;
    private View mViewAnim3;
    private static final int ANIM_SLOW_PLAY_RATE = 1;

    public CommonLoadingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        View.inflate(context, R.layout.view_loading, this);
        mViewAnim1 = findViewById(R.id.view_fragment_search_anim_1);
        mViewAnim2 = findViewById(R.id.view_fragment_search_anim_2);
        mViewAnim3 = findViewById(R.id.view_fragment_search_anim_3);
    }

    /**
     * 开始加载
     * */
    public void startLoading() {
        if (mAnim == null) {
            final float duration1 = 0.135f;
            final float duration2 = 0.1f;
            mAnim = ValueAnimator.ofFloat(0.0f, 0.505f);
            mAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float value = (float) animation.getAnimatedValue();
                    if (value - mLastValue > 0.001f) {
                        if (value >= 0.0f && value <= 0.235f) {
                            if (value >= 0.0f && value < 0.135f) {
                                float progress = (value / duration1) * (2.4f - 1) + 1;
                                mViewAnim1.setScaleX(progress);
                                mViewAnim1.setScaleY(progress);
                            } else {
                                float progress = 2.4f - ((value - duration1) / duration2) * (2.4f - 1);
                                mViewAnim1.setScaleX(progress);
                                mViewAnim1.setScaleY(progress);
                            }
                        }

                        if (value >= 0.135f && value <= 0.370f) {
                            float value2 = value - 0.135f;
                            if (value2 >= 0.0f && value2 < 0.135f) {
                                float progress = (value2 / duration1) * (2.4f - 1) + 1;
                                mViewAnim2.setScaleX(progress);
                                mViewAnim2.setScaleY(progress);
                            } else {
                                float progress = 2.4f - ((value2 - duration1) / duration2) * (2.4f - 1);
                                mViewAnim2.setScaleX(progress);
                                mViewAnim2.setScaleY(progress);
                            }
                        }

                        if (value >= 0.270f && value <= 0.505f) {
                            float value3 = value - 0.270f;
                            if (value3 >= 0.0f && value3 < 0.135f) {
                                float progress = (value3 / duration1) * (2.4f - 1) + 1;
                                mViewAnim3.setScaleX(progress);
                                mViewAnim3.setScaleY(progress);
                            } else {
                                float progress = 2.4f - ((value3 - duration1) / duration2) * (2.4f - 1);
                                mViewAnim3.setScaleX(progress);
                                mViewAnim3.setScaleY(progress);
                            }
                        }

                        mLastValue = value;
                    }
                }
            });

            mAnim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    setVisibility(VISIBLE);
                    if (mOnLoadingAnimStateListener != null) {
                        mOnLoadingAnimStateListener.onStart();
                    }
                    mViewAnim1.setScaleX(1.0f);
                    mViewAnim1.setScaleY(1.0f);
                    mViewAnim2.setScaleX(1.0f);
                    mViewAnim2.setScaleY(1.0f);
                    mViewAnim3.setScaleX(1.0f);
                    mViewAnim3.setScaleY(1.0f);
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                    mLastValue = 0.0f;

                    mViewAnim1.setScaleX(1.0f);
                    mViewAnim1.setScaleY(1.0f);
                    mViewAnim2.setScaleX(1.0f);
                    mViewAnim2.setScaleY(1.0f);
                    mViewAnim3.setScaleX(1.0f);
                    mViewAnim3.setScaleY(1.0f);

                    if (mOnLoadingAnimStateListener != null) {
                        mOnLoadingAnimStateListener.onRepeat();
                    }
                }


            });
            mAnim.setInterpolator(new LinearInterpolator());
            mAnim.setDuration(505 * ANIM_SLOW_PLAY_RATE);
            mAnim.setRepeatMode(ValueAnimator.RESTART);
            mAnim.setRepeatCount(ValueAnimator.INFINITE);
        }
        mAnim.start();
    }

    /**
     * 完成加载
     * */
    public void stopLoading() {
        if (mAnim != null) {
            mAnim.cancel();
            mAnim.removeAllUpdateListeners();
            mAnim.removeAllListeners();
            mAnim = null;
            mLastValue = 0.0f;
        }
        setVisibility(GONE);
    }

    /**
     * 监听器
     * */

    private OnLoadingAnimStateListener mOnLoadingAnimStateListener;

    public void setOnLoadingAnimStateListener(OnLoadingAnimStateListener onLoadingAnimStateListener) {
        mOnLoadingAnimStateListener = onLoadingAnimStateListener;
    }

    private interface OnLoadingAnimStateListener {
        void onStart();
        void onRepeat();
    }
}
