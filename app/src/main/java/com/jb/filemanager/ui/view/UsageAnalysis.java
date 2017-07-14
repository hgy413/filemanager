package com.jb.filemanager.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by bill wang on 2017/7/10.
 *
 */

public class UsageAnalysis extends View {

    private ArrayList<Bean> mItems = new ArrayList<>();
    private ArrayList<Bean> mItemsForDraw = new ArrayList<>();
    private int mOtherColor;
    private long mUsedSize;
    private long mTotalSize;

    private Paint mPaint;

    public UsageAnalysis(Context context) {
        super(context);
    }

    public UsageAnalysis(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public UsageAnalysis(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void addItem(int color, long size) {
        if (size > 0) {
            mItemsForDraw.clear();

            Bean bean = new Bean();
            bean.mColor = color;
            bean.mSize = size;

            mItems.add(bean);
            mItemsForDraw.addAll(mItems);

            long otherSize = mUsedSize;
            for (Bean bean1 : mItems) {
                otherSize -= bean1.mSize;
            }

            if (otherSize > 0) {
                Bean other = new Bean();
                other.mSize = otherSize;
                other.mColor = mOtherColor;
                mItemsForDraw.add(other);
            }

            Collections.sort(mItemsForDraw, new Comparator<Bean>() {
                @Override
                public int compare(Bean o1, Bean o2) {
                    return Long.valueOf(o2.mSize).compareTo(o1.mSize);
                }
            });

            calcRect();

            invalidate();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mPaint == null) {
            mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        }

        for (Bean bean : mItemsForDraw) {
            if (bean.mRect != null) {
                mPaint.setColor(bean.mColor);
                canvas.drawRect(bean.mRect, mPaint);
            }
        }
    }

    public void reload() {
        mItems.clear();
    }

    public void setTotal(long total) {
        mTotalSize = total;
    }

    public void setUsed(int color, long used) {
        mOtherColor = color;
        mUsedSize = used;
    }

    private void calcRect() {
        int left = 0;
        if (mTotalSize > 0) {
            for (Bean bean : mItemsForDraw) {
                long size = bean.mSize;
                float percent = (float)size / mTotalSize;
                int width = (int) (percent * getWidth());
                bean.mRect = new Rect(left, 0, left + width, getHeight());
                left += width;
            }
        }
    }

    private static class Bean {
        int mColor;
        long mSize;
        Rect mRect;
    }
}
