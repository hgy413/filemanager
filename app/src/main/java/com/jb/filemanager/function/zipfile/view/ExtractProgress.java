package com.jb.filemanager.function.zipfile.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by xiaoyu on 2017/7/11 15:56.
 */

public class ExtractProgress extends View {

    private float mPercent = 0;
    private Paint mPaint;

    public ExtractProgress(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mPaint == null) {
            mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mPaint.setStyle(Paint.Style.FILL);
        }
        mPaint.setColor(0xffe7e9f3);
        canvas.drawRect(0, 0, getWidth(), getHeight(), mPaint);
        mPaint.setColor(0xff39bdac);
        canvas.drawRect(0, 0, getWidth() * mPercent, getHeight(), mPaint);
    }

    public float getPercent() {
        return mPercent;
    }

    public void setPercent(float percent) {
        if (percent != mPercent) {
            mPercent = percent;
            invalidate();
        }
    }
}
