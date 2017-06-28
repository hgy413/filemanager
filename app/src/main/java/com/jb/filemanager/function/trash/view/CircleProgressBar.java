package com.jb.filemanager.function.trash.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.jb.filemanager.util.DrawUtils;


/**
 * Created by xiaoyu on 2016/11/25 15:10.<br>
 * 清理动画group滚动使用, 有进度0 -100<br>
 */

public class CircleProgressBar extends View {

    private Paint mBgPaint;
    private Paint mPaint;

    private static final int STROKE_WIDTH = 2;

    private static final int RADIUS = 11;

    private static final int RECT_WIDTH = RADIUS * 2 - 1;

    private RectF mRectF;
    private float mRatio;

    public float getRatio() {
        return mRatio;
    }

    public void setRatio(float ratio) {
        mRatio = ratio;
        if (mRatio < 0) {
            mRatio = 0;
        }
        if (mRatio > 1 || mRatio > 0.95) {
            mRatio = 1;
        }
        invalidate();
    }

    public CircleProgressBar(Context context) {
        super(context);
        initView();
    }

    public CircleProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public CircleProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    public void setProgress(float ratio) {
        this.mRatio = ratio;
        if (mRatio < 0) {
            mRatio = 0;
        }
        if (mRatio > 1 || mRatio > 0.95) {
            mRatio = 1;
        }
        invalidate();
    }

    private void initView() {
        mRectF = new RectF(DrawUtils.dip2px(1), DrawUtils.dip2px(1), DrawUtils.dip2px(RECT_WIDTH), DrawUtils.dip2px(RECT_WIDTH));
        // 背景画笔
        mBgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBgPaint.setColor(Color.parseColor("#EDEDEC"));
        mBgPaint.setStyle(Paint.Style.STROKE);
        mBgPaint.setStrokeWidth(DrawUtils.dip2px(STROKE_WIDTH));

        // 进度条画笔
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.parseColor("#47A6E7"));
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(DrawUtils.dip2px(STROKE_WIDTH));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawArc(mRectF, 0, 360, false, mBgPaint);
        canvas.drawArc(mRectF, -90, 360 * mRatio, false, mPaint);
    }
}
