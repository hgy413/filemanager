package com.jb.filemanager.function.trash.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.jb.filemanager.util.DrawUtils;


/**
 * Created by xiaoyu on 2017/3/6 9:39.
 */

public class ScanProgress extends View {

    private static final int BIG_CIRCLE_RADIUS = DrawUtils.dip2px(5);
    private static final int SMALL_CIRCLE_RADIUS = DrawUtils.dip2px(3);
    private static final int STROKE_WIDTH = DrawUtils.dip2px(4);

    private float mRatio = 1;
    private Paint mPaint;
    private LinearGradient mLinearGradient;

    public ScanProgress(Context context) {
        super(context);
    }

    public ScanProgress(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ScanProgress(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mPaint == null) {
            mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        }
        // calculate the center point of the circle
        float lineLength = getWidth() * mRatio;
        float centerX;
        float centerY = getHeight() / 2;
        if (lineLength <= BIG_CIRCLE_RADIUS) {
            centerX = BIG_CIRCLE_RADIUS;
        } else if (lineLength >= getWidth() - BIG_CIRCLE_RADIUS) {
            centerX = getWidth() - BIG_CIRCLE_RADIUS;
            lineLength = getWidth() - BIG_CIRCLE_RADIUS;
        } else {
            centerX = lineLength;
        }
        // 1. draw biggest circle
        mPaint.setColor(Color.parseColor("#88ffffff"));
        canvas.drawCircle(centerX, centerY, BIG_CIRCLE_RADIUS, mPaint);
        // 2. draw rectangle
        mPaint.setColor(Color.WHITE);
        mLinearGradient = new LinearGradient(0, 0, lineLength, 0,
                Color.parseColor("#88ffffff"),
                Color.parseColor("#ffffffff"),
                Shader.TileMode.CLAMP);
        mPaint.setShader(mLinearGradient);
        mPaint.setStrokeWidth(STROKE_WIDTH);
        canvas.drawLine(0, getHeight() / 2, lineLength, getHeight() / 2, mPaint);
        mPaint.setShader(null);
        mPaint.setStrokeWidth(0);
        // 3. draw second circle
        mPaint.setColor(Color.parseColor("#ffffff"));
        canvas.drawCircle(centerX, centerY, SMALL_CIRCLE_RADIUS, mPaint);
    }

    public void setRatio(float ratio) {
        if (mRatio != ratio) {
            mRatio = ratio;
            postInvalidate();
        }
    }
}
