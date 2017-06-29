package com.jb.filemanager.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Xfermode;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.jb.filemanager.R;

/**
 * 提供圆角效果的FrameLayout<br>
 *
 * @author laojiale
 *
 */
public class RoundFrameLayout extends FrameLayout {

    private Paint mMaskPaint;
    private Xfermode mXfermode = new PorterDuffXfermode(PorterDuff.Mode.DST_IN);
    private Bitmap mMaskBitmap;
    private final Paint mLayerPaint = new Paint();
    private int mCornerRadius = 5;
    private int mCornerSide = 0;

    public RoundFrameLayout(Context context) {
        this(context, null);
    }

    public RoundFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.RoundFrameLayout);
        mCornerRadius = a.getDimensionPixelSize(
                R.styleable.RoundFrameLayout_corner_radius, 5);
        mCornerSide = a.getInteger(
                R.styleable.RoundFrameLayout_corner_side, 0);
        a.recycle();

        init();
    }

    private void init() {
        setWillNotDraw(false);
        mMaskPaint = new Paint();
        mMaskPaint.setAntiAlias(true);
        mMaskPaint.setFilterBitmap(true);
        mMaskPaint.setColor(0xFFFFFFFF);
    }

    private void updateMask() {
        if (mMaskBitmap != null && !mMaskBitmap.isRecycled()) {
            mMaskBitmap.recycle();
            mMaskBitmap = null;
        }
        try {
            mMaskBitmap = Bitmap.createBitmap(getWidth(), getHeight(),
                    Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError e) {

        }
        if (mMaskBitmap != null) {
//			final float density = getResources().getDisplayMetrics().density;
//			final float radius = density * mCornerRadius; // 5dp
            final float radius = mCornerRadius;
            final Canvas canvas = new Canvas(mMaskBitmap);
            int left = 0;
            int right = mMaskBitmap.getWidth();
            if (mCornerSide == 1) {
                right += radius;
            } else if (mCornerSide == 2) {
                left -= radius;
            }
            canvas.drawRoundRect(new RectF(left, 0, right,
                    mMaskBitmap.getHeight()), radius, radius, mMaskPaint);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        updateMask();
    }

    @Override
    public void draw(Canvas canvas) {
        if (mMaskBitmap != null) {
            final int sc = canvas.saveLayer(0, 0, getWidth(), getHeight(),
                    mLayerPaint, Canvas.ALL_SAVE_FLAG);
            super.draw(canvas);
            mMaskPaint.setXfermode(mXfermode);
            canvas.drawBitmap(mMaskBitmap, 0, 0, mMaskPaint);
            mMaskPaint.setXfermode(null);
            canvas.restoreToCount(sc);
        } else {
            super.draw(canvas);
        }
    }

}
