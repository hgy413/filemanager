package com.jb.filemanager.ui.view;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.jb.filemanager.R;
import com.jb.filemanager.TheApplication;
import com.jb.filemanager.util.DrawUtils;
import com.jb.filemanager.util.Logger;


/**
 * Created by xiaoyu on 2017/4/13 16:25.
 */

public class TitleScrollView extends FrameLayout implements View.OnTouchListener {

    private Context mContext;
    private TextView mTvSize;
    private TextView mTvSizeFake;
    private TextView mTvUnit;
    private TextView mTvDes;
    private ProgressBar mScanProgress;
    private TextView mTvPath;
    private ScrollView mScrollView;
    private Animator mScrollAnimation;
    private RelativeLayout mRlContainer;
    private TextWatcher mTextWatcher;

    public TitleScrollView(@NonNull Context context) {
        super(context);
        mContext = context.getApplicationContext();
        initializeView();
    }

    public TitleScrollView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context.getApplicationContext();
        initializeView();
    }

    public TitleScrollView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context.getApplicationContext();
        initializeView();
    }

    private void initializeView() {
        View rootView = View.inflate(mContext, R.layout.view_scroll_title, null);
        addView(rootView);
        mScrollView = (ScrollView) rootView.findViewById(R.id.scroll_view_root);
        mScrollView.setOnTouchListener(this);
        mRlContainer = (RelativeLayout) rootView.findViewById(R.id.scroll_view_container);
        mTvSize = (TextView) rootView.findViewById(R.id.scroll_view_size);
        mTvSizeFake = (TextView) rootView.findViewById(R.id.scroll_view_size_fake);
        mTvUnit = (TextView) rootView.findViewById(R.id.scroll_view_unit);
        mTvDes = (TextView) rootView.findViewById(R.id.scroll_view_des);
        mScanProgress = (ProgressBar) rootView.findViewById(R.id.trash_aty_rubbish_pro);
        mTvPath = (TextView) rootView.findViewById(R.id.clean_trash_total_top_path);
        // 针对动画后排版会导致相应的排版出现叠加的可能进行修复
        // 缩放率 : 48 / 32 = 1.5
        // 缩放后Size的宽度 和 高度
        // 单位X方向的偏移量
        // 建议的X方向偏移量
        mTextWatcher = new TextWatcher() {
            private int mTempWidth;

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (currentType == TYPE_UP_SCALED) {
                    mTempWidth = mTvUnit.getWidth();
                }
            }

            @Override
            public void afterTextChanged(final Editable s) {
                TheApplication.postRunOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        int widthU = mTvUnit.getWidth();
                        if (currentType == TYPE_UP_SCALED && mTempWidth != widthU) {
                            float ratio = normalSize / scaleSize;// 缩放率 : 48 / 32 = 1.5
                            // 缩放后Size的宽度 和 高度
                            float width = mTvSizeFake.getWidth() / ratio;

                            // 单位X方向的偏移量
                            final float unitXTran = mTvUnit.getLeft() - width - DrawUtils.dip2px(10 + 16);

                            // 建议的X方向偏移量
                            final float desXTran = mTvDes.getLeft() - width - mTvUnit.getWidth() - DrawUtils.dip2px(20 + 16);

                            mTvUnit.setTranslationX(-unitXTran);
                            mTvDes.setTranslationX(-desXTran);
                        }

                    }
                });
            }
        };
        mTvUnit.addTextChangedListener(mTextWatcher);
    }

    private static final int START_SCROLL_ANIMATION_THRESHOLD_VALUE = DrawUtils.dip2px(12);
    private static final int TYPE_DOWN_NORMAL = 0;
    private static final int TYPE_UP_SCALED = 1;

    private int currentType = TYPE_DOWN_NORMAL;

    public void scrollAndUpdateText(int scrollY) {
        if (mScrollView != null) {
            mScrollView.smoothScrollTo(0, scrollY);
            if (scrollY > START_SCROLL_ANIMATION_THRESHOLD_VALUE) {
                Logger.e("SLIDE", "滑动到阈值至上了" + scrollY);
                // 滑动到阈值之上了
                if (currentType == TYPE_DOWN_NORMAL) {
                    currentType = TYPE_UP_SCALED;
                    // 开始缩小动画
                    mTvPath.setVisibility(INVISIBLE);
                    mScrollAnimation = createScrollAnimation(true);
                    mScrollAnimation.start();
                }
            } else {
                // 滑动到阈值之下
                if (currentType == TYPE_UP_SCALED) {
                    currentType = TYPE_DOWN_NORMAL;
                    // 开始放大还原动画
                    mTvPath.setVisibility(VISIBLE);
                    mScrollAnimation = createScrollAnimation(false);
                    mScrollAnimation.start();
                }
            }
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mTvUnit != null) {
            mTvUnit.removeTextChangedListener(mTextWatcher);
        }
    }

    /**
     * 显示的数字不需要改变时调用此方法, 提高性能
     */
    public void removeTextWatcher() {
        if (mTvUnit != null) {
            mTvUnit.removeTextChangedListener(mTextWatcher);
        }
    }

    /**
     * 1. 字体变化<br>
     * 2. 单位的位置变化<br>
     * 3. 建议的位置变化<br>
     * <p>
     * 以上
     *
     * @param isToUpScaled top
     * @return set
     */
    private float normalSize = 55.0f;
    private float scaleSize = 32.0f;

    private Animator createScrollAnimation(boolean isToUpScaled) {
        float ratio = normalSize / scaleSize;// 缩放率 : 48 / 32 = 1.5
        int originHeight = mTvSizeFake.getHeight();
        // 缩放后Size的宽度 和 高度
        float width = mTvSizeFake.getWidth() / ratio;
        float height = originHeight / ratio;

        // 建议的Y方向的偏移量
        float desYTran = 2 * (height - mTvDes.getHeight()) / 5 - (mTvSizeFake.getBottom() - mTvDes.getBottom());

        // 单位的Y方向偏移量
        float unitYTran = (originHeight - mTvUnit.getBottom()) - (originHeight - mTvDes.getBottom()) - desYTran;
        Logger.e("Title", "Unit.getBoottom=" + mTvUnit.getBottom() + " > Des.getBottom=" + mTvDes.getBottom()
                + " > desYtran=" + desYTran + " > height = " + height);

        // 单位X方向的偏移量
        float unitXTran = mTvUnit.getLeft() - width - DrawUtils.dip2px(10 + 16);

        // 建议的X方向偏移量
        float desXTran = mTvDes.getLeft() - width - mTvUnit.getWidth() - DrawUtils.dip2px(20 + 16);


        ObjectAnimator containerAnim;
        ObjectAnimator textSize;
        ObjectAnimator desYAnim;
        ObjectAnimator desXAnim;
        ObjectAnimator unitYAnim;
        ObjectAnimator unitXAnim;
        if (isToUpScaled) {// 缩放
            containerAnim = ObjectAnimator.ofFloat(mRlContainer, "translationY", 0, DrawUtils.dip2px(10));
            textSize = ObjectAnimator.ofFloat(mTvSize, "textSize", normalSize, scaleSize);
            unitYAnim = ObjectAnimator.ofFloat(mTvUnit, "translationY", 0, unitYTran);
            unitXAnim = ObjectAnimator.ofFloat(mTvUnit, "translationX", 0, -unitXTran);
            desYAnim = ObjectAnimator.ofFloat(mTvDes, "translationY", 0, -desYTran);
            desXAnim = ObjectAnimator.ofFloat(mTvDes, "translationX", 0, -desXTran);
        } else {
            containerAnim = ObjectAnimator.ofFloat(mRlContainer, "translationY", DrawUtils.dip2px(10), 0);
            textSize = ObjectAnimator.ofFloat(mTvSize, "textSize", scaleSize, normalSize);
            unitYAnim = ObjectAnimator.ofFloat(mTvUnit, "translationY", unitYTran, 0);
            unitXAnim = ObjectAnimator.ofFloat(mTvUnit, "translationX", -unitXTran, 0);
            desYAnim = ObjectAnimator.ofFloat(mTvDes, "translationY", -desYTran, 0);
            desXAnim = ObjectAnimator.ofFloat(mTvDes, "translationX", -desXTran, 0);

        }
        AnimatorSet set = new AnimatorSet();
        set.playTogether(containerAnim, textSize, unitXAnim, unitYAnim, desYAnim, desXAnim);
        return set;
    }

    public void setTotalSize(String size) {
        if (mTvSize != null) {
            mTvSize.setText(size);
        }
        if (mTvSizeFake != null) {
            mTvSizeFake.setText(size);
        }
    }

    public void setSizeUnit(String unit) {
        if (mTvUnit != null) {
            mTvUnit.setText(unit);
        }
    }

    public void setDescription(String description) {
        if (mTvDes != null) {
            mTvDes.setText(description);
        }
    }

    /*public void setScanProgress(float ratio) {
        if (mScanProgress != null) {
            mScanProgress.setRatio(ratio);
        }
    }*/

    public void goneScanProgress() {
        if (mScanProgress != null) {
            mScanProgress.setVisibility(GONE);
        }
    }

    public void hideScanProgress() {
        if (mScanProgress != null) {
            mScanProgress.setVisibility(INVISIBLE);
        }
    }

    public void showScanProgress() {
        if (mScanProgress != null) {
            mScanProgress.setVisibility(VISIBLE);
        }
    }

    public void setScanPath(String path) {
        if (mTvPath != null) {
            mTvPath.setText(path);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return true;
    }
}
