package com.jb.filemanager.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.jb.filemanager.util.AppUtils;
import com.jb.filemanager.util.DrawUtils;

/**
 * Desc: 从矩形变成圆形的imageView
 * Author lqf
 * Email: liqf@m15.cn
 * Date: 2017/7/6 11:36
 */

public class TranslateImageView extends ImageView {

    private RectF mRectF;
    private int mRectFRadius = DrawUtils.dip2px(10);
    private Paint mPaint;

    public TranslateImageView(Context context) {
        super(context);
        initView();
    }

    public TranslateImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public TranslateImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.RED);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mRectF = new RectF(0, 0, getMeasuredWidth(), getMeasuredHeight());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRoundRect(mRectF, mRectFRadius, mRectFRadius, mPaint);
    }

    public void setPaintColor(int color) {
        mPaint.setColor(color);
    }

    public void setRectRadius(int rectRadius) {
        this.mRectFRadius = rectRadius;
        invalidate();
    }

    public int getRectFRadius() {
        return mRectFRadius;
    }

    public int getDefaultRectRadius() {
        return DrawUtils.dip2px(10);
    }
}
