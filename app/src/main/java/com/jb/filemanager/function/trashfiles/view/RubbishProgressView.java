package com.jb.filemanager.function.trashfiles.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Xfermode;
import android.util.AttributeSet;
import android.view.View;

import com.jb.filemanager.R;


/**
 * Created by xiaoyu on 2017/2/4 19:30.
 */

public class RubbishProgressView extends View {

    private static final int FOREGROUND_COLOR = Color.WHITE;
    private Paint mPaint;
    private Bitmap mBitmap;
    private Xfermode mXfermode;
    private float mRatio = 1.0f;
    private Rect mRect;

    public RubbishProgressView(Context context) {
        super(context);
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        initializeIngredient();
    }

    public RubbishProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        initializeIngredient();
    }

    public RubbishProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        initializeIngredient();
    }

    private void initializeIngredient() {
        mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.trash_progress_2);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(FOREGROUND_COLOR);
        mXfermode = new PorterDuffXfermode(PorterDuff.Mode.SRC_IN);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int id = canvas.saveLayer(0, 0, getWidth(), getHeight(), null, Canvas.ALL_SAVE_FLAG);
        // 1. draw background -- bitmap destination
        if (mRect == null) {
            mRect = new Rect(0, (int) (getHeight() * (1 - mRatio)), getWidth(), getHeight());
        } else {
            mRect.top = (int) (getHeight() * (1 - mRatio));
        }
        canvas.drawBitmap(mBitmap, mRect, mRect, mPaint);
        // 2. draw foreground -- white shader according to ratio
        mPaint.setXfermode(mXfermode);
        canvas.drawRect(0, getHeight() * (1 - mRatio), getWidth(), getHeight(), mPaint);

        mPaint.setXfermode(null);
        canvas.restoreToCount(id);
    }

    public float getRatio() {
        return mRatio;
    }

    public void setRatio(float ratio) {
        if (ratio != mRatio) {
            mRatio = ratio;
        }
        invalidate();
    }
}
