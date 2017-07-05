package com.jb.filemanager.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by xiaoyu on 2017/3/6 14:00.
 * <p>
 * isSelect = true : top shader<br>
 * isSelect = false : bottom shader
 * </p>
 */

public class ShaderLine extends View {

    private Paint mPaint;
    /**
     * show shader-line or show only line
     */
    private boolean mIsShow;
    private LinearGradient mLinearGradient;

    public ShaderLine(Context context) {
        super(context);
        setLayerType(LAYER_TYPE_SOFTWARE, null);
    }

    public ShaderLine(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setLayerType(LAYER_TYPE_SOFTWARE, null);
    }

    public ShaderLine(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setLayerType(LAYER_TYPE_SOFTWARE, null);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL);
        if (isClickable()) { // top shader
            if (mIsShow) {
                mLinearGradient = new LinearGradient(0, 0, 0, getHeight(),
                        Color.parseColor("#fff1f1f1"), Color.parseColor("#88f1f1f1"), Shader.TileMode.CLAMP);
                mPaint.setShader(mLinearGradient);
                canvas.drawRect(0, 0, getWidth(), getHeight(), mPaint);
            } else {
                canvas.drawARGB(255, 255, 255, 255);
                // draw line
                mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
                mPaint.setStrokeWidth(1);
                mPaint.setColor(Color.parseColor("#cccccc"));
                canvas.drawLine(0, 0, getWidth(), 0, mPaint);
            }
        } else { // bottom shader
            if (mIsShow) {
                mLinearGradient = new LinearGradient(0, 0, 0, getHeight(),
                        Color.parseColor("#88f1f1f1"), Color.parseColor("#fff1f1f1"), Shader.TileMode.CLAMP);
                mPaint.setShader(mLinearGradient);
                canvas.drawRect(0, 0, getWidth(), getHeight(), mPaint);
            } else {
                canvas.drawRGB(255, 255, 255);
                // draw line
                mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
                mPaint.setStrokeWidth(1);
                mPaint.setColor(Color.parseColor("#cccccc"));
                canvas.drawLine(0, getHeight(), getWidth(), getHeight(), mPaint);
            }
        }
    }

    public void setShaderVisibility(boolean isShow) {
        if (mIsShow != isShow) {
            mIsShow = isShow;
            postInvalidate();
        }
    }
}
