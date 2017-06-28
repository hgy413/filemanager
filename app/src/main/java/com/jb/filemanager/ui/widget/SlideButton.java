package com.jb.filemanager.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.jb.filemanager.util.DrawUtils;
import com.jb.filemanager.R;

/**
 * Created by xiaoyu on 2016/12/7 14:20.
 */

public class SlideButton extends View {

    private boolean mIsOn = true;
    private Paint mPaint;
    private Paint mBgPaint;
    private float mRadius;
    private float mBgWidth;
    private float mBgHeight;
    private float mRoundRadius;
    private float mPositionDelta;
    private float mCenterY;
    private float mCircleStartX;
    private float mCircleEndX;
    private RectF mRect;
    private boolean mDefaultChecked;

    public SlideButton(Context context) {
        super(context);
        initPaint();
    }

    public SlideButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.slideButton);
        mDefaultChecked = typedArray.getBoolean(R.styleable.slideButton_default_checked, true);
        typedArray.recycle();
        initPaint();
    }

    public SlideButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint();
    }

    public boolean isOpen() {
        return mIsOn;
    }

    public void setOpen(boolean on) {
        mIsOn = on;
        invalidate();
    }

    private void initPaint() {
        mIsOn = mDefaultChecked;

        mRadius = DrawUtils.dip2px(9);
        mBgWidth = DrawUtils.dip2px(40);
        mBgHeight = DrawUtils.dip2px(20);
        mRoundRadius = DrawUtils.dip2px(10f);
        mPositionDelta = DrawUtils.dip2px(0f);
        mCenterY = DrawUtils.dip2px(10);
        mCircleStartX = DrawUtils.dip2px(10f);
        mCircleEndX = DrawUtils.dip2px(30f);

        mRect = new RectF(mPositionDelta, mPositionDelta,
                mPositionDelta + mBgWidth, mPositionDelta + mBgHeight);

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL);
        mBgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBgPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // two status of the slide button : on or off
        // first step : draw the background
        // first step : draw the button
        if (isOpen()) {
            mBgPaint.setColor(Color.parseColor("#229eff"));
            mPaint.setColor(Color.parseColor("#ffffff"));
            canvas.drawRoundRect(mRect, mRoundRadius, mRoundRadius, mBgPaint);
            canvas.drawCircle(mCircleEndX, mCenterY, mRadius, mPaint);
        } else {
            mBgPaint.setColor(Color.parseColor("#42221F1F"));
            mPaint.setColor(Color.parseColor("#ffffff"));
            canvas.drawRoundRect(mRect, mRoundRadius, mRoundRadius, mBgPaint);
            canvas.drawCircle(mCircleStartX, mCenterY, mRadius, mPaint);
        }
    }
}
