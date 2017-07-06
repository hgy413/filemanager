package com.jb.filemanager.function.trash;

import android.animation.ValueAnimator;
import android.os.Bundle;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import com.jb.filemanager.BaseActivity;
import com.jb.filemanager.R;
import com.jb.filemanager.ui.view.TranslateImageView;

public class CleanResultActivity extends BaseActivity {
    private ImageView mIvCleanButtonTranslate;
    private TranslateImageView mIvCleanButton;
    private boolean mFirstInflate = true;
    private ValueAnimator mTranslateAnimator;
    private float mWidthScale;
    private float mHeightScale;
    private int mTranslateX;
    private int mTranslateY;
    private float mRadiusScale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clean_result);

        mIvCleanButton = (TranslateImageView) findViewById(R.id.iv_clean_button);
        mIvCleanButtonTranslate = (ImageView) findViewById(R.id.iv_clean_button_translate);

        initAnimation();
    }

    private void initAnimation() {
        mTranslateAnimator = ValueAnimator.ofFloat(0, 1);
        mTranslateAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float animatedValue = (float) valueAnimator.getAnimatedValue();
                mIvCleanButton.setScaleX(animatedValue * (mWidthScale - 1) + 1);
                mIvCleanButton.setScaleY(animatedValue * (mHeightScale - 1) + 1);
                mIvCleanButton.setTranslationX(animatedValue * mTranslateX);
                mIvCleanButton.setTranslationY(animatedValue * mTranslateY);
                mIvCleanButton.setRectRadius((int) (1.3f * mIvCleanButton.getDefaultRectRadius() * (animatedValue * (mRadiusScale - 1) + 1)));
            }
        });
        mTranslateAnimator.setInterpolator(new LinearInterpolator());//后期图案较大   加个减速可以更平滑
        mTranslateAnimator.setDuration(1500);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (mFirstInflate) {
            int targetWidth = mIvCleanButtonTranslate.getWidth();
            int targetHeight = mIvCleanButtonTranslate.getHeight();
            int fromWidth = mIvCleanButton.getWidth();
            int fromHeight = mIvCleanButton.getHeight();
            int[] fromLocation = new int[2];
            int[] targetLocation = new int[2];
            mIvCleanButton.getLocationInWindow(fromLocation);
            mIvCleanButtonTranslate.getLocationInWindow(targetLocation);

            //计算中心的差值
            int fromX = fromLocation[0];
            int fromY = fromLocation[1];

            int toX = targetLocation[0] + (targetWidth - fromWidth) / 2;
            int toY = targetLocation[1] + (targetHeight - fromHeight) / 2;


            mTranslateX = toX - fromX;
            mTranslateY = toY - fromY;
            //计算长宽缩放比例
            mWidthScale = targetWidth * 1f / fromWidth;
            mHeightScale = targetHeight * 1f / fromHeight;
            //计算圆角的缩放比例
            mRadiusScale = fromWidth / 2 * 1f / mIvCleanButton.getDefaultRectRadius();//可以适度放大

            mTranslateAnimator.start();
            mFirstInflate = false;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        KShareViewActivityManager.getInstance(CleanResultActivity.this).finish(CleanResultActivity.this);
    }
}
